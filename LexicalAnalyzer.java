import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JLabel;

public class LexicalAnalyzer {
	
	private int lineNumber = 0;
	private String[] splitLine = null;
	private String[] keywords = {"if", "else", "ifend", "while", "whileend", "function", "get", "put", "true", "false", "int", "boolean", "real", "return"};
	private String line;
	private String[] Operator = {"==", "=", "^=", ">", "=>", "<", "=<", "*", "/", "+", "-"};
	private String[] Separator = {"$$", "(", ")", "{", "}", ";", "," , ":"};
    private String filename;
    private char[] charString;
    private int arraySize;
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
	private boolean isOperatorUpArrow;
	private boolean isSeparatorDollarSign;
	private boolean insideOfComment = false;
	private int lineNumberInvalid = 0;
	private String lineNumberString = "";
	//Constructor
    public LexicalAnalyzer(String input) {
    	this.filename = input;
    }
    
	public void start() throws FileNotFoundException, IOException {
		
	
	try (BufferedReader br = new BufferedReader(new FileReader(filename))){
    	BufferedWriter wr = new BufferedWriter(new FileWriter("text2.txt"));
        
    	//Read in a line one by one
        while((line = br.readLine())!= null) {
        	lineNumber++;
        	lineNumberString = "";
        	lineNumberString += lineNumber;
        	//Trim whitespaces from line and store in string array
            splitLine = line.trim().split("\\s+");
            
            //arraySize is the the amount of strings that were in the line
            arraySize = splitLine.length;
            
            
            //split the string into their own chars
    		for (int i = 0; i < arraySize; i++){
    			charString = splitLine[i].toCharArray();
    			String temp = "";    			
    			//Output for testing; outputs the current token being analyzed
    			//System.out.println("String is: " + splitLine[i]);
    			
    			//Check for comments
    			if (insideOfComment == true && 
    			   (splitLine[i].matches("^\\*\\].*") || splitLine[i].matches("^[^\\s\\[\\*].*\\*\\].*"))) {
    				insideOfComment = false;
    				//If this is the end of a comment, split the token from it and store it with temp.
    				if (splitLine[i].length() > 2) {
    					String afterComment[] = splitLine[i].split("\\*]");
    					charString = afterComment[1].toCharArray();
    					for (int j = 0; j < charString.length; j++) {
    						temp += charString[j];
    					}
    					splitLine[i] = temp;
    					temp = "";
    				}
    			}

    			//Check the input for errors first
    				if(checkForError(charString)) {
    				output.add(new String[] {"Invalid         ", splitLine[i], lineNumberString});
    				setLineNumber(lineNumber);	
    				continue;
    			}

    			
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

    				//Incase split string is only Separators and/or operators
    			    else if(splitLine[i].matches("[(\\$\\$)|\\(|\\)|\\{|\\}|;|,|:\\=\\\\^><\\\\*\\\\+\\\\-\\\\/\\[\\*]+")){
    					currentState = 1;
    					IdAndKeyWordFSM(charString);
    					//output.add(new String[] {"Separator        ", splitLine[i], lineNumberString});				
	    				break;
    				}		
    			}
    		}
    	}							
        
        //Output all tokens found
        wr.write("Tokens          Lexeme" + System.lineSeparator());
        for (String[] row : output) {
            wr.write(row[0] + row[1] + System.lineSeparator());
        }
        wr.close();
	    }
	
		
	
		
    }    

	public void IdAndKeyWordFSM(char[] charString){
		String token = "";
		isSeparator = false;
		isOperator = false;
		isOperatorUpArrow = false;
		isSeparatorDollarSign = false;
		//Iterate through potential tokens one character at a time
		for (int k = 0; k < charString.length; k++){		
			String temp = "";
			temp += charString[k];			
			//these boolean values check if temp is operator, separator, the "^" symbol and "$" symbol
			isSeparator = checkSeparator(temp);
			isOperator = checkOperator(temp);	
			isOperatorUpArrow = checkOperatorUpArrow(temp);
			isSeparatorDollarSign = checkSeparatorDollarSign(temp);
			
			//Check for comments
			if (insideOfComment == true) {
				token = "";
				k = charString.length;
				continue;
			}
			//If the next two char are [*, then we can ignore input until we find *]
			if(charString.length -1 > k) {
				String temp2 = "";				
				temp2 += charString[k+1];
				
				//Submit any completed tokens before the comment block
				if (checkComment(temp,temp2)) {
					if (!token.equals(""))
						finishedState(token);
					token = "";					
					k=k+2;
					
					//Look for *]
					insideOfComment = true;
					while (charString.length -1 > k){
						String temp3 = "";
						temp3 = temp3 + charString[k] + charString[k+1];
						
						//When we find *], break and continue analyzing tokens
						if (temp3.matches(".*\\*\\]$")) {
							insideOfComment = false;
							k++;
							break;
						}
						else {
							insideOfComment = true;
						}
						k++;
					}				
					continue;
				}
			}
			
			//Check if character is a letter, adjust state accordingly
			if (Character.isLetter(charString[k])) {
				currentState = tableFSM[currentState][inputLetter];				
			}
			//Check if character is a digit, adjust state accordingly
			else if(Character.isDigit(charString[k])){			
				currentState = tableFSM[currentState][inputDigit];			
			}
			else if(temp.equals(".")) {
				//If it encounters a dot, it must be a real and if it encountered a dot followed by an invalid, it will print invalid and move on
				finishedState(token);
				currentState = tableFSM[currentState][inputDot];
				if (currentState == 2) {
					output.add(new String[] {"Invalid         ", temp, lineNumberString});
					setLineNumber(lineNumber);	
					token = "";
					currentState = 1;
					continue;
					}
					
				}
			
			//If we encountered a separator 
			else if(isSeparatorDollarSign == true) {
				if(k < charString.length - 1) {
					char temp1;
					temp1 = charString[k+1];
					//if the token starts with $
					if(token.equals("") && temp1 == ('$')) {
						output.add(new String[] {"Separator       ", temp+temp1, lineNumberString});
						token = "";
						k +=1;
						continue;
					}
					//if the token did not start with $
					if(temp1 == ('$') && !token.equals("")) {
						finishedState(token);
						output.add(new String[] {"Separator       ", temp+temp1, lineNumberString});
						token = "";
						k +=1;
						continue;
					}
					//if none of above
					else if (!token.equals("")){
						finishedState(token);
						token = "";
						output.add(new String[] {"Invalid         ", temp, lineNumberString});
						setLineNumber(lineNumber);	
						token = "";
						continue;
					}
					//if this is string is only a $
					else {
						output.add(new String[] {"Invalid         ", temp, lineNumberString});
						setLineNumber(lineNumber);	
						token = "";
						continue;

					}
				}
				else {
					output.add(new String[] {"Invalid         ", temp, lineNumberString});
					setLineNumber(lineNumber);	
					token = "";
					continue;
				}
				
			}
				
			else if(isOperatorUpArrow == true) {
				//check if its the only char in the string and makes sure if there are more inputs in the array
				if(k < charString.length - 1) {
					char temp1;
					temp1 = charString[k+1];
					if(temp1 == ('=')) {
						if(temp.equals("^")){
							finishedState(token);
							output.add(new String[] {"Operator        ", temp+temp1, lineNumberString});
							k += 1;
							token = "";
							continue;

						}
						else {
							//if the length is not more than 1, then it must be the only character
							output.add(new String[] {"Invalid         ", temp, lineNumberString});
							setLineNumber(lineNumber);	
							continue;
						}
					}
					else {
						//if its not the only character in the string and the next symbol is not a "=" then call function
						finishedState(token);
						output.add(new String[] {"Invalid         ", temp, lineNumberString});
						setLineNumber(lineNumber);	
						token = "";
						continue;
					}
					
				}
				else {
					output.add(new String[] {"Invalid         ", temp, lineNumberString});
					setLineNumber(lineNumber);	
					token = "";
					continue;
				}
	

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
						output.add(new String[] {"Operator        ", temp+temp2, lineNumberString});				
						k++;
						continue;
					}
				}
				//
				if (currentState == 4) {
					output.add(new String[] {"Invalid         ", token, lineNumberString});	
					setLineNumber(lineNumber);	
				}
				token = "";		
				output.add(new String[] {"Operator        ", temp, lineNumberString});									
				continue;			
			}
			//User inputs a separator, determine completed tokens
			else if(isSeparator == true) {
				if (currentState == 4) {
					setLineNumber(lineNumber);	
					output.add(new String[] {"Invalid         ", token, lineNumberString});	
				}
				//determines if the token before the separator is a keyword
				if (!token.equals("")) {
					for(int n = 0; n <keywords.length; n++) {
						if(token.equals(keywords[n])) {
							currentState = 8;
							finishedState(token);
						}
					}
					finishedState(token);
				}
				token = "";	
				output.add(new String[] {"Separator       ", temp, lineNumberString});								
				continue;			
			}
			//Keep adding to token until it is complete
			token +=charString[k];			
			
		}
	
		//Check if the token being built is a keyword or not
		if (currentState == 4) {
            output.add(new String[] {"Invalid         ", token, lineNumberString});
            setLineNumber(lineNumber);	   
		}
		for(int i = 0; i < keywords.length; i++) {
			if(token.equals(keywords[i])) {
				currentState = 8;
				break;
			}
		}
		//Add completed token to list of outputs
		if (!token.equals(""))
			finishedState(token);
	
}
		
	
	public void digitsFSM(char[] charString) {
		String token = "";
		isSeparator = false;
		isOperator = false;
		//Iterate through potential tokens one character at a time
		for (int k = 0; k < charString.length; k++){		
			String temp = "";		
			temp += charString[k];			
			//these boolean values check if temp is operator, separator, the "^" symbol and "$" symbol
			isSeparator = checkSeparator(temp);
			isOperator = checkOperator(temp);
			isOperatorUpArrow = checkOperatorUpArrow(temp);
			isSeparatorDollarSign = checkSeparatorDollarSign(temp);
			
			//Check for comments
			if (insideOfComment == true) {
				token = "";
				k++;
				continue;
			}
			//If the next two char are [*, then we can ignore input until we find *]
			if(charString.length -1 > k) {
				String temp2 = "";				
				temp2 += charString[k+1];
				
				//Submit any completed tokens before the comment block
				if (checkComment(temp,temp2)) {
					if (!token.equals(""))
						finishedState(token);
					token = "";					
					k=k+2;
					
					//Look for *]
					insideOfComment = true;
					while (charString.length -1 > k){
						String temp3 = "";
						temp3 = temp3 + charString[k] + charString[k+1];
						
						//When we find *], break and continue analyzing tokens
						if (temp3.matches(".*\\*\\]$")) {
							insideOfComment = false;
							k++;
							break;
						}
						else {
							insideOfComment = true;
						}
						k++;
					}				
					continue;
				}
			}
			
			
			//User inputs a digit, progress on the table accordingly
			if(Character.isDigit(charString[k])){			
				currentState = tableFSM[currentState][inputDigit];			
			}
			if (Character.isLetter(charString[k])) {
				currentState = tableFSM[currentState][inputLetter];
			}
			//User inputs a period, progress on the table accordingly
			else if(temp.equals(".")) {
				//If it encounters a dot, it must be a real
				currentState = tableFSM[currentState][inputDot];
			}
			else if(isSeparatorDollarSign == true) {
				if(k < charString.length - 1) {
					char temp1;
					temp1 = charString[k+1];
					//if the dollar sign is the first input and the next input is also a dollar sign
					if(token.equals("") && temp1 == ('$')) {
						output.add(new String[] {"Separator       ", temp+temp1, lineNumberString});
						token = "";
						k +=1;
						continue;
					}
					//if the next input is a dollar sign and this was not the first character read in, finish the state on previous token then proceed
					if(temp1 == ('$') && !token.equals("")) {
						finishedState(token);
						output.add(new String[] {"Separator       ", temp+temp1, lineNumberString});
						token = "";
						k +=1;
						continue;
					}
					//if the token was not the first input and the currentstate is bad, make it invalid
					else if (!token.equals("")){
						if (currentState == 3) {
							output.add(new String[] {"Invalid         ", token, lineNumberString});	
							setLineNumber(lineNumber);		
						}
						finishedState(token);
						token = "";
						output.add(new String[] {"Invalid         ", temp, lineNumberString});
						setLineNumber(lineNumber);	
						continue;
					}
					else {
						
						output.add(new String[] {"Invalid         ", temp, lineNumberString});
						setLineNumber(lineNumber);	
						token = "";
						continue;
					}
				}
				else {
					output.add(new String[] {"Invalid         ", temp, lineNumberString});
					setLineNumber(lineNumber);	
					token = "";
					continue;
				}
				
			}
				//if it encounters an operator that is "^"
			else if(isOperatorUpArrow == true) {
				//if operator was not the first input read in the string
				if(!token.equals("")) {
					finishedState(token);
					temp = "^";
				}
				//if there are more inputs after the operator
				if(k < charString.length - 1) {
					char temp1;
					temp1 = charString[k+1];
					if(temp1 == ('=')) {
						if(temp.equals("^")){
							output.add(new String[] {"Operator        ", temp+temp1, lineNumberString});
							k += 1;
							token = "";
							continue;

						}
						else {
							output.add(new String[] {"Invalid         ", temp, lineNumberString});
							setLineNumber(lineNumber);	
							continue;
						}
					}
					else {
						output.add(new String[] {"Invalid         ", temp, lineNumberString});
						setLineNumber(lineNumber);	
						token = "";
						continue;
					}
				}
			}
			
			//User inputs an operator, token is finished
			else if(isOperator == true) {
				//Add the completed token to the output
				if (!token.equals("")) {
					finishedState(token);	
				}
				//Check first for double operators such as ==
				if(charString.length -1 > k) {
					String temp2 = "";				
					temp2 += charString[k+1];
					if (checkOperator(temp+temp2)) {
						token = "";		
						output.add(new String[] {"Operator        ", temp+temp2, lineNumberString});				
						k++;
						continue;
					}
				}
				//Check for invalid inputs
				if (currentState == 3) {
					output.add(new String[] {"Invalid         ", token, lineNumberString});	
					setLineNumber(lineNumber);	
				}
				//Add single operators to list of outputs
				token = "";
				output.add(new String[] {"Operator        ", temp, lineNumberString});				
				continue;
			}
			//User inputs a separator, token is finished
			else if(isSeparator == true) {
				if (currentState == 3) {
					output.add(new String[] {"Invalid         ", token, lineNumberString});
					setLineNumber(lineNumber);	
				}
				if (!token.equals(""))
					finishedState(token);					
				token = "";	
				output.add(new String[] {"Separator       ", temp, lineNumberString });								
				continue;										
			}
			
			//Keep adding to token until it is complete
			token +=charString[k];		
		}
		
		//Add completed digit token to list of outputs
		if (isSeparator == false && isOperator == false && !token.equals("")) {
			finishedState(token);
		}			
			
	}
	
	
	private void finishedState(String token) {
		String temp = "";
		temp += lineNumber;
		//switch case that determines which state is which
		switch (currentState){
	    case integer: 
	    	output.add(new String[] {"Integer         ", token, temp, lineNumberString});        
	        break;
	    case real: 
	    	output.add(new String[] {"Real            ", token, temp, lineNumberString});
	        break;
	    case identifier:
	    	output.add(new String[] {"Identifier      ", token, temp, lineNumberString});
	        break;
		case keyword:
	    	output.add(new String[] {"Keyword         ", token, temp, lineNumberString});
	    	break;
	    case invalid:
	    	output.add(new String[] {"Invalid         ", token, temp, lineNumberString});
	    	setLineNumber(lineNumber);	
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
	
	private boolean checkOperatorUpArrow(String temp) {
		boolean isOperatorUpArrow = false;
		if(temp.equals("^")) 
			isOperatorUpArrow = true;
		
		return isOperatorUpArrow;
	}

	private boolean checkSeparatorDollarSign(String temp) {
		boolean isOperatorDollarSign = false;
		if(temp.equals("$"))
			isOperatorDollarSign = true;
		return isOperatorDollarSign;
	}
	
	private boolean checkSeparator(String temp) {
		boolean isSeparator = false;
		for(int i = 0; i < Separator.length; i++) {
			if (Separator[i].equals(temp))
				isSeparator = true;		
		}					
			return isSeparator;
	}
	
	private boolean checkComment(String temp, String temp2) {
		boolean isComment = false;
		String temp3 = temp + temp2;
		if (temp3.equals("[*") || temp3.equals("*]"))
			isComment = true;
		
			return isComment;
	}
	
	private boolean checkForError(char[] charString) {
		boolean hasError = false;
		String temp,temp2,temp3 = "";
		for (int i = 0; i < charString.length; i++) {
			temp = "";
			temp += charString[i];
			if(charString.length -1 > i) {
				temp2 = "";				
				temp2 += charString[i+1];
				temp3 = temp+temp2;
			
			}
			//If input is valid, return false
			if (Character.isDigit(charString[i]) || Character.isLetter(charString[i]) ||
								temp.equals(".") || temp.equals("$") 				  ||
								temp.equals("^") || checkOperator(temp)				  ||
							checkSeparator(temp) ||  temp3.equals("[*") 			  ||
							 temp3.equals("*]"))  {		
				if (temp3.equals("*]")) {
					i++;
				}
			}
			//Any other character input not explicitly stated, return true
			else
				hasError = true;
		}
		return hasError;
	}
	
	public void setLineNumber(int lineNumber) {
		if (lineNumberInvalid == 0 && lineNumberInvalid < lineNumber)
			this.lineNumberInvalid = lineNumber;
			
		
	}
	
	public int getLineNumber()
	{
		return lineNumberInvalid;
	}
	public ArrayList getOutput() {
        return output;
    }
    
}