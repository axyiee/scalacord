package lol.syntax.scalacord.gateway

import cats.effect.kernel.{Ref, Sync}
import cats.effect.std.Queue
import fs2.concurrent.Topic
import io.circe.Json
import lol.syntax.scalacord.gateway.payload.GatewayPayload
import org.http4s.client.Client
import spire.math.ULong

import java.net.URL

case class WebSocketProperties(address: URL)

case class WebSocket[F[_]: Sync](
    client: Client[F],
    decoder: Array[Byte] => Json,
    inbound: Topic[F, GatewayPayload],
    outbound: Queue[F, GatewayPayload],
    sequence: Ref[F, Option[ULong]]
) {}
