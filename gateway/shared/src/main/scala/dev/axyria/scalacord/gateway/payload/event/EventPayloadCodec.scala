package dev.axyria.scalacord.gateway.payload.event

import dev.axyria.scalacord.gateway.payload.PayloadCodec
import dev.axyria.scalacord.gateway.payload.PayloadData
import io.circe.Encoder

object EventPayloadCodec {

    /** The opcode for most events. */
    val DefaultOpCode = 0
}

/** A payload codec trait driven especially for gateway events.
  * @tparam A
  *   The kind of event-driven [[PayloadData]] this codec refers to.
  */
trait EventPayloadCodec[A <: PayloadData] extends PayloadCodec[A] {
    override def opCode: Int = EventPayloadCodec.DefaultOpCode

    /** Returns the identifier for this event. This is used amongst all events in order to
      * distinguish between them. It is sent as the 't' field on a
      * [[dev.axyria.scalacord.gateway.payload.GatewayPayload]].
      */
    def eventId: Option[String]
}

/** An object representing all available event codecs on Scalacord. */
object Events {
    // (opcode, event id)
    type Id = (Int, Option[String])

    val Codecs: Map[Id, EventPayloadCodec[?]] =
        List(HelloCodec, ReadyCodec, HeartbeatAckCodec, CloseCodec)
            .map(codec => ((codec.opCode, codec.eventId), codec))
            .toMap

    def get(
        id: Option[String],
        opcode: Int = EventPayloadCodec.DefaultOpCode
    ): Option[EventPayloadCodec[?]] = Codecs.get((opcode, id))
}
