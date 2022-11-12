package lol.syntax.scalacord.common

import cats.effect.{IO, IOApp}

object Root extends IOApp.Simple {
    def run: IO[Unit] =
        IO.println("Hello sbt-typelevel!")
}
