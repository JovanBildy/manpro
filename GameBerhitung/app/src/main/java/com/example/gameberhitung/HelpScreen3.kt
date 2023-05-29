package com.example.gameberhitung

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class HelpScreen3 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help_screen3)

        val _buttonNext : Button = findViewById(R.id.buttonSelanjutnya3)

        _buttonNext.setOnClickListener{
            val intent = Intent(this@HelpScreen3, MainActivity::class.java)
            startActivity(intent)
        }
    }
}