package dev.axyria.scalacord.gateway.platform

import cats.effect.kernel.Async
import cats.effect.kernel.Resource
import java.net.http.HttpClient
import org.http4s.client.websocket.WSClientHighLevel
import org.http4s.jdkhttpclient.JdkHttpClient
import org.http4s.jdkhttpclient.JdkWSClient

object WsClientPlatform {
    def apply[F[_]: Async]: Resource[F, WSClientHighLevel[F]] =
        Resource
            .eval(Async[F].delay(HttpClient.newHttpClient()))
            .flatMap(client => JdkWSClient(client))
}
