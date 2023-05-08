package com.example.gameberhitung

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.ProgressBar

class TestBtnTimer : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.test_btn_timer)

        val progressBar: ProgressBar = findViewById(R.id.progressBar)
        val myButton: Button = findViewById(R.id.button33)

        val totalDuration = 10000L // Total duration in milliseconds (10 secs)
        val updateInterval = 100L // Update interval in milliseconds (100 milli-secs)

        val countDownTimer = object : CountDownTimer(totalDuration, updateInterval) {
            override fun onTick(millisUntilFinished: Long) {
                val progress = (millisUntilFinished.toFloat() / totalDuration * 100).toInt()
                progressBar.progress = progress
            }

            override fun onFinish() {
                progressBar.progress = 0
            }
        }

        myButton.setOnClickListener {
            countDownTimer.start()
        }

    }
}