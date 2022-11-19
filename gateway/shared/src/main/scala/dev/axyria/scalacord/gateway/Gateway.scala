package dev.axyria.scalacord.gateway

import cats.effect.kernel.Async
import cats.effect.kernel.Concurrent
import cats.effect.kernel.Ref
import cats.effect.std.Queue
import cats.syntax.all.*
import dev.axyria.scalacord.gateway.datatype.Intent
import dev.axyria.scalacord.gateway.payload.GatewayPayload
import fs2.Stream
import fs2.concurrent.Topic
import org.http4s.Uri
import scala.concurrent.duration.FiniteDuration
import spire.math.ULong

/** A general-purpose simple I/O consumer of the Discord Gateway.
  * @tparam F
  *   A type-class able to suspend side effects into the [[F]] context.
  */
trait Gateway[F[_]] {

    /** A simple stream of messages received from the gateway. */
    def receive: Stream[F, GatewayPayload]

    /** Send a payload to the gateway. */
    def send(payload: GatewayPayload): F[Unit]

    /** The latency between heartbeat sending and its ack receiving. */
    def ping: F[FiniteDuration]

    /** Connects into the gateway and return a stream about the WebSocket connection individually.
      * @param url
      *   The url used to connect to the gateway.
      */
    def connect(
        url: Uri = Uri.unsafeFromString("wss://gateway.discord.gg/?v=10")
    )(using Concurrent[F]): Stream[F, Unit]
}

object Gateway {
    import dev.axyria.scalacord.common.entity.PresenceUpdate
    import dev.axyria.scalacord.common.entity.Shard
    import dev.axyria.scalacord.gateway.decoder.TransportCompressedMessageIo
    import dev.axyria.scalacord.gateway.platform.WsClientPlatform
    import java.util.zip.Inflater

    def apply[F[_]: Async](
        token: String,
        intents: List[Intent] = List(Intent.Guilds),
        shard: Option[Shard] = None,
        largeThreshold: Int = 50,
        presence: Option[PresenceUpdate] = None,
    ): Stream[F, Gateway[F]] =
        (
            Stream.resource(WsClientPlatform[F]),
            Stream.eval(Topic[F, GatewayPayload]),
            Stream.eval(Queue.unbounded[F, GatewayPayload]),
            Stream.eval(Ref.of[F, Option[ULong]](None)),
            Stream.eval(Ref.of[F, Option[FiniteDuration]](None)),
            Stream
                .eval(Ref.of[F, Option[Inflater]](None))
                .map(ref => TransportCompressedMessageIo[F](ref))
                .map(io =>
                    DiscordGatewaySettings(token, io, shard, largeThreshold, presence, intents)
                ),
        ).mapN(DiscordGateway[F](_, _, _, _, _, _))
}
