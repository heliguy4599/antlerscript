import java.io.*;
import java.lang.reflect.*;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.ParseCancellationException;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testing the ANTLR4-generated concrete syntax tree (CST)")
class CstTest {
	// testInput: Input fully matches rule with no errors or leftover tokens
	// testInputNoRule: Input fails to match rule (ANTLR errors suppressed)
	// testInputPartialMatch: Input partially matches but leaves unparsed tokens (ANTLR errors suppressed)
	// testInputNoRulePrintErr: Same as testInputNoRule but prints errors for debugging
	// testInputPartialMatchPrintErr: Same as testInputPartialMatch but prints errors for debugging

	// Custom exception to catch ANTLR4 exceptions
	// This hack is needed because not all ANTLR4 exceptions share the same
	// ANTLR4 parent class
	static class ParseException extends Exception {
		public ParseException(String message) {
			super(message);
		}
	}

	static AntlerScriptParser getNewParser(String input) {
		CharStream charStream = CharStreams.fromString(input);
		AntlerScriptLexer lexer = new AntlerScriptLexer(charStream);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		AntlerScriptParser parser = new AntlerScriptParser(tokens);
		parser.setErrorHandler(new BailErrorStrategy());
		return parser;
	}

	static String parsedGetText(AntlerScriptParser parser) {
		TokenStream tokenStream = parser.getTokenStream();
		int size = tokenStream.size() - 1; // Ignore EOF
		StringBuilder matched = new StringBuilder();
		for (int i = 0; i < size; i++) {
			Token token = tokenStream.get(i);
			matched.append(token.getText());
		}
		return matched.toString();
	}

	static void assertInputMatchesParsed(String input, AntlerScriptParser parser) {
		assertEquals(input, parsedGetText(parser));
	}

	static void assertInputDoesNotMatchParsed(String input, AntlerScriptParser parser) {
		assertNotEquals(input, parsedGetText(parser));
	}

	static void testInput(String input, String ruleName) {
		AntlerScriptParser parser = getNewParser(input);
		assertDoesNotThrow(() -> {
			Method rule = parser.getClass().getMethod(ruleName);
			ParserRuleContext context = (ParserRuleContext) rule.invoke(parser);
			assertNull(context.exception);
		});
		assertEquals(0, parser.getNumberOfSyntaxErrors());
		assertInputMatchesParsed(input, parser);
	}

	static void parseRule(AntlerScriptParser parser, String ruleName) throws Exception {
		Method rule = parser.getClass().getMethod(ruleName);
		try {
			rule.invoke(parser);
		} catch (InvocationTargetException e) {
			Throwable cause = e.getCause();

			if (cause instanceof RecognitionException
			    || cause instanceof ParseCancellationException) {
				throw new ParseException(cause.getMessage());
			}

			throw new RuntimeException(cause);
		}
	}

	static void testInputNoRulePrintErr(String input, String ruleName) {
		AntlerScriptParser parser = getNewParser(input);
		assertThrows(ParseException.class, () -> {
			parseRule(parser, ruleName);
		});
	}

	static void testInputPartialMatchPrintErr(String input, String ruleName) {
		AntlerScriptParser parser = getNewParser(input);
		assertDoesNotThrow(() -> {
			parseRule(parser, ruleName);
		});
		assertEquals(0, parser.getNumberOfSyntaxErrors());
		assertInputDoesNotMatchParsed(input, parser);
	}

	static void testInputPartialMatch(String input, String ruleName) {
		PrintStream originalErr = System.err;
		try (PrintStream nullStream = new PrintStream(new OutputStream() {
			public void write(int b) {}
		})) {
			System.setErr(nullStream);
			testInputPartialMatchPrintErr(input, ruleName);
		} finally {
			System.setErr(originalErr);
		}
	}

	static void testInputNoRule(String input, String ruleName) {
		PrintStream originalErr = System.err;
		try (PrintStream nullStream = new PrintStream(new OutputStream() {
			public void write(int b) {}
		})) {
			System.setErr(nullStream);
			testInputNoRulePrintErr(input, ruleName);
		} finally {
			System.setErr(originalErr);
		}
	}

	@Nested
	@DisplayName("Files")
	class Files {
		@Test
		void file_directive() {
			testInput("$directive hello", "file_directive");
		}

		@ParameterizedTest
		@ValueSource(strings = {
			"",
			"$directive name;",
			"$directive name; $directive name;",
			"$directive name; let Int i = 10",
			"let Int i = 10; let Int i = 10;",
			"$directive name; extends One.Two;",
			"type Thing = List(Int)",
			"$directive name; type Thing = List(Int)",
		})
		void file_program(String input) {
			testInput(input, "program");
		}

		@ParameterizedTest
		@ValueSource(strings = {
			"directive name",
			"$ directive name",
			"$ directive",
			"$directive",
			"$directive $directive name",
			"$directive $directive",
			"$ $directive"
		})
		void fail_file_directive(String input) {
			testInputNoRule(input, "file_directive");
		}

		@ParameterizedTest
		@ValueSource(strings = {
			"capture(One).A -> B",
			"extends Two",
			"$directive name; let Int i = 10; $directive name; type Thing = List(Int)",
			"let Int i = 10; $directive name; extends One.Two; let Int j = 5",
		})
		void fail_no_rule_file_program(String input) {
			testInputNoRule(input, "program");
		}
	}

	@Nested
	@DisplayName("Classes")
	class Classes {
		@ParameterizedTest
		@ValueSource(strings = {
			";;;extends One;;const i = 10",
			"let Int j = 23",
			"capture(Two).i -> j",
		})
		void class_top_level(String input) {
			testInput(input, "class_top_level");
		}

		@ParameterizedTest
		@ValueSource(strings = {
			"extends One, Two, thing = 3, let item = false",
			"const a = 5, capture(Two).three -> item",
			"extends Two",
		})
		void class_header_inside(String input) {
			testInput(input, "class_header_inside");
		}

		@Test
		void class_extends() {
			testInput("extends Obj", "class_extends");
			testInput("extends Obj.Obj, Obj.Obj", "class_extends");
		}

		@Test
		void class_extends_access() {
			testInput("myObj", "class_extends_access");
			testInput("myObj.myVar", "class_extends_access");
			testInput("myObj.myVar0.myVar1.myVar2.myVar3.myVar4.myVar5.myVar6.myVar7.myVar8.myVar9.myVar10.myVar11.myVar12.myVar13.myVar14.myVar15.myVar16.myVar17.myVar18.myVar19.myVar20.myVar21.myVar22.myVar23.myVar24.myVar25.myVar26.myVar27.myVar28.myVar29.myVar30.myVar31.myVar32.myVar33.myVar34.myVar35.myVar36.myVar37.myVar38.myVar39.myVar40.myVar41.myVar42.myVar43.myVar44.myVar45.myVar46.myVar47.myVar48.myVar49.myVar50.myVar51.myVar52.myVar53.myVar54.myVar55", "class_extends_access");
		}

		@ParameterizedTest
		@ValueSource(strings = {
			"constructor(Int i, j, Int ... args) { 2 + 2 }",
			"constructor(Int i, j, Int ... args)",
			"constructor()",
		})
		void class_constructor(String input) {
			testInput(input, "constructor");
		}

		@ParameterizedTest
		@ValueSource(strings = {
			"(Int i)",
			"(i)",
			"(Int ... i)",
			"(j, Int _i, Float ... a_float)",
			"()",
			"(String ... text)",
		})
		void class_constructor_params(String input) {
			testInput(input, "constructor_params");
		}

		@ParameterizedTest
		@ValueSource(strings = {
			"a_symbol",
			"Int something",
			"Int thingie = 2 + 2",
		})
		void class_constructor_params_elm(String input) {
			testInput(input, "constructor_params_elm");
		}

		@Test
		void class_cast() {
			testInput("cast(Int) { 2 + 2 }", "cast");
		}

		@Test
		void class_operator_overload() {
			testInput("operator + (Int thing: Int) { 2 + 2 }", "operator_overload");
		}

		@ParameterizedTest
		@ValueSource(strings = {
			"+",
			"-",
			"*",
			"/",
			"%",
			"<",
			">",
			"++",
			"**",
			"//",
			"%%",
			"==",
			"[]",
		})
		void class_overridable(String input) {
			testInput(input, "overridable");
		}

		@ParameterizedTest
		@ValueSource(strings = {
			"capture (Thing.A).B -> C",
			"capture (Other).item -> whatever = 2 + 2",
		})
		void class_capture(String input) {
			testInput(input, "capture");
		}

		@Test
		void class_extends_assign() {
			testInput("four = 2 + 2", "extends_assign");
		}
	}

	@Nested
	@DisplayName("Enums")
	class Enums {
		@ParameterizedTest
		@ValueSource(strings = {
			"ONE, TWO, THREE",
			"extends One",
			"extends One, TWO, THREE",
			"extends One.Two, THREE",
		})
		void enum_header_inside(String input) {
			testInput(input, "enum_header_inside");
		}

		@ParameterizedTest
		@ValueSource(strings = {
			"",
			"extends, Two",
		})
		void fail_enum_header_inside_no_rule(String input) {
			testInputNoRule(input, "enum_header_inside");
		}

		@ParameterizedTest
		@ValueSource(strings = {
			"ONE, extends Two",
		})
		void fail_enum_header_inside_partial_match(String input) {
			testInputPartialMatch(input, "enum_header_inside");
		}
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
			testInputPartialMatch("if 2 + 2 {} else {} elif 2 + 2", "if_");
		}
	}

	@Nested
	@DisplayName("Expressions")
	class Expression {
		@ParameterizedTest
		@CsvSource({
			".=,  expression_assignment",
			"+=,  expression_assignment",
			"-=,  expression_assignment",
			"*=,  expression_assignment",
			"**=, expression_assignment",
			"/=,  expression_assignment",
			"//=, expression_assignment",
			"%=,  expression_assignment",
			"%%=, expression_assignment",
			"|=,  expression_assignment",
			"&=,  expression_assignment",
			"~=,  expression_assignment",
			"^=,  expression_assignment",
			"<<=, expression_assignment",
			">>=, expression_assignment",
			"+=+, expression_assignment",
			"??=, expression_assignment",
			"=,   expression_assignment",
			"or,  expression_logical_or",
			"??,  expression_logical_or",
			"and,  expression_logical_and",
			"<,  expression_cmp",
			">,  expression_cmp",
			"<=,  expression_cmp",
			">=,  expression_cmp",
			"==,  expression_cmp",
			"!=,  expression_cmp",
			"is,  expression_cmp",
			"in,  expression_cmp",
			"|,  expression_bit_or",
			"^,  expression_bit_xor",
			"&,  expression_bit_and",
			"<<,  expression_bit_shift",
			">>,  expression_bit_shift",
			"+,  expression_add",
			"++,  expression_add",
			"-,  expression_add",
			"*,  expression_mult",
			"**,  expression_mult",
			"/,  expression_mult",
			"//,  expression_mult",
			"%,  expression_mult",
			"%%,  expression_mult",
			"**,  expression_exp",
		})
		void binary(String op, String rule) {
			testInput("i" + op + "i", rule);
			testInput("i" + op + "i" + op + "i" + op + "i" + op + "i" + op + "i" + op + "i" + op + "i" + op + "i" + op + "i" + op + "i", rule);
		}

		@ParameterizedTest
		@ValueSource(strings = {
			"((x + y)\n * (a\n - b)) / (c ** 2)",
		})
		void expression(String expr) {
			testInput(expr, "expression");
		}

		@ParameterizedTest
		@ValueSource(strings = {
			"+",
			"-",
			"~",
			"not",
		})
		void unary(String op) {
			testInput(op + "0", "expression_unary");
		}

		@ParameterizedTest
		@ValueSource(strings = {
			"\"this is a string\"",
			"`this is a raw string`",
			"10",
			"10.20",
			"true",
			"false",
			"null",
			"super",
			"self",
			"to",
			"from",
			"by",
			"myVariable",
			"MyObject{}",
			"List(){}",
			"Array(){}",
			"Class(){}",
			"Map(){}",
			"🪐",
			"(1 + -1 * ~1 ** 1)",
			"Func(:){}",
			"select(true: true)"
		})
		void atom(String word) {
			testInput(word, "expression_atom");
		}

		@ParameterizedTest
		@ValueSource(strings = {
			"select(true: true)",
			"select[0](0: \"is zero\", true: \"is not zero\")",
			"select[0](0: \"is zero\", true: \"is not zero\",)",
			"select[0](0: \"is zero\",\ntrue: \"is not zero\",\n)",
		})
		void select(String expr) {
			testInput(expr, "expression_postfix");
		}

		@Test
		void indexing() {
			testInput("x[10]", "expression_postfix");
		}

		@ParameterizedTest
		@ValueSource(strings = {
			"x()",
			"x(_)",
			"x(_, _,\n_)",
			"x(1+1,\ntrue, false)",
			"x(first=\"first\", false, _)",
		})
		void function_call(String expr) {
			testInput(expr, "expression_postfix");
		}

		@ParameterizedTest
		@ValueSource(strings = {
			"MyOjbect{}",
			"MyOjbect{_}",
			"MyOjbect{_, _,\n_}",
			"MyOjbect{1+1,\ntrue, false}",
			"MyOjbect{first=\"first\", false, _}",
		})
		void new_object_instance(String expr) {
			testInput(expr, "expression_atom");
		}

		@ParameterizedTest
		@ValueSource(strings = {
			"List(){}",
			"List(){_}",
			"List(){_, _,\n_}",
			"List(){1+1,\ntrue, false}",
			"List(){first=\"first\", false, _}",
		})
		void new_list_instance(String expr) {
			testInput(expr, "expression_atom");
		}

		@ParameterizedTest
		@ValueSource(strings = {
			"Array(){}",
			"Array(){_}",
			"Array(){_, _,\n_}",
			"Array(){1+1,\ntrue, false}",
			"Array(){first=\"first\", false, _}",
		})
		void new_array_instance(String expr) {
			testInput(expr, "expression_atom");
		}

		@ParameterizedTest
		@ValueSource(strings = {
			"Class(){}",
			"Class(){_}",
			"Class(){_, _,\n_}",
			"Class(){1+1,\ntrue, false}",
			"Class(){first=\"first\", false, _}",
		})
		void new_class_instancing(String expr) {
			testInput(expr, "expression_atom");
		}

		@ParameterizedTest
		@ValueSource(strings = {
			"Map(){}",
			"Map(){true: false}",
			"Map(){true: false,\nfalse: true}",
		})
		void new_map_instance(String expr) {
			testInput(expr, "expression_atom");
		}

		@Test
		void member_access() {
			testInput("x.y.z", "expression_postfix");
		}

		@Test
		void null_dereferencing() {
			testInput("x?.y?.z", "expression_postfix");
		}

		@ParameterizedTest
		@ValueSource(strings = {
			"((a + b) * (c - d)) / (e ** 2)",
			"(x * y + z) ** (a - b * c)",
			"((a / b) ** c) * ((d + e) - f)",
			"obj.method().field[0].nested?.value",
			"data[i][j].transform().result?.output",
			"api.get(\"users\")[0]?.name.toLowerCase()",
			"compute((x + y) * 2, transform(a, b, c), result ?? default)",
			"map(list, Func(Int x: Int){x ** 2})",
			"filter(data, Func(Int item: bool){item.value > threshold and item.active})",
			"((flags & MASK) | NEW_BIT) ^ (old_flags << 2)",
			"(bits >> 4) & 0xFF | ((high & 0xF0) << 8)",
			"~(a & b) | (c ^ d) & (e | f)",
			"x > 0 and x < 100 or x == -1",
			"a >= b and b >= c and c >= d",
			"(value in range) and (myType is ValidType) and (status != Error)",
			"user?.name ?? profile?.displayName ?? \"Anonymous\"",
			"config?.settings?.theme ?? defaults.theme ?? \"dark\"",
			"data[key] ?? cache[key] ?? fetch(key) ?? null",
			"result += compute(x) * factor ?? default_value",
			"matrix[i][j] *= scale_factor + offset",
			"accumulator.= transform(value, Func(Int v: Int){v ** 2})",
			"not not not not not true",
			"-(+x) * ~(flags & mask)",
			"not (enabled and not disabled)",
			"-velocity.y + ~~Math.floor(delta * 60)",
			"prefix ++ middle ++ suffix ++ extension",
			"path ++ \"/\" ++ filename ++ \".txt\"",
			"List(){1, 2, 3, 4}[2] + Array(int){x, y, z}.length()",
			"Map(){\"x\": 10, \"y\": 20}[key] ?? 0",
			"select[condition](true: positive_result, false: negative_result)",
			"select[x > 0](true: x ** 2, false: -x) + offset",
			"numbers.map(Func(Int n: Int){return n}, int{n * 2 + 1})",
			"items.filter(Func(Int x: bool){x.valid and not x.expired})",
			"Class(x = value, y = other){compute(), transform(), validate()}",
			"Config{host=\"localhost\", port=8080, secure=true}",
			"Math.sqrt(x ** 2 + y ** 2) / magnitude",
			"(sin(angle) * radius) + (cos(angle) * radius)",
			"((((a))))",
			"list[0][1][2].value",
			"func()()(arg1, arg2)",
			"~flags[i] & (mask << shift) | (data >> bits) ^ constant",
			"(base + offset * scale) ** exponent / divisor % modulo",
			"player.position.x += velocity.x * deltaTime * speed_multiplier",
			"total = items.map(Func(Int i: Int){}).sum() * (1.0 - discount)",
			"isValid = (age >= 18) and (email != \"\") and (terms_accepted)",
			"(x +\ny -\nz)",
			"a[\n0\n][\n1\n]",
			"(enabled or force_run) and not (disabled or error_state)",
			"((a and b) or (c and d)) and not (e or f)",
			"x += y *= z **= 2",
			"flags |= mask &= ~disabled_bits",
			"builder.withName(name).withAge(age).withEmail(email).build()",
			"query.match(\"*\").from(\"users\").where(condition).limit(10)",
			"a + b * c ** d / e - f % g",
			"x << 2 + y >> 3 & mask | flags",
			"not a and b or c and not d",
		})
		void complexExpression(String expr) {
			testInput(expr, "expression");
		}

		@ParameterizedTest
		@ValueSource(strings = {
			"a.b.c.d.e.f.g.h.i.j.k.l.m.n.o.p",
			"x[0][1][2][3][4][5]",
			"f()()()()()",
			"a + b + c + d + e + f + g + h + i + j",
			"x * y * z * w * v * u * t * s",
			"a and b and c and d and e and f",
			"x or y or z or w or v or u",
			"obj.deep.nested[i].method(a, b, c)[j]?.field.value",
		})
		void deeplyNestedExpression(String expr) {
			testInput(expr, "expression");
		}

		@ParameterizedTest
		@ValueSource(strings = {
			"((x += 1) *= 2) **= 3",
			"a = b = c = d = e = 0",
			"x += y -= z *= w /= v %= 2",
			"a ?? b ?? c ?? d ?? e ?? f ?? null",
			"x?.y?.z ?? w?.v?.u ?? fallback",
			"total += items[i].price * (1.0 + tax_rate) ?? 0",
			"accumulator.= transform(Func(Int x: Int){x + offset})",
		})
		void assignmentExpression(String expr) {
			testInput(expr, "expression");
		}

		@Test
		void keypair_clause() {
			testInput("\"key\" : value", "keypair_clause");
		}
	}

	@Nested
	@DisplayName("Types")
	class Type {
		@ParameterizedTest
		@ValueSource(strings = {
			"Symbol",
			"Int",
			"I",
			"List()",
			"List(Int)",
			"List(( Int | Float ) & Pineapple)",
			"Array()",
			"Array(Int)",
			"Array(( Int | Float ) & Pineapple)",
			"Array()",
			"Array(Int)",
			"Map()",
			"Map(Int, Int)",
			"Map(Int, ( Int | Float ) & Pineapple)",
			"Map(( Int | Float ) & Pineapple, Int)",
			"Map(( Int | Float ) & Pineapple, ( Int | Float ) & Pineapple)",
			"Func(:)",
			"Func(: Int)",
			"Func(Int a: Int)",
			"Func(Int a, Int b = 0: Int)",
			"Func(( Int | Float ) & Pineapple a = 0, ( Int | Float ) & Pineapple b = 0: ( Int | Float ) & Pineapple)",
			"Class()",
			"Class(let Int a)",
			"Class(let ( Int | Float ) & Pineapple a = 0,)",
			"Enum(ONE, TWO)",
			"Enum(extends Other)",
			"Enum(extends Other, ONE, TWO)",
		})
		void atomic(String type) {
			testInput(type, "type_atomic");
		}

		@ParameterizedTest
		@ValueSource(strings = {
			"_",
			"(Int &)",
			"(Int & & Int)",
			"(Int |)",
			"(Int | | Int)",
			"List(",
			"List(Int Int",
			"List(( Int | Float  & Pineapple",
			"Array(",
			"Array(Int",
			"Array(( Int | Float  & Pineapple",
			"Array(",
			"Array(Int",
			"Map(",
			"Map(Int, Int",
			"Map(Int, ( Int | Float  & Pineapple",
			"Map(( Int | Float  & Pineapple, Int",
			"Map(( Int | Float  & Pineapple, ( Int | Float  & Pineapple",
			"Func(:",
			"Func(: Int",
			"Func(Int a: Int",
			"Func(Int a, Int b = 0: Int",
			"Func(( Int | Float  & Pineapple a = 0, ( Int | Float  & Pineapple b = 0: ( Int | Float  & Pineapple",
			"Class(",
			"Class(let Int a",
			"Class(let ( Int | Float  & Pineapple a = 0,",
			"Enum(",
			"Enum()",
			"Enum(extends Other",
			"Enum(One Two)",
			"Enum(One",
			"Enum(One, Two",
		})
		void fail_atomic(String type) {
			testInputNoRule(type, "type_atomic");
		}
	}

	@Test
	void var_args() {
		testInput("Int ... argument", "var_args");
	}
}
