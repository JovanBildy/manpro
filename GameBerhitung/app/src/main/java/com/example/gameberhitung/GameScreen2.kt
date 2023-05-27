package com.example.gameberhitung

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

@Suppress("DEPRECATION")
class GameScreen2 : AppCompatActivity() {

    private lateinit var buttons: Array<Button>
    private lateinit var imageButtons: Array<ImageButton>
    private lateinit var timers: Array<ProgressBar>
    private lateinit var countDownTimers: Array<CountDownTimer>

    private lateinit var infoText: TextView
    private lateinit var coinText: TextView
    private lateinit var sharedPreferences: SharedPreferences

    private var coinsMainMenu: Int = 0
    private var coinsEarned: Int = 0
    private var isGameOver: Boolean = false
    private var equippedButton: String = ""

    // RED -> Soal | Question
    // BLUE -> Jawaban | Answer

    // Spawner variables
    private var questionShown: Int = 0
    private var answered: Int = 0
    private var spawnRate: Double = 5000.00 // Initial spawn rate 5 seconds
    private val buttonQuestions = mutableListOf<Int>() // List of button questions' id
    private var visibleBtn = mutableListOf<Int>() // List of visible buttons' id
    private var totalDuration = 30000.00 // Total duration in milliseconds (30 secs)

    // Custom button variables
    private var buttonTypes = mutableMapOf<String, Int>()

    // Game mechanic variables
    private var chosenBtnText = mutableListOf<Int>()
    private var chosenBtnId = mutableListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_screen2)

        // Get layout objects
        infoText = findViewById(R.id.infoText)
        coinText = findViewById(R.id.coinText)
        coinsMainMenu = intent.getStringExtra(getCoins)?.toIntOrNull() ?: 0

        // Get equipped button
        sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        equippedButton = sharedPreferences.getString("equipment", "").toString()

        prepareTimers()
        prepareButtons()
        prepareSpawner()
    }

    @SuppressLint("DiscouragedApi")
    private fun prepareButtons() {
        buttons = Array(32) { index ->
            findViewById<Button>(resources.getIdentifier(
                "button${index+1}", "id", packageName)).apply {
                visibility = View.INVISIBLE
            }
        }

        imageButtons = Array(32) { index ->
            findViewById<ImageButton>(resources.getIdentifier(
                "imageButton${index+1}", "id", packageName)).apply {
                visibility = View.INVISIBLE
            }
        }

        when (equippedButton) {
            "button_es" -> {
                buttonTypes["blue"] = R.drawable.button1_blue
                buttonTypes["red"] = R.drawable.button1_red
                buttonTypes["grey"] = R.drawable.button1_grey
            }

            "button_sedang" -> {
                buttonTypes["blue"] = R.drawable.button2_blue
                buttonTypes["red"] = R.drawable.button2_red
                buttonTypes["grey"] = R.drawable.button2_grey
            }

            "button_mahal" -> {
                buttonTypes["blue"] = R.drawable.button3_blue
                buttonTypes["red"] = R.drawable.button3_red
                buttonTypes["grey"] = R.drawable.button3_grey
            }

            else -> { }
        }
    }

    @SuppressLint("DiscouragedApi")
    private fun prepareTimers() {
        val updateInterval = 100L // Update interval in milliseconds (100 milli-secs)

        timers = Array(32) { index ->
            findViewById<ProgressBar?>(resources.getIdentifier(
                "progressBar${index + 1}", "id", packageName)).apply {
                visibility = View.INVISIBLE
            }
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
                        countDownTimers.forEach { timer -> timer.cancel() }
                        isGameOver = true
                        showGameOverDialog()
                    }
                }
            }
        }
    }

    private fun prepareSpawner() {
        val handler = Handler()

        val runnable = object : Runnable {
            override fun run() {
                if (!isGameOver) {
                    if (questionShown <= 0) {
                        generateQuestion(); generateQuestion()
                        handler.postDelayed(this, spawnRate.toLong())
                    } else if (questionShown <= 5) {
                        generateQuestion()
                        handler.postDelayed(this, spawnRate.toLong())
                    }
                }
            }
        }

        handler.post(runnable)
    }

    private fun showButton(id: Int, number: Int, type: String) {
        buttons[id].text = number.toString()
        buttons[id].visibility = View.VISIBLE
        imageButtons[id].visibility = View.VISIBLE

        if (type == "Question") {
            buttons[id].tag = 3
            buttonTypes["red"]?.let { imageButtons[id].setBackgroundResource(it) }

            buttons[id].setOnClickListener {
                buttons[id].isEnabled = false
                buttonTypes["grey"]?.let { imageButtons[id].setBackgroundResource(it) }

                chosenBtnText.add(buttons[id].text.toString().toInt())
                chosenBtnId.add(id)
                if (infoText.text.isNullOrEmpty()) {
                    infoText.append(buttons[id].text.toString())
                } else if (infoText.text.toString() == resources.getString(R.string.status_incorrect) ||
                    infoText.text.toString() == resources.getString(R.string.status_correct) ||
                    infoText.text.toString() == resources.getString(R.string.status_empty)) {
                    infoText.text = buttons[id].text.toString()
                } else {
                    when (intent.getStringExtra(getGameMode)) {
                        "penjumlahan" -> { infoText.append(" + " + buttons[id].text.toString()) }
                        "pengurangan" -> {infoText.append(" - " + buttons[id].text.toString())}
                        "perkalian" -> { infoText.append(" * " + buttons[id].text.toString()) }
                        "pembagian" -> {infoText.append(" / " + buttons[id].text.toString())}
                        else -> { Log.d("cek123", "Game mode invalid!") }
                    }
                }

//                Log.d("cek123", buttons[id].text.toString())

                // Click animation
//                YoYo.with(Techniques.BounceIn).duration(700).repeat(0).playOn(buttons[id])
            }
        }
        else if (type == "Answer") {
            buttons[id].tag = 1
            buttonTypes["blue"]?.let { imageButtons[id].setBackgroundResource(it) }

            timers[id].visibility = View.VISIBLE
            countDownTimers[id].start()

            buttons[id].setOnClickListener {
                if (chosenBtnText.isEmpty()) {
                    infoText.text = resources.getString(R.string.status_empty)

                    // Incorrect text animation
//                    YoYo.with(Techniques.Tada).duration(700).repeat(0).playOn(infoText)
                } else { // CHANGE LOGIC HERE
                    val isCorrect: Boolean = when (intent.getStringExtra(getGameMode)) {
                        "penjumlahan" -> { chosenBtnText.sum() == buttons[id].text.toString().toInt() }
                        "pengurangan" -> { chosenBtnText.reduce { total, next -> total - next } == buttons[id].text.toString().toInt() }
                        "perkalian" -> { chosenBtnText.reduce { total, next -> total * next } == buttons[id].text.toString().toInt() }
                        "pembagian" -> { chosenBtnText.reduce { total, next -> total / next } == buttons[id].text.toString().toInt() }
                        else -> { false }
                    }

//                    Log.d("cek", chosenBtnText.sum().toString())
//                    Log.d("cek", isCorrect.toString())
                    if (isCorrect) { // CORRECT
                        infoText.text = resources.getString(R.string.status_correct)

                        // Correct text animation
//                        YoYo.with(Techniques.RubberBand).duration(700).repeat(0).playOn(infoText)

                        // Remove all buttons from the screen
                        for (button_id in 0 until chosenBtnId.size) {

                            // Click animation
//                            YoYo.with(Techniques.FadeOut).duration(700).repeat(0).playOn(buttons[id])

                            hideButton(chosenBtnId[button_id])
                        }
                        hideButton(id)

                        coinsEarned += 10
                        numberAnimation(coinText, coinsEarned - 10, coinsEarned)
                        questionShown -= 1
                        answered += 1

                        // If milestone is reached -> increase spawn rate
                        if (answered % 10 == 0 && answered > 0 && spawnRate >= 1000) {
                            // Spawn rate reduced by 10% every 10 questions answered
                            spawnRate *= 0.9
                            totalDuration *= 0.9
                        }
                    }
                    else { // INCORRECT
                        infoText.text = resources.getString(R.string.status_incorrect)

                        // Incorrect text animation
//                        YoYo.with(Techniques.Tada).duration(700).repeat(0).playOn(infoText)

                        // Reset buttons' state
                        for (button_id in 0 until chosenBtnId.size) {
                            val buttonOpacity: Float = when (buttons[chosenBtnId[button_id]].tag) {
                                1 -> 0.6f; 2 -> 0.8f; else -> 1f
                            }
                            buttonTypes["red"]?.let { imageButtons[chosenBtnId[button_id]].setBackgroundResource(it) }
                            imageButtons[chosenBtnId[button_id]].alpha = buttonOpacity
                            buttons[chosenBtnId[button_id]].isEnabled = true
                        }
                    }

                    chosenBtnText.clear()
                    chosenBtnId.clear()
                }

                // Click animation
//                YoYo.with(Techniques.BounceIn).duration(700).repeat(0).playOn(buttons[id])
            }
        }
    }

    private fun hideButton(id: Int) {
        timers[id].visibility = View.INVISIBLE
        countDownTimers[id].cancel()

        val storedInteger = buttons[id].tag as? Int
        val result = storedInteger?.minus(1)
        buttons[id].tag = result

        var buttonOpacity = 1f
        when (buttons[id].tag) { // Check button's durability
            0 -> {
                val strBtn = buttons[id].text.toString()
                val intVal = strBtn.toInt()

                val index = buttonQuestions.indexOfFirst { it == intVal }
                if (index != -1) { buttonQuestions.removeAt(index) }

                buttons[id].visibility = View.INVISIBLE
                imageButtons[id].visibility = View.INVISIBLE
                buttons[id].text = ""
            }
            1 -> buttonOpacity = 0.6f
            2 -> buttonOpacity = 0.8f
        }
        buttonTypes["red"]?.let { imageButtons[id].setBackgroundResource(it) }
        imageButtons[id].alpha = buttonOpacity
        buttons[id].isEnabled = true
    }

    private fun generateQuestion() {
        questionShown += 1
        val question = IntArray(3)
        val isTooMuch: Boolean

        when (intent.getStringExtra(getGameMode)) {
            "penjumlahan" -> {
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
            }
            "pengurangan" -> {
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

                question[2] = question[0] - question[1]
            }
            "perkalian" -> {
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

                question[2] = question[0] * question[1]
            }
            "pembagian" -> {
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

                val quotient = question[0] / question[1]
                question[2] = if (question[0] % question[1] == 0) {
                    quotient // If the remainder is 0, the result is an integer
                } else {
                    quotient + 1 // Add 1 to the quotient to make it an integer
                }
            }
            else -> {
                isTooMuch = false
            }
        }

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

    private fun showGameOverDialog() {
        val totalCoins = coinsEarned + coinsMainMenu

        AlertDialog.Builder(this@GameScreen2)
            .setTitle("GAME OVER")
            .setMessage("Coins Earned: $coinsEarned" +
                    "\nTotal Coins: $totalCoins")
            .setPositiveButton("OK") { _, _ ->
                val intent = Intent(
                    this@GameScreen2, MainActivity::class.java).apply {
                        putExtra(MainActivity.getData, coinsEarned.toString())
                    }
                startActivity(intent)

                Toast.makeText(
                    this@GameScreen2,
                    "Back to Main Menu",
                    Toast.LENGTH_SHORT).show()
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
        const val getCoins = "Get total coins."
        const val getGameMode = "Get the game mode."
    }
}