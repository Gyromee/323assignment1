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
	private ArrayList<String> output = new ArrayList<String>();
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
	    			output.add("Detected an Invalid Token on line " + lexical.getLineNumber() + " for " + splitLine[1]);
	    			writeToFile(wr);
	    		}
	    	 }

  	 	    		    	
		
		lex();
		Rat18F();
		writeToFile(wr);
		}

		
		
	}
	private void writeToFile(BufferedWriter wr) throws IOException {
		for (String row : output) {
            wr.write(row +  System.lineSeparator());
        }
        wr.close();
        System.exit(0);;
	}
	private void lex() {
	    String temp[] = tokensAndLexeme.get(x);
	    token = temp[0];
	    lexeme = temp[1];
	    lineNumber = temp[2];
	    
	    x++;
	}
	private void error(String expectedString) {
        output.add("Error, expected a " + expectedString + " on line " + lineNumber + ".");
        output.add("");
       // x++;
    }
    private void error() {
        output.add("Error, expected " + token + " on line " + lineNumber + ".");
        output.add("");
       // x++;
    }
	
	public void Rat18F(){
		output.add("Token: " + token + " Lexeme: " + lexeme);
		output.add("<Rat18F>  ::=   <Opt Function Definitions>");
		Opt_Function_Definitions();
		lex();
		if (!lexeme.equals("$$")) {
			x--;
			error("$$");
		}
		output.add("");
		output.add("Token: " + token + " Lexeme: " + lexeme);
		Opt_Declaration_List();
		Statement_List();
		lex();
		
		if (!lexeme.equals("$$")) {
			x--;
			error("$$");
		}
		output.add("");
		output.add("Token: " + token + " Lexeme: " + lexeme);
	}
	

	public void Opt_Function_Definitions(){
		output.add("<Opt Function Definitions> ::= <Function Definitions> ");
		Function_Definitions();
		Empty();
	}
	
	public void Function_Definitions(){
		output.add("<Function Definitions>  ::= <Function>");
		Function();
		Function_Definition_Prime();
	}
	
	public void Function_Definition_Prime()
	{
		output.add("<Function Definitions Prime> ::= <Function> <Function Definitions Prime> | <Empty>");
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
		output.add("<Function> ::= function  <Identifier>   ( <Opt Parameter List> )  <Opt Declaration List>  <Body>");
		//Call the next element and token of the array  
		if(!lexeme.equals("function")) {
			x--;
		
			isEmpty = true;
			return;
		}
		
		lex();
		
		if(!token.matches("^Identifier.*")) {
			x--;
			error();
			
		}
			
		output.add("<Function> ::= function  <Identifier>");
		//Print token and Lexeme
		output.add("");
		output.add("Token: " + token + " Lexeme: " + lexeme);
		
		lex();
		if(!lexeme.equals("(")) {
			x--;
			error("(");
		}
		output.add("");
		output.add("Token: " + token + " Lexeme: " + lexeme);
		
		Opt_Parameter_List();
		lex();

		if(!lexeme.equals(")")){
			x--;
			error(")");
		}
		output.add("");
		output.add("Token: " + token + " Lexeme: " + lexeme);
		output.add("<Function> ::= function  <Identifier>   ( <Opt Parameter List> )  <Opt Declaration List>  <Body>");

		Opt_Declaration_List();
		
		output.add("<Function> ::= function  <Identifier>   ( <Opt Parameter List> )  <Opt Declaration List>  <Body>");

		Body();
		
	}
	
	public void Opt_Parameter_List() {
		output.add("<Opt Parameter List> ::=  <Parameter List>    |     <Empty>");
		Parameter_List();
		Empty();
	}
	
	public void Parameter_List() {
		output.add("<Parameter List>  ::=  <Parameter> <Parameter List Prime>");
		Parameter();
		if(isEmpty == true)
			return;
		Parameter_List_Prime();
		
	}

	public void Parameter_List_Prime() {
		output.add("<Parameter List Prime>  ::=  , <Parameter> <Parameter List Prime>    |     <Empty>");
		lex();
		
		if(!lexeme.equals(",")) {
			x--;
			return;
		}
		Parameter();
		if(isEmpty == true) {
			x--;
			error();
		}
		
		Parameter_List_Prime();
		
	}
	
	public void Parameter() {
		output.add("<Parameter> ::=  <IDs > : <Qualifier> ");
		IDs();
		if(isEmpty == true) {
			x--;
			return;
		}
		lex();
		
		if(!lexeme.equals(":")) {
			x--;
			error(":");
		}
		//print out :
		output.add("");
		output.add("Token: " + token + " Lexeme: " + lexeme);
		output.add("<Parameter> ::=  <IDs > : <Qualifier> ");
		
		
		Qualifier();
		if(isEmpty == true) {
			output.add("Expecting a qualifier on line: " + lineNumber);
			System.exit(0);
		}

	}
	
	public void Qualifier() {
		output.add("<Qualifier> ::= int     |    boolean    |  real ");
		lex();
		if(lexeme.equals("int") || lexeme.equals("boolean") || lexeme.equals("real")) {
			output.add("");
			output.add("Token: " + token + " Lexeme: " + lexeme);
			output.add("<Qualifier> ::= int     |    boolean    |  real ");
		}
		else{
			
			isEmpty = true;
			return;
		}

	}
	
	public void Body() {
		output.add("<Body>  ::=  {  < Statement List>  }");
		if(!lexeme.equals("{")) {
			x--;
			error("{");
		}
		lex();
		
		Statement_List();
		x++;

		lex();
		
		if(!lexeme.equals("}")) {
			x--;
			error("}");
		}
		output.add("");
		output.add("Token: " + token + " Lexeme: " + lexeme);

	}
	
	public void Opt_Declaration_List() {
		output.add("<Opt Declaration List> ::= <Declaration List>   |    <Empty>");
		Declaration_List();
		Empty();
	}
	
	public void Declaration_List() {
		output.add("<Declaration List>  ::= <Declaration> <Declaration List Prime>");
		Declaration();
		if(isEmpty == true) {
			output.add("<Function> ::= function  <Identifier>   ( <Opt Parameter List> )  <Opt Declaration List>");
			return;
		}
		Declaration_List_Prime();
	}
	
	public void Declaration_List_Prime() {
        lex();
        output.add("<Declaration List Prime>  ::= ; <Declaration> <Declaration List Prime>  |  <Empty>");

        if(!lexeme.equals(";")) {
            output.add("");
            output.add("Token: " + token + " Lexeme: " + lexeme);
            x--;
            return;
        }
        if(lexeme.equals(";")){
            output.add("");
            output.add("Token: " + token + " Lexeme: " + lexeme);
            Declaration();

        }
        else {
            lex();
            if(lexeme.equals("{")) {
                output.add("");
                output.add("Token: " + token + " Lexeme: " + lexeme);
                x--;
                error("{");
            }
            else
                x--;
            
                
        }
        Declaration();
        if(isEmpty == true) {
            output.add("Expecting a qualifier on line: " + lineNumber);
            System.exit(0);
        }
        Declaration_List_Prime();
    }
	
	public void Declaration()
	{
		output.add("<Declaration> ::=   <Qualifier > <IDs>");
		Qualifier();
		if(isEmpty == true) {
			x--;
			Empty();
			return;
		}
		IDs();
		if(!lexeme.equals(";")) {
			x--;
			error(";");
		}
		if(isEmpty == true) {
			x--;
			error();
		}
	}
	
	public void IDs()
	{
		output.add("<IDs> ::=     <Identifier> <IDs Prime>");
		lex();
		
		if(!token.matches("^Identifier.*")) {
			isEmpty = true;
			return;
		}
			
		output.add("");
		output.add("Token: " + token + " Lexeme: " + lexeme);
		output.add("<IDs> ::=     <Identifier> <IDs Prime>");
		IDs_Prime();
		if(isEmpty == true){
			Empty();
			return;
		}
	}
	
	public void IDs_Prime()
	{
		lex();
		output.add("<IDs Prime> ::=	, <Identifier> <IDs Prime> | <Empty>");
		if(!lexeme.equals(",")){
			isEmpty = true;
			x--;
			return;
		}
		
		else {
			output.add("");
			output.add("Token: " + token + " Lexeme: " + lexeme);
			lex();
			if(!token.matches("^Identifier.*")) {
				x--;
				error();
			}
			output.add("");
			output.add("Token: " + token + " Lexeme: " + lexeme);
			IDs_Prime();
		}
	}
	
	public void Statement_List()
	{
		output.add("<Statement List> ::=   <Statement> <Statement List Prime>");
		Statement();
		if(isEmpty == true)
			return;
			
		Statement_List_Prime();
		
	}
	
	public void Statement_List_Prime()
	{
		output.add("<Statement List Prime> ::= <Statement> <Statement List Prime> | <Empty>");
		Statement();
		if(isEmpty == true) 
			return;
		Statement_List_Prime();
	}

	public void Statement()
	{

		output.add("<Statement> ::=   <Compound>  |  <Assign>  |   <If>  |  <Return>   | <Print>   |   <Scan>   |  <While>");

		lex();
		if (lexeme.equals("{")) {
			output.add("");
	     	output.add("Token: " + token + " Lexeme: " + lexeme);
			Compound();
			
		}
		else if (token.matches("^Identifier.*")) {
			output.add("");
	     	output.add("Token: " + token + " Lexeme: " + lexeme);
	     	Assign();
		}
		else if (lexeme.equals("if")){
			output.add("");
	     	output.add("Token: " + token + " Lexeme: " + lexeme);
	     	If();
		}
		else if (lexeme.equals("return")){
			output.add("");
	     	output.add("Token: " + token + " Lexeme: " + lexeme);
	     	Return();
		}
		else if (lexeme.equals("put")){
			output.add("");
	     	output.add("Token: " + token + " Lexeme: " + lexeme);
	     	Print();
		}
		else if (lexeme.equals("get")){
			output.add("");
	     	output.add("Token: " + token + " Lexeme: " + lexeme);
	     	Scan();
		}
		else if (lexeme.equals("while")){
			output.add("");
	     	output.add("Token: " + token + " Lexeme: " + lexeme);
	     	While();
		}
		else {
			isEmpty = true;
			x--;
			return;
		
		}
		
		
	}
	
	public void Compound()
	{
		output.add("<Compound> ::=   {  <Statement List>  } ");
		Statement_List();
		lex();
		if(!lexeme.equals("}")) {
			x--;
			error("}");
		}
		output.add("");
		output.add("Token: " + token + " Lexeme: " + lexeme);
	}
	
	public void Assign()
	{
		output.add("<Assign> ::=     <Identifier> = <Expression> ;");
		lex();
		if(!lexeme.equals("=")) {
			x--;
			error("=");
		}
		
		Expression();
		lex();
		if(!lexeme.equals(";")) {
			x--;
			error(";");
		}
		
	}
	
	public void If()
	{
		output.add("<If> ::= if (<Condition>) <Statement> <If Prime>");
		lex();
		if(!lexeme.equals("(")) {
			x--;
			error("(");
		}
		Condition();
		lex();
		if(!lexeme.equals(")")) {
			x--;
			error(")");
		}
		Statement();
		If_Prime();
	}
	
	public void If_Prime()
	{
		output.add("<If Prime> ::= ifend |	else  <Statement>  ifend");
		lex();
		if(lexeme.equals("ifend")) {
			output.add("Token: " + token + " Lexeme: " + lexeme);
			return;
		}
		else if (lexeme.equals("^else.*")) {
			Statement();
			if(lexeme.equals("^ifend.*")) {
				output.add("Token: " + token + " Lexeme: " + lexeme);
				return;
			}
	
			else {
				x--;
				error();
			}
				
		}
		else {
			x--; 
			error();
		}
			
		
	}
	
	public void Return() {
		output.add("<Return> ::=  return <Return Prime>");
		Return_Prime();
	}
	
	public void Return_Prime()
	{
		output.add("<Return Prime> ::= ; |  <Expression>;");
		lex();
		if(lexeme.equals(";"))
			return;
		else {
			Expression();
			if(isEmpty == true)
				return;
			else {
				if(!lexeme.equals(";")) {
					x--;
					error(";");
				}
				else {
					return;
				}
			}
		}
	}
	
	public void Print() {
		output.add("<Print> ::=    put ( <Expression>);");
		lex();
		if(!lexeme.equals("(")) {
			x--;
			error("(");
		}
			
		
		output.add("");
		output.add("Token: " + token + " Lexeme: " + lexeme);
		
		//lex();
		Expression();
		if(isEmpty == true) {
			x--;
			error();
		}
		
		lex();
		if(!lexeme.equals(")")) {
			x--;
			error(")");
		}

		lex();
		if(!lexeme.equals(";")) {
			x--;
			error(";");
		}
		output.add("");
     	output.add("Token: " + token + " Lexeme: " + lexeme);
		
	}
	
	public void Scan() {
		output.add("<Scan> ::=    get ( <IDs> );");
		lex();
		if(!lexeme.equals("(")) {
			x--;
			error("(");
		}
		output.add("");
		output.add("Token: " + token + " Lexeme: " + lexeme);
		
		IDs();
		if(isEmpty == true) {
			x--;
			error();
		}
		
		lex();
		if(!lexeme.equals(")")) {
			x--;
			error(")");
		}
		
		
		output.add("");
		output.add("Token: " + token + " Lexeme: " + lexeme);
		
		lex();
		if(!lexeme.equals(";")) {
			x--;
			error(";");
		}
		output.add("");
		output.add("Token: " + token + " Lexeme: " + lexeme);
	}
	
	public void While() {
		output.add("<While> ::=  while ( <Condition>  )  <Statement>  whileend");
		lex();
		if(!lexeme.equals("(")) {
			x--;
			error("(");
		}
		Condition();
		if(isEmpty == true) {
			x--;
			error();
		}
		
		lex();
		if(!lexeme.equals(")")) {
			x--;
			error(")");
		}
		Statement();
		
		lex();
		if(!lexeme.equals("whileend")) {
			x--;
			error("whileend");
		}
		output.add("");
		output.add("Token: " + token + " Lexeme: " + lexeme);

	}
	
	public void Condition()
	{
		output.add("<Condition> ::=     <Expression>  <Relop>   <Expression>");
		Expression();
		lex();
		Relop();
		lex();
		Expression();
	}
	
    private void Relop() {
    	output.add("<Relop> ::=        ==   |   ^=    |   >     |   <    |   =>    |   =<");

    	if(lexeme.equals("==")) return;
    	else if(lexeme.equals("^=")) return;
    	else if(lexeme.equals(">")) return;
    	else if(lexeme.equals("<")) return;
    	else if(lexeme.equals("=>")) return;
    	else if(lexeme.equals("=<")) return;
    	else {
    		x--;
    		error();
    	}
    }
    
    //31
    private void Expression() {
    	output.add("<Expression>  ::=    <Term> <Expression Prime>");
    	output.add("");
    	output.add("TokenHLLOE: " + token + " Lexeme: " + lexeme);
        Term();
        Expression_Prime();
        if(isEmpty == true) {
    
        	Empty();
        	return;
        }
    }
    
    //32
    private void Expression_Prime() {
    	output.add("<Expression Prime>  ::= + <Term> <Expression Prime>  |   - <Term> <Expression Prime>  | <Empty>");
    	lex();
        if(lexeme.equals("+")) {
        	lex();
        	if(lexeme.equals("+"))
        	{
        		error("Detected an extra + operator");
        	}
        	else {
        		x--;
        		Term();
                Expression_Prime();
        	}

        }
        else if(lexeme.equals("-")) {
        	lex();
        	if(lexeme.equals("-"))
        	{
        		error("Detected an extra - operator");
        	}
        	else {
        		x--;
        		Term();
                Expression_Prime();
        	}

        }
        else {
        	isEmpty = true;
        	x--;
        	return;
        }
                  
    }
    
    //33
    private void Term() {    
    	output.add("<Term>  ::= <Factor> <Term Prime>");
        Factor();
        Term_Prime();        
        if(isEmpty == true) {
        	Empty();
        	return;
        }
    }
    
    //34
    private void Term_Prime() {
    	output.add("<Term Prime>  ::= * <Factor> <Term Prime>  |   / <Factor> <Term Prime>  | <Empty>");
    	lex();
        if(lexeme.equals("*")) {
        	output.add("");
        	output.add("Token: " + token + " Lexeme: " + lexeme);
        	lex();
        	
        	if(lexeme.equals("*"))
        	{
        		error("Detected an extra * operator");
        	}
        	else {
        		--x;
           	
                Factor();
                Term_Prime();
        	}
        	
     
        }
        else if(lexeme.equals( "/")) {
        	output.add("");
         	output.add("Token: " + token + " Lexeme: " + lexeme);
          	lex();
        	if(lexeme.equals("/"))
        	{
        		error("Detected an extra / operator");
        	}
        	else {
        		
        		x--;
                Factor();
                Term_Prime();
        	}
        }
        else {
        	output.add("");
         	output.add("Token: " + token + " Lexeme: " + lexeme);
        	isEmpty = true;
        	x--;
        	return;
        }
                
    }
    
    //35
    private void Factor() {
    	output.add("<Factor> ::=      -  <Primary>    |    <Primary>");
    	lex();
        if(lexeme.equals("-")) {
            Primary();
        	output.add("");
	     	output.add("Token: " + token + " Lexeme: " + lexeme);
        }
        else {
        	x--;
            Primary();
            if(isEmpty == true)
            	return;
        }
    }
   
    private void Primary() {
    	output.add("<Primary> ::=     <Identifier> <Identifier Prime>  |  <Integer>  |   ( <Expression> )   | <Real>  |   true   |  false");

  
    	lex();
    	//
        if(token.matches("^Identifier.*")) {
        	output.add("");
        	output.add("TokenFFFFF: " + token + " Lexeme: " + lexeme);
        	Identifier_Prime();
        	if(isEmpty == true)
        		return;
        }
        else if(token.matches("^Integer.*")) {
        	output.add("");
        	output.add("Token: " + token + " Lexeme: " + lexeme);
        	return;
        }

        else if(lexeme.equals("(")) {
        	Expression();
        	if(isEmpty == true) {
        		x--;
        		error();
        	}
        	
        	lex();
        	if(!lexeme.equals(")")) {
        		x--;
        		error(")");
        	}
        	
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
        output.add("<Identifier Prime> ::= ( <IDs> ) | <Empty>");
        
        lex();
        
        if(!lexeme.equals("(")) {
        	isEmpty = true;
        	x--;
            return;
        }
        IDs();
        if(isEmpty == true) {
        	x--;
        	error();
        }
        
        lex();
        if(!lexeme.equals(")")) {
        	x--;
        	error(")");
        }
        
    }
    
    //38
    public void Empty() {
    	output.add("<Empty>   ::= E");
    	isEmpty = false;
        return;
    }
	

	
}
