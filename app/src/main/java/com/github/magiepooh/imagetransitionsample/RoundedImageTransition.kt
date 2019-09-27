package com.github.magiepooh.imagetransitionsample

import android.animation.Animator
import android.animation.ValueAnimator
import android.transition.Transition
import android.transition.TransitionValues
import android.util.Log
import android.view.ViewGroup

class RoundedImageTransition : Transition() {

    companion object {
        private const val KEY_TOP_LEFT_RADIUS =
            "com.github.magiepooh.imagetransitionsample:RoundedImageTransition:topleft"
        private const val KEY_TOP_RIGHT_RADIUS =
            "com.github.magiepooh.imagetransitionsample:RoundedImageTransition:topright"
        private const val KEY_BOTTOM_LEFT_RADIUS =
            "com.github.magiepooh.imagetransitionsample:RoundedImageTransition:bottomleft"
        private const val KEY_BOTTOM_RIGHT_RADIUS =
            "com.github.magiepooh.imagetransitionsample:RoundedImageTransition:bottomright"
    }

    override fun captureStartValues(transitionValues: TransitionValues?) {
        val view = transitionValues?.view as? RoundedImageView
        transitionValues?.values?.run {
            put(KEY_TOP_LEFT_RADIUS, view?.topLeftRadius)
            put(KEY_TOP_RIGHT_RADIUS, view?.topRightRadius)
            put(KEY_BOTTOM_LEFT_RADIUS, view?.bottomLeftRadius)
            put(KEY_BOTTOM_RIGHT_RADIUS, view?.bottomRightRadius)
        }
    }

    override fun captureEndValues(transitionValues: TransitionValues?) {
        // no op
    }

    override fun createAnimator(
        sceneRoot: ViewGroup?,
        startValues: TransitionValues?,
        endValues: TransitionValues?
    ): Animator? {

        startValues ?: return null
        val view = startValues.view as? RoundedImageView ?: return null


        val topLeftRadius = startValues.values[KEY_TOP_LEFT_RADIUS] as? Int ?: return null
        val topRightRadius = startValues.values[KEY_TOP_RIGHT_RADIUS] as? Int ?: return null
        val bottomLeftRadius = startValues.values[KEY_BOTTOM_LEFT_RADIUS] as? Int ?: return null
        val bottomRightRadius = startValues.values[KEY_BOTTOM_RIGHT_RADIUS] as? Int ?: return null

        return ValueAnimator.ofFloat(1f, 0f).apply {
            addUpdateListener {
                val fraction = it.animatedValue as Float
                view.setRadius(
                    (topLeftRadius * fraction).toInt(),
                    (topRightRadius * fraction).toInt(),
                    (bottomLeftRadius * fraction).toInt(),
                    (bottomRightRadius * fraction).toInt()
                )
            }
        }
    }

    private fun log(text: String) {
        Log.v(RoundedImageTransition::class.java.simpleName, text)
    }
}