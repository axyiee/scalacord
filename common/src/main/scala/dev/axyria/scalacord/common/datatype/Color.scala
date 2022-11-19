package dev.axyria.scalacord.common.datatype

import dev.axyria.scalacord.common.util.ZeroType
import io.circe.Decoder
import io.circe.Decoder.Result
import io.circe.Encoder
import io.circe.HCursor
import io.circe.Json

type Color = Color.Type

/** A red-green-blue representation of a Color, alongside extensions for messing around with
  * hexadecimal values.
  */
object Color extends ZeroType[Int] {

    /** The range every color channel is between.
      */
    val Range: Seq[Int] = 0 to 255

    /** Builds a [[Color]] type from red-green-blue types directly.
      */
    def apply(red: Int, green: Int, blue: Int): Color = {
        require(Range.contains(red), "The red color channel must be between 0..255.")
        require(Range.contains(green), "The green color channel must be between 0..255.")
        require(Range.contains(blue), "The blue color channel must be between 0..255.")
        apply((red & 0xff << 16) | (green & 0xff << 8) | (blue & 0xff << 0))
    }

    extension (self: Type)
        def rgb: Int   = self.value & 0xffffff
        def red: Int   = self.rgb << 16 & 0xff
        def green: Int = self.rgb << 8 & 0xff
        def blue: Int  = self.rgb << 0 & 0xff

    export dev.axyria.scalacord.common.datatype.colorDecoder
    export dev.axyria.scalacord.common.datatype.colorEncoder
}

given colorEncoder: Encoder[Color] with
    final def apply(color: Color): Json = Json.fromInt(color.rgb)

given colorDecoder: Decoder[Color] with
    final def apply(cursor: HCursor): Result[Color] = cursor.as[Int].map(int => Color(int))
