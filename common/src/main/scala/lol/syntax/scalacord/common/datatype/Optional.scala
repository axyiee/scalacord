package lol.syntax.scalacord.common.datatype

import cats.Eval
import io.circe.*
import io.circe.Decoder.Result
import io.circe.DecodingFailure.Reason.MissingField

sealed trait Optional[+A] {
    def toOption: Option[A]
}

case object Missing extends Optional[Nothing] {
    override def toOption: None.type = None
}

case class Keep[+A](value: Option[A]) extends Optional[A] {
    override def toOption: value.type = value
}

object Optional {
    inline def keep[A](value: Option[A]): Optional[A] = Keep(value)

    inline def missing[A]: Optional[A] = Missing.asInstanceOf
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
        case _ => super.tryDecode(c)
    }
