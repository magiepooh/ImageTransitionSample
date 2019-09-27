package com.github.magiepooh.imagetransitionsample

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.*
import android.util.AttributeSet
import androidx.annotation.DimenRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat

/**
 * 角丸にクリッピングしてくれる[AppCompatImageView]
 *
 * ### 追加理由について
 * 以下のようにCardView上にedge-to-edgeに広がるImageViewを表現するときに、うまくいかなかったことがあったため。
 *
 * - GlideのRoundedCornersTransformationでの角丸と、CardViewの角丸が微妙に一致せず
 * landscapeでCardViewとImageViewの間に角丸があいてしまう
 *
 *
 */
class RoundedImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    var topLeftRadius: Int = 0
        private set
    var topRightRadius: Int = 0
        private set
    var bottomLeftRadius: Int = 0
        private set
    var bottomRightRadius: Int = 0
        private set

    private val maskedPaint: Paint = Paint().apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP)
    }
    private val copyPaint: Paint = Paint()
    private val maskDrawable: RoundRectDrawable = RoundRectDrawable(
        ColorStateList.valueOf(ContextCompat.getColor(context, android.R.color.white))
    ).apply {
        radius = context.getDimens(R.dimen.radius_card).toFloat()
        setRadius(radius.toInt(), radius.toInt(), 0, 0)
    }

    private var mBounds: Rect? = null
    private var mBoundsF: RectF? = null

    init {
        context.obtainStyledAttributes(attrs, R.styleable.RoundedImageView).also {
            if (it.hasValue(R.styleable.RoundedImageView_flo_radius)) {
                it.getDimensionPixelSize(R.styleable.RoundedImageView_flo_radius, 0).let {
                    topLeftRadius = it
                    topRightRadius = it
                    bottomLeftRadius = it
                    bottomRightRadius = it
                }
            } else {
                topLeftRadius = it.getDimensionPixelSize(
                    R.styleable.RoundedImageView_flo_top_left_radius,
                    0
                )
                topRightRadius = it.getDimensionPixelSize(
                    R.styleable.RoundedImageView_flo_top_right_radius,
                    0
                )
                bottomLeftRadius = it.getDimensionPixelSize(
                    R.styleable.RoundedImageView_flo_bottom_left_radius, 0
                )
                bottomRightRadius = it.getDimensionPixelSize(
                    R.styleable.RoundedImageView_flo_bottom_right_radius, 0
                )
            }
        }.recycle()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        mBounds = Rect(0, 0, w, h)
        mBoundsF = RectF(mBounds)
    }

    override fun onDraw(canvas: Canvas) {
        val sc = canvas.saveLayer(mBoundsF, copyPaint)
        mBounds?.let { maskDrawable.bounds = it }
        updateMaskDrawable()
        maskDrawable.draw(canvas)
        canvas.saveLayer(mBoundsF, maskedPaint)

        super.onDraw(canvas)

        canvas.restoreToCount(sc)
    }

    fun setRadius(radius: Int) {
        setRadius(radius, radius, radius, radius)
    }

    fun setRadius(
        topLeftRadius: Int = 0,
        topRightRadius: Int = 0,
        bottomLeftRadius: Int = 0,
        bottomRightRadius: Int = 0
    ) {
        this.topLeftRadius = topLeftRadius
        this.topRightRadius = topRightRadius
        this.bottomLeftRadius = bottomLeftRadius
        this.bottomRightRadius = bottomRightRadius
        updateMaskDrawable()
    }

    private fun updateMaskDrawable() {
        maskDrawable.setRadius(topLeftRadius, topRightRadius, bottomLeftRadius, bottomRightRadius)
    }
}


fun Context.getDimens(@DimenRes resId: Int) = resources.getDimensionPixelSize(resId)