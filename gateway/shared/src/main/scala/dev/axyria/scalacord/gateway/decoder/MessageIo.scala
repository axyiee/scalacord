package dev.axyria.scalacord.gateway.decoder

import cats.effect.kernel.Concurrent
import cats.kernel.Monoid
import fs2.Stream
import io.circe.Json
import org.http4s.Uri
import org.http4s.client.websocket.WSDataFrame

/** A type for decoding/encoding byte array into JSON data. The reason why this kind of function is
  * used to manage message frames into JSON objects rather than just hard-coding string-based
  * decoding, is because stuff can be decompressed as well, so we would like to let the decision up
  * to the user.
  * @tparam F
  *   A type-class able to suspend side effects into the [[F]] context.
  */
trait MessageIo[F[_]] {

    /** The kind of compression applied to the gateway connection or payloads. */
    def kind: CompressionKind

    /** Decodes a binary or text packet into Json data. */
    def decode(input: Stream[F, WSDataFrame]): Stream[F, Json]

    /** Encodes Json data into text or binary format. */
    def encode(input: Stream[F, Json]): Stream[F, WSDataFrame]

    /** Set-up the required elements to encode/decode in each session. */
    def setup: Stream[F, Unit]
}

object MessageIo {
    export dev.axyria.scalacord.gateway.decoder.messageIoMonoid
}

/** The kind of compression applied to the gateway connection or payloads:
  *   - `None` - No compression will be applied to the payloads.
  *   - `Payload` - Enables optional per-packet compression for some events when Discord is sending
  *     events over the connection.
  *   - `Transport` - Enables optional per-packet compression for *all* received gateway payloads.
  */
enum CompressionKind {
    case None, Payload, Transport

    def compileUrl[F[_]](uri: Uri): Stream[F, Uri] = Stream.emit((this match {
        case Transport => uri.withQueryParam("compress", "zlib-stream")
        case _         => uri.removeQueryParam("compress")
    }).withQueryParam("encoding", "json"))
}

given messageIoMonoid[F[_]: Concurrent]: Monoid[MessageIo[F]] with
    override def empty: MessageIo[F] = new MessageIo[F]:
        override def kind: CompressionKind                                  = CompressionKind.None
        override def setup: Stream[F, Unit]                                 = Stream.empty
        override def decode(input: Stream[F, WSDataFrame]): Stream[F, Json] = Stream.empty
        override def encode(input: Stream[F, Json]): Stream[F, WSDataFrame] = Stream.empty

    override def combine(x: MessageIo[F], y: MessageIo[F]): MessageIo[F] = new MessageIo[F]:
        override def kind: CompressionKind =
            if x.kind == CompressionKind.None then y.kind else x.kind
        override def setup: Stream[F, Unit] = x.setup.flatMap(_ => y.setup)
        override def decode(input: Stream[F, WSDataFrame]): Stream[F, Json] =
            x.decode(input) ifEmpty y.decode(input)
        override def encode(input: Stream[F, Json]): Stream[F, WSDataFrame] =
            x.encode(input) ifEmpty y.encode(input)
