package dev.axyria.scalacord.gateway

import dev.axyria.scalacord.gateway.payload.GatewayPayload
import fs2.Stream
import org.http4s.client.websocket.WSConnectionHighLevel
import scala.concurrent.duration.FiniteDuration
import org.http4s.Uri

/** A general-purpose simple I/O consumer of the Discord Gateway.
  * @tparam F
  *   A type-class able to suspend side effects into the [[F]] context.
  */
trait Gateway[F[_]] {

    /** A simple stream of messages received from the gateway. */
    def receive: Stream[F, GatewayPayload]

    /** Send a payload to the gateway. */
    def send(payload: GatewayPayload): F[Unit]

    /** The latency between heartbeat sending and its ack receiving. */
    def ping: F[FiniteDuration]

    /** Connects into the gateway and return a stream about the WebSocket connection individually.
      * @param url
      *   The url used to connect to the gateway.
      */
    def connect(url: Uri): Stream[F, WSConnectionHighLevel[F]]
}
