package com.example.gameberhitung

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import java.util.Locale

class Shop : AppCompatActivity() {

    private lateinit var buyButtons: Array<TextView>
    private lateinit var closeButton: Button
    private lateinit var shopText: TextView
    private lateinit var koinKu: TextView
    private var coinKu: Int = 0

    @SuppressLint("DiscouragedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.shop_screen)

        shopText = findViewById(R.id.shopText)
        koinKu = findViewById(R.id.uangkoin)
        closeButton = findViewById(R.id.buttonClose)
        closeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra(MainActivity.getData, coinKu)
            }
            startActivity(intent)
        }

        getCoins()

        buyButtons = Array(3) { index ->
            findViewById<TextView>(resources.getIdentifier("square${index+4}", "id", packageName)).apply {
                setOnClickListener {
                    shopText.visibility = View.INVISIBLE
                    showConfirmationDialog(text.toString().replace(" $", ""), tag.toString())
                }
            }
        }

//        resetInventory()

        checkInventory()
    }

    private fun getCoins() {
        val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)

        val editorStart = sharedPreferences.edit()
        if (!sharedPreferences.contains("coins") || sharedPreferences.all["coins"] !is String) {
            editorStart.clear()
            editorStart.putString("coins", "0")
            editorStart.apply()
        }

        val currentCoins = sharedPreferences.getString("coins", "")?.toIntOrNull() ?: 0

        val txtCoin : TextView = findViewById(R.id.uangkoin)
        txtCoin.text = currentCoins.toString()
    }

    private fun showConfirmationDialog(cost: String, btnId: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirm Purchase")
        builder.setMessage("Are you sure you want to proceed with the purchase? -$cost coins")
        builder.setPositiveButton("Yes") { _, _ ->
            val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
            val currentCoins = sharedPreferences.getString("coins", "")?.toIntOrNull() ?: 0

            if (currentCoins < cost.toInt()) {
                shopText.visibility = View.VISIBLE
            } else {
                performPurchase(cost, btnId)
            }
        }
        builder.setNegativeButton("No", null)
        builder.create().show()
    }

    @SuppressLint("SetTextI18n")
    private fun performPurchase(cost: String, btnId: String) {
        // UPDATE INVENTORY
        val shopPreferences = getSharedPreferences("ShopPrefs", Context.MODE_PRIVATE)
        val editor = shopPreferences.edit()
        editor.putBoolean(btnId, true)
        editor.apply()

        // UPDATE COINS
        val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val currentCoins = sharedPreferences.getString("coins", "")?.toIntOrNull() ?: 0
        val totalCoins = currentCoins - cost.toInt()
        val editorEnd = sharedPreferences.edit()
        editorEnd.putString("coins", totalCoins.toString())
        editorEnd.apply()
        coinKu = totalCoins

        // UPDATE EQUIPPED BUTTON
        val editorEquip = sharedPreferences.edit()
        editorEquip.putString("equipment", btnId)
        editorEquip.apply()
        checkInventory()

        // UPDATE THE INFO TEXT
        val buttonName = sharedPreferences.getString("equipment", "")
            ?.let { convertUnderscoreToTitle(it) }
        shopText.text = "Purchase successful! \n" +
                "You have bought $buttonName for $cost coins."
        shopText.setTextColor(Color.GREEN)
        shopText.visibility = View.VISIBLE
        numberAnimation(koinKu, currentCoins, totalCoins)
    }

    @SuppressLint("SetTextI18n")
    private fun checkInventory() {
        val shopPreferences = getSharedPreferences("ShopPrefs", Context.MODE_PRIVATE)
        val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)

        for (custom_button in buyButtons) {
            val cButton = custom_button.tag.toString()
            if (shopPreferences.getBoolean(cButton, false)) { // If the button has been bought
                val equippedButton = sharedPreferences.getString("equipment", "")
                if (equippedButton == cButton) { // If the button is equipped
                    custom_button.text = "EQUIPPED"
                    custom_button.setOnClickListener { unequip() }
                } else { // If the button is unequipped
                    custom_button.text = "PURCHASED"
                    custom_button.setOnClickListener { equip(custom_button) }
                }
            } else { // If the button hasn't been bought
                val cost = custom_button.text.toString().replace(" $", "")
                val btnId = custom_button.tag.toString()
                custom_button.setOnClickListener {
                    shopText.visibility = View.INVISIBLE
                    showConfirmationDialog(cost, btnId)
                }
            }
        }
    }

    private fun equip(button: TextView) {
        // FIND EQUIPPED BUTTON IN INVENTORY, THEN UNEQUIPPED IT
        var equippedButton: TextView? = null
        val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val eButton = sharedPreferences.getString("equipment", "")

        for (bButton in buyButtons) {
            if (bButton.tag.toString() == eButton) {
                equippedButton = bButton
            }
        }
        if (equippedButton != null) {
            unequip()
        } else {
            println("equippedButton is null!")
        }

        // EQUIP THE NEW BUTTON
        val editorEnd = sharedPreferences.edit()
        editorEnd.putString("equipment", button.tag.toString())
        editorEnd.apply()

        val shopPreferences = getSharedPreferences("ShopPrefs", Context.MODE_PRIVATE)
        val editorInventory = shopPreferences.edit()
        editorInventory.putBoolean(button.tag.toString(), true)
        editorInventory.apply()

        shopText.visibility = View.INVISIBLE
        checkInventory()
    }

    private fun unequip() {
        val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val editorEnd = sharedPreferences.edit()
        editorEnd.putString("equipment", "default")
        editorEnd.apply()

        shopText.visibility = View.INVISIBLE
        checkInventory()
    }

    private fun convertUnderscoreToTitle(input: String): String {
        val words = input.split("_")
        return words.joinToString(" ") { it ->
            it.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(
                Locale.ROOT
            ) else it.toString()
        } }
    }

    private fun numberAnimation(textViewKu: TextView, start: Int, end: Int) {
        // NUMBER ANIMATION
        val valueAnimator = ValueAnimator.ofInt(start, end)
        valueAnimator.duration = 2000

        val initialFontSize = textViewKu.textSize // Store the initial font size

        valueAnimator.addUpdateListener { animator ->
            val animatedValue = animator.animatedValue as Int
            textViewKu.text = animatedValue.toString()

            val scale = animatedValue.toFloat() / 100.0f

            // Decrease the font size after reaching halfway point of the animation
            val fontSize: Float = if (scale >= 0.5f) {
                val scaleDown = (1.0f - scale) * 2.0f
                initialFontSize + (scaleDown * 5.0f)
            } else {
                initialFontSize + (scale * 5.0f)
            }

            textViewKu.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize)
        }

        valueAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                // Animation started, increase the font size
                textViewKu.setTextSize(TypedValue.COMPLEX_UNIT_PX, initialFontSize + 20.0f)
            }

            override fun onAnimationEnd(animation: Animator) {
                // Animation ended, revert the font size back to normal
                textViewKu.setTextSize(TypedValue.COMPLEX_UNIT_PX, initialFontSize)
            }

            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })

        valueAnimator.start()
    }

//    private fun resetInventory() {
//        val shopPreferences = getSharedPreferences("ShopPrefs", Context.MODE_PRIVATE)
//        val editorInventory = shopPreferences.edit()
//        for (custom_button in buyButtons) {
//            editorInventory.putBoolean(custom_button.tag.toString(), false)
//        }
//        editorInventory.apply()
//    }
}