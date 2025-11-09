import java.lang.reflect.*;

import org.antlr.v4.runtime.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

// Test starting rule
// class_extends_access
// : SYMBOL ( '.' SYMBOL )*
// ;


@DisplayName("Testing the ANTLR4-generated concrete syntax tree (CST)")
class CSTTest {
	static void testInput(String input, String ruleName) {
		CharStream charStream = CharStreams.fromString(input);
		AntlerScriptLexer lexer = new AntlerScriptLexer(charStream);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		AntlerScriptParser parser = new AntlerScriptParser(tokens);
		assertDoesNotThrow(() -> {
			Method rule = parser.getClass().getMethod(ruleName);
			ParserRuleContext context = (ParserRuleContext) rule.invoke(parser);
			assertEquals(0, parser.getNumberOfSyntaxErrors());
			assertNull(context.exception);
			assertEquals(input.replaceAll("[ \\t\\f\\u000B]+", ""), context.getText());
		});
	}

	static void testInputNoRule(String input, String ruleName) {
		CharStream charStream = CharStreams.fromString(input);
		AntlerScriptLexer lexer = new AntlerScriptLexer(charStream);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		AntlerScriptParser parser = new AntlerScriptParser(tokens);
		assertDoesNotThrow(() -> {
			Method rule = parser.getClass().getMethod(ruleName);
			ParserRuleContext context = (ParserRuleContext) rule.invoke(parser);
			assertNotEquals(0, parser.getNumberOfSyntaxErrors());
			assertNotNull(context.exception);
		});
	}

	static void testInputPartialMatch(String input, String ruleName) {
		CharStream charStream = CharStreams.fromString(input);
		AntlerScriptLexer lexer = new AntlerScriptLexer(charStream);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		AntlerScriptParser parser = new AntlerScriptParser(tokens);
		assertDoesNotThrow(() -> {
			Method rule = parser.getClass().getMethod(ruleName);
			ParserRuleContext context = (ParserRuleContext) rule.invoke(parser);
			assertEquals(0, parser.getNumberOfSyntaxErrors());
			assertNull(context.exception);
			assertNotEquals(input.replaceAll("[ \\t\\f\\u000B]+", ""), context.getText());
		});
	}

	@Test
	void class_extends_access() {
		testInput("myObj", "class_extends_access");
		testInput("myObj.myVar", "class_extends_access");
		testInput("myObj.myVar0.myVar1.myVar2.myVar3.myVar4.myVar5.myVar6.myVar7.myVar8.myVar9.myVar10.myVar11.myVar12.myVar13.myVar14.myVar15.myVar16.myVar17.myVar18.myVar19.myVar20.myVar21.myVar22.myVar23.myVar24.myVar25.myVar26.myVar27.myVar28.myVar29.myVar30.myVar31.myVar32.myVar33.myVar34.myVar35.myVar36.myVar37.myVar38.myVar39.myVar40.myVar41.myVar42.myVar43.myVar44.myVar45.myVar46.myVar47.myVar48.myVar49.myVar50.myVar51.myVar52.myVar53.myVar54.myVar55", "class_extends_access");
	}

	@Nested
	@DisplayName("Statements")
	class Statements {
		@Test
		void statement_expression() {
			testInput("2 + 2", "statement");
		}

		@Test
		void statement_defer_expression() {
			testInput("defer 1 + 1", "statement");
		}

		@Test
		void statement_break() {
			testInput("break", "statement");
		}

		@Test
		void statement_continue() {
			testInput("continue", "statement");
		}

		@Test
		void statement_return() {
			testInput("return", "statement");
		}

		@Test
		void statement_return_expression() {
			testInput("return 2 + 2", "statement");
		}

		@Test
		void statement_loop_to() {
			testInput("loop to 2 + 2 {}", "loop");
		}

		@Test
		void statement_loop_from_to() {
			testInput("loop from 2 + 2 to 2 + 2 {}", "loop");
		}

		@Test
		void statement_loop_to_by() {
			testInput("loop to 2 + 2 by 2 + 2 {}", "loop");
		}

		@Test
		void statement_loop_from_to_by() {
			testInput("loop from 2 + 2 to 2 + 2 by 2 + 2 {}", "loop");
		}

		@Test
		void statement_loop_to_arrow() {
			testInput("loop to 2 + 2 -> i {}", "loop");
		}

		@Test
		void statement_loop_from_to_arrow() {
			testInput("loop from 2 + 2 to 2 + 2 -> i {}", "loop");
		}

		@Test
		void statement_loop_to_by_arrow() {
			testInput("loop to 2 + 2 by 2 + 2 -> i {}", "loop");
		}

		@Test
		void statement_loop_from_to_by_arrow() {
			testInput("loop from 2 + 2 to 2 + 2 by 2 + 2 -> i {}", "loop");
		}

		@Test
		void statement_while() {
			testInput("while 2 + 2 {}", "while_");
		}

		@Test
		void statement_iterate() {
			testInput("iterate 2 + 2 {}", "iterate");
		}

		@Test
		void statement_iterate_one_arg() {
			testInput("iterate 2 + 2 -> i {}", "iterate");
		}

		@Test
		void statement_iterate_two_args() {
			testInput("iterate 2 + 2 -> i, j {}", "iterate");
		}

		@Test
		void statement_declaration_const_assign() {
			testInput("const name = 2 + 2", "declaration");
		}

		@Test
		void statement_declaration_let_assign() {
			testInput("let name = 2 + 2", "declaration");
		}

		@Test
		void statement_declaration_let_mut_assign() {
			testInput("let mut name = 2 + 2", "declaration");
		}

		@Test
		void statement_declaration_const_type_assign() {
			testInput("const Int name = 2 + 2", "declaration");
		}

		@Test
		void statement_declaration_let_type_assign() {
			testInput("let Int name = 2 + 2", "declaration");
		}

		@Test
		void statement_declaration_let_mut_type_assign() {
			testInput("let mut Int name = 2 + 2", "declaration");
		}

		@Test
		void statement_typedef() {
			testInput("type MyType = Int", "typedef");
		}

		@Test
		void statement_if() {
			testInput("if 2 + 2 { 2 + 2 }", "if_");
		}

		@Test
		void statement_elif() {
			testInput("elif 2 + 2 { 2 + 2 }", "elif");
		}

		@Test
		void statement_else() {
			testInput("else { 2 + 2 }", "else_");
		}

		@Test
		void statement_if_elif() {
			testInput("if 2 + 2 { 2 + 2 } elif 2 + 2 { 2 + 2 }", "if_");
		}

		@Test
		void statement_if_else() {
			testInput("if 2 + 2 { 2 + 2 } else { 2 + 2 }", "if_");
		}

		@Test
		void statement_if_elif_else() {
			testInput("if 2 + 2 { 2 + 2 } elif 2 + 2 { 2 + 2 } else { 2 + 2 }", "if_");
		}

		@Test
		void statement_if_elif_elif_else() {
			testInput("if 2 + 2 { 2 + 2 } elif 2 + 2 { 2 + 2 } elif 2 + 2 { 2 + 2 } else { 2 + 2 }", "if_");
		}

		@Test
		void statement_case() {
			testInput("case 2 + 2 { 2 + 2 }", "case_");
		}

		@Test
		void statement_case_multi() {
			testInput("case 2 + 2, 2 + 2 { 2 + 2 }", "case_");
		}

		@Test
		void statement_switch_case() {
			testInput("switch 2 + 2 case 2 + 2 { 2 + 2 }", "switch_");
		}

		@Test
		void statement_switch_case_else() {
			testInput("switch 2 + 2 case 2 + 2 { 2 + 2 } else { 2 + 2 }", "switch_");
		}

		@Test
		void statement_switch_case_case_multi() {
			testInput("switch 2 + 2 case 2 + 2, 2 + 2 { 2 + 2 } case 2 + 2 { 2 + 2 }", "switch_");
		}

		@Test
		void statement_switch_case_case_multi_else() {
			testInput("switch 2 + 2 case 2 + 2, 2 + 2 { 2 + 2 } case 2 + 2 { 2 + 2 } else { 2 + 2 }", "switch_");
		}

		@Test
		void statement_block() {
			testInput("{ 2 + 2 }", "statement");
		}

		@Test
		void defer_statement_block() {
			testInput("defer { 2 + 2 }", "statement");
		}
	}

	@Nested
	@DisplayName("NegativeStatements")
	class NegativeStatements {
		@Test
		void fail_statement_break() {
			testInputPartialMatch("break 5", "statement");
		}

		@Test
		void fail_statement_continue() {
			testInputPartialMatch("continue 6", "statement");
		}

		@Test
		void fail_statement_declaration_const() {
			testInputNoRule("const name", "declaration");
		}

		@Test
		void fail_statement_declaration_let() {
			testInputNoRule("let name", "declaration");
		}

		@Test
		void fail_statement_declaration_let_mut() {
			testInputNoRule("let mut name", "declaration");
		}

		@Test
		void fail_statement_switch_else() {
			testInputNoRule("switch 2 + 2 else { 2 + 2 }", "switch_");
		}

		@Test
		void fail_statement_if_else_elif() {
			testInputNoRule("if 2 + 2 {} else {} elif 2 + 2", "if_");
		}
	}
}
