package io.github.heliguy4599.antlerscript;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.IOException;

public class Main {
	public static void main(String[] args) {
		if (args.length != 1) {
			System.err.println("Usage: java Main <source file>");
			System.exit(1);
		}

		String filePath = args[0];

		try {
			CharStream charStream = CharStreams.fromFileName(filePath);

			// Lexer
			AntlerScriptLexer lexer = new AntlerScriptLexer(charStream);
			CommonTokenStream tokens = new CommonTokenStream(lexer);

			// Parser
			AntlerScriptParser parser = new AntlerScriptParser(tokens);
			ParseTree tree = parser.statement();
			System.out.println(tree.toStringTree(parser));

			// AST
			CstToAstConverter conv = new CstToAstConverter();
			conv.visit(tree);
		} catch (IOException e) {
			System.err.println("Error reading file: " + e.getMessage());
		}
	}
}
