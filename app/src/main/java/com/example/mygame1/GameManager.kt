


package com.example.mygame1


class GameManager {
    companion object {
        const val ROWS = 6
        const val COLUMNS = 3
    }

    var lives = 3
    var catColumn = 1
    var matrix = Array(COLUMNS) { BooleanArray(ROWS) }

    fun moveCatLeft() {
        if (catColumn > 0) catColumn--
    }

    fun moveCatRight() {
        if (catColumn < COLUMNS - 1) catColumn++
    }

    fun dropBombs(): Boolean {
        for (col in 0 until COLUMNS) {
            for (row in ROWS - 1 downTo 1) {
                matrix[col][row] = matrix[col][row - 1]
            }
            matrix[col][0] = (0..4).random() == 0
        }
        if (matrix[catColumn][ROWS - 1]) {
            matrix[catColumn][ROWS - 1] = false
            lives--
            return true
        }
        return false
    }

    fun isGameOver() = lives <= 0
}
