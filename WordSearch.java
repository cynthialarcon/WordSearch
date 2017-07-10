/* CS5112 Programming Assignment
 * 10111778 - Kieran McMahon
 * 11139323 - Kevin Murphy
 * 16068858 - Cynthia Alarcon
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
public class WordSearch{
    private char[][] puzzle;
    public ArrayList<String> puzzleWords;
    public int totalChars = 0;
    public int nextWordLoc = 0;
    public static int testCol = 0;
    public static int testRow = 0;
    private String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    String word = "/0";

    public WordSearch(String[] userSpecifiedWords) {
        for(int i = 0; i < userSpecifiedWords.length; i++){  //For loop takes each passed string and 
            String word = userSpecifiedWords[i];             //adds the length of each to totalChars
            totalChars += word.length();                     //this value will be used to generate the
        }                                                    //grid

        puzzleWords = new ArrayList<String>(Arrays.asList(userSpecifiedWords));  
        //New arrayList is a copy of the passed array
        generatePuzzleGrid(totalChars);
    }

    public WordSearch(String wordFile, int wordCount, int shortest, int longest){
        double diff = 0;
        diff = (longest / shortest);
        if(wordCount > 25){                     //Security around word selection, the list of words to be embedded in the puzzle
            wordCount = 25;                     //can contain a max of 25 words.
        }
        if(diff > 3.0){                         //Security around word selection, if a text file with very long words is passed
            longest = shortest * 3;             //and the longest word requested is very large this operation ensures it cannot
        }                                       //be more than three times the shortest requested.

        puzzleWords = new ArrayList<String>();              //New arrayList created
        ArrayList<String> validWords = new ArrayList<String>();
        File words = new File(wordFile);        
        BufferedReader br = null;
        int i = 0;
        int rand = 0;
        try{
            FileReader fr = new FileReader(words);
            br = new BufferedReader(fr);
            String word = br.readLine();
            while(word != null){
                if(word.length() >= shortest){
                    if(word.length() <= longest){               //All the words that satisfy the length arguments
                        if(!validWords.contains(word)){          //moved to a new ArrayList
                            validWords.add(word);
                        }
                    }
                }
                word = br.readLine();            
            }      
        }
        catch(FileNotFoundException e){
            System.out.println("File not found: " + words.toString());
        }
        catch(IOException e){
            System.out.println("Unable to close file: " + words.toString());
        }
        while(i < wordCount){
            rand = (int)(Math.random() * validWords.size());    //Randomly select words from the created validWords list
            String validWord = validWords.get(rand);            //Add to the puzzleWords list. Stop when wordCount is 
            totalChars += validWord.length();                   //satisfied
            puzzleWords.add(validWord);
            i++;
        }
        Collections.sort(validWords);                           //We now have a list of words to be used in generation of the puzzle
        generatePuzzleGrid(totalChars);                         //The puzzle grid is generated, it's dimensions based on the totalChars
        //variable, contains null characters on creation.
    }
    //main method to use
    public void generateWordSearchPuzzle(){
        int wordCounter = 0;
        char toPlace = ' ';                                     //This method is responsible for the full generation of the puzzle, calling other
        fillSpaces();                                           //methods that have been specified in the class.  When this is called by the driver the
        while(wordCounter < puzzleWords.size()){                //constructor has been used, so an instance of the grid exists. Each location contains a 
            word = getWord();                                   //null charcter.  The first operation this method carries out involves replaces all these 
            toPlace = validPlacement(word);                     //nulls with a space.  The loop will continue while iterator is shorter than the number of 
            placeWord(word,toPlace);                            //words to be used, while this is the case there are words to be placed.  A word is selected 
            wordCounter++;                                      //from this list, the word is tested for a valid direction, the valid direction is then passed to
        }                                                       //the place word method, placed. The counter then increments and then next word is placed.
        fillGrid();                                             //Once all words have been placed the remaining spaces are filled with random letters, this completes
    }                                                           //the puzzle.

    private void generatePuzzleGrid(int totalChars){
        double size = totalChars * 1.75;                        //Dimensions of the grid are decided via calculation on the totalChars variable.
        int dimension = (int)(Math.sqrt(size));
        puzzle = new char[dimension][dimension];
    }

    public int getRand(){
        int rand = (int)(Math.random() * puzzle.length);
        return rand;
    }

    private int getRandCoord(){
        int rand = (int)(Math.random() * puzzle.length);
        return rand;
    }

    public String getWord(){
        String word = puzzleWords.get(nextWordLoc);
        nextWordLoc++;
        return word;
    }

    public char validPlacement(String word){
        ArrayList<Character> validDirections;                   //This list can contain N, S, E, W or have a size 0 at end of operation, if size
        validDirections = new ArrayList<Character>();           //is greater than 0 then a valid direction has been identified.
        boolean listPopulated = false;                          //This will remain false while list size = 0.
        int len = word.length();
        boolean north = true;
        boolean south = true;
        boolean east = true;
        boolean west = true;
        int rand = 0;
        char valid = ' ';

        while(!listPopulated){                   //Main body of the directions test, a random coordinate is generated
            validDirections.clear();             //to be tested, the directions tests will commence if there is not an
            testRow = getRand();                 //already placed character at the location                   
            testCol = getRand();
            char test = ' ';
            char letter = ' ';
            north = true;
            south = true;
            east = true;
            west = true;

            //changed for loops so the shouldnt go over the edge of the grid
            if(puzzle[testRow][testCol] == ' '){                //Test the randomly generated location, if it is not empty there is no point
                //in carrying out the directions test, none will pass.
                if(puzzle[testRow][testCol] == ' '){
                    for(int i = testRow; i >= testRow - (len - 1) && north; i--){
                        if(i >= 0 && i < puzzle.length){            //North test, can the word be placed - row numbers?
                            test = puzzle[i][testCol];              //The randomly selected coordinate is used, as well as the length
                            if(alphabet.indexOf(test) >= 0){        //of the word to be tested, if we can span in a northerly direction
                                north = false;                      //from this location (word.length() - 1) times without reaching a 
                            }                                       //character that has been placed or a grid boundary then the boolean remains
                        }else{                                      //true when i = testRow - (len - 1) - the north test has passed.  If we ever do 
                            north = false;                          //arrive at an already placed char or the edge then the boolean is false, this breaks
                        }                                           //the loop, north is then not a potential direction.
                    }

                    for(int i = testRow; i <= testRow + (len - 1) && south; i++){
                        if(i >= 0 && i < puzzle.length){
                            test = puzzle[i][testCol];
                            if(alphabet.indexOf(test) >= 0){        //These tests are the same for each direction, the direction is which we are moving
                                south = false;                      //from the generated location is all that changes
                            }                                       //North = -Rows, South = +Rows, East = +Cols, West = -Cols
                        }else{
                            south = false;
                        }   
                    }

                    for(int i = testCol; i >= testCol - (len - 1) && west; i--){
                        if(i >= 0 && i < puzzle.length){
                            test = puzzle[testRow][i];
                            if(alphabet.indexOf(test) >= 0){
                                west = false;
                            }
                        }else{
                            west = false;
                        }
                    }

                    for(int i = testCol; i <= testCol + (len - 1) && east; i++){
                        if(i >= 0 && i < puzzle.length){
                            test = puzzle[testRow][i];
                            if(alphabet.indexOf(test) >= 0){
                                east = false;
                            }
                        }else{
                            east = false;
                        }
                    }
                }else{
                    north = false;          //Reset the boolean values for the next location to
                    south = false;          //be tested
                    east = false;
                    west = false;
                }

                if(north){                          
                    validDirections.add('N');       //Based on which booleans have remained true we can add chars to the valid directions list
                }

                if(south){
                    validDirections.add('S');
                }

                if(east){
                    validDirections.add('E');
                }

                if(west){
                    validDirections.add('W');
                }

                if(validDirections.size() > 0){     //Having checked the boolean values and adding to the list accordingly if the size is 0 then no valid direction
                    listPopulated = true;           //from the generated location has been found, the while loop will execute again with a new location, this will
                }                                   //continue until a valid direction has been identified.
            }
        }

        rand = (int)(Math.random() * validDirections.size());
        valid = validDirections.get(rand);          //One char value is taken from this list at random, this serves as the return for the method, when it comes 
        //to placing the word this char is passed to the method that does so.
        return valid;    
    }

    public void placeWord(String word, char direction){
        boolean placed = false;
        char toPlace = ' ';                         //As mentioned at end of previous method, the word to be placed and the direction are passed, the word 
        int i = 0;                                  //is then placed on the grid in the direction specified.
        int letPos = 0;
        int len = word.length();
        int row = testRow;
        int col = testCol;

        switch(direction){                          //The placement direction changes based on the char value that has been passed, this is achieved using 
            case 'N':                               //a switch statement.
            letPos = 0;     
            for(i = row; i >= row - (len - 1); i--){
                toPlace = Character.toUpperCase(word.charAt(letPos));   //All words to be placed in upper case in line with common word searches.
                puzzle[i][col] = toPlace;                               //character placed, letPos++ ensures that the next letter in the word 
                letPos++;                                               //will be selected next.
            } 
            break;

            case 'S':
            letPos = 0;
            for(i = row; i <= row + (len - 1); i++){
                toPlace = Character.toUpperCase(word.charAt(letPos));
                puzzle[i][col] = toPlace;
                letPos++;
            }
            break;

            case 'E':
            letPos = 0;
            for(i = col; i <= col + (len - 1); i++){
                toPlace = Character.toUpperCase(word.charAt(letPos));
                puzzle[row][i] = toPlace;
                letPos++;
            }
            break;

            case 'W':
            for(i = col; i >= col - (len - 1); i--){
                toPlace = Character.toUpperCase(word.charAt(letPos));
                puzzle[row][i] = toPlace;
                letPos++;
            }
            break;
        }
    }

    public int getTestCol(){
        return testCol;
    }

    public int getTestRow(){
        return testRow;
    }

    public void fillGrid(){
        char fillWith = ' ';
        for(int i = 0; i < puzzle.length; i++){
            for(int j = 0; j < puzzle.length; j++) {
                fillWith = alphabet.charAt((int)(Math.random() * (alphabet.length() - 1)));
                if(puzzle[i][j] == ' '){
                    puzzle[i][j] = fillWith;                    //Method to fill the grid with random letters from the alphabet specfied 
                }                                               //at top of the class
            }
        }
    }

    public void fillSpaces(){                                   //Method used on grid generation to fill all locations with spaces, this is 
        for(int i = 0; i < puzzle.length; i++){                 //in the interest of testing
            for(int j = 0; j < puzzle.length; j++) {
                puzzle[i][j] = ' ';   
            }
        }
    }

    //new display - as it says on spec
    public void showWordSearchPuzzle(){
        for(int i = 0; i < puzzle.length; i++){
            for(int j = 0; j < puzzle.length; j++) {
                System.out.print(puzzle[i][j]+" ");
            }
            System.out.println("");
        }

        System.out.println();
        System.out.println("------** Word List **------");
        for(int i = 0; i < puzzleWords.size(); i++){
            System.out.print(puzzleWords.get(i) + ", ");
            if(i % 3 == 0 && i != 0){
                System.out.println();
            }
        }
        System.out.println();
        System.out.println("---------------------------");
        System.out.println();
        System.out.println();
    }

    //putting in methods that are required by the spec

    //return the list of words used in the puzzle
    public List<String> getWordSearchList(){
        return puzzleWords;
    }

    //returning puzzle as 2D array grid
    public char[][] getPuzzleAsGrid(){
        return puzzle;
    }

    //returning puzzle as a string
    public String getPuzzleAsString(){
        String puzzleOutput = "\0";
        for(int i = 0; i < puzzle.length; i++){
            for(int j = 0; j < puzzle.length; j++) {
                puzzleOutput += puzzle[i][j];
            }
            System.out.println("\n");
        }
        return puzzleOutput;
    }
}

