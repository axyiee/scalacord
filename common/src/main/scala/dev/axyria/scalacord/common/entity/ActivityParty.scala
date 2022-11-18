package dev.axyria.scalacord.common.entity

import dev.axyria.scalacord.common.datatype.*
import dev.axyria.scalacord.common.util.*
import io.circe.{Decoder, Encoder, HCursor, Json}

/** The party an [[Activity]] may belong to.
  * @param id
  *   The id of the party.
  * @param size
  *   The current size of the party.
  * @param maxSize
  *   The maximum size of the party.
  */
case class ActivityParty(
    id: Optional[String] = Optional.missing,
    size: Optional[Int] = Optional.missing,
    maxSize: Optional[Int] = Optional.missing
)

object ActivityParty {
    export dev.axyria.scalacord.common.entity.activityPartyEncoder
    export dev.axyria.scalacord.common.entity.activityPartyDecoder
}

given activityPartyEncoder: Encoder[ActivityParty] with
    final def apply(party: ActivityParty): Json =
        val elems: List[Option[EncodingContext[?]]] =
            ("id", party.id).context ::
                ("size", List(party.size, party.maxSize)).context ::
                Nil
        elems.withOptional

given activityPartyDecoder: Decoder[ActivityParty] with
    final def apply(c: HCursor): Decoder.Result[ActivityParty] =
        for
            id   <- c.get[Optional[String]]("id")
            size <- c.get[Optional[List[Int]]]("size")
        yield ActivityParty(
            id,
            Optional.keep(size.toOption.flatMap(_.headOption)),
            Optional.keep(size.toOption.flatMap(_.lastOption))
        )
