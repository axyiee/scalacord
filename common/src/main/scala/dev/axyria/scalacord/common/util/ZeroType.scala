package dev.axyria.scalacord.common.util

import io.circe.Decoder
import io.circe.Encoder
import io.circe.generic.semiauto.*

trait ZeroType[Source] {
    opaque type Type = Source

    extension (self: Type) inline def value: Source = self

    def apply(value: Source): Type = value

    final def unapply[A](a: A)(using convert: A =:= Type): Some[Source] =
        Some(convert(a).value)
}
