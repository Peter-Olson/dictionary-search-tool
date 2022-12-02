/**

   WordFinderGame.java
   
      Choose the number of letters to use for your game ( from 2-15, inclusive ). The game then selects that many random
      letters and displays them. You can also choose to allow for random letters, '?' tiles, also know as 'blank' tiles,
      such as in Scrabble, which can be any letters of the alphabet. Enter any words as you find them, and attempt to find
      as many as you can. You can choose to display the words you have found thus far, and you can also choose to find all
      the words possible.
      
      This game uses the Dictionary.java file for processing and uses the Collin's 15th Edition Dictionary (2015).

   @author Peter Olson
   @version 4/1/18
   @see Dictionary.java

*/

import java.util.Scanner;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.File;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.util.Collections;

public class WordFinderGame {

   public static ArrayList<String> currentList;
   public static Dictionary dct;
   public static int score = 0;
   public static final String RESULT_TXT = "result_word_finder_game.txt";
   public static final String CURRENT_TXT = "current_words_found_finder_game.txt";
   
   private static String letterList = "";

   public static void main( String[] args ) {
      
      Scanner scanner = new Scanner( System.in );
      
      currentList = new ArrayList<String>();
      
      dct = new Dictionary();
      dct.setCurrentDirectory( dct.DIRECTORY_PATH );
      dct.clearFile( CURRENT_TXT );
      dct.clearFile( RESULT_TXT );
      
      SOPln("Welcome to the Word Finder Game!\n\nThis game is similar to many word games where you try and" +
            " find as many words as you can with the letters given.\n" +
            "This game uses Collin's 15th Edition Dictionary (2015).");
      
      SOPln("\nHow many letters would you like to play with?");
      
      int numLetters = scanner.nextInt();
      scanner.nextLine();
      
      SOPln("Would you like to use random letters / blanks?");
      
      boolean useBlanks = false;
      if( isPositiveResponse() )
         useBlanks = true;
      
      letterList = getLetters( numLetters, useBlanks );
      letterList = letterList.toUpperCase();
      
      dct.descramble( letterList, RESULT_TXT );
      
      boolean willContinue = true;
      do {
         SOPln("Letters:\t\t" + letterList + "\n");
         SOPln("Enter a word to add it to your list. Enter the below single letters to do the following:\n" +
               "\tp - print your current list\n\ts - scramble the letters\n\th - see how many words you are missing of each length\n" +
               "\tf - end the game, and see what words you missed and your final score\n" + "\tq - quit");
         String response = scanner.nextLine();
         
         int lengthResponse = response.length();
         if( lengthResponse == 1 )
            willContinue = assessResponse( response );
         else if( lengthResponse > 0 && lengthResponse <= numLetters )
            assessWord( response );
         else
            SOPln("\nPlease enter a word greater than 0 letters long and less than " + numLetters + " long.");
         
         
      } while( willContinue );
      
      scanner.close();
      
   }

   /**
      Gets a random list of letters dependent on:
         1. The number of letters the user wants
         2. The weights of each letter, taken from Scrabble's distribution
         3. Whether or not the user has chosen to use blank letters
      
      @param numLetters The total number of letters in the list
      @param useBlanks True if blanks ('?') are being used, false otherwise (aka variable letters)
      @return String A String of random weighted letters
   */
   public static String getLetters( int numLetters, boolean useBlanks ) {
      Random random = new Random();
      String letterList = "";
      final String[] ALPHABET_LIST = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z","?"};
      int[] weights =                { 9,  2,  2,  4,  12, 2,  3,  2,  9,  1,  1,  4,  2,  6,  8,  2,  1,  6,  4,  6,  4,  2,  2,  1,  2,  1,  2};
      
      //Used to shut out blank letters if they aren't being used
      int VAR_BUFFER = 1;
      if( useBlanks )
         VAR_BUFFER = 0;
      
      //Set the cumulativeWeights, based on the weights, as well as the total weight
      int[] cumulativeWeights = new int[ weights.length ];
      int totalWeight = 0;
      for( int i = 0; i < weights.length; i++ ) {
         totalWeight += weights[i];
         if( i != 0 )
            cumulativeWeights[i] = weights[i] + cumulativeWeights[ i - 1 ];
         else
            cumulativeWeights[i] = weights[i];
      }
      
      //Add random letters based on the weights until the set number of letters is reached
      int count = 0;
      while( count++ < numLetters ) {
         int letterScore = random.nextInt( totalWeight - (VAR_BUFFER * weights[ weights.length - 1 ]) );
         int letterIndex = 0;
         while( letterScore > cumulativeWeights[ letterIndex ] ) {
            letterIndex++;
         }
         
         letterList += ALPHABET_LIST[ letterIndex ];
      }
      
      return letterList;
   }
   
   /**
      Check if the response is a word. If it is, add it to the list and update the score.
      
      @param response The String the user believes is a word
      @param letterList The list of letters that can be used for finding words from
      @see Dictionary.isWord(..)
   */
   private static void assessWord( String response ) {
      response = response.toUpperCase();
      
      if( dct.isWord( response ) && dct.fileContains( RESULT_TXT, response ) ) {
         if( !currentList.contains( response ) ) {
            currentList.add( response );
            score += response.length();
            SOPln( "\n" + response + " is a word! +" + response.length() + " points!\t\tScore: " + score + "\n" );
         } else {
            
            SOPln( "\n" + response + " is a word! But you have already found this word." );
         }
      } else {
         SOPln("\nThat's not a valid word!\n");
      }
         
   }
   
   /**
      Handles the responses for menu selections such as printing the current words found, printing hints,
      and ending the game and showing scores, or simply quickly quitting the game.
      
      @param response The String single-letter response
      @return boolean True if the game is to be continued, false otherwise
   */
   private static boolean assessResponse( String response ) {
      response = response.toUpperCase();
      if( response.equals("P") )
         printCurrentList();
      else if( response.equals("S") )
         scramble( );
      else if( response.equals("H") )
         printHints();
      else if( response.equals("F") ) {
         scores();
         return false;
      } else
         return false;
      
      return true;
   }

   /**
      Scramble the letters to a different orientation
      
      @see assessResponse( String response, String letterList )
   */
   private static void scramble( ) {
      ArrayList<String> list = new ArrayList<String>();
      for( int i = 0; i < letterList.length(); i++ ) {
         list.add( String.valueOf( letterList.charAt(i) ) );
      }
      
      Collections.shuffle( list );
      
      String newList = "";
      for( String letter : list )
         newList += letter;
      
      letterList = newList;
   }

   /**
      Print the current list of words and see the current score
      
      @see Dictionary.write(..)
      @see Dictionary.removeDuplicates(..)
      @see Dictionary.orderIncreasing(..)
      @see Dictionary.alphabetizeSets(..)
      @see Dictionary.printFile(..)
   */
   private static void printCurrentList() {
      SOPln("\nWords found:\n");
   
      dct.write( currentList, CURRENT_TXT );
      dct.removeDuplicates( CURRENT_TXT );
      dct.orderIncreasing( CURRENT_TXT );
      dct.alphabetizeSets( CURRENT_TXT );
      dct.printFile( CURRENT_TXT );
      
      SOPln( "Score: " + score + "\n");
   }

   /**
      Print hints to what words are still missing from each word length.
      
      @see getMaxScore();
      @see getWordsPerTier();
   */
   private static void printHints() {
      dct.write( currentList, CURRENT_TXT );
      dct.removeDuplicates( CURRENT_TXT );
      dct.orderIncreasing( CURRENT_TXT );
      dct.alphabetizeSets( CURRENT_TXT );
   
      int maxScore = getMaxScore();
      int[] numWordsPerTierCurrent = getWordsPerTier( CURRENT_TXT );
      int[] numWordsPerTierResult = getWordsPerTier( RESULT_TXT );
      
      SOPln("");
      
      for( int i = 0; i < numWordsPerTierResult.length; i++ ) {
         int wordsMissing = numWordsPerTierResult[i];
         if( numWordsPerTierCurrent.length > i && numWordsPerTierCurrent[i] != 0 ) {
            wordsMissing = numWordsPerTierResult[i] - numWordsPerTierCurrent[i];
            if( wordsMissing != 0 )
               SOPln( (i + 2) + " letter words missing: " + wordsMissing );
            else
               SOPln( (i + 2) + " letter words missing: None!" );
         } else {
            SOPln( (i + 2) + " letter words missing: All of them! (" + wordsMissing + " total)" );
         }
      }
      
      SOPln("\nYour score: " + score + "\nMax score: " + maxScore + "\n");
   }
   
   /**
      Returns a list of the total number of words found of a given length for all lengths found in the text file
      
      @param fileName The text file to be processed
      @return int[] An array of numbers, each of which correlates to the next total number of words found in the text file,
                    each adjacent element being words one letter longer than the previous one
      @see printHints();
      @see getScanner();
      @see toIntArray( Integer[] intArray );
      @see Arrays.copyOf(..)
   */
   private static int[] getWordsPerTier( String fileName ) {
      Scanner scanner = getScanner( fileName );
      ArrayList<Integer> numList = new ArrayList<Integer>();
      
      int counter = 0;
      int wordSize = -1;
      while( scanner.hasNextLine() ) {
         String word = scanner.nextLine();
         if( wordSize == -1 ) {
            wordSize = word.length();
            counter++;
         } else if( word.length() != wordSize ) {
            numList.add( counter );
            while( ++wordSize != word.length() ) {
               counter = 0;
               numList.add( counter );
            }
            counter = 1;
         } else {
            counter++;
         }
            
      }
      numList.add( counter );
      
      scanner.close();
      
      return toIntArray( Arrays.copyOf( numList.toArray(), numList.size(), Integer[].class ) );
   }
   
   /**
      Convert an Integer array to an int array
      
      @param list The array to be converted to an int array
      @return int[] The resulting int array
      @see getWordsPerTier()
   */
   private static int[] toIntArray( Integer[] list ){
      int[] intArray = new int[ list.length ];
      for( int i = 0; i < intArray.length; i++ )
         intArray[i] = list[i];
      return intArray;
   }
   
   /**
      Finds the max score of a text file of words
      
      @return int The total score of all the words in a file
      @see getScanner();
      @see printHints();
      @see scores();
   */
   private static int getMaxScore() {
      Scanner scanner = getScanner( RESULT_TXT );
      int maxScore = 0;
      while( scanner.hasNextLine() ) {
         String word = scanner.nextLine();
         maxScore += word.length();
      }
      
      scanner.close();
      
      return maxScore;
   }

   /**
      Print your findings and what you missed and the final scores
      
      @see printCurrentList()
      @see Dictionary.printList()
      @see getMaxScore()
   */
   private static void scores() {
      int maxScore = getMaxScore();
      SOPln("\nHere's what you found: ");
      printCurrentList();
      SOPln("Here's what you missed: ");
      removeOverlap();
      dct.printFile( RESULT_TXT );
      SOPln("Your final score: " + score + "\nMax possible score: " + maxScore );
      SOPln("\nGoodbye nerd.\n");
   }

   /**
      Remove all words found in the current list from the results list
      
      @see Dictionary.removeWordsContainingX(..)
      @see scores()
   */
   private static void removeOverlap() {
      for( String word : currentList )
         dct.removeWords( RESULT_TXT, word );
   }

   /**
      Given a text file name, attach a reader and a scanner to that reader to read the text file.
      Throws a FileNotFoundException and return "File not found" is the text file cannot be found.
      
      @param fileName The text file to be read
      @return Scanner The scanner object now attached to the text file
      @see File.getAbsoluteFile()
   */
   private static Scanner getScanner( String fileName ) {
      FileReader fr = null;
      Scanner scan = null;

      try {
         fr = new FileReader( fileName );
         scan = new Scanner( fr );
      } catch( FileNotFoundException e ) {
         try {
            fr = new FileReader( new File( fileName ).getAbsoluteFile() );
            scan = new Scanner( fr );
         } catch( FileNotFoundException e2 ) {
            System.out.println("File not found");
            return null;
         }
      }
      
      return scan;
   }
   
   /**
      Assuming the user will input some form of yes or no, assess the response and return a boolean interpretation
      
      @return boolean True if yes, false if no 
   */
   private static boolean isPositiveResponse( ) {
      Scanner scanner = new Scanner( System.in );
      String response = scanner.nextLine();
      response = response.toUpperCase();
      
      if( response.contains("Y") || response.contains("1") || response.contains("SURE") || response.contains("OK") )
         return true;
      return false;
   }
   
   /**
      Replaces System.out.println(..) because laziness.
      
      @param message The message to print
   */
   private static void SOPln( String message ) {
      System.out.println( message );
   }

}