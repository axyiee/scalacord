package dev.axyria.scalacord.gateway.decoder

import cats.Monad
import cats.effect.kernel.{Deferred, Ref, Sync}
import cats.implicits.*
import fs2.Stream
import io.circe.Json
import io.circe.parser.parse
import io.circe.syntax.EncoderOps
import org.http4s.client.websocket.{WSDataFrame, WSFrame}

import java.io.ByteArrayOutputStream
import java.util.zip.{Inflater, InflaterOutputStream}

/** A implementation of [[MessageIo]] for transport-compressed streams.
  *
  * @tparam F
  *   A type-class able to suspend side effects into the [[F]] context.
  */
case class TransportCompressedMessageIo[F[_]: Sync](
    inflater: Ref[F, Option[Inflater]]
) extends MessageIo[F] {
    def setup(): Stream[F, Unit] = Stream.eval(inflater.set(Some(new Inflater())))

    def kind: CompressionKind = CompressionKind.Transport

    def decode(input: Stream[F, WSDataFrame]): Stream[F, Json] =
        input.flatMap {
            case WSFrame.Binary(data, _) =>
                Stream
                    .eval(inflater.get)
                    .flatMap {
                        case Some(inf) => Stream.emit(inf)
                        case None =>
                            Stream.raiseError(
                                new NoSuchElementException("Inflater was not set up.")
                            )
                    }
                    .map(inf => (inf, ByteArrayOutputStream()))
                    .map((inf, byteOutput) =>
                        (inf, byteOutput, InflaterOutputStream(byteOutput, inf))
                    )
                    .flatMap((_, byteOutput, infOutput) =>
                        (
                            Stream.bracket(Sync[F].delay(infOutput))(infOutput =>
                                Sync[F].delay(infOutput.close())
                            ),
                            Stream.bracket(Sync[F].delay(byteOutput))(byteOutput =>
                                Sync[F].delay(byteOutput.close())
                            )
                        ).tupled
                    )
                    .flatMap((infOutput, byteOutput) =>
                        Stream
                            .eval(Sync[F].delay(infOutput.write(data.toArray)))
                            .flatMap(_ => Stream.eval(Sync[F].delay(byteOutput.toByteArray)))
                            .flatMap(bytes => Stream.emit(new String(bytes, "UTF-8")))
                    )
                    .flatMap(string => Stream.fromEither(parse(string)))
            // case WSFrame.Close(code, reason) =>
            //    Stream.emit(GatewayPayload(-1, Close(code, reason).asJson).asJson)
            case _ => Stream.empty
        }

    def encode(input: Stream[F, Json]): Stream[F, WSDataFrame] =
        input.map(json => WSFrame.Text(json.noSpaces))
}
