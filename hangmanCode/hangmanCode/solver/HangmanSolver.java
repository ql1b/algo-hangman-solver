package solver;

import java.util.*;

/**
 * Abstract class of all Hangman solvers you are to implement.
 *
 * Generally no need to modify, but you can if you wanted to add some common
 * attributes or methods.
 *
 * @author Jeffrey Chan, RMIT 2020
 */
public abstract class HangmanSolver
{
    /**
     * A new game has started.
     *
     * @param wordLengths Length of words we are guessing for.
     * @param maxIncorrectGuesses Maximum number of incorrect guesses we are allowed.
     */
    public abstract void newGame(int[] wordLengths, int maxIncorrectGuesses);


    /**
     * Make a guess.
     *
     * @return A character guess.
     */
    public abstract char makeGuess();


    /**
     * Feedback/response to previous guess made.
     *
     * @param guessedChar Character that was previously guessed.
     * @param bGuess True if the character guessed is in one or more of the words, otherwise false.
     * @param positions For each word, a list of positions where we have a correct guess.
     */
    public abstract void guessFeedback(char guessedChar, Boolean bGuess,
        ArrayList< ArrayList<Integer> > positions);

} // end of class HangmanSolver
