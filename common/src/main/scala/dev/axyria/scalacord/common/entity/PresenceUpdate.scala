package dev.axyria.scalacord.common.entity

import dev.axyria.scalacord.common.datatype.*
import dev.axyria.scalacord.common.util.*
import io.circe.Decoder
import io.circe.Encoder
import io.circe.generic.semiauto.*

case class PresenceUpdate(
    since: Option[Int] = None,
    activities: List[Activity] = List.empty,
    status: UserStatus = UserStatus.Online,
    afk: Boolean = false
)

object PresenceUpdate {
    given encoder: Encoder[PresenceUpdate] = deriveEncoder
    given decoder: Decoder[PresenceUpdate] = deriveDecoder
}
