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
    private val activeBombs = mutableListOf<Bomb>()



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


//    private fun spawnBomb() {
//        val randomCol = (0..2).random()
//
//        // ניצור פצצה חדשה רק אם אין פצצה למעלה בעמודה
//        if (bombsMatrix[randomCol][0].visibility == View.INVISIBLE) {
//            bombsMatrix[randomCol][0].visibility = View.VISIBLE
//        }
//    }
//private fun spawnBomb() {
//    val randomCol = (0..2).random()
//
//    // בודקים שאין כבר פצצה בשורה 0 באותו עמודה
//    val alreadyExists = activeBombs.any { it.col == randomCol && it.row == 0 }
//
//    if (!alreadyExists) {
//        activeBombs.add(Bomb(randomCol, 0))
//    }
//}








    private fun moveBombsDown() {
        // קודם כל מנקים את כל התצוגה
        for (col in 0..2) {
            for (row in 0..5) {
                bombsMatrix[col][row].visibility = View.INVISIBLE
            }
        }

        // מעדכנים את מיקום הפצצות
        val iterator = activeBombs.iterator()
        while (iterator.hasNext()) {
            val bomb = iterator.next()
            bomb.row++ // ירידה שורה

            if (bomb.row > 5) {
                // פצצה ירדה מתחת למסך
                iterator.remove()
            } else {
                bombsMatrix[bomb.col][bomb.row].visibility = View.VISIBLE
            }
        }
    }





//    private fun checkCollisions() {
//        for (col in 0..2) {
//            if (bombsMatrix[col][5].visibility == View.VISIBLE && col == gameManager.catCol) {
//                bombsMatrix[col][5].visibility = View.INVISIBLE
//                handleCollision()
//            }
//        }
//    }


//    private fun checkCollisions() {
//        val iterator = activeBombs.iterator()
//        while (iterator.hasNext()) {
//            val bomb = iterator.next()
//            if (bomb.row == 5 && bomb.col == gameManager.catCol) {
//                bombsMatrix[bomb.col][bomb.row].visibility = View.INVISIBLE
//                iterator.remove()
//                handleCollision()
//                break
//            }
//        }
//    }




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
//            SignalManager.toast(this, "המשחק נגמר!")
            isRunning = false
            goToGameOverScreen()
//            showGameOverDialog()
        }
    }
    private fun goToGameOverScreen() {
        val intent = Intent(this, GameOverActivity::class.java)
        startActivity(intent)
        finish()
    }

    //    private fun showGameOverDialog() {
//        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
//        builder.setTitle("Game Over")
//        builder.setMessage("נגמרו לך כל החיים. רוצה לשחק שוב?")
//        builder.setPositiveButton("נסה שוב") { _, _ ->
//            restartGame()
//        }
//        builder.setNegativeButton("סיים") { _, _ ->
//            finish()
//        }
//        builder.setCancelable(false)
//        builder.show()
//    }
    private fun restartGame() {
        gameManager.lives = 3
        gameManager.catCol = 1
        isRunning = true

        updateCatPosition()
        updateHeartsFull()
        clearBombs()
        startGameLoop()
    }
    private fun updateHeartsFull() {
        for (heart in hearts) {
            heart.visibility = View.VISIBLE
        }
    }
    private fun clearBombs() {
        for (col in 0..2) {
            for (row in 0..5) {
                bombsMatrix[col][row].visibility = View.INVISIBLE
            }
        }
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

