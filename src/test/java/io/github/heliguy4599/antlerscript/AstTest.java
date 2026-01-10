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

	static Ast.SymbolType symbolTypeFor(String name) {
		assert name != null;
		return new Ast.SymbolType(genTokens(name), name);
	}

	static Ast.BooleanExpression boolExpFor(boolean value) {
		return new Ast.BooleanExpression(genTokens(value ? "true" : "false"), value);
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
		@Test
		void VariableDeclarationLet() {
			testInput(
				"let Int i",
				"declaration",
				new Ast.VariableDeclaration(
					genTokens("let", "Int", "i"),
					false,
					false,
					symbolTypeFor("Int"),
					"i",
					null
				)
			);
		}
	}

	@Nested
	@DisplayName("Expressions")
	class ExpressionTests {
		// @Test
		// TODO: This seems to cause an infinite loop??
		void UnaryExpressionNot() {
			testInput(
				"not true",
				"expression",
				new Ast.UnaryExpression(
					genTokens("not", "true"),
					Ast.UnaryExpression.Kind.NOT,
					boolExpFor(true)
				)
			);
		}

		// @Test
		// TODO: This seems to cause an infinite loop??
		void ChainedUnaryExpressionNot() {
			testInput(
				"not not true",
				"expression",
				new Ast.UnaryExpression(
					genTokens("not", "not", "true"),
					Ast.UnaryExpression.Kind.NOT,
					new Ast.UnaryExpression(
						genTokens("not", "true"),
						Ast.UnaryExpression.Kind.NOT,
						boolExpFor(true)
					)
				)
			);
		}

		@Test
		void BinaryExpressionAdd() {
			testInput(
				"1 + 1",
				"expression",
				new Ast.BinaryExpression(
					genTokens("1", "+", "1"),
					Ast.BinaryExpression.Kind.ADD,
					new Ast.IntExpression(genTokens("1"), (long) 1, (byte) 64, true),
					new Ast.IntExpression(genTokens("1"), (long) 1, (byte) 64, true)
				)
			);
		}

		@Test
		void ChainedBinaryExpressionAdd() {
			testInput(
				"1 + 1 + 1",
				"expression",
				new Ast.BinaryExpression(
					genTokens("1", "+", "1", "+", "1"),
					Ast.BinaryExpression.Kind.ADD,
					new Ast.BinaryExpression(
						genTokens("1", "+", "1"),
						Ast.BinaryExpression.Kind.ADD,
						new Ast.IntExpression(genTokens("1"), (long) 1, (byte) 64, true),
						new Ast.IntExpression(genTokens("1"), (long) 1, (byte) 64, true)
					),
					new Ast.IntExpression(genTokens("1"), (long) 1, (byte) 64, true)
				)
			);
		}
	}

	@Nested
	@DisplayName("Types")
	class TypeTests {
		@Test
		void SymbolType() {
			testInput("Int", "type_atomic", symbolTypeFor("Int"));
		}

		@Test
		void ListType() {
			testInput(
				"List(Int)",
				"type_atomic",
				new Ast.ListType(
					genTokens("List", "(", "Int", ")"),
					symbolTypeFor("Int")
				)
			);
		}

		// @Test
		// TODO: This fails
		void ArrayType() {
			testInput(
				"Array(Int)",
				"type_atomic",
				new Ast.ArrayType(
					genTokens("Array", "(", "Int", ")"),
					symbolTypeFor("Int"),
					null
				)
			);
		}

		@Test
		void MapType() {
			testInput(
				"Map(String, Int)",
				"type_atomic",
				new Ast.MapType(
					genTokens("Map", "(", "String", ",", "Int", ")"),
					symbolTypeFor("String"),
					symbolTypeFor("Int")
				)
			);
		}

		// @Test
		// TODO: This fails, due to expecting a DeclarationClassMember yet getting a VariableDeclarationMember
		void ClassType() {
			testInput(
				"Class(let Int i)",
				"type_atomic",
				new Ast.ClassType(
					genTokens("Class", "(", "let", "Int", "i", ")"),
					null,
					List.of(new Ast.DeclarationClassMember(
						genTokens("let", "Int", "i"),
						new Ast.VariableDeclaration(
							genTokens("let", "Int", "i"),
							false,
							false,
							symbolTypeFor("Int"),
							"i",
							null
						)
					))
				)
			);
		}

		// @Test
		// TODO: This fails
		void EnumType() {
			testInput(
				"Enum(ONE, TWO)",
				"type_atomic",
				new Ast.EnumType(
					genTokens("Enum", "(", "ONE", ",", "TWO", ")"),
					null,
					Arrays.asList("ONE", "TWO")
				)
			);
		}

		@Test
		void FuncType() {
			testInput(
				"Func(: Int)",
				"type_atomic",
				new Ast.FunctionType(
					genTokens("Func", "(", ":", "Int", ")"),
					null,
					symbolTypeFor("Int")
				)
			);
		}

		@Test
		void SelfType() {
			testInput("Self", "type_atomic", new Ast.SelfClassType(genTokens("Self")));
		}

		@Test
		void GroupType() {
			testInput(
				"(Int)",
				"type_atomic",
				// TODO: Is this correct? There is no Ast.GroupType, but apparently this works... What about "(", ")"?
				symbolTypeFor("Int")
			);
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
