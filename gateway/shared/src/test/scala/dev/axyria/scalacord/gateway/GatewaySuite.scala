package dev.axyria.scalacord.gateway

import munit.CatsEffectSuite
import cats.effect.IO
import cats.implicits.*
import scala.concurrent.duration.{Duration, DurationInt}

class GatewaySuite extends CatsEffectSuite {
    override def munitIOTimeout: Duration = 10.minutes

    test("it connects") {
        IO(Option(System.getenv("TEST_TOKEN")))
            .flatMap { option =>
                option match {
                    case Some(token) =>
                        DiscordGateway[IO](s"Bot $token").flatTap(_.connect()).compile.drain
                    case None => IO.println("Token not provided. Skipping...")
                }
            }
    }
}
