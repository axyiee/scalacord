package dev.axyria.scalacord.common.entity

import dev.axyria.scalacord.common.datatype.*
import io.circe.Decoder
import io.circe.Decoder.Result
import io.circe.Encoder
import io.circe.HCursor
import io.circe.Json

/** The flags (can also be represented as badges) for an user account. */
enum UserFlag(val value: Int) extends BitSet[UserFlag] {
    case Staff                 extends UserFlag(1 << 0)
    case Partner               extends UserFlag(1 << 1)
    case HypeSquad             extends UserFlag(1 << 2)
    case BugHunterLevel1       extends UserFlag(1 << 3)
    case HypeSquadOnlineHouse1 extends UserFlag(1 << 6)
    case HypeSquadOnlineHouse2 extends UserFlag(1 << 7)
    case HypeSquadOnlineHouse3 extends UserFlag(1 << 8)
    case EarlySupporter        extends UserFlag(1 << 9)
    case TeamPseudoUser        extends UserFlag(1 << 10)
    case BugHunterLevel2       extends UserFlag(1 << 14)
    case VerifiedBot           extends UserFlag(1 << 16)
    case VerifiedDeveloper     extends UserFlag(1 << 17)
    case CertifiedModerator    extends UserFlag(1 << 18)
    case BotHasInteractions    extends UserFlag(1 << 19)
}

object UserFlag {
    given all: AllBitSet[UserFlag] with
        final def values: Array[UserFlag] = UserFlag.values
}
