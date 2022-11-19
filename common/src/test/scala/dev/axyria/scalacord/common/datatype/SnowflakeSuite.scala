package dev.axyria.scalacord.common.datatype

import cats.effect.IO
import cats.syntax.all.*
import io.circe.parser.decode
import io.circe.syntax._
import math.Ordered.orderingToOrdered
import munit.CatsEffectSuite
import scala.concurrent.duration.DurationInt
import scala.concurrent.duration.DurationLong

class SnowflakeSuite extends CatsEffectSuite {
    test("it is comparable") {
        (
            IO.realTime.flatMap(now => IO.fromEither(Snowflake(now))),
            IO.sleep(1.second) *> IO.realTime.flatMap(now => IO.fromEither(Snowflake(now)))
        ).tupled.flatMap { case (a, b) => IO(assert(a < b)) }
    }
    test("it is (de)serializable") {
        IO.realTime
            .flatMap(duration => IO.fromEither(Snowflake(duration)))
            .map(snowflake => (snowflake, snowflake.asJson.noSpaces))
            .flatMap((snowflake, json) =>
                (IO.pure(snowflake), IO.fromEither(decode[Snowflake](json))).tupled
            )
            .flatMap { case (a, b) => IO(assertEquals(a, b)) }
    }
    test("its timestamp can be extracted") {
        IO.realTime
            .flatMap(now => (IO.fromEither(Snowflake(now)), IO.pure(now)).tupled)
            .flatMap { case (snowflake, instant) => IO(assert(snowflake matches instant)) }
    }
    test("minimum value matches discord's epoch") {
        IO(Snowflake.MinValue)
            .flatMap(min => IO(assert(min matches Snowflake.Epoch.toLong.milliseconds)))
    }
}
