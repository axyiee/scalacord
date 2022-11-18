package dev.axyria.scalacord.gateway.platform

import cats.effect.kernel.{Resource, Async}
import org.http4s.client.websocket.WSClientHighLevel
import org.http4s.dom.WebSocketClient

object WsClientPlatform {
    def apply[F[_]: Async]: Resource[F, WSClientHighLevel[F]] =
        Resource.eval(Async[F].delay(WebSocketClient[F]))
}
