package com.example.gameberhitung

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
//import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var intentKu: Intent

    private var totalCoins: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val clickAdd: ImageView = findViewById(R.id.button_add)
        val clickMin : ImageView = findViewById(R.id.button_min)
        val clickTimes : ImageView = findViewById(R.id.button_times)
        val clickDivide : ImageView = findViewById(R.id.button_divide)
        val clickCoinShop : LinearLayout = findViewById(R.id.coinShop)
        val txtCoin : TextView = findViewById(R.id.textCoin)

        sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)

        val editorStart = sharedPreferences.edit()
        if (!sharedPreferences.contains("coins")
            || sharedPreferences.all["coins"] !is String) {
            editorStart.clear()
            editorStart.putString("coins", "0")
            editorStart.apply()
        }
        if (!sharedPreferences.contains("equipment")
            || sharedPreferences.all["equipment"] !is String) {
            editorStart.clear()
            editorStart.putString("equipment", "default")
            editorStart.apply()
        }

        val currentCoins = sharedPreferences.getString("coins", "")?.toIntOrNull() ?: 0
        val coinsEarned = intent.getStringExtra(getData)?.toIntOrNull() ?: 0
        totalCoins = currentCoins + coinsEarned
        txtCoin.text = totalCoins.toString()

        val editorEnd = sharedPreferences.edit()
        editorEnd.putString("coins", totalCoins.toString())
        editorEnd.apply()

        clickAdd.setOnClickListener { goToGameScreen("penjumlahan") }

        clickMin.setOnClickListener { goToGameScreen("pengurangan") }

        clickTimes.setOnClickListener { goToGameScreen("perkalian") }

        clickDivide.setOnClickListener { goToGameScreen("pembagian") }

        clickCoinShop.setOnClickListener {
            val intent = Intent(this@MainActivity, Shop::class.java)
            startActivity(intent)
        }
    }

    private fun goToGameScreen(gameMode: String) {
        intentKu = if (sharedPreferences.getString("equipment", "") != "default") {
            Intent(this@MainActivity, GameScreen2::class.java).apply {
                putExtra(GameScreen.getCoins, totalCoins.toString())
                putExtra(GameScreen.getGameMode, gameMode)
            }
        } else {
            Intent(this@MainActivity, GameScreen::class.java).apply {
                putExtra(GameScreen.getCoins, totalCoins.toString())
                putExtra(GameScreen.getGameMode, gameMode)
            }
        }

        startActivity(intentKu)
    }

    companion object {
        const val getData = "Get coins earned after the game is over."
    }
}
