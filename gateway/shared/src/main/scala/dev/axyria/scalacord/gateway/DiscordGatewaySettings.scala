package dev.axyria.scalacord.gateway

import dev.axyria.scalacord.common.entity.Shard
import dev.axyria.scalacord.gateway.decoder.MessageIo
import org.http4s.Uri

/** Properties taken in consideration to create a connection into the Discord Gateway.
  * @param token
  *   The token for performing the authentication
  * @param shard
  *   The shard information, if required
  * @param io
  *   The message frame encoder/decoder, useful to differ from compressed environments.
  * @tparam F
  *   A type-class able to suspend side effects into the [[F]] context.
  */
case class DiscordGatewaySettings[F[_]](
    token: String,
    io: MessageIo[F],
    shard: Option[Shard] = None,
)
