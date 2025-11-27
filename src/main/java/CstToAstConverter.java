import java.util.*;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;


// public final class CstToAstConverter extends AbstractParseTreeVisitor<Ast.Node> implements AntlerScriptParserVisitor<Ast.Node> {
public final class CstToAstConverter extends AntlerScriptParserBaseVisitor<Object> {
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

	@Override
	public List<Ast.ClassExtendsAccess> visitClass_extends(AntlerScriptParser.Class_extendsContext ctx) {
		return ctx.class_extends_access().stream().map(this::visitClass_extends_access).toList();
	}

	@Override
	public Ast.ClassExtendsAccess visitClass_extends_access(AntlerScriptParser.Class_extends_accessContext ctx) {
		return new Ast.ClassExtendsAccess(ctx.symbol().stream().map(RuleContext::getText).toList());
	}

	@Override
	public Ast.ConstructorClassMember visitConstructor(AntlerScriptParser.ConstructorContext ctx) {
		return new Ast.ConstructorClassMember(
			getTokens(ctx),
			visitConstructor_params(ctx.constructor_params()),
			visitStatement_block(ctx.statement_block())
		);
	}

	@Override
	public List<Ast.ConstructorParameter> visitConstructor_params(AntlerScriptParser.Constructor_paramsContext ctx) {
		List<Ast.ConstructorParameter> params = new ArrayList<>(ctx.constructor_params_elm().stream().map(this::visitConstructor_params_elm).toList());
		if (ctx.var_args() != null) {
			params.add(visitVar_args(ctx.var_args()));
		}
		return params;
	}

	@Override
	public Ast.ConstructorParameter visitConstructor_params_elm(AntlerScriptParser.Constructor_params_elmContext ctx) {
		return new Ast.ConstructorParameter(visitType(ctx.type()), ctx.symbol().getText(), visitExpression(ctx.expression()), false);
	}

	@Override
	public Ast.ConstructorParameter visitVar_args(AntlerScriptParser.Var_argsContext ctx) {
		return new Ast.ConstructorParameter(visitType(ctx.type()), ctx.symbol().getText(), null, true);
	}

	@Override
	public Ast.CastClassMember visitCastClassMember(AntlerScriptParser.CastClassMemberContext ctx) {
		return visitCast(ctx.cast());
	}

	@Override
	public Ast.DeclarationClassMember visitDeclarationClassMember(AntlerScriptParser.DeclarationClassMemberContext ctx) {
		return new Ast.DeclarationClassMember(getTokens(ctx), switch (ctx.declaration()) {
			case AntlerScriptParser.LetDefinitionContext def -> visitLetDefinition(def);
			case AntlerScriptParser.LetDeclarationContext decl -> visitLetDeclaration(decl);
			case AntlerScriptParser.ConstDefinitionContext decl -> visitConstDefinition(decl);
			default -> null;
		});
	}

	// CLASS-MEMBER-declaration

	// CLASS-MEMBER-operator_overload

	// CLASS-MEMBER-constructor

	// CLASS-MEMBER-capture

	// CLASS-MEMBER-extends_assign

	@Override
	public Ast.CastClassMember visitCast(AntlerScriptParser.CastContext ctx) {
		return new Ast.CastClassMember(getTokens(ctx), visitType(ctx.type()), visitStatement_block(ctx.statement_block()));
	}

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

	// TYPE-ATOMIC-symbol

	@Override
	public Ast.ListType visitListType(AntlerScriptParser.ListTypeContext ctx) {
		return visitList_header(ctx.list_header());
	}

	// TYPE-ATOMIC-array_header

	@Override
	public Ast.MapType visitMapType(AntlerScriptParser.MapTypeContext ctx) {
		return visitMap_header(ctx.map_header());
	}

	// TYPE-ATOMIC-class_header

	// TYPE-ATOMIC-enum_header

	// TYPE-ATOMIC-func_header

	// TYPE-ATOMIC-SELF_CLASS

	// TYPE-ATOMIC-'(' type ')'

	@Override
	public Ast.ListType visitList_header(AntlerScriptParser.List_headerContext ctx) {
		Ast.Type type = visitType(ctx.type());
		return new Ast.ListType(getTokens(ctx), type);
	}

	// array_header

	@Override
	public Ast.MapType visitMap_header(AntlerScriptParser.Map_headerContext ctx) {
		Ast.Type first = visitType(ctx.type(0));
		Ast.Type second = visitType(ctx.type(1));
		return new Ast.MapType(getTokens(ctx), first, second);
	}

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

	// indexAccess

	// functionCall

	// memberAccess

	// nullishAccess

	@Override
	public List<Ast.Argument> visitArguments(AntlerScriptParser.ArgumentsContext ctx) {
		return ctx.argument_elm().stream().map((arg_elm_ctx) -> switch (arg_elm_ctx) {
			case AntlerScriptParser.DiscardArgumentContext disc -> visitDiscardArgument(disc);
			case AntlerScriptParser.ExpressionArgumentContext expr -> visitExpressionArgument(expr);
			default -> null;
		}).toList();
	}

	@Override
	public Ast.Argument visitDiscardArgument(AntlerScriptParser.DiscardArgumentContext _ctx) {
		return new Ast.Argument(null, null, true);
	}

	@Override
	public Ast.Argument visitExpressionArgument(AntlerScriptParser.ExpressionArgumentContext ctx) {
		return new Ast.Argument(visitExpression(ctx.expression()), ctx.symbol().getText(), false);
	}

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

	@Override
	public Ast.NewListExpression visitNewListExpression(AntlerScriptParser.NewListExpressionContext ctx) {
		return visitNew_list_instance(ctx.new_list_instance());
	}

	// EXPRESSION-ATOM-new_array_instance

	@Override
	public Ast.NewMapExpression visitNewMapExpression(AntlerScriptParser.NewMapExpressionContext ctx) {
		return visitNew_map_instance(ctx.new_map_instance());
	}

	// EXPRESSION-ATOM-new_class_instance

	// EXPRESSION-ATOM-lambda

	// EXPRESSION-ATOM-select

	// EXPRESSION-ATOM-'(' expression ')'

	// new_object_instance

	@Override
	public Ast.NewListExpression visitNew_list_instance(AntlerScriptParser.New_list_instanceContext ctx) {
		Ast.ListType type = visitList_header(ctx.list_header());
		List<Ast.Argument> args = visitObject_instantiation_args(ctx.object_instantiation_args());
		return new Ast.NewListExpression(getTokens(ctx), type, args);
	}

	// new_array_instance

	// new_class_instance

	@Override
	public List<Ast.Argument> visitObject_instantiation_args(AntlerScriptParser.Object_instantiation_argsContext ctx) {
		return visitArguments(ctx.arguments());
	}

	@Override
	public Ast.NewMapExpression visitNew_map_instance(AntlerScriptParser.New_map_instanceContext ctx) {
		Ast.MapType type = visitMap_header(ctx.map_header());
		List<Ast.KeyValuePair> pairs = visitKeypair_list(ctx.keypair_list());
		return new Ast.NewMapExpression(getTokens(ctx), type, pairs);
	}

	@Override
	public Ast.SelectExpression visitSelect(AntlerScriptParser.SelectContext ctx) {
		return new Ast.SelectExpression(getTokens(ctx), visitExpression(ctx.expression()), visitKeypair_list(ctx.keypair_list()));
	}

	@Override
	public List<Ast.KeyValuePair> visitKeypair_list(AntlerScriptParser.Keypair_listContext ctx) {
		return ctx.keypair_clause().stream().map(this::visitKeypair_clause).toList();
	}

	@Override
	public Ast.KeyValuePair visitKeypair_clause(AntlerScriptParser.Keypair_clauseContext ctx) {
		var expression = ctx.expression();
		return new Ast.KeyValuePair(visitExpression(expression.getFirst()), visitExpression(expression.getLast()));
	}

	// === STATEMENT ===

	@Override
	public Ast.ExpressionStatement visitExpressionStatement(AntlerScriptParser.ExpressionStatementContext ctx) {
		return new Ast.ExpressionStatement(getTokens(ctx), visitExpression(ctx.expression()), ctx.DEFER() != null);
	}

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

	@Override
	public Ast.VariableDeclaration visitDeclarationStatement(AntlerScriptParser.DeclarationStatementContext ctx) {
		return switch (ctx.declaration()) {
			case AntlerScriptParser.LetDefinitionContext def -> visitLetDefinition(def);
			case AntlerScriptParser.LetDeclarationContext decl -> visitLetDeclaration(decl);
			case AntlerScriptParser.ConstDefinitionContext decl -> visitConstDefinition(decl);
			default -> null;
		};
	}

	@Override
	public Ast.Typedef visitTypedefStatement(AntlerScriptParser.TypedefStatementContext ctx) {
		return visitTypedef(ctx.typedef());
	}

	@Override
	public Ast.IfStatement visitIfStatement(AntlerScriptParser.IfStatementContext ctx) {
		return visitIf(ctx.if_());
	}

	@Override
	public Ast.SwitchStatement visitSwitchStatement(AntlerScriptParser.SwitchStatementContext ctx) {
		return visitSwitch(ctx.switch_());
	}

	@Override
	public Ast.StatementBlock visitStatementBlockStatement(AntlerScriptParser.StatementBlockStatementContext ctx) {
		return new Ast.StatementBlock(getTokens(ctx), ctx.statement_block().statement().stream().map(this::visitStatement).toList(), ctx.DEFER() != null);
	}

	public Ast.Statement visitStatement(AntlerScriptParser.StatementContext ctx) {
		return switch (ctx) {
			case AntlerScriptParser.ExpressionStatementContext stmt -> visitExpressionStatement(stmt);
			case AntlerScriptParser.BreakStatementContext stmt -> visitBreakStatement(stmt);
			case AntlerScriptParser.ContinueStatementContext stmt -> visitContinueStatement(stmt);
			case AntlerScriptParser.ReturnStatementContext stmt -> visitReturnStatement(stmt);
			case AntlerScriptParser.LoopStatementContext stmt -> visitLoopStatement(stmt);
			case AntlerScriptParser.WhileStatementContext stmt -> visitWhileStatement(stmt);
			case AntlerScriptParser.IterateStatementContext stmt -> visitIterateStatement(stmt);
			case AntlerScriptParser.DeclarationStatementContext stmt -> visitDeclarationStatement(stmt);
			case AntlerScriptParser.TypedefStatementContext stmt -> visitTypedefStatement(stmt);
			case AntlerScriptParser.IfStatementContext stmt -> visitIfStatement(stmt);
			case AntlerScriptParser.SwitchStatementContext stmt -> visitSwitchStatement(stmt);
			case AntlerScriptParser.StatementBlockStatementContext stmt -> visitStatementBlockStatement(stmt);
		default -> null;
		};
	}

	@Override
	public Ast.StatementBlock visitStatement_block(AntlerScriptParser.Statement_blockContext ctx) {
		return new Ast.StatementBlock(getTokens(ctx), ctx.statement().stream().map(this::visitStatement).toList(), false);
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

	@Override
	public Ast.VariableDeclaration visitLetDeclaration(AntlerScriptParser.LetDeclarationContext ctx) {
		return new Ast.VariableDeclaration(
			getTokens(ctx),
			false,
			ctx.isMutable != null,
			visitType(ctx.type()),
			ctx.variableName.getText(),
			null
		);
	}

	public Ast.VariableDeclaration visitLetDefinition(AntlerScriptParser.LetDefinitionContext ctx) {
		var type = ctx.type();
		return new Ast.VariableDeclaration(
			getTokens(ctx),
			false,
			ctx.isMutable != null,
			type != null ? visitType(type) : null,
			ctx.variableName.getText(),
			visitExpression(ctx.expression())
		);
	}

	@Override
	public Ast.VariableDeclaration visitConstDefinition(AntlerScriptParser.ConstDefinitionContext ctx) {
		var type = ctx.type();
		return new Ast.VariableDeclaration(
			getTokens(ctx),
			true,
			false,
			type != null ? visitType(type) : null,
			ctx.variableName.getText(),
			visitExpression(ctx.expression())
		);
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
