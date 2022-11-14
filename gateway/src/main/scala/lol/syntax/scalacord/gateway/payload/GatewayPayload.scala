package lol.syntax.scalacord.gateway.payload

import io.circe.generic.semiauto.*
import io.circe.{Decoder, Encoder, Json}
import lol.syntax.scalacord.common.datatype.*
import lol.syntax.scalacord.gateway.payload.GatewayPayload

/** Payload format for both output and input on a Discord gateway connection.
  * @param op
  *   The type of command that is being sent/received
  * @param d
  *   The data object which is being received/sent
  * @param s
  *   An optional field for the sequence of events which can be resumed if the current connection
  *   fails
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
    given encoder: Encoder[GatewayPayload] =
        deriveEncoder[GatewayPayload].mapJson(_.skipMissing.innerOptional)
    given decoder: Decoder[GatewayPayload] = deriveDecoder
}
