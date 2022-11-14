package lol.syntax.scalacord.common.datatype

import munit.CatsEffectSuite
import java.time.Instant
import cats.effect.IO
import cats.syntax.all.*

import scala.concurrent.duration.DurationInt
import math.Ordered.orderingToOrdered
import io.circe.syntax._
import io.circe.parser.decode

class SnowflakeSuite extends CatsEffectSuite {
    test("it is comparable") {
        (
            IO(Instant.now()).flatMap(now => IO.fromEither(Snowflake(now))),
            IO.sleep(1.second) *> IO(Instant.now()).flatMap(now => IO.fromEither(Snowflake(now)))
        ).tupled.flatMap { case (a, b) => IO(assert(a < b)) }
    }
    test("it is (de)serializable") {
        IO.fromEither(Snowflake(Instant.now()))
            .map(snowflake => (snowflake, snowflake.asJson.noSpaces))
            .flatMap((snowflake, json) =>
                (IO.pure(snowflake), IO.fromEither(decode[Snowflake](json))).tupled
            )
            .flatMap { case (a, b) => IO(assertEquals(a, b)) }
    }
}
