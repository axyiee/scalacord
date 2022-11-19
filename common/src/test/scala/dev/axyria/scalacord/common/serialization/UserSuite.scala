package dev.axyria.scalacord.common.serialization

import cats.effect.IO
import cats.syntax.all.*
import dev.axyria.scalacord.common.datatype.Keep
import dev.axyria.scalacord.common.entity.User
import dev.axyria.scalacord.common.entity.userDecoder
import dev.axyria.scalacord.common.entity.userEncoder
import io.circe.parser.decode
import io.circe.syntax.*
import munit.CatsEffectSuite

class UserSuite extends CatsEffectSuite {
    test("it can be (de)serialized as snake case with optional fields") {
        IO(
            User(
                username = "Hello",
                email = Keep(Some("hello@github.com")),
                has2FA = Keep(Some(true))
            )
        ).map(user => (user, user.asJson.noSpaces))
            .flatMap((user, json) => (IO.pure(user), IO.fromEither(decode[User](json))).tupled)
            .flatMap { case (a, b) => IO(assertEquals(a, b)) }
    }
}
