// MainActivity.kt
package com.example.mygame1

//import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
//import android.os.Vibrator
import android.view.View
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton

class MainActivity : AppCompatActivity() {
    private lateinit var gameManager: GameManager
    private lateinit var handler: Handler
    private lateinit var gameBoard: GridLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gameManager = GameManager()
        gameBoard = findViewById(R.id.game_board)
        handler = Handler(Looper.getMainLooper())

        findViewById<View>(R.id.buttonLeft).setOnClickListener {
            gameManager.moveCatLeft()
            updateUI()
        }

        findViewById<View>(R.id.buttonRight).setOnClickListener {
            gameManager.moveCatRight()
            updateUI()
        }

        handler.post(gameLoop)
    }

    private val gameLoop = object : Runnable {
        override fun run() {
            val hit = gameManager.dropBombs()
            updateUI()
            if (hit) {

                Toast.makeText(this@MainActivity, "Ouch!", Toast.LENGTH_SHORT).show()
            }
            if (!gameManager.isGameOver()) {
                handler.postDelayed(this, 500)
            } else {
                Toast.makeText(this@MainActivity, "Game Over!", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun updateUI() {
        for (col in 0 until 3) {
            for (row in 0 until 6) {
               // val bombId = resources.getIdentifier("bomb_${col}_${row}", "id", packageName)
               // val bomb = findViewById<View>(bombId)
              //  bomb?.visibility = if (gameManager.matrix[col][row]) View.VISIBLE else View.INVISIBLE
            }
        }

        val cat = findViewById<ImageView>(R.id.cat)
        val params = cat.layoutParams as GridLayout.LayoutParams
        params.columnSpec = GridLayout.spec(gameManager.catColumn)
        params.rowSpec = GridLayout.spec(5)
        cat.layoutParams = params

        findViewById<AppCompatImageButton>(R.id.heart1).visibility = if (gameManager.lives >= 1) View.VISIBLE else View.INVISIBLE
        findViewById<AppCompatImageButton>(R.id.heart2).visibility = if (gameManager.lives >= 2) View.VISIBLE else View.INVISIBLE
        findViewById<AppCompatImageButton>(R.id.heart3).visibility = if (gameManager.lives >= 3) View.VISIBLE else View.INVISIBLE
    }
}



