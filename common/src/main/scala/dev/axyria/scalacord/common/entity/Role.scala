package dev.axyria.scalacord.common.entity

import dev.axyria.scalacord.common.datatype.*
import dev.axyria.scalacord.common.util.*
import io.circe.{Decoder, Encoder, HCursor, Json}

/** Roles represent a set of permissions attached to a group of users. Roles have names, colors, and
  * can be "pinned" to the side bar, causing their members to be listed separately. Roles can have
  * separate permission profiles for the global context (guild) and channel context. The @everyone
  * role has the same ID as the guild it belongs to.
  *
  * @param id
  *   The role id.
  * @param name
  *   The role name.
  * @param color
  *   Integer representation of hexadecimal color code.
  * @param isHoist
  *   Whether the role is pinned in the user listing.
  * @param iconHash
  *   The role icon hash.
  * @param unicodeEmoji
  *   The role unicode emoji.
  * @param position
  *   The position of this role in the role hierarchy.
  * @param permissions
  *   The permissions bit set.
  * @param isManaged
  *   Whether this role is managed by an integration.
  * @param isMentionable
  *   Whether this role is mentionable.
  * @param tags
  *   The tags this role has.
  */
case class Role(
    id: Snowflake = Snowflake.MinValue,
    name: String = "",
    color: Color = Color(0, 0, 0),
    isHoist: Boolean = false,
    iconHash: Optional[String] = Optional.missing,
    unicodeEmoji: Optional[String] = Optional.missing,
    position: Int = 0,
    permissions: List[Permission] = List.empty,
    isManaged: Boolean = false,
    isMentionable: Boolean = false,
    tags: Optional[List[RoleTags]] = Optional.missing,
)

object Role {
    export dev.axyria.scalacord.common.entity.roleEncoder
    export dev.axyria.scalacord.common.entity.roleDecoder
}

given roleEncoder: Encoder[Role] with
    final def apply(role: Role): Json =
        val elems: List[Option[EncodingContext[?]]] =
            ("id", role.id).context
                :: ("name", role.name).context
                :: ("color", role.color).context
                :: ("hoist", role.isHoist).context
                :: ("icon", role.iconHash).optionContext
                :: ("unicode_emoji", role.unicodeEmoji).optionContext
                :: ("position", role.position).context
                :: ("permissions", role.permissions).context
                :: ("managed", role.isManaged).context
                :: ("mentionable", role.isMentionable).context
                :: ("tags", role.tags).optionContext
                :: Nil
        elems.withOptional

given roleDecoder: Decoder[Role] with
    final def apply(cursor: HCursor): Decoder.Result[Role] =
        for
            id            <- cursor.get[Snowflake]("id")
            name          <- cursor.get[String]("name")
            color         <- cursor.get[Color]("color")
            isHoist       <- cursor.get[Boolean]("hoist")
            iconHash      <- cursor.get[Optional[String]]("icon")
            unicodeEmoji  <- cursor.get[Optional[String]]("unicode_emoji")
            position      <- cursor.get[Int]("position")
            permissions   <- cursor.get[List[Permission]]("permissions")
            isManaged     <- cursor.get[Boolean]("managed")
            isMentionable <- cursor.get[Boolean]("mentionable")
            tags          <- cursor.get[Optional[List[RoleTags]]]("tags")
        yield Role(
            id,
            name,
            color,
            isHoist,
            iconHash,
            unicodeEmoji,
            position,
            permissions,
            isManaged,
            isMentionable,
            tags,
        )
