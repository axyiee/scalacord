package dev.axyria.scalacord.common.entity

import io.circe.{Decoder, Encoder, HCursor, Json}

/** A type representing shard information for a bot application. It is encoded as an array in the
  * format: [(current, num_shards]).
  */
case class Shard(current: Int, count: Int)

object Shard {
    export dev.axyria.scalacord.common.entity.shardEncoder
    export dev.axyria.scalacord.common.entity.shardDecoder
}

given shardEncoder: Encoder[Shard] with
    final def apply(shard: Shard): Json =
        Json.arr(Json.fromInt(shard.current), Json.fromInt(shard.count))

given shardDecoder: Decoder[Shard] with
    final def apply(cursor: HCursor): Decoder.Result[Shard] =
        val array = cursor.downArray
        for {
            current <- array.as[Int]
            count   <- array.right.as[Int]
        } yield Shard(current, count)
