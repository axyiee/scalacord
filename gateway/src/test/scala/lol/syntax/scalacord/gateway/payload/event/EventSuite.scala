/*
 * Copyright 2022 FromSyntax
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package lol.syntax.scalacord

import munit.CatsEffectSuite
import cats.effect.IO
import cats.syntax.all.*
import lol.syntax.scalacord.gateway.payload.event.*
import lol.syntax.scalacord.common.entity.User
import io.circe.parser.decode
import io.circe.syntax.*

class GatewaySuite extends CatsEffectSuite {
    test("can be (de)serialized") {
        IO(Ready(10, User(username = "ReadyEvent")))
            .map(ready => Event(ReadyCodec, ready, 0))
            .flatMap(event => (IO.pure(event), IO(event.asJson.noSpaces)).tupled)
            .flatMap((event, json) => (IO.pure(event), IO.fromEither(decode[Event](json))).tupled)
            .flatMap { case (a, b) => IO(assertEquals(a.show, b.show)) }
    }
}
