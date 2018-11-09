import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class SyntaxAnalyzer {

	private String firstFile;
	private String secondFile;
	private String line;
	private String tokenLine;
	private String[] splitLine = null;
	private String[] splitTokenLine = null;
	private ArrayList<String[]> lastTokenInLine = new ArrayList<String[]>();
	private char[] charString;
	private int lineNumber = 0;
	private int invalidLineNumber = 1;
	
	public SyntaxAnalyzer(String firstFile, String secondFile){
		this.firstFile = firstFile;
		this.secondFile = secondFile;
	}
	
	public void start() throws FileNotFoundException, IOException {
		
		try (BufferedReader br = new BufferedReader(new FileReader(secondFile))){
	    	BufferedWriter wr = new BufferedWriter(new FileWriter("syntaxResult.txt"));
	    	
	    	//Checks if there are any invalid tokens in our text file
	    	 while((line = br.readLine())!= null) {
	 	    	splitLine = line.trim().split("\\s+");
	    		if(splitLine[0].matches("Invalid")) {
	    
	    			System.out.println("Detected an Invalid Token on line " + invalidLineNumber + " for " + splitLine[1]);
	    			System.exit(0);
	    		}
	    		invalidLineNumber++;
	    	 }
	    	 //File with lexemes and tokens
	    	 BufferedReader tokenFile = new BufferedReader(new FileReader(secondFile));	
	    	 //File with the original file without token and lexemes separated
	    	 BufferedReader oldFile = new BufferedReader(new FileReader(firstFile));
	    	 //splitTokenLine contains the array that has both token and lexeme stored in the current line
	    	 if(tokenFile.readLine() != null)
	    		 //This skips the first line of the token file that says "Token      Lexeme"
	    		 tokenLine = tokenFile.readLine();
	    	 //while the original file is not the end of the text
	    	 while((line = oldFile.readLine())!= null) {
	         	//Trim whitespaces from Original Text File, line and store in string array
	    		 splitLine = line.trim().split("\\s+");
	    		 //trim whitespaces for token file
	    		 if(tokenFile.readLine() != null) {
	    			 splitTokenLine = tokenLine.trim().split("\\s+");
	    			 }
	    		 
	    		 //For loop to have all the lexemes into an array list. 
	    		 for(int i = 0; i < splitLine.length; i++) {
	    			 //Checks if the the token equals the last character in our orignal text string
	    			 if(splitTokenLine[1].equals(splitLine[splitLine.length - 1])) {
	    				 break;
	    			 }
	    			 //else keep adding all the tokens into an array list
	    			 else
	    				 lastTokenInLine.add(new String[] {splitTokenLine[1]});
	    			 
	    			 
	    			 if(tokenFile.readLine() != null) {
	    				//Read in the next line and store it into splitTokenLine
	    	    		 splitTokenLine = tokenLine.trim().split("\\s+");
	    	    		 System.out.println("Token" + splitTokenLine[0] + " Lexeme " + splitTokenLine[1] );
	    			 }

	    		 }
	    		 
//	    		 for (String[] row : lastTokenInLine) {
//	    	            System.out.println(row[0]);
//	    	        }

	      
	    	 }
	    	
		}
		
	}

	
//	public void functionStatement(String token, String lexeme)
//	{
//		if(token.equals("Identifier")) {
//			if(line=rr.readLine() != null) {
//				System.out.println("<Statement> -> <Assign>");
//				System.out.println("<Assign> -> <Identifier> = <Expression>");
//			}
//			
//		}
//
//	}
	
}
