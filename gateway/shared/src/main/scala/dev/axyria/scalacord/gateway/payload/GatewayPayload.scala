package dev.axyria.scalacord.gateway.payload

import dev.axyria.scalacord.common.datatype.*
import dev.axyria.scalacord.common.util.*
import dev.axyria.scalacord.gateway.payload.event.{Event, Events}
import io.circe.*
import io.circe.DecodingFailure.Reason.CustomReason
import io.circe.generic.semiauto.*

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
    d: Json = Json.Null,
    s: Optional[Long] = Optional.missing,
    t: Optional[String] = Optional.missing
) {
    @transient
    lazy val event: Either[DecodingFailure, Event] = Events
        .get(t.toOption, op)
        .toRight(
            DecodingFailure(
                CustomReason(s"Codec not found for [$op, ${t.toOption}]"),
                d.hcursor
            )
        )
        .flatMap { co =>
            val event = co.decoder(d.hcursor)
            event.map(d => Event(co, d, s.toOption.getOrElse(0L)))
        }
}

object GatewayPayload {
    export dev.axyria.scalacord.gateway.payload.payloadEncoder
    export dev.axyria.scalacord.gateway.payload.payloadDecoder
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
