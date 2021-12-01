package compiler;

import org.antlr.v4.runtime.ParserRuleContext; // need to debug every rule
import lexparse.*; //classes for lexer parser
import org.objectweb.asm.*;  //classes for generating bytecode
import org.objectweb.asm.Opcodes; //Explicit import for ASM bytecode constants
import java.util.HashMap;

/**
 * myListener.java
 * This class contains all methods used to run bytecode operations and manipulate the symbol table.
 * @author Christina Porter
 * @author Kaitlyn Reed
 * @version 7.3
 * Programming Project 4
 * CS322 - Compiler Construction
 * Fall 2021
 */

public class myListener extends KnightCodeBaseListener{

	private ClassWriter cw;  //class level ClassWriter 
	private MethodVisitor mainVisitor; //class level MethodVisitor
	private String programName; //name of the class and the output file (used by ASM)
	private boolean debug; //flag to indicate debug status
	private boolean variable;
	private String varType;
	private String instruction;
	private String numType;
	private static int storageLocation = 1;
	private int scannerLocation;
	//private String nameVariable;
	//private int variableLocation;

    private HashMap<String, variable> symbolTable = new HashMap<String, variable>();

	public myListener(String programName, boolean debug){
	       
		this.programName = programName;
		this.debug = debug;

	}//end constructor

	/**
	 * This method creates the class writer.
	 */
	public void setupClass(){
		
		//Set up the classwriter
		cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        cw.visit(Opcodes.V11, Opcodes.ACC_PUBLIC,this.programName, null, "java/lang/Object",null);
	
		//Use local MethodVisitor to create the constructor for the object
		MethodVisitor mv=cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
        mv.visitCode();
        mv.visitVarInsn(Opcodes.ALOAD, 0); //load the first local variable: this
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V",false);
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(1,1);
        mv.visitEnd();
       	
		//Use global MethodVisitor to write bytecode according to entries in the parsetree	
	 	mainVisitor = cw.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC,  "main", "([Ljava/lang/String;)V", null, null);
        mainVisitor.visitCode();

	}//end setupClass

	/**
	 * This method closes the class writer.
	 */
	public void closeClass(){
		//Use global MethodVisitor to finish writing the bytecode and write the binary file.
		mainVisitor.visitInsn(Opcodes.RETURN);
		mainVisitor.visitMaxs(3, 3);
		mainVisitor.visitEnd();

		cw.visitEnd();

        byte[] b = cw.toByteArray();

        Utilities.writeFile(b,this.programName+".class");
        
        System.out.println("Done!");

	}//end closeClass


	/**
	 * Overrides the enterFile method. Sets up the class.
	 */
    @Override
	public void enterFile(KnightCodeParser.FileContext ctx){

		System.out.println("Enter program rule for first time");
		setupClass();
	} // end enterFile

	/**
	 * Overrides the exitFile method. Closes the class.
	 */
    @Override
	public void exitFile(KnightCodeParser.FileContext ctx){

		System.out.println("Leaving program rule. . .");
		closeClass();

	} // end exitFile

	/**
	 * Overrides the enterDeclare method.
	 */
    @Override 
    public void enterDeclare(KnightCodeParser.DeclareContext ctx) { 
        System.out.println("Enter declare...");
    } // end enterDeclare

	/**
	 * Overrides the exitDeclare method.
	 */
	@Override 
    public void exitDeclare(KnightCodeParser.DeclareContext ctx) { 
        System.out.println("Exit declare...");
    } // end exitDeclare

	/**
	 * Overrides the enterVariable method.
	 */
    @Override 
    public void enterVariable(KnightCodeParser.VariableContext ctx) { 
        System.out.println("Enter variable...");
        variable var = new variable(); //sets up a new variable object

		//System.out.println(ctx.getChild(0).getText());
		//System.out.println(ctx.getChild(1).getText());

		String identifier = ctx.getChild(1).getText(); //sets the identifier to the name of the variable name node of the parse tree
		var.setVariableType(ctx.getChild(0).getText()); //sets the variable type of the variable object to the variable type node of the parse tree
		var.setMemoryLocation(storageLocation);
		storageLocation = storageLocation + 1;

		symbolTable.put(identifier, var); //places the name and variable object into the symbol table

    } // end enterVariable

	/**
	 * Overrides the exitVariable method
	 */
	@Override 
    public void exitVariable(KnightCodeParser.VariableContext ctx) { 
		System.out.println("Exit variable...");

		System.out.println(symbolTable.toString());

    } // end exitVariable
	
	/**
	 * Overrides the enterPrint method.
	 */
    @Override
    public void enterPrint(KnightCodeParser.PrintContext ctx){

        System.out.println("Entering print statement...");
        String output = ctx.getChild(1).getText(); //sets the output to the input passed as shown in the parse tree
		System.out.println(output);
		String variableName = "";
		

		//Searches for the output in the symbol table. If the output is found in the symbol table, it is marked as a variable and retrieves the storage location.
		if(symbolTable.containsKey(output)){
			variableName = output;
			variable = true;
			variable var = symbolTable.get(variableName);
			storageLocation = var.getMemoryLocation();
			//System.out.println(var.getVariableType());
			//System.out.println(storageLocation);
			varType = var.getVariableType();
			//System.out.println("Found variable in symbol table.");
			//System.out.println(storageLocation);
		}//end if
		else{
			System.out.println(symbolTable.toString());
			System.out.println("Can't find variable in symbol table");
		} // end else



		//System.out.println(variable);
		//System.out.println(numType);
		//System.out.println(varType);

		//checks if variable is set to true. If it is, it checks what type of variable. If it is not, it prints out the context as-is.
		if(variable){
			//If the variable type in the symbol table is an integer, print the integer. If the variable type in the symbo ltable is a string, print the string.
			if(varType.equals("INTEGER")){
				System.out.println("true");
				mainVisitor.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"); //Sets up the print stream
            	mainVisitor.visitVarInsn(Opcodes.ILOAD, storageLocation); //Loads the int stored in the given storage location
            	mainVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V", false); //invokes the printstream to print the int loaded onto the stack to the screen
			
			}else if(varType.equals("STRING")){

				mainVisitor.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"); //Sets up the print stream
            	mainVisitor.visitVarInsn(Opcodes.ALOAD, storageLocation); //Loads the string stored in the given storage location
            	mainVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false); //invokes the printstream to print the string loaded onto the stack to the screen
		
			}//end if

		}else{

			numType = "";
			//output = output.substring(5,output.length());
			mainVisitor.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"); //Sets up the print stream
			mainVisitor.visitLdcInsn(output); //Sets up the output
			mainVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream",  "println", "(Ljava/lang/String;)V", false); //prints the output to the console
		
		}//end else

		variable = false; //Sets variable to false <If this is not set the next line won't print properly/at all>

    }//end enterPrint

	/**
	 * Overrides the exitPrint method
	 */
    @Override 
    public void exitPrint(KnightCodeParser.PrintContext ctx) { 
        System.out.println("Exiting print statement...");
    } // end exitPrint

	/**
	 * Overrides the enterRead method
	 */
	@Override 
	public void enterRead(KnightCodeParser.ReadContext ctx) { 
		System.out.println("Enter read...");


		//System.out.println(ctx.getChild(0).getText());
		//System.out.println(ctx.getChild(1).getText());

		mainVisitor.visitTypeInsn(Opcodes.NEW, "java/util/Scanner"); //Instantiates the scanner object
        mainVisitor.visitInsn(Opcodes.DUP); //Duplicates the previous object <Code will not work without this>
        mainVisitor.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "in", "Ljava/io/InputStream;"); //Sets up a stream to read the next line
        mainVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/util/Scanner", "<init>", "(Ljava/io/InputStream;)V"); //Places the objet that reads user input on the stack
        mainVisitor.visitVarInsn(Opcodes.ASTORE, storageLocation); //Stores the object that reads user input in slot 1 and removes from the stack
		scannerLocation = storageLocation; //sets the scanner location to the storage location so we can use the scanner later.
		storageLocation = storageLocation + 1; //increases the storage location by 1

		varType = ""; //Sets the varType to none
		instruction = ""; //sets the instructoin type to none
		numType = ""; //sets the numType to none
		String varName = ctx.getChild(1).getText();


		//Compares each variable in the symbol table against the variable name in the read statement.
		for(String key : symbolTable.keySet()){
			variable var = symbolTable.get(key);//sets the variable to the value associated with the name of the variable
			//if the name in the symbol table matches the name of the child, set the variable type to the variable type associated with the name in the symbol table
			if(key.equals(varName)){
				varType = var.getVariableType();
			}//end if
		}//end for
		
		//System.out.println(varType);

		//If the variable type is a string, we set up the scanner to read a string.
		//If the variable type is an integer, we set up the scanner to read an integer.
		if(varType.equals("STRING")){
			instruction = "next"; //sets the instruction to next
			numType = "()Ljava/lang/String;"; //sets the instruction type to a String
			mainVisitor.visitVarInsn(Opcodes.ALOAD, scannerLocation); //Loads the scanner
        	mainVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/Scanner", instruction, numType, false); //Uses the scanner on the stack to get the next input and stores that input on the stack
        	mainVisitor.visitVarInsn(Opcodes.ASTORE, storageLocation); //Stores the input in slot 2 and removes from the stack
			variable var = symbolTable.get(varName); //Obtains the variable object stored in the symbol table
			var.setMemoryLocation(storageLocation); //sets the storage location of the variable object in the symbol table
			storageLocation = storageLocation + 1; //increases storage location by 1
			
		}else if(varType.equals("INTEGER")){
			instruction = "nextInt"; //sets the instruction to nextInt
			numType = "()I"; //sets the instruction type to integer
			mainVisitor.visitVarInsn(Opcodes.ALOAD, scannerLocation); //Loads the scanner
        	mainVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/Scanner", instruction, numType, false); //Uses the scanner on the stack to get the next input and stores that input on the stack
        	mainVisitor.visitVarInsn(Opcodes.ISTORE, storageLocation); //Stores the input in slot 2 and removes from the stack
			variable var = symbolTable.get(varName);//Obtains the variable object stored in the symbol table
			var.setMemoryLocation(storageLocation); //sets the storage location of the variable object in the symbol table
			storageLocation = storageLocation + 1; //increases storage location by 1
		} // end if
	} // end enterRead
	
	/**
	 * Overrides the exitRead
	 */
	@Override 
	public void exitRead(KnightCodeParser.ReadContext ctx) { 
		System.out.println("Exit read...");
	} // end exitRead
 
	/**
	 * Overrides the enterSetVar method
	 */
    @Override
    public void enterSetvar(KnightCodeParser.SetvarContext ctx) {
        System.out.println("Entering set var...");

		//System.out.println(symbolTable.toString());

		//System.out.println(ctx.getChild(0).getText());
		//System.out.println(ctx.getChild(1).getText());
		//System.out.println(ctx.getChild(2).getText());
		//System.out.println(ctx.getChild(3).getText());

		String variableName = ctx.getChild(1).getText(); //sets the output to the input passed as shown in the parse tree
		variable var = symbolTable.get(variableName);
		storageLocation = var.getMemoryLocation();

		if(var.getVariableType().equals("INTEGER")){
			if(ctx.getChild(3).getText().contains("+")){
				System.out.println("Addition");

			}else if(ctx.getChild(3).getText().contains("-")){
				System.out.println("Subtraction");

			}else if(ctx.getChild(3).getText().contains("*")){
				System.out.println("Multiplication");
				
			}else if(ctx.getChild(3).getText().contains("/")){
				System.out.println("Division");
				
			}else{
				var.setVariableValue(Integer.parseInt(ctx.getChild(3).getText()));
				mainVisitor.visitLdcInsn(Integer.parseInt(ctx.getChild(3).getText())); // initializes the first integer
				mainVisitor.visitVarInsn(Opcodes.ISTORE,storageLocation); // stores the first integer on the stack
				var.setMemoryLocation(storageLocation);
				//System.out.println("Storage location in setvar..." + var.getMemoryLocation());
			} // end if

		} // end if

		if(var.getVariableType().equals("STRING")) {
			var.setVariableValue(ctx.getChild(3).getText());
			mainVisitor.visitLdcInsn(ctx.getChild(3).getText());
			mainVisitor.visitVarInsn(Opcodes.ASTORE, storageLocation);
			var.setMemoryLocation(storageLocation);
			System.out.println("Storage location in setvar..." + var.getMemoryLocation());
		} // end if
		
    } // end enterSetVar

	/**
	 * Overrides the exitSetVar method
	 */
	@Override
    public void exitSetvar(KnightCodeParser.SetvarContext ctx) { 

        System.out.println("Exiting set var...");
		//System.out.println("Storage Location in exitSetVar..." + storageLocation);
    } // end exitSetVar

	/**
	 * Overrides the enterAddition method
	 */
	@Override 
	public void enterAddition(KnightCodeParser.AdditionContext ctx) { 

		System.out.println("Entering Addition...");
		//System.out.println(ctx.getChild(0).getText());
		//System.out.println(ctx.getChild(1).toString());
		//System.out.println(ctx.getChild(2).getText());
		//System.out.println("Storage Location in addition..." + storageLocation);

		String variable1 = ctx.getChild(0).getText();
		String variable2 = ctx.getChild(2).getText();

		int variableLocation1;
		int variableLocation2;

		if(symbolTable.containsKey(variable1) && symbolTable.containsKey(variable2)){
			variable var1 = symbolTable.get(variable1);
			variableLocation1 = var1.getMemoryLocation();
			//System.out.println(variableLocation1);
			//System.out.println(var1.getVariableValue());
			variable var2 = symbolTable.get(variable2);
			variableLocation2 = var2.getMemoryLocation();
			//System.out.println(variableLocation2);
			//4System.out.println(var2.getVariableValue());
			mainVisitor.visitVarInsn(Opcodes.ILOAD, variableLocation1);
			mainVisitor.visitVarInsn(Opcodes.ILOAD, variableLocation2);
			mainVisitor.visitInsn(Opcodes.IADD);
			mainVisitor.visitVarInsn(Opcodes.ISTORE, storageLocation);

		} // end if
		
	} // end enterAddition
		
	/**
	 * Overrides the exitAddition method
	 */
	@Override 
	public void exitAddition(KnightCodeParser.AdditionContext ctx) { 
		System.out.println("Exiting Addition...");
	} // end exitAddition

	/**
	 * Overrides the enterSubtraction method
	 */
	@Override 
	public void enterSubtraction(KnightCodeParser.SubtractionContext ctx) { 
		System.out.println("Entering subtraction...");
		String variable1 = ctx.getChild(0).getText();
		String variable2 = ctx.getChild(2).getText();

		int variableLocation1;
		int variableLocation2;

		if(symbolTable.containsKey(variable1) && symbolTable.containsKey(variable2)){
			variable var1 = symbolTable.get(variable1);
			variableLocation1 = var1.getMemoryLocation();
			variable var2 = symbolTable.get(variable2);
			variableLocation2 = var2.getMemoryLocation();
			mainVisitor.visitVarInsn(Opcodes.ILOAD, variableLocation1);
			mainVisitor.visitVarInsn(Opcodes.ILOAD, variableLocation2);
			mainVisitor.visitInsn(Opcodes.ISUB);
			mainVisitor.visitVarInsn(Opcodes.ISTORE, storageLocation);

		} // end if
	} // end enterSubtraction

	/**
	 * Overrides the exitSubtraction method
	 */
	@Override 
	public void exitSubtraction(KnightCodeParser.SubtractionContext ctx) { 
		System.out.println("Exiting subtraction...");
	} // end exitSubtraction

	/**
	 * Overrides the enterMultiplication method
	 */
	@Override 
	public void enterMultiplication(KnightCodeParser.MultiplicationContext ctx) { 
		System.out.println("Entering multiplication...");
		String variable1 = ctx.getChild(0).getText();
		String variable2 = ctx.getChild(2).getText();

		int variableLocation1;
		int variableLocation2;

		if(symbolTable.containsKey(variable1) && symbolTable.containsKey(variable2)){
			variable var1 = symbolTable.get(variable1);
			variableLocation1 = var1.getMemoryLocation();
			variable var2 = symbolTable.get(variable2);
			variableLocation2 = var2.getMemoryLocation();
			mainVisitor.visitVarInsn(Opcodes.ILOAD, variableLocation1);
			mainVisitor.visitVarInsn(Opcodes.ILOAD, variableLocation2);
			mainVisitor.visitInsn(Opcodes.IMUL);
			mainVisitor.visitVarInsn(Opcodes.ISTORE, storageLocation);

		} // end if
	} // end enterMultiplication

	/**
	 * Overrides the exitMultiplication method
	 */
	@Override 
	public void exitMultiplication(KnightCodeParser.MultiplicationContext ctx) { 
		System.out.println("Exiting multiplication...");
	} // end exitMultiplication

	/**
	 * Overrides the enterDivision method
	 */
	@Override 
	public void enterDivision(KnightCodeParser.DivisionContext ctx) { 
		System.out.println("Entering division...");
		String variable1 = ctx.getChild(0).getText();
		String variable2 = ctx.getChild(2).getText();

		int variableLocation1;
		int variableLocation2;

		if(symbolTable.containsKey(variable1) && symbolTable.containsKey(variable2)){
			variable var1 = symbolTable.get(variable1);
			variableLocation1 = var1.getMemoryLocation();
			variable var2 = symbolTable.get(variable2);
			variableLocation2 = var2.getMemoryLocation();
			mainVisitor.visitVarInsn(Opcodes.ILOAD, variableLocation1);
			mainVisitor.visitVarInsn(Opcodes.ILOAD, variableLocation2);
			mainVisitor.visitInsn(Opcodes.IDIV);
			mainVisitor.visitVarInsn(Opcodes.ISTORE, storageLocation);

		} // end if
	} // end enterDivision

	/**
	 * Overrides exitDivision method
	 */
	@Override 
	public void exitDivision(KnightCodeParser.DivisionContext ctx) { 
		System.out.println("Exiting division...");
	} // end ExitDivision

	/**
	 * Overrides enterId method
	 */
	@Override 
	public void enterId(KnightCodeParser.IdContext ctx) { 
		System.out.println("Enter ID...");
	} // end enterId

	/**
	 * Overrides exitId method
	 */
	@Override 
	public void exitId(KnightCodeParser.IdContext ctx) { 
		System.out.println("Exit ID...");
	} // end exitId

	/**
	 * Overrides enterNumber method
	 */
	@Override 
	public void enterNumber(KnightCodeParser.NumberContext ctx) {
		System.out.println("Enter Number...");
	 } // end enterNumber

	 /**
	  * Overrides exitNumber method
	  */
	@Override public void exitNumber(KnightCodeParser.NumberContext ctx) { 
		System.out.println("Exit Number...");
	} // end exitNumber

	/**
	 * Prints context string. Used for debugging purposes
	 * @param ctx
	 */
	private void printContext(String ctx){
		System.out.println(ctx);
	} // end printContext

	/**
	 * Overrides the enterEveryRule method
	 */
	@Override 
	public void enterEveryRule(ParserRuleContext ctx){ 
		if(debug) printContext(ctx.getText());
	} // end enterEveryRule

	/**
	 * Overrides the enterStat method
	 */
	@Override 
	public void enterStat(KnightCodeParser.StatContext ctx) {
		System.out.println("Enter Stat...");
	 } // end enterStat
	
	 /**
	 * Overrides the exitStat method
	 */
	@Override 
	public void exitStat(KnightCodeParser.StatContext ctx) { 
		System.out.println("Exit Stat...");
	} // end exitStat

	/**
	 * Overrides the enterLoop method
	 */
	@Override 
	public void enterLoop(KnightCodeParser.LoopContext ctx) { 
		System.out.println("Enter Loop...");

		//String reps = ctx.getChild(1).getText();
	} // end enterLoop
	
	/**
	 * Overrides the exitLoop method
	 */
	@Override 
	public void exitLoop(KnightCodeParser.LoopContext ctx) {
		System.out.println("Exit Loop...");
	 } // end exitLoop

	 /**
	 * Overrides the enterDecision method
	 */
	 @Override public void enterDecision(KnightCodeParser.DecisionContext ctx) {
		 System.out.println("Enter Decision...");
	  } // end enterDecision
	 
	 /**
	 * Overrides the exitDecision method
	 */
	 @Override public void exitDecision(KnightCodeParser.DecisionContext ctx) {
		 System.out.println("Exit Decision...");
	  } // end exitDecision
	 
}//end class
