package lol.syntax.scalacord.gateway.payload.event

import io.circe.{Decoder, Encoder, HCursor, Json}
import lol.syntax.scalacord.common.datatype.Optional
import lol.syntax.scalacord.common.util.{context, optionContext, withOptional}
import lol.syntax.scalacord.common.entity.*
import lol.syntax.scalacord.gateway.payload.PayloadData
import lol.syntax.scalacord.common.util.HasEncoderContext

object ReadyCodec extends EventPayloadCodec[Ready] {
    override def decoder: Decoder[Ready] = readyDecoder

    override def encoder: Encoder[Ready] = readyEncoder

    override def eventId: String = "READY"
}

case class Ready(
    apiVersion: Int = 9,
    user: User,
    guilds: List[UnavailableGuild] = List.empty,
    sessionId: String = "",
    resumeGatewayUrl: String = "",
    shard: Optional[(Int, Int)] = Optional.missing,
    application: Application = Application()
) extends PayloadData

object Ready {
    export lol.syntax.scalacord.gateway.payload.event.readyEncoder
    export lol.syntax.scalacord.gateway.payload.event.readyDecoder
}

given readyEncoder: Encoder[Ready] with
    override def apply(ready: Ready): Json =
        val elems: List[Option[HasEncoderContext[?]]] =
            (("v", ready.apiVersion).context)
                :: (("user", ready.user).context)
                :: (("guilds", ready.guilds).context)
                :: (("session_id", ready.sessionId).context)
                :: (("resume_gateway_url", ready.resumeGatewayUrl).context)
                :: (("shard", ready.shard).optionContext)
                :: (("application", ready.application).context)
                :: Nil
        elems.withOptional

given readyDecoder: Decoder[Ready] with
    override def apply(cursor: HCursor): Decoder.Result[Ready] =
        for {
            apiVersion       <- cursor.get[Int]("v")
            user             <- cursor.get[User]("user")
            guilds           <- cursor.get[List[UnavailableGuild]]("guilds")
            sessionId        <- cursor.get[String]("session_id")
            resumeGatewayUrl <- cursor.get[String]("resume_gateway_url")
            shard            <- cursor.get[Optional[(Int, Int)]]("shard")
            application      <- cursor.get[Application]("application")
        } yield Ready(apiVersion, user, guilds, sessionId, resumeGatewayUrl, shard, application)
