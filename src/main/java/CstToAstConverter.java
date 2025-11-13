import java.util.*;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;


public final class CstToAstConverter extends AntlerScriptParserBaseVisitor<Ast.Node> {
	private static List<Token> getTokens(ParserRuleContext ctx) {
		final List<Token> tokens = new ArrayList<>();
		getTokensInternal(ctx.children, tokens);
		return tokens;
	}

	private static void getTokensInternal(List<ParseTree> parseTrees, List<Token> out) {
		for (ParseTree pt : parseTrees) {
			Object pl = pt.getPayload();
			
			if (pl instanceof Token token) {
				out.add(token);
				return;
			}

			RuleContext rule = (RuleContext)pl;

			List<ParseTree> parseTreesChild = new ArrayList<>();
			for (int i = 0; i < rule.getChildCount(); i++) {
				parseTreesChild.add(rule.getChild(i));
			}

			getTokensInternal(parseTreesChild, out);
		}
	}

	@Override
	public Ast.Node visitContinueStatement(AntlerScriptParser.ContinueStatementContext ctx) {
		var stmt = new Ast.ContinueStatement(getTokens(ctx));
		return stmt;
	}
}
