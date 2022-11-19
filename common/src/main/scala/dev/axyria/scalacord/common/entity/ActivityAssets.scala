package dev.axyria.scalacord.common.entity

import dev.axyria.scalacord.common.datatype.*
import dev.axyria.scalacord.common.util.*
import io.circe.Decoder
import io.circe.Encoder
import io.circe.HCursor
import io.circe.Json

/** The assets for an active [[Activity]].
  * @param largeImage
  *   The path for the bigger image of the activity.
  * @param largeText
  *   The hover text for the [[largeImage]].
  * @param smallImage
  *   The path for the smaller image of the activity.
  * @param smallText
  *   The hover text for the [[smallImage]].
  */
case class ActivityAssets(
    val largeImage: Optional[String] = Optional.missing,
    val largeText: Optional[String] = Optional.missing,
    val smallImage: Optional[String] = Optional.missing,
    val smallText: Optional[String] = Optional.missing
)

object ActivityAssets {
    export dev.axyria.scalacord.common.entity.activityAssetsDecoder
    export dev.axyria.scalacord.common.entity.activityAssetsEncoder
}

given activityAssetsEncoder: Encoder[ActivityAssets] with
    final def apply(assets: ActivityAssets): Json =
        val elems: List[Option[EncodingContext[?]]] =
            ("large_image", assets.largeImage).optionContext ::
                ("large_text", assets.largeText).optionContext ::
                ("small_image", assets.smallImage).optionContext ::
                ("small_text", assets.smallText).optionContext ::
                Nil
        elems.withOptional

given activityAssetsDecoder: Decoder[ActivityAssets] with
    final def apply(cursor: HCursor): Decoder.Result[ActivityAssets] =
        for
            largeImage <- cursor.get[Optional[String]]("large_image")
            largeText  <- cursor.get[Optional[String]]("large_text")
            smallImage <- cursor.get[Optional[String]]("small_image")
            smallText  <- cursor.get[Optional[String]]("small_text")
        yield ActivityAssets(largeImage, largeText, smallImage, smallText)
