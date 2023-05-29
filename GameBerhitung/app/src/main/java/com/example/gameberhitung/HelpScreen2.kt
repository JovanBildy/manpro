package com.example.gameberhitung

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class HelpScreen2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help_screen2)

        val _buttonNext : Button = findViewById(R.id.buttonSelanjutnya2)

        _buttonNext.setOnClickListener{
            val intent = Intent(this@HelpScreen2, HelpScreen3::class.java)
            startActivity(intent)
        }
    }
}