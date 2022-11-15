package lol.syntax.scalacord.gateway.payload.event

import io.circe.Encoder
import lol.syntax.scalacord.gateway.payload.{PayloadCodec, PayloadData}

/** A payload codec trait driven especially for gateway events.
  * @tparam A
  *   The kind of event-driven [[PayloadData]] this codec refers to.
  */
trait EventPayloadCodec[A <: PayloadData] extends PayloadCodec[A] {
    override def opCode: Int = 0

    /** Returns the identifier for this event. This is used amongst all events in order to
      * distinguish between them. It is sent as the 't' field on a
      * [[lol.syntax.scalacord.gateway.payload.GatewayPayload]].
      */
    def eventId: String
}

/** An object representing all available event codecs on Scalacord. */
object Events {
    val Codecs: Map[String, EventPayloadCodec[?]] =
        List(HelloCodec, ReadyCodec).map(codec => (codec.eventId, codec)).toMap
}
