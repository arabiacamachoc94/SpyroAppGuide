package dam.pmdm.spyrothedragon.ui

import dam.pmdm.spyrothedragon.R
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import android.util.AttributeSet
import android.graphics.BitmapFactory
import android.graphics.RectF
import android.graphics.Bitmap
import android.graphics.Path


class RiptoMagicView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val paint = Paint()
    private var radius = 0f

    private var currentColor = Color.CYAN

    private val diamanteBitmap: Bitmap = BitmapFactory.decodeResource(
        resources, R.drawable.gems
    )

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = width / 2f
        val centerY = height / 2f

        // Escalamos la imagen según el radius del animator
        val scaledSize = radius * 2f

        val left = centerX - scaledSize / 2
        val top = centerY - scaledSize / 2
        val right = centerX + scaledSize / 2
        val bottom = centerY + scaledSize / 2

        val rect = RectF(left, top, right, bottom)
        canvas.drawBitmap(diamanteBitmap, null, rect, null)

        // Glow alrededor del diamante
        paint.color = currentColor
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 8f
        paint.alpha = 150

        val path = Path()
        path.moveTo(centerX, centerY - radius)
        path.lineTo(centerX + radius, centerY)
        path.lineTo(centerX, centerY + radius)
        path.lineTo(centerX - radius, centerY)
        path.close()

        canvas.drawPath(path, paint)
        paint.alpha = 255
    }

    fun updateRadius(value: Float) {
        radius = value
        invalidate()
    }
    fun updateColor(color: Int) {
        currentColor = color
        invalidate()
    }
}