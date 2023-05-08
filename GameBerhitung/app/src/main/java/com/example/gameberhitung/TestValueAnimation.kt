package com.example.gameberhitung

import android.animation.Animator
import android.animation.ValueAnimator
import android.os.Bundle
import android.util.TypedValue
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class TestValueAnimation : AppCompatActivity() {

    private lateinit var numberTextView: TextView
    private lateinit var btnTest: Button
//    private late-init var btnTimer: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.test_value_animation)

        numberTextView = findViewById(R.id.textView)
        btnTest = findViewById(R.id.btnTest1)

        // NUMBER ANIMATION
        val valueAnimator = ValueAnimator.ofInt(0, 100)
        valueAnimator.duration = 2000

        val initialFontSize = numberTextView.textSize // Store the initial font size

        valueAnimator.addUpdateListener { animator ->
            val animatedValue = animator.animatedValue as Int
            numberTextView.text = animatedValue.toString()

            val scale = animatedValue.toFloat() / 100.0f

            // Decrease the font size after reaching halfway point of the animation
            val fontSize: Float = if (scale >= 0.5f) {
                val scaleDown = (1.0f - scale) * 2.0f
                initialFontSize + (scaleDown * 25.0f)
            } else {
                initialFontSize + (scale * 25.0f)
            }

            numberTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize)
        }

        valueAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                // Animation started, increase the font size
                numberTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, initialFontSize + 20.0f)
            }

            override fun onAnimationEnd(animation: Animator) {
                // Animation ended, revert the font size back to normal
                numberTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, initialFontSize)
            }

            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
        //

        btnTest.setOnClickListener {
            valueAnimator.start()
        }
    }
}