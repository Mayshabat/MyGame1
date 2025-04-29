package com.example.mygame1

class GameManager(
    var lives: Int = 3,
    var catCol: Int = 1
) {

    fun moveLeft() {
        if (catCol > 0) {
            catCol--
        }
    }

    fun moveRight() {
        if (catCol < 2) {
            catCol++
        }
    }

    fun loseLife() {
        if (lives > 0) {
            lives--
        }
    }

    fun isGameOver(): Boolean {
        return lives <= 0
    }
}
