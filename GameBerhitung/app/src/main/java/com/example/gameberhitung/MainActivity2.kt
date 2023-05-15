package com.example.gameberhitung

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo

@Suppress("DEPRECATION")
class MainActivity2 : AppCompatActivity() {

    private lateinit var buttons: Array<Button>
    private lateinit var timers: Array<ProgressBar>
    private lateinit var countDownTimers: Array<CountDownTimer>
    private lateinit var infoText: TextView
    private lateinit var coinText: TextView

    private var visibleBtn = mutableListOf<Int>()
    private var chosenBtnText = mutableListOf<Int>()
    private var chosenBtnId = mutableListOf<Int>()

    private var coins = 0
    private var gameOver = false

    // TIMER VARIABLES
    private var totalDuration: Double = 30000.00 // Total duration in milliseconds (30 secs)
    private val updateInterval = 100L // Update interval in milliseconds (100 milli-secs)

    // SPAWN RATE VARIABLES
    private var questionShown = 0
    private var answered = 0
    private var spawnRate: Double = 5000.00 // Initial spawn rate 5 seconds

    @SuppressLint("DiscouragedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        infoText = findViewById(R.id.infoText)
        coinText = findViewById(R.id.coinText)

        // TIMERS
        timers = Array(32) { index ->
            findViewById(resources.getIdentifier("progressBar${index + 1}", "id",
                packageName))
        }
        countDownTimers = Array(timers.size) { index ->
            object : CountDownTimer(totalDuration.toLong(), updateInterval) {
                override fun onTick(millisUntilFinished: Long) {
                    val progress = (millisUntilFinished.toFloat() / totalDuration * 100).toInt()
                    timers[index].progress = progress
                }

                override fun onFinish() {
                    timers[index].progress = 0
                    if (timers[index].visibility == View.VISIBLE) {
                        gameOver = true
                    }
                }
            }
        }
        timers.forEach { timer ->
            timer.visibility = View.INVISIBLE
        }
        //

        buttons = Array(32) { index ->
            findViewById<Button>(resources.getIdentifier("button${index+1}", "id",
                packageName)).apply {
                setOnClickListener {
                    // Apply onClickListener on all buttons in array
                }
            }
        }
        buttons.forEach { button ->
            button.visibility = View.INVISIBLE
        }

        // SPAWN RATE
        val handler = Handler()
        val runnable = object : Runnable {
            override fun run() {
                if (questionShown <= 5) {
                    generateQuestion()
                    handler.postDelayed(this, spawnRate.toLong())
                } else {
                    if (gameOver) {
                        timers.forEach { timer ->
                            timer.visibility = View.INVISIBLE
                        }
                        buttons.forEach { button ->
                            button.visibility = View.INVISIBLE
                        }
                    } else {
                        handler.postDelayed(this, 1000)
                    }
                }
            }
        }
        handler.post(runnable)
        //
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

    @SuppressLint("SetTextI18n")
    private fun showButton(id: Int, number: Int, type: String) {
        buttons[id].text = number.toString()
        buttons[id].visibility = View.VISIBLE

        if (type == "Question") {
            buttons[id].setBackgroundColor(Color.RED)

            buttons[id].setOnClickListener {
                buttons[id].setBackgroundColor(Color.GRAY)
                buttons[id].isEnabled = false
                // Click animation
                YoYo.with(Techniques.BounceIn)
                    .duration(700)
                    .repeat(0)
                    .playOn(buttons[id])
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

            timers[id].visibility = View.VISIBLE
            countDownTimers[id].start()

            buttons[id].setOnClickListener {
                // Click animation
                YoYo.with(Techniques.BounceIn)
                    .duration(700)
                    .repeat(0)
                    .playOn(buttons[id])

                if (chosenBtnText.isEmpty()) {
                    infoText.text = resources.getString(R.string.status_empty)
                    // Incorrect text animation
                    YoYo.with(Techniques.Tada)
                        .duration(700)
                        .repeat(0)
                        .playOn(infoText)
                } else {
                    if (chosenBtnText.sum() == buttons[id].text.toString().toInt()) { // CORRECT
                        infoText.text = resources.getString(R.string.status_correct)
                        // Correct text animation
                        YoYo.with(Techniques.RubberBand)
                            .duration(700)
                            .repeat(0)
                            .playOn(infoText)

                        // Remove all buttons from the screen
                        for (button_id in 0 until chosenBtnId.size) {
                            //click animation
                            YoYo.with(Techniques.FadeOut)
                                .duration(700)
                                .repeat(0)
                                .playOn(buttons[id])

                            hideButton(chosenBtnId[button_id])
                        }
                        hideButton(id)

                        coins += 10
                        coinText.text = coins.toString()
                        questionShown -= 1
                        answered += 1

                        // If milestone is reached -> increase spawn rate
                        if (answered % 10 == 0 && answered > 0 && spawnRate >= 1000) {
                            // Spawn rate reduced by 10% every 10 questions answered
                            spawnRate *= 0.9
                            totalDuration *= 0.9
                        }
                    } else { // INCORRECT
                        infoText.text = resources.getString(R.string.status_incorrect)
                        // Incorrect text animation
                        YoYo.with(Techniques.Tada)
                            .duration(700)
                            .repeat(0)
                            .playOn(infoText)

                        // Reset buttons' state
                        for (button_id in 0 until chosenBtnId.size) {
                            buttons[chosenBtnId[button_id]].setBackgroundColor(Color.RED)
                            buttons[chosenBtnId[button_id]].isEnabled = true
                        }
                    }
                    chosenBtnText.clear()
                    chosenBtnId.clear()
                }
            }
        }
    }

    private fun hideButton(id: Int) {
        timers[id].visibility = View.INVISIBLE
        countDownTimers[id].cancel()

        buttons[id].visibility = View.INVISIBLE
        buttons[id].text = ""
    }
}