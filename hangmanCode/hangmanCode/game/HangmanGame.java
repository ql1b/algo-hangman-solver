package game;

import java.io.*;
import java.util.*;

import solver.*;
import trace.*;

/**
 * Class to implement running of a hangman game.
 * This one class can run standard, two word and Wheel of Fortune games.
 * DO NOT MODIFY.
 *
 * @author Jeffrey Chan, RMIT 2020
 */
public class HangmanGame
{
    /** PrintWriter to output messages to. */
    protected PrintWriter mOutWriter;

    /** Game Tracer/logger. */
    protected HangmanGameTracer mTracer;


    /**
     * Constructor.
     *
     * @param writer PrintWriter to output to.
     * @param tracer Game tracer/logger.
     */
    public HangmanGame(PrintWriter writer, HangmanGameTracer tracer)
    {
        mOutWriter = writer;
        mTracer = tracer;
    } // end of HangmanGame()


    /**
     * Run a Hangman game.
     *
     * @param wordsToGuess The words to guess for this game.
     * @param maxIncorrectNum Maximum number of incorrect guesses allowed for this game.
     * @param solver Hangman solver to use for this game.
     *
     * @throws IllegalArgumentException If one or more of the input arguments have issues.
     */
    // @Override
    public void runGame(String[] wordsToGuess, int maxIncorrectNum, HangmanSolver solver)
        throws IllegalArgumentException
    {
        // Check that there is at least one word go guess
        if (wordsToGuess.length <= 0) {
            throw new IllegalArgumentException("No words were inputed for this game!");
        }

        //
        // Initialisation
        //

        // number of (incorrect guesses left)
        int incorrectLeft = maxIncorrectNum;
        // storage for (character) guesses made already
        ArrayList<Character> charGuesses = new ArrayList<Character>();

        // construct word to position mappings for the guessed words
        ArrayList<Map<Character, ArrayList<Integer> > > wordCharPosMaps
            = constructCharPosMaps(wordsToGuess);

        // length of words
        int[] wordLengths = new int[wordsToGuess.length];
        for (int s = 0; s < wordsToGuess.length; ++s) {
            wordLengths[s] = wordsToGuess[s].length();
        }

        // tell solver it is a new game and pass it appropriate arguments.
        solver.newGame(wordLengths, maxIncorrectNum);

        // storage for which characters we have guessed already currently
        // (current status of word)
        ArrayList<char[]> wordsStatus = new ArrayList<char[]>();
        for (int s = 0; s < wordsToGuess.length; ++s) {
            char[] wordStatus = new char[wordsToGuess[s].length()];
            for (int i = 0; i < wordStatus.length; ++i) {
                // we use the 'null character' to denote character not guessed/revealed yet
                wordStatus[i] = '\0';
            }
            wordsStatus.add(wordStatus);
        }

        // whether the game has been "solved"/word guessed (initially not solved)
        Boolean bSolved = false;


        //
        // Run game!
        //

        mOutWriter.println("Welcome to Rmit Hangman!");
        mOutWriter.println("We are solving the following word(s):");
        mOutWriter.println(wordPrintingString(wordsStatus));
        mOutWriter.println("Incorrect guesses left: " + incorrectLeft);

        // current round/iteration
        int iterNum = 1;
        // Game continues while more incorrect guesses available and word not solved/guessed.
        while (!bSolved && incorrectLeft > 0) {
            //
            // Ask solver for a guess.
            //
            char c = solver.makeGuess();
            mOutWriter.println("[" + iterNum + "] Making guess: " + c);
            charGuesses.add(c);

            //
            // Check guess.
            //
            Boolean bGuess = false;

            // Scan across the words to see if guessed character hit any positions in them.
            ArrayList<ArrayList<Integer> > foundPositions = new ArrayList< ArrayList<Integer> >();
            for (int s = 0; s < wordCharPosMaps.size(); ++s) {
                ArrayList<Integer> currPositions = wordCharPosMaps.get(s).get(c);
                // currPositions != null if character is in current examined word
                if (currPositions != null) {
                    bGuess = true;
                    // we also update the wordStatus array to reflect correct guess
                    for (Integer pos : currPositions) {
                        wordsStatus.get(s)[pos] = c;
                    }
                    // we remove the character from the char->position maps
                    wordCharPosMaps.get(s).remove(c);
                }
                foundPositions.add(currPositions);
            }

            // print out relevant message
            // correct guess
            if (bGuess) {
                StringBuffer buf = new StringBuffer();
                // print out the positions that the guessed character was found in
                int currWordIndex = 0;
                for (ArrayList<Integer> positions : foundPositions) {
                    if (positions != null) {
                        buf.append("word " + currWordIndex + ": " + positions.toString() + " ");
                    }
                    ++currWordIndex;
                }
                mOutWriter.println("Hit! " + c + " occurs in positions: " + buf.toString());
            }
            // incorrect guess
            else {
                mOutWriter.println("Miss! Character " + c + " is not in word(s).");
                --incorrectLeft;
            }

            mOutWriter.println(wordPrintingString(wordsStatus));
            mOutWriter.println("Incorrect guesses left: " + incorrectLeft);

            //
            // Update/send feedback to solver for its guess.
            //
            solver.guessFeedback(c, bGuess, foundPositions);

            //
            // Check if game/words solved.
            //
            if (checkSolved(wordCharPosMaps)) {
                bSolved = true;
                mOutWriter.println("Game finished! You have guessed the word, well done!");
            }

            ++iterNum;
        } // end of while loop

        // Check if game solved, if not, need to print out gameover message.
        if (!bSolved) {
            mOutWriter.println("Gameover! You have failed to guess the word and consumed all your tries, try again next time.");
            mOutWriter.print("Word(s) to guess was: ");
            for (int s = 0; s < wordsToGuess.length; ++s) {
                mOutWriter.print(wordsToGuess[s] + " ");
            }
            mOutWriter.println("");
        }

        // add results of game to trace/logs
        mTracer.addEntry(wordsToGuess, charGuesses);
    } // end of runGame()


    /* ************************************************* */
    // Helper methods


    /**
     * Returns formated output string for the current status of guessed words.
     *
     * @param wordStatus Current status of words.
     *
     * @return Formated output string of current status of woreds.
     */
    protected String wordPrintingString(ArrayList<char[]> wordsStatus) {
        StringBuffer buf = new StringBuffer();

        // loop through words
        for (char[] wordStatus : wordsStatus) {
            for (int i = 0; i < wordStatus.length; ++i) {
                // if character guessed/revealed, we print out the character
                if (wordStatus[i] != '\0') {
                    buf.append(wordStatus[i]);
                }
                // if character not guessed/revealed yet, we print out "*"
                else {
                    buf.append("*");
                }
            }
            // space between words
            buf.append(" ");
        } // end of for loop

        return buf.toString();
    } // end of wordPrintingString()


    /**
     * Construct character->position maps for the guessed words.
     *
     * @param wordsToGuess Array of words to guess.
     *
     * @return For each word, character->position maps.
     */
    protected ArrayList< Map<Character, ArrayList<Integer> > >
        constructCharPosMaps(String[] wordsToGuess)
    {
        ArrayList< Map<Character, ArrayList<Integer> > > wordCharPosMaps =
            new ArrayList< Map<Character, ArrayList<Integer> > >();

        // loop through each guessed word
        for (int s = 0; s < wordsToGuess.length; ++s) {
            Map<Character, ArrayList<Integer> > wordCharPosMap =
                new HashMap<Character, ArrayList<Integer> >();
            String currWord = wordsToGuess[s];
            // loop through each character in word
            for (int i = 0; i < currWord.length(); ++i) {
                char c = currWord.charAt(i);
                ArrayList<Integer> lPos = wordCharPosMap.get(c);
                // character not in map
                if (lPos == null) {
                    lPos = new ArrayList<Integer>();
                    lPos.add(i);
                    wordCharPosMap.put(c, lPos);
                }
                // character in map
                else {
                    lPos.add(i);
                }
            } // end of inner for loop

            // add map for current word
            wordCharPosMaps.add(wordCharPosMap);
        } // end of outer for loop

        return wordCharPosMaps;
    } // end of constructCharPosMaps()


    /**
     * Check if game has been solved/words guessed.
     *
     * @param wordCharPosMaps char->position maps for each word.
     *
     * @return True if all words have been guessed/revealed.
     */
    protected boolean checkSolved(ArrayList< Map<Character, ArrayList<Integer> > > wordCharPosMaps)
    {
        // loop through each word/map
        for (Map<Character, ArrayList<Integer> > currMap : wordCharPosMaps) {
            // if map is not empty, then we have not guessed/revealed all words
            if (currMap.size() > 0) {
                return false;
            }
        }

        // we only reach here if all maps are empty, so we have solved game
        return true;
    } // end of checkSolved()

} // end of class HangmanGame
