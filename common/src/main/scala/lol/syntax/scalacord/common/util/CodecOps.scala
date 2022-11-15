package lol.syntax.scalacord.common.util

import io.circe.{Encoder, Json}
import lol.syntax.scalacord.common.datatype.{Keep, Missing, Optional}

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
        val l = list.flatten
            .filterNot(_.value == Missing)
            .map(ctx => (ctx.key, ctx.encoder.apply(ctx.value)))
        Json.obj(l*)
