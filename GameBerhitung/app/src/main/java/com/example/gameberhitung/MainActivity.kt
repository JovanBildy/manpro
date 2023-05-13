package com.example.gameberhitung

import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val clickAdd: ImageView = findViewById(R.id.button_add)
        val clickMin : ImageView = findViewById(R.id.button_min)
        val clickTimes : ImageView = findViewById(R.id.button_times)
        val clickDivide : ImageView = findViewById(R.id.button_divide)
        val clickCoinShop : LinearLayout = findViewById(R.id.coinShop)
//        val btn2: Button = findViewById(R.id.button)
//        val btn3: Button = findViewById(R.id.button34)

        clickAdd.setOnClickListener {
            val intent = Intent(this@MainActivity, MainActivity2::class.java)
            startActivity(intent)
        }

        clickMin.setOnClickListener {
            val intent = Intent(this@MainActivity, MainActivity3::class.java)
            startActivity(intent)
        }

        clickTimes.setOnClickListener {
            val intent = Intent(this@MainActivity, MainActivity4::class.java)
            startActivity(intent)
        }

        clickDivide.setOnClickListener {
            val intent = Intent(this@MainActivity, MainActivity5::class.java)
            startActivity(intent)
        }

        clickCoinShop.setOnClickListener {
            val intent = Intent(this@MainActivity, MainActivity6::class.java)
            startActivity(intent)
        }
//        btn2.setOnClickListener {
//            val intent = Intent(this@MainActivity, TestValueAnimation::class.java)
//            startActivity(intent)
//        }

//        btn3.setOnClickListener {
//            val intent = Intent(this@MainActivity, TestBtnTimer::class.java)
//            startActivity(intent)
//        }
    }
}