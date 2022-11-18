package dev.axyria.scalacord.common.entity

import dev.axyria.scalacord.common.datatype.*
import io.circe.Decoder.Result
import io.circe.{Decoder, Encoder, HCursor, Json}

/** The flags for an application. */
enum ApplicationFlag(val value: Int) extends BitSet[ApplicationFlag] {
    case GatewayPresence               extends ApplicationFlag(1 << 12)
    case GatewayPresenceLimited        extends ApplicationFlag(1 << 13)
    case GatewayGuildMembers           extends ApplicationFlag(1 << 14)
    case GatewayGuildMembersLimited    extends ApplicationFlag(1 << 15)
    case VerificationPendingGuildLimit extends ApplicationFlag(1 << 16)
    case Embedded                      extends ApplicationFlag(1 << 17)
    case GatewayMessageContent         extends ApplicationFlag(1 << 18)
    case GatewayMessageContentLimited  extends ApplicationFlag(1 << 19)
    case ApplicationCommandBadge       extends ApplicationFlag(1 << 23)
}

given AllBitSet[ApplicationFlag] with
    final def values: Array[ApplicationFlag] = ApplicationFlag.values
