package dev.axyria.scalacord.gateway.job

import cats.Show
import cats.effect.kernel.Sync
import cats.syntax.all.*
import dev.axyria.scalacord.gateway.DiscordGateway
import dev.axyria.scalacord.gateway.payload.GatewayPayload
import dev.axyria.scalacord.gateway.payload.event.Event
import fs2.Stream

/** A simple event logging system useful for debugging.
  * @tparam F
  *   A type-class able to suspend side effects into the [[F]] context.
  */
class EventLoggingJob[F[_]: Sync] extends GatewayJob[F] {
    override def entrypoint(payload: GatewayPayload, event: Stream[F, Event]): Stream[F, Unit] =
        Stream.empty // todo
            // event.map { event =>
            //    println(s"${event.show}")
            // }
}

given eventShow: Show[Event] = Show.show { event =>
    s"(${event.codec.opCode}, ${event.codec.eventId}) => ${event.data}"
}
