package dev.axyria.scalacord.common.entity

import dev.axyria.scalacord.common.datatype.*
import dev.axyria.scalacord.common.util.*
import io.circe.Decoder
import io.circe.Encoder
import io.circe.generic.semiauto.*

/** When received over the gateway, the buttons field is an array of strings, which are the button
  * labels.
  * @param label
  *   Text shown on the button (1-32 characters)
  * @param url
  *   URL opened when clicking the button (1-512 characters)
  */
case class ActivityButton(label: String, url: String)

object ActivityButton {
    given encoder: Encoder[ActivityButton] = deriveEncoder
    given decoder: Decoder[ActivityButton] = deriveDecoder
}
