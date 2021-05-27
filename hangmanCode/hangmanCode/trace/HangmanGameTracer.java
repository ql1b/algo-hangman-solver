package trace;

import java.io.PrintWriter;
import java.util.List;

/**
 * Trace/logger.
 * DO NOT MODIFY.
 *
 * @author Jeffrey Chan, RMIT 2020
 */
public class HangmanGameTracer
{
    /** PrintWriter to write output to. */
    protected PrintWriter mWriter;


    /**
     * Constructor.
     *
     * @param writer PrintWriter to write output to.
     */
    public HangmanGameTracer(PrintWriter writer) {
        mWriter = writer;
    } // end of HangmanGameTracer()


    /**
     * Add entry to trace/log.
     *
     * @param words Words we guessed.
     * @param guesses List of character guesses made.
     */
    public void addEntry(String[] words, List<Character> guesses) {
        if (mWriter != null) {

            mWriter.println(String.join(" ", words) + '|' + guesses.toString().substring(1, 3 * guesses.size() - 1) );
        }
    } // end of addEntry()

} // end of class HangmanGameTracer
