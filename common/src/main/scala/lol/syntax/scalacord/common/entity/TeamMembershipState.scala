package lol.syntax.scalacord.common.entity

import io.circe.Decoder.Result
import io.circe.{Decoder, Encoder, HCursor, Json}

/** The state (whether the invite was accepted) of a team membership. */
enum TeamMembershipState(val value: Int) {
    case Invited extends TeamMembershipState(1)

    case Accepted extends TeamMembershipState(2)

    export lol.syntax.scalacord.common.entity.membershipDecoder
    export lol.syntax.scalacord.common.entity.membershipEncoder
}

given membershipEncoder: Encoder[TeamMembershipState] with
    final def apply(sub: TeamMembershipState): Json =
        Json.fromInt(sub.value)

given membershipDecoder: Decoder[TeamMembershipState] with
    final def apply(cursor: HCursor): Result[TeamMembershipState] =
        cursor.as[Int].map { int => TeamMembershipState.values.find(_.value == int).get }
