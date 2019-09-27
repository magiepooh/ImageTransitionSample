package com.github.magiepooh.imagetransitionsample

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnPreDraw
import androidx.databinding.DataBindingUtil
import com.github.magiepooh.imagetransitionsample.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    companion object {
        fun createIntent(context: Context) = Intent(context, DetailActivity::class.java)

        const val TRANSITION_NAME_IMAGE = "image"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.sharedElementEnterTransition = RoundedImageTransitionSet()
        val binding = DataBindingUtil.setContentView<ActivityDetailBinding>(this, R.layout.activity_detail)

        supportPostponeEnterTransition()

        binding.header.transitionName = TRANSITION_NAME_IMAGE

        binding.header.doOnPreDraw {
            supportStartPostponedEnterTransition()
        }
    }
}
