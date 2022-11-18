package dev.axyria.scalacord.core

import cats.effect.IO
import cats.effect.IOApp

object Main extends IOApp.Simple {

    def run: IO[Unit] =
        IO.println("Hello sbt-typelevel!")
}
