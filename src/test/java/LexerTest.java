import org.antlr.v4.runtime.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

// TODO: add comprehensive negative tests

@DisplayName("Testing the ANTLR4-generated lexer")
class LexerTest {
	// Type alias class
	abstract class L extends AntlerScriptLexer {
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
		testInput("$lmao", L.DIRECTIVE);
	}
	
	@Test
	void annotation() {
		testInput("@test", L.ANNOTATION);
	}
	
	@Test
	void integerDec() {
		testInput("123", L.INTEGER);
		testInput("1_2_3", L.INTEGER);

		// Signed suffix
		testInput("123i", L.INTEGER);
		testInput("123i8", L.INTEGER);
		testInput("123i16", L.INTEGER);
		testInput("123i32", L.INTEGER);
		testInput("123i64", L.INTEGER);
		testInput("123i128", L.INTEGER);

		// Unsigned suffix
		testInput("123u", L.INTEGER);
		testInput("123u8", L.INTEGER);
		testInput("123u16", L.INTEGER);
		testInput("123u32", L.INTEGER);
		testInput("123u64", L.INTEGER);
		testInput("123u128", L.INTEGER);
	}
	
	@Test
	void integerHex() {
		testInput("0xFF", L.INTEGER);
		testInput("0xF_F", L.INTEGER);

		// Signed suffix
		testInput("0xFFi", L.INTEGER);
		testInput("0xFFi8", L.INTEGER);
		testInput("0xFFi16", L.INTEGER);
		testInput("0xFFi32", L.INTEGER);
		testInput("0xFFi64", L.INTEGER);
		testInput("0xFFi128", L.INTEGER);

		// Unsigned suffix
		testInput("0xFFu", L.INTEGER);
		testInput("0xFFu8", L.INTEGER);
		testInput("0xFFu16", L.INTEGER);
		testInput("0xFFu32", L.INTEGER);
		testInput("0xFFu64", L.INTEGER);
		testInput("0xFFu128", L.INTEGER);
	}
	
	@Test
	void integerBin() {
		testInput("0b101", L.INTEGER);
		testInput("0b1_0_1", L.INTEGER);

		// Signed suffix
		testInput("0b101i", L.INTEGER);
		testInput("0b101i8", L.INTEGER);
		testInput("0b101i16", L.INTEGER);
		testInput("0b101i32", L.INTEGER);
		testInput("0b101i64", L.INTEGER);
		testInput("0b101i128", L.INTEGER);

		// Unsigned suffix
		testInput("0b101u", L.INTEGER);
		testInput("0b101u8", L.INTEGER);
		testInput("0b101u16", L.INTEGER);
		testInput("0b101u32", L.INTEGER);
		testInput("0b101u64", L.INTEGER);
		testInput("0b101u128", L.INTEGER);
	}
	
	@Test
	void floatDec() {
		testInput("3.14", L.FLOAT);
		testInput("0_3.1_4", L.FLOAT);

		// Suffix
		testInput("3.14f", L.FLOAT);
		testInput("3.14f8", L.FLOAT);
		testInput("3.14f16", L.FLOAT);
		testInput("3.14f32", L.FLOAT);
		testInput("3.14f64", L.FLOAT);
		testInput("3.14f128", L.FLOAT);
	}

	@Test
	void floatDecExponent() {
		testInput("3e10", L.FLOAT);
		testInput("3e+10", L.FLOAT);
		testInput("3e-10", L.FLOAT);

		testInput("3.14e10", L.FLOAT);
		testInput("3.14e+10", L.FLOAT);
		testInput("3.14e-10", L.FLOAT);

		testInput("0_3.1_4e1_0", L.FLOAT);
		testInput("0_3.1_4e+1_0", L.FLOAT);
		testInput("0_3.1_4e-1_0", L.FLOAT);

		testInput("3.14e10f", L.FLOAT);
		testInput("3.14e10f8", L.FLOAT);
		testInput("3.14e10f16", L.FLOAT);
		testInput("3.14e10f32", L.FLOAT);
		testInput("3.14e10f64", L.FLOAT);
		testInput("3.14e10f128", L.FLOAT);

		testInput("3.14e+10f", L.FLOAT);
		testInput("3.14e+10f8", L.FLOAT);
		testInput("3.14e+10f16", L.FLOAT);
		testInput("3.14e+10f32", L.FLOAT);
		testInput("3.14e+10f64", L.FLOAT);
		testInput("3.14e+10f128", L.FLOAT);

		testInput("3.14e-10f", L.FLOAT);
		testInput("3.14e-10f8", L.FLOAT);
		testInput("3.14e-10f16", L.FLOAT);
		testInput("3.14e-10f32", L.FLOAT);
		testInput("3.14e-10f64", L.FLOAT);
		testInput("3.14e-10f128", L.FLOAT);
	}
	
	@Test
	void floatHex() {
		testInput("0xF.F", L.FLOAT);
		testInput("0x0_F.F_0", L.FLOAT);
		
		testInput("0xF.Ff", L.FLOAT);
		testInput("0xF.Ff8", L.FLOAT);
		testInput("0xF.Ff16", L.FLOAT);
		testInput("0xF.Ff32", L.FLOAT);
		testInput("0xF.Ff64", L.FLOAT);
		testInput("0xF.Ff128", L.FLOAT);
	}

	@Test
	void floatHexExponent() {
		testInput("0xFe10", L.FLOAT);
		testInput("0xFe+10", L.FLOAT);
		testInput("0xFe-10", L.FLOAT);

		testInput("0xF.Fe10", L.FLOAT);
		testInput("0xF.Fe10f", L.FLOAT);
		testInput("0xF.Fe10f8", L.FLOAT);
		testInput("0xF.Fe10f16", L.FLOAT);
		testInput("0xF.Fe10f32", L.FLOAT);
		testInput("0xF.Fe10f64", L.FLOAT);
		testInput("0xF.Fe10f128", L.FLOAT);

		testInput("0xF.Fe+10", L.FLOAT);
		testInput("0xF.Fe+10f", L.FLOAT);
		testInput("0xF.Fe+10f8", L.FLOAT);
		testInput("0xF.Fe+10f16", L.FLOAT);
		testInput("0xF.Fe+10f32", L.FLOAT);
		testInput("0xF.Fe+10f64", L.FLOAT);
		testInput("0xF.Fe+10f128", L.FLOAT);

		testInput("0xF.Fe-10", L.FLOAT);
		testInput("0xF.Fe-10f", L.FLOAT);
		testInput("0xF.Fe-10f8", L.FLOAT);
		testInput("0xF.Fe-10f16", L.FLOAT);
		testInput("0xF.Fe-10f32", L.FLOAT);
		testInput("0xF.Fe-10f64", L.FLOAT);
		testInput("0xF.Fe-10f128", L.FLOAT);

		testInput("0xF.Fp10", L.FLOAT);
		testInput("0xF.Fp10f8", L.FLOAT);
		testInput("0xF.Fp10f16", L.FLOAT);
		testInput("0xF.Fp10f32", L.FLOAT);
		testInput("0xF.Fp10f64", L.FLOAT);
		testInput("0xF.Fp10f128", L.FLOAT);

		testInput("0xF.Fp+10", L.FLOAT);
		testInput("0xF.Fp+10f", L.FLOAT);
		testInput("0xF.Fp+10f8", L.FLOAT);
		testInput("0xF.Fp+10f16", L.FLOAT);
		testInput("0xF.Fp+10f32", L.FLOAT);
		testInput("0xF.Fp+10f64", L.FLOAT);
		testInput("0xF.Fp+10f128", L.FLOAT);

		testInput("0xF.Fp-10", L.FLOAT);
		testInput("0xF.Fp-10f", L.FLOAT);
		testInput("0xF.Fp-10f8", L.FLOAT);
		testInput("0xF.Fp-10f16", L.FLOAT);
		testInput("0xF.Fp-10f32", L.FLOAT);
		testInput("0xF.Fp-10f64", L.FLOAT);
		testInput("0xF.Fp-10f128", L.FLOAT);
	}
	
	@Test
	void rawString() {
		testInput("\\\"hello\"", L.RAW_STRING);
		testInput("\\\"hello\\\"", L.RAW_STRING);
		testInput("\\\"he\n\nllo\"", L.RAW_STRING);
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
	void capture() {
		testInput("capture", L.CAPTURE);
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
	void iterate() {
		testInput("iterate", L.ITERATE);
	}
	
	@Test
	void let() {
		testInput("let", L.LET);
	}
	
	@Test
	void list() {
		testInput("List", L.LIST);
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
	void privateConstructor() {
		testInput("__constructor", L.PRIVATE_CONSTRUCTOR);
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
	void trueKw() {
		testInput("true", L.TRUE);
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
		testInput("<=", L.LESSER_THAN_EQ);
	}
	
	@Test
	void greaterThanEq() {
		testInput(">=", L.GREATER_THAN_EQ);
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
