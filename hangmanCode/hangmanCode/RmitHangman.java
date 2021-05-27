import java.util.*;
import java.io.*;

import game.*;
import solver.*;
import trace.*;

/**
 * Main class for assignment 2, 2020 s2.
 * Implements dictionary reading IO, command line parsing and construction of
 * relevant Hangman solvers.
 * Please read to understand how the progrom works, will be very useful to how
 * you will approach assignment.
 * AVOID MODIFYING.
 *
 * @author Jeffrey Chan, RMIT 2020
 */
public class RmitHangman
{
    /** Name of class, used in error messages. */
    protected static final String mProgName = "RmitHangman";





    /* **************************************************** */

    /**
     * Main method.
     */
    public static void main(String[] args) {

        //
        // Process command line arguments
        //

        // check number of command line arguments
		if (args.length > 5 || args.length < 4) {
			printErrorMsg("Incorrect number of arguments.");
			usage(mProgName);
		}

        // solver type
        String solverType = args[0];
        // dictionary filename
        String dictionaryFilename = args[1];
        // maximum number of incorrect guesses
        int maxIncorrectGuesses = Integer.parseInt(args[2]);
        // string ot guess (must be specified by double quotes "" on command line)
        String toGuess = args[3];
        // (optional) game trace filename
		String gameTraceFilename = null;
		if (args.length == 5) {
			gameTraceFilename = args[4];
		}


        //
        // Create game and solver
        //

        PrintWriter outWriter = null;
        PrintWriter traceWriter = null;
        try {
            // construct in and output streams/writers/readers
            outWriter = new PrintWriter(System.out, true);

            // load dictionary
            Set<String> dictionary = loadDictionary(dictionaryFilename);

            // check if words are in dictionary
            String[] wordsToGuess = toGuess.split(" ");
            if (!checkWordsInDict(dictionary, wordsToGuess)) {
                System.err.println("One or more words to guess are not in dictionary.");
                usage(mProgName);
            }

            // tracer/logger
            // open file first

            if (gameTraceFilename != null) {
                File traceFile = new File(gameTraceFilename);
                // append mode and auto-flush
                traceWriter = new PrintWriter(new FileWriter(traceFile, true), true);
            }
            // create tracer
            HangmanGameTracer tracer = new HangmanGameTracer(traceWriter);

            // create game type
            HangmanGame game = new HangmanGame(outWriter, tracer);;

            // create appropriate game + solver
            HangmanSolver solver = null;
            switch (solverType) {
                case "random":
                    solver = new RandomGuessSolver(dictionary);
                    break;
                case "dict":
                    solver = new DictAwareSolver(dictionary);
                    break;
                case "twoword":
                    // solver = new RandomGuessSolver(dictionary);
                    solver = new TwoWordHangmanGuessSolver(dictionary);
                    break;
                case "wheel":
                    // solver = new RandomGuessSolver(dictionary);
                    solver = new WheelOfFortuneGuessSolver(dictionary);
                    break;
                default:
                    System.err.println(solverType + ": Unknown solver type specified.\n");
                    usage(mProgName);
            }


            //
            // Run game
            //
            game.runGame(wordsToGuess, maxIncorrectGuesses, solver);

        }
        catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
            usage(mProgName);
        }
        catch(IllegalArgumentException e) {
            System.err.println(e.getMessage());
            usage(mProgName);
        }
        catch (IOException e) {
            System.err.println(e.getMessage());
            usage(mProgName);
        }
        finally {
            if (outWriter != null) {
                outWriter.close();
            }
            if (traceWriter != null) {
                traceWriter.close();
            }
        } // end of try-catch-finally block

    } // end of main()


    /* ************************************************ */


    /**
     * Print help/usage message.
     *
     * @param progName Name of program.
     */
     protected static void usage(String progName) {
         printErrorMsg(progName + ": <solver type> <dictionary filename> <maximum incorrect number> <word(s) to guess> [(optional) game trace fileName]");
         printErrorMsg("<solver> = <random | dict | twoword | wheel>");

         System.exit(1);
     } // end of usage


    /**
     * Print error message to System.err.
     *
     * @param msg Error message.
     */
    protected static void printErrorMsg(String msg) {
        System.err.println("> " + msg);
    } // end of printErrorMsg()


    /**
     * Load dictionary.
     *
     * @param dictionaryFilename Filename of dictionary to load.
     *
     * @throws FileNotFoundException If filename not found.
     */
    public static Set<String> loadDictionary(String dictionaryFilename)
        throws FileNotFoundException
    {
        //
        // Construct dictionary
        //
        Set<String> dictionary = new HashSet<String>();

        Scanner scanner = null;
        try {
            File dictFile = new File(dictionaryFilename);
            scanner = new Scanner(dictFile);

            while (scanner.hasNextLine()) {
                String sLine = scanner.nextLine().trim();
                dictionary.add(sLine);
            }
        }
        finally {
            if (scanner != null) {
                scanner.close();
            }
        }

        return dictionary;
    } // end of loadDictionary()


    /**
     * Check if words are in dictionary.
     *
     * @param dictionary Dictionary.
     * @param words Array of words to check whether they are in the dictionary.
     *
     * @return True if all the words are in the dictionary, otherwise false
     *      (including if one or more words are not in the dictionary).
     */
    public static boolean checkWordsInDict(Set<String> dictionary, String[] words) {
        for (String word : words) {
            if (!dictionary.contains(word)) {
                return false;
            }
        }

        // all words in dictionary
        return true;
    } // end of checkWordsInDict()

} // end of class RmitHangman
