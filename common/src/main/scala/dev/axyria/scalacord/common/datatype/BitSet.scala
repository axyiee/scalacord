package dev.axyria.scalacord.common.datatype

import io.circe.{Encoder, Decoder}

/** A simple helper for working with bitsets. A bitset is, as the name suggests, a number composed
  * by a set of bits. Each bit can be either 0 or 1 and the number is composed by the bits from
  * right to left. Most programming languages already provide you a great and simple way to work
  * with bitsets, but this one strives on serialization and algebraic data types support.
  *
  * ===Usage===
  *
  * {{{
  * enum Intent(override val value: Int) extends BitSet[Intent] {
  *  case Guilds extends Intent(1 << 0)
  *  case GuildMembers extends Intent(1 << 1)
  * }
  * }}}
  *
  * /// ... val intents: List[Intent] = Intent.Guilds + Intent.GuildMembers val toBitSet: Int =
  * intents.toBitSet
  */
trait BitSet[T <: BitSet[T]] { self =>
    def value: Int

    def +(other: T): List[T] = List(self.asInstanceOf, other)

    def +(other: List[T]): List[T] = self.asInstanceOf +: other

    def toBitSet: Int = value
}

trait AllBitSet[T <: BitSet[T]] {
    def values: Array[T]
}

extension [T <: BitSet[T]](bitSet: List[T])
    def toBitSet: Int = bitSet.foldLeft(0)((acc, bit) => acc | bit.value)

extension (bits: Int)
    def fromBitSet[T <: BitSet[T]](using all: AllBitSet[T]): List[T] =
        all.values.filter(bit => (bits & bit.value) == bit.value).toList

object BitSet {
    given encoder[T <: BitSet[T]]: Encoder[List[T]] = Encoder.encodeInt.contramap(_.toBitSet)

    given decoder[T <: BitSet[T]](using all: AllBitSet[T]): Decoder[List[T]] =
        Decoder.decodeInt.map(_.fromBitSet[T])
}
