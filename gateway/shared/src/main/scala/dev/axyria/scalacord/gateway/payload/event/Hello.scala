package dev.axyria.scalacord.gateway.payload.event

import dev.axyria.scalacord.common.datatype.Optional
import dev.axyria.scalacord.common.entity.*
import dev.axyria.scalacord.common.util.EncodingContext
import dev.axyria.scalacord.common.util.context
import dev.axyria.scalacord.common.util.optionContext
import dev.axyria.scalacord.common.util.withOptional
import dev.axyria.scalacord.gateway.payload.PayloadData
import io.circe.Decoder
import io.circe.Encoder
import io.circe.HCursor
import io.circe.Json

/** Handshake event sent by the gateway after the connection is established. */
object HelloCodec extends EventPayloadCodec[Hello] {
    override def encoder: Encoder[Hello] = helloEncoder

    override def decoder: Decoder[Hello] = helloDecoder

    override def eventId: Option[String] = None

    override def opCode: Int = 10
}

/** Handshake event sent by the gateway after the connection is established.
  * @param heartbeatInterval
  *   Interval (in milliseconds) an app should heartbeat with
  */
case class Hello(heartbeatInterval: Long) extends PayloadData

object Hello {
    export dev.axyria.scalacord.gateway.payload.event.helloDecoder
    export dev.axyria.scalacord.gateway.payload.event.helloEncoder
}

given helloEncoder: Encoder[Hello] with
    final def apply(hello: Hello): Json =
        val elems: List[Option[EncodingContext[?]]] =
            ("heartbeat_interval", hello.heartbeatInterval).context
                :: Nil
        elems.withOptional

given helloDecoder: Decoder[Hello] with
    final def apply(cursor: HCursor): Decoder.Result[Hello] =
        for {
            heartbeatInterval <- cursor.get[Long]("heartbeat_interval")
        } yield Hello(heartbeatInterval)
