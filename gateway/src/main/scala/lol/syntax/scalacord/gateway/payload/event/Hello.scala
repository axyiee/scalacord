package lol.syntax.scalacord.gateway.payload.event

import io.circe.generic.semiauto.*
import io.circe.{Decoder, Encoder}
import lol.syntax.scalacord.common.datatype.Optional
import lol.syntax.scalacord.common.entity.User
import lol.syntax.scalacord.gateway.payload.PayloadData

object ReadyCodec extends EventPayloadCodec[Ready] {
    override def decoder: Decoder[Ready] = deriveDecoder

    override def encoder: Encoder[Ready] = deriveEncoder

    override def eventId: String = "READY"
}

case class Ready(
    apiVersion: Int = 9,
    user: User,
    // guilds: List[UnavailableGuild],
    sessionId: String,
    resumeGatewayUrl: String,
    shard: Optional[(Int, Int)] = Optional.missing,
    // application: Application
) extends PayloadData
