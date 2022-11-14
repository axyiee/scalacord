package lol.syntax.scalacord.gateway.payload.event

import io.circe.*
import io.circe.Decoder.Result
import io.circe.DecodingFailure.Reason.CustomReason
import lol.syntax.scalacord.gateway.payload.{GatewayPayload, PayloadData}
import lol.syntax.scalacord.common.datatype.Optional

sealed trait Event {
    type A <: PayloadData

    def codec: EventPayloadCodec[A]

    def data: A

    def sequence: Long
}

object Event {
    type Aux[A0] = Event { type A = A0 }

    given encoder(using gp: Encoder[GatewayPayload]): Encoder[Event] with
        final def apply(event: Event): Json =
            gp.apply(
                GatewayPayload(
                    event.codec.opCode,
                    event.codec.encoder(event.data),
                    Optional.keep(Some(event.sequence)),
                    Optional.keep(Some(event.codec.eventId))
                )
            )

    given decoder(using gp: Decoder[GatewayPayload]): Decoder[Event] with
        final def apply(c: HCursor): Result[Event] =
            gp(c) match {
                case Right(value) =>
                    value.t.toOption
                        .toRight(DecodingFailure(CustomReason("Event name not provided"), c))
                        .flatMap { name =>
                            Events.Codecs
                                .get(name)
                                .toRight(
                                    DecodingFailure(CustomReason(s"Codec not found for $name"), c)
                                )
                        }
                        .flatMap { co =>
                            val event = co.decoder(value.d.hcursor)
                            event.map(d =>
                                new Event {
                                    type A = d.type
                                    def codec: co.type = co
                                    def data: d.type   = d
                                    def sequence: Long = value.s.toOption.getOrElse(0)
                                }
                            )
                        }
                case Left(value) => Left(value)
            }
}
