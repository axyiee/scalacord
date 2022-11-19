package dev.axyria.scalacord.gateway.job

import cats.effect.kernel.Async
import cats.effect.kernel.Concurrent
import cats.effect.syntax.all.*
import cats.kernel.Monoid
import cats.syntax.all.*
import dev.axyria.scalacord.gateway.DiscordGateway
import dev.axyria.scalacord.gateway.payload.GatewayPayload
import dev.axyria.scalacord.gateway.payload.event.Event
import fs2.Stream

/** A simple internal handler for whenever [[DiscordWebSocket]] receive new payloads.
  * @tparam F
  *   A type-class able to suspend side effects into the [[F]] context.
  */
trait GatewayJob[F[_]] {

    /** The entrypoint for this job. This runs whenever a new payload that meets the required
      * condition provided by [[should]] is received.
      */
    def entrypoint(payload: GatewayPayload, event: Stream[F, Event]): Stream[F, Unit]
}

object GatewayJob {
    def all[F[_]: Async](client: DiscordGateway[F]): F[GatewayJob[F]] =
        (
            Async[F].delay(SequenceJob(client)),
            Async[F].delay(LoginJob(client)),
            Async[F].delay(EventLoggingJob[F]),
            HeartbeatingJob(client),
        ).tupled.map { tuple => Monoid[GatewayJob[F]].combineAll(tuple.toList) }

    given jobMonoid[F[_]: Concurrent]: Monoid[GatewayJob[F]] with
        def empty: GatewayJob[F] = new GatewayJob[F] {
            def entrypoint(payload: GatewayPayload, event: Stream[F, Event]): Stream[F, Unit] =
                Stream.empty
        }
        def combine(x: GatewayJob[F], y: GatewayJob[F]): GatewayJob[F] = new GatewayJob[F] {
            def entrypoint(payload: GatewayPayload, event: Stream[F, Event]): Stream[F, Unit] =
                (x.entrypoint(payload, event) merge y.entrypoint(payload, event))
        }
}
