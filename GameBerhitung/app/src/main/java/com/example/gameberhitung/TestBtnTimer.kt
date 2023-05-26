package com.example.gameberhitung

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton

class TestBtnTimer : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.test_btn_timer)

//        val frameLayout = findViewById<FrameLayout>(R.id.frameLayout1)
        val imageButton = findViewById<ImageButton>(R.id.imageButton1)
        val button = findViewById<Button>(R.id.button1)

        button.setOnClickListener {
            // Perform any desired actions when the FrameLayout is clicked
            // For example, change the ImageButton source or background

            // Change the ImageButton source to a different image
            imageButton.setImageResource(R.drawable.button1_grey)

            // Change the ImageButton background
            imageButton.setBackgroundResource(R.drawable.button1_grey)
        }


    }
}