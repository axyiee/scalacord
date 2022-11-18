package dev.axyria.scalacord.common.entity

import dev.axyria.scalacord.common.datatype.*
import dev.axyria.scalacord.common.util.*
import io.circe.{Decoder, Encoder, HCursor, Json}

/** The ecrets for an [[Activity]].
  * @param join
  *   The secret for joining a party.
  * @param spectate
  *   The secret for spectating a game.
  * @param matchSecret
  *   The secret for a specific instanced match.
  */
case class ActivitySecrets(
    join: Optional[String] = Optional.missing,
    spectate: Optional[String] = Optional.missing,
    matchSecret: Optional[String] = Optional.missing
)

object ActivitySecrets {
    export dev.axyria.scalacord.common.entity.activitySecretsEncoder
    export dev.axyria.scalacord.common.entity.activitySecretsDecoder
}

given activitySecretsEncoder: Encoder[ActivitySecrets] with
    final def apply(secrets: ActivitySecrets): Json =
        val elems: List[Option[EncodingContext[?]]] =
            ("join", secrets.join).optionContext ::
                ("spectate", secrets.spectate).optionContext ::
                ("match", secrets.matchSecret).optionContext ::
                Nil
        elems.withOptional

given activitySecretsDecoder: Decoder[ActivitySecrets] with
    final def apply(cursor: HCursor): Decoder.Result[ActivitySecrets] =
        for
            join     <- cursor.get[Optional[String]]("join")
            spectate <- cursor.get[Optional[String]]("spectate")
            match_   <- cursor.get[Optional[String]]("match")
        yield ActivitySecrets(join, spectate, match_)
