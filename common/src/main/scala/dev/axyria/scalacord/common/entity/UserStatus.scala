package dev.axyria.scalacord.common.entity

import dev.axyria.scalacord.common.datatype.*
import dev.axyria.scalacord.common.util.*
import io.circe.Decoder
import io.circe.Encoder
import io.circe.HCursor
import io.circe.Json

/** Represents all status an [[User]] account can have. */
enum UserStatus(val id: String) {
    case Online       extends UserStatus("online")
    case Idle         extends UserStatus("idle")
    case DoNotDisturb extends UserStatus("dnd")
    case Invisible    extends UserStatus("invisible")
    case Offline      extends UserStatus("offline")
}

object UserStatus {
    given encoder: Encoder[UserStatus] = Encoder.encodeString.contramap(_.id)

    given decoder: Decoder[UserStatus] = Decoder.decodeString.map { value =>
        UserStatus.values
            .find(_.id == value)
            .getOrElse(throw new IllegalArgumentException(s"Invalid UserStatus: $value"))
    }
}
