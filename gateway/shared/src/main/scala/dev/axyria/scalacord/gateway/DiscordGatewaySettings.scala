package dev.axyria.scalacord.gateway

import dev.axyria.scalacord.common.entity.PresenceUpdate
import dev.axyria.scalacord.common.entity.Shard
import dev.axyria.scalacord.gateway.datatype.Intent
import dev.axyria.scalacord.gateway.decoder.MessageIo
import org.http4s.Uri

/** Properties taken in consideration to create a connection into the Discord Gateway.
  * @param token
  *   The token for performing the authentication.
  * @param io
  *   The message frame encoder/decoder, useful to differ from compressed environments.
  * @param shard
  *   The shard information, if required.
  * @param largeThreshold
  *   The number of members in a guild after which offline users will no longer be sent in the
  *   initial guild member list.
  * @param presence
  *   The initial presence information.
  * @param intents
  *   The gateway intents to use.
  * @tparam F
  *   A type-class able to suspend side effects into the [[F]] context.
  */
case class DiscordGatewaySettings[F[_]](
    token: String,
    io: MessageIo[F],
    shard: Option[Shard] = None,
    largeThreshold: Int = 50,
    presence: Option[PresenceUpdate] = None,
    intents: List[Intent] = List.empty,
)
