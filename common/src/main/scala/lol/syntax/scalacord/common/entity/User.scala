package lol.syntax.scalacord.common.entity

import io.circe.{Decoder, Encoder, HCursor, Json}
import lol.syntax.scalacord.common.datatype.*
import lol.syntax.scalacord.common.util.*

/** Reference about an user profile.
  * @param bannerHash
  *   The user's banner hash
  * @param accentColor
  *   The user's banner color encoded as an integer representation of hexadecimal color code
  * @param subscription
  *   The type of Nitro subscription on a user's account
  * @param publicFlags
  *   The public flags on an user's account
  * @param flags
  *   The flags on an user's account
  */
case class Profile(
    bannerHash: Optional[String] = Optional.missing,
    accentColor: Optional[Color] = Optional.missing,
    subscription: Optional[NitroSubscription] = Optional.missing,
    publicFlags: Optional[List[UserFlag]] = Optional.missing,
    flags: Optional[List[UserFlag]] = Optional.missing,
)

/** Reference about an user on Discord.
  *
  * @param id
  *   The user's id
  * @param username
  *   The user's username, not unique across the platform
  * @param discriminator
  *   The user's 4-digit discord-tag
  * @param avatarHash
  *   The user's avatar hash
  * @param isBot
  *   Whether the user belongs to an OAuth2 application
  * @param isSystem
  *   Whether the user is an Official Discord System user (part of the urgent message system)
  * @param isVerified
  *   Whether the email on this account has been verified
  * @param has2FA
  *   Whether the user has two factor enabled on their account
  * @param email
  *   The user's email
  * @param locale
  *   The user's chosen language option
  * @param profile
  *   The user's profile
  */
case class User(
    id: Snowflake = Snowflake.MinValue,
    username: String = "",
    discriminator: String = "0000",
    avatarHash: Option[String] = None,
    isBot: Optional[Boolean] = Optional.missing,
    isSystem: Optional[Boolean] = Optional.missing,
    isVerified: Optional[Boolean] = Optional.missing,
    has2FA: Optional[Boolean] = Optional.missing,
    email: Optional[String] = Optional.missing,
    locale: Optional[String] = Optional.missing,
    profile: Profile = Profile()
)

object User {
    export lol.syntax.scalacord.common.entity.userEncoder
    export lol.syntax.scalacord.common.entity.userDecoder
}

given userEncoder: Encoder[User] with
    override def apply(user: User): Json =
        val x: List[Option[HasEncoderContext[?]]] =
            println(("id", user.id).context)
            (("id", user.id).context)
                :: (("username", user.username).context)
                :: (("discriminator", user.discriminator).context)
                :: (("avatar", user.avatarHash).context)
                :: (("mfa_enabled", user.has2FA).optionContext)
                :: (("bot", user.isBot).optionContext)
                :: (("system", user.isSystem).optionContext)
                :: (("banner", user.profile.bannerHash).optionContext)
                :: (("accent_color", user.profile.accentColor).optionContext)
                :: (("locale", user.locale).optionContext)
                :: (("verified", user.isVerified).optionContext)
                :: (("email", user.email).optionContext)
                :: (("flags", user.profile.flags).optionContext)
                :: (("premium_type", user.profile.subscription).optionContext)
                :: (("public_flags", user.profile.publicFlags).optionContext)
                :: Nil
        x.withOptional

given userDecoder: Decoder[User] with
    final def apply(cursor: HCursor): Decoder.Result[User] =
        for {
            id            <- cursor.get[Snowflake]("id")
            username      <- cursor.get[String]("username")
            discriminator <- cursor.get[String]("discriminator")
            avatarHash    <- cursor.get[Option[String]]("avatar")
            isBot         <- cursor.get[Optional[Boolean]]("bot")
            isSystem      <- cursor.get[Optional[Boolean]]("system")
            isVerified    <- cursor.get[Optional[Boolean]]("verified")
            has2FA        <- cursor.get[Optional[Boolean]]("mfa_enabled")
            email         <- cursor.get[Optional[String]]("email")
            locale        <- cursor.get[Optional[String]]("locale")
            bannerHash    <- cursor.get[Optional[String]]("banner")
            accentColor   <- cursor.get[Optional[Color]]("accent_color")
            subscription  <- cursor.get[Optional[NitroSubscription]]("premium_type")
            flags         <- cursor.get[Optional[List[UserFlag]]]("flags")
            publicFlags   <- cursor.get[Optional[List[UserFlag]]]("public_flags")
        } yield {
            val profile = Profile(bannerHash, accentColor, subscription, publicFlags, flags)
            User(
                id,
                username,
                discriminator,
                avatarHash,
                isBot,
                isSystem,
                isVerified,
                has2FA,
                email,
                locale,
                profile
            )
        }
