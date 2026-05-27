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

	static void testInput(String input, String ruleName, Ast.Node expected) {
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

	static List<Token> genTokens(Ast.Argument arg) {
		assert arg != null;

		if (arg.isBlank()) {
			return genTokens("_");
		}

		List<Token> tokens = new ArrayList<>();

		if (arg.keyword() != null && !arg.keyword().isEmpty()) {
			tokens.addAll(genTokens(arg.keyword(), "="));
		}

		tokens.addAll(arg.value().tokens);

		return tokens;
	}

	// Warning: does not include trialing commas
	static List<Token> genTokens(Ast.ListArgs args) {
		assert args != null;

		List<Token> tokens = new ArrayList<>();

		for (Ast.Argument arg : args.args()) {
			tokens.addAll(genTokens(arg));
			tokens.addAll(genTokens(","));
		}

		// Remove the trailing comma
		if (!args.args().isEmpty()) {
			tokens.remove(tokens.size() - 1);
		}

		return tokens;
	}

	static List<Token> genTokens(Ast.KeyValuePair pair) {
		assert pair != null;

		List<Token> tokens = new ArrayList<>();

		tokens.addAll(pair.left().tokens);
		tokens.addAll(genTokens(":"));
		tokens.addAll(pair.right().tokens);

		return tokens;
	}

	static List<Token> genTokens(Ast.ListKeyValuePairs pairs) {
		assert pairs != null;

		List<Token> tokens = new ArrayList<>();

		for (Ast.KeyValuePair pair : pairs.pairs()) {
			tokens.addAll(genTokens(pair));
			tokens.addAll(genTokens(","));
		}

		// Remove the trailing comma
		if (!pairs.pairs().isEmpty()) {
			tokens.remove(tokens.size() - 1);
		}

		return tokens;
	}

	static List<Token> genTokens(String... tokens) {
		var result = new ArrayList<Token>();

		for (String tokenText : tokens) {
			result.add(new DummyToken(tokenText));
		}

		return result;
	}

	static Ast.SymbolType symType(String name) {
		assert name != null && !name.isEmpty();
		return new Ast.SymbolType(genTokens(name), name, null);
	}

	static Ast.SymbolExpression sym(String name) {
		assert name != null && !name.isEmpty();
		return new Ast.SymbolExpression(genTokens(name), name, null);
	}

	static Ast.IntExpression num(long value) {
		return new Ast.IntExpression(
			genTokens(String.valueOf(value)),
			value,
			(byte) 64,
			true
		);
	}

	static Ast.FloatExpression fnum(double value) {
		return new Ast.FloatExpression(
			genTokens(String.valueOf(value)), // flimsy
			value,
			(byte) 64
		);
	}

	static Ast.BooleanExpression bool(boolean value) {
		return new Ast.BooleanExpression(
			genTokens(value ? "true" : "false"),
			value
		);
	}

	static Ast.StringExpression string(String string) {
		assert string != null;
		return new Ast.StringExpression(
			genTokens("\"" + string + "\""),
			string,
			false
		);
	}

	static Ast.VariableDeclaration decl(
		Ast.Type type,
		String name,
		Ast.Expression value
	) {
		assert type != null;
		assert name != null && !name.isEmpty();
		assert value != null;

		List<Token> tokens = new ArrayList<>();
		tokens.addAll(genTokens("let"));
		tokens.addAll(type.tokens);
		tokens.addAll(genTokens(name));
		tokens.addAll(genTokens("="));
		tokens.addAll(value.tokens);

		return new Ast.VariableDeclaration(
			tokens,
			false,
			false,
			false,
			type,
			name,
			value,
			null
		);
	}

	static Ast.BinaryExpression bin(
		Ast.Expression left,
		String op,
		Ast.Expression right
	) {

		Ast.BinaryExpression.Kind kind = null;

		switch (op) {
		case "+=": kind = Ast.BinaryExpression.Kind.PLUS_ASSIGN; break;
		case "-=": kind = Ast.BinaryExpression.Kind.MINUS_ASSIGN; break;
		case "*=": kind = Ast.BinaryExpression.Kind.MULTIPLY_ASSIGN; break;
		case "**=": kind = Ast.BinaryExpression.Kind.EXPONENT_ASSIGN; break;
		case "/=": kind = Ast.BinaryExpression.Kind.DIVIDE_ASSIGN; break;
		case "//=": kind = Ast.BinaryExpression.Kind.FLOOR_DIVIDE_ASSIGN; break;
		case "%%=": kind = Ast.BinaryExpression.Kind.MODULO_ASSIGN; break;
		case "%=": kind = Ast.BinaryExpression.Kind.REMAINDER_ASSIGN; break;
		case "|=": kind = Ast.BinaryExpression.Kind.BIT_OR_ASSIGN; break;
		case "&=": kind = Ast.BinaryExpression.Kind.BIT_AND_ASSIGN; break;
		case "~=": kind = Ast.BinaryExpression.Kind.BIT_NOT_ASSIGN; break;
		case "^=": kind = Ast.BinaryExpression.Kind.BIT_XOR_ASSIGN; break;
		case "<<=": kind = Ast.BinaryExpression.Kind.BIT_LSHIFT_ASSIGN; break;
		case ">>=": kind = Ast.BinaryExpression.Kind.BIT_RSHIFT_ASSIGN; break;
		case "++=": kind = Ast.BinaryExpression.Kind.CONCAT_ASSIGN; break;
		case "??=": kind = Ast.BinaryExpression.Kind.NULLISH_ASSIGN; break;
		case ".=": kind = Ast.BinaryExpression.Kind.CHAIN_ASSIGN; break;
		case "=": kind = Ast.BinaryExpression.Kind.ASSIGN; break;
		case "or": kind = Ast.BinaryExpression.Kind.OR; break;
		case "??": kind = Ast.BinaryExpression.Kind.NULLISH; break;
		case "and": kind = Ast.BinaryExpression.Kind.AND; break;
		case "<": kind = Ast.BinaryExpression.Kind.LESSER_THAN; break;
		case ">": kind = Ast.BinaryExpression.Kind.GREATER_THAN; break;
		case "<=": kind = Ast.BinaryExpression.Kind.LESSER_OR_EQUAL; break;
		case ">=": kind = Ast.BinaryExpression.Kind.GREATER_OR_EQUAL; break;
		case "==": kind = Ast.BinaryExpression.Kind.EQUAL; break;
		case "!=": kind = Ast.BinaryExpression.Kind.NOT_EQUAL; break;
		case "is": kind = Ast.BinaryExpression.Kind.IS; break;
		case "in": kind = Ast.BinaryExpression.Kind.IN; break;
		case "|>": kind = Ast.BinaryExpression.Kind.FUNC_PIPE; break;
		case "|": kind = Ast.BinaryExpression.Kind.BIT_OR; break;
		case "^": kind = Ast.BinaryExpression.Kind.BIT_XOR; break;
		case "&": kind = Ast.BinaryExpression.Kind.BIT_AND; break;
		case "<<": kind = Ast.BinaryExpression.Kind.BIT_LSHIFT; break;
		case ">>": kind = Ast.BinaryExpression.Kind.BIT_RSHIFT; break;
		case "+": kind = Ast.BinaryExpression.Kind.ADD; break;
		case "-": kind = Ast.BinaryExpression.Kind.SUBTRACT; break;
		case "++": kind = Ast.BinaryExpression.Kind.CONCAT; break;
		case "*": kind = Ast.BinaryExpression.Kind.MULTIPLY; break;
		case "/": kind = Ast.BinaryExpression.Kind.DIVIDE; break;
		case "//": kind = Ast.BinaryExpression.Kind.FLOOR_DIVIDE; break;
		case "%%": kind = Ast.BinaryExpression.Kind.MODULO; break;
		case "%": kind = Ast.BinaryExpression.Kind.REMAINDER; break;
		case "**": kind = Ast.BinaryExpression.Kind.EXPONENT; break;
		default: assert false;
		}

		List<Token> tokens = new ArrayList<>();
		tokens.addAll(left.tokens);
		tokens.addAll(genTokens(op));
		tokens.addAll(right.tokens);

		return new Ast.BinaryExpression(tokens, kind, left, right);
	}

	static Ast.UnaryExpression unary(
		String op,
		Ast.Expression operand
	) {

		Ast.UnaryExpression.Kind kind = null;

		switch (op) {
		case "not": kind = Ast.UnaryExpression.Kind.NOT; break;
		case "~": kind = Ast.UnaryExpression.Kind.BIT_NOT; break;
		case "+": kind = Ast.UnaryExpression.Kind.PLUS; break;
		case "-": kind = Ast.UnaryExpression.Kind.MINUS; break;
		default: assert false;
		}

		List<Token> tokens = new ArrayList<>();
		tokens.addAll(genTokens(op));
		tokens.addAll(operand.tokens);

		return new Ast.UnaryExpression(tokens, kind, operand);
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
