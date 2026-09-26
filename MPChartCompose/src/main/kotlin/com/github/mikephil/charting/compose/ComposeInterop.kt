package com.github.mikephil.charting.compose

import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import com.github.mikephil.charting.data.BaseDataSet
import com.github.mikephil.charting.data.LineDataSet
import kotlin.math.roundToInt

/**
 * Sets the colors the data set cycles through for its entries, converting each Compose color to ARGB.
 * For a single color use `color = myColor.toArgb()`.
 */
public fun BaseDataSet<*>.setColors(colors: List<Color>) {
    this.colors = colors.map { it.toArgb() }
}

/** Sets the colors of the circles drawn at each point of a line, converting each Compose color to ARGB. */
public fun LineDataSet<*>.setCircleColors(colors: List<Color>) {
    circleColors = colors.map { it.toArgb() }
}

/** Turns a list of ARGB ints, such as `ColorTemplate.MATERIAL_COLORS`, into Compose colors. */
public fun List<Int>.toComposeColors(): List<Color> = map { Color(it) }

/**
 * Resolves a Compose font to the [Typeface] the chart components take, for example for axis and
 * legend text. The result is remembered until [fontFamily], [fontWeight] or [fontStyle] change.
 */
@Composable
public fun rememberTypeface(
    fontFamily: FontFamily,
    fontWeight: FontWeight = FontWeight.Normal,
    fontStyle: FontStyle = FontStyle.Normal,
): Typeface {
    val resolver = LocalFontFamilyResolver.current
    return remember(resolver, fontFamily, fontWeight, fontStyle) {
        resolver.resolve(fontFamily, fontWeight, fontStyle).value as Typeface
    }
}

/**
 * Draws [painter] into a bitmap of [size], given in dp and converted with the current density, and
 * returns it as a [Drawable] with its bounds set, ready to use as an entry icon. The bitmap is at
 * least 1 by 1 px and is remembered until the painter, size, density or layout direction change.
 */
@Composable
public fun rememberDrawable(painter: Painter, size: DpSize): Drawable {
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current
    val resources = LocalResources.current
    return remember(painter, size, density, layoutDirection) {
        val pixelSize = with(density) { Size(size.width.toPx(), size.height.toPx()) }
        val width = pixelSize.width.roundToInt().coerceAtLeast(1)
        val height = pixelSize.height.roundToInt().coerceAtLeast(1)
        val bitmap = ImageBitmap(width, height)
        CanvasDrawScope().draw(density, layoutDirection, Canvas(bitmap), pixelSize) {
            with(painter) { draw(pixelSize) }
        }
        BitmapDrawable(resources, bitmap.asAndroidBitmap()).apply { setBounds(0, 0, width, height) }
    }
}
