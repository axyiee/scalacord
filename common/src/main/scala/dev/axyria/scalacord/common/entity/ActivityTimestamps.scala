package dev.axyria.scalacord.common.entity

import dev.axyria.scalacord.common.datatype.*
import dev.axyria.scalacord.common.util.*
import io.circe.Decoder
import io.circe.Encoder
import io.circe.generic.semiauto.*

/** Represents when a timestamp starts and ends. */
case class ActivityTimestamps(start: Long, end: Long)

object ActivityTimestamps {
    given encoder: Encoder[ActivityTimestamps] = deriveEncoder
    given decoder: Decoder[ActivityTimestamps] = deriveDecoder
}
