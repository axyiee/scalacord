package dev.axyria.scalacord.common.entity

import dev.axyria.scalacord.common.datatype.*
import dev.axyria.scalacord.common.util.*
import io.circe.{Decoder, Encoder, HCursor, Json}

/** Represents all types an [[Activity]] can have. */
enum ActivityType(val id: Int) {
    case Game      extends ActivityType(0)
    case Streaming extends ActivityType(1)
    case Listening extends ActivityType(2)
    case Watching  extends ActivityType(3)
    case Custom    extends ActivityType(4)
    case Competing extends ActivityType(5)
}

object ActivityType {
    given encoder: Encoder[ActivityType] = Encoder.encodeInt.contramap(_.id)

    given decoder: Decoder[ActivityType] = Decoder.decodeInt.map { value =>
        ActivityType.values
            .find(_.id == value)
            .getOrElse(throw new IllegalArgumentException(s"Invalid ActivityType: $value"))
    }
}
