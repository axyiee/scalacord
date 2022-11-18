package dev.axyria.scalacord.common.entity

import io.circe.Decoder.Result
import io.circe.{Decoder, Encoder, HCursor, Json}

/** The flags for an application. */
enum ApplicationFlag(val value: Int) {
    case GatewayPresence extends ApplicationFlag(1 << 12)

    case GatewayPresenceLimited extends ApplicationFlag(1 << 13)

    case GatewayGuildMembers extends ApplicationFlag(1 << 14)

    case GatewayGuildMembersLimited extends ApplicationFlag(1 << 15)

    case VerificationPendingGuildLimit extends ApplicationFlag(1 << 16)

    case Embedded extends ApplicationFlag(1 << 17)

    case GatewayMessageContent extends ApplicationFlag(1 << 18)

    case GatewayMessageContentLimited extends ApplicationFlag(1 << 19)

    case ApplicationCommandBadge extends ApplicationFlag(1 << 23)

    export dev.axyria.scalacord.common.entity.applicationFlagsDecoder
    export dev.axyria.scalacord.common.entity.applicationFlagsEncoder
}

given applicationFlagsEncoder: Encoder[List[ApplicationFlag]] with
    final def apply(sub: List[ApplicationFlag]): Json =
        Json.fromInt(sub.foldLeft(0)((acc, flag) => acc | flag.value))

given applicationFlagsDecoder: Decoder[List[ApplicationFlag]] with
    final def apply(cursor: HCursor): Result[List[ApplicationFlag]] =
        cursor.as[Int].map { int =>
            ApplicationFlag.values.filter(flag => (int & flag.value) == flag.value).toList
        }
