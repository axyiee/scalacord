package dev.axyria.scalacord.gateway

import cats.effect.IO
import cats.implicits.*
import munit.CatsEffectSuite
import scala.concurrent.duration.Duration
import scala.concurrent.duration.DurationInt

class GatewaySuite extends CatsEffectSuite {
    override def munitIOTimeout: Duration = 10.minutes

    test("it connects") {
        IO(Option(System.getenv("TEST_TOKEN")))
            .flatMap {
                case Some(token) =>
                    Gateway[IO](s"Bot $token")
                        .flatTap(_.connect())
                        .compile
                        .drain
                case None => IO.println("Token not provided. Skipping...")
            }
    }
}
