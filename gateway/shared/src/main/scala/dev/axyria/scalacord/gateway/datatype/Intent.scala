package dev.axyria.scalacord.gateway.datatype

import dev.axyria.scalacord.common.datatype.*
import io.circe.Decoder.Result
import io.circe.{Decoder, Encoder, HCursor, Json}

/** Maintaining a stateful application can be difficult when it comes to the amount of data your app
  * is expected to process over a Gateway connection, especially at scale. Gateway intents are a
  * system to help you lower the computational burden. Intents are bitwise values passed in the
  * `intents` parameter when Identifying which correlate to a set of related events. For example,
  * the event sent when a guild is created (`GUILD_CREATE`) and when a channel is updated
  * (`CHANNEL_UPDATE`) both require the same `GUILDS (1 << 0)` intent (as listed in the table
  * below). If you do not specify an intent when identifying, you will not receive any of the
  * Gateway events associated with that intent.
  *
  * Two types of intents exist:
  *   - **Standard intents** can be passed by default. You don't need any additional permissions or
  *     configurations.
  *   - **Privileged intents** require you to toggle the intent for your app in your app's settings
  *     within the Developer Portal before passing said intent. For verified apps (required for apps
  *     in 100+ guilds), the intent must also be approved after the verification process to use the
  *     intent. More information about privileged intents can be found in the section below.
  */
enum Intent(val value: Int, val isPrivileged: Boolean = false) extends BitSet[Intent] {
    case Guilds                      extends Intent(1 << 0)
    case GuildMembers                extends Intent(1 << 1, true)
    case GuildBans                   extends Intent(1 << 2)
    case GuildEmojisAndStickers      extends Intent(1 << 3)
    case GuildIntegrations           extends Intent(1 << 4)
    case GuildWebhooks               extends Intent(1 << 5)
    case GuildInvites                extends Intent(1 << 6)
    case GuildVoiceStates            extends Intent(1 << 7)
    case GuildPresences              extends Intent(1 << 8, true)
    case GuildMessages               extends Intent(1 << 9)
    case GuildMessageReactions       extends Intent(1 << 10)
    case GuildMessageTyping          extends Intent(1 << 11)
    case DirectMessages              extends Intent(1 << 12)
    case DirectMessageReactions      extends Intent(1 << 13)
    case DirectMessageTyping         extends Intent(1 << 14)
    case MessageContent              extends Intent(1 << 15, true)
    case GuildScheduledEvents        extends Intent(1 << 16)
    case AutoModerationConfiguration extends Intent(1 << 17)
    case AutoModerationExecution     extends Intent(1 << 18)
}

object Intent {
    given all: AllBitSet[Intent] with
        override def values: Array[Intent] = Intent.values
}
