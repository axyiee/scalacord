package dev.axyria.scalacord.gateway.util

import io.circe.DecodingFailure

extension [F[_]: fs2.RaiseThrowable, A](stream: fs2.Stream[F, A])
    def skipUselessErrors: fs2.Stream[F, A] =
        stream
            .handleErrorWith {
                case e: DecodingFailure =>
                    e match {
                        case DecodingFailure(msg, _) =>
                            if msg.contains("Codec not found for [") then fs2.Stream.empty
                            else fs2.Stream.raiseError(e)
                        case _ => fs2.Stream.raiseError(e)
                    }
                case e => fs2.Stream.raiseError(e)
            }
