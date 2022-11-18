package dev.axyria.scalacord.common.entity

import dev.axyria.scalacord.common.datatype.*
import io.circe.Decoder.Result
import io.circe.{Decoder, Encoder, HCursor, Json}

/** All permissions an [[User]] or [[Role]] can have on a Discord server. It is encoded and decoded
  * as a bitset using a custom codec. ca
  */
enum Permission(val value: Int) extends BitSet[Permission] {
    case CreateInstantInvite     extends Permission(1 << 0)
    case KickMembers             extends Permission(1 << 1)
    case BanMembers              extends Permission(1 << 2)
    case Administrator           extends Permission(1 << 3)
    case ManageChannels          extends Permission(1 << 4)
    case ManageGuild             extends Permission(1 << 5)
    case AddReactions            extends Permission(1 << 6)
    case ViewAuditLog            extends Permission(1 << 7)
    case PrioritySpeaker         extends Permission(1 << 8)
    case Stream                  extends Permission(1 << 9)
    case ViewChannel             extends Permission(1 << 10)
    case SendMessages            extends Permission(1 << 11)
    case SendTTSMessages         extends Permission(1 << 12)
    case ManageMessages          extends Permission(1 << 13)
    case EmbedLinks              extends Permission(1 << 14)
    case AttachFiles             extends Permission(1 << 15)
    case ReadMessageHistory      extends Permission(1 << 16)
    case MentionEveryone         extends Permission(1 << 17)
    case UseExternalEmojis       extends Permission(1 << 18)
    case ViewGuildInsights       extends Permission(1 << 19)
    case Connect                 extends Permission(1 << 20)
    case Speak                   extends Permission(1 << 21)
    case MuteMembers             extends Permission(1 << 22)
    case DeafenMembers           extends Permission(1 << 23)
    case MoveMembers             extends Permission(1 << 24)
    case UseVAD                  extends Permission(1 << 25)
    case ChangeNickname          extends Permission(1 << 26)
    case ManageNicknames         extends Permission(1 << 27)
    case ManageRoles             extends Permission(1 << 28)
    case ManageWebhooks          extends Permission(1 << 29)
    case ManageEmojisAndStickers extends Permission(1 << 30)
    case UseApplicationCommands  extends Permission(1 << 31)
    case RequestToSpeak          extends Permission(1 << 32)
    case ManageEvents            extends Permission(1 << 33)
    case ManageThreads           extends Permission(1 << 34)
    case CreatePublicThreads     extends Permission(1 << 35)
    case CreatePrivateThreads    extends Permission(1 << 36)
    case UseExternalStickers     extends Permission(1 << 37)
    case SendMessagesInThreads   extends Permission(1 << 38)
    case UseEmbeddedActivities   extends Permission(1 << 39)
    case ModerateMembers         extends Permission(1 << 40)
}

object Permission {
    given all: AllBitSet[Permission] with
        final def values: Array[Permission] = Permission.values
}
