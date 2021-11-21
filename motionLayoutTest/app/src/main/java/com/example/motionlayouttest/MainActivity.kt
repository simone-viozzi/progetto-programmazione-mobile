package com.example.motionlayouttest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.databinding.DataBindingUtil
import com.example.motionlayouttest.databinding.ActivityMainBinding
import timber.log.Timber
import kotlin.random.Random

class MainActivity : AppCompatActivity()
{
    lateinit var binding: ActivityMainBinding

    private var state: Int = 0

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.materialButton.setOnClickListener {

            if (binding.textView.visibility == View.VISIBLE)
            {
                binding.textView.visibility = View.GONE
            }
            else
            {
                binding.textView.visibility = View.VISIBLE
            }
        }

        binding.materialButton1.setOnClickListener {

            if (binding.textView.visibility == View.VISIBLE)
            {
                binding.textView.visibility = View.GONE
                with(binding.constraintLayout)
                {
                    setTransition(R.id.start, R.id.end)
                    transitionToState(R.id.end)
                }

            }
            else
            {
                binding.textView.visibility = View.VISIBLE
                with(binding.constraintLayout)
                {
                    setTransition(R.id.end, R.id.start)
                    transitionToState(R.id.start)
                }
            }
        }

        binding.button2.setOnClickListener {
            if (state == 0)
            {
                with(binding.constraintLayout)
                {
                    setTransition(R.id.imgStart, R.id.imgEnd)
                    transitionToState(R.id.imgEnd)
                }
                state = 1
            }
            else
            {
                with(binding.constraintLayout)
                {
                    setTransition(R.id.imgEnd, R.id.imgStart)
                    transitionToState(R.id.imgStart)
                }
                state = 0
            }
        }

        binding.imageView.setOnClickListener {
            val r = Random.nextInt(255)
            val g = Random.nextInt(255)
            val b = Random.nextInt(255)
            binding.imageView.setColorFilter(Color(r, g, b).toArgb())
        }

        binding.frameLayout.setOnClickListener {
            Toast.makeText(this, "frame Layout Click", Toast.LENGTH_SHORT).show()
        }
    }
}