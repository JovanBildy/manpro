package com.example.gameberhitung

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btn1: Button = findViewById(R.id.button6)
        val btn2: Button = findViewById(R.id.button)
        val btn3: Button = findViewById(R.id.button34)

        btn1.setOnClickListener {
            val intent = Intent(this@MainActivity, MainActivity2::class.java)
            startActivity(intent)
        }

        btn2.setOnClickListener {
            val intent = Intent(this@MainActivity, TestValueAnimation::class.java)
            startActivity(intent)
        }

        btn3.setOnClickListener {
            val intent = Intent(this@MainActivity, TestBtnTimer::class.java)
            startActivity(intent)
        }
    }
}