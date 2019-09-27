package com.github.magiepooh.imagetransitionsample

import android.content.res.ColorStateList
import android.graphics.*
import android.graphics.drawable.Drawable

/**
 * RoundRectDrawableを元に改変したもの.
 *
 * @see https://android.googlesource.com/platform/frameworks/support/+/83b8526/v7/cardview/api21/android/support/v7/widget/RoundRectDrawable.java
 */
class RoundRectDrawable(
    backgroundColor: ColorStateList,
    private var mRadius: Float = 0f
) : Drawable() {

    companion object {
        private const val SHADOW_MULTIPLIER = 1.5f
        private val COS_45 = Math.cos(Math.toRadians(45.0))
    }

    private val mPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG)
    private val mBoundsF: RectF
    private val mBoundsI: Rect
    var padding: Float = 0.toFloat()
        private set
    private var mInsetForPadding = false
    private var mInsetForRadius = true

    private var mBackground: ColorStateList? = null
    private var mTintFilter: PorterDuffColorFilter? = null
    private var mTint: ColorStateList? = null
    private var mTintMode: PorterDuff.Mode? = PorterDuff.Mode.SRC_IN

    private var topLeftRadius: Int = 0
    private var topRightRadius: Int = 0
    private var bottomLeftRadius: Int = 0
    private var bottomRightRadius: Int = 0

    var radius: Float
        get() = mRadius
        set(radius) {
            if (radius == mRadius) {
                return
            }
            mRadius = radius
            updateBounds(null)
            invalidateSelf()
        }

    var color: ColorStateList?
        get() = mBackground
        set(color) {
            setBackground(color)
            invalidateSelf()
        }

    init {
        setBackground(backgroundColor)

        mBoundsF = RectF()
        mBoundsI = Rect()
    }

    override fun draw(canvas: Canvas) {
        val paint = mPaint

        val clearColorFilter: Boolean
        if (mTintFilter != null && paint.colorFilter == null) {
            paint.colorFilter = mTintFilter
            clearColorFilter = true
        } else {
            clearColorFilter = false
        }

        canvas.drawPath(createRoundedPath(), paint)

        if (clearColorFilter) {
            paint.colorFilter = null
        }
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
        updateBounds(null)
        invalidateSelf()
    }

    fun setPadding(padding: Float, insetForPadding: Boolean, insetForRadius: Boolean) {
        if (padding == this.padding && mInsetForPadding == insetForPadding &&
            mInsetForRadius == insetForRadius
        ) {
            return
        }
        this.padding = padding
        mInsetForPadding = insetForPadding
        mInsetForRadius = insetForRadius
        updateBounds(null)
        invalidateSelf()
    }

    /**
     * 指定した角丸をPathで表現する
     */
    private fun createRoundedPath() = Path().also {
        val topLeftRadius = Math.max(topLeftRadius, 0).toFloat()
        val topRightRadius = Math.max(topRightRadius, 0).toFloat()
        val bottomLeftRadius = Math.max(bottomLeftRadius, 0).toFloat()
        val bottomRightRadius = Math.max(bottomRightRadius, 0).toFloat()

        val width = mBoundsF.run { right - left }
        val height = mBoundsF.run { bottom - top }
        it.moveTo(mBoundsF.right, mBoundsF.top + topRightRadius)
        it.rQuadTo(0f, -topRightRadius, -topRightRadius, -topRightRadius)
        it.rLineTo(-(width - topLeftRadius - topRightRadius), 0f)
        it.rQuadTo(-topLeftRadius, 0f, -topLeftRadius, topLeftRadius)
        it.rLineTo(0f, height - topLeftRadius - bottomLeftRadius)
        it.rQuadTo(0f, bottomLeftRadius, bottomLeftRadius, bottomLeftRadius)
        it.rLineTo(width - bottomLeftRadius - bottomRightRadius, 0f)
        it.rQuadTo(bottomRightRadius, 0f, bottomRightRadius, -bottomRightRadius)
        it.rLineTo(0f, -(height - topRightRadius - bottomRightRadius))
        it.close()
    }

    private fun setBackground(color: ColorStateList?) {
        mBackground = color ?: ColorStateList.valueOf(Color.TRANSPARENT)
        mPaint.color = mBackground!!.getColorForState(state, mBackground!!.defaultColor)
    }

    private fun updateBounds(newBounds: Rect?) {
        val bounds = newBounds ?: bounds
        mBoundsF.set(
            bounds!!.left.toFloat(), bounds.top.toFloat(), bounds.right.toFloat(),
            bounds.bottom.toFloat()
        )
        mBoundsI.set(bounds)
        if (mInsetForPadding) {
            val vInset = calculateVerticalPadding(padding, mRadius, mInsetForRadius)
            val hInset = calculateHorizontalPadding(padding, mRadius, mInsetForRadius)
            mBoundsI.inset(Math.ceil(hInset.toDouble()).toInt(), Math.ceil(vInset.toDouble()).toInt())
            // to make sure they have same bounds.
            mBoundsF.set(mBoundsI)
        }
    }

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        updateBounds(bounds)
    }

    override fun getOutline(outline: Outline) {
        outline.setRoundRect(mBoundsI, mRadius)
    }

    override fun setAlpha(alpha: Int) {
        mPaint.alpha = alpha
    }

    override fun setColorFilter(cf: ColorFilter?) {
        mPaint.colorFilter = cf
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    override fun setTintList(tint: ColorStateList?) {
        mTint = tint
        mTintFilter = createTintFilter(mTint, mTintMode)
        invalidateSelf()
    }

    override fun setTintMode(tintMode: PorterDuff.Mode?) {
        mTintMode = tintMode
        mTintFilter = createTintFilter(mTint, mTintMode)
        invalidateSelf()
    }

    override fun onStateChange(stateSet: IntArray): Boolean {
        val newColor = mBackground!!.getColorForState(stateSet, mBackground!!.defaultColor)
        val colorChanged = newColor != mPaint.color
        if (colorChanged) {
            mPaint.color = newColor
        }
        if (mTint != null && mTintMode != null) {
            mTintFilter = createTintFilter(mTint, mTintMode)
            return true
        }
        return colorChanged
    }

    override fun isStateful(): Boolean {
        return (mTint != null && mTint!!.isStateful ||
                mBackground != null && mBackground!!.isStateful || super.isStateful())
    }

    /**
     * Ensures the tint filter is consistent with the current tint color and
     * mode.
     */
    private fun createTintFilter(
        tint: ColorStateList?,
        tintMode: PorterDuff.Mode?
    ): PorterDuffColorFilter? {
        if (tint == null || tintMode == null) {
            return null
        }
        val color = tint.getColorForState(state, Color.TRANSPARENT)
        return PorterDuffColorFilter(color, tintMode)
    }

    private fun calculateVerticalPadding(
        maxShadowSize: Float,
        cornerRadius: Float,
        addPaddingForCorners: Boolean
    ): Float = if (addPaddingForCorners) {
        (maxShadowSize * SHADOW_MULTIPLIER + (1 - COS_45) * cornerRadius).toFloat()
    } else {
        maxShadowSize * SHADOW_MULTIPLIER
    }

    private fun calculateHorizontalPadding(
        maxShadowSize: Float,
        cornerRadius: Float,
        addPaddingForCorners: Boolean
    ): Float = if (addPaddingForCorners) {
        (maxShadowSize + (1 - COS_45) * cornerRadius).toFloat()
    } else {
        maxShadowSize
    }
}
