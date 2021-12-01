package compiler;
/**
 * This class encapsulates a basic grammar test.
 */

import java.io.IOException;
//ANTLR packages
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.tree.*;
import org.antlr.v4.gui.Trees;
import java.util.Scanner;

import lexparse.*;

/**
 * kcc.java
 * This class contains the main method.
 * @author Christina Porter
 * @author Kaitlyn Reed
 * @version 1.0
 * Programming Project 4
 * CS322 - Compiler Construction
 * Fall 2021
 */

public class kcc{


    public static void main(String[] args){
        CharStream input;
        KnightCodeLexer lexer;
        CommonTokenStream tokens;
        KnightCodeParser parser;

        try{
            input = CharStreams.fromFileName(args[0]);  //get the input
            lexer = new KnightCodeLexer(input); //create the lexer
            tokens = new CommonTokenStream(lexer); //create the token stream
            parser = new KnightCodeParser(tokens); //create the parser
       
            ParseTree tree = parser.file();  //set the start location of the parser

            Scanner scan = new Scanner(System.in);
	        System.out.print("Enter name for output class file: ");
	        String classFile = scan.next();
             
            
            Trees.inspect(tree, parser);
            
            //System.out.println(tree.toStringTree(parser));

            myListener listener = new myListener(classFile, false);
	        ParseTreeWalker walker = new ParseTreeWalker();
	        walker.walk(listener, tree);
        
            scan.close();
        } // end try
        catch(IOException e){
            System.out.println(e.getMessage());
        } // end catch


    } // end main

}//end class