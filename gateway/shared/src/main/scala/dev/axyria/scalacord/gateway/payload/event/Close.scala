package dev.axyria.scalacord.gateway.payload.event

import dev.axyria.scalacord.gateway.payload.PayloadData
import io.circe.Decoder
import io.circe.Encoder
import io.circe.HCursor
import io.circe.Json
import io.circe.generic.semiauto.*

/** Artificial event used to indicate connection closing. */
object CloseCodec extends EventPayloadCodec[Close] {
    override def encoder: Encoder[Close] = Close.encoder

    override def decoder: Decoder[Close] = Close.decoder

    override def eventId: Option[String] = None

    override def opCode: Int = -1
}

/** Artificial event used to indicate connection closing.
  * @param code
  *   The status code for the close event
  * @param reason
  *   The reason for the close event
  */
case class Close(code: Int, reason: String) extends PayloadData

object Close {
    given encoder: Encoder[Close] = deriveEncoder
    given decoder: Decoder[Close] = deriveDecoder
}
