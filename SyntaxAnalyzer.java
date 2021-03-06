import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.Hashtable;
import java.util.LinkedHashMap;


public class SyntaxAnalyzer {

	private String filename;
	private String line;
	private String[] splitLine = null;
	private String lineNumber = "";
	private LexicalAnalyzer lexical;
	private String lexeme = "";
	private String token = "";
	private int x = 0;
	private boolean isEmpty = false;
	private ArrayList<String[]> tokensAndLexeme= new ArrayList<String[]>();
	private ArrayList<String> output = new ArrayList<String>();
	private boolean indexOut = false;
	private int instr_address = 1;
	private ArrayList<String[]> instr_table = new ArrayList<String[]>();
	private LinkedHashMap<String, Integer> symbolTable = new LinkedHashMap<String, Integer>();
	private LinkedHashMap<Integer, String> symbolTableTypes = new LinkedHashMap<Integer, String>();
	private int memoryAddress = 5000;
	private String save;	
	private ArrayList<Integer> jumpstack = new ArrayList<Integer>();
	String saveType = "";
	String saveOp = "";
	String saveScan = "";
	String saveWhile = "";
	int duplicatePush;
	boolean isInitialized = false;
	String currentIdentifier;
	boolean inAWhileOrIf = false;
	
	public SyntaxAnalyzer(String filename, LexicalAnalyzer lexical){
		this.filename=filename;
		this.lexical = lexical;
		this.tokensAndLexeme = lexical.getOutput();				
	}
	
	public void start() throws FileNotFoundException, IOException {
		
		try (BufferedReader br = new BufferedReader(new FileReader(filename))){
	    	BufferedWriter wr = new BufferedWriter(new FileWriter("Syntax Result.txt"));
	    	
	    	//Checks if there are any invalid tokens in our text file
	    	//if there were, write to a new file and write where it found the invalid and what token it was
	    	 while((line = br.readLine())!= null) {
	    		 // trims the spaces in each line, making them index 0 and 1
	 	    	splitLine = line.trim().split("\\s+");
	    		if(splitLine[0].matches("Invalid")) {
	    			output.add("Detected an Invalid Token on line " + lexical.getLineNumber() + " for " + splitLine[1]);
	    			writeToFile(wr);
	    		}
	    	 }

	   String[] columnNames = {"Address", "Op", "Oprnd"};	   
	   instr_table.add(columnNames); 	 
		//call our text file to grab the next token and lexeme
		lex();
		//Begin syntax analyzer
		Rat18F();
		//Write to the new file
		writeToFile(wr);
		//Write symbol table to new file
		try {
			output_symbolTable();
			output_instr_table();

		} catch (Exception e) {			
			e.printStackTrace();
		}
		System.exit(0);;
	}
		
		
		
	}
	//Writes to a new file 
	private void writeToFile(BufferedWriter wr) throws IOException {
		
		for (String row : output) {
            wr.write(row +  System.lineSeparator());
        }
        wr.close();
        
	}
	
	//Grabs the next token and lexeme
	private void lex() {
		//this if statement makes it so we won't get index out of bounds
		
		if(indexOut == false){
			
		    String temp[] = tokensAndLexeme.get(x);
		    token = temp[0];
		    lexeme = temp[1];
		    lineNumber = temp[2];
		    
		    //If token is an Identifier, then add it to the symbol table
		    if(token.matches("^Identifier.*")) {
		    
		    	//Store the current identifier to check against for future tokens associated with it.
		    	currentIdentifier = lexeme;
		    	
		    	
		    	//Check if it has already been initialized earlier
		    	if (checkSymbolTable(lexeme) && isInitialized == true) {
		    		 output.add("Error, the identifier " + lexeme + " has already been initialized" + ".");
		    	     output.add("");
		    	}
		    	
		    	//Add new identifier to the symbol table
		    	if (!checkSymbolTable(lexeme) && isInitialized == true) {
		    		insertSymbolTable(lexeme, saveType);
		    	}		    	
		    	   	
			}		   
		    //If there are no more symbols and the last one was not $$, then error
		    x++;
		    if(x == tokensAndLexeme.size()) {		    	
		    	indexOut = true;
		    	}
		}    
	}
	//prints out an error with an expected string
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
	//The beginning of the rat18F function
	public void Rat18F(){
		output.add("Token: " + token + " Lexeme: " + lexeme);
		//if the next lexeme does not equal $$, then error out because it needs a $$
		if (!lexeme.equals("$$")) {
			x--;
			error("$$");
		}
		
		//if it did contain $$, then go to this function
		Opt_Declaration_List();
		//next function and last thing in the rat18F
		Statement_List();
		
		
		lex();
		
		//We are done with the syntax. If the file did not end with a $$, then there must be an error
		if (!lexeme.equals("$$")) {
			x--;
			error("$$");
		}
		output.add("");
		output.add("Token: " + token + " Lexeme: " + lexeme);
	}
	
	public void Qualifier() {
		output.add("<Qualifier> ::= int     |    boolean    ");
		lex();
		//Following the rule above, if it doesn't have any of those qualifiers we return
		if(lexeme.equals("int") || lexeme.equals("boolean")) {
			output.add("");
			output.add("Token: " + token + " Lexeme: " + lexeme);
			output.add("<Qualifier> ::= int     |    boolean    ");
			
			//Determine which type to add to symbol table
			switch (lexeme) {
				case "int" : saveType = "Integer";
					break;
				case "boolean" : saveType = "Boolean";
					break;				
				default :
					break;						
			}
			
		}
		else{
			isEmpty = true;
			return;
		}

	}
	
	public void Body() {
		output.add("<Body>  ::=  {  < Statement List>  }");
		// by the rule above, it needs to start with { or error
		if(!lexeme.equals("{")) {
			x--;
			error("{");
		}
		lex();
		
		Statement_List();
		x++;

		lex();
		// by the rule above, it needs to start with } or error
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
		if(isEmpty == true) {
			x--;
			Empty();
			return;
		}
	}
	
	public void Declaration_List() {
		output.add("<Declaration List>  ::= <Declaration> <Declaration List Prime>");
		Declaration();
		if(isEmpty == true) {
			output.add("<Function> ::= function  <Identifier>   ( <Opt Parameter List> )  <Opt Declaration List>");
			return;
		}
		
		lex();
		output.add("Token: " + token + " Lexeme: " + lexeme);
		// by the rule above, if it didnt end with a semicolon then keep recursively calling the prime function
		if(!lexeme.equals(";")) {
			x--;
			error(";");
		}
		Declaration_List_Prime();

	}
	
	public void Declaration_List_Prime() {
		
		output.add("<Declaration List Prime>  ::= ; <Declaration> <Declaration List Prime>  |  <Empty>");

		Declaration();
		if(isEmpty == true) {
			//x--;
			return;
		}
		
		lex();
		output.add("Token: " + token + " Lexeme: " + lexeme);
		if(!lexeme.equals(";")) {
			x--;
			error(";");
		}
		else
			Declaration_List_Prime();
	}
	
	public void Declaration()
	{
		output.add("<Declaration> ::=   <Qualifier > <IDs>");
		Qualifier();
		if(isEmpty == true) {
			return;
		}
		else
			isInitialized = true;
		IDs();
		isInitialized = false;
		if(isEmpty == true) {
			x--;
			error();
		}
	}
	
	public void IDs() {
		output.add("<IDs> ::=     <Identifier> <IDs Prime>");
		lex();
		//if its not an identifer then we kick it out		
		if(!token.matches("^Identifier.*")) {
			isEmpty = true;
			return;
		}
		
		
		
	      
		output.add("");
		output.add("Token: " + token + " Lexeme: " + lexeme);
		saveScan = lexeme;
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
		//if there is no comma then it is empty. no more Ids
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
			//or Empty();
		}
	}
	
	public void Statement_List() {
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

	public void Statement() {

		output.add("<Statement> ::=   <Compound>  |  <Assign>  |   <If>  |  <Return>   | <Print>   |   <Scan>   |  <While>");
		lex();
		
		//if { then go to compound 
		if (lexeme.equals("{")) {
			output.add("");
	     	output.add("Token: " + token + " Lexeme: " + lexeme);
			Compound();
			
		}
		//if there was an identifier it must be assign then go to compound 
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
		
		//at the end of compound it must be a } or it will be error
		if(!lexeme.equals("}")) {
			x--;
			error("}");
		}
		output.add("");
		output.add("Token: " + token + " Lexeme: " + lexeme);
		
	}
	
	public void Assign() {
		
		
        output.add("<Assign> ::=     <Identifier> = <Expression> ;");
        save = lexeme;
        if (!checkSymbolTable(save)) {
        	
        	error("Qualifier");
        }
        lex();
        //since we are in assign, we need to assign something. if there is no equal, we cant assign therefore error
        if(!lexeme.equals("=")) {
            x--;
            error("=");
        }
        
        
        Expression();
        
        if (checkSymbolTable(save))
        gen_instr("POPM", get_address(save));
        
        lex();
        
        if(!lexeme.equals(";")) {
            x--;
            error(";");
        }
        
        
    }
	
	public void If()
	{
		inAWhileOrIf = true;
		output.add("<If> ::= if (<Condition>) <Statement> <If Prime>");		
		lex();
		//following rule above, we need a parenthesis
		if(!lexeme.equals("(")) {
			x--;
			error("(");
		}
		Condition();
		lex();
		inAWhileOrIf = false;
		//following rule above, we need to have end parenthesis after running those functions
		if(!lexeme.equals(")")) {
			x--;
			error(")");
		}
		Statement();
		back_patch(instr_address);
		If_Prime();
        lex();
        //check after whileend is $$, if not then there are most statements we must find
        if(lexeme.equals("$$")) {
            x--;
            isEmpty = true;
            return;
        }
        else {
            x--;
            Empty();
            return;
        }
	}
	
	public void If_Prime(){
		
		output.add("<If Prime> ::= ifend |	else  <Statement>  ifend");
		lex();
		//if there was ifend then we are done with if statement
		if(lexeme.equals("ifend")) {			
			output.add("Token: " + token + " Lexeme: " + lexeme);
			return;
		}
		//if it did mean else
		else if (lexeme.equals("else")) {
			output.add("");
			output.add("Token: " + token + " Lexeme: " + lexeme);
			Statement();
			lex();
			//check if it is ifend. if not then there must be error
			if(lexeme.equals("ifend")) {
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
		//if semicolon, then it marks the ending. return
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
		//must have parenthesis
		if(!lexeme.equals("(")) {
			x--;
			error("(");
		}
			
		
		output.add("");
		output.add("Token: " + token + " Lexeme: " + lexeme);
		
		lex();
		Expression();
		if(isEmpty == true) {
			x--;
			error();
		}
		
		lex();
		//must have parenthesis
		if(!lexeme.equals(")")) {
			x--;
			error(")");
		}

		lex();
		//must have semicolon
		if(!lexeme.equals(";")) {
			x--;
			error(";");
		}
		output.add("");
     	output.add("Token: " + token + " Lexeme: " + lexeme);
     	gen_instr("STDOUT", "nil");
		
	}
	
	public void Scan() {
		output.add("<Scan> ::=    get ( <IDs> );");		
		lex();
		//must have parenthesis
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
		
		//must have parenthesis
		if(!lexeme.equals(")")) {
			x--;
			error(")");
		}
		
		
		output.add("");
		output.add("Token: " + token + " Lexeme: " + lexeme);
		
		lex();
		//must have semicolon
		if(!lexeme.equals(";")) {
			x--;
			error(";");
		}
		output.add("");
		output.add("Token: " + token + " Lexeme: " + lexeme);
		gen_instr("STDIN", "nil");
		gen_instr("POPM", get_address(saveScan));
	}
	
	public void While() {
		inAWhileOrIf = true;
        output.add("<While> ::=  while ( <Condition>  )  <Statement>  whileend");
        String addr = "";
        addr += instr_address;
        gen_instr("LABEL", "nil");
        
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
        
        inAWhileOrIf = false;
        if(!lexeme.equals(")")) {
            x--;
            error(")");
        }
        
        Statement();
        
        gen_instr("JUMP", addr);
        back_patch(instr_address);
        
        lex();
        
        if(!lexeme.equals("whileend")) {
            x--;
            error("whileend");
        }
        output.add("");
        output.add("Token: " + token + " Lexeme: " + lexeme);
        lex();
        //check after whileend is $$, if not then there are most statements we must find
        if(lexeme.equals("$$")) {
            x--;
            isEmpty = true;
            return;
        }
        else {
            x--;
            Empty();
            return;
        }
        
    }
	
	public void Condition(){
		output.add("<Condition> ::=     <Expression>  <Relop>   <Expression>");
		// calls each function with respect to the above rule
		
		Expression();
		
		lex();
		Relop();		
		lex();
		Expression();
		
		switch (saveOp) {
		case "==" : {
			gen_instr("EQU", "nil");
    		push_jumpstack(instr_address);
    		gen_instr("JUMPZ", "nil");
		}		
			break;
		case "^=" : {
			gen_instr("NEQ", "nil");
    		push_jumpstack(instr_address);
    		gen_instr("JUMPZ", "nil");
		}		
			break;
		case ">" : { 
			gen_instr("GRT", "nil");
    		push_jumpstack(instr_address);
    		gen_instr("JUMPZ", "nil");
		}		
			break;
		case "<" : {
			gen_instr("LES", "nil");
    		push_jumpstack(instr_address);
    		gen_instr("JUMPZ", "nil");
		}		
			break;
		case "=>" : { 
			gen_instr("GEQ", "nil");
    		push_jumpstack(instr_address);
    		gen_instr("JUMPZ", "nil");
		}		
			break;
		case "=<" : {
			gen_instr("LEQ", "nil");
    		push_jumpstack(instr_address);
    		gen_instr("JUMPZ", "nil");
		}		
			break;
		default :
			break;
		}	
	}
	
    private void Relop() {
    	output.add("<Relop> ::=        ==   |   ^=    |   >     |   <    |   =>    |   =<");
    	//checks each relop
    	saveOp = lexeme;
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
    	output.add("Token: " + token + " Lexeme: " + lexeme);
    	duplicatePush = 0;
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
    	
    	//Check if the value is of type boolean before performing arithmetic.
    	
    	if(lexeme.equals("+") || lexeme.equals("-") || lexeme.equals("*") || lexeme.equals("/")) {
    		if (inAWhileOrIf == true) save = saveWhile; 	    	
	    	if (symbolTableTypes.get(symbolTable.get(save)).equals("Boolean")) {
    		output.add("Error, arithmetic is not allowed for boolean values.");
            output.add("");
            System.out.println(lexeme);
	    	}
    	}
    	
    	lex();
    	    	
        if(lexeme.equals("+")) {
        	lex();
        	//checks if there are multiple op signs that werent caught in our lexical analyzer
        	if(lexeme.equals("+") || lexeme.equals("-") || lexeme.equals("*") || lexeme.equals("/"))
        	{
        		x--;
        		error("Detected an extra + or - or * or / operator");
        	}
        	else {
        		x--;
        		Term();
        		gen_instr("ADD", "nil");
                Expression_Prime();
        	}

        }
        else if(lexeme.equals("-")) {
        	lex();
        	//checks if there are multiple op signs that werent caught in our lexical analyzer
        	if(lexeme.equals("+") || lexeme.equals("-") || lexeme.equals("*") || lexeme.equals("/"))
        	{
        		x--;
        		error("Detected an extra + or - or * or / operator");
        	}
        	else {
        		x--;
        		Term();
        		gen_instr("SUB", "nil");
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
        	//checks if there are multiple op signs that werent caught in our lexical analyzer
        	if(lexeme.equals("+") || lexeme.equals("-") || lexeme.equals("*") || lexeme.equals("/"))
        	{
        		x--;
        		error("Detected an extra + or - or * or / operator");
        	}
        	else {
        		--x;
           	
                Factor();
                gen_instr("MUL", "nil");
                Term_Prime();
        	}
        	
     
        }
        else if(lexeme.equals( "/")) {
        	output.add("");
         	output.add("Token: " + token + " Lexeme: " + lexeme);
          	lex();
          	//checks if there are multiple op signs that werent caught in our lexical analyzer
          	if(lexeme.equals("+") || lexeme.equals("-") || lexeme.equals("*") || lexeme.equals("/"))
        	{
        		x--;
        		error("Detected an extra + or - or * or / operator");
        	}
        	else {
        		
        		x--;
                Factor();
                gen_instr("DIV", "nil");
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
    	
    	if(token.matches("^Identifier.*")) {
            save = lexeme;
    		if (checkSymbolTable(lexeme))
    		gen_instr("PUSHM",  get_address(lexeme));
    		duplicatePush = 1;
    	}
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
    	output.add("<Primary> ::=     <Identifier> <Identifier Prime>  |  <Integer>  |   ( <Expression> )   |   true   |  false");        
        lex();
        
        //catching if statements for rule above
        if(token.matches("^Identifier.*")) {
        	
            output.add("");
            output.add("Token: " + token + " Lexeme: " + lexeme);
            saveWhile = lexeme;	
            if(duplicatePush < 1) {
            	if (checkSymbolTable(lexeme))
            	gen_instr("PUSHM",  get_address(lexeme));             	
            	duplicatePush++;
            }
            Identifier_Prime();
            
            if(isEmpty == true)
                return;
        }
        else if(token.matches("^Integer.*")) {
        	//If the identifier associated with this token is a boolean, the only values it can accept are true and false        	
        	if (checkSymbolTable(currentIdentifier)) {
	        	if(symbolTableTypes.get(symbolTable.get(currentIdentifier)).equals("Boolean")) {
	            	if (!lexeme.equals("true") || !lexeme.equals("false"))
	            		error("boolean value");
	            }	        	
        	}
            output.add("");
            output.add("Token: " + token + " Lexeme: " + lexeme);
            gen_instr("PUSHI", lexeme);
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
        else if(lexeme.equals("true")){
        	//If the identifier associated with this token is an integer, then its an error
        	if (checkSymbolTable(currentIdentifier)) {
        		if(symbolTableTypes.get(symbolTable.get(currentIdentifier)).equals("Integer")) {        		
	            	if (!lexeme.matches("^\\d+$"))
	            		error("integer value");  
        		}
	        }
            output.add("");
             output.add("Token: " + token + " Lexeme: " + lexeme);
             gen_instr("PUSHI", "1");
            return;
        }
        else if(lexeme.equals("false")) {
        	//If the identifier associated with this token is an integer, then its an error
        	if (checkSymbolTable(currentIdentifier)) {
        		if(symbolTableTypes.get(symbolTable.get(currentIdentifier)).equals("Integer")) {
        			if (!lexeme.matches("^\\d+$"))
        				error("integer value");
            	}    
        	}
            output.add("");
			output.add("Token: " + token + " Lexeme: " + lexeme);
			gen_instr("PUSHI", "0");
            return;
        }
        else {
            isEmpty = true;
            x--;
            return;
        }       
    }
    
    //37
    private void Identifier_Prime() {
        
        lex();
        
        //if no parenthesis, check if there is comma, else then there are more IDS
        if(!lexeme.equals( "(")) {
        	if(lexeme.equals(",")) {
        		output.add("");
            	output.add("Token: " + token + " Lexeme: " + lexeme);            	
        		IDs();
        		 if(isEmpty == true) {
                     x--;
                     error();
                 }
        		 if(lexeme.equals(")")) {
                 	return;
        		 }        		 
        		Identifier_Prime();
        	}
            isEmpty = true;
            x--;
            return;
        }
        else {
        	
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
    }
    
    //38
    public void Empty() {
    	output.add("<Empty>   ::= E");
    	isEmpty = false;
        return;
    }
    
    
    //Gets the memory address for an identifier
    public String get_address(String id) {    	
    	int address = symbolTable.get(id);
    	String temp = "";
    	temp += address;
    	return temp;
    }
    
    //Generates entry for instruction table
    public void gen_instr(String op, String oprnd) {
    	if (oprnd.equals("nil")) oprnd = "";
    	String temp = "";
    	temp += instr_address;
    	String[] temp2 = {temp, op, oprnd};
    	instr_table.add(instr_address, temp2);
    	
    	instr_address++;
    }
    
    //Check if an identifier is in the symbol table
    public boolean checkSymbolTable(String id) {    	    	
    	if (symbolTable.containsKey(id))
    		return true;
    	else
    		return false;
    }
    
    //Insert into symbol table
    public void insertSymbolTable(String id, String type) {
    	symbolTable.put(id, memoryAddress);
		symbolTableTypes.put(memoryAddress, type);
		memoryAddress++;
    }
    
    //Output the symbol table in aligned columns
    public void output_symbolTable() throws Exception {
    	BufferedWriter wr = new BufferedWriter(new FileWriter("Symbol Table.txt"));    	
    	Set<String> keys = symbolTable.keySet();
    	String formatStr = "%-20s %-15s %-15s%n";    	
    	    wr.write("Identifier       MemoryLocation       Type");
    	    wr.newLine();
    	for (String key: keys) {    		
    		wr.write(String.format(formatStr ,key, symbolTable.get(key), symbolTableTypes.get(symbolTable.get(key)) ));
    	}
    	wr.close();
    }
    
    
    //Output the instr_table in aligned columns
    public void output_instr_table() throws Exception {
    	BufferedWriter wr = new BufferedWriter(new FileWriter("Instr Table.txt"));    	
    	String formatStr = "%-20s %-15s %-15s%n";     	
    	for(String[] table_entry : instr_table) {
    		wr.write(String.format(formatStr, table_entry[0], table_entry[1], table_entry[2]));
    	}
    	wr.close();
    }
    
    
    public void back_patch(int jump_addr) {    	
	    int addr = pop_jumpstack();	    
	    
	    //Convert the addresses to Strings to be added to the table	     	 	
    	String temp_jump_addr = "";
    	temp_jump_addr += jump_addr;
    	
    	//Set the new address in the table
	    String[] temp_table_entry = instr_table.get(addr);
	    temp_table_entry[2] = temp_jump_addr;
	    instr_table.set(addr, temp_table_entry);	    
    }
    
    //Add a value to the jumpstack
	public void push_jumpstack(int addr) {
		jumpstack.add(addr);
	}
	
	//Pop the most recent value on the jumpstack
	public int pop_jumpstack() {
    	int lastIndex = jumpstack.size() - 1;
    	int addr = jumpstack.get(lastIndex);
    	jumpstack.remove(lastIndex);
		return addr; 
	}
}
