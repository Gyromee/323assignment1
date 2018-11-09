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
	
	
	public void Rat18F()
	{
		Opt_Function_Definitions();
		
		
		
	}
	

	public void Opt_Function_Definitions()
	{
		Function_Definitions();
		//or Empty();
	}
	
	public void Function_Definitions()
	{
		Function();
		Function_Definition_Prime();
	
	}
	
	public void Function_Definition_Prime()
	{
		Function();
		Function_Definition_Prime();
		//or Empty();
	}
	
	public void Function()
	{
		//lex()
		//if(token != "function")
		//{
		//	system.out("syntax error");
		//	exit
		//}
		
		//lex()
		//if(token != "identifier")
		//{
		//	system.out("syntax error");
		//	exit
		//}
		
		//lex()
		//if(token != "(")
		//{
		//	system.out("syntax error");
		//	exit
		//}
		
		Opt_Parameter_List();
		
		//lex()
		//if(token != ")")
		//{
		//	system.out("syntax error");
		//	exit
		//}
		
		Opt_Declaration_List();
		Body();
		
	}
	
	public void Opt_Parameter_List() {
		Parameter_List();
		//or Empty();
	}
	
	public void Parameter_List() {
		Parameter();
		//lex()
		//if(token != ",")
		//{
		//	system.out("syntax error");
		//	exit
		//}
		Parameter_List_Prime();
		
	}

	public void Parameter_List_Prime() {
		Parameter();
		//lex()
		//if(token != ",")
		//{
		//	system.out("syntax error");
		//	exit
		//}
		Parameter_List_Prime();
		//or Empty();
	}
	
	public void Parameter() {
		IDs();
		//lex()
		//if(token != ":")
		//{
		//	system.out("syntax error");
		//	exit
		//}
		Qualifier();

	}
	
	public void Qualifier() {
		//if(token != "int" | "bolean" | "real")
		//{
		//	system.out("syntax error");
		//	exit
		//}
	}
	
	public void Body() {
		//if(token != "{")
		//{
		//	system.out("syntax error");
		//	exit
		//}
		Statement_List();
		//if(token != "}")
		//{
		//	system.out("syntax error");
		//	exit
		//}
	}
	
	public void Opt_Declaration_List() {
		Declaration_List();
		//or Empty();
	}
	
	public void Declaration_List() {
		Declaration();
		//if(token != "}")
		//{
		//	system.out("syntax error");
		//	exit
		//}
		Declaration_List_Prime();
	}
	
	public void Declaration_List_Prime() {
		Declaration();
		//if(token != "}")
		//{
		//	system.out("syntax error");
		//	exit
		//}
		Declaration_List_Prime();
		//or Empty();
	}
	
	public void Declaration()
	{
		Qualifier();
		IDs();
	}
	
	public void IDs()
	{
		//if(token != "Identifier")
		//{
		//	system.out("syntax error");
		//	exit
		//}
		IDs_Prime();
	}
	
	public void IDs_Prime()
	{
		//if(token != "Identifier")
		//{
		//	system.out("syntax error");
		//	exit
		//}
		IDs_Prime();
		//or Empty();
		
	}
	
	public void Statement_List()
	{
		Statement();
		Statement_List_Prime();
		
	}
	
	public void Statement_List_Prime()
	{
		Statement();
		Statement_List_Prime();
		//or Empty();
	}

	public void Statement()
	{
		// if (Compound()  |  Assign()  |   If()  |  Return()   | Print()   |   Scan()   |  While())
		// else {
		// error
		//exit;
		//}
	}
	
	public void Compound()
	{
		//if( != "{")
		//{
		//	system.out("syntax error");
		//	exit
		//}
		Statement_List();
		//if(token != "}")
		//{
		//	system.out("syntax error");
		//	exit
		//}
	}
	
	public void Assign()
	{
		//if(token != "Idntifier")
		//{
		//	system.out("syntax error");
		//	exit
		//}
		//if(token != "=")
		//{
		//	system.out("syntax error");
		//	exit
		//}
		Expression();
		//if(token != ";")
		//{
		//	system.out("syntax error");
		//	exit
		//}
	}
	
	public void If()
	{
		//if(token != "if")
		//{
		//	system.out("syntax error");
		//	exit
		//}
		//if(token != "(")
		//{
		//	system.out("syntax error");
		//	exit
		//}
		Condition();
		//if(token != ")")
		//{
		//	system.out("syntax error");
		//	exit
		//}
		Statement();
		If_Prime();
	}
	
	public void If_Prime()
	{
		//lex();
		//if(token == "ifend")
		//{
		//	return;
		//}
		//if(token == "else") {
			Statement();
			//if(token == "ifend")
			//{
			//	return;
			//}
		//}
		//else {
		//	exit;
		//}
		
	}
	
	public void Return()
	{
		//if(token != "return")
		//{
		//	system.out("syntax error");
		//	exit;
		//}
		Return_Prime();
	}
	
	public void Return_Prime()
	{
		//if(token == ";")
		//{
		// return;
		//}
		//OR
		Expression();
		//if(token == ";")
		//{
		// return;
		//}
		//else {
		//exit
		//}
	}
	
	public void Condition()
	{
		Expression();
		Relop();
		Expression();
	}
	
    private void Relop() {
        if(token=="==") {
            return "==;
        }
        else if(token=="^=") {
            return "^=;
        }
        else if(token==">") {
            return ">;
        }
        else if(token=="<") {
            return "<;
        }
        else if(token=="=>") {
            return "=>;
        }
        else if(token=="=<") {
            return "=<;
        }
    }
    
    //31
    private void Expression() {
        Term();
        Expression_Prime();
    }
    
    //32
    private void Expression_Prime() {
        if(token=="+") {
            Term();
            Expression_Prime();
        }
        else if(token=="-") {
            Term();
            Expression_Prime();
        }
        else
            Empty();        
    }
    
    //33
    private void Term() {    
        Factor();
        TermPrime();        
    }
    
    //34
    private void Term_Prime() {
        if(token == "*") {
            Factor();
            TermPrime();
        }
        else if(token == "/") {
            Factor();
            TermPrime();
        }
        else 
            Empty();    
    }
    
    //35
    private Factor() {
        if(token == "-") {
            Primary();
        }
        else
            Primary();
    }
    //36
    private Primary() {
        if(token=="Identifier" || token=="")
    }
    
    //37
    private Identifier_Prime() {
        
        if(token != "(") {
            Empty();
        }
        IDs();
        if(token != ")") {
            //error();
        }
        
    }
    
    //38
    private void Empty() {
        //return epsilon
    }
	

	
}