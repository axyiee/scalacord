package lol.syntax.scalacord.gateway.payload.event

import io.circe.generic.semiauto.*
import io.circe.{Decoder, Encoder}
import lol.syntax.scalacord.gateway.payload.PayloadData

object HelloCodec extends EventPayloadCodec[Hello] {
    override def decoder: Decoder[Hello] = deriveDecoder

    override def encoder: Encoder[Hello] = deriveEncoder

    override def eventId: String = "HELLO"
}

case class Hello() extends PayloadData
