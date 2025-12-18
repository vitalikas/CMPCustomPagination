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

    val Notifications: ImageVector by lazy {
        ImageVector.Builder(
            name = "notifications",
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
                moveTo(160f, 760f)
                lineTo(160f, 680f)
                lineTo(240f, 680f)
                lineTo(240f, 400f)
                quadTo(240f, 307f, 287f, 232f)
                quadTo(334f, 157f, 420f, 126f)
                lineTo(420f, 100f)
                quadTo(420f, 75f, 437.5f, 57.5f)
                quadTo(455f, 40f, 480f, 40f)
                quadTo(505f, 40f, 522.5f, 57.5f)
                quadTo(540f, 75f, 540f, 100f)
                lineTo(540f, 126f)
                quadTo(626f, 157f, 673f, 232f)
                quadTo(720f, 307f, 720f, 400f)
                lineTo(720f, 680f)
                lineTo(800f, 680f)
                lineTo(800f, 760f)
                lineTo(160f, 760f)
                close()
                moveTo(480f, 920f)
                quadTo(447f, 920f, 423.5f, 896.5f)
                quadTo(400f, 873f, 400f, 840f)
                lineTo(560f, 840f)
                quadTo(560f, 873f, 536.5f, 896.5f)
                quadTo(513f, 920f, 480f, 920f)
                close()
            }
        }.build()
    }

    val Analytics: ImageVector by lazy {
        ImageVector.Builder(
            name = "analytics",
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
                lineTo(280f, 520f)
                lineTo(400f, 520f)
                lineTo(400f, 880f)
                lineTo(280f, 880f)
                close()
                moveTo(440f, 880f)
                lineTo(440f, 80f)
                lineTo(560f, 80f)
                lineTo(560f, 880f)
                lineTo(440f, 880f)
                close()
                moveTo(600f, 880f)
                lineTo(600f, 320f)
                lineTo(720f, 320f)
                lineTo(720f, 880f)
                lineTo(600f, 880f)
                close()
            }
        }.build()
    }

    val Refresh: ImageVector by lazy {
        ImageVector.Builder(
            name = "refresh",
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
                moveTo(480f, 800f)
                quadTo(380f, 800f, 297f, 755.5f)
                quadTo(214f, 711f, 161f, 638f)
                lineTo(254f, 546f)
                quadTo(289f, 595f, 346f, 627.5f)
                quadTo(403f, 660f, 480f, 660f)
                quadTo(580f, 660f, 653.5f, 594f)
                quadTo(727f, 528f, 739f, 433f)
                lineTo(623f, 318f)
                lineTo(880f, 318f)
                lineTo(880f, 574f)
                lineTo(785f, 479f)
                quadTo(761f, 600f, 672f, 700f)
                quadTo(583f, 800f, 480f, 800f)
                close()
                moveTo(80f, 642f)
                lineTo(80f, 386f)
                lineTo(175f, 481f)
                quadTo(199f, 360f, 288f, 260f)
                quadTo(377f, 160f, 480f, 160f)
                quadTo(580f, 160f, 663f, 204.5f)
                quadTo(746f, 249f, 799f, 322f)
                lineTo(706f, 414f)
                quadTo(671f, 365f, 614f, 332.5f)
                quadTo(557f, 300f, 480f, 300f)
                quadTo(380f, 300f, 306.5f, 366f)
                quadTo(233f, 432f, 221f, 527f)
                lineTo(337f, 642f)
                lineTo(80f, 642f)
                close()
            }
        }.build()
    }

    val Settings: ImageVector by lazy {
        ImageVector.Builder(
            name = "settings",
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
                moveTo(370f, 880f)
                lineTo(351f, 742f)
                quadTo(336f, 737f, 322f, 728f)
                quadTo(308f, 719f, 296f, 710f)
                lineTo(168f, 762f)
                lineTo(78f, 602f)
                lineTo(188f, 521f)
                quadTo(186f, 512f, 186f, 501.5f)
                quadTo(186f, 491f, 186f, 480f)
                quadTo(186f, 469f, 186f, 458.5f)
                quadTo(186f, 448f, 188f, 439f)
                lineTo(78f, 358f)
                lineTo(168f, 198f)
                lineTo(296f, 250f)
                quadTo(308f, 241f, 322f, 232f)
                quadTo(336f, 223f, 351f, 218f)
                lineTo(370f, 80f)
                lineTo(590f, 80f)
                lineTo(609f, 218f)
                quadTo(624f, 223f, 638f, 232f)
                quadTo(652f, 241f, 664f, 250f)
                lineTo(792f, 198f)
                lineTo(882f, 358f)
                lineTo(772f, 439f)
                quadTo(774f, 448f, 774f, 458.5f)
                quadTo(774f, 469f, 774f, 480f)
                quadTo(774f, 491f, 774f, 501.5f)
                quadTo(774f, 512f, 772f, 521f)
                lineTo(882f, 602f)
                lineTo(792f, 762f)
                lineTo(664f, 710f)
                quadTo(652f, 719f, 638f, 728f)
                quadTo(624f, 737f, 609f, 742f)
                lineTo(590f, 880f)
                lineTo(370f, 880f)
                close()
                moveTo(480f, 600f)
                quadTo(530f, 600f, 565f, 565f)
                quadTo(600f, 530f, 600f, 480f)
                quadTo(600f, 430f, 565f, 395f)
                quadTo(530f, 360f, 480f, 360f)
                quadTo(430f, 360f, 395f, 395f)
                quadTo(360f, 430f, 360f, 480f)
                quadTo(360f, 530f, 395f, 565f)
                quadTo(430f, 600f, 480f, 600f)
                close()
            }
        }.build()
    }

    val Search: ImageVector by lazy {
        ImageVector.Builder(
            name = "search",
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
                moveTo(796f, 876f)
                lineTo(532f, 612f)
                quadTo(502f, 637f, 463f, 650.5f)
                quadTo(424f, 664f, 380f, 664f)
                quadTo(273f, 664f, 198.5f, 589.5f)
                quadTo(124f, 515f, 124f, 408f)
                quadTo(124f, 301f, 198.5f, 226.5f)
                quadTo(273f, 152f, 380f, 152f)
                quadTo(487f, 152f, 561.5f, 226.5f)
                quadTo(636f, 301f, 636f, 408f)
                quadTo(636f, 452f, 622.5f, 491f)
                quadTo(609f, 530f, 584f, 560f)
                lineTo(848f, 824f)
                lineTo(796f, 876f)
                close()
                moveTo(380f, 592f)
                quadTo(454f, 592f, 505f, 541f)
                quadTo(556f, 490f, 556f, 408f)
                quadTo(556f, 326f, 505f, 275f)
                quadTo(454f, 224f, 380f, 224f)
                quadTo(306f, 224f, 255f, 275f)
                quadTo(204f, 326f, 204f, 408f)
                quadTo(204f, 490f, 255f, 541f)
                quadTo(306f, 592f, 380f, 592f)
                close()
            }
        }.build()
    }

    val Close: ImageVector by lazy {
        ImageVector.Builder(
            name = "close",
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
                moveTo(256f, 760f)
                lineTo(200f, 704f)
                lineTo(424f, 480f)
                lineTo(200f, 256f)
                lineTo(256f, 200f)
                lineTo(480f, 424f)
                lineTo(704f, 200f)
                lineTo(760f, 256f)
                lineTo(536f, 480f)
                lineTo(760f, 704f)
                lineTo(704f, 760f)
                lineTo(480f, 536f)
                lineTo(256f, 760f)
                close()
            }
        }.build()
    }

    val Star: ImageVector by lazy {
        ImageVector.Builder(
            name = "star",
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
                moveTo(480f, 757f)
                lineTo(318f, 849f)
                lineTo(361f, 663f)
                lineTo(223f, 544f)
                lineTo(415f, 527f)
                lineTo(480f, 353f)
                lineTo(545f, 527f)
                lineTo(737f, 544f)
                lineTo(599f, 663f)
                lineTo(642f, 849f)
                lineTo(480f, 757f)
                close()
            }
        }.build()
    }

    val StarBorder: ImageVector by lazy {
        ImageVector.Builder(
            name = "star_border",
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
                moveTo(480f, 696f)
                lineTo(584f, 755f)
                lineTo(557f, 639f)
                lineTo(648f, 566f)
                lineTo(528f, 558f)
                lineTo(480f, 447f)
                lineTo(432f, 558f)
                lineTo(312f, 566f)
                lineTo(403f, 639f)
                lineTo(376f, 755f)
                lineTo(480f, 696f)
                close()
                moveTo(480f, 757f)
                lineTo(318f, 849f)
                lineTo(361f, 663f)
                lineTo(223f, 544f)
                lineTo(415f, 527f)
                lineTo(480f, 353f)
                lineTo(545f, 527f)
                lineTo(737f, 544f)
                lineTo(599f, 663f)
                lineTo(642f, 849f)
                lineTo(480f, 757f)
                close()
            }
        }.build()
    }

    val Sort: ImageVector by lazy {
        ImageVector.Builder(
            name = "sort",
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
                moveTo(120f, 720f)
                lineTo(120f, 640f)
                lineTo(360f, 640f)
                lineTo(360f, 720f)
                lineTo(120f, 720f)
                close()
                moveTo(120f, 520f)
                lineTo(120f, 440f)
                lineTo(600f, 440f)
                lineTo(600f, 520f)
                lineTo(120f, 520f)
                close()
                moveTo(120f, 320f)
                lineTo(120f, 240f)
                lineTo(840f, 240f)
                lineTo(840f, 320f)
                lineTo(120f, 320f)
                close()
            }
        }.build()
    }
}
