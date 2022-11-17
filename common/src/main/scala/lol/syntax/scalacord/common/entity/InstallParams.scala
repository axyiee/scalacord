package lol.syntax.scalacord.common.entity

import io.circe.generic.semiauto.*
import io.circe.{Decoder, Encoder}
import lol.syntax.scalacord.common.datatype.*

case class InstallParams(
    scopes: List[String] = List.empty,
    permissions: String, // TODO: bitflags-based permission system
)

object InstallParams {
    given encoder: Encoder[InstallParams] = deriveEncoder
    given decoder: Decoder[InstallParams] = deriveDecoder
}
