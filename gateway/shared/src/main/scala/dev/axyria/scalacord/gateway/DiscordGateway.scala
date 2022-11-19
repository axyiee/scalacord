// noinspection ScalaDocUnknownParameter
package dev.axyria.scalacord.gateway

import cats.Monad
import cats.effect.*
import cats.effect.std.Queue
import cats.syntax.all.*
import dev.axyria.scalacord.gateway.job.GatewayJob
import dev.axyria.scalacord.gateway.payload.GatewayPayload
import dev.axyria.scalacord.gateway.util.skipUselessErrors
import fs2.Pipe
import fs2.Stream
import fs2.concurrent.Topic
import io.circe.Encoder
import io.circe.Json
import io.circe.syntax.EncoderOps
import java.util.concurrent.TimeUnit
import org.http4s.Uri
import org.http4s.client.websocket.WSClientHighLevel
import org.http4s.client.websocket.WSConnectionHighLevel
import org.http4s.client.websocket.WSDataFrame
import org.http4s.client.websocket.WSRequest
import scala.concurrent.duration.FiniteDuration
import spire.math.ULong

/** The stream type for receiving payloads. Topic is being used because a topic is a type similar to
  * a [[Stream]] which allows publishing and an arbitrary number of consumers.
  * @tparam F
  *   The monad-ish type for composing stuff into the [[F]] context.
  */
type InboundStream[F[_]] = Topic[F, GatewayPayload]

/** The stream/queue analyzed often to send data back to Discord. Queue being is used instead of fs2
  * alternatives because it already suits all necessities, which are simply 'arbitrary popping' in
  * order to send data back to Discord.
  * @tparam F
  *   The monad-ish type for composing stuff into the [[F]] context.
  */
type OutboundStream[F[_]] = Queue[F, GatewayPayload]

/** A atomic reference for event sequence numbers. It is used to resume a failing connection.
  *
  * @tparam F
  *   The monad-ish type for composing stuff into the [[F]] context.
  */
type EventSequence[F[_]] = Ref[F, Option[ULong]]

/** A atomic reference for the gateway send/receive latency.
  *
  * @tparam F
  *   The monad-ish type for composing stuff into the [[F]] context.
  */
type Latency[F[_]] = Ref[F, Option[FiniteDuration]]

/** A simple and functional consumer for the Discord Gateway, built on top of cats and fs2.
  * @param client
  *   The active connection of the websocket.
  * @param inbound
  *   The stream for messages received *from* Discord.
  * @param outbound
  *   The queue for sending messages *to* Discord.
  * @param sequence
  *   An incrementing event sequence counter used to resume failing connections.
  * @param settings
  *   The configuration for this gateway connection, mostly connection-related stuff.
  * @tparam F
  *   A type-class able to suspend side effects into the [[F]] context.
  */
case class DiscordGateway[F[_]: Async: Concurrent](
    client: WSClientHighLevel[F],
    inbound: InboundStream[F],
    outbound: OutboundStream[F],
    sequence: EventSequence[F],
    ping0: Latency[F],
    settings: DiscordGatewaySettings[F]
) extends Gateway[F] {
    def receive: Stream[F, GatewayPayload] = inbound.subscribe(256)

    def send(payload: GatewayPayload): F[Unit] =
        outbound.offer(payload)

    def ping: F[FiniteDuration] =
        ping0.get.map(_.getOrElse(FiniteDuration(Long.MaxValue, TimeUnit.MILLISECONDS)))

    def connect(
        url: Uri = Uri.unsafeFromString("wss://gateway.discord.gg/?v=10")
    )(using Concurrent[F]): Stream[F, Unit] =
        settings.io.kind
            .compileUrl(url)
            .flatMap(uri => Stream.resource(client.connectHighLevel(WSRequest(uri))))
            .map { connection =>
                given WSConnectionHighLevel[F] = connection
                (publishEnqueued concurrently subscribe).merge(listen)
            }
            .parJoinUnbounded

    private def listen: Stream[F, Unit] =
        Stream
            .eval(GatewayJob.all[F](this))
            .flatMap(job =>
                receive
                    .map(payload =>
                        job.entrypoint(payload, Stream.fromEither(payload.event).skipUselessErrors)
                    )
                    .parJoinUnbounded
            )

    private def subscribe(using
        connection: WSConnectionHighLevel[F]
    )(using Concurrent[F]): Stream[F, Unit] =
        settings.io.setup
            .flatMap(_ => connection.receiveStream)
            .through(settings.io.decode)
            .flatMap(json => Stream.fromEither(json.as[GatewayPayload]))
            .through(inbound.publish)

    private def publishEnqueued(using connection: WSConnectionHighLevel[F]): Stream[F, Unit] =
        Stream
            .fromQueueUnterminated(outbound)
            .map(payload => payload.asJson)
            .through(settings.io.encode)
            .evalMap(connection.send)
}
