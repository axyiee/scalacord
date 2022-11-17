package lol.syntax.scalacord.gateway.payload

import io.circe.generic.semiauto.*
import io.circe.{Decoder, Encoder, HCursor, Json}
import lol.syntax.scalacord.common.datatype.*
import lol.syntax.scalacord.common.util.*

import scala.::

/** Payload format for both output and input on a Discord gateway connection.
  * @param op
  *   The type of command that is being sent/received.
  * @param d
  *   The data object which is being received/sent.
  * @param s
  *   An optional field for the sequence of events which can be resumed if the current connection
  *   fails.
  * @param t
  *   An optional field for the type of event being received if applicable. This must be omitted
  *   when being used as output.
  */
case class GatewayPayload(
    op: Int,
    d: Json,
    s: Optional[Long] = Optional.missing,
    t: Optional[String] = Optional.missing
)

object GatewayPayload {
    export lol.syntax.scalacord.gateway.payload.payloadEncoder
    export lol.syntax.scalacord.gateway.payload.payloadDecoder
}

given payloadEncoder: Encoder[GatewayPayload] with
    final def apply(payload: GatewayPayload): Json =
        val elems: List[Option[EncodingContext[?]]] =
            ("op", payload.op).context
                :: ("d", payload.d).context
                :: ("s", payload.s).optionContext
                :: ("t", payload.t).optionContext
                :: Nil
        elems.withOptional

given payloadDecoder: Decoder[GatewayPayload] with
    final def apply(cursor: HCursor): Decoder.Result[GatewayPayload] =
        for {
            op <- cursor.get[Int]("op")
            d  <- cursor.get[Json]("d")
            s  <- cursor.get[Optional[Long]]("s")
            t  <- cursor.get[Optional[String]]("t")
        } yield GatewayPayload(op, d, s, t)
