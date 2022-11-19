package dev.axyria.scalacord.gateway.payload.event

import dev.axyria.scalacord.gateway.payload.PayloadData
import io.circe.Decoder
import io.circe.Encoder
import io.circe.HCursor
import io.circe.Json

/** When you send a Heartbeat event, Discord will respond with a Heartbeat ACK (opcode 11) event,
  * which is an acknowledgement that the heartbeat was received.
  */
object HeartbeatAckCodec extends EventPayloadCodec[HeartbeatAck.type] {
    override def encoder: Encoder[HeartbeatAck.type] = heartbeatAckEncoder

    override def decoder: Decoder[HeartbeatAck.type] = heartbeatAckDecoder

    override def eventId: Option[String] = None

    override def opCode: Int = 11
}

/** When you send a Heartbeat event, Discord will respond with a Heartbeat ACK (opcode 11) event,
  * which is an acknowledgement that the heartbeat was received.
  */
case object HeartbeatAck extends PayloadData {
    export dev.axyria.scalacord.gateway.payload.event.heartbeatAckDecoder
    export dev.axyria.scalacord.gateway.payload.event.heartbeatAckEncoder
}

given heartbeatAckEncoder: Encoder[HeartbeatAck.type] with
    final def apply(heartbeatAck: HeartbeatAck.type): Json =
        Json.Null

given heartbeatAckDecoder: Decoder[HeartbeatAck.type] with
    final def apply(cursor: HCursor): Decoder.Result[HeartbeatAck.type] =
        Right(HeartbeatAck)
