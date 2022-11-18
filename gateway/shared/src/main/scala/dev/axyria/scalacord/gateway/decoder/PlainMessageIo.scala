package dev.axyria.scalacord.gateway.decoder

import cats.effect.Spawn
import fs2.Stream
import io.circe.Json
import io.circe.parser.parse
import io.circe.syntax.EncoderOps
import org.http4s.client.websocket.{WSDataFrame, WSFrame}

/** A implementation of [[MessageIo]] for non-compressed streams.
  * @tparam F
  *   A type-class able to suspend side effects into the [[F]] context.
  */
class PlainMessageIo[F[_]: Spawn] extends MessageIo[F] {
    def kind: CompressionKind = CompressionKind.None

    def decode(input: Stream[F, WSDataFrame]): Stream[F, Json] =
        input.flatMap {
            case WSFrame.Text(data, _) => Stream.fromEither(parse(data))
            // case WSFrame.Close(code, reason) =>
            //    Stream.emit(GatewayPayload(-1, Close(code, reason).asJson).asJson)
            case _ => Stream.empty
        }

    def encode(input: Stream[F, Json]): Stream[F, WSDataFrame] =
        input.map(json => WSFrame.Text(json.noSpaces))

    def setup(): Stream[F, Unit] = Stream.empty
}
