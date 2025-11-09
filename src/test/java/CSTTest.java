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
			assertEquals(parser.getNumberOfSyntaxErrors(), 0);
			assertNull(context.exception);
		});
	}

	@Test
	void class_extends_access() {
		testInput("myObj", "class_extends_access");
		testInput("myObj.myVar", "class_extends_access");
		testInput("myObj.myVar0.myVar1.myVar2.myVar3.myVar4.myVar5.myVar6.myVar7.myVar8.myVar9.myVar10.myVar11.myVar12.myVar13.myVar14.myVar15.myVar16.myVar17.myVar18.myVar19.myVar20.myVar21.myVar22.myVar23.myVar24.myVar25.myVar26.myVar27.myVar28.myVar29.myVar30.myVar31.myVar32.myVar33.myVar34.myVar35.myVar36.myVar37.myVar38.myVar39.myVar40.myVar41.myVar42.myVar43.myVar44.myVar45.myVar46.myVar47.myVar48.myVar49.myVar50.myVar51.myVar52.myVar53.myVar54.myVar55", "class_extends_access");
	}
}
