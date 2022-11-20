package dev.axyria.scalacord.gateway.decoder

import cats.Monad
import cats.effect.kernel.Async
import cats.effect.kernel.Deferred
import cats.effect.kernel.Ref
import cats.effect.kernel.Sync
import cats.implicits.*
import fs2.Chunk
import fs2.Stream
import fs2.compression.Compression
import fs2.compression.InflateParams
import fs2.io.compression.*
import fs2.text
import io.circe.Json
import io.circe.parser.parse
import io.circe.syntax.EncoderOps
import org.http4s.client.websocket.WSDataFrame
import org.http4s.client.websocket.WSFrame

/** A implementation of [[MessageIo]] for transport-compressed streams.
  *
  * @tparam F
  *   A type-class able to suspend side effects into the [[F]] context.
  */
case class TransportCompressedMessageIo[F[_]: Async](
    compress: Ref[F, Option[Compression[F]]],
    params: InflateParams = InflateParams()
) extends MessageIo[F] {
    def setup: Stream[F, Unit] = Stream
        .eval(Sync[F].delay(Compression[F]))
        .evalMap(cmp => compress.set(Some(cmp)))

    def kind: CompressionKind = CompressionKind.Transport

    def decode(input: Stream[F, WSDataFrame]): Stream[F, Json] =
        input.flatMap {
            case WSFrame.Binary(data, _) =>
                Stream
                    .eval(compress.get)
                    .flattenOption
                    .flatMap(_.inflate(params)(Stream.chunk(Chunk.array(data.toArray))))
                    .through(text.utf8.decode)
                    .flatMap { input => Stream.fromEither(parse(input)) }
            // case WSFrame.Close(code, reason) =>
            //    Stream.emit(GatewayPayload(-1, Close(code, reason).asJson).asJson)
            case _ => Stream.empty
        }

    def encode(input: Stream[F, Json]): Stream[F, WSDataFrame] =
        input.map(json => WSFrame.Text(json.noSpaces))
}
