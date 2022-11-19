package dev.axyria.scalacord.gateway.job

import cats.effect.kernel.Async
import cats.effect.kernel.Clock
import cats.effect.kernel.Ref
import cats.syntax.all.*
import dev.axyria.scalacord.gateway.DiscordGateway
import dev.axyria.scalacord.gateway.payload.GatewayPayload
import dev.axyria.scalacord.gateway.payload.command.Heartbeat
import dev.axyria.scalacord.gateway.payload.command.HeartbeatCodec
import dev.axyria.scalacord.gateway.payload.event.Event
import dev.axyria.scalacord.gateway.payload.event.HeartbeatAck
import dev.axyria.scalacord.gateway.payload.event.Hello
import fs2.Stream
import scala.concurrent.duration.FiniteDuration
import scala.concurrent.duration.MILLISECONDS
import spire.math.ULong

/** A job for maintaining the connection active within the Heartbeating protocol.
  *
  * @tparam F
  *   A type-class able to suspend side effects into the [[F]] context.
  */
case class HeartbeatingJob[F[_]: Async: Clock](
    client: DiscordGateway[F],
    interval: Ref[F, Long],
    last: Ref[F, FiniteDuration],
) extends GatewayJob[F] {
    override def entrypoint(payload: GatewayPayload, event: Stream[F, Event]): Stream[F, Unit] =
        event
            .map(event => event.data)
            .flatMap {
                case hello: Hello =>
                    Stream
                        .eval(interval.set(hello.heartbeatInterval))
                        .flatTap(_ => send()) merge start(hello.heartbeatInterval)
                case HeartbeatAck =>
                    Stream
                        .eval((Clock[F].realTime, last.get).tupled)
                        .map { case (now, last) => now - last }
                        .evalMap(duration => client.ping0.set(Some(duration)))
                case _ =>
                    Stream.empty
            }

    private def start(interval: Long): Stream[F, Unit] =
        Stream
            .awakeEvery[F](FiniteDuration(interval, MILLISECONDS))
            .flatMap(_ => send())
            .drain
            .void

    private def send(): Stream[F, Unit] =
        Stream
            .eval(client.sequence.get)
            .map(_.map(_.toLong))
            .map(Heartbeat(_))
            .evalMap(packet => client.send(HeartbeatCodec.toPayload(packet)))
            .evalTap(_ => Clock[F].realTime.flatMap(last.set))
}

object HeartbeatingJob {
    def apply[F[_]: Async](client: DiscordGateway[F]): F[HeartbeatingJob[F]] =
        for {
            interval <- Ref[F].of(0L)
            last     <- Ref[F].of(FiniteDuration(0, MILLISECONDS))
        } yield HeartbeatingJob(client, interval, last)
}
