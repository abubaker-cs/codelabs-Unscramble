import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.android.unscramble.ui.game.MAX_NO_OF_WORDS
import com.example.android.unscramble.ui.game.SCORE_INCREASE
import com.example.android.unscramble.ui.game.allWordsList

/**
 * ViewModel containing the app data and methods to process the data
 */
class GameViewModel : ViewModel() {

    // Score: We will be using the "Score" variable to display results using the MaterialAlertDialog
    private var _score = 0
    val score: Int
        get() = _score

    // currentWordCount
    private var _currentWordCount = 0
    val currentWordCount: Int
        get() = _currentWordCount

    // currentScrambledWord
    private lateinit var _currentScrambledWord: String
    val currentScrambledWord: String
        get() = _currentScrambledWord

    // List of words used in the game
    private var wordsList: MutableList<String> = mutableListOf()
    private lateinit var currentWord: String

    /**
     * We are using the init block to initialize lateinit properties in the class such as the current word.
     */
    init {

        Log.d("GameFragment", "GameViewModel created!")

        // The result will be that the first word displayed on the screen will be a scrambled word.
        getNextWord()

    }

    override fun onCleared() {
        super.onCleared()
        Log.d("GameFragment", "GameViewModel destroyed!")
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
            _currentScrambledWord = String(tempWord)

            // Increase the word count
            ++_currentWordCount

            // Add the new word to the wordsList
            wordsList.add(currentWord)

        }
    }

    /*
    * Re-initializes the game data to restart the game.
    */
    fun reinitializeData() {
        _score = 0
        _currentWordCount = 0
        wordsList.clear()
        getNextWord()
    }


    /*
    * Increases the game score if the player's word is correct.
    */
    private fun increaseScore() {
        _score += SCORE_INCREASE
    }

    /*
    * Returns true if the player word is correct.
    * Increases the score accordingly.
    */
    fun isUserWordCorrect(playerWord: String): Boolean {
        if (playerWord.equals(currentWord, true)) {
            increaseScore()
            return true
        }
        return false
    }

    /*
    * Helper Method
    * =============
    * Returns true if the current word count is less than MAX_NO_OF_WORDS
    */
    fun nextWord(): Boolean {
        return if (_currentWordCount < MAX_NO_OF_WORDS) {
            getNextWord()
            true
        } else false
    }
}