package dev.axyria.scalacord.common.entity

import dev.axyria.scalacord.common.datatype.*
import dev.axyria.scalacord.common.util.*
import io.circe.{Decoder, Encoder, HCursor, Json}

/** An activity of a user, which is displayed in the user's presence. Bots are only able to send
  * [[name]], [[type]], and optionally [[url]].
  *
  * @param name
  *   The name of the activity.
  * @param type
  *   The type of the activity.
  * @param url
  *   Stream URL, is validated when type is 1
  * @param createdAt
  *   Unix time (in milliseconds) of when the activity was added to the user's session
  * @param timestamps
  *   Timestamps for the start and/or end of the game
  * @param applicationId
  *   The application id for the game
  * @param details
  *   What the player is currently doing
  * @param state
  *   The user's current party status
  * @param emoji
  *   The emoji used for a custom status
  * @param party
  *   The user's current party status
  * @param assets
  *   Images for the presence and their hover texts
  * @param secrets
  *   Secrets for Rich Presence joining and spectating
  * @param isInstance
  *   Whether or not the activity is an instanced game session
  * @param flags
  *   Activity flags ORd together, describes what the payload includes
  * @param buttons
  *   The buttons shown in the Rich Presence (max 2)
  */
case class Activity(
    name: String,
    `type`: ActivityType,
    url: Optional[String] = Optional.missing,
    createdAt: Long = Snowflake.Epoch.toLong,
    timestamps: Optional[List[ActivityTimestamps]] = Optional.missing,
    applicationId: Optional[Snowflake] = Optional.missing,
    details: Optional[String] = Optional.missing,
    state: Optional[String] = Optional.missing,
    emoji: Optional[Emoji] = Optional.missing,
    party: Optional[ActivityParty] = Optional.missing,
    assets: Optional[ActivityAssets] = Optional.missing,
    secrets: Optional[ActivitySecrets] = Optional.missing,
    isInstance: Optional[Boolean] = Optional.missing,
    flags: Optional[List[ActivityFlag]] = Optional.missing,
    buttons: Optional[List[ActivityButton]] = Optional.missing,
)

object Activity {
    export dev.axyria.scalacord.common.entity.activityEncoder
    export dev.axyria.scalacord.common.entity.activityDecoder
}

given activityEncoder: Encoder[Activity] with
    final def apply(activity: Activity): Json =
        val elems: List[Option[EncodingContext[?]]] =
            ("name", activity.name).context ::
                ("type", activity.`type`).context ::
                ("url", activity.url).optionContext ::
                ("created_at", activity.createdAt).context ::
                ("timestamps", activity.timestamps).optionContext ::
                ("application_id", activity.applicationId).optionContext ::
                ("details", activity.details).optionContext ::
                ("state", activity.state).optionContext ::
                ("emoji", activity.emoji).optionContext ::
                ("party", activity.party).optionContext ::
                ("assets", activity.assets).optionContext ::
                ("secrets", activity.secrets).optionContext ::
                ("instance", activity.isInstance).optionContext ::
                ("flags", activity.flags).optionContext ::
                ("buttons", activity.buttons).optionContext ::
                Nil
        elems.withOptional

given activityDecoder: Decoder[Activity] with
    final def apply(cursor: HCursor): Decoder.Result[Activity] =
        for
            name          <- cursor.get[String]("name")
            `type`        <- cursor.get[ActivityType]("type")
            url           <- cursor.get[Optional[String]]("url")
            createdAt     <- cursor.get[Long]("created_at")
            timestamps    <- cursor.get[Optional[List[ActivityTimestamps]]]("timestamps")
            applicationId <- cursor.get[Optional[Snowflake]]("application_id")
            details       <- cursor.get[Optional[String]]("details")
            state         <- cursor.get[Optional[String]]("state")
            emoji         <- cursor.get[Optional[Emoji]]("emoji")
            party         <- cursor.get[Optional[ActivityParty]]("party")
            assets        <- cursor.get[Optional[ActivityAssets]]("assets")
            secrets       <- cursor.get[Optional[ActivitySecrets]]("secrets")
            isInstance    <- cursor.get[Optional[Boolean]]("instance")
            flags         <- cursor.get[Optional[List[ActivityFlag]]]("flags")
            buttons       <- cursor.get[Optional[List[ActivityButton]]]("buttons")
        yield Activity(
            name,
            `type`,
            url,
            createdAt,
            timestamps,
            applicationId,
            details,
            state,
            emoji,
            party,
            assets,
            secrets,
            isInstance,
            flags,
            buttons
        )
