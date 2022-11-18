package dev.axyria.scalacord.common.entity

import dev.axyria.scalacord.common.datatype.*
import dev.axyria.scalacord.common.util.*
import io.circe.{Decoder, Encoder, HCursor, Json}

/** The tags a [[Role]] can have.
  * @param botId
  *   The id of the bot this role belongs to.
  * @param integrationId
  *   The id of the integration this role belongs to.
  * @param premiumSubscriberRole
  *   Whether this is the guild's premium subscriber role.
  */
case class RoleTags(
    val botId: Optional[Snowflake] = Optional.missing,
    val integrationId: Optional[Snowflake] = Optional.missing,
    val premiumSubscriber: Optional[Boolean] = Optional.missing
)

object RoleTags {
    export dev.axyria.scalacord.common.entity.roleTagsEncoder
    export dev.axyria.scalacord.common.entity.roleTagsDecoder
}

given roleTagsEncoder: Encoder[RoleTags] with
    final def apply(tags: RoleTags): Json =
        val elems: List[Option[EncodingContext[?]]] =
            ("bot_id", tags.botId).optionContext ::
                ("integration_id", tags.integrationId).optionContext ::
                ("premium_subscriber", tags.premiumSubscriber).optionContext ::
                Nil
        elems.withOptional

given roleTagsDecoder: Decoder[RoleTags] with
    final def apply(c: HCursor): Decoder.Result[RoleTags] =
        for
            botId             <- c.get[Optional[Snowflake]]("bot_id")
            integrationId     <- c.get[Optional[Snowflake]]("integration_id")
            premiumSubscriber <- c.get[Optional[Boolean]]("premium_subscriber")
        yield RoleTags(botId, integrationId, premiumSubscriber)
