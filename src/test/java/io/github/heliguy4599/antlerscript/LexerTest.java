package io.github.heliguy4599.antlerscript;

import org.antlr.v4.runtime.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("Testing the ANTLR4-generated lexer")
class LexerTest {
	// Type alias class
	abstract static class L extends AntlerScriptLexer {
		L(CharStream input) {
			super(input);
		}
	}

	static void testInput(String input, int expectedTokenType) {
		CharStream charStream = CharStreams.fromString(input);
		AntlerScriptLexer lexer = new AntlerScriptLexer(charStream);
		Token next = lexer.nextToken();
		Vocabulary v = lexer.getVocabulary();
		assertEquals(input, next.getText());
		assertEquals(v.getDisplayName(expectedTokenType),
			     v.getDisplayName(next.getType()));
	}

	@Test
	void directive() {
		testInput(":: classname", L.CLASSNAME_DIRECTIVE);
		testInput(":: namespace", L.NAMESPACE_DIRECTIVE);
		testInput(":: using", L.USING_DIRECTIVE);
		testInput("::", L.OTHER_DIRECTIVE);
	}

	@Test
	void decorator() {
		testInput("@", L.DECORATOR);
	}

	@ParameterizedTest
	@ValueSource(strings = {
		"123",
		"1_2_3",
		"123i8",
		"123i16",
		"123i32",
		"123i64",
		"123u8",
		"123u16",
		"123u32",
		"123u64",
	})
	void integerDec(String i) {
		testInput(i, L.INTEGER);
	}

	@ParameterizedTest
	@ValueSource(strings = {
		"0xFF",
		"0xF_F",
		"0xFFi8",
		"0xFFi16",
		"0xFFi32",
		"0xFFi64",
		"0xFFu8",
		"0xFFu16",
		"0xFFu32",
		"0xFFu64",
	})
	void integerHex(String f) {
		testInput(f, L.INTEGER);
	}

	@ParameterizedTest
	@ValueSource(strings = {
		"0b101",
		"0b1_0_1",
		"0b101i8",
		"0b101i16",
		"0b101i32",
		"0b101i64",
		"0b101u8",
		"0b101u16",
		"0b101u32",
		"0b101u64",
	})
	void integerBin(String i) {
		testInput(i, L.INTEGER);
	}

	@ParameterizedTest
	@ValueSource(strings = {
		"3.14",
		"0_3.1_4",
		"3f8",
		"3f16",
		"3f32",
		"3f64",
		"3.14f8",
		"3.14f16",
		"3.14f32",
		"3.14f64",
	})
	void floatDec(String f) {
		testInput(f, L.FLOAT);
	}

	@ParameterizedTest
	@ValueSource(strings = {
		"3e10",
		"3e+10",
		"3e-10",
		"3.14e10",
		"3.14e+10",
		"3.14e-10",
		"0_3.1_4e1_0",
		"0_3.1_4e+1_0",
		"0_3.1_4e-1_0",
		"3.14e10f8",
		"3.14e10f16",
		"3.14e10f32",
		"3.14e10f64",
		"3.14e+10f8",
		"3.14e+10f16",
		"3.14e+10f32",
		"3.14e+10f64",
		"3.14e-10f8",
		"3.14e-10f16",
		"3.14e-10f32",
		"3.14e-10f64",
	})
	void floatDecExponent(String f) {
		testInput(f, L.FLOAT);
	}

	@ParameterizedTest
	@ValueSource(strings = {
		"0xF.F",
		"0x0_F.F_0",
		"0xF.Ff8",
		"0xF.Ff16",
		"0xF.Ff32",
		"0xF.Ff64",
	})
	void floatHex(String f) {
		testInput(f, L.FLOAT);
	}

	@ParameterizedTest
	@ValueSource(strings = {
		"0xFe10",
		"0xFe+10",
		"0xFe-10",
		"0xF.Fe10",
		"0xF.Fe10f8",
		"0xF.Fe10f16",
		"0xF.Fe10f32",
		"0xF.Fe10f64",
		"0xF.Fe+10",
		"0xF.Fe+10f8",
		"0xF.Fe+10f16",
		"0xF.Fe+10f32",
		"0xF.Fe+10f64",
		"0xF.Fe-10",
		"0xF.Fe-10f8",
		"0xF.Fe-10f16",
		"0xF.Fe-10f32",
		"0xF.Fe-10f64",
		"0xF.Fp10",
		"0xF.Fp10f8",
		"0xF.Fp10f16",
		"0xF.Fp10f32",
		"0xF.Fp10f64",
		"0xF.Fp+10",
		"0xF.Fp+10f8",
		"0xF.Fp+10f16",
		"0xF.Fp+10f32",
		"0xF.Fp+10f64",
		"0xF.Fp-10",
		"0xF.Fp-10f8",
		"0xF.Fp-10f16",
		"0xF.Fp-10f32",
		"0xF.Fp-10f64",
	})
	void floatHexExponent(String f) {
		testInput(f, L.FLOAT);
	}

	@Test
	void rawString() {
		testInput("`hello`", L.RAW_STRING);
		testInput("`hello`", L.RAW_STRING);
		testInput("`he\n\nllo`", L.RAW_STRING);
	}

	@Test
	void string() {
		testInput("\"hello\"", L.STRING);
		testInput("\"he\n\nllo\"", L.STRING);
		testInput("\"he\\\"llo\"", L.STRING);
	}

	@Test
	void and() {
		testInput("and", L.AND);
	}

	@Test
	void array() {
		testInput("Array", L.ARRAY);
	}

	@Test
	void as() {
		testInput("as", L.AS);
	}

	@Test
	void breakKw() {
		testInput("break", L.BREAK);
	}

	@Test
	void alias() {
		testInput("alias", L.ALIAS);
	}

	@Test
	void caseKw() {
		testInput("case", L.CASE);
	}

	@Test
	void cast() {
		testInput("cast", L.CAST);
	}

	@Test
	void classKw() {
		testInput("Class", L.CLASS);
	}

	@Test
	void enumKw() {
		testInput("Enum", L.ENUM);
	}

	@Test
	void constKw() {
		testInput("const", L.CONST);
	}

	@Test
	void constructor() {
		testInput("constructor", L.CONSTRUCTOR);
	}

	@Test
	void continueKw() {
		testInput("continue", L.CONTINUE);
	}

	@Test
	void coroutine() {
		testInput("Coroutine", L.COROUTINE);
	}

	@Test
	void defer() {
		testInput("defer", L.DEFER);
	}

	@Test
	void elif() {
		testInput("elif", L.ELIF);
	}

	@Test
	void ellipsis() {
		testInput("...", L.ELLIPSIS);
	}

	@Test
	void elseKw() {
		testInput("else", L.ELSE);
	}

	@Test
	void extendsKw() {
		testInput("extends", L.EXTENDS);
	}

	@Test
	void falseKw() {
		testInput("false", L.FALSE);
	}

	@Test
	void func() {
		testInput("Func", L.FUNC);
	}

	@Test
	void ifKw() {
		testInput("if", L.IF);
	}

	@Test
	void in() {
		testInput("in", L.IN);
	}

	@Test
	void is() {
		testInput("is", L.IS);
	}

	@Test
	void let() {
		testInput("let", L.LET);
	}

	@Test
	void loop() {
		testInput("loop", L.LOOP);
	}

	@Test
	void map() {
		testInput("Map", L.MAP);
	}

	@Test
	void mut() {
		testInput("mut", L.MUT);
	}

	@Test
	void sealed() {
		testInput("sealed", L.SEALED);
	}

	@Test
	void newline() {
		testInput("\n", L.NEWLINE);
		testInput("\r", L.NEWLINE);
		testInput("\r\n", L.NEWLINE);
	}

	@Test
	void not() {
		testInput("not", L.NOT);
	}

	@Test
	void nullKw() {
		testInput("null", L.NULL);
	}

	@Test
	void operator() {
		testInput("operator", L.OPERATOR);
	}

	@Test
	void or() {
		testInput("or", L.OR);
	}

	@Test
	void over() {
		testInput("over", L.OVER);
	}

	@Test
	void returnKw() {
		testInput("return", L.RETURN);
	}

	@Test
	void select() {
		testInput("select", L.SELECT);
	}

	@Test
	void semicolon() {
		testInput(";", L.SEMICOLON);
	}

	@Test
	void superKw() {
		testInput("super", L.SUPER);
	}

	@Test
	void switchKw() {
		testInput("switch", L.SWITCH);
	}

	@Test
	void throwKw() {
		testInput("throw", L.THROW);
	}

	@Test
	void trueKw() {
		testInput("true", L.TRUE);
	}

	@Test
	void tryKw() {
		testInput("try", L.TRY);
	}

	@Test
	void type() {
		testInput("type", L.TYPE);
	}

	@Test
	void underscore() {
		testInput("_", L.UNDERSCORE);
	}

	@Test
	void whileKw() {
		testInput("while", L.WHILE);
	}

	@Test
	void yield() {
		testInput("yield", L.YIELD);
	}

	@Test
	void whitespace() {
		testInput(" ", L.WHITESPACE);
		testInput("\t", L.WHITESPACE);
		testInput("\f", L.WHITESPACE);
		testInput("\u000B", L.WHITESPACE); // vertical tab
		testInput("\u000B \f\t\u000B \t\f\u000B \f\t\u000B",
			  L.WHITESPACE);
	}

	// TODO: add more symbol testing
	@Test
	void symbol() {
		testInput("myVar", L.SYMBOL);
	}

	@Test
	void from() {
		testInput("from", L.FROM);
	}

	@Test
	void to() {
		testInput("to", L.TO);
	}

	@Test
	void by() {
		testInput("by", L.BY);
	}

	@Test
	void lparen() {
		testInput("(", L.LPAREN);
	}

	@Test
	void rparen() {
		testInput(")", L.RPAREN);
	}

	@Test
	void lbrack() {
		testInput("[", L.LBRACK);
	}

	@Test
	void rbrack() {
		testInput("]", L.RBRACK);
	}

	@Test
	void lcurly() {
		testInput("{", L.LCURLY);
	}

	@Test
	void rcurly() {
		testInput("}", L.RCURLY);
	}

	@Test
	void lgeneric() {
		testInput("<[", L.LGENERIC);
	}

	@Test
	void rgeneric() {
		testInput("]>", L.RGENERIC);
	}

	@Test
	void nullAccess() {
		testInput("?.", L.NULL_ACCESS);
	}

	@Test
	void qmark() {
		testInput("?", L.QMARK);
	}

	@Test
	void rarrow() {
		testInput("->", L.RARROW);
	}

	@Test
	void colon() {
		testInput(":", L.COLON);
	}

	@Test
	void exclaim() {
		testInput("!", L.EXCLAIM);
	}

	@Test
	void comma() {
		testInput(",", L.COMMA);
	}

	@Test
	void comment() {
		testInput("#", L.COMMENT);
		testInput("# this is quite a long comment", L.COMMENT);
	}

	@Test
	void dot() {
		testInput(".", L.DOT);
	}

	@Test
	void equal() {
		testInput("=", L.EQUAL);
	}

	@Test
	void plus() {
		testInput("+", L.PLUS);
	}

	@Test
	void minus() {
		testInput("-", L.MINUS);
	}

	@Test
	void star() {
		testInput("*", L.STAR);
	}

	@Test
	void slash() {
		testInput("/", L.SLASH);
	}

	@Test
	void percent() {
		testInput("%", L.PERCENT);
	}

	@Test
	void lesserThan() {
		testInput("<", L.LESSER_THAN);
	}

	@Test
	void greaterThan() {
		testInput(">", L.GREATER_THAN);
	}

	@Test
	void pipe() {
		testInput("|", L.PIPE);
	}

	@Test
	void amp() {
		testInput("&", L.AMP);
	}

	@Test
	void carret() {
		testInput("^", L.CARRET);
	}

	@Test
	void tilde() {
		testInput("~", L.TILDE);
	}

	@Test
	void bitLshift() {
		testInput("<<", L.BIT_LSHIFT);
	}

	@Test
	void bitRshift() {
		testInput(">>", L.BIT_RSHIFT);
	}

	@Test
	void doubleQmark() {
		testInput("??", L.DOUBLE_QMARK);
	}

	@Test
	void doublePlus() {
		testInput("++", L.DOUBLE_PLUS);
	}

	@Test
	void doubleStar() {
		testInput("**", L.DOUBLE_STAR);
	}

	@Test
	void doubleSlash() {
		testInput("//", L.DOUBLE_SLASH);
	}

	@Test
	void doublePercent() {
		testInput("%%", L.DOUBLE_PERCENT);
	}

	@Test
	void doubleEqual() {
		testInput("==", L.DOUBLE_EQUAL);
	}

	@Test
	void dotEqual() {
		testInput(".=", L.DOT_EQUAL);
	}

	@Test
	void notEqual() {
		testInput("!=", L.NOT_EQUAL);
	}

	@Test
	void plusEq() {
		testInput("+=", L.PLUS_EQ);
	}

	@Test
	void minusEq() {
		testInput("-=", L.MINUS_EQ);
	}

	@Test
	void starEq() {
		testInput("*=", L.STAR_EQ);
	}

	@Test
	void doubleStarEq() {
		testInput("**=", L.DOUBLE_STAR_EQ);
	}

	@Test
	void slashEq() {
		testInput("/=", L.SLASH_EQ);
	}

	@Test
	void doubleSlashEq() {
		testInput("//=", L.DOUBLE_SLASH_EQ);
	}

	@Test
	void percentEq() {
		testInput("%=", L.PERCENT_EQ);
	}

	@Test
	void doublePercentEq() {
		testInput("%%=", L.DOUBLE_PERCENT_EQ);
	}

	@Test
	void lesserThanEq() {
		testInput("<=", L.LESSER_OR_EQ);
	}

	@Test
	void greaterThanEq() {
		testInput(">=", L.GREATER_OR_EQ);
	}

	@Test
	void pipeEq() {
		testInput("|=", L.PIPE_EQ);
	}

	@Test
	void ampEq() {
		testInput("&=", L.AMP_EQ);
	}

	@Test
	void tildeEq() {
		testInput("~=", L.TILDE_EQ);
	}

	@Test
	void carretEq() {
		testInput("^=", L.CARRET_EQ);
	}

	@Test
	void funcPipe() {
		testInput("|>", L.FUNC_PIPE);
	}

	@Test
	void bitLshiftEq() {
		testInput("<<=", L.BIT_LSHIFT_EQ);
	}

	@Test
	void bitRshiftEq() {
		testInput(">>=", L.BIT_RSHIFT_EQ);
	}

	@Test
	void doublePlusEq() {
		testInput("++=", L.DOUBLE_PLUS_EQ);
	}

	@Test
	void doubleQmarkEq() {
		testInput("??=", L.DOUBLE_QMARK_EQ);
	}
}
