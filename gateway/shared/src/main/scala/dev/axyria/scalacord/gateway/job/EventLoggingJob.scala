package dev.axyria.scalacord.gateway.job

import cats.Show
import cats.syntax.all.*
import cats.effect.kernel.Sync
import dev.axyria.scalacord.gateway.payload.GatewayPayload
import dev.axyria.scalacord.gateway.payload.event.Event
import org.typelevel.log4cats.syntax.*
import org.typelevel.log4cats.{Logger, LoggerFactory}
import fs2.Stream

/** A simple event logging system useful for debugging.
  * @tparam F
  *   A type-class able to suspend side effects into the [[F]] context.
  */
class EventLoggingJob[F[_]: Sync](using factory: LoggerFactory[F]) extends GatewayJob[F] {
    given Logger[F] = factory.getLogger

    override def entrypoint(payload: GatewayPayload): Stream[F, Unit] =
        Stream.fromEither(payload.event).map { event =>
            debug"${event.show}"
        }
}

given eventShow: Show[Event] = Show.show { event =>
    s"(${event.codec.opCode}, ${event.codec.eventId}) => ${event.data}"
}
