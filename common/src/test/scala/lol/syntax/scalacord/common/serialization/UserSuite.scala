import munit.CatsEffectSuite
import cats.effect.IO
import cats.syntax.all.*
import lol.syntax.scalacord.common.entity.{User, userEncoder, userDecoder}
import lol.syntax.scalacord.common.datatype.Keep
import io.circe.syntax._
import io.circe.parser.decode

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
