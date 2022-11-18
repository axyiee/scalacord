package dev.axyria.scalacord.gateway.payload.event

import dev.axyria.scalacord.common.entity.Shard
import dev.axyria.scalacord.common.entity.UnavailableGuild
import dev.axyria.scalacord.common.entity.PartialApplication
import dev.axyria.scalacord.common.datatype.Optional
import dev.axyria.scalacord.common.util.{context, optionContext, withOptional}
import dev.axyria.scalacord.common.entity.*
import dev.axyria.scalacord.gateway.payload.PayloadData
import dev.axyria.scalacord.common.util.EncodingContext
import io.circe.{Decoder, Encoder, HCursor, Json}

/** The ready event is dispatched when a client has completed the initial handshake with the gateway
  * (for new sessions). The ready event can be the largest and most complex event the gateway will
  * send, as it contains all the state required for a client to begin interacting with the rest of
  * the platform.
  */
object ReadyCodec extends EventPayloadCodec[Ready] {
    override def decoder: Decoder[Ready] = readyDecoder

    override def encoder: Encoder[Ready] = readyEncoder

    override def eventId: Option[String] = Some("READY")
}

/** The ready event is dispatched when a client has completed the initial handshake with the gateway
  * (for new sessions). The ready event can be the largest and most complex event the gateway will
  * send, as it contains all the state required for a client to begin interacting with the rest of
  * the platform.
  *
  * @param apiVersion
  *   [[https://discord.com/developers/docs/reference#api-versioning-api-versions API version]]
  * @param user
  *   Information about the user including email
  * @param guilds
  *   Guilds the user is in
  * @param sessionId
  *   Used for resuming connections
  * @param resumeGatewayUrl
  *   Gateway URL for resuming connections
  * @param shard
  *   Shard information associated with this session, if sent when identifying
  * @param application
  *   Contains id and flags
  */
case class Ready(
    apiVersion: Int = 9,
    user: User,
    guilds: List[UnavailableGuild] = List.empty,
    sessionId: String = "",
    resumeGatewayUrl: String = "",
    shard: Optional[Shard] = Optional.missing,
    application: PartialApplication = PartialApplication()
) extends PayloadData

object Ready {
    export dev.axyria.scalacord.gateway.payload.event.readyEncoder
    export dev.axyria.scalacord.gateway.payload.event.readyDecoder
}

given readyEncoder: Encoder[Ready] with
    override def apply(ready: Ready): Json =
        val elems: List[Option[EncodingContext[?]]] =
            ("v", ready.apiVersion).context
                :: ("user", ready.user).context
                :: ("guilds", ready.guilds).context
                :: ("session_id", ready.sessionId).context
                :: ("resume_gateway_url", ready.resumeGatewayUrl).context
                :: ("shard", ready.shard).optionContext
                :: ("application", ready.application).context
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
            shard            <- cursor.get[Optional[Shard]]("shard")
            application      <- cursor.get[PartialApplication]("application")
        } yield Ready(apiVersion, user, guilds, sessionId, resumeGatewayUrl, shard, application)
