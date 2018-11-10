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
		System.out.println("<Rat18F>  ::=   <Opt Function Definitions>");
		Opt_Function_Definitions();
		lex();
		if (!lexeme.equals("$$"))
			error("$$");
		System.out.println("");
		System.out.println("Token: " + token + " Lexeme: " + lexeme);
		Opt_Declaration_List();
		Statement_List();
		lex();
		if (!lexeme.equals("$$"))
			error("$$");
		System.out.println("");
		System.out.println("Token: " + token + " Lexeme: " + lexeme);
	}
	

	public void Opt_Function_Definitions(){
		System.out.println("<Opt Function Definitions> ::= <Function Definitions> ");
		Function_Definitions();
		Empty();
	}
	
	public void Function_Definitions(){
		System.out.println("<Function Definitions>  ::= <Function>");
		Function();
		Function_Definition_Prime();
	}
	
	public void Function_Definition_Prime()
	{
		System.out.println("<Function Definitions Prime> ::= <Function> <Function Definitions Prime> | <Empty>");
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
		System.out.println("<Function> ::= function  <Identifier>   ( <Opt Parameter List> )  <Opt Declaration List>  <Body>");
		//Call the next element and token of the array  
		if(!lexeme.equals("function")) {
			x--;
		
			isEmpty = true;
			return;
		}
		
		lex();
		
		if(!token.matches("^Identifier.*"))
			error();
		System.out.println("<Function> ::= function  <Identifier>");
		//Print token and Lexeme
		System.out.println("");
		System.out.println("Token: " + token + " Lexeme: " + lexeme);
		
		lex();
		if(!lexeme.equals("("))
			error("(");
		System.out.println("");
		System.out.println("Token: " + token + " Lexeme: " + lexeme);
		
		Opt_Parameter_List();
		lex();

		if(!lexeme.equals(")")){
			error(")");
		}
		System.out.println("");
		System.out.println("Token: " + token + " Lexeme: " + lexeme);
		System.out.println("<Function> ::= function  <Identifier>   ( <Opt Parameter List> )  <Opt Declaration List>  <Body>");

		Opt_Declaration_List();
		
		System.out.println("<Function> ::= function  <Identifier>   ( <Opt Parameter List> )  <Opt Declaration List>  <Body>");
		Body();
		
	}
	
	public void Opt_Parameter_List() {
		System.out.println("<Opt Parameter List> ::=  <Parameter List>    |     <Empty>");
		Parameter_List();
		Empty();
	}
	
	public void Parameter_List() {
		System.out.println("<Parameter List>  ::=  <Parameter> <Parameter List Prime>");
		Parameter();
		if(isEmpty == true)
			return;
		Parameter_List_Prime();
		
	}

	public void Parameter_List_Prime() {
		System.out.println("<Parameter List Prime>  ::=  , <Parameter> <Parameter List Prime>    |     <Empty>");
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
		System.out.println("<Parameter> ::=  <IDs > : <Qualifier> ");
		IDs();
		if(isEmpty == true) {
			x--;
			return;
		}
		lex();
		
		if(!lexeme.equals(":"))
			error(":");
		//print out :
		System.out.println("");
		System.out.println("Token: " + token + " Lexeme: " + lexeme);
		System.out.println("<Parameter> ::=  <IDs > : <Qualifier> ");
		
		
		Qualifier();
		if(isEmpty == true) {
			System.out.println("Expecting a qualifier on line: " + lineNumber);
			System.exit(0);
		}

	}
	
	public void Qualifier() {
		System.out.println("<Qualifier> ::= int     |    boolean    |  real ");
		lex();
		if(lexeme.equals("int") || lexeme.equals("boolean") || lexeme.equals("real")) {
			System.out.println("");
			System.out.println("Token: " + token + " Lexeme: " + lexeme);
			System.out.println("R10. <Qualifier> ::= int     |    boolean    |  real ");
		}
		else{
			isEmpty = true;
			return;
		}

	}
	
	public void Body() {
		System.out.println("<Body>  ::=  {  < Statement List>  }");
		lex();
		if(!lexeme.equals("{")) 
			error("{");
		
		Statement_List();
		System.out.println("Token13: " + token + " Lexeme: " + lexeme);
		x++;
		
		System.out.println("Token543: " + token + " Lexeme: " + lexeme);
		lex();
		System.out.println("Tokenr: " + token + " Lexeme: " + lexeme);
		if(!lexeme.equals("}")) {
			System.out.println("Tokennnnn: " + token + " Lexeme: " + lexeme);
			error("}");
		}

	}
	
	public void Opt_Declaration_List() {
		System.out.println("<Opt Declaration List> ::= <Declaration List>   |    <Empty>");
		Declaration_List();
		Empty();
	}
	
	public void Declaration_List() {
		System.out.println("<Declaration List>  ::= <Declaration> <Declaration List Prime>");
		Declaration();
		if(isEmpty == true) {
			System.out.println("<Function> ::= function  <Identifier>   ( <Opt Parameter List> )  <Opt Declaration List>");
			return;
		}
		Declaration_List_Prime();
	}
	
	public void Declaration_List_Prime() {
		lex();
		System.out.println("<Declaration List Prime>  ::= ; <Declaration> <Declaration List Prime>  |  <Empty>");
		System.out.println("");
		System.out.println("Token: " + token + " Lexeme: " + lexeme);
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
		System.out.println("<Declaration> ::=   <Qualifier > <IDs>");
		Qualifier();
		if(isEmpty == true) {
			x--;
			Empty();
			return;
		}
		IDs();
		if(!lexeme.equals(";"))
			error(";");
		if(isEmpty == true) 
			error();
	}
	
	public void IDs()
	{
		System.out.println("<IDs> ::=     <Identifier> <IDs Prime>");
		lex();
		
		if(!token.matches("^Identifier.*")) {
			isEmpty = true;
			return;
		}
			
		System.out.println("");
		System.out.println("Token: " + token + " Lexeme: " + lexeme);
		System.out.println("<IDs> ::=     <Identifier> <IDs Prime>");
		IDs_Prime();
		if(isEmpty == true){
			Empty();
			return;
		}
	}
	
	public void IDs_Prime()
	{
		lex();
		System.out.println("<IDs Prime> ::=	, <Identifier> <IDs Prime> | <Empty>");
		if(!lexeme.equals(",")){
			isEmpty = true;
			x--;
			return;
		}
		else {
			System.out.println("");
			System.out.println("Token: " + token + " Lexeme: " + lexeme);
			lex();
			if(!token.matches("^Identifier.*")) 
				error();
			System.out.println("");
			System.out.println("Token: " + token + " Lexeme: " + lexeme);
			IDs_Prime();
			//or Empty();
		}
	}
	
	public void Statement_List()
	{
		System.out.println("<Statement List> ::=   <Statement> <Statement List Prime>");
		Statement();
		if(isEmpty == true)
			return;
		Statement_List_Prime();
		
	}
	
	public void Statement_List_Prime()
	{
		System.out.println("<Statement List Prime> ::= <Statement> <Statement List Prime> | <Empty>");
		Statement();
		if(isEmpty == true)
			return;
		Statement_List_Prime();
	}

	public void Statement()
	{
		System.out.println("<Statement> ::=   <Compound>  |  <Assign>  |   <If>  |  <Return>   | <Print>   |   <Scan>   |  <While>");
		lex();
		System.out.println("");
		System.out.println("Token: " + token + " Lexeme: " + lexeme);
		if (lexeme.equals("{"))
			Compound();
		else if (token.matches("^Identifier.*"))
			Assign();
		else if (lexeme.equals("if"))
			If();
		else if (lexeme.equals("return"))
			Return();
		else if (lexeme.equals("put"))
			Print();
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
		System.out.println("<Compound> ::=   {  <Statement List>  } ");
		Statement_List();
		lex();
		if(!lexeme.equals("}"))
			error("}");
		System.out.println("");
		System.out.println("Token: " + token + " Lexeme: " + lexeme);
	}
	
	public void Assign()
	{
		System.out.println("<Assign> ::=     <Identifier> = <Expression> ;");
		lex();
		if(!lexeme.equals("=")) 
			error("=");
		
		Expression();
		lex();
		if(!lexeme.equals(";"))
			error(";");
		
	}
	
	public void If()
	{
		System.out.println("<If> ::= if (<Condition>) <Statement> <If Prime>");
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
		System.out.println("<If Prime> ::= ifend |	else  <Statement>  ifend");
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
		System.out.println("<Return> ::=  return <Return Prime>");
		Return_Prime();
	}
	
	public void Return_Prime()
	{
		System.out.println("<Return Prime> ::= ; |  <Expression>;");
		lex();
		if(lexeme.equals(";"))
			return;
		else {
			Expression();
			if(isEmpty == true)
				return;
			else {
				System.out.println("Token3333: " + token + " Lexeme: " + lexeme);
				if(!lexeme.equals(";"))
					error(";");
				else {
					return;
				}
			}
		}
	}
	
	public void Print() {
		System.out.println("<Print> ::=    put ( <Expression>);");
		lex();
		if(!lexeme.equals("("))
			error("(");
		
		System.out.println("");
		System.out.println("Token: " + token + " Lexeme: " + lexeme);
		
		lex();
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
		System.out.println("<Scan> ::=    get ( <IDs> );");
		lex();
		if(!lexeme.equals("("))
			error("(");
		
		IDs();
		if(isEmpty == true)
			error();
		
		lex();
		if(!lexeme.equals(")"))
			error(")");
		
		System.out.println("");
		System.out.println("Token: " + token + " Lexeme: " + lexeme);
		
		lex();
		if(!lexeme.equals(";"))
			error(";");
		
		System.out.println("");
		System.out.println("Token: " + token + " Lexeme: " + lexeme);
	}
	
	public void While() {
		System.out.println("<While> ::=  while ( <Condition>  )  <Statement>  whileend");
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
		System.out.println("");
		System.out.println("Token: " + token + " Lexeme: " + lexeme);

	}
	
	public void Condition()
	{
		System.out.println("<Condition> ::=     <Expression>  <Relop>   <Expression>");
		Expression();
		Relop();
		lex();
		System.out.println("Tokenfuck: " + token + " Lexeme: " + lexeme);
		Expression();
	}
	
    private void Relop() {
    	System.out.println("<Relop> ::=        ==   |   ^=    |   >     |   <    |   =>    |   =<");
		System.out.println("TokenREE: " + token + " Lexeme: " + lexeme);
    	if(lexeme.equals("==")) return;
    	else if(lexeme.equals("^=")) return;
    	else if(lexeme.equals(">")) return;
    	else if(lexeme.equals("<")) return;
    	else if(lexeme.equals("=>")) return;
    	else if(lexeme.equals("=<")) return;
    	else
    		error();
    }
    
    //31
    private void Expression() {
    	System.out.println("<Expression>  ::=    <Term> <Expression Prime>");
    	System.out.println("");
    	System.out.println("Token: " + token + " Lexeme: " + lexeme);
        Term();
        Expression_Prime();
        if(isEmpty == true) {
        	Empty();
        	return;
        }
    }
    
    //32
    private void Expression_Prime() {
    	System.out.println("<Expression Prime>  ::= + <Term> <Expression Prime>  |   - <Term> <Expression Prime>  | <Empty>");
    	lex();;
        if(lexeme.equals("+")) {
            Term();
            Expression_Prime();
        }
        else if(lexeme.equals("-")) {
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
    	System.out.println("<Term>  ::= <Factor> <Term Prime>");
        Factor();
        Term_Prime();        
        if(isEmpty == true) {
        	Empty();
        	return;
        }
    }
    
    //34
    private void Term_Prime() {
    	System.out.println("<Term Prime>  ::= * <Factor> <Term Prime>  |   / <Factor> <Term Prime>  | <Empty>");
    	lex();
    	System.out.println("");
    	System.out.println("Token: " + token + " Lexeme: " + lexeme);
        if(lexeme.equals("*")) {
            Factor();
            Term_Prime();
        }
        else if(lexeme.equals( "/")) {
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
    	System.out.println("<Factor> ::=      -  <Primary>    |    <Primary>");
    	lex();
        if(lexeme.equals("-")) {
            Primary();
        }
        else {
        	x--;
            Primary();
            if(isEmpty == true)
            	return;
        }
    }
    //36
    private void Primary() {
    	System.out.println("<Primary> ::=     <Identifier> <Identifier Prime>  |  <Integer>  |   ( <Expression> )   | <Real>  |   true   |  false");

  
    	lex();
    	System.out.println("");
    	System.out.println("Token: " + token + " Lexeme: " + lexeme);
        if(token.matches("^Identifier.*")) {
        	Identifier_Prime();
        	if(isEmpty == true)
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
        else {
        	isEmpty = true;
        	x--;
        	return;
        }
        
    }
    
    //37
    private void Identifier_Prime() {
        System.out.println("<Identifier Prime> ::= ( <IDs> ) | <Empty>");
        
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
    	System.out.println("<Empty>   ::= E");
    	isEmpty = false;
        return;
    }
	

	
}
