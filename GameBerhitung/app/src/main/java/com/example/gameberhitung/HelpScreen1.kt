package com.example.gameberhitung

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class HelpScreen1 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help_screen1)

        val _buttonNext : Button = findViewById(R.id.buttonSelanjutnya1)

        _buttonNext.setOnClickListener{
            val intent = Intent(this@HelpScreen1, HelpScreen2::class.java)
            startActivity(intent)
        }
    }
}