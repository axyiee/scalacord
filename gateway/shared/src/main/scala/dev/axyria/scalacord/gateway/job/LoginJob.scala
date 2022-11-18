package dev.axyria.scalacord.gateway.job

import cats.effect.kernel.Sync
import dev.axyria.scalacord.common.datatype.Optional
import dev.axyria.scalacord.gateway.DiscordGateway
import dev.axyria.scalacord.gateway.decoder.CompressionKind
import dev.axyria.scalacord.gateway.payload.GatewayPayload
import dev.axyria.scalacord.gateway.payload.command.{Identify, IdentifyCodec}
import dev.axyria.scalacord.gateway.payload.event.Hello
import fs2.Stream
import cats.syntax.all.*
import dev.axyria.scalacord.gateway.payload.event.Ready

/** A job for providing our identity to Gateway as a step of authentication.
  *
  * @tparam F
  *   A type-class able to suspend side effects into the [[F]] context.
  */
case class LoginJob[F[_]: Sync](client: DiscordGateway[F]) extends GatewayJob[F] {
    override def entrypoint(payload: GatewayPayload): Stream[F, Unit] =
        Stream.fromEither(payload.event).flatMap { event =>
            event.data match {
                case _: Hello =>
                    Stream
                        .emit(
                            Identify(
                                client.settings.token,
                                Optional.keepOrMiss(client.settings.shard),
                                client.settings.io.kind == CompressionKind.Payload,
                            )
                        )
                        .evalMap(identify => client.send(IdentifyCodec.toPayload(identify)))
                case _        => Stream.empty
            }
        }
}
