
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class LexicalAnalyzer {
	
	private int lineNumber;
	private String[] splitLine = null;
	private String[] keywords = {"if", "else", "ifend", "while", "whileend", "function", "get", "put", "true", "false", "int", "boolean", "real", "return"};
	private String line;
	private String[] Operator = {"==", "=", "^=", ">", "=>", "<", "=<", "*", "/", "+", "-"};
	private String[] Separator = {"$$", "(", ")", "{", "}", ";", ","};
    private String filename;
    private char[] charString;
    private int arraySize;
    private int keywordSize;
    private int idFail = 0;
    private boolean idNotEndWithLetter = false; 
    private boolean idNotDigitOrLetter = false; 
    private boolean keyWordFound = false;
    private ArrayList<String[]> output = new ArrayList<String[]>();
    

	
	//Constructor
    public LexicalAnalyzer(String filename) {
    	this.filename = filename;
    }
    
	public void start() throws FileNotFoundException, IOException {
	
	try (BufferedReader br = new BufferedReader(new FileReader(filename))){
    	BufferedWriter wr = new BufferedWriter(new FileWriter("text2.txt"));
    	lineNumber = 0;
        
        while((line = br.readLine())!= null) {
        	//Trim the line read in by strings and whitespaces
            splitLine = line.trim().split("\\s+");
            
            //arraySize is the the amount of strings that were in the line
            arraySize = splitLine.length;
            
            //keywordSize is the keywords length
            keywordSize = keywords.length;
            
            //split the string into their own chars
            
          

    		for (int i = 0; i < arraySize; i++){
    			charString = splitLine[i].toCharArray();
    		}		
			if(Character.isDigit(charString[0])) {
				//digitsFSM(charString)
			}
			
			if(Character.isLetter(charString[0])) {
				 fsmIdAndKeyWord(arraySize, splitLine);
			}
    	}
            		
    } 
      
}
	
	//fsm for identifier
	public void fsmIdAndKeyWord(int arraySize, String spltLine[])
	{
		
		for (int i = 0; i < arraySize; i++){
			//split the string into char's
			charString = splitLine[i].toCharArray();
			
			if(Character.isLetter(charString[0])){
				for (int k = 0; k < charString.length; k++){
					if(Character.isLetter(charString[k])){
						//If it encounters a letter in the string, make idFail to 1.
						idFail = 1;
					}
					if(Character.isDigit(charString[k])){
						//If it encounters a digit in the string, make idFail to 1.
						idFail = 1;
						if (k == charString.length - 1){
							//If it encountered a digit, and it is the last char in the string
							//Make the boolean value to true
							idNotEndWithLetter = true;
						}
					}
					if(idFail == 0){
						//If it did not detect a digit or a letter because none of the
						//idFail were set to 1, that means a non ASCII character was identified
						idNotDigitOrLetter = true; 
					}
					//Set it back to 0 to restart this loop
					idFail = 0;	
				}
				//If there was a non ASCII character detected, it won't be an identifier
				//If there was a digit at the end of the string, it is not an identifier
				if(idNotDigitOrLetter == true || idNotEndWithLetter == true){
					System.out.println(splitLine[i] + " is not an identifier");
					
				}
				//If it did end with a letter, check to see if it is a keyword or not
				if(idNotEndWithLetter == false){
					//Check if its a keyword, if not its an identifier
					for (int j = 0; j < keywordSize; j++){
	    				if(keywords[j].equals(splitLine[i])){
	    					System.out.println(splitLine[i] +"IM A KEYWORD");
	    					output.add(new String[] {"Keyword", splitLine[i]});
	    					//Set keywordfound to true that you have found a keyword match
	    					keyWordFound = true;
	    				}	
					}	
				}
				// If it did end with a letter and the keyword was not found, it is an identifier
				if(idNotEndWithLetter == false && keyWordFound == false){
					System.out.println(splitLine[i] +"Im an identifier");
					output.add(new String[] {"Identifier", splitLine[i]});
				}
				
			//Switch back the boolean values to false to restart the loop with all false.
			keyWordFound = false; 
			idNotEndWithLetter = false;  
			idNotDigitOrLetter = false; 
			}	
		}
	}
}
	
	
	
	





