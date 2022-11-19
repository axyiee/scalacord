package dev.axyria.scalacord.gateway.payload.command

import dev.axyria.scalacord.gateway.payload.PayloadData
import io.circe.Decoder
import io.circe.Encoder
import io.circe.HCursor
import io.circe.Json

/** Used to maintain an active gateway connection. */
object HeartbeatCodec extends CommandPayloadCodec[Heartbeat] {
    override def opCode: Int = 1

    override def encoder: Encoder[Heartbeat] = heartbeatEncoder

    override def decoder: Decoder[Heartbeat] = heartbeatDecoder
}

/** Used to maintain an active gateway connection.
  * @param sequence
  *   The last sequence number received by the client.
  */
case class Heartbeat(sequence: Option[Long]) extends PayloadData

object Heartbeat {
    export dev.axyria.scalacord.gateway.payload.command.heartbeatDecoder
    export dev.axyria.scalacord.gateway.payload.command.heartbeatEncoder
}

given heartbeatEncoder: Encoder[Heartbeat] with
    final def apply(heartbeat: Heartbeat): Json =
        Encoder.encodeOption[Long].apply(heartbeat.sequence)

given heartbeatDecoder: Decoder[Heartbeat] with
    final def apply(cursor: HCursor): Decoder.Result[Heartbeat] =
        Decoder.decodeOption[Long].apply(cursor).map(value => Heartbeat(value))
