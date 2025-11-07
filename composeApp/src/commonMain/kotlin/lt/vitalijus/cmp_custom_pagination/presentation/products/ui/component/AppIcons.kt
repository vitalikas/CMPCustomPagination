package lt.vitalijus.cmp_custom_pagination.presentation.products.ui.component

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

object AppIcons {
    val Favorite: ImageVector by lazy {
        ImageVector.Builder(
            name = "favorite",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                fillAlpha = 1.0f,
                stroke = null,
                strokeAlpha = 1.0f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(480f, 840f)
                lineTo(422f, 788f)
                quadTo(321f, 697f, 255f, 631f)
                quadTo(189f, 565f, 150f, 512.5f)
                quadTo(111f, 460f, 95.5f, 416f)
                quadTo(80f, 372f, 80f, 326f)
                quadTo(80f, 232f, 143f, 169f)
                quadTo(206f, 106f, 300f, 106f)
                quadTo(352f, 106f, 399f, 128f)
                quadTo(446f, 150f, 480f, 190f)
                quadTo(514f, 150f, 561f, 128f)
                quadTo(608f, 106f, 660f, 106f)
                quadTo(754f, 106f, 817f, 169f)
                quadTo(880f, 232f, 880f, 326f)
                quadTo(880f, 372f, 864.5f, 416f)
                quadTo(849f, 460f, 810f, 512.5f)
                quadTo(771f, 565f, 705f, 631f)
                quadTo(639f, 697f, 538f, 788f)
                lineTo(480f, 840f)
                close()
            }
        }.build()
    }

    val FavoriteBorder: ImageVector by lazy {
        ImageVector.Builder(
            name = "favorite_border",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                fillAlpha = 1.0f,
                stroke = null,
                strokeAlpha = 1.0f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(480f, 732f)
                quadTo(576f, 646f, 638f, 584.5f)
                quadTo(700f, 523f, 736f, 477.5f)
                quadTo(772f, 432f, 786f, 396.5f)
                quadTo(800f, 361f, 800f, 326f)
                quadTo(800f, 266f, 760f, 226f)
                quadTo(720f, 186f, 660f, 186f)
                quadTo(613f, 186f, 573f, 212.5f)
                quadTo(533f, 239f, 518f, 280f)
                lineTo(442f, 280f)
                quadTo(427f, 239f, 387f, 212.5f)
                quadTo(347f, 186f, 300f, 186f)
                quadTo(240f, 186f, 200f, 226f)
                quadTo(160f, 266f, 160f, 326f)
                quadTo(160f, 361f, 174f, 396.5f)
                quadTo(188f, 432f, 224f, 477.5f)
                quadTo(260f, 523f, 322f, 584.5f)
                quadTo(384f, 646f, 480f, 732f)
                close()
                moveTo(480f, 840f)
                lineTo(422f, 788f)
                quadTo(321f, 697f, 255f, 631f)
                quadTo(189f, 565f, 150f, 512.5f)
                quadTo(111f, 460f, 95.5f, 416f)
                quadTo(80f, 372f, 80f, 326f)
                quadTo(80f, 232f, 143f, 169f)
                quadTo(206f, 106f, 300f, 106f)
                quadTo(352f, 106f, 399f, 128f)
                quadTo(446f, 150f, 480f, 190f)
                quadTo(514f, 150f, 561f, 128f)
                quadTo(608f, 106f, 660f, 106f)
                quadTo(754f, 106f, 817f, 169f)
                quadTo(880f, 232f, 880f, 326f)
                quadTo(880f, 372f, 864.5f, 416f)
                quadTo(849f, 460f, 810f, 512.5f)
                quadTo(771f, 565f, 705f, 631f)
                quadTo(639f, 697f, 538f, 788f)
                lineTo(480f, 840f)
                close()
            }
        }.build()
    }

    val ShoppingCart: ImageVector by lazy {
        ImageVector.Builder(
            name = "shopping_cart",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                fillAlpha = 1.0f,
                stroke = null,
                strokeAlpha = 1.0f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(280f, 880f)
                quadTo(247f, 880f, 223.5f, 856.5f)
                quadTo(200f, 833f, 200f, 800f)
                quadTo(200f, 767f, 223.5f, 743.5f)
                quadTo(247f, 720f, 280f, 720f)
                quadTo(313f, 720f, 336.5f, 743.5f)
                quadTo(360f, 767f, 360f, 800f)
                quadTo(360f, 833f, 336.5f, 856.5f)
                quadTo(313f, 880f, 280f, 880f)
                close()
                moveTo(680f, 880f)
                quadTo(647f, 880f, 623.5f, 856.5f)
                quadTo(600f, 833f, 600f, 800f)
                quadTo(600f, 767f, 623.5f, 743.5f)
                quadTo(647f, 720f, 680f, 720f)
                quadTo(713f, 720f, 736.5f, 743.5f)
                quadTo(760f, 767f, 760f, 800f)
                quadTo(760f, 833f, 736.5f, 856.5f)
                quadTo(713f, 880f, 680f, 880f)
                close()
                moveTo(246f, 240f)
                lineTo(342f, 440f)
                lineTo(622f, 440f)
                lineTo(732f, 240f)
                lineTo(246f, 240f)
                close()
                moveTo(208f, 160f)
                lineTo(798f, 160f)
                quadTo(821f, 160f, 833f, 180.5f)
                quadTo(845f, 201f, 834f, 222f)
                lineTo(692f, 478f)
                quadTo(681f, 498f, 662.5f, 509f)
                quadTo(644f, 520f, 622f, 520f)
                lineTo(324f, 520f)
                lineTo(280f, 600f)
                lineTo(760f, 600f)
                lineTo(760f, 680f)
                lineTo(280f, 680f)
                quadTo(235f, 680f, 212f, 640.5f)
                quadTo(189f, 601f, 210f, 562f)
                lineTo(264f, 464f)
                lineTo(120f, 160f)
                lineTo(40f, 160f)
                lineTo(40f, 80f)
                lineTo(170f, 80f)
                lineTo(208f, 160f)
                close()
            }
        }.build()
    }

    val GridView: ImageVector by lazy {
        ImageVector.Builder(
            name = "grid_view",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                fillAlpha = 1.0f,
                stroke = null,
                strokeAlpha = 1.0f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(120f, 440f)
                lineTo(120f, 120f)
                lineTo(440f, 120f)
                lineTo(440f, 440f)
                lineTo(120f, 440f)
                close()
                moveTo(120f, 840f)
                lineTo(120f, 520f)
                lineTo(440f, 520f)
                lineTo(440f, 840f)
                lineTo(120f, 840f)
                close()
                moveTo(520f, 440f)
                lineTo(520f, 120f)
                lineTo(840f, 120f)
                lineTo(840f, 440f)
                lineTo(520f, 440f)
                close()
                moveTo(520f, 840f)
                lineTo(520f, 520f)
                lineTo(840f, 520f)
                lineTo(840f, 840f)
                lineTo(520f, 840f)
                close()
                moveTo(200f, 360f)
                lineTo(360f, 360f)
                lineTo(360f, 200f)
                lineTo(200f, 200f)
                lineTo(200f, 360f)
                close()
                moveTo(600f, 360f)
                lineTo(760f, 360f)
                lineTo(760f, 200f)
                lineTo(600f, 200f)
                lineTo(600f, 360f)
                close()
                moveTo(600f, 760f)
                lineTo(760f, 760f)
                lineTo(760f, 600f)
                lineTo(600f, 600f)
                lineTo(600f, 760f)
                close()
                moveTo(200f, 760f)
                lineTo(360f, 760f)
                lineTo(360f, 600f)
                lineTo(200f, 600f)
                lineTo(200f, 760f)
                close()
            }
        }.build()
    }

    val ViewList: ImageVector by lazy {
        ImageVector.Builder(
            name = "view_list",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                fillAlpha = 1.0f,
                stroke = null,
                strokeAlpha = 1.0f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(360f, 720f)
                lineTo(800f, 720f)
                lineTo(800f, 613f)
                lineTo(360f, 613f)
                lineTo(360f, 720f)
                close()
                moveTo(160f, 347f)
                lineTo(280f, 347f)
                lineTo(280f, 240f)
                lineTo(160f, 240f)
                lineTo(160f, 347f)
                close()
                moveTo(160f, 534f)
                lineTo(280f, 534f)
                lineTo(280f, 427f)
                lineTo(160f, 427f)
                lineTo(160f, 534f)
                close()
                moveTo(160f, 720f)
                lineTo(280f, 720f)
                lineTo(280f, 613f)
                lineTo(160f, 613f)
                lineTo(160f, 720f)
                close()
                moveTo(360f, 534f)
                lineTo(800f, 534f)
                lineTo(800f, 427f)
                lineTo(360f, 427f)
                lineTo(360f, 534f)
                close()
                moveTo(360f, 347f)
                lineTo(800f, 347f)
                lineTo(800f, 240f)
                lineTo(360f, 240f)
                lineTo(360f, 347f)
                close()
            }
        }.build()
    }

    val ArrowBack: ImageVector by lazy {
        ImageVector.Builder(
            name = "arrow_back",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                fillAlpha = 1.0f,
                stroke = null,
                strokeAlpha = 1.0f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(313f, 520f)
                lineTo(537f, 744f)
                lineTo(480f, 800f)
                lineTo(160f, 480f)
                lineTo(480f, 160f)
                lineTo(537f, 216f)
                lineTo(313f, 440f)
                lineTo(800f, 440f)
                lineTo(800f, 520f)
                lineTo(313f, 520f)
                close()
            }
        }.build()
    }

    val StarRateHalf: ImageVector by lazy {
        ImageVector.Builder(
            name = "star_rate_half",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                fillAlpha = 1.0f,
                stroke = null,
                strokeAlpha = 1.0f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(480f, 316f)
                lineTo(480f, 552f)
                lineTo(576f, 626f)
                lineTo(540f, 504f)
                lineTo(630f, 440f)
                lineTo(518f, 440f)
                lineTo(480f, 316f)
                close()
                moveTo(233f, 840f)
                lineTo(326f, 536f)
                lineTo(80f, 360f)
                lineTo(384f, 360f)
                lineTo(480f, 40f)
                lineTo(576f, 360f)
                lineTo(880f, 360f)
                lineTo(634f, 536f)
                lineTo(727f, 840f)
                lineTo(480f, 652f)
                lineTo(233f, 840f)
                close()
            }
        }.build()
    }

    val Delete: ImageVector by lazy {
        ImageVector.Builder(
            name = "delete",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                fillAlpha = 1.0f,
                stroke = null,
                strokeAlpha = 1.0f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(280f, 840f)
                quadTo(247f, 840f, 223.5f, 816.5f)
                quadTo(200f, 793f, 200f, 760f)
                lineTo(200f, 240f)
                lineTo(160f, 240f)
                lineTo(160f, 160f)
                lineTo(360f, 160f)
                lineTo(360f, 120f)
                lineTo(600f, 120f)
                lineTo(600f, 160f)
                lineTo(800f, 160f)
                lineTo(800f, 240f)
                lineTo(760f, 240f)
                lineTo(760f, 760f)
                quadTo(760f, 793f, 736.5f, 816.5f)
                quadTo(713f, 840f, 680f, 840f)
                lineTo(280f, 840f)
                close()
                moveTo(680f, 240f)
                lineTo(280f, 240f)
                lineTo(280f, 760f)
                lineTo(680f, 760f)
                lineTo(680f, 240f)
                close()
                moveTo(360f, 680f)
                lineTo(440f, 680f)
                lineTo(440f, 320f)
                lineTo(360f, 320f)
                lineTo(360f, 680f)
                close()
                moveTo(520f, 680f)
                lineTo(600f, 680f)
                lineTo(600f, 320f)
                lineTo(520f, 320f)
                lineTo(520f, 680f)
                close()
            }
        }.build()
    }
}
