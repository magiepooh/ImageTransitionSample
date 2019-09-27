package com.github.magiepooh.imagetransitionsample

import android.transition.*

class RoundedImageTransitionSet : TransitionSet() {
    init {
        addTransition(ChangeBounds())
        addTransition(ChangeTransform())
        addTransition(ChangeImageTransform())
        addTransition(ChangeClipBounds())
        addTransition(RoundedImageTransition().addTarget(R.id.header))
    }
}