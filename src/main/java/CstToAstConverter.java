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

	// === MISC ===

	// symbol

	// SEMICOLON

	// SEMICOLON-newline

	// SEMICOLON-semicolon

	// === FILES ===

	// PROGRAM

	// PROGRAM-main_program

	// PROGRAM-class_program

	// PROGRAM-namespace_program

	// PROGRAM-implicit_namespace_program

	// other_directive

	// namespace_directive

	// classname_directive

	// main_directive

	// main_program

	// class_program

	// namespace_program

	// implicit_namespace_program

	// namespace_member

	// === CLASSES ===

	// class_top_level

	// class_header_inside

	// class_extends

	// class_extends_access

	// constructor

	// constructor_params

	// constructor_params_elm

	// var_args

	// CLASS-MEMBER

	// CLASS-MEMBER-cast

	// CLASS-MEMBER-declaration

	// CLASS-MEMBER-operator_overload

	// CLASS-MEMBER-constructor

	// CLASS-MEMBER-capture

	// CLASS-MEMBER-extends_assign

	// cast

	// operator_overload

	// overridable

	// capture

	// extends_assign

	// === ENUMS ===

	// enum_header_inside

	// === TYPES ===

	@Override
	public Ast.Type visitType(AntlerScriptParser.TypeContext ctx) {
		// TODO, placeholder
		return new Ast.SymbolType(null, null);
	}

	// type_or

	// type_and

	// TYPE-ATOMIC

	// TYPE-ATOMIC-symbol

	// TYPE-ATOMIC-list_header

	// TYPE-ATOMIC-array_header

	// TYPE-ATOMIC-map_header

	// TYPE-ATOMIC-class_header

	// TYPE-ATOMIC-enum_header

	// TYPE-ATOMIC-func_header

	// TYPE-ATOMIC-SELF_CLASS

	// TYPE-ATOMIC-'(' type ')'

	// list_header

	// array_header

	// map_header

	// func_header

	// func_params

	// func_param_elm

	// lambda

	// class_header

	// enum_header

	// === EXPRESSIONS ===

	@Override
	public Ast.Expression visitExpression(AntlerScriptParser.ExpressionContext ctx) {
		// TODO, placeholder
		return new Ast.IndexExpression(null, null, null);
	}

	// expression_assignment

	// expression_assignment_right

	// expression_logical_or

	// expression_logical_or_right

	// expression_logical_and

	// expression_logical_and_right

	// expression_logical_not

	// expression_cmp

	// expression_cmp_right

	// expression_bit_or

	// expression_bit_or_right

	// expression_bit_xor

	// expression_bit_xor_right

	// expression_bit_and

	// expression_bit_and_right

	// expression_bit_shift

	// expression_bit_shift_right

	// expression_add

	// expression_add_right

	// expression_mult

	// expression_mult_right

	// expression_unary

	// expression_unary_op

	// expression_exp

	// expression_exp_right

	// expression_postfix

	// expression_access

	// arguments

	// argument_elm

	// EXPRESSION-ATOM

	// EXPRESSION-ATOM-symbol

	// EXPRESSION-ATOM-string

	// EXPRESSION-ATOM-raw string

	// EXPRESSION-ATOM-float

	// EXPRESSION-ATOM-integer

	// EXPRESSION-ATOM-true

	// EXPRESSION-ATOM-false

	// EXPRESSION-ATOM-null

	// EXPRESSION-ATOM-super

	// EXPRESSION-ATOM-self instance

	// EXPRESSION-ATOM-new_object_instance

	// EXPRESSION-ATOM-new_list_instance

	// EXPRESSION-ATOM-new_array_instance

	// EXPRESSION-ATOM-new_map_instance

	// EXPRESSION-ATOM-new_class_instance

	// EXPRESSION-ATOM-lambda

	// EXPRESSION-ATOM-select

	// EXPRESSION-ATOM-'(' expression ')'

	// new_object_instance

	// new_list_instance

	// new_array_instance

	// new_class_instance

	// object_instantiation_args

	// new_map_instance

	// select

	// keypair_list

	// keypair_clause

	// === STATEMENT ===

	// STATEMENT-defer? expression

	@Override
	public Ast.BreakStatement visitBreakStatement(AntlerScriptParser.BreakStatementContext ctx) {
		return new Ast.BreakStatement(getTokens(ctx));
	}

	@Override
	public Ast.ContinueStatement visitContinueStatement(AntlerScriptParser.ContinueStatementContext ctx) {
		return new Ast.ContinueStatement(getTokens(ctx));
	}

	@Override
	public Ast.ReturnStatement visitReturnStatement(AntlerScriptParser.ReturnStatementContext ctx) {
		Ast.Expression expression = visitExpression(ctx.expression());
		return new Ast.ReturnStatement(getTokens(ctx), expression);
	}

	@Override
	public Ast.LoopStatement visitLoopStatement(AntlerScriptParser.LoopStatementContext ctx) {
		return visitLoop(ctx.loop());
	}

	@Override
	public Ast.WhileStatement visitWhileStatement(AntlerScriptParser.WhileStatementContext ctx) {
		return visitWhile(ctx.while_());
	}

	@Override
	public Ast.IterateStatement visitIterateStatement(AntlerScriptParser.IterateStatementContext ctx) {
		return visitIterate(ctx.iterate());
	}

	// STATEMENT-declaration

	@Override
	public Ast.Typedef visitTypedefStatement(AntlerScriptParser.TypedefStatementContext ctx) {
		return visitTypedef(ctx.typedef());
	}

	@Override
	public Ast.IfStatement visitIfStatement(AntlerScriptParser.IfStatementContext ctx) {
		return visitIf(ctx.if_());
	}

	// STATEMENT-switch

	// STATEMENT-defer? statement_block

	@Override
	public Ast.StatementBlock visitStatement_block(AntlerScriptParser.Statement_blockContext ctx) {
		// TODO, placeholder
		return new Ast.StatementBlock(null, null, false);
	}

	@Override
	public Ast.LoopStatement visitLoop(AntlerScriptParser.LoopContext ctx) {
		Ast.Expression from = visitExpression(ctx.from);
		Ast.Expression to = visitExpression(ctx.to);
		Ast.Expression by = visitExpression(ctx.by);
		String iterationSymbol = ctx.iterator.getText();
		Ast.StatementBlock block = visitStatement_block(ctx.block);
		return new Ast.LoopStatement(getTokens(ctx), from, to, by, iterationSymbol, block);
	}

	@Override
	public Ast.WhileStatement visitWhile(AntlerScriptParser.WhileContext ctx) {
		Ast.Expression test = visitExpression(ctx.test);
		Ast.StatementBlock block = visitStatement_block(ctx.block);
		return new Ast.WhileStatement(getTokens(ctx), test, block);
	}

	@Override
	public Ast.IterateStatement visitIterate(AntlerScriptParser.IterateContext ctx) {
		Ast.Expression iterable = visitExpression(ctx.iterable);
		Ast.StatementBlock block = visitStatement_block(ctx.block);
		String indexSymbol = ctx.index.getText();
		String elementSymbol = ctx.element.getText();
		return new Ast.IterateStatement(getTokens(ctx), iterable, indexSymbol, elementSymbol, block);
	}

	// declaration

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
	public Ast.StatementBlock visitElse(AntlerScriptParser.ElseContext ctx) {
		return visitStatement_block(ctx.block);
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
	public Ast.CaseBranch visitCase(AntlerScriptParser.CaseContext ctx) {
		Ast.StatementBlock body = visitStatement_block(ctx.block);

		List<Ast.Expression> values = new ArrayList<>();
		for (AntlerScriptParser.ExpressionContext expr : ctx.expression()) {
			values.add(visitExpression(expr));
		}

		return new Ast.CaseBranch(getTokens(ctx), values, body);
	}
}
