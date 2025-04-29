package com.example.mygame1

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.GridLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.mygame1.utilities.SignalManager
import android.content.Intent

class MainActivity : AppCompatActivity() {
    private val columnInUse = BooleanArray(3) { false }
    private lateinit var gameBoard: GridLayout
    private lateinit var cats: Array<ImageView>
    private lateinit var hearts: Array<ImageView>
//    private val activeBombs = mutableListOf<Bomb>()



    private val handler = Handler(Looper.getMainLooper())
    private val gameManager = GameManager()
    private var isRunning = true

    private lateinit var bombsMatrix: Array<Array<ImageView>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gameBoard = findViewById(R.id.game_board)

        cats = arrayOf(
            findViewById(R.id.cat1),
            findViewById(R.id.cat2),
            findViewById(R.id.cat3)
        )

        hearts = arrayOf(
            findViewById(R.id.heart1),
            findViewById(R.id.heart2),
            findViewById(R.id.heart3)
        )
        for (i in 0..2)
        {
            cats[i].visibility = View.INVISIBLE
        }

        cats[gameManager.catCol].visibility = View.VISIBLE

        findViewById<View>(R.id.buttonLeft).setOnClickListener {
            gameManager.moveLeft()
            updateCatPosition()
        }

        findViewById<View>(R.id.buttonRight).setOnClickListener {
            gameManager.moveRight()
            updateCatPosition()
        }

        setupBombMatrix()
        startGameLoop()
    }

    private fun setupBombMatrix() {
        bombsMatrix = Array(3) { col ->
            Array(6) { row ->
                val bombId = resources.getIdentifier("bomb_${col}_${row}", "id", packageName)
                val bomb = findViewById<ImageView>(bombId)
                bomb.visibility = View.INVISIBLE
                bomb
            }
        }
    }




    private fun startGameLoop() {
        handler.post(object : Runnable {
            override fun run() {
                if (isRunning) {

                    spawnBomb()
                    handler.postDelayed(this, 1000)
                }
            }
        })
    }


    private fun dropBomb(column: Int) {
        if (columnInUse[column]) return
        columnInUse[column] = true

        val handler = Handler(Looper.getMainLooper())
        var row = 0

        val runnable = object : Runnable {
            override fun run() {
                if (row > 0) {
                    bombsMatrix[column][row - 1].visibility = View.INVISIBLE
                }

                // Check collision when bomb is one row above the cat (row 5 is the bottom)
                if (row == 5) {
                    bombsMatrix[column][row].visibility = View.VISIBLE
                    bombsMatrix[column][row].setImageResource(R.drawable.bomb)

                    if (column == gameManager.catCol) {
                        bombsMatrix[column][row].visibility = View.INVISIBLE
                        columnInUse[column] = false
                        handleCollision()
                        return
                    } else {
                        // Clear bomb after a short delay even if no collision
                        handler.postDelayed({
                            bombsMatrix[column][row].visibility = View.INVISIBLE
                            columnInUse[column] = false
                        }, 300)
                        return
                    }
                }

                // Show bomb and move to next row
                if (row < 6) {
                    bombsMatrix[column][row].visibility = View.VISIBLE
                    bombsMatrix[column][row].setImageResource(R.drawable.bomb)
                    row++
                    handler.postDelayed(this, 300)
                }
            }
        }

        handler.post(runnable)
    }


    private fun spawnBomb() {
        val availableCols = (0..2).filter { !columnInUse[it] }
        if (availableCols.isNotEmpty()) {
            val randomCol = availableCols.random()
            dropBomb(randomCol)
        }
    }


    private fun handleCollision() {
        gameManager.loseLife()
        SignalManager.vibrate(this)
        SignalManager.toast(this, "נפגעת!")

        updateHearts()

        if (gameManager.isGameOver()) {
            isRunning = false
            goToGameOverScreen()

        }
    }
    private fun goToGameOverScreen() {
        val intent = Intent(this, GameOverActivity::class.java)
        startActivity(intent)
        finish()
    }


    private fun updateHearts() {
        when (gameManager.lives) {
            2 -> hearts[2].visibility = View.INVISIBLE
            1 -> hearts[1].visibility = View.INVISIBLE
            0 -> hearts[0].visibility = View.INVISIBLE
        }
    }

private fun updateCatPosition() {
    for (i in 0..2) {
        cats[i].visibility = if (i == gameManager.catCol) View.VISIBLE else View.INVISIBLE
    }
}
}

