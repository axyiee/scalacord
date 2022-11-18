package dev.axyria.scalacord.gateway.job

import cats.effect.kernel.{Async, Concurrent}
import cats.effect.syntax.all.*
import cats.kernel.Monoid
import cats.syntax.all.*
import dev.axyria.scalacord.gateway.DiscordGateway
import dev.axyria.scalacord.gateway.payload.GatewayPayload
import fs2.Stream

/** A simple internal handler for whenever [[DiscordWebSocket]] receive new payloads.
  * @tparam F
  *   A type-class able to suspend side effects into the [[F]] context.
  */
trait GatewayJob[F[_]] {

    /** The entrypoint for this job. This runs whenever a new payload that meets the required
      * condition provided by [[should]] is received.
      */
    def entrypoint(payload: GatewayPayload): Stream[F, Unit]
}

object GatewayJob {
    def all[F[_]: Async](client: DiscordGateway[F]): F[GatewayJob[F]] =
        (HeartbeatingJob(client), Async[F].delay(LoginJob(client))).tupled
            .map(jobs => jobs.toList.map(_.asInstanceOf[GatewayJob[F]]))
            .map(list => Monoid[GatewayJob[F]].combineAll(list))

    given jobMonoid[F[_]: Concurrent]: Monoid[GatewayJob[F]] with
        def empty: GatewayJob[F] = new GatewayJob[F] {
            def entrypoint(payload: GatewayPayload): Stream[F, Unit] = Stream.empty
        }
        def combine(x: GatewayJob[F], y: GatewayJob[F]): GatewayJob[F] = new GatewayJob[F] {
            def entrypoint(payload: GatewayPayload): Stream[F, Unit] =
                x.entrypoint(payload) concurrently y.entrypoint(payload)
        }
}
