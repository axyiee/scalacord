package dev.axyria.scalacord.common.entity

import dev.axyria.scalacord.common.datatype.*
import io.circe.Decoder
import io.circe.Encoder
import io.circe.generic.semiauto.*

case class InstallParams(
    scopes: List[String] = List.empty,
    permissions: String, // TODO: bitflags-based permission system
)

object InstallParams {
    given encoder: Encoder[InstallParams] = deriveEncoder
    given decoder: Decoder[InstallParams] = deriveDecoder
}
