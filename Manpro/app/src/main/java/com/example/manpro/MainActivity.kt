package com.example.manpro

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val _play = findViewById<ImageView>(R.id.playbutton)


        _play.setOnClickListener {
            val sendIntent = Intent(this@MainActivity,Mode::class.java).apply {
            }
            startActivity(sendIntent)
        }
    }
}