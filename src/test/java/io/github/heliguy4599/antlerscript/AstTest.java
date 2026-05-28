package io.github.heliguy4599.antlerscript;

import java.lang.reflect.*;
import java.util.*;

import org.antlr.v4.runtime.*;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

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
		boolean isConst,
		Ast.Type type,
		String name,
		boolean mutable,
		boolean sealed,
		Ast.Expression value
	) {
		assert name != null && !name.isEmpty();
		List<Token> tokens = new ArrayList<>(genTokens(isConst ? "const" : "let"));
		if (mutable) {
			tokens.addAll(genTokens("mut"));
		}
		if (sealed) {
			tokens.addAll(genTokens("sealed"));
		}
		if (type != null) {
			tokens.addAll(type.tokens);
		}
		tokens.addAll(genTokens(name));
		if (value != null) {
			tokens.addAll(genTokens("="));
			tokens.addAll(value.tokens);
		}
		return new Ast.VariableDeclaration(
			tokens,
			isConst,
			mutable,
			sealed,
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
		case "as": kind = Ast.BinaryExpression.Kind.AS; break;
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
	@DisplayName("Statements")
	class StatementTests {
		@Test
		void expressionStatement() {
			testInput(
				"10",
				"statement",
				new Ast.ExpressionStatement(
					genTokens("10"),
					num(10),
					false
				)
			);
		}

		@Test
		void expressionStatementDeferred() {
			testInput(
				"defer 10",
				"statement",
				new Ast.ExpressionStatement(
					genTokens("defer", "10"),
					num(10),
					true
				)
			);
		}

		@Test
		void breakStatement() {
			testInput(
				"break",
				"statement",
				new Ast.BreakStatement(genTokens("break"))
			);
		}

		@Test
		void continueStatement() {
			testInput(
				"continue",
				"statement",
				new Ast.ContinueStatement(genTokens("continue"))
			);
		}

		@Test
		void returnStatement() {
			testInput(
				"return",
				"statement",
				new Ast.ReturnStatement(genTokens("return"), null)
			);
		}

		@Test
		void returnStatementWithExpression() {
			testInput(
				"return 10",
				"statement",
				new Ast.ReturnStatement(genTokens("return", "10"), num(10))
			);
		}

		// TODO: Test loop statements

		@Test
		void declarationStatementLetNoType() {
			testInput(
				"let i = 10",
				"statement",
				decl(false, null, "i", false, false, num(10))
			);
			testInput(
				"let mut i = 10",
				"statement",
				decl(false, null, "i", true, false, num(10))
			);
			testInput(
				"let sealed i = 10",
				"statement",
				decl(false, null, "i", false, true, num(10))
			);
		}

		@Test
		void declarationStatementLetWithTypeNoInit() {
			testInput(
				"let Int i",
				"statement",
				decl(false, symType("Int"), "i", false, false, null)
			);
			testInput(
				"let mut Int i",
				"statement",
				decl(false, symType("Int"), "i", true, false, null)
			);
			testInput(
				"let sealed Int i",
				"statement",
				decl(false, symType("Int"), "i", false, true, null)
			);
		}

		@Test
		void declarationStatementLetWithTypeWithInit() {
			testInput(
				"let Int i = 10",
				"statement",
				decl(false, symType("Int"), "i", false, false, num(10))
			);
			testInput(
				"let mut Int i = 10",
				"statement",
				decl(false, symType("Int"), "i", true, false, num(10))
			);
			testInput(
				"let sealed Int i = 10",
				"statement",
				decl(false, symType("Int"), "i", false, true, num(10))
			);
		}

		@Test
		void declarationStatementConst() {
			testInput(
				"const i = 10",
				"statement",
				decl(true, null, "i", false, false, num(10))
			);
			testInput(
				"const Int i = 10",
				"statement",
				decl(true, symType("Int"), "i", false, false, num(10))
			);
		}

		@Test
		void typedef() {
			testInput(
				"type MyInt = Int",
				"statement",
				new Ast.Typedef(
					genTokens("type", "MyInt", "=", "Int"),
					"MyInt",
					symType("Int")
				)
			);
		}

		// TODO: Test ifStatement

		// TODO: Test switchStatement

		// TODO: Fix statement_block in language
		// @Test
		// void statementBlock() {
		// 	testInput(
		// 		"{ }",
		// 		"statement",
		// 		new Ast.StatementBlock(
		// 			genTokens("{", "}"),
		// 			null,
		// 			false
		// 		)
		// 	);
		// }

		@Test
		void throwStatement() {
			testInput(
				"throw 10",
				"statement",
				new Ast.ThrowStatement(
					genTokens("throw", "10"),
					num(10)
				)
			);
		}
	}

	@Nested
	@DisplayName("Expressions")
	class ExpressionTests {
		// TODO: Binary expression

		@ParameterizedTest
		@ValueSource(strings = {
			// "not", TODO: using 'not' makes this become an SymbolExpression
			"~",
			"+",
			"-"
		})
		void unaryExpression(String operator) {
			// TODO: Unary expressions only get the operator token, we need to either fix unary() or the AST
			Ast.UnaryExpression.Kind kind = switch (operator) {
			case "not" -> Ast.UnaryExpression.Kind.NOT;
			case "~" -> Ast.UnaryExpression.Kind.BIT_NOT;
			case "+" -> Ast.UnaryExpression.Kind.PLUS;
			case "-" -> Ast.UnaryExpression.Kind.MINUS;
			default -> null;
			};
			assert kind != null;
			testInput(
				operator + "10",
				"expression",
				// unary(operator, num(10)) THIS THROWS
				new Ast.UnaryExpression(
					genTokens(operator), // See, no operand tokens
					kind,
					num(10)
				)
			);
		}

		@Test
		void indexExpression() {
			// TODO: Test with generics
			testInput(
				"list[10]",
				"expression",
				new Ast.IndexExpression(
					// TODO: Is it ok that 'list' token is required to not be here?
					genTokens("[", "10", "]"),
					sym("list"),
					num(10),
					null
				)
			);
		}

		@Test
		void accessExpression() {
			// TODO: Test with generics
			testInput(
				"obj.hello",
				"expression",
				new Ast.AccessExpression(
					genTokens(".", "hello"),
					sym("obj"),
					"hello",
					false,
					null
				)
			);
			testInput(
				"obj?.hello",
				"expression",
				new Ast.AccessExpression(
					genTokens("?.", "hello"),
					sym("obj"),
					"hello",
					true,
					null
				)
			);
		}

		@Test
		void callExpression() {
			testInput(
				"some_func()",
				"expression",
				new Ast.CallExpression(
					genTokens("(", ")"),
					sym("some_func"),
					null
				)
			);
		}

		@Test
		void callExpressionWithArgs() {
			var exprs = new ArrayList<Ast.Argument>();
			exprs.add(new Ast.Argument(
				num(10),
				null,
				false
			));
			testInput(
				"some_func(10)",
				"expression",
				new Ast.CallExpression(
					genTokens("(", "10", ")"),
					sym("some_func"),
					exprs
				)
			);
		}

		@Test
		void symbolExpression() {
			testInput(
				"item",
				"expression",
				sym("item")
			);
		}

		@Test
		void yieldExpression() {
			testInput(
				"yield 10",
				"expression",
				new Ast.YieldExpression(
					genTokens("yield", "10"),
					num(10)
				)
			);
		}
	}

	@Nested
	@DisplayName("Types")
	class TypeTests {
		@Test
		void unionAnd() {
			testInput(
				"a & b",
				"type",
				new Ast.UnionType(
					genTokens("a", "&", "b"),
					Ast.UnionType.Kind.AND,
					symType("a"),
					symType("b")
				)
			);
		}

		@Test
		void unionOr() {
			testInput(
				"a | b",
				"type",
				new Ast.UnionType(
					genTokens("a", "|", "b"),
					Ast.UnionType.Kind.OR,
					symType("a"),
					symType("b")
				)
			);
		}

		@Test
		void nullable() {
			testInput(
				"lmao?",
				"type",
				new Ast.UnionType(
					genTokens("lmao", "?"),
					Ast.UnionType.Kind.OR,
					symType("lmao"),
					new Ast.SymbolType(
						genTokens("?"),
						"Null",
						null
					)
				)
			);
		}

		@Test
		void symbol() {
			testInput(
				"lmao",
				"type",
				symType("lmao")
			);
		}

		@Test
		void symbolGeneric() {
			List<Ast.Type> generics = List.of(
				symType("T1"),
				symType("T2")
			);

			testInput(
				"lmao[T1, T2]",
				"type",
				new Ast.SymbolType(
					genTokens("lmao", "[", "T1", ",", "T2", "]"),
					"lmao",
					generics
				)
			);
		}

		@Test
		void array() {
			testInput(
				"Array[Int, 7]",
				"type",
				new Ast.ArrayType(
					genTokens("Array", "[", "Int", ",", "7", "]"),
					symType("Int"),
					num(7)
				)
			);
		}

		@Test
		void arrayInferred() {
			testInput(
				"Array",
				"type",
				new Ast.ArrayType(
					genTokens("Array"),
					null,
					null
				)
			);
		}

		@Test
		void fullFunction() {
			testInput(
				"Func(:)",
				"type",
				new Ast.FullFunctionType(
					genTokens("Func", "(", ":", ")"),
					null,
					null,
					null,
					null
				)
			);
		}

		// @Test
		// void fullFunctionArgs() {
		// 	testInput(
		// 		"Func(Int a, Int b, Int ...rest:)",
		// 		"type",
		// 		new Ast.FullFunctionType(
		// 			genTokens("Func", "(", "Int", "a", ",", "Int", "b", ",", "Int", "...", "rest", ":", ")"),
		// 			null,
		// 			null,
		// 			null,
		// 			null
		// 		)
		// 	);
		// }

		// @Test
		// void fullFunctionGenerics() {
		// 	testInput(
		// 		"Func<Any T>(:)",
		// 		getTokens("Func", "<", "Any", "T", ">", "(", ":", ")"),
		// 	);
		// }
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
