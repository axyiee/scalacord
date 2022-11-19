package dev.axyria.scalacord.gateway.payload.command

import dev.axyria.scalacord.gateway.payload.GatewayPayload
import dev.axyria.scalacord.gateway.payload.PayloadCodec
import dev.axyria.scalacord.gateway.payload.PayloadData
import io.circe.Encoder

/** A payload codec trait driven especially for gateway commands.
  * @tparam A
  *   The kind of command-driven [[PayloadData]] this codec refers to.
  */
trait CommandPayloadCodec[A <: PayloadData] extends PayloadCodec[A] {

    /** Converts a command of type [[A]] into a payload.
      * @param command
      *   The command data to be converted in.
      * @param encoder
      *   The encoder for this command data.
      */
    def toPayload(command: A)(using encoder: Encoder[A]): GatewayPayload =
        GatewayPayload(
            op = opCode,
            d = encoder(command),
        )
}

object Commands {
    // opcode
    type Opcode = Int

    val Codecs: Map[Opcode, CommandPayloadCodec[?]] =
        List(HeartbeatCodec, IdentifyCodec).map(codec => (codec.opCode, codec)).toMap

    def get(op: Opcode): Option[CommandPayloadCodec[?]] = Codecs.get(op)
}
