package com.example.gameberhitung

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import java.util.Locale

class Shop : AppCompatActivity() {

    private lateinit var buyButtons: Array<TextView>
    private lateinit var closeButton: Button
    private lateinit var shopText: TextView
    private var coinKu: Int = 0

    // Temporary variables, will be replaced with shared preferences later
//    private var coins = 300
//    private lateinit var inventory: MutableMap<String, Boolean>
//    private var equipped = "default"

    @SuppressLint("DiscouragedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.shop_screen)

        // Temporary variables
//        inventory = mutableMapOf(
//            "button_es" to false,
//            "button_sedang" to false,
//            "button_mahal" to false
//        )

        shopText = findViewById(R.id.shopText)
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

//        checkInventory()
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

//        txtCoin.text = coins.toString()
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

//            if (coins < cost.toInt()) {
//                shopText.visibility = View.VISIBLE
//            } else {
//                performPurchase(cost, btnId)
//            }
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

//        inventory[btnId] = true

        // UPDATE COINS
        val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val currentCoins = sharedPreferences.getString("coins", "")?.toIntOrNull() ?: 0
        val totalCoins = currentCoins - cost.toInt()
        val editorEnd = sharedPreferences.edit()
        editorEnd.putString("coins", totalCoins.toString())
        editorEnd.apply()
        coinKu = totalCoins

//        coins -= cost.toInt()

        // UPDATE EQUIPPED BUTTON
        val editorEquip = sharedPreferences.edit()
        editorEquip.putString("equipment", btnId)
        editorEquip.apply()

//        equipped = btnId

        checkInventory()

        // UPDATE THE INFO TEXT
//        val buttonName = convertUnderscoreToTitle(equipped)

        val buttonName = sharedPreferences.getString("equipment", "")
            ?.let { convertUnderscoreToTitle(it) }
        shopText.text = "Purchase successful! \n" +
                "You have bought $buttonName for $cost coins."
        shopText.setTextColor(Color.GREEN)
        shopText.visibility = View.VISIBLE
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
                    custom_button.setOnClickListener { unequip(custom_button) }
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

//            if (inventory[cButton] == true) {
//                if (equipped == cButton) {
//                    custom_button.text = "EQUIPPED"
//                    custom_button.setOnClickListener { unequip(custom_button) }
//                } else {
//                    custom_button.text = "PURCHASED"
//                    custom_button.setOnClickListener { equip(custom_button) }
//                }
//            } else {
//                val cost = custom_button.text.toString().replace(" $", "")
//                val btnId = custom_button.tag.toString()
//                custom_button.setOnClickListener {
//                    showConfirmationDialog(cost, btnId)
//                    shopText.visibility = View.INVISIBLE
//                }
//            }
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

//            if (bButton.tag.toString() == equipped) {
//                equippedButton = bButton
//            }
        }
        if (equippedButton != null) {
            unequip(equippedButton)
        } else {
            println("equippedButton is null!")
        }

        // EQUIP THE NEW BUTTON
//        equipped = button.tag.toString()
//        inventory[button.tag.toString()] = true

        val editorEnd = sharedPreferences.edit()
        editorEnd.putString("equipment", button.tag.toString())
        editorEnd.apply()

        val shopPreferences = getSharedPreferences("ShopPrefs", Context.MODE_PRIVATE)
        val editorInventory = shopPreferences.edit()
        editorInventory.putBoolean(button.tag.toString(), true)
        editorInventory.apply()

        shopText.visibility = View.INVISIBLE
    }

    private fun unequip(button: TextView) {
//        equipped = "default"
//        inventory[button.tag.toString()] = false

        val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val editorEnd = sharedPreferences.edit()
        editorEnd.putString("equipment", "default")
        editorEnd.apply()

        val shopPreferences = getSharedPreferences("ShopPrefs", Context.MODE_PRIVATE)
        val editorInventory = shopPreferences.edit()
        editorInventory.putBoolean(button.tag.toString(), false)
        editorInventory.apply()

        shopText.visibility = View.INVISIBLE
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

}