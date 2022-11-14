package lol.syntax.scalacord.gateway.payload

import io.circe.{Decoder, Encoder}
import lol.syntax.scalacord.gateway.payload.event.definition.{Event, EventCodec}
import lol.syntax.scalacord.gateway.payload.event.{Event, EventCodec}
import lol.syntax.scalacord.gateway.payload.{PayloadCodec, PayloadData}

/** Trait used to distinguish from normal case classes and payload data. This is intended to be used
  * as parameter on [[PayloadCodec]].
  */
trait PayloadData

/** The definition of a payload codec. A payload codec is a type designed for expressing the
  * [[opCode]] (identifier for different commands) and its circe-based encoder and decoder for JSON
  * data.
  * @tparam A
  *   The kind of [[PayloadData]] this codec refers to.
  */
trait PayloadCodec[A <: PayloadData] {
    def opCode: Int
    def encoder: Encoder[A]
    def decoder: Decoder[A]
}
