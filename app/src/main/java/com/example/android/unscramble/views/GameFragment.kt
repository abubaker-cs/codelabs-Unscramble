/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */

package com.example.android.unscramble.views

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.android.unscramble.adapters.GameViewModel
import com.example.android.unscramble.R
import com.example.android.unscramble.databinding.GameFragmentBinding
import com.example.android.unscramble.ui.game.MAX_NO_OF_WORDS
import com.example.android.unscramble.ui.game.allWordsList
import com.google.android.material.dialog.MaterialAlertDialogBuilder


/**
 * Fragment where the game is played, contains the game logic.
 */
class GameFragment : Fragment() {

    // Binding object instance with access to the views in the game_fragment.xml layout
    private lateinit var binding: GameFragmentBinding

    // Create a ViewModel the first time the fragment is created.
    // If the fragment is re-created, it receives the same com.example.android.unscramble.Adapters.GameViewModel instance created by the
    // first fragment


    /**
     * Issue:
     * =====
     * In your app, if you initialize the view model using default com.example.android.unscramble.Adapters.GameViewModel constructor, like below:
     *
     *      private val viewModel = com.example.android.unscramble.Adapters.GameViewModel()
     *
     * Then the app will lose the state of the viewModel reference when the device goes through a configuration change.
     *
     * Solution:
     * =========
     * Instead, use the property delegate approach and delegate the responsibility of the viewModel
     * object to a separate class called viewModels. That means when you access the viewModel object,
     * it is handled internally by the delegate class, viewModels. The delegate class creates the
     * viewModel object for you on the first access, and retains its value through configuration
     * changes and returns the value when requested.
     */

    // Connect our com.example.android.unscramble.Adapters.GameViewModel with the UI Controller (i.e. GameFragment)
    // Note: A delegate property is defined using the by clause and a delegate class instance.
    private val viewModel: GameViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout XML file and return a binding object instance

        // Using ViewBinding
        // binding = GameFragmentBinding.inflate(inflater, container, false)

        // Using DataBinding
        binding = DataBindingUtil.inflate(inflater, R.layout.game_fragment, container, false)

        //
        Log.d("GameFragment", "GameFragment created/re-created!")

        // We are observing if our "app data" is being preserved in the viewModel during the configuration change.
        // *****
        // Print the:
        // 1. app data,
        // 2. word,
        // 3. score, and
        // 4. word count.
        Log.d(
            "GameFragment", "Word: ${viewModel.currentScrambledWord} " +
                    "Score: ${viewModel.score} WordCount: ${viewModel.currentWordCount}"
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 01 Binding VARIABLES from game_fragment.xml file
        binding.gameViewModel = viewModel
        binding.maxNoOfWords = MAX_NO_OF_WORDS

        // 02 Allow the binding for livedata observation.
        // Specify the fragment view as the lifecycle owner of the binding.
        // This is used so that the binding can observe LiveData updates
        binding.lifecycleOwner = viewLifecycleOwner

        // Setup a click listener for the Submit and Skip buttons.
        binding.submit.setOnClickListener { onSubmitWord() }
        binding.skip.setOnClickListener { onSkipWord() }

        // Update the UI
        // updateNextWordOnScreen()

        // binding.score.text = getString(R.string.score, 0)
        // binding.wordCount.text = getString(R.string.word_count, 0, MAX_NO_OF_WORDS)

        // currentScrambledWord
        // Observe the scrambledCharArray LiveData, passing in the LifecycleOwner and the observer.
        viewModel.currentScrambledWord.observe(
            viewLifecycleOwner
        ) { newWord ->
            binding.textViewUnscrambledWord.text = newWord
        }

        // Score
        viewModel.score.observe(
            viewLifecycleOwner
        ) { newScore ->
            binding.score.text = getString(R.string.score, newScore)
        }

        // currentWordCount
        viewModel.currentWordCount.observe(
            viewLifecycleOwner
        ) { newWordCount ->
            binding.wordCount.text =
                getString(R.string.word_count, newWordCount, MAX_NO_OF_WORDS)
        }
    }

    /*
    * Checks the user's word, and updates the score accordingly.
    * Displays the next scrambled word.
    * After the last word, the user is shown a Dialog with the final score.
    */
    private fun onSubmitWord() {

        // Store the player's word in it, by extracting it from the text field in the binding variable.
        val playerWord = binding.textInputEditText.text.toString()

        // Validate the player's word
        if (viewModel.isUserWordCorrect(playerWord)) {

            // Error TextField: False (reset the text field)
            setErrorTextField(false)

            // If Next Word is not available:
            if (!viewModel.nextWord()) {

                // Final Score
                showFinalScoreDialog()

            }

        } else {

            // If the user word is incorrect, show an error message in the text field
            setErrorTextField(true)

        }

    }

    /*
    * Skips the current word without changing the score.
    */
    private fun onSkipWord() {

        if (viewModel.nextWord()) {

            //  True: Display the word on screen and reset the text field
            setErrorTextField(false)
            // updateNextWordOnScreen()

        } else {

            // FALSE: There's no more words left in this round, show the alert dialog with the final score.
            showFinalScoreDialog()
        }

    }

    /*
     * Gets a random word for the list of words and shuffles the letters in it.
     */
    private fun getNextScrambledWord(): String {
        val tempWord = allWordsList.random().toCharArray()
        tempWord.shuffle()
        return String(tempWord)
    }

    /*
    * Creates and shows an AlertDialog with the final score.
    */
    private fun showFinalScoreDialog() {

        // The requireContext() method returns a non-null Context
        // Usually it is used to get access to resources, databases, and other system services.
        // In this step, you pass the fragment context to create the alert dialog.
        MaterialAlertDialogBuilder(requireContext())

            // Title: Congratulations!
            .setTitle(getString(R.string.congratulations))

            // Body Message: You scored: %d
            .setMessage(getString(R.string.you_scored, viewModel.score.value))

            // Make your alert dialog not cancelable when the back key is pressed
            .setCancelable(false)

            // Negative Button: EXIT
            .setNegativeButton(getString(R.string.exit)) { _, _ ->

                // activity?.finish()
                exitGame()

            }

            // Positive Button: Play Again
            .setPositiveButton(getString(R.string.play_again)) { _, _ ->

                // Re-initializes the data in the ViewModel and updates the views with the new data
                restartGame()

            }

            // Initialize the MaterialAlertDialog
            .show()
    }

    /*
     * Re-initializes the data in the ViewModel and updates the views with the new data, to
     * restart the game.
     */
    private fun restartGame() {

        // Reset Values + Data
        viewModel.reinitializeData()

        // Hide Error Field
        setErrorTextField(false)

        // Displays the next scrambled word on screen.
        // updateNextWordOnScreen()
    }

    /*
     * Exits the game.
     */
    private fun exitGame() {
        activity?.finish()
    }

    override fun onDetach() {
        super.onDetach()
        Log.d("GameFragment", "GameFragment destroyed!")
    }

    /*
    * Built-in Feature of TextInputLayout: Display Errors using MaterialUI Component.
    * Sets and resets the text field error status.
    */
    private fun setErrorTextField(error: Boolean) {

        if (error) {

            // Enable Error Feature
            binding.textField.isErrorEnabled = true

            // Display Error Message | Note .error is a built-in feature of TextInputLayout
            binding.textField.error = getString(R.string.try_again)

        } else {

            // Disable supporting Error Feature
            binding.textField.isErrorEnabled = false

            // null = It will clear the Error Message from the TextField
            binding.textInputEditText.text = null
        }

    }

    /*
     * Displays the next scrambled word on screen.
     */
    // private fun updateNextWordOnScreen() {
    //    binding.textViewUnscrambledWord.text = viewModel.currentScrambledWord
    // }
}
