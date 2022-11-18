package dev.axyria.scalacord.common.util

import dev.axyria.scalacord.common.datatype.{Keep, Missing, Optional}
import io.circe.{Encoder, Json}

case class EncodingContext[A: Encoder](encoder: Encoder[A], key: String, value: A)

extension [A: Encoder](seq: (String, A))
    def context: Option[EncodingContext[A]] =
        Some(EncodingContext(Encoder.apply[A], seq._1, seq._2))

extension [A: Encoder](seq: (String, Optional[A]))
    def optionContext: Option[EncodingContext[Option[A]]] =
        if seq._2 == Missing then None
        else Some(EncodingContext(Encoder.encodeOption[A], seq._1, seq._2.toOption))

extension (list: List[Option[EncodingContext[?]]])
    def withOptional: Json =
        Json.obj(list.flatten.map(ctx => (ctx.key, ctx.encoder.apply(ctx.value)))*)
