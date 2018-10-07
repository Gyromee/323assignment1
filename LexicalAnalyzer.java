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
	private static final int keyword = 8;
    private int currentState;
	private int inputLetter = 0;
	private int inputDigit = 1;
	private int inputDot = 2;
	private boolean isOperator;
	private boolean isSeparator;
	
	
	//Constructor
    public LexicalAnalyzer(String filename) {
    	this.filename = filename;
    }
    
	public void start() throws FileNotFoundException, IOException {
	
	try (BufferedReader br = new BufferedReader(new FileReader(filename))){
    	BufferedWriter wr = new BufferedWriter(new FileWriter("text2.txt"));
    	lineNumber = 1;
        
    	//Read in a line one by one
        while((line = br.readLine())!= null) {
        	//Trim whitespaces from line and store in string array
            splitLine = line.trim().split("\\s+");
            
            //arraySize is the the amount of strings that were in the line
            arraySize = splitLine.length;
            
            //keywordSize is the keywords length
            keywordSize = keywords.length;
            
            //split the string into their own chars
    		for (int i = 0; i < arraySize; i++){
    			charString = splitLine[i].toCharArray();
    			//String temp = "";
    			//temp += charString[i];
    			//Determine which finite state machine to use
    			for (int j = 0; j < charString.length; j++) {
    				//Call FSM for digit input
    				if(Character.isDigit(charString[j])) {    				
	    				currentState = tableFSM[startingState][inputDigit];
	            		digitsFSM(charString);  
	            		break;
	    			}
    				//Call FSM for identifiers and reals
    				else if(Character.isLetter(charString[j])) {
	    				currentState = tableFSM[startingState][inputLetter];
	    				IdAndKeyWordFSM(charString);
	    				break;
	    			}
    				//Incase split string is only operators
    				else if(splitLine[i].matches("[=\\^><\\*\\+\\-\\/]+")){
    					currentState = 1;
    					output.add(new String[] {"Operator        ", splitLine[i]});				
    					System.out.println("input: " + splitLine[i] );	
	    				break;
    				}
    				//Incase split string is only Separators
    				else if(splitLine[i].matches("(\\$\\$)|\\(|\\)|\\{|\\}|;|,")){
    					currentState = 1;
    					output.add(new String[] {"Operator        ", splitLine[i]});				
    					System.out.println("input: " + splitLine[i] );	
	    				break;
    				}				
    			}
    		}
    	}							
        //Output all tokens found
        System.out.println("Tokens          Lexeme");
        for (String[] row : output) {
	        System.out.println(row[0] + row[1]);
	    }
    }    
}
	public void IdAndKeyWordFSM(char[] charString){
		String token = "";
		isSeparator = false;
		isOperator = false;
		//Iterate through potential tokens one character at a time
		for (int k = 0; k < charString.length; k++){		
			String temp = "";
			temp += charString[k];			
			isSeparator = checkSeparator(temp);
			isOperator = checkOperator(temp);	
			
			//Check if character is a letter, adjust state accordingly
			if (Character.isLetter(charString[k])) {
				currentState = tableFSM[currentState][inputLetter];				
			}
			//Check if character is a digit, adjust state accordingly
			else if(Character.isDigit(charString[k])){			
				currentState = tableFSM[currentState][inputDigit];			
			}
			//User inputs operator, determine completed tokens
			else if(isOperator == true) {
				//Add the completed token to the output
				if (!token.equals(""))
					finishedState(token);	
				//Check first for double operators such as ==
				if(charString.length -1 > k) {
					String temp2 = "";				
					temp2 += charString[k+1];
					if (checkOperator(temp+temp2)) {
						token = "";		
						output.add(new String[] {"Operator        ", temp+temp2});				
						System.out.println("input: " + temp+temp2 );
						k++;
						continue;
					}
				}
				//
				if (currentState == 4) 
					output.add(new String[] {"Invalid         ", token});							
				token = "";		
				output.add(new String[] {"Operator        ", temp});				
				System.out.println("input: " + temp );					
				continue;			
			}
			//User inputs a separator, determine completed tokens
			else if(isSeparator == true) {
				if (currentState == 4) 
					output.add(new String[] {"Invalid         ", token});				
				if (!token.equals(""))
					finishedState(token);					
				token = "";	
				output.add(new String[] {"Separator       ", temp});				
				System.out.println("input: " + temp );					
				continue;			
			}
			//Keep adding to token until it is complete
			token +=charString[k];			
			
			//Testing outputs and inputs
			System.out.println("input: " + temp + "     current state: " + currentState);		
		}
	
		//Check if the token being built is a keyword or not
		for(int i = 0; i < keywordSize; i++) {
			if(token.equals(keywords[i])) {
				currentState = 8;
				break;
			}
		}
		//Add completed token to list of outputs
		if (!token.equals(""))
			finishedState(token);
		System.out.println("");
	
}
		
	
	public void digitsFSM(char[] charString) {
		String token = "";
		isSeparator = false;
		isOperator = false;
		//Iterate through potential tokens one character at a time
		for (int k = 0; k < charString.length; k++){		
			String temp = "";		
			temp += charString[k];			
			isSeparator = checkSeparator(temp);
			isOperator = checkOperator(temp);
			
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
			else if(isOperator == true) {
				//Add the completed token to the output
				if (!token.equals(""))
					finishedState(token);	
				//Check first for double operators such as ==
				if(charString.length -1 > k) {
					String temp2 = "";				
					temp2 += charString[k+1];
					if (checkOperator(temp+temp2)) {
						token = "";		
						output.add(new String[] {"Operator        ", temp+temp2});				
						System.out.println("input: " + temp+temp2 );	
						k++;
						continue;
					}
				}
				//Check for invalid inputs
				if (currentState == 3) 
					output.add(new String[] {"Invalid         ", token});	
				//Add single operators to list of outputs
				token = "";
				output.add(new String[] {"Operator        ", temp});				
				System.out.println("input: " + temp );				
				continue;
			}
			//User inputs a separator, token is finished
			else if(isSeparator == true) {
				if (currentState == 3) 
					output.add(new String[] {"Invalid         ", token});
				if (!token.equals(""))
					finishedState(token);					
				token = "";	
				output.add(new String[] {"Separator       ", temp});				
				System.out.println("input: " + temp );					
				continue;										
			}
			
			//Keep adding to token until it is complete
			token +=charString[k];		
			
			//Testing outputs and inputs
			System.out.println("input: " + temp + "     current state: " + currentState);		
		}
		
		//Add completed digit token to list of outputs
		if (isSeparator == false && isOperator == false) {
			finishedState(token);
		}			
			
			System.out.println("");
	}
	
	
	private void finishedState(String token) {
		switch (currentState){
	    case integer: 
	    	output.add(new String[] {"Integer         ", token});        
	        break;
	    case real: 
	    	output.add(new String[] {"Real            ", token});
	        break;
	    case identifier:
	    	output.add(new String[] {"Identifier      ", token});
	        break;
		case keyword:
	    	output.add(new String[] {"Keyword         ", token});
	    	break;
	    case invalid:
	    	output.add(new String[] {"Invalid         ", token});
	    	break;
	    default:
	    		break;	    		
		}
		currentState = 1;	
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

	
    
}