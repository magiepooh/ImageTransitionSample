package com.github.magiepooh.imagetransitionsample

import android.app.ActivityOptions
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.github.magiepooh.imagetransitionsample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        binding.image1.setOnClickListener {
            val elements = mutableListOf<android.util.Pair<View, String>>().apply {
                add(android.util.Pair(binding.image1, DetailActivity.TRANSITION_NAME_IMAGE))
            }
            startActivity(
                DetailActivity.createIntent(this),
                ActivityOptions.makeSceneTransitionAnimation(this, *elements.toTypedArray()).toBundle()
            )
        }
    }
}
