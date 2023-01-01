import java.util.Scanner;
import java.util.Random;
import java.util.ArrayList;

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
   @version 12/29/22
*/

public class Wordle {
   
   public static void main( String[] args ) {
      String response = "";
      do {
         Scanner scanner = new Scanner( System.in );
         SOPln("\nWelcome to Wordle!\n\nWhat length of words do you want to play with?");
         int lengthWords = scanner.nextInt(); scanner.nextLine();
         SOPln("\nHow many guesses do you want?");
         int totalGuesses = scanner.nextInt(); scanner.nextLine();
         String dictionaryFile = lengthWords + "letterwords.txt";
         Dictionary wordleDict = new Dictionary( dictionaryFile, false );
         scanner.close();
         wordle( wordleDict, lengthWords, totalGuesses );
         SOPln("\n\nWould you like to play again?");
         Scanner newScanner = new Scanner(System.in);
         response = newScanner.nextLine().toLowerCase();
         newScanner.close();
      } while( response.contains("y") || response.contains("again") || response.contains("ok") );
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
      Scanner inputScanner = new Scanner( System.in );
      int totalGuesses = 0;
      String guess = "";
      String guessHistory = "";
      String lettersLeft = " A B C D E F G H I J K L M \n N O P Q R S T U V W X Y Z ";
      
      //game loop
      while( totalGuesses <= maxGuesses && !guess.equals( answer ) ) {
         printWordleMenu( guessHistory, lettersLeft );
         guess = inputScanner.nextLine().toUpperCase();
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
      
      inputScanner.close();
      
      if( totalGuesses > maxGuesses )
         SOPln("\nYou ran out of guesses! The word was " + answer + 
               ".\nThe definition of " + answer + " is: " + wordleDict.getDef( answer ) );
      else
         SOPln("\nYou win! The definition of " + answer + " is: " + wordleDict.getDef( answer ) );
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
      
      for( int rep = 0; rep < guessSize; rep++ ) {
         char guessLetter = guess.charAt(rep);
         char answerLetter = answer.charAt(rep);
         
         if( guessLetter == answerLetter )
            clue += CORRECT;
         else if( answer.contains( Character.toString( guessLetter ) ) )
            clue += RIGHT_LETTER_WRONG_SPOT;
         else
            clue += WRONG;
      }
      
      return clue;
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