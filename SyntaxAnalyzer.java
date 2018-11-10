import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class SyntaxAnalyzer {

	private String filename;
	private String line;
	private String[] splitLine = null;
	private char[] charString;
	private String lineNumber = "";
	private LexicalAnalyzer lexical;
	private String lexeme = "";
    private String token = "";
    private int x = 0;
	private boolean isEmpty = false;
	private ArrayList<String[]> tokensAndLexeme= new ArrayList<String[]>();
	public SyntaxAnalyzer(String filename, LexicalAnalyzer lexical){
		this.filename=filename;
		this.lexical = lexical;
		this.tokensAndLexeme = lexical.getOutput();
		
	}
	
public void start() throws FileNotFoundException, IOException {
		
		try (BufferedReader br = new BufferedReader(new FileReader(filename))){
	    	BufferedWriter wr = new BufferedWriter(new FileWriter("syntaxResult.txt"));
	    	
	    	//Checks if there are any invalid tokens in our text file
	    	 while((line = br.readLine())!= null) {
	 	    	splitLine = line.trim().split("\\s+");
	    		if(splitLine[0].matches("Invalid")) {
	    
	    			System.out.println("Detected an Invalid Token on line " + lexical.getLineNumber() + " for " + splitLine[1]);
	    			System.exit(0);
	    		}
	    	 }

	    	 //prints out the arraylist
//	    	 for (int i=0; i<tokensAndLexeme.size(); i++) {
//	    		 String temp[] = tokensAndLexeme.get(i);
//	             System.out.println(temp[0] +" "+ temp[1] + " " + temp[2]);
//	    	 }	    	 	    		    	
		}
		lex();
		Rat18F();
	}

	private void lex() {
	    String temp[] = tokensAndLexeme.get(x);
	    token = temp[0];
	    lexeme = temp[1];
	    lineNumber = temp[2];
	    x++;
	}
	private void error(String expectedString) {
        System.out.println("Error, expected a " + expectedString + " on line " + lineNumber + ".");
        System.exit(0);
    }
    private void error() {
        System.out.println("Error, expected " + token + " on line " + lineNumber + ".");
        System.exit(0);
    }
	
	public void Rat18F(){
		System.out.println("Token: " + token + " Lexeme: " + lexeme);
		System.out.println("R1. <Rat18F>  ::=   <Opt Function Definitions>");
		Opt_Function_Definitions();
		lex();
		if (!lexeme.equals("$$"))
			error("$$");
		
		Opt_Declaration_List();
		Statement_List();
		lex();
		if (!lexeme.equals("$$"))
			error("$$");
	}
	

	public void Opt_Function_Definitions(){
		System.out.println("R2. <Opt Function Definitions> ::= <Function Definitions> ");
		Function_Definitions();
		Empty();
	}
	
	public void Function_Definitions(){
		System.out.println("R3. <Function Definitions>  ::= <Function>");
		Function();
		Function_Definition_Prime();
	}
	
	public void Function_Definition_Prime()
	{
		if(isEmpty == false) {
			Function();
			Function_Definition_Prime();
		}
		else {
			Empty();
			return;
		}
	}
	
	public void Function()
	{
		//Call the next element and token of the array  
		if(!lexeme.equals("function")) {
			x--;
			isEmpty = true;
			return;
		}
		
		lex();
		
		if(!token.matches("^Identifier.*"))
			error();
		System.out.println("R5. <Function> ::= function  <Identifier>");
		//Print token and Lexeme
		System.out.println("Token: " + token + " Lexeme: " + lexeme);
		
		lex();
		if(!lexeme.equals("("))
			error("(");
		
		System.out.println("Token: " + token + " Lexeme: " + lexeme);
		
		Opt_Parameter_List();
		lex();

		if(!lexeme.equals(")")){
			error(")");
		}
		System.out.println("Token: " + token + " Lexeme: " + lexeme);
		System.out.println("R5. <Function> ::= function  <Identifier> ( <Opt Parameter List> )");

		Opt_Declaration_List();
		
		System.out.println("R5. <Function> ::= function  <Identifier>   ( <Opt Parameter List> )  <Opt Declaration List>  <Body>");
		Body();
		
	}
	
	public void Opt_Parameter_List() {
		System.out.println("R6. <Opt Parameter List> ::=  <Parameter List>    |     <Empty>");
		Parameter_List();
		Empty();
	}
	
	public void Parameter_List() {
		System.out.println("R7. <Parameter List>  ::=  <Parameter> <Parameter List Prime>");
		Parameter();
		if(isEmpty == true)
			return;
		Parameter_List_Prime();
		
	}

	public void Parameter_List_Prime() {
		
		lex();
		
		if(!lexeme.equals(",")) {
			x--;
			return;
		}
		Parameter();
		if(isEmpty == true)
			error();
		
		Parameter_List_Prime();
		
	}
	
	public void Parameter() {
		System.out.println("R9. <Parameter> ::=  <IDs > : <Qualifier> ");
		IDs();
		if(isEmpty == true) {
			x--;
			return;
		}
		lex();
		
		if(!lexeme.equals(":"))
			error(":");
		//print out :
		System.out.println("Token: " + token + " Lexeme: " + lexeme);
		System.out.println("R9. <Parameter> ::=  <IDs > : <Qualifier> ");
		
		
		Qualifier();
		if(isEmpty == true) {
			System.out.println("Expecting a qualifier on line: " + lineNumber);
			System.exit(0);
		}

	}
	
	public void Qualifier() {
		lex();
		if(lexeme.equals("int") || lexeme.equals("boolean") || lexeme.equals("real")) {
			System.out.println("Token: " + token + " Lexeme: " + lexeme);
			System.out.println("R10. <Qualifier> ::= int     |    boolean    |  real ");
		}
		else{
			isEmpty = true;
			return;
		}

	}
	
	public void Body() {
		lex();
		if(!lexeme.equals("{")) 
			error("{");
		System.out.println("Token: " + token + " Lexeme: " + lexeme);
		Statement_List();
		lex();
		if(!lexeme.equals("}"))
			error("}");
		System.out.println("Token: " + token + " Lexeme: " + lexeme);
	}
	
	public void Opt_Declaration_List() {
		System.out.println("R12. <Opt Declaration List> ::= <Declaration List>   |    <Empty>");
		Declaration_List();
		Empty();
	}
	
	public void Declaration_List() {
		Declaration();
		if(isEmpty == true) {
			System.out.println("R5. <Function> ::= function  <Identifier>   ( <Opt Parameter List> )  <Opt Declaration List>");
			return;
		}
		Declaration_List_Prime();
	}
	
	public void Declaration_List_Prime() {
		lex();
	
		if(!lexeme.equals(";")) {
			x--;
			return;
		}
		Declaration();
		if(isEmpty == true) {
			System.out.println("Expecting a qualifier on line: " + lineNumber);
			System.exit(0);
		}
		Declaration_List_Prime();
	}
	
	public void Declaration()
	{
		Qualifier();
		if(isEmpty == true) {
			x--;
			Empty();
			return;
		}
		IDs();
		if(isEmpty == true) 
			error();
	}
	
	public void IDs()
	{
	
		lex();
		
		if(!token.matches("^Identifier.*")) {
			isEmpty = true;
			return;
		}
			

		System.out.println("Token: " + token + " Lexeme: " + lexeme);
		System.out.println("R16. <IDs> ::=     <Identifier> <IDs Prime>");
		IDs_Prime();
		if(isEmpty == true){
			Empty();
			return;
		}
	}
	
	public void IDs_Prime()
	{
		System.out.println("R17. <IDs Prime> ::=	, <Identifier> <IDs Prime> | <Empty>");
		if(!lexeme.equals(",")){
			Empty();
			return;
		}
		
		if(!token.matches("^Identifier.*")) 
			error();
		
		IDs_Prime();
		//or Empty();
		
	}
	
	public void Statement_List()
	{

		Statement();
		if(isEmpty == true)
			return;
		Statement_List_Prime();
		
	}
	
	public void Statement_List_Prime()
	{
		Statement();
		if(isEmpty == true)
			return;
		Statement_List_Prime();
	}

	public void Statement()
	{
		lex();
		System.out.println("Token1: " + token + " Lexeme: " + lexeme);
		if (lexeme.equals("{"))
			Compound();
		else if (token.matches("^Identifier.*"))
			Assign();
		else if (lexeme.equals("if"))
			If();
		else if (lexeme.equals("return"))
			Return();
		else if (lexeme.equals("get"))
			Scan();
		else if (lexeme.equals("while"))
			While();
		else {
			isEmpty = true;
			x--;
			return;
		}
		
		
	}
	
	public void Compound()
	{
		Statement_List();
		lex();
		if(!lexeme.equals("}"))
			error("}");

		System.out.println("Token: " + token + " Lexeme: " + lexeme);
	}
	
	public void Assign()
	{
		lex();
		if(!lexeme.equals("=")) 
			error("=");
		
		Expression();
		lex();
		if(!lexeme.equals(")"))
			error(")");
	}
	
	public void If()
	{
		lex();
		if(!lexeme.equals("("))
			error("(");
		Condition();
		lex();
		if(!lexeme.equals(")"))
			error(")");
		Statement();
		If_Prime();
	}
	
	public void If_Prime()
	{
		lex();
		if(token.matches("^ifend.*"))
			return;
		else if (token.matches("^else.*")) {
			Statement();
			if(token.matches("^ifend.*"))
				return;
			else
				System.exit(0);
		}
		else
			System.exit(0);
		
	}
	
	public void Return() {
		System.out.println("Token2: " + token + " Lexeme: " + lexeme);
		Return_Prime();
	}
	
	public void Return_Prime()
	{
		lex();
		System.out.println("Token3: " + token + " Lexeme: " + lexeme);
		if(lexeme.equals(";"))
			return;
		else {
			Expression();
			if(isEmpty == true)
				error();
			else {
				if(!lexeme.equals(";"))
					error(";");
			}
		}
	}
	
	public void Print() {
		lex();
		if(!lexeme.equals("("))
			error("(");
		
		Expression();
		if(isEmpty == true)
			error();
		lex();
		if(!lexeme.equals(")"))
			error(")");
		lex();
		if(!lexeme.equals(";"))
			error(";");
		
	}
	
	public void Scan() {
		lex();
		if(!lexeme.equals("("))
			error("(");
		
		IDs();
		if(isEmpty == true)
			error();
		
		lex();
		if(!lexeme.equals(")"))
			error(")");
		
		lex();
		if(!lexeme.equals(";"))
			error(";");
	}
	
	public void While() {
		lex();
		if(!lexeme.equals("("))
			error("(");
		
		Condition();
		if(isEmpty == true)
			error();
		
		lex();
		if(!lexeme.equals(")"))
			error(")");
		
		Statement();
		
		lex();
		if(!lexeme.equals("whileend"))
			error();
	}
	
	public void Condition()
	{
		Expression();
		Relop();
		Expression();
	}
	
    private void Relop() {
    	 if(!lexeme.equals("==") || !lexeme.equals("^=") || !lexeme.equals(">") || !lexeme.equals("<") || !lexeme.equals("=>") ||!lexeme.equals("=<")) {
             error();
         }
    	 else
    		 return;
    }
    
    //31
    private void Expression() {
    	System.out.println("Token4: " + token + " Lexeme: " + lexeme);
        Term();
        Expression_Prime();
        if(isEmpty == true) {
        	Empty();
        	return;
        }
    }
    
    //32
    private void Expression_Prime() {
    	lex();
        if(token=="+") {
            Term();
            Expression_Prime();
        }
        else if(token=="-") {
            Term();
            Expression_Prime();
        }
        else {
        	isEmpty = true;
        	x--;
        	return;
        }
                  
    }
    
    //33
    private void Term() {    
        Factor();
        Term_Prime();        
        if(isEmpty == true) {
        	Empty();
        	return;
        }
    }
    
    //34
    private void Term_Prime() {
        if(token == "*") {
            Factor();
            Term_Prime();
        }
        else if(token == "/") {
            Factor();
            Term_Prime();
        }
        else {
        	isEmpty = true;
        	x--;
        	return;
        }
                
    }
    
    //35
    private void Factor() {
        if(lexeme.equals("-")) {
            Primary();
        }
        else {
            Primary();
            if(isEmpty == true)
            	error();
        }
    }
    //36
    private void Primary() {
        if(token.matches("^Identifier.*")) {
        	Identifier_Prime();
        	if(isEmpty == true)
        		error();
        	return;
        }
        else if(token.matches("^Integer.*"))
        	return;
        else if(lexeme.equals("(")) {
        	Expression();
        	if(isEmpty == true)
        		error();
        	
        	lex();
        	if(!lexeme.equals(")"))
        		error(")");
        	
        }
        else if(token.matches("^Real.*"))
        	return;
        else if(lexeme.equals("true"))
        	return;
        else if(lexeme.equals("false"))
        	return;
        else
        	error("Primary");
        
    }
    
    //37
    private void Identifier_Prime() {
        
        if(token != "(") {
        	isEmpty = true;
            return;
        }
        IDs();
        
        if(isEmpty == true)
        	error();
        
        lex();
        if(!lexeme.equals(")"))
        	error(")");
        
    }
    
    //38
    public void Empty() {
    	isEmpty = false;
        return;
    }
	

	
}