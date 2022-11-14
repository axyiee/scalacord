package lol.syntax.scalacord.common.entity

import io.circe.Decoder.Result
import io.circe.{Decoder, Encoder, HCursor, Json}
import lol.syntax.scalacord.common.datatype.*
import lol.syntax.scalacord.common.util.insert

case class Profile(
    bannerHash: Optional[String] = Optional.missing,
    accentColor: Optional[Color] = Optional.missing,
    subscription: Optional[NitroSubscription] = Optional.missing,
    publicFlags: Optional[List[UserFlag]] = Optional.missing,
    flags: Optional[List[UserFlag]] = Optional.missing,
)

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

given userEncoder: Encoder[User] with
    override def apply(user: User): Json =
        val values = List.newBuilder[(String, Json)]
        insert[Snowflake](values, "id", user.id)
        insert(values, "username", user.username)
        insert(values, "discriminator", user.discriminator)
        insert(values, "avatar", user.avatarHash)
        insert(values, "mfa_enabled", user.has2FA)
        insert(values, "bot", user.isBot)
        insert(values, "system", user.isSystem)
        insert(values, "banner", user.profile.bannerHash)
        insert(values, "accent_color", user.profile.accentColor)
        insert(values, "locale", user.locale)
        insert(values, "verified", user.isVerified)
        insert(values, "email", user.email)
        insert(values, "flags", user.profile.flags)
        insert(values, "premium_type", user.profile.subscription)
        insert(values, "public_flags", user.profile.publicFlags)
        Json.obj(values.result()*)

given userDecoder: Decoder[User] with
    final def apply(cursor: HCursor): Result[User] =
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
