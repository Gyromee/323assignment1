
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.*;

	public class Driver extends JFrame{
		 public Driver() {
		    	super("Lexical Analyzer Tester");
		    	setSize(600, 100);
		    	setResizable(true);
		    	setDefaultCloseOperation(EXIT_ON_CLOSE);
		    	
		    	JPanel jp = new JPanel();
		    	JLabel jl = new JLabel();
		    	JTextField jt = new JTextField(30);
		    	JButton jb = new JButton("Enter");
		    	
		    	jp.add(jt);
		    	
		    	jt.addActionListener(new ActionListener(){
		    		public void actionPerformed(ActionEvent e) {
		    			String input = jt.getText();
		    			jl.setText(input);
		    		}
		    	});
		    	
		    	jp.add(jb);
		    	
		    	jb.addActionListener(new ActionListener(){
		    		public void actionPerformed(ActionEvent e) {
		    			String input = jt.getText();
		    			jl.setText(input);
		    			LexicalAnalyzer lexical = new LexicalAnalyzer(input);
		    		    try {
							lexical.start();
							SyntaxAnalyzer syntax = new SyntaxAnalyzer("lexicalResult.txt", lexical);
						 	syntax.start();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
		    		}
		    	});
		    	
		    	add(jp);
		 
		    	
		    }
	    public static void main(String[] args) throws FileNotFoundException, IOException {
	        new Driver().setVisible(true);

	    	}
	}

