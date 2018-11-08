import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class SyntaxAnalyzer {

	private String filename;
	private String line;
	private String[] splitLine = null;
	private char[] charString;
	private int lineNumber = 0;
	private int invalidLineNumber = 1;
	
	public SyntaxAnalyzer(String filename)
	{
		this.filename=filename;
	}
	
	public void start() throws FileNotFoundException, IOException {
		
		try (BufferedReader br = new BufferedReader(new FileReader(filename))){
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
	    	 BufferedReader rr = new BufferedReader(new FileReader(filename));	
	    	 while((line = rr.readLine())!= null) {
	         	//Trim whitespaces from line and store in string array
	    		 splitLine = line.trim().split("\\s+");
	    		 if(lineNumber == 0)
	    		 {
	    			 lineNumber++;
	    			 continue;
	    		 }
	    		 if(lineNumber > 0) {
		    		System.out.println("Token: " + splitLine[0] + "                  Lexeme: " + splitLine[1]);
		    		//functionStatement(splitLine[0], splitLine[1]);
		    		 
		    		 
	    		 }

	    		 //for(int lineNumber = )
 				
             
	      
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
