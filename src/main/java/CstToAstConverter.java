import java.util.*;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;


// public final class CstToAstConverter extends AbstractParseTreeVisitor<Ast.Node> implements AntlerScriptParserVisitor<Ast.Node> {
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

			// Payload can now only be RuleContext
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
		return new Ast.ContinueStatement(getTokens(ctx));
	}

	@Override
	public Ast.Expression visitExpression(AntlerScriptParser.ExpressionContext ctx) {
		// TODO, placeholder
		return new Ast.IndexExpression(null, null, null);
	}

	@Override
	public Ast.StatementBlock visitStatement_block(AntlerScriptParser.Statement_blockContext ctx) {
		// TODO, placeholder
		return new Ast.StatementBlock(null, null, false);
	}

	@Override
	public Ast.WhileStatement visitWhileStatement(AntlerScriptParser.WhileStatementContext ctx) {
		return visitWhile(ctx.while_());
	}

	@Override
	public Ast.WhileStatement visitWhile(AntlerScriptParser.WhileContext ctx) {
		Ast.Expression test = visitExpression(ctx.test);
		Ast.StatementBlock block = visitStatement_block(ctx.block);
		return new Ast.WhileStatement(getTokens(ctx), test, block);
	}

	@Override
	public Ast.IterateStatement visitIterateStatement(AntlerScriptParser.IterateStatementContext ctx) {
		return visitIterate(ctx.iterate());
	}

	@Override
	public Ast.IterateStatement visitIterate(AntlerScriptParser.IterateContext ctx) {
		Ast.Expression iterable = visitExpression(ctx.iterable);
		Ast.StatementBlock block = visitStatement_block(ctx.block);
		String indexSymbol = ctx.index.getText();
		String elementSymbol = ctx.element.getText();
		return new Ast.IterateStatement(getTokens(ctx), iterable, indexSymbol, elementSymbol, block);
	}

	@Override
	public Ast.Type visitType(AntlerScriptParser.TypeContext ctx) {
		// TODO, placeholder
		return new Ast.SymbolType(null, null);
	}

	@Override
	public Ast.Typedef visitTypedefStatement(AntlerScriptParser.TypedefStatementContext ctx) {
		return visitTypedef(ctx.typedef());
	}

	@Override
	public Ast.Typedef visitTypedef(AntlerScriptParser.TypedefContext ctx) {
		Ast.Type type = visitType(ctx.type());
		return new Ast.Typedef(getTokens(ctx), ctx.symbol().getText(), type);
	}

	@Override
	public Ast.IfStatement visitIf(AntlerScriptParser.IfContext ctx) {
		Ast.Expression test = visitExpression(ctx.test);
		Ast.StatementBlock thenBranch = visitStatement_block(ctx.block);
		Ast.StatementBlock elseBranch = visitElse(ctx.else_());

		List<Ast.ElifBranch> elifBranches = new ArrayList<>();
		for (AntlerScriptParser.ElifContext elif : ctx.elif()) {
			elifBranches.add(visitElif(elif));
		}

		return new Ast.IfStatement(getTokens(ctx), test, thenBranch, elifBranches, elseBranch);
	}

	@Override
	public Ast.ElifBranch visitElif(AntlerScriptParser.ElifContext ctx) {
		Ast.Expression test = visitExpression(ctx.test);
		Ast.StatementBlock block = visitStatement_block(ctx.block);
		return new Ast.ElifBranch(getTokens(ctx), test, block);
	}

	@Override
	public Ast.IfStatement visitIfStatement(AntlerScriptParser.IfStatementContext ctx) {
		return visitIf(ctx.if_());
	}

	@Override
	public Ast.SwitchStatement visitSwitch(AntlerScriptParser.SwitchContext ctx) {
		Ast.Expression test = visitExpression(ctx.test);

		List<Ast.CaseBranch> cases = new ArrayList<>();
		for (AntlerScriptParser.CaseContext case_ : ctx.case_()) {
			cases.add(visitCase(case_));
		}

		Ast.StatementBlock defaultCase = visitElse(ctx.catchAll);

		return new Ast.SwitchStatement(getTokens(ctx), test, cases, defaultCase);
	}

	@Override
	public Ast.StatementBlock visitElse(AntlerScriptParser.ElseContext ctx) {
		return visitStatement_block(ctx.block);
	}

	@Override
	public Ast.CaseBranch visitCase(AntlerScriptParser.CaseContext ctx) {
		Ast.StatementBlock body = visitStatement_block(ctx.block);

		List<Ast.Expression> values = new ArrayList<>();
		for (AntlerScriptParser.ExpressionContext expr : ctx.expression()) {
			values.add(visitExpression(expr));
		}

		return new Ast.CaseBranch(getTokens(ctx), values, body);
	}
}
