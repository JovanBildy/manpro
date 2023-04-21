package com.example.gameberhitung

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity2 : AppCompatActivity() {

    private lateinit var buttons: Array<Button>
    private lateinit var infoText: TextView
    private var visibleBtn = mutableListOf<Int>()
    private var chosenBtnText = mutableListOf<Int>()
    private var chosenBtnId = mutableListOf<Int>()
    private var questionShown = 0

    @SuppressLint("DiscouragedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        infoText = findViewById(R.id.infoText)

        buttons = Array(32) { index ->
            findViewById<Button>(resources.getIdentifier("button${index+1}", "id",
                packageName)).apply {
                setOnClickListener {
                    // apply onClickListener on all buttons in array
                }
            }
        }

        buttons.forEach { button ->
            button.visibility = View.INVISIBLE
        }

        generateQuestion()

        val handler = Handler()
        val runnable = object : Runnable {
            override fun run() {
                if (questionShown <= 5) generateQuestion()
                handler.postDelayed(this, 3000) // 5 seconds delay
            }
        }
        handler.post(runnable)
    }

    private fun generateQuestion() {
        questionShown += 1

        val question = IntArray(3)
        question[0] = (1..10).random()
        question[1] = (1..10).random()
        question[2] = question[0] + question[1]

        for (i in 0..2) {
            var randomNumber = (0..31).random()
            while (visibleBtn.contains(randomNumber)) {
                randomNumber = (0..31).random()
            }
            visibleBtn.add(randomNumber)

            when (i) {
                0 -> showButton(randomNumber, question[0], "Question")
                1 -> showButton(randomNumber, question[1], "Question")
                2 -> showButton(randomNumber, question[2], "Answer")
            }
        }
    }

    private fun showButton(id: Int, number: Int, type: String) {
        buttons[id].text = number.toString()
        buttons[id].visibility = View.VISIBLE

        if (type == "Question") {
            buttons[id].setBackgroundColor(Color.RED)

            buttons[id].setOnClickListener {
                buttons[id].setBackgroundColor(Color.GRAY)
                chosenBtnText.add(buttons[id].text.toString().toInt())
                chosenBtnId.add(id)
                if (infoText.text.isNullOrEmpty()) {
                    infoText.append(buttons[id].text.toString())
                } else if (infoText.text.toString() == resources.getString(R.string.status_incorrect) || infoText.text.toString() == resources.getString(R.string.status_correct) || infoText.text.toString() == resources.getString(R.string.status_empty)) {
                    infoText.text = buttons[id].text.toString()
                } else {
                    infoText.append(" + " + buttons[id].text.toString())
                }
            }
        } else if (type == "Answer") {
            buttons[id].setBackgroundColor(Color.BLUE)

            buttons[id].setOnClickListener {

                if (chosenBtnText.isEmpty()) {
                    infoText.text = resources.getString(R.string.status_empty)
                } else {
                    if (chosenBtnText.sum() == buttons[id].text.toString().toInt()) { // CORRECT
                        infoText.text = resources.getString(R.string.status_correct)

                        // REMOVE BUTTONS FROM SCREEN
                        for (button_id in 0 until chosenBtnId.size) {
                            hideButton(chosenBtnId[button_id])
                        }
                        hideButton(id)

                        questionShown -= 1
                    } else { // INCORRECT
                        infoText.text = resources.getString(R.string.status_incorrect)

                        // RESET BUTTONS' STATE
                        for (button_id in 0 until chosenBtnId.size) {
                            buttons[chosenBtnId[button_id]].setBackgroundColor(Color.RED)
                        }
                    }
                    chosenBtnText.clear()
                    chosenBtnId.clear()
                }
            }
        }
    }

    private fun hideButton(id: Int) {
        buttons[id].visibility = View.INVISIBLE
        buttons[id].text = ""
    }
}