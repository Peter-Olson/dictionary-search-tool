import java.util.Scanner;
import java.util.Random;
import java.util.ArrayList;
import java.util.HashMap;

/**
   Wordle.java
   
   Play the popular game Wordle, using Collin's 15th Ed. Dictionary.
   
   In this version of Wordle, the player can play the game using words of any length instead of the regular 5-letter words. The
   player can also specify how many guesses they should be allowed to have before they lose.
   
   For each guess made, a series of clues about each letter is given:
   
   O - Correct letter, correct spot
   ? - Correct letter, wrong spot
   X - Wrong letter, not in the word
   
   @author Peter Olson
   @version 1/4/23
*/

public class Wordle {
   
   public static Scanner scanner = new Scanner( System.in );
   
   public static void main( String[] args ) {
      String response = "";
      do {
         SOPln("\nWelcome to Wordle!\n\nWhat length of words do you want to play with?");
         int lengthWords = scanner.nextInt(); scanner.nextLine();
         SOPln("\nHow many guesses do you want?");
         int totalGuesses = scanner.nextInt(); scanner.nextLine();
         String dictionaryFile = lengthWords + "letterwords.txt";
         Dictionary wordleDict = new Dictionary( dictionaryFile, true );
         wordle( wordleDict, lengthWords, totalGuesses );
         SOPln("\n\nWould you like to play again?");
         response = scanner.nextLine().toLowerCase();
      } while( response.contains("y") || response.contains("again") || response.contains("ok") );
      scanner.close();
   }
   
   /**
      Play a game of Wordle, the popular word-guessing game.
      
      In this version of Wordle, you can change the settings for how many guesses you are allowed in the game,
      and the length of the word that you are trying to guess.
      
      The game uses Collin's 15th Edition Dictionary, which has substantially more words than Webster's dictionary
      
      @param wordleDict The dictionary being used for this Wordle game. The dictionary should contain a list of words that are
                        all of the same length
      @param wordLength The length of the word being guessed
      @param maxGuesses The total number of guesses allowed in the game before the player loses
   */
   public static void wordle( Dictionary wordleDict, int wordLength, int maxGuesses ) {
      String answer = getRandomWord( wordleDict );
      int totalGuesses = 0;
      String guess = "";
      String guessHistory = "";
      String lettersLeft = " A B C D E F G H I J K L M \n N O P Q R S T U V W X Y Z ";
      
      //game loop
      while( totalGuesses <= maxGuesses && !guess.equals( answer ) ) {
         printWordleMenu( guessHistory, lettersLeft );
         guess = scanner.nextLine().toUpperCase();
         if( guess.length() != wordLength ) {
            SOPln("Guess needs to be " + wordLength + " letters long.");
            continue;
         }
         totalGuesses++;
         lettersLeft = removeLetters( lettersLeft, guess );
         guessHistory += "\n" + guess.toUpperCase();
         String guessClue = getGuessClue( guess, answer );
         guessHistory += "\n" + guessClue;
      }
      
      if( totalGuesses > maxGuesses )
         SOPln("\nYou ran out of guesses! The word was " + answer + 
               ".\nThe definition of " + answer + " is: " + wordleDict.getDef( answer ) );
      else
         SOPln("\nYou win! The definition of " + answer + " is: " + wordleDict.getDef( answer.toUpperCase() ) );
   }
   
   /**
      Get the clue String based on the player's guess and the actual answer.
      
      The clue String is the same number of characters as the guess and answer, and informs the player
      which letters are in the correct position, which letters are in the word but in the wrong position,
      and which letters are not in the word at all. These symbols are below:
      
      CORRECT - "O"
      RIGHT_LETTER_WRONG_SPOT - "?"
      WRONG - "X"
      
      @param guess The word (doesn't have to be an actual word to be guessed) that the player guessed
      @param answer The actual answer
      @return String The guess clue, composed of characters depending on the CORRECT, RIGHT_LETTER_WRONG_SPOT, and WRONG
                     variables
   */
   private static String getGuessClue( String guess, String answer ) {
      int guessSize = guess.length();
      String clue = "";
      String CORRECT = "O";
      String RIGHT_LETTER_WRONG_SPOT = "?";
      String WRONG = "X";
      
      //Create a map that keeps track of which letters exist in the answer and which letters have multiple appearances
      HashMap<String, Integer> letterCount = new HashMap<String, Integer>();
      char[] answerLetters = answer.toCharArray();
      for( int rep = 0; rep < answerLetters.length; rep++ ) {
         String letter = Character.toString( answerLetters[rep] );
         if( letterCount.containsKey( letter ) )
            letterCount.replace( letter, letterCount.get( letter ), letterCount.get( letter ) + 1 );
         else
            letterCount.put( letter, 1 );
      }

      //Find how many letters are in the right spot and reduce the count for these letters, which
      //is necessary in order to correctly display letters that are in the word but in the wrong spot, but
      //without double-counting anything. There's probably a tricky way to do this all in one loop, but
      //I don't think it's really worth the effort considering how short of words are being iterated
      for( int rep = 0; rep < guessSize; rep++ ) {
         char guessLetter = guess.charAt(rep);
         char answerLetter = answer.charAt(rep);
         String letter = Character.toString( guessLetter );
         
         if( guessLetter == answerLetter )
            reduceLetterCount( letter, letterCount );
      }
      
      //Put together the clue based on the answer and the letter count map
      for( int rep = 0; rep < guessSize; rep++ ) {
         char guessLetter = guess.charAt(rep);
         char answerLetter = answer.charAt(rep);
         String letter = Character.toString( guessLetter );
         
         if( guessLetter == answerLetter ) {
            clue += CORRECT;
         } else if ( letterCount.containsKey( letter ) ) {
            clue += RIGHT_LETTER_WRONG_SPOT;
            reduceLetterCount( letter, letterCount );
         } else {
            clue += WRONG;
         }
      }

      return clue;
   }

   /**
      Reduce the total letter count of the given letter within the answer map

      @param letter The letter whose count is to be reduced
      @param letterCount The map of letters and their counts which represents the answer word
   */
   private static void reduceLetterCount( String letter, HashMap<String, Integer> letterCount ) {
      if( !letterCount.containsKey( letter ) )
         return;
      
      if( letterCount.get( letter ) == 1 )
         letterCount.remove( letter );
      else
         letterCount.replace( letter, letterCount.get( letter ), letterCount.get( letter ) - 1 );
   }
   
   /**
      Removes letters from the list of letters that haven't been guessed, based on the guess given from the player
      
      @param lettersLeft The String of letters left, which is composed of capital single alphabetic letters, and is
                         delimited by spaces, and is in lexicographical order at all times. When a letter is removed,
                         this String is adjusted by removing the guessed letters
      @param guess The guess that the player gave
      @return String The list of letters remaining that the player hasn't guessed yet. This list is delimited by spaces
                     and contains alphabetic, capital letters
   */
   private static String removeLetters( String lettersLeft, String guess ) {
      int guessSize = guess.length();
      
      for( int rep = 0; rep < guessSize; rep++ ) {
         String letter = guess.substring(rep,rep+1) + " ";
         lettersLeft = lettersLeft.replace( letter, "" );
      }
      
      return lettersLeft;
   }
   
   /**
      Prints the contents of the Wordle game, which contains the history of guesses and the guess clues,
      along with the list of letters remaining that has not yet been guessed, and the prompt for the user to
      guess again
      
      @param guessHistory The history of the guesses and their guess clues
      @param lettersLeft A String containing the list of letters left that have not been guessed
   */
   private static void printWordleMenu( String guessHistory, String lettersLeft ) {
      SOPln( guessHistory + "\n\nLetters Not Guessed:\n" + lettersLeft + "\n\nEnter your next guess:" );
   }
   
   /**
      Gets a random word from the dictionary
      
      @return String A random word from the dictionary
   */
   private static String getRandomWord( Dictionary dct ) {
      ArrayList<String> keysAsArray = new ArrayList<String>( dct.keySet() );
      Random r = new Random();
      return keysAsArray.get( r.nextInt( keysAsArray.size() ) );
   }
   
   private static void SOPln( String str ) {
      System.out.println( str );
   }
   
}