package lol.syntax.scalacord.common.datatype

import cats.Eval
import io.circe.*
import io.circe.Decoder.Result
import io.circe.DecodingFailure.Reason.MissingField

/** A codec-friendly utility type for skipping or keeping values from encoding. This is necessary
  * since some structures cannot be specified as null when sending data back to Discord, only either
  * as sent or not sent at all. A good example of this is the `presence` field in the gateway
  * settings payload.
  *
  * ===Encoding JSON with circe===
  *
  * Our representation of [[Optional]] must be either [[Keep]] or [[Missing]], and when using circe
  * the target encoder needs to be modified to skip and unwrap optional values. For example:
  * {{{
  * scala> given encoder: Encoder[Hello] = deriveEncoder[Hello].mapJson(_.skipMissing.innerOptional)
  * }}}
  */
sealed trait Optional[+A] {
    def toOption: Option[A]
    
    def map[B](action: Option[A] => B): Option[B]
}

/** Optional value that represents a value that must not be encoded at all. */
case object Missing extends Optional[Nothing] {
    override def toOption: None.type = None

    override def map[B](action: Option[Nothing] => B): Option[B] = None
}

/** Optional value that represents a value that must be encoded, even if [[Json.Null]]. */
case class Keep[+A](value: Option[A]) extends Optional[A] {
    override def toOption: value.type = value

    override def map[B](action: Option[A] => B): Option[B] = Some(action(value))
}

object Optional {
    inline def keep[A](value: Option[A]): Optional[A] = Keep(value)

    inline def missing[A]: Optional[A] = Missing.asInstanceOf
    
    export lol.syntax.scalacord.common.datatype.optionalEncoder
    export lol.syntax.scalacord.common.datatype.optionalDecoder
}

extension (json: Json)
    def skipMissing: Json =
        json.mapObject(
            _.filter((_, v) =>
                !v.hcursor.downField("$optional_type").as[String].contains("missing")
            )
        )

    def innerOptional: Json =
        json.mapObject(
            _.mapValues(v =>
                v.hcursor.downField("$optional_value").as[Json] match {
                    case Right(value) => value
                    case _            => v
                }
            )
        )

given optionalEncoder[A: Encoder]: Encoder[Optional[A]] with
    final def apply(optional: Optional[A]): Json =
        optional match {
            case Keep(value) => Json.obj(("$optional_value", Encoder.encodeOption[A].apply(value)))
            case Missing     => Json.obj(("$optional_type", Json.fromString("missing")))
        }

given optionalDecoder[A: Decoder]: Decoder[Optional[A]] with
    final def apply(c: HCursor): Result[Optional[A]] =
        Decoder.decodeOption[A].apply(c) match {
            case Right(value) => Right(Optional.keep(value))
            case Left(error) =>
                error.reason match {
                    case MissingField => Right(Optional.missing[A])
                    case _            => Left(error)
                }
        }

    override def tryDecode(c: ACursor): Result[Optional[A]] = c match {
        case _: FailedCursor => Right(Optional.missing[A])
        case _               => super.tryDecode(c)
    }
