package lol.syntax.scalacord.gateway.payload.event

import io.circe.{Decoder, Encoder, HCursor, Json}
import lol.syntax.scalacord.common.datatype.Optional
import lol.syntax.scalacord.common.util.{context, optionContext, withOptional}
import lol.syntax.scalacord.common.entity.*
import lol.syntax.scalacord.gateway.payload.PayloadData
import lol.syntax.scalacord.common.util.HasEncoderContext

object HelloCodec extends EventPayloadCodec[Hello] {
    override def encoder: Encoder[Hello] = helloEncoder

    override def decoder: Decoder[Hello] = helloDecoder

    override def eventId: String = "HELLO"
}

case class Hello(
    val heartbeatInterval: Int
) extends PayloadData

object Hello {
    export lol.syntax.scalacord.gateway.payload.event.helloEncoder
    export lol.syntax.scalacord.gateway.payload.event.helloDecoder
}

given helloEncoder: Encoder[Hello] with
    final def apply(hello: Hello): Json =
        val elems: List[Option[HasEncoderContext[?]]] =
            (("heartbeat_interval", hello.heartbeatInterval).context)
                :: Nil
        elems.withOptional

given helloDecoder: Decoder[Hello] with
    final def apply(cursor: HCursor): Decoder.Result[Hello] =
        for {
            heartbeatInterval <- cursor.get[Int]("heartbeat_interval")
        } yield Hello(heartbeatInterval)
