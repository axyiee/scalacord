package lol.syntax.scalacord.common.datatype

import io.circe.*
import io.circe.generic.semiauto.*
import io.circe.parser.{decode, parse}
import io.circe.syntax.*
import cats.effect.IO
import io.circe.Decoder.Result
import munit.CatsEffectSuite

case class Hello(x: Optional[Int], z: String, c: Optional[Long], d: String, e: Optional[Long])

object Hello {
    given encoder: Encoder[Hello] = deriveEncoder[Hello].mapJson(_.skipMissing.innerOptional)

    given decoder: Decoder[Hello] = deriveDecoder
}

class OptionalSuite extends CatsEffectSuite {
    test("it doesn't encode when Missing is provided") {
        IO(Hello(Missing, "Hello", Keep(Some(252525)), "Hi", Keep(None)))
            .flatMap(hello => IO((hello, hello.asJson)))
            .flatMap((hello, json) => json.as[Hello] match {
                case Left(error) => IO.raiseError(error)
                case Right(value) => IO((hello, value))
            })
            .flatMap((a1, a2) => IO(assertEquals(a1, a2)))
    }
}
