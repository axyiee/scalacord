package lol.syntax.scalacord.common.entity

import io.circe.Decoder.Result
import io.circe.{Decoder, Encoder, HCursor, Json}

/** The active Discord Nitro subscription for an user account. */
enum NitroSubscription(val value: Int) {
    case None extends NitroSubscription(0)

    case Classic extends NitroSubscription(1)

    case Nitro extends NitroSubscription(2)

    case Basic extends NitroSubscription(3) 

    export lol.syntax.scalacord.common.entity.subscriptionEncoder
    export lol.syntax.scalacord.common.entity.subscriptionDecoder
}

given subscriptionEncoder: Encoder[NitroSubscription] with
    final def apply(sub: NitroSubscription): Json = Json.fromInt(sub.value)

given subscriptionDecoder: Decoder[NitroSubscription] with
    final def apply(cursor: HCursor): Result[NitroSubscription] = cursor.as[Int]
        .map(int => NitroSubscription.values.find(_.value == int).getOrElse(NitroSubscription.None))
