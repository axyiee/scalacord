package lol.syntax.scalacord.common.entity

import io.circe.Decoder.Result
import io.circe.{Decoder, Encoder, HCursor, Json}

enum UserFlag(val value: Int) {
    case Staff extends UserFlag(1 << 0)

    case Partner extends UserFlag(1 << 1)

    case HypeSquad extends UserFlag(1 << 2)

    case BugHunterLevel1 extends UserFlag(1 << 3)

    case HypeSquadOnlineHouse1 extends UserFlag(1 << 6)

    case HypeSquadOnlineHouse2 extends UserFlag(1 << 7)

    case HypeSquadOnlineHouse3 extends UserFlag(1 << 8)

    case EarlySupporter extends UserFlag(1 << 9)

    case TeamPseudoUser extends UserFlag(1 << 10)

    case BugHunterLevel2 extends UserFlag(1 << 14)

    case VerifiedBot extends UserFlag(1 << 16)

    case VerifiedDeveloper extends UserFlag(1 << 17)

    case CertifiedModerator extends UserFlag(1 << 18)

    case BotHasInteractions extends UserFlag(1 << 19)

    export lol.syntax.scalacord.common.entity.userFlagsDecoder
    export lol.syntax.scalacord.common.entity.userFlagsEncoder
}

given userFlagsEncoder: Encoder[List[UserFlag]] with
    final def apply(sub: List[UserFlag]): Json =
        Json.fromInt(sub.foldLeft(0)((acc, flag) => acc | flag.value))

given userFlagsDecoder: Decoder[List[UserFlag]] with
    final def apply(cursor: HCursor): Result[List[UserFlag]] =
        cursor.as[Int].map { int =>
            UserFlag.values.filter(flag => (int & flag.value) == flag.value).toList
        }
