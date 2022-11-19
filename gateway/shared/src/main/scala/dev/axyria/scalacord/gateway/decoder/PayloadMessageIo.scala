package dev.axyria.scalacord.gateway.decoder

import cats.effect.kernel.Ref
import cats.effect.kernel.Spawn
import cats.effect.kernel.Sync
import cats.kernel.Monoid
import fs2.Stream
import io.circe.Json
import io.circe.parser.parse
import java.util.zip.Inflater
import org.http4s.client.websocket.WSDataFrame
import org.http4s.client.websocket.WSFrame

/** A implementation of [[MessageIo]] for possibly compressed streams.
  *
  * @tparam F
  *   A type-class able to suspend side effects into the [[F]] context.
  */
case class PayloadMessageIo[F[_]: Sync: Spawn](inflater: Ref[F, Option[Inflater]])(using
    monoid: Monoid[MessageIo[F]]
) extends MessageIo[F] {
    def kind: CompressionKind = CompressionKind.Payload

    private val io: MessageIo[F] =
        monoid.combine(PlainMessageIo[F], TransportCompressedMessageIo[F](inflater))

    def decode(input: Stream[F, WSDataFrame]): Stream[F, Json] =
        io.decode(input)

    def encode(input: Stream[F, Json]): Stream[F, WSDataFrame] =
        io.encode(input)

    def setup: Stream[F, Unit] = io.setup
}
