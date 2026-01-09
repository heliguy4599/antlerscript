package io.github.heliguy4599.antlerscript;

import java.lang.reflect.*;
import java.util.*;

import org.antlr.v4.runtime.*;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testing the ANTLR4-generated abstract syntax tree (AST)")
class AstTest {
	static AntlerScriptParser getNewParser(String input) {
		CharStream charStream = CharStreams.fromString(input);
		var lexer = new AntlerScriptLexer(charStream);
		var tokens = new CommonTokenStream(lexer);
		var parser = new AntlerScriptParser(tokens);
		parser.setErrorHandler(new BailErrorStrategy());
		return parser;
	}

	static void testInput(String input, String ruleName, Object expected) {
		AntlerScriptParser parser = getNewParser(input);
		assertDoesNotThrow(() -> {
			Method rule = parser.getClass().getMethod(ruleName);
			var context = (ParserRuleContext) rule.invoke(parser);
			assertNull(context.exception);
			var converter = new CstToAstConverter();
			var converted = converter.visit(context);
			assertEquals(expected, converted);
		});
		assertEquals(0, parser.getNumberOfSyntaxErrors());
	}

	static List<Token> genTokens(String... tokens) {
		var result = new ArrayList<Token>();

		for (String tokenText : tokens) {
			result.add(new DummyToken(tokenText));
		}

		return result;
	}

	@Nested
	@DisplayName("Program")
	class ProgamTests {

	}

	@Nested
	@DisplayName("Classes")
	class ClassTests {

	}

	@Nested
	@DisplayName("Enums")
	class EnumTests {

	}

	@Nested
	@DisplayName("Statements")
	class StatementTests {

	}

	@Nested
	@DisplayName("Expressions")
	class ExpressionTests {

	}

	@Nested
	@DisplayName("Types")
	class TypeTests {
		@Test
		void lmao() {
			testInput("Self", "type_atomic", new Ast.SelfClassType(genTokens("Self")));
		}

		@Test
		void lmao2() {
			testInput("1 + 1", "expression",
				  new Ast.BinaryExpression(genTokens("1", "+", "1"), Ast.BinaryExpression.Kind.ADD,
							   new Ast.IntExpression(genTokens("1"), (long) 1, (byte) 64, true),
							   new Ast.IntExpression(genTokens("1"), (long) 1, (byte) 64, true)));
		}

		@Test
		void lmao3() {
			testInput("1 + 1 + 1", "expression",
				  new Ast.BinaryExpression(genTokens("1", "+", "1", "+", "1"), Ast.BinaryExpression.Kind.ADD, new Ast.BinaryExpression(genTokens("1", "+", "1"), Ast.BinaryExpression.Kind.ADD, new Ast.IntExpression(genTokens("1"), (long) 1, (byte) 64, true), new Ast.IntExpression(genTokens("1"), (long) 1, (byte) 64, true)), new Ast.IntExpression(genTokens("1"), (long) 1, (byte) 64, true)));
		}
	}
}

// Dummy class to compare token texts with ANTLR4's tokens from parser
class DummyToken implements Token {
	private String text;

	public DummyToken(String text) {
		assert text != null;
		this.text = text;
	}

	public int getChannel() {
		assert false;
		return 0;
	}

	public int getCharPositionInLine() {
		assert false;
		return 0;
	}

	public CharStream getInputStream() {
		assert false;
		return null;
	}

	public int getLine() {
		assert false;
		return 0;
	}

	public int getStartIndex() {
		assert false;
		return 0;
	}

	public int getStopIndex() {
		assert false;
		return 0;
	}

	public String getText() {
		return text;
	}

	public int getTokenIndex() {
		assert false;
		return 0;
	}

	public TokenSource getTokenSource() {
		assert false;
		return null;
	}

	public int getType() {
		assert false;
		return 0;
	}
}
