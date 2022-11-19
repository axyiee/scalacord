package dev.axyria.scalacord.gateway.job

import cats.effect.kernel.Sync
import cats.syntax.all.*
import dev.axyria.scalacord.gateway.DiscordGateway
import dev.axyria.scalacord.gateway.payload.GatewayPayload
import dev.axyria.scalacord.gateway.payload.event.Event
import fs2.Stream
import spire.math.ULong

/** A job for incremeting the sequence based on event receiving.
  *
  * @tparam F
  *   A type-class able to suspend side effects into the [[F]] context.
  */
case class SequenceJob[F[_]: Sync](client: DiscordGateway[F]) extends GatewayJob[F] {
    override def entrypoint(payload: GatewayPayload, event: Stream[F, Event]): Stream[F, Unit] =
        Stream
            .fromOption(payload.s.toOption)
            .evalMap(sequence => client.sequence.set(Some(ULong(sequence))))
}
