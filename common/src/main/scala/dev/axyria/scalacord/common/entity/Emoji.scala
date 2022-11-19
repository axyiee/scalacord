package dev.axyria.scalacord.common.entity

import dev.axyria.scalacord.common.datatype.*
import dev.axyria.scalacord.common.util.*
import io.circe.Decoder
import io.circe.Encoder
import io.circe.HCursor
import io.circe.Json

/** Represents an emoji on Discord.
  *
  * @param id
  *   The id of the emoji.
  * @param name
  *   The name of the emoji.
  * @param roles
  *   The roles that can use this emoji.
  * @param user
  *   The user that created this emoji.
  * @param requireColons
  *   Whether this emoji must be wrapped in colons.
  * @param isManaged
  *   Whether this emoji is managed.
  * @param isAnimated
  *   Whether this emoji is animated.
  * @param isAvailable
  *   Whether this emoji can be used, may be false due to loss of Server Boosts.
  */
case class Emoji(
    id: Optional[Snowflake] = Optional.missing,
    name: Option[String] = None,
    roles: Optional[List[Role]] = Optional.missing,
    user: Optional[User] = Optional.missing,
    requireColons: Optional[Boolean] = Optional.missing,
    isManaged: Optional[Boolean] = Optional.missing,
    isAnimated: Optional[Boolean] = Optional.missing,
    isAvailable: Optional[Boolean] = Optional.missing,
)

object Emoji {
    export dev.axyria.scalacord.common.entity.emojiDecoder
    export dev.axyria.scalacord.common.entity.emojiEncoder
}

given emojiEncoder: Encoder[Emoji] with
    final def apply(emoji: Emoji): Json =
        val elems: List[Option[EncodingContext[?]]] =
            ("id", emoji.id).optionContext
                :: ("name", emoji.name).context
                :: ("roles", emoji.roles).optionContext
                :: ("user", emoji.user).optionContext
                :: ("require_colons", emoji.requireColons).optionContext
                :: ("managed", emoji.isManaged).optionContext
                :: ("animated", emoji.isAnimated).optionContext
                :: ("available", emoji.isAvailable).optionContext
                :: Nil
        elems.withOptional

given emojiDecoder: Decoder[Emoji] with
    final def apply(cursor: HCursor): Decoder.Result[Emoji] =
        for {
            id            <- cursor.get[Optional[Snowflake]]("id")
            name          <- cursor.get[Option[String]]("name")
            roles         <- cursor.get[Optional[List[Role]]]("roles")
            user          <- cursor.get[Optional[User]]("user")
            requireColons <- cursor.get[Optional[Boolean]]("require_colons")
            isManaged     <- cursor.get[Optional[Boolean]]("managed")
            isAnimated    <- cursor.get[Optional[Boolean]]("animated")
            isAvailable   <- cursor.get[Optional[Boolean]]("available")
        } yield Emoji(id, name, roles, user, requireColons, isManaged, isAnimated, isAvailable)
