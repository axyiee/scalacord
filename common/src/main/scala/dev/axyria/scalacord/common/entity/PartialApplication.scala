package dev.axyria.scalacord.common.entity

import dev.axyria.scalacord.common.datatype.*
import dev.axyria.scalacord.common.util.*
import io.circe.Decoder
import io.circe.Encoder
import io.circe.generic.semiauto.*

/** Represents a partial application sent at the Ready event.
  * @param id
  *   The snowflake ID for this application.
  * @param flags
  *   The flags for this application.
  */
case class PartialApplication(
    id: Snowflake = Snowflake.MinValue,
    flags: List[ApplicationFlag] = List.empty,
)

object PartialApplication {
    given encoder: Encoder[PartialApplication] = deriveEncoder
    given decoder: Decoder[PartialApplication] = deriveDecoder
}
