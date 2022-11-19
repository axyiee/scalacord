package dev.axyria.scalacord.gateway.decoder

import cats.Monad
import cats.effect.kernel.Deferred
import cats.effect.kernel.Ref
import cats.effect.kernel.Sync
import cats.implicits.*
import fs2.Stream
import io.circe.Json
import io.circe.parser.parse
import io.circe.syntax.EncoderOps
import java.io.ByteArrayOutputStream
import java.util.zip.Inflater
import java.util.zip.InflaterOutputStream
import org.http4s.client.websocket.WSDataFrame
import org.http4s.client.websocket.WSFrame

/** A implementation of [[MessageIo]] for transport-compressed streams.
  *
  * @tparam F
  *   A type-class able to suspend side effects into the [[F]] context.
  */
case class TransportCompressedMessageIo[F[_]: Sync](
    inflater: Ref[F, Option[Inflater]]
) extends MessageIo[F] {
    def setup: Stream[F, Unit] = Stream.eval(inflater.set(Some(new Inflater())))

    def kind: CompressionKind = CompressionKind.Transport

    def decode(input: Stream[F, WSDataFrame]): Stream[F, Json] =
        input.flatMap {
            case WSFrame.Binary(data, _) =>
                Stream
                    .bracket(Sync[F].delay(ByteArrayOutputStream())) { out =>
                        Sync[F].delay(out.close())
                    }
                    .product(
                        Stream.eval(inflater.get).flatMap {
                            case Some(inf) => Stream.emit(inf)
                            case None =>
                                Stream.raiseError(new Exception("Inflater not initialized"))
                        }
                    )
                    .flatMap { case (out, infl) =>
                        Stream
                            .bracket(Sync[F].delay(new InflaterOutputStream(out, infl))) { out2 =>
                                Sync[F].delay(out2.close())
                            }
                            .tupleLeft(out)
                    }
                    .flatTap { case (out, out2) =>
                        Stream.eval(Sync[F].delay(out2.write(data.toArray)))
                    }
                    .map { case (out, out2) => String(out.toByteArray, 0, out.size(), "UTF-8") }
                    .flatMap { input => Stream.fromEither(parse(input)) }
            // case WSFrame.Close(code, reason) =>
            //    Stream.emit(GatewayPayload(-1, Close(code, reason).asJson).asJson)
            case _ => Stream.empty
        }

    def encode(input: Stream[F, Json]): Stream[F, WSDataFrame] =
        input.map(json => WSFrame.Text(json.noSpaces))
}
