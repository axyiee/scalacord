package lol.syntax.scalacord.common.util

import io.circe.{Decoder, Encoder, HCursor, Json}
import lol.syntax.scalacord.common.datatype.{Keep, Optional}

type ListBuffer[A] = scala.collection.mutable.Builder[A, List[A]]

private[common] def insert[A: Encoder](
    buffer: ListBuffer[(String, Json)],
    name: String,
    value: Optional[A]
)(using encoder: Encoder[A]) =
    value match {
        case Keep(_) =>
            buffer += {
                (name, value.toOption.map(v => encoder(v)).getOrElse(Json.Null))
            }
        case _ => ()
    }

private[common] def insert[A: Encoder](buffer: ListBuffer[(String, Json)], name: String, value: A)(
    using encoder: Encoder[A]
) =
    buffer += { (name, encoder.apply(value)) }
