package dev.axyria.scalacord.common.entity

import dev.axyria.scalacord.common.datatype.*
import io.circe.Decoder
import io.circe.Decoder.Result
import io.circe.Encoder
import io.circe.HCursor
import io.circe.Json

/** The flags (can also be represented as badges) for an [[Activity]]. */
enum ActivityFlag(val value: Int) extends BitSet[ActivityFlag] {
    case Instance                 extends ActivityFlag(1 << 0)
    case Join                     extends ActivityFlag(1 << 1)
    case Spectate                 extends ActivityFlag(1 << 2)
    case JoinRequest              extends ActivityFlag(1 << 3)
    case Sync                     extends ActivityFlag(1 << 4)
    case Play                     extends ActivityFlag(1 << 5)
    case PartyPrivacyFriends      extends ActivityFlag(1 << 6)
    case PartyPrivacyVoiceChannel extends ActivityFlag(1 << 7)
    case Embedded                 extends ActivityFlag(1 << 8)
}

object ActivityFlag {
    given all: AllBitSet[ActivityFlag] with
        final def values: Array[ActivityFlag] = ActivityFlag.values
}
