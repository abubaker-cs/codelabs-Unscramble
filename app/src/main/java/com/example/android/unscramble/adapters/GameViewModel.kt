package com.example.android.unscramble.adapters

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.unscramble.ui.game.MAX_NO_OF_WORDS
import com.example.android.unscramble.ui.game.SCORE_INCREASE
import com.example.android.unscramble.ui.game.allWordsList

/**
 * ViewModel containing the app data and methods to process the data
 */
class GameViewModel : ViewModel() {

    // Score: We will be using the "Score" variable to display results using the MaterialAlertDialog
    private val _score = MutableLiveData(0)
    val score: LiveData<Int>
        get() = _score

    // currentWordCount
    private val _currentWordCount = MutableLiveData(0)
    val currentWordCount: LiveData<Int>
        get() = _currentWordCount

    // currentScrambledWord
    private val _currentScrambledWord = MutableLiveData<String>()
    val currentScrambledWord: LiveData<String>
        get() = _currentScrambledWord

    // List of words used in the game
    private var wordsList: MutableList<String> = mutableListOf()
    private lateinit var currentWord: String

    /**
     * We are using the init block to initialize lateinit properties in the class such as the current word.
     */
    init {

        Log.d("GameFragment", "com.example.android.unscramble.Adapters.GameViewModel created!")

        // The result will be that the first word displayed on the screen will be a scrambled word.
        getNextWord()

    }

    override fun onCleared() {
        super.onCleared()
        Log.d("GameFragment", "com.example.android.unscramble.Adapters.GameViewModel destroyed!")
    }

    /*
    * Updates currentWord and currentScrambledWord with the next word.
    */
    private fun getNextWord() {

        // We are selecting a random word.
        currentWord = allWordsList.random()

        // The currently selected word is temporarily being saved in tempWord for shuffling
        val tempWord = currentWord.toCharArray()

        // We are shuffling random characters in the array using shuffle() method.
        tempWord.shuffle()

        // We are verifying that the shuffling does not result in generating the same current word.
        while (String(tempWord).equals(currentWord, false)) {
            tempWord.shuffle()
        }

        if (wordsList.contains(currentWord)) {

            // If the wordsList contains currentWord, call getNextWord().
            getNextWord()

        } else {

            //  update the value of _currentScrambledWord with the newly scrambled word
            // Note: To access the data within a LiveData object, use the value property
            _currentScrambledWord.value = String(tempWord)

            // Increase the word count
            // ++_currentWordCount
            // use inc() Kotlin function to increment the value by one with null-safety.
            _currentWordCount.value = (_currentWordCount.value)?.inc()

            // Add the new word to the wordsList
            wordsList.add(currentWord)

        }
    }

    /*
    * Helper:
    * ******
    * Re-initializes the game data to restart the game.
    */
    fun reinitializeData() {

        // Reset Values + Data
        _score.value = 0
        _currentWordCount.value = 0
        wordsList.clear()

        // Next Word
        getNextWord()
    }


    /*
    * Helper Method:
    * =============
    * Increases the game score if the player's word is correct.
    */
    private fun increaseScore() {
        // _score += SCORE_INCREASE
        // Use the plus() Kotlin function to increase the _score value, which performs the addition with null-safety.
        _score.value = (_score.value)?.plus(SCORE_INCREASE)
    }

    /*
    * Helper Method:
    * =============
    * Returns true if the player word is correct.
    * Increases the score accordingly.
    */
    fun isUserWordCorrect(playerWord: String): Boolean {

        // Validate the player's word and increase the score if the guess is correct.
        if (playerWord.equals(currentWord, true)) {

            //  This will update the final score in your alert dialog.
            increaseScore()
            return true

        }

        //
        return false
    }

    /*
    * Helper Method
    * =============
    * Returns true if the current word count is less than MAX_NO_OF_WORDS
    */
    fun nextWord(): Boolean {
        return if (_currentWordCount.value!! < MAX_NO_OF_WORDS) {
            getNextWord()
            true
        } else false
    }
}