import java.util.*;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;


// public final class CstToAstConverter extends AbstractParseTreeVisitor<Ast.Node> implements
// AntlerScriptParserVisitor<Ast.Node> {
public final class CstToAstConverter extends AntlerScriptParserBaseVisitor<Object> {
	private static List<Token> getTokens(ParserRuleContext ctx) {
		final List<Token> tokens = new ArrayList<>();
		getTokensInternal(ctx.children, tokens);
		return tokens;
	}

	private static double parseFloat(String floatString) {
		assert !floatString.contains("f");

		floatString = floatString.replace("_", "");

		if (floatString.startsWith("0x")) {
			floatString = floatString.replace("e", "p");
			if (!floatString.contains("p")) {
				floatString = floatString + "p0";
			}
		}

		return Double.parseDouble(floatString);
	}

	private static long parseInt(String intString) {
		assert !intString.contains("i");
		assert !intString.contains("u");

		intString = intString.replace("_", "");

		Long result = null;

		if (intString.startsWith("0x")) {
			intString = intString.substring(2);
			result = Long.parseLong(intString, 16);
		} else if (intString.startsWith("0b")) {
			intString = intString.substring(2);
			result = Long.parseLong(intString, 2);
		} else {
			result = Long.parseLong(intString, 10);
		}

		return result;
	}

	private static void getTokensInternal(List<ParseTree> parseTrees, List<Token> out) {
		for (ParseTree pt : parseTrees) {
			Object pl = pt.getPayload();

			if (pl instanceof Token token) {
				out.add(token);
				return;
			}

			// Payload can now only be RuleContext
			assert pl instanceof RuleContext;

			RuleContext rule = (RuleContext) pl;

			List<ParseTree> parseTreesChild = new ArrayList<>();
			for (int i = 0; i < rule.getChildCount(); i++) {
				parseTreesChild.add(rule.getChild(i));
			}

			getTokensInternal(parseTreesChild, out);
		}
	}

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

	// Helper, not an override
	public Ast.ClassMember visitClassMember(AntlerScriptParser.Class_memberContext ctx) {
		return (Ast.ClassMember) visit(ctx);
	}

	// class_top_level

	@Override
	public Ast.ClassType visitClass_header_inside(AntlerScriptParser.Class_header_insideContext ctx) {
		// TODO: not that
		return null;
	}

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
			getTokens(ctx), visitConstructor_params(ctx.constructor_params()), visitStatement_block(ctx.statement_block())
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
		return new Ast.DeclarationClassMember(getTokens(ctx), visitDeclaration(ctx.declaration()));
	}

	@Override
	public Ast.OperatorOverloadClassMember visitOperatorOverloadClassMember(AntlerScriptParser.OperatorOverloadClassMemberContext ctx) {
		return visitOperator_overload(ctx.operator_overload());
	}

	@Override
	public Ast.ConstructorClassMember visitConstructorClassMember(AntlerScriptParser.ConstructorClassMemberContext ctx) {
		return visitConstructor(ctx.constructor());
	}

	@Override
	public Ast.CaptureClassMember visitCaptureClassMember(AntlerScriptParser.CaptureClassMemberContext ctx) {
		return visitCapture(ctx.capture());
	}

	@Override
	public Ast.ExtendsAssignClassMember visitExtendsClassMember(AntlerScriptParser.ExtendsClassMemberContext ctx) {
		return visitExtends_assign(ctx.extends_assign());
	}

	@Override
	public Ast.CastClassMember visitCast(AntlerScriptParser.CastContext ctx) {
		return new Ast.CastClassMember(getTokens(ctx), visitType(ctx.type()), visitStatement_block(ctx.statement_block()));
	}

	@Override
	public Ast.OperatorOverloadClassMember visitOperator_overload(AntlerScriptParser.Operator_overloadContext ctx) {
		return new Ast.OperatorOverloadClassMember(
			getTokens(ctx),
			visitOverridable(ctx.overridable()),
			visitType(ctx.rightType),
			ctx.symbol().getText(),
			visitType(ctx.returnType),
			visitStatement_block(ctx.statement_block())
		);
	}

	@Override
	public Ast.OperatorOverloadClassMember.Kind visitOverridable(AntlerScriptParser.OverridableContext ctx) {
		Ast.OperatorOverloadClassMember.Kind result = switch(ctx.operator.getType()) {
		case AntlerScriptParser.PLUS -> Ast.OperatorOverloadClassMember.Kind.PLUS;
		case AntlerScriptParser.MINUS -> Ast.OperatorOverloadClassMember.Kind.MINUS;
		case AntlerScriptParser.STAR -> Ast.OperatorOverloadClassMember.Kind.MULTIPLY;
		case AntlerScriptParser.SLASH -> Ast.OperatorOverloadClassMember.Kind.DIVIDE;
		case AntlerScriptParser.PERCENT -> Ast.OperatorOverloadClassMember.Kind.REMAINDER;
		case AntlerScriptParser.LESSER_THAN -> Ast.OperatorOverloadClassMember.Kind.LESSER_THAN;
		case AntlerScriptParser.GREATER_THAN -> Ast.OperatorOverloadClassMember.Kind.GREATER_THAN;
		case AntlerScriptParser.DOUBLE_PLUS -> Ast.OperatorOverloadClassMember.Kind.CONCAT;
		case AntlerScriptParser.DOUBLE_STAR -> Ast.OperatorOverloadClassMember.Kind.EXPONENT;
		case AntlerScriptParser.DOUBLE_SLASH -> Ast.OperatorOverloadClassMember.Kind.FLOOR_DIVIDE;
		case AntlerScriptParser.DOUBLE_PERCENT -> Ast.OperatorOverloadClassMember.Kind.MODULO;
		case AntlerScriptParser.DOUBLE_EQUAL -> Ast.OperatorOverloadClassMember.Kind.EQUAL;
		case AntlerScriptParser.LBRACK -> Ast.OperatorOverloadClassMember.Kind.INDEX;
		default -> null;
		};

		assert result != null;

		return result;
	}

	@Override
	public Ast.CaptureClassMember visitCapture(AntlerScriptParser.CaptureContext ctx) {
		String target = ctx.target == null ? null : ctx.target.getText();
		var extendsAssign = ctx.extends_assign() == null ? null : visitExtends_assign(ctx.extends_assign());
		return new Ast.CaptureClassMember(getTokens(ctx), visitClass_extends_access(ctx.class_extends_access()), ctx.origin.getText(), target, extendsAssign);
	}

	@Override
	public Ast.ExtendsAssignClassMember visitExtends_assign(AntlerScriptParser.Extends_assignContext ctx) {
		return new Ast.ExtendsAssignClassMember(getTokens(ctx), ctx.symbol().getText(), visitExpression(ctx.expression()));
	}

	// === ENUMS ===

	@Override
	public Ast.EnumType visitEnum_header_inside(AntlerScriptParser.Enum_header_insideContext ctx) {
		Ast.ClassExtendsAccess extendsAccess = ctx.class_extends_access() == null
			? null
			: visitClass_extends_access(ctx.class_extends_access());
		List<String> memberSymbols = ctx.symbol().isEmpty()
			? null
			: ctx.symbol().stream().map(AntlerScriptParser.SymbolContext::getText).toList();

		return new Ast.EnumType(getTokens(ctx), extendsAccess, memberSymbols);
	}

	// === TYPES ===

	@Override
	public Ast.Type visitType(AntlerScriptParser.TypeContext ctx) {
		return visitType_or(ctx.type_or());
	}

	@Override
	public Ast.Type visitType_or(AntlerScriptParser.Type_orContext ctx) {
		if (ctx.right == null) {
			return visitType_and(ctx.left);
		}

		return new Ast.UnionType(getTokens(ctx), Ast.UnionType.Kind.OR, visitType_and(ctx.left), visitType_or(ctx.right.type_or()));
	}

	@Override
	public Ast.Type visitType_and(AntlerScriptParser.Type_andContext ctx) {
		if (ctx.right == null) {
			return visitTypeAtomic(ctx.left);
		}

		return new Ast.UnionType(getTokens(ctx), Ast.UnionType.Kind.AND, visitTypeAtomic(ctx.left), visitType_and(ctx.right.type_and()));
	}

	// Helper, not an override
	public Ast.Type visitTypeAtomic(AntlerScriptParser.Type_atomicContext ctx) {
		return (Ast.Type) visit(ctx);
	}

	@Override
	public Ast.SymbolType visitSymbolType(AntlerScriptParser.SymbolTypeContext ctx) {
		return new Ast.SymbolType(getTokens(ctx), ctx.symbol().getText());
	}

	@Override
	public Ast.ListType visitListType(AntlerScriptParser.ListTypeContext ctx) {
		return visitList_header(ctx.list_header());
	}

	@Override
	public Ast.ArrayType visitArrayType(AntlerScriptParser.ArrayTypeContext ctx) {
		return visitArray_header(ctx.array_header());
	}

	@Override
	public Ast.MapType visitMapType(AntlerScriptParser.MapTypeContext ctx) {
		return visitMap_header(ctx.map_header());
	}

	public Ast.ClassType visitClassType(AntlerScriptParser.ClassTypeContext ctx) {
		return visitClass_header(ctx.class_header());
	}

	@Override
	public Ast.EnumType visitEnumType(AntlerScriptParser.EnumTypeContext ctx) {
		return visitEnum_header(ctx.enum_header());
	}

	@Override
	public Ast.FunctionType visitFuncType(AntlerScriptParser.FuncTypeContext ctx) {
		return visitFunc_header(ctx.func_header());
	}

	@Override
	public Ast.SelfClassType visitSelfType(AntlerScriptParser.SelfTypeContext ctx) {
		return new Ast.SelfClassType(getTokens(ctx));
	}

	@Override
	public Ast.Type visitTypeGroup(AntlerScriptParser.TypeGroupContext ctx) {
		return visitType(ctx.type());
	}

	@Override
	public Ast.ListType visitList_header(AntlerScriptParser.List_headerContext ctx) {
		Ast.Type type = visitType(ctx.type());
		return new Ast.ListType(getTokens(ctx), type);
	}

	@Override
	public Ast.ArrayType visitArray_header(AntlerScriptParser.Array_headerContext ctx) {
		Ast.Type type = visitType(ctx.type());
		return new Ast.ArrayType(getTokens(ctx), type);
	}

	@Override
	public Ast.MapType visitMap_header(AntlerScriptParser.Map_headerContext ctx) {
		Ast.Type first = visitType(ctx.type(0));
		Ast.Type second = visitType(ctx.type(1));
		return new Ast.MapType(getTokens(ctx), first, second);
	}

	@Override
	public Ast.FunctionType visitFunc_header(AntlerScriptParser.Func_headerContext ctx) {
		Ast.Type returnType = ctx.type() == null ? null : visitType(ctx.type());
		List<Ast.FunctionParameter> params = ctx.func_params() == null ? null : visitFunc_params(ctx.func_params());
		return new Ast.FunctionType(getTokens(ctx), params, returnType);
	}

	@Override
	public List<Ast.FunctionParameter> visitFunc_params(AntlerScriptParser.Func_paramsContext ctx) {
		List<Ast.FunctionParameter> params = new ArrayList<>(ctx.func_param_elm().stream().map(this::visitFunc_param_elm).toList());

		if (ctx.var_args() != null) {
			params.add(new Ast.FunctionParameter(visitVar_args(ctx.var_args())));
		}

		return params;
	}

	@Override
	public Ast.FunctionParameter visitFunc_param_elm(AntlerScriptParser.Func_param_elmContext ctx) {
		Ast.Type type = visitType(ctx.type());
		String symbol = ctx.symbol().getText();
		Ast.Expression expr = ctx.expression() == null ? null : visitExpression(ctx.expression());
		return new Ast.FunctionParameter(type, symbol, expr, false);
	}

	@Override
	public Ast.LambdaExpression visitLambda(AntlerScriptParser.LambdaContext ctx) {
		Ast.FunctionType type = visitFunc_header(ctx.func_header());
		Ast.StatementBlock block = visitStatement_block(ctx.statement_block());
		return new Ast.LambdaExpression(getTokens(ctx), type, block);
	}

	@Override
	public Ast.ClassType visitClass_header(AntlerScriptParser.Class_headerContext ctx) {
		if (ctx.class_header_inside() == null) {
			return new Ast.ClassType(getTokens(ctx), null);
		}
		return visitClass_header_inside(ctx.class_header_inside());
	}

	@Override
	public Ast.EnumType visitEnum_header(AntlerScriptParser.Enum_headerContext ctx) {
		return visitEnum_header_inside(ctx.enum_header_inside());
	}

	// === EXPRESSIONS ===

	// Helper, not an override
	public Ast.Expression visitExpressionAtom(AntlerScriptParser.Expression_atomContext ctx) {
		return (Ast.Expression) visit(ctx);
	}

	@Override
	public Ast.Expression visitExpression(AntlerScriptParser.ExpressionContext ctx) {
		// TODO: not that
		return null;
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

	@Override
	public Ast.Expression visitExpression_unary(AntlerScriptParser.Expression_unaryContext ctx) {
		Ast.Expression latest = visitExpression_exp(ctx.expression_exp());

		var ops = ctx.expression_unary_op();
		var iter = ops.listIterator(ops.size());
		while (iter.hasPrevious()) {
			var opCtx = iter.previous();
			Token op = opCtx.operator;

			Ast.UnaryExpression.Kind kind = switch(op.getType()) {
			case AntlerScriptParser.PLUS -> Ast.UnaryExpression.Kind.PLUS;
			case AntlerScriptParser.MINUS -> Ast.UnaryExpression.Kind.MINUS;
			case AntlerScriptParser.TILDE -> Ast.UnaryExpression.Kind.BIT_NOT;
			default -> null;
			};

			assert kind != null;

			latest = new Ast.UnaryExpression(getTokens(opCtx), kind, latest);
		}

		return latest;
	}

	@Override
	public Object visitExpression_unary_op(AntlerScriptParser.Expression_unary_opContext ctx) {
		// Should be handled by the caller
		assert false;
		return null;
	}

	@Override
	public Ast.Expression visitExpression_exp(AntlerScriptParser.Expression_expContext ctx) {
		Ast.Expression latest = visitExpression_postfix(ctx.expression_postfix());

		for (AntlerScriptParser.Expression_exp_rightContext expCtx : ctx.expression_exp_right()) {
			latest = new Ast.BinaryExpression(getTokens(expCtx),
							  Ast.BinaryExpression.Kind.EXPONENT,
							  latest,
							  visitExpression_postfix(expCtx.expression_postfix()));
		}

		return latest;
	}

	@Override
	public Object visitExpression_exp_right(AntlerScriptParser.Expression_exp_rightContext ctx) {
		// Should be handled by the caller
		assert false;
		return null;
	}

	@Override
	public Ast.Expression visitExpression_postfix(AntlerScriptParser.Expression_postfixContext ctx) {
		Ast.Expression latest = visitExpressionAtom(ctx.expression_atom());

		for (AntlerScriptParser.Expression_accessContext accessCtx : ctx.expression_access()) {
			switch (accessCtx) {
			case AntlerScriptParser.IndexAccessContext idxCtx:
				latest = new Ast.IndexExpression(getTokens(idxCtx),
									latest,
									visitExpression(idxCtx.expression()));
				break;
			case AntlerScriptParser.FunctionCallContext funcallCtx:
				latest = new Ast.CallExpression(getTokens(funcallCtx),
								       latest,
								       funcallCtx.arguments() == null
								       ? null
								       : visitArguments(funcallCtx.arguments()));
				break;
			case AntlerScriptParser.MemberAccessContext memCtx:
				latest = new Ast.AccessExpression(getTokens(memCtx),
									 latest,
									 memCtx.symbol().getText(),
									 false);
				break;
			case AntlerScriptParser.NullishAccessContext nullCtx:
				latest = new Ast.AccessExpression(getTokens(nullCtx),
									 latest,
									 nullCtx.symbol().getText(),
									 true);
				break;
			default: assert false;
			}
		}

		return latest;
	}

	@Override
	public Object visitIndexAccess(AntlerScriptParser.IndexAccessContext ctx) {
		// Should be handled by the caller
		assert false;
		return null;
	}

	@Override
	public Object visitFunctionCall(AntlerScriptParser.FunctionCallContext ctx) {
		// Should be handled by the caller
		assert false;
		return null;
	}

	@Override
	public Object visitMemberAccess(AntlerScriptParser.MemberAccessContext ctx) {
		// Should be handled by the caller
		assert false;
		return null;
	}

	@Override
	public Object visitNullishAccess(AntlerScriptParser.NullishAccessContext ctx) {
		// Should be handled by the caller
		assert false;
		return null;
	}

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

	@Override
	public Ast.SymbolExpression visitSymbolExpression(AntlerScriptParser.SymbolExpressionContext ctx) {
		return new Ast.SymbolExpression(getTokens(ctx), ctx.getText());
	}

	@Override
	public Ast.StringExpression visitStringExpression(AntlerScriptParser.StringExpressionContext ctx) {
		return new Ast.StringExpression(getTokens(ctx), ctx.STRING().getText(), false);
	}

	@Override
	public Ast.StringExpression visitRawStringExpression(AntlerScriptParser.RawStringExpressionContext ctx) {
		return new Ast.StringExpression(getTokens(ctx), ctx.RAW_STRING().getText(), true);
	}

	@Override
	public Ast.FloatExpression visitFloatExpression(AntlerScriptParser.FloatExpressionContext ctx) {
		String[] floatOut = ctx.getText().split("f");

		switch (floatOut.length) {
		case 1:
			return new Ast.FloatExpression(getTokens(ctx), parseFloat(floatOut[0]), (byte) 64);
		case 2:
			return new Ast.FloatExpression(getTokens(ctx), parseFloat(floatOut[0]), Byte.parseByte(floatOut[1]));
		default:
			assert false;
			return null;
		}
	}

	@Override
	public Ast.IntExpression visitIntegerExpression(AntlerScriptParser.IntegerExpressionContext ctx) {
		String[] intOut = ctx.getText().split("i|u");

		switch (intOut.length) {
		case 1:
			return new Ast.IntExpression(getTokens(ctx), parseInt(intOut[0]), (byte) 64, true);
		case 2:
			return new Ast.IntExpression(getTokens(ctx), parseInt(intOut[0]), Byte.parseByte(intOut[1]), ctx.getText().contains("i"));
		default:
			assert false;
			return null;
		}
	}

	@Override
	public Ast.BooleanExpression visitTrueExpression(AntlerScriptParser.TrueExpressionContext ctx) {
		return new Ast.BooleanExpression(getTokens(ctx), true);
	}

	@Override
	public Ast.BooleanExpression visitFalseExpression(AntlerScriptParser.FalseExpressionContext ctx) {
		return new Ast.BooleanExpression(getTokens(ctx), false);
	}

	@Override
	public Ast.NullExpression visitNullExpression(AntlerScriptParser.NullExpressionContext ctx) {
		return new Ast.NullExpression(getTokens(ctx));
	}

	@Override
	public Ast.SelfExpression visitSelfInstanceExpression(AntlerScriptParser.SelfInstanceExpressionContext ctx) {
		return new Ast.SelfExpression(getTokens(ctx));
	}

	@Override
	public Ast.SuperExpression visitSuperExpression(AntlerScriptParser.SuperExpressionContext ctx) {
		return new Ast.SuperExpression(getTokens(ctx));
	}

	@Override
	public Ast.NewObjectExpression visitNewObjectExpression(AntlerScriptParser.NewObjectExpressionContext ctx) {
		return visitNew_object_instance(ctx.new_object_instance());
	}

	@Override
	public Ast.NewListExpression visitNewListExpression(AntlerScriptParser.NewListExpressionContext ctx) {
		return visitNew_list_instance(ctx.new_list_instance());
	}

	@Override
	public Ast.NewArrayExpression visitNewArrayExpression(AntlerScriptParser.NewArrayExpressionContext ctx) {
		return visitNew_array_instance(ctx.new_array_instance());
	}

	@Override
	public Ast.NewMapExpression visitNewMapExpression(AntlerScriptParser.NewMapExpressionContext ctx) {
		return visitNew_map_instance(ctx.new_map_instance());
	}

	@Override
	public Ast.NewClassInstance visitNewClassInstance(AntlerScriptParser.NewClassInstanceContext ctx) {
		return visitNew_class_instance(ctx.new_class_instance());
	}

	@Override
	public Ast.LambdaExpression visitLambdaExpression(AntlerScriptParser.LambdaExpressionContext ctx) {
		return visitLambda(ctx.lambda());
	}

	@Override
	public Ast.SelectExpression visitSelectExpression(AntlerScriptParser.SelectExpressionContext ctx) {
		return visitSelect(ctx.select());
	}

	@Override
	public Ast.Expression visitGroupedExpression(AntlerScriptParser.GroupedExpressionContext ctx) {
		return visitExpression(ctx.expression());
	}

	@Override
	public Ast.NewObjectExpression visitNew_object_instance(AntlerScriptParser.New_object_instanceContext ctx) {
		List<Ast.Argument> args = visitObject_instantiation_args(ctx.object_instantiation_args());
		return new Ast.NewObjectExpression(getTokens(ctx), ctx.symbol().getText(), args);
	}

	@Override
	public Ast.NewListExpression visitNew_list_instance(AntlerScriptParser.New_list_instanceContext ctx) {
		Ast.ListType type = visitList_header(ctx.list_header());
		List<Ast.Argument> args = visitObject_instantiation_args(ctx.object_instantiation_args());
		return new Ast.NewListExpression(getTokens(ctx), type, args);
	}

	@Override
	public Ast.NewArrayExpression visitNew_array_instance(AntlerScriptParser.New_array_instanceContext ctx) {
		Ast.ArrayType type = visitArray_header(ctx.array_header());
		List<Ast.Argument> args = visitObject_instantiation_args(ctx.object_instantiation_args());
		return new Ast.NewArrayExpression(getTokens(ctx), type, args);
	}

	@Override
	public Ast.NewClassInstance visitNew_class_instance(AntlerScriptParser.New_class_instanceContext ctx) {
		List<Ast.Argument> args = visitObject_instantiation_args(ctx.object_instantiation_args());
		Ast.ClassType type = visitClass_header(ctx.class_header());
		return new Ast.NewClassInstance(getTokens(ctx), type, args);
	}

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

	// Helper, not an override
	public Ast.VariableDeclaration visitDeclaration(AntlerScriptParser.DeclarationContext ctx) {
		return (Ast.VariableDeclaration) visit(ctx);
	}

	@Override
	public Ast.VariableDeclaration visitDeclarationStatement(AntlerScriptParser.DeclarationStatementContext ctx) {
		return visitDeclaration(ctx.declaration());
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

	// Helper, not an override
	public Ast.Statement visitStatement(AntlerScriptParser.StatementContext ctx) {
		return (Ast.Statement) visit(ctx);
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
			getTokens(ctx), false, ctx.isMutable != null, visitType(ctx.type()), ctx.variableName.getText(), null
		);
	}

	@Override
	public Ast.VariableDeclaration visitLetDefinition(AntlerScriptParser.LetDefinitionContext ctx) {
		var type = ctx.type();
		return new Ast.VariableDeclaration(
			getTokens(ctx), false, ctx.isMutable != null, type != null ? visitType(type) : null, ctx.variableName.getText(), visitExpression(ctx.expression())
		);
	}

	@Override
	public Ast.VariableDeclaration visitConstDefinition(AntlerScriptParser.ConstDefinitionContext ctx) {
		var type = ctx.type();
		return new Ast.VariableDeclaration(
			getTokens(ctx), true, false, type != null ? visitType(type) : null, ctx.variableName.getText(), visitExpression(ctx.expression())
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
