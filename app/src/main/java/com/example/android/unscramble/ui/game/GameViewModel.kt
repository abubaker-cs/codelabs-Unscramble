package com.example.android.unscramble.ui.game

import android.util.Log
import androidx.lifecycle.ViewModel

// GameFragment will access information about the game from the GameViewModel
class GameViewModel : ViewModel() {

    private var score = 0
    private var currentWordCount = 0


    // Backing Property
    private var _currentScrambledWord = "test"
    val currentScrambledWord: String
        get() = _currentScrambledWord


    // Kotlin's initializer block
    init {
        Log.d("GameFragment", "GameViewMode created!")
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("GameFragment", "GameViewModel destroyed!")
    }

}