package lol.syntax.scalacord.common.entity

import io.circe.{Decoder, Encoder, HCursor, Json}
import lol.syntax.scalacord.common.datatype.*
import lol.syntax.scalacord.common.util.*

/** Represents an application on Discord. Most of the times, it will be related to a bot.
  * @param id
  *   The id of the app
  * @param name
  *   The name of the app
  * @param icon
  *   The icon hash of the app
  * @param description
  *   The description of the app
  * @param rpcOrigins
  *   An array of rpc origin urls, if rpc is enabled
  * @param isPublic
  *   When false only app owner can join the app's bot to guilds
  * @param requireCodeGrant
  *   When true the app's bot will only join upon completion of the full oauth2 code grant flow
  * @param termsOfServiceUrl
  *   The url of the app's terms of service
  * @param privacyPolicyUrl
  *   The url of the app's privacy policy
  * @param owner
  *   Partial user object containing info on the owner of the application
  * @param verifyKey
  *   The hex encoded key for verification in interactions and the GameSDK's GetTicket
  * @param team
  *   If the application belongs to a team, this will be a list of the members of that team
  * @param guildId
  *   If this application is a game sold on Discord, this field will be the guild to which it has
  *   been linked
  * @param primarySkuId
  *   If this application is a game sold on Discord, this field will be the id of the "Game SKU"
  *   that is created, if exists
  * @param slug
  *   If this application is a game sold on Discord, this field will be the URL slug that links to
  *   the store page
  * @param coverImage
  *   The application's default rich presence invite cover image hash
  * @param flags
  *   The application's public flags
  * @param tags
  *   Up to 5 tags describing the content and functionality of the application
  * @param installParams
  *   Settings for the application's default in-app authorization link, if enabled
  * @param customInstallUrl
  *   The application's default custom authorization link, if enabled
  */
case class Application(
    id: Snowflake = Snowflake.MinValue,
    name: String = "",
    icon: Option[String] = None,
    description: String = "",
    rpcOrigins: Optional[List[String]] = Optional.missing,
    isPublic: Boolean = false,
    requireCodeGrant: Boolean = false,
    termsOfServiceUrl: Optional[String] = Optional.missing,
    privacyPolicyUrl: Optional[String] = Optional.missing,
    owner: Optional[User] = Optional.missing,
    verifyKey: String = "",
    team: Option[Team] = None,
    guildId: Optional[Snowflake] = Optional.missing,
    primarySkuId: Optional[Snowflake] = Optional.missing,
    slug: Optional[String] = Optional.missing,
    coverImage: Optional[String] = Optional.missing,
    flags: Optional[List[ApplicationFlag]] = Optional.missing,
    tags: Optional[List[String]] = Optional.missing,
    installParams: Optional[InstallParams] = Optional.missing,
    customInstallUrl: Optional[String] = Optional.missing,
)

object Application {
    export lol.syntax.scalacord.common.entity.applicationEncoder
    export lol.syntax.scalacord.common.entity.applicationDecoder
}

given applicationEncoder: Encoder[Application] with
    final def apply(application: Application): Json =
        val elems: List[Option[EncodingContext[?]]] =
            ("id", application.id).context
                :: ("name", application.name).context
                :: ("icon", application.icon).context
                :: ("description", application.description).context
                :: ("rpc_origins", application.rpcOrigins).optionContext
                :: ("bot_public", application.isPublic).context
                :: ("bot_require_code_grant", application.requireCodeGrant).context
                :: ("terms_of_service_url", application.termsOfServiceUrl).optionContext
                :: ("privacy_policy_url", application.privacyPolicyUrl).optionContext
                :: ("owner", application.owner).optionContext
                :: ("verify_key", application.verifyKey).context
                :: ("team", application.team).context
                :: ("guild_id", application.guildId).optionContext
                :: ("primary_sku_id", application.primarySkuId).optionContext
                :: ("slug", application.slug).optionContext
                :: ("cover_image", application.coverImage).optionContext
                :: ("flags", application.flags).optionContext
                :: ("tags", application.tags).optionContext
                :: ("install_params", application.installParams).optionContext
                :: ("custom_install_url", application.customInstallUrl).optionContext
                :: Nil
        elems.withOptional

given applicationDecoder: Decoder[Application] with
    final def apply(cursor: HCursor): Decoder.Result[Application] =
        for {
            id                <- cursor.get[Snowflake]("id")
            name              <- cursor.get[String]("name")
            icon              <- cursor.get[Option[String]]("icon")
            description       <- cursor.get[String]("description")
            rpcOrigins        <- cursor.get[Optional[List[String]]]("rpc_origins")
            isPublic          <- cursor.get[Boolean]("bot_public")
            requireCodeGrant  <- cursor.get[Boolean]("bot_require_code_grant")
            termsOfServiceUrl <- cursor.get[Optional[String]]("terms_of_service_url")
            privacyPolicyUrl  <- cursor.get[Optional[String]]("privacy_policy_url")
            owner             <- cursor.get[Optional[User]]("owner")
            verifyKey         <- cursor.get[String]("verify_key")
            team              <- cursor.get[Option[Team]]("team")
            guildId           <- cursor.get[Optional[Snowflake]]("guild_id")
            primarySkuId      <- cursor.get[Optional[Snowflake]]("primary_sku_id")
            slug              <- cursor.get[Optional[String]]("slug")
            coverImage        <- cursor.get[Optional[String]]("cover_image")
            flags             <- cursor.get[Optional[List[ApplicationFlag]]]("flags")
            tags              <- cursor.get[Optional[List[String]]]("tags")
            installParams     <- cursor.get[Optional[InstallParams]]("install_params")
            customInstallUrl  <- cursor.get[Optional[String]]("custom_install_url")
        } yield Application(
            id,
            name,
            icon,
            description,
            rpcOrigins,
            isPublic,
            requireCodeGrant,
            termsOfServiceUrl,
            privacyPolicyUrl,
            owner,
            verifyKey,
            team,
            guildId,
            primarySkuId,
            slug,
            coverImage,
            flags,
            tags,
            installParams,
            customInstallUrl,
        )
