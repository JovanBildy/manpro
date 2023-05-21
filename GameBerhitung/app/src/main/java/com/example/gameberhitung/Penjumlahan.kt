package com.example.gameberhitung

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.TypedValue
//import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo

@Suppress("DEPRECATION")
class Penjumlahan : AppCompatActivity() {

    private lateinit var buttons: Array<Button>
    private lateinit var timers: Array<ProgressBar>
    private lateinit var countDownTimers: Array<CountDownTimer>
    private lateinit var infoText: TextView
    private lateinit var coinText: TextView

    private var visibleBtn = mutableListOf<Int>()
    private var chosenBtnText = mutableListOf<Int>()
    private var chosenBtnId = mutableListOf<Int>()

    private var coinsEarned = 0
    private var coinsMainMenu = 0
    private var gameOver = false

    // NEW MECHANIC
    private val buttonQuestions = mutableListOf<Int>()

    // TIMER VARIABLES
    private var totalDuration: Double = 30000.00 // Total duration in milliseconds (30 secs)
    private val updateInterval = 100L // Update interval in milliseconds (100 milli-secs)

    // SPAWN RATE VARIABLES
    private var questionShown = 0
    private var answered = 0
    private var spawnRate: Double = 5000.00 // Initial spawn rate 5 seconds

    @SuppressLint("DiscouragedApi", "Recycle", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.penjumlahan)

        infoText = findViewById(R.id.infoText)
        coinText = findViewById(R.id.coinText)

        coinsMainMenu = intent.getStringExtra(getCoins)?.toIntOrNull() ?: 0

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

//                    Log.d("cek123", "progress: $progress")
                }

                override fun onFinish() {
                    timers[index].progress = 0
                    if (timers[index].visibility == View.VISIBLE) {
                        countDownTimers.forEach { timer ->
                            timer.cancel()
                        }
                        gameOver = true
                        gameOverDialog()
                    }
                }
            }
        }
        timers.forEach { timer ->
            timer.visibility = View.INVISIBLE
        }

        // PREPARE BUTTONS
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

        // SPAWNERS
        val handler = Handler()
        val runnable = object : Runnable {
            override fun run() {
                if (!gameOver) {
                    if (questionShown <= 0) {
                        generateQuestion()
                        generateQuestion()
                        handler.postDelayed(this, spawnRate.toLong())
                    } else if (questionShown <= 5) {
                        generateQuestion()
                        handler.postDelayed(this, spawnRate.toLong())
                    }
                }
            }
        }
        handler.post(runnable)

        // GAME PAUSE (masih belum selesai)
//        val pauseButton: Button = findViewById(R.id.pauseButton)
//        val dialog = Dialog(this)
//
//        pauseButton.setOnClickListener {
//            // Create and set the custom layout for the dialog
//            dialog.setContentView(R.layout.dialog_pause)
//
//            // Set the title and message of the dialog
//            val titleTextView: TextView = dialog.findViewById(R.id.pauseTitle)
//            val messageTextView: TextView = dialog.findViewById(R.id.pauseMessage)
//            titleTextView.text = "Pause"
//            messageTextView.text = "Game Paused"
//
//            // Set any other customizations or interactions for the dialog
//
//            // Show the dialog
//            dialog.show()
//        }
    }

    private fun generateQuestion() { // CHANGE LOGIC HERE
//        Log.d("cek123", "$buttonQuestions")
//        Log.d("cek123", "${buttonQuestions.size}")

        questionShown += 1
        val question = IntArray(3)
        val isTooMuch: Boolean

        if (buttonQuestions.size < 6) {
            question[0] = (1..10).random()
            question[1] = (1..10).random()

            buttonQuestions.add(question[0])
            buttonQuestions.add(question[1])

            isTooMuch = false
        } else {
            val temp = buttonQuestions.toMutableList()
            val numbers = temp.toMutableList()

            var randomIndex: Int = (0 until numbers.size).random()
            var randomElement: Int = numbers.removeAt(randomIndex)
            question[0] = randomElement

            randomIndex = (0 until numbers.size).random()
            randomElement = numbers.removeAt(randomIndex)
            question[1] = randomElement

            isTooMuch = true
        }

        question[2] = question[0] + question[1]


        for (i in 0..2) {
            var randomNumber = (0..31).random()
            while (visibleBtn.contains(randomNumber)) {
                randomNumber = (0..31).random()
            }
            visibleBtn.add(randomNumber)

            if (!isTooMuch) {
                when (i) {
                    0 -> showButton(randomNumber, question[0], "Question")
                    1 -> showButton(randomNumber, question[1], "Question")
                }
            }

            if (i == 2 ) showButton(randomNumber, question[2], "Answer")
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showButton(id: Int, number: Int, type: String) {
        buttons[id].text = number.toString()
        buttons[id].visibility = View.VISIBLE

        if (type == "Question") {
            buttons[id].tag = 3
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
                } else if (infoText.text.toString() == resources.getString(R.string.status_incorrect) ||
                        infoText.text.toString() == resources.getString(R.string.status_correct) ||
                        infoText.text.toString() == resources.getString(R.string.status_empty)) {
                    infoText.text = buttons[id].text.toString()
                } else {
                    infoText.append(" + " + buttons[id].text.toString())
                }
            }
        } else if (type == "Answer") {
            buttons[id].tag = 1
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
                } else { // CHANGE LOGIC HERE
                    if (chosenBtnText.sum() == buttons[id].text.toString().toInt()) { // CORRECT
                        infoText.text = resources.getString(R.string.status_correct)
                        // Correct text animation
                        YoYo.with(Techniques.RubberBand)
                            .duration(700)
                            .repeat(0)
                            .playOn(infoText)

                        // Remove all buttons from the screen
                        for (button_id in 0 until chosenBtnId.size) {
                            // Click animation
                            YoYo.with(Techniques.FadeOut)
                                .duration(700)
                                .repeat(0)
                                .playOn(buttons[id])

                            hideButton(chosenBtnId[button_id])
                        }
                        hideButton(id)

                        coinsEarned += 10
                        numberAnimation(coinText, coinsEarned - 10, coinsEarned)
//                        coinText.text = coinsEarned.toString()
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

        val storedInteger = buttons[id].tag as? Int
        val result = storedInteger?.minus(1)
        buttons[id].tag = result

        var buttonColor: Int = Color.WHITE

        when (buttons[id].tag) {
            0 -> {
                val strBtn = buttons[id].text.toString()
                val intVal = strBtn.toInt()

                val index = buttonQuestions.indexOfFirst { it == intVal }
                if (index != -1) {
                    buttonQuestions.removeAt(index)
                }

                buttons[id].visibility = View.INVISIBLE
                buttons[id].text = ""
            }
            1 -> buttonColor = Color.rgb(255, 192, 203)
            2 -> buttonColor = Color.rgb(220, 120, 120)
        }


        buttons[id].setBackgroundColor(buttonColor)
//        buttons[id].setBackgroundColor(Color.RED)
        buttons[id].isEnabled = true


    }

    private fun gameOverDialog() {
        val totalCoins = coinsEarned + coinsMainMenu

        AlertDialog.Builder(this@Penjumlahan)
            .setTitle("GAME OVER")
            .setMessage("Coins Earned: $coinsEarned" +
                    "\nTotal Coins: $totalCoins")
            .setPositiveButton("OK") { _, _ ->
                val intent =
                    Intent(this@Penjumlahan, MainActivity::class.java).apply {
                        putExtra(
                            MainActivity.getData,
                            coinsEarned.toString()
                        )
                    }
                startActivity(intent)

                Toast.makeText(
                    this@Penjumlahan,
                    "Back to Main Menu",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .show()
    }

    private fun numberAnimation(textViewKu: TextView, start: Int, end: Int) {
        // NUMBER ANIMATION
        val valueAnimator = ValueAnimator.ofInt(start, end)
        valueAnimator.duration = 500

        val initialFontSize = textViewKu.textSize // Store the initial font size

        valueAnimator.addUpdateListener { animator ->
            val animatedValue = animator.animatedValue as Int
            textViewKu.text = animatedValue.toString()

            val scale = animatedValue.toFloat() / 100.0f

            // Decrease the font size after reaching halfway point of the animation
            val fontSize: Float = if (scale >= 0.5f) {
                val scaleDown = (1.0f - scale) * 2.0f
                initialFontSize + (scaleDown * 25.0f)
            } else {
                initialFontSize + (scale * 25.0f)
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

    companion object {
        const val getCoins = "Get total coins from main menu."
    }
}