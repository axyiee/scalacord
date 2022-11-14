package lol.syntax.scalacord.common.entity

import io.circe.{Encoder, Decoder}
import io.circe.generic.semiauto.*
import lol.syntax.scalacord.common.datatype.*
import lol.syntax.scalacord.common.util.*

case class InstallParams(
    val scopes: List[String] = List.empty,
    val permissions: String, // TODO: bitflags-based permission system
)

object InstallParams {
    given encoder: Encoder[InstallParams] = deriveEncoder
    given decoder: Decoder[InstallParams] = deriveDecoder
}
