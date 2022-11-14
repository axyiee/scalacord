package lol.syntax.scalacord.common.entity

import io.circe.{Decoder, Encoder, HCursor, Json}
import lol.syntax.scalacord.common.datatype.*
import lol.syntax.scalacord.common.util.*

/** Represents a Discord team member.
  *
  * @param user
  *   The avatar, discriminator, id, and username of the user
  * @param teamId
  *   The id of the parent team of which they are a member
  * @param permissions
  *   The permissions of the user in the team - will always be ["*"] for now
  * @param membershipState
  *   The user's membership state on the team
  */
case class TeamMember(
    user: User,
    teamId: Snowflake = Snowflake.MinValue,
    permissions: List[String] = List.empty,
    membershipState: TeamMembershipState = TeamMembershipState.Invited,
)

object TeamMember {
    export lol.syntax.scalacord.common.entity.teamMemberEncoder
    export lol.syntax.scalacord.common.entity.teamMemberDecoder
}

given teamMemberEncoder: Encoder[TeamMember] with
    final def apply(member: TeamMember): Json =
        val elems: List[Option[HasEncoderContext[?]]] =
            (("user", member.user).context)
                :: (("team_id", member.teamId).context)
                :: (("permissions", member.permissions).context)
                :: (("membership_state", member.membershipState).context)
                :: Nil
        elems.withOptional

given teamMemberDecoder: Decoder[TeamMember] with
    final def apply(cursor: HCursor): Decoder.Result[TeamMember] =
        for {
            user            <- cursor.get[User]("user")
            teamId          <- cursor.get[Snowflake]("team_id")
            permissions     <- cursor.get[List[String]]("permissions")
            membershipState <- cursor.get[TeamMembershipState]("membership_state")
        } yield TeamMember(user, teamId, permissions, membershipState)
