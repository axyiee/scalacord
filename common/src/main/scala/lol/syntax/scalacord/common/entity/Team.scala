package lol.syntax.scalacord.common.entity

import io.circe.{Decoder, Encoder, HCursor, Json}
import lol.syntax.scalacord.common.datatype.*
import lol.syntax.scalacord.common.util.*

/** A team representing a group of applications.
  *
  * @param id
  *   The unique id of the team
  * @param icon
  *   A hash of the image of the team's icon
  * @param members
  *   The members of the team
  * @param ownerUserId
  *   The id of the current team owner
  */
case class Team(
    id: Snowflake = Snowflake.MinValue,
    icon: Option[String] = None,
    members: List[TeamMember] = List.empty,
    ownerUserId: Snowflake = Snowflake.MinValue
)

object Team {
    export lol.syntax.scalacord.common.entity.teamEncoder
    export lol.syntax.scalacord.common.entity.teamDecoder
}

given teamEncoder: Encoder[Team] with
    final def apply(team: Team): Json =
        val elems: List[Option[EncodingContext[?]]] =
            ("id", team.id).context
                :: ("icon", team.icon).context
                :: ("members", team.members).context
                :: ("owner_user_id", team.ownerUserId).context
                :: Nil
        elems.withOptional

given teamDecoder: Decoder[Team] with
    final def apply(c: HCursor): Decoder.Result[Team] =
        for
            id          <- c.get[Snowflake]("id")
            icon        <- c.get[Option[String]]("icon")
            members     <- c.get[List[TeamMember]]("members")
            ownerUserId <- c.get[Snowflake]("owner_user_id")
        yield Team(id, icon, members, ownerUserId)
