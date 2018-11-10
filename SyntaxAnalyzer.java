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
	private int lineNumber = 0;
	private int invalidLineNumber = 1;
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
	    		invalidLineNumber++;
	    	 }

	    	 //prints out the arraylist
	    	 for (int i=0; i<tokensAndLexeme.size(); i++) {
	    		 String temp[] = tokensAndLexeme.get(i);
	             System.out.println(temp[0] +" "+ temp[1] + " " + temp[2]);
	    	 }	    	 	    		    	
		}
	}

	private void lex() {
	    String temp[] = tokensAndLexeme.get(x);
	    token = temp[0];
	    lexeme = temp[1];
	    lineNumber = temp[2];
	    x++;
	}
	
//	public void Rat18F(String token, String lexeme, String lineNumber)
//	{
//		Opt_Function_Definitions();
//		if (!lexeme.equals("$$"){
//			System.out.println("Error on line _______ expecting a $$")
//			System.exit(0)
//		}
//		Opt_Declaration_List();
//
//		
//	}
//	
//
//	public void Opt_Function_Definitions()
//	{
//		Function_Definitions();
//		Empty();
//	}
//	
//	public void Function_Definitions()
//	{
//		Function();
//		Function_Definition_Prime();
//	
//	}
//	
//	public void Function_Definition_Prime()
//	{
//		if(isEmpty == false) {
//			Function();
//			Function_Definition_Prime();
//		}
//		else 
//			Empty();
//	}
//	
//	public void Function()
//	{
//		lex();  Call the next element and token of the array
//		if(!lexeme.equals("function"))
//		{
//			isEmpty = true;
//			return;
//		}
//		
//		lex()
//		if(!token.equals("identifier"))
//		{
//			system.out("syntax error");
//			System.exit(0);
//		}
//		
//		lex()
//		if(token != "(")
//		{
//			system.out("syntax error");
//			System.exit(0);
//		}
//		
//		Opt_Parameter_List();
//		
//		lex()
//		if(token != ")")
//		{
//			system.out("syntax error");
//			exit
//		}
//		
//		Opt_Declaration_List();
//		Body();
//		
//	}
//	
//	public void Opt_Parameter_List() {
//		Parameter_List();
//		//or Empty();
//	}
//	
//	public void Parameter_List() {
//		Parameter();
//		lex()
//		if(token != ",")
//		{
//			system.out("syntax error");
//			exit
//		}
//		Parameter_List_Prime();
//		
//	}
//
//	public void Parameter_List_Prime() {
//		Parameter();
//		//lex()
//		//if(token != ",")
//		//{
//		//	system.out("syntax error");
//		//	exit
//		//}
//		Parameter_List_Prime();
//		//or Empty();
//	}
//	
//	public void Parameter() {
//		IDs();
//		//lex()
//		//if(token != ":")
//		//{
//		//	system.out("syntax error");
//		//	exit
//		//}
//		Qualifier();
//
//	}
//	
//	public void Qualifier() {
//		if(!lexeme.equals("int") || !lexeme.equals("boolean") || !lexem.equals("real"))
//		{
//			system.out("syntax error");
//			exit
//		}
//	}
//	
//	public void Body() {
//		//if(token != "{")
//		//{
//		//	system.out("syntax error");
//		//	exit
//		//}
//		Statement_List();
//		//if(token != "}")
//		//{
//		//	system.out("syntax error");
//		//	exit
//		//}
//	}
//	
//	public void Opt_Declaration_List() {
//		Declaration_List();
//		//or Empty();
//	}
//	
//	public void Declaration_List() {
//		Declaration();
//		//if(token != "}")
//		//{
//		//	system.out("syntax error");
//		//	exit
//		//}
//		Declaration_List_Prime();
//	}
//	
//	public void Declaration_List_Prime() {
//		Declaration();
//		//if(token != "}")
//		//{
//		//	system.out("syntax error");
//		//	exit
//		//}
//		Declaration_List_Prime();
//		//or Empty();
//	}
//	
//	public void Declaration()
//	{
//		Qualifier();
//		IDs();
//	}
//	
//	public void IDs()
//	{
//		if(!token.equals("Identifier"))
//		{
//			System.out.println("Expecting an identifier on line ____");
//			System.exit(0);
//		}
//		IDs_Prime();
//	}
//	
//	public void IDs_Prime()
//	{
//		if(!lexeme.equals(",")){
//			
//			Empty();
//		}
//		if(!Token.eqauls("Identifier")) {
//			System.out.println("Expecting an identifier on line ____");
//			System.exit(0);
//		}
//		
//		IDs_Prime();
//		//or Empty();
//		
//	}
//	
//	public void Statement_List()
//	{
//		Statement();
//		Statement_List_Prime();
//		
//	}
//	
//	public void Statement_List_Prime()
//	{
//		Statement();
//		Statement_List_Prime();
//		//or Empty();
//	}
//
//	public void Statement()
//	{
//		// if (Compound()  |  Assign()  |   If()  |  Return()   | Print()   |   Scan()   |  While())
//		// else {
//		// error
//		//exit;
//		//}
//	}
//	
//	public void Compound()
//	{
//		//if( != "{")
//		//{
//		//	system.out("syntax error");
//		//	exit
//		//}
//		Statement_List();
//		//if(token != "}")
//		//{
//		//	system.out("syntax error");
//		//	exit
//		//}
//	}
//	
//	public void Assign()
//	{
//		//if(token != "Idntifier")
//		//{
//		//	system.out("syntax error");
//		//	exit
//		//}
//		//if(token != "=")
//		//{
//		//	system.out("syntax error");
//		//	exit
//		//}
//		Expression();
//		//if(token != ";")
//		//{
//		//	system.out("syntax error");
//		//	exit
//		//}
//	}
//	
//	public void If()
//	{
//		//if(token != "if")
//		//{
//		//	system.out("syntax error");
//		//	exit
//		//}
//		//if(token != "(")
//		//{
//		//	system.out("syntax error");
//		//	exit
//		//}
//		Condition();
//		//if(token != ")")
//		//{
//		//	system.out("syntax error");
//		//	exit
//		//}
//		Statement();
//		If_Prime();
//	}
//	
//	public void If_Prime()
//	{
//		//lex();
//		//if(token == "ifend")
//		//{
//		//	return;
//		//}
//		//if(token == "else") {
//			Statement();
//			//if(token == "ifend")
//			//{
//			//	return;
//			//}
//		//}
//		//else {
//		//	exit;
//		//}
//		
//	}
//	
//	public void Return()
//	{
//		//if(token != "return")
//		//{
//		//	system.out("syntax error");
//		//	exit;
//		//}
//		Return_Prime();
//	}
//	
//	public void Return_Prime()
//	{
//		//if(token == ";")
//		//{
//		// return;
//		//}
//		//OR
//		Expression();
//		//if(token == ";")
//		//{
//		// return;
//		//}
//		//else {
//		//exit
//		//}
//	}
//	
//	public void Condition()
//	{
//		Expression();
//		Relop();
//		Expression();
//	}
//	
//    private void Relop() {
//        if(token=="==") {
//            return "==;
//        }
//        else if(token=="^=") {
//            return "^=;
//        }
//        else if(token==">") {
//            return ">;
//        }
//        else if(token=="<") {
//            return "<;
//        }
//        else if(token=="=>") {
//            return "=>;
//        }
//        else if(token=="=<") {
//            return "=<;
//        }
//    }
//    
//    //31
//    private void Expression() {
//        Term();
//        Expression_Prime();
//    }
//    
//    //32
//    private void Expression_Prime() {
//        if(token=="+") {
//            Term();
//            Expression_Prime();
//        }
//        else if(token=="-") {
//            Term();
//            Expression_Prime();
//        }
//        else
//            Empty();        
//    }
//    
//    //33
//    private void Term() {    
//        Factor();
//        TermPrime();        
//    }
//    
//    //34
//    private void Term_Prime() {
//        if(token == "*") {
//            Factor();
//            TermPrime();
//        }
//        else if(token == "/") {
//            Factor();
//            TermPrime();
//        }
//        else 
//            Empty();    
//    }
//    
//    //35
//    private Factor() {
//        if(token == "-") {
//            Primary();
//        }
//        else
//            Primary();
//    }
//    //36
//    private Primary() {
//        if(token=="Identifier" || token=="")
//    }
//    
//    //37
//    private Identifier_Prime() {
//        
//        if(token != "(") {
//            Empty();
//        }
//        IDs();
//        if(token != ")") {
//            //error();
//        }
//    }
//    
//    //38
//    public void Empty() {
//        return;
//    }
	

	
}
