
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
    private int[][] tableFSM = {{0,0,0},{7,5,2}, {2,2,2},{2,6,2},{7,4,2},{2,5,3},{2,6,2},{7,4,2}};
    private int startingState = 1;
    private static final int invalid = 2;
    private static final int integer = 5;
    private static final int real = 6;
    private static final int identifier = 7;
    private int currentState;
	private int inputLetter = 0;
	private int inputDigit = 1;
	private int inputDot = 2;
	
	
	
	//Constructor
    public LexicalAnalyzer(String filename) {
    	this.filename = filename;
    }
    
	public void start() throws FileNotFoundException, IOException {
	
	try (BufferedReader br = new BufferedReader(new FileReader(filename))){
    	BufferedWriter wr = new BufferedWriter(new FileWriter("text2.txt"));
    	lineNumber = 1;
        
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
    			
    			if(Character.isDigit(charString[0])) {
    				//Call FSM for digit input
    				currentState = tableFSM[startingState][inputDigit];
            		digitsFSM(charString);           			
    			}
    			if(Character.isLetter(charString[0])) {
   				 //fsmIdAndKeyWord(arraySize, splitLine);
    			}
    		}		
			
			
			
    	}
           
        
        //Output all tokens found
        System.out.println("Tokens          Lexeme");
        for (String[] row : output) {
	        System.out.println(row[0] + "          " + row[1]);
	    }
    } 
      
}
	
	//fsm for identifier
//	public void fsmIdAndKeyWord(int arraySize, String spltLine[])
//	{
//		
//		for (int i = 0; i < arraySize; i++){
//			//split the string into char's
//			charString = splitLine[i].toCharArray();
//			
//			if(Character.isLetter(charString[0])){
//				for (int k = 0; k < charString.length; k++){
//					if(Character.isLetter(charString[k])){
//						//If it encounters a letter in the string, make idFail to 1.
//						idFail = 1;
//					}
//					if(Character.isDigit(charString[k])){
//						//If it encounters a digit in the string, make idFail to 1.
//						idFail = 1;
//						if (k == charString.length - 1){
//							//If it encountered a digit, and it is the last char in the string
//							//Make the boolean value to true
//							idNotEndWithLetter = true;
//						}
//					}
//					if(idFail == 0){
//						//If it did not detect a digit or a letter because none of the
//						//idFail were set to 1, that means a non ASCII character was identified
//						idNotDigitOrLetter = true; 
//					}
//					//Set it back to 0 to restart this loop
//					idFail = 0;	
//				}
//				//If there was a non ASCII character detected, it won't be an identifier
//				//If there was a digit at the end of the string, it is not an identifier
//				if(idNotDigitOrLetter == true || idNotEndWithLetter == true){
//					System.out.println(splitLine[i] + " is not an identifier");
//					
//				}
//				//If it did end with a letter, check to see if it is a keyword or not
//				if(idNotEndWithLetter == false){
//					//Check if its a keyword, if not its an identifier
//					for (int j = 0; j < keywordSize; j++){
//	    				if(keywords[j].equals(splitLine[i])){
//	    					System.out.println(splitLine[i] +"IM A KEYWORD");
//	    					output.add(new String[] {"Keyword", splitLine[i]});
//	    					//Set keywordfound to true that you have found a keyword match
//	    					keyWordFound = true;
//	    				}	
//					}	
//				}
//				// If it did end with a letter and the keyword was not found, it is an identifier
//				if(idNotEndWithLetter == false && keyWordFound == false){
//					System.out.println(splitLine[i] +"Im an identifier");
//					output.add(new String[] {"Identifier", splitLine[i]});
//				}
//				
//			//Switch back the boolean values to false to restart the loop with all false.
//			keyWordFound = false; 
//			idNotEndWithLetter = false;  
//			idNotDigitOrLetter = false; 
//			}	
//		}
//	}
	
	public void digitsFSM(char[] charString) {
		String token = "";
		for (int k = 0; k < charString.length; k++){		
			String temp = "";		
			temp += charString[k];			
			boolean endsWithSeparator = checkSeparator(temp);
			boolean endsWithOperator = checkOperator(temp);
			
			//User inputs a digit, progress on the table accordingly
			if(Character.isDigit(charString[k])){
				
				currentState = tableFSM[currentState][inputDigit];
			
			}
			//User inputs a period, progress on the table accordingly
			else if(temp.equals(".")) {
				//If it encounters a dot, it must be a real
				currentState = tableFSM[currentState][inputDot];
			}
			//User inputs an operator, token is finished
			else if(endsWithOperator == true) {
				output.add(new String[] {"Operator", temp});
				System.out.println("input: " + temp );
				break;
											
			}
			//User inputs a separator, token is finished
			else if(endsWithSeparator == true) {
				output.add(new String[] {"Separator", temp});
				System.out.println("input: " + temp );
				break;
											
			}
			//User input is invalid
			else {
				//Any other input is invalid
				System.out.println("input: " + temp + "     current state: " + currentState);
				error();
				break;
			}
			token +=charString[k];
			
			System.out.println("input: " + temp + "     current state: " + currentState);
			
		}
		finishedState(token);
			
			System.out.println("");
	}
	
	
	private void finishedState(String token) {
		switch (currentState){
	    case integer: 
	    	output.add(new String[] {"Integer", token});        
	        break;
	    case real: 
	    	output.add(new String[] {"Real", token});
	        break;
	    case identifier:
	    	output.add(new String[] {"Identifier", token});
	        break;
	    case invalid:
	    	error();
	    	break;
		}
		
	}

	private boolean checkOperator(String temp) {
		boolean isOperator = false;
		for(int i = 0; i < Operator.length; i++) {
			if (Operator[i].equals(temp))
				isOperator = true;
		}					
			return isOperator;
	}

	private boolean checkSeparator(String temp) {
		boolean isSeparator = false;
		for(int i = 0; i < Separator.length; i++) {
			if (Separator[i].equals(temp))
				isSeparator = true;
		}					
			return isSeparator;
	}

	public void error() {
		System.out.println("Error.");
    	System.out.println("Invalid input detected on line: " + lineNumber);
    }
    
}
	
	
	
	




