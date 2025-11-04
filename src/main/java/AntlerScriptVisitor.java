import java.io.*;
import java.lang.reflect.Method;
import java.util.*;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import org.objectweb.asm.*;
import org.objectweb.asm.util.*;

public class AntlerScriptVisitor extends AntlerScriptParserBaseVisitor<Void> {
    static private AntlerScriptClassLoader classLoader = new AntlerScriptClassLoader();

    final private ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES
						   | ClassWriter.COMPUTE_MAXS);
    private MethodVisitor mv;
    private static final String MAIN_CLASS_NAME = "com/fawnscript/Main";
    private static final String MAIN_CLASS_JAVA_NAME = "com.fawnscript.Main";
    private static final String MAIN_FUNC_NAME = "__MAIN__";

    final private HashMap<String, Integer> symbolTable = new HashMap<>();
    // function name : method descriptor
    final private HashMap<String, String> functionTable = new HashMap<>();
    // Indicate if a previous expression left a value on the stack
    boolean dirtyStack = false;

    static private class AntlerScriptClassLoader extends ClassLoader {
	public Class<?> loadClassFromBytes(String name, byte[] bytes) {
	    return defineClass(name, bytes, 0, bytes.length);
	}
    }

    public Void visitProgram(AntlerScriptParser.ProgramContext ctx) {
	cw.visit(Opcodes.V21,
		 Opcodes.ACC_PUBLIC,
		 MAIN_CLASS_NAME,
		 null,
		 "java/lang/Object",
		 null);

	visit(ctx.def());

	mv = cw.visitMethod(
			    Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC,
			    MAIN_FUNC_NAME,
			    "()V",
			    null,
			    null);
	mv.visitCode();
	visit(ctx.stat());
	mv.visitInsn(Opcodes.RETURN);
	mv.visitMaxs(0, 0);
	mv.visitEnd();

	cw.visitEnd();
	byte[] bytecode = cw.toByteArray();
	
	Class<?> program = classLoader.loadClassFromBytes(MAIN_CLASS_JAVA_NAME, bytecode);
	Method mainMethod;
	try {
	    mainMethod = program.getMethod(MAIN_FUNC_NAME);
	    mainMethod.invoke(null);
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}

	// Print
	System.out.printf("%nResulting bytecode:%n");
	ClassReader cr = new ClassReader(bytecode);
	TraceClassVisitor tcv = new TraceClassVisitor(new PrintWriter(System.out));
	cr.accept(tcv, 0);

	return null;
    }

    public Void visitStat(AntlerScriptParser.StatContext ctx) {
	visit(ctx.expr());

	// Expression statement
	if (ctx.ID() == null) {
	    if (dirtyStack) {
		mv.visitInsn(Opcodes.POP);
		dirtyStack = false;
	    }
	    return null;
	}

	// Variable assignment

	final String varName = ctx.ID().getText();
	final int line = ctx.ID().getSymbol().getLine();
	final int column = ctx.ID().getSymbol().getCharPositionInLine();

	// Variable already exists
	if (symbolTable.containsKey(varName)) {
	    final String error = "ERROR [%d:%d]: variable '%s' is already defined"
		.formatted(line, column, varName);
	    throw new IllegalArgumentException(error);
	}
	// Empty asssignment
	if (!dirtyStack) {
	    final String error = "ERROR [%d:%d]: assigning a non-value to '%s'"
		.formatted(line, column, varName);
	    throw new IllegalArgumentException(error);
	}

	// Populate slot with expression value
	int slot = symbolTable.size();
	symbolTable.put(varName, Integer.valueOf(slot));
	mv.visitVarInsn(Opcodes.ISTORE, slot);

	return null;
    }

    public Void visitDef(AntlerScriptParser.DefContext ctx) {
	final String descriptor = "("
	    + "I".repeat(ctx.params().ID().size())
	    + ")V";
	final String funcName = ctx.ID().getText();
	final int line = ctx.ID().getSymbol().getLine();
	final int column = ctx.ID().getSymbol().getCharPositionInLine();

	// Reserved functions
	if (funcName.equals("__MAIN__")) {
	    final String error = "ERROR [%d:%d]: '__MAIN__' is a reserved function"
		.formatted(line, column);
	    throw new IllegalArgumentException(error);
	}
	if (funcName.equals("print")) {
	    final String error = "ERROR [%d:%d]: 'print' is a reserved function"
		.formatted(line, column);
	    throw new IllegalArgumentException(error);
	}

	// Already defined function
	if (functionTable.containsKey(funcName)) {
	    final String error = "ERROR [%d:%d]: function '%s' is already defined"
		.formatted(line, column, funcName);
	    throw new IllegalArgumentException(error);
	}

	// Create function
	mv = cw.visitMethod(
			    Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC,
			    funcName,
			    descriptor,
			    null,
			    null);

	// Function parameters
	if (ctx.params() != null) {
	    visit(ctx.params());
	}

	// Function body
	for (AntlerScriptParser.StatContext s : ctx.stat()) {
	    visit(s);
	}

	// Finalizing and cleaning state
	mv.visitInsn(Opcodes.RETURN);
	mv.visitMaxs(0, 0);
	mv.visitEnd();
	symbolTable.clear();
	functionTable.put(funcName, descriptor);

	return null;
    }

    public Void visitExpr(AntlerScriptParser.ExprContext ctx) {
	// Variable expression
	if (ctx.ID() != null) {
	    final String varName = ctx.ID().getText();
	    if (symbolTable.containsKey(varName)) {
		mv.visitVarInsn(Opcodes.ILOAD,
				symbolTable.get(varName));
		dirtyStack = true;
		return null;
	    }
			
	    final int line = ctx.ID().getSymbol().getLine();
	    final int column = ctx.ID().getSymbol().getCharPositionInLine();
	    final String error = "ERROR [%d:%d]: variable '%s' not found"
		.formatted(line, column, varName);
	    throw new IllegalArgumentException(error);			
	}

	// Integer literal
	if (ctx.INT() != null) {
	    // Okay Claude, this is so weird. 
	    final Integer integer = Integer.parseInt(ctx.INT().getText());
	    mv.visitLdcInsn(integer);
	    dirtyStack = true;
	    return null;
	}

	// Function call
	if (ctx.func() != null) {
	    visit(ctx.func());
	    dirtyStack = false;
	    return null;
	}

	// Binary addition
	visit(ctx.expr(0));
	if (!dirtyStack) {
	    final int line = ctx.expr(0).getStart().getLine();
	    final int column = ctx.expr(0).getStart().getCharPositionInLine();
	    final String error = "ERROR [%d:%d]: expression does not produce a value"
		.formatted(line, column);
	    throw new IllegalArgumentException(error);
	}
	
	visit(ctx.expr(1));
	if (!dirtyStack) {
	    final int line = ctx.expr(1).getStart().getLine();
	    final int column = ctx.expr(1).getStart().getCharPositionInLine();
	    final String error = "ERROR [%d:%d]: expression does not produce a value"
		.formatted(line, column);
	    throw new IllegalArgumentException(error);	    
	}

	mv.visitInsn(Opcodes.IADD);

	return null;
    }

    public Void visitFunc(AntlerScriptParser.FuncContext ctx) {
	final String funcName = ctx.ID().getText();
	final int line = ctx.LPAREN().getSymbol().getLine();
	final int column = ctx.LPAREN().getSymbol().getCharPositionInLine();

	// Undefined function
	if (!funcName.equals("print") && !functionTable.containsKey(funcName)) {
	    final String error = "ERROR [%d:%d]: function '%s' does not exist"
		.formatted(line, column, funcName);
	    throw new IllegalArgumentException(error);
	}

	// Print function call
	if (funcName.equals("print")) {
	    // Wrong arg count
	    final int argCount = ctx.args().expr().size();
	    if (argCount != 1) {
		final String error = "ERROR [%d:%d]: function 'print' called with %d argument(s), expected 1"
		    .formatted(line, column, argCount);
		throw new IllegalArgumentException(error);
	    }
	    mv.visitFieldInsn(Opcodes.GETSTATIC,
			      "java/lang/System",
			      "out",
			      "Ljava/io/PrintStream;");
	    visit(ctx.args().expr(0));
	    if (!dirtyStack) {
		final String error = "ERROR [%d:%d]: function 'print' called with void argument"
		    .formatted(line, column);
		throw new IllegalArgumentException(error);		
	    }
	    mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, 
			       "java/io/PrintStream", "println", "(I)V", false);
	    return null;
	}

	final String description = functionTable.get(funcName);
	final int start = description.indexOf('(');
	final int end = description.indexOf(')', start);
	final int expectedArgCount = description.substring(start + 1, end)
	    .length();
	final int actualArgCount = ctx.args().expr().size();

	// Function called with the wrong number of arguments
	if (actualArgCount != expectedArgCount) {
	    final String error = "ERROR [%d:%d]: function '%s' called with %d arguments, expected %d"
		.formatted(line, column, funcName, actualArgCount, expectedArgCount);
	    throw new IllegalArgumentException(error);
	}

	visit(ctx.args());
		
	mv.visitMethodInsn(
			   Opcodes.INVOKESTATIC,
			   MAIN_CLASS_NAME,
			   funcName,
			   description,
			   false);

	return null;
    }

    public Void visitParams(AntlerScriptParser.ParamsContext ctx) {
	for (int i = 0; i < ctx.ID().size(); i++) {
	    final int slot = symbolTable.size();
	    symbolTable.put(ctx.ID(i).getText(), slot);
	}
	return null;
    }

    public Void visitArgs(AntlerScriptParser.ArgsContext ctx) {
	for (int i = 0; i < ctx.expr().size(); i++) {
	    final AntlerScriptParser.ExprContext arg = ctx.expr(i);

	    final int line = arg.getStart().getLine();
	    final int column = arg.getStart().getCharPositionInLine();

	    visit(arg);
	    if (!dirtyStack) {
		final String error = "ERROR [%d:%d]: argument %d does not produce a value"
		    .formatted(line, column, i);
		throw new IllegalArgumentException(error);
	    }
	}
	return null;
    }
}
