package com.example.gameberhitung

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
//import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val clickAdd: ImageView = findViewById(R.id.button_add)
        val clickMin : ImageView = findViewById(R.id.button_min)
        val clickTimes : ImageView = findViewById(R.id.button_times)
        val clickDivide : ImageView = findViewById(R.id.button_divide)
        val clickCoinShop : LinearLayout = findViewById(R.id.coinShop)
        val txtCoin : TextView = findViewById(R.id.textCoin)

        // SHARED PREFERENCES
        val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)

        val editorStart = sharedPreferences.edit()
        if (!sharedPreferences.contains("coins") || sharedPreferences.all["coins"] !is String) {
            editorStart.clear()
            editorStart.putString("coins", "0")
            editorStart.apply()
        }

        if (!sharedPreferences.contains("equipment") || sharedPreferences.all["equipment"] !is String) {
            editorStart.clear()
            editorStart.putString("equipment", "default")
            editorStart.apply()
        }

        val currentCoins = sharedPreferences.getString("coins", "")?.toIntOrNull() ?: 0
        val coinsEarned = intent.getStringExtra(getData)?.toIntOrNull() ?: 0

        val totalCoins = currentCoins + coinsEarned

//        Log.d("cek123", "currentCoins: $currentCoins")
//        Log.d("cek123", "coinsEarned: $coinsEarned")

        txtCoin.text = totalCoins.toString()

        val editorEnd = sharedPreferences.edit()
        editorEnd.putString("coins", totalCoins.toString())
        editorEnd.apply()

        clickAdd.setOnClickListener {
            val intent = Intent(this@MainActivity, Penjumlahan::class.java).apply {
                putExtra(Penjumlahan.getCoins, totalCoins.toString())
            }
            startActivity(intent)
        }

        clickMin.setOnClickListener {
            val intent = Intent(this@MainActivity, Pengurangan::class.java).apply {
                putExtra(Penjumlahan.getCoins, totalCoins.toString())
            }
            startActivity(intent)
        }

        clickTimes.setOnClickListener {
            val intent = Intent(this@MainActivity, Perkalian::class.java).apply {
                putExtra(Penjumlahan.getCoins, totalCoins.toString())
            }
            startActivity(intent)
        }

        clickDivide.setOnClickListener {
            val intent = Intent(this@MainActivity, Pembagian::class.java).apply {
                putExtra(Penjumlahan.getCoins, totalCoins.toString())
            }
            startActivity(intent)
        }

        clickCoinShop.setOnClickListener {
            val intent = Intent(this@MainActivity, Shop::class.java)
            startActivity(intent)
        }
    }

    companion object {
        const val getData = "Get coins earned after the game is over."
    }
}
