package dev.axyria.scalacord.gateway.decoder

import cats.effect.kernel.Ref
import cats.effect.kernel.Async
import cats.kernel.Monoid
import fs2.Stream
import fs2.compression.Compression
import io.circe.Json
import io.circe.parser.parse
import org.http4s.client.websocket.WSDataFrame
import org.http4s.client.websocket.WSFrame

/** A implementation of [[MessageIo]] for possibly compressed streams.
  *
  * @tparam F
  *   A type-class able to suspend side effects into the [[F]] context.
  */
case class PayloadMessageIo[F[_]: Async](compression: Ref[F, Option[Compression[F]]])(using
    monoid: Monoid[MessageIo[F]]
) extends MessageIo[F] {
    def kind: CompressionKind = CompressionKind.Payload

    private val io: MessageIo[F] =
        monoid.combine(PlainMessageIo[F], TransportCompressedMessageIo[F](compression))

    def decode(input: Stream[F, WSDataFrame]): Stream[F, Json] =
        io.decode(input)

    def encode(input: Stream[F, Json]): Stream[F, WSDataFrame] =
        io.encode(input)

    def setup: Stream[F, Unit] = io.setup
}
