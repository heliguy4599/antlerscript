import java.util.*;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;


// public final class CstToAstConverter extends AbstractParseTreeVisitor<Ast.Node> implements
// AntlerScriptParserVisitor<Ast.Node> {
public final class CstToAstConverter extends AntlerScriptParserBaseVisitor<Object> {
	private static List<Token> getTokens(ParserRuleContext ctx) {
		assert ctx != null;

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
		assert ctx != null;

		return (Ast.ClassMember) visit(ctx);
	}

	@Override
	public Ast.ClassType visitClass_top_level(AntlerScriptParser.Class_top_levelContext ctx) {
		assert ctx != null;

		List<Ast.ClassExtendsAccess> extendsAccess = visitClass_extends(ctx.class_extends());
		List<Ast.ClassMember> members = ctx.class_member().stream().map(this::visitClassMember).toList();

		return new Ast.ClassType(getTokens(ctx), extendsAccess, members);
	}

	@Override
	public Ast.ClassType visitClass_header_inside(AntlerScriptParser.Class_header_insideContext ctx) {
		assert ctx != null;

		List<Ast.ClassExtendsAccess> extendsAccess = visitClass_extends(ctx.class_extends());
		List<Ast.ClassMember> members = ctx.class_member().stream().map(this::visitClassMember).toList();

		return new Ast.ClassType(getTokens(ctx), extendsAccess, members);
	}

	@Override
	public List<Ast.ClassExtendsAccess> visitClass_extends(AntlerScriptParser.Class_extendsContext ctx) {
		assert ctx != null;

		return ctx.class_extends_access().stream().map(this::visitClass_extends_access).toList();
	}

	@Override
	public Ast.ClassExtendsAccess visitClass_extends_access(AntlerScriptParser.Class_extends_accessContext ctx) {
		assert ctx != null;

		return new Ast.ClassExtendsAccess(ctx.symbol().stream().map(RuleContext::getText).toList());
	}

	@Override
	public Ast.ConstructorClassMember visitConstructor(AntlerScriptParser.ConstructorContext ctx) {
		assert ctx != null;

		return new Ast.ConstructorClassMember(
			getTokens(ctx), visitConstructor_params(ctx.constructor_params()), visitStatement_block(ctx.statement_block())
		);
	}

	@Override
	public List<Ast.ConstructorParameter> visitConstructor_params(AntlerScriptParser.Constructor_paramsContext ctx) {
		assert ctx != null;

		List<Ast.ConstructorParameter> params = new ArrayList<>(ctx.constructor_params_elm().stream().map(this::visitConstructor_params_elm).toList());
		if (ctx.var_args() != null) {
			params.add(visitVar_args(ctx.var_args()));
		}
		return params;
	}

	@Override
	public Ast.ConstructorParameter visitConstructor_params_elm(AntlerScriptParser.Constructor_params_elmContext ctx) {
		assert ctx != null;

		return new Ast.ConstructorParameter(visitType(ctx.type()), ctx.symbol().getText(), visitExpression(ctx.expression()), false);
	}

	@Override
	public Ast.ConstructorParameter visitVar_args(AntlerScriptParser.Var_argsContext ctx) {
		assert ctx != null;

		return new Ast.ConstructorParameter(visitType(ctx.type()), ctx.symbol().getText(), null, true);
	}

	@Override
	public Ast.CastClassMember visitCastClassMember(AntlerScriptParser.CastClassMemberContext ctx) {
		assert ctx != null;

		return visitCast(ctx.cast());
	}

	@Override
	public Ast.DeclarationClassMember visitDeclarationClassMember(AntlerScriptParser.DeclarationClassMemberContext ctx) {
		assert ctx != null;

		return new Ast.DeclarationClassMember(getTokens(ctx), visitDeclaration(ctx.declaration()));
	}

	@Override
	public Ast.OperatorOverloadClassMember visitOperatorOverloadClassMember(AntlerScriptParser.OperatorOverloadClassMemberContext ctx) {
		assert ctx != null;

		return visitOperator_overload(ctx.operator_overload());
	}

	@Override
	public Ast.ConstructorClassMember visitConstructorClassMember(AntlerScriptParser.ConstructorClassMemberContext ctx) {
		assert ctx != null;

		return visitConstructor(ctx.constructor());
	}

	@Override
	public Ast.CaptureClassMember visitCaptureClassMember(AntlerScriptParser.CaptureClassMemberContext ctx) {
		assert ctx != null;

		return visitCapture(ctx.capture());
	}

	@Override
	public Ast.ExtendsAssignClassMember visitExtendsClassMember(AntlerScriptParser.ExtendsClassMemberContext ctx) {
		assert ctx != null;

		return visitExtends_assign(ctx.extends_assign());
	}

	@Override
	public Ast.CastClassMember visitCast(AntlerScriptParser.CastContext ctx) {
		assert ctx != null;

		return new Ast.CastClassMember(getTokens(ctx), visitType(ctx.type()), visitStatement_block(ctx.statement_block()));
	}

	@Override
	public Ast.OperatorOverloadClassMember visitOperator_overload(AntlerScriptParser.Operator_overloadContext ctx) {
		assert ctx != null;

		return new Ast.OperatorOverloadClassMember(
			getTokens(ctx), visitOverridable(ctx.overridable()), visitType(ctx.rightType), ctx.symbol().getText(), visitType(ctx.returnType), visitStatement_block(ctx.statement_block())
		);
	}

	@Override
	public Ast.OperatorOverloadClassMember.Kind visitOverridable(AntlerScriptParser.OverridableContext ctx) {
		assert ctx != null;

		Ast.OperatorOverloadClassMember.Kind result = switch (ctx.operator.getType()) {
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
		assert ctx != null;

		String target = ctx.target == null ? null : ctx.target.getText();
		var extendsAssign = ctx.extends_assign() == null ? null : visitExtends_assign(ctx.extends_assign());
		return new Ast.CaptureClassMember(getTokens(ctx), visitClass_extends_access(ctx.class_extends_access()), ctx.origin.getText(), target, extendsAssign);
	}

	@Override
	public Ast.ExtendsAssignClassMember visitExtends_assign(AntlerScriptParser.Extends_assignContext ctx) {
		assert ctx != null;

		return new Ast.ExtendsAssignClassMember(getTokens(ctx), ctx.symbol().getText(), visitExpression(ctx.expression()));
	}

	// === ENUMS ===

	@Override
	public Ast.EnumType visitEnum_header_inside(AntlerScriptParser.Enum_header_insideContext ctx) {
		assert ctx != null;

		Ast.ClassExtendsAccess extendsAccess = ctx.class_extends_access() == null ? null : visitClass_extends_access(ctx.class_extends_access());
		List<String> memberSymbols = ctx.symbol().isEmpty() ? null : ctx.symbol().stream().map(AntlerScriptParser.SymbolContext::getText).toList();

		return new Ast.EnumType(getTokens(ctx), extendsAccess, memberSymbols);
	}

	// === TYPES ===

	@Override
	public Ast.Type visitType(AntlerScriptParser.TypeContext ctx) {
		assert ctx != null;

		return visitType_or(ctx.type_or());
	}

	@Override
	public Ast.Type visitType_or(AntlerScriptParser.Type_orContext ctx) {
		assert ctx != null;

		if (ctx.right == null) {
			return visitType_and(ctx.left);
		}

		return new Ast.UnionType(getTokens(ctx), Ast.UnionType.Kind.OR, visitType_and(ctx.left), visitType_or(ctx.right.type_or()));
	}

	@Override
	public Ast.Type visitType_and(AntlerScriptParser.Type_andContext ctx) {
		assert ctx != null;

		if (ctx.right == null) {
			return visitTypeAtomic(ctx.left);
		}

		return new Ast.UnionType(getTokens(ctx), Ast.UnionType.Kind.AND, visitTypeAtomic(ctx.left), visitType_and(ctx.right.type_and()));
	}

	// Helper, not an override
	public Ast.Type visitTypeAtomic(AntlerScriptParser.Type_atomicContext ctx) {
		assert ctx != null;

		return (Ast.Type) visit(ctx);
	}

	@Override
	public Ast.SymbolType visitSymbolType(AntlerScriptParser.SymbolTypeContext ctx) {
		assert ctx != null;

		return new Ast.SymbolType(getTokens(ctx), ctx.symbol().getText());
	}

	@Override
	public Ast.ListType visitListType(AntlerScriptParser.ListTypeContext ctx) {
		assert ctx != null;

		return visitList_header(ctx.list_header());
	}

	@Override
	public Ast.ArrayType visitArrayType(AntlerScriptParser.ArrayTypeContext ctx) {
		assert ctx != null;

		return visitArray_header(ctx.array_header());
	}

	@Override
	public Ast.MapType visitMapType(AntlerScriptParser.MapTypeContext ctx) {
		assert ctx != null;

		return visitMap_header(ctx.map_header());
	}

	public Ast.ClassType visitClassType(AntlerScriptParser.ClassTypeContext ctx) {
		assert ctx != null;

		return visitClass_header(ctx.class_header());
	}

	@Override
	public Ast.EnumType visitEnumType(AntlerScriptParser.EnumTypeContext ctx) {
		assert ctx != null;

		return visitEnum_header(ctx.enum_header());
	}

	@Override
	public Ast.FunctionType visitFuncType(AntlerScriptParser.FuncTypeContext ctx) {
		assert ctx != null;

		return visitFunc_header(ctx.func_header());
	}

	@Override
	public Ast.SelfClassType visitSelfType(AntlerScriptParser.SelfTypeContext ctx) {
		assert ctx != null;

		return new Ast.SelfClassType(getTokens(ctx));
	}

	@Override
	public Ast.Type visitTypeGroup(AntlerScriptParser.TypeGroupContext ctx) {
		assert ctx != null;

		return visitType(ctx.type());
	}

	@Override
	public Ast.ListType visitList_header(AntlerScriptParser.List_headerContext ctx) {
		assert ctx != null;

		Ast.Type type = visitType(ctx.type());
		return new Ast.ListType(getTokens(ctx), type);
	}

	@Override
	public Ast.ArrayType visitArray_header(AntlerScriptParser.Array_headerContext ctx) {
		assert ctx != null;

		Ast.Type type = visitType(ctx.type());
		return new Ast.ArrayType(getTokens(ctx), type);
	}

	@Override
	public Ast.MapType visitMap_header(AntlerScriptParser.Map_headerContext ctx) {
		assert ctx != null;

		Ast.Type first = visitType(ctx.type(0));
		Ast.Type second = visitType(ctx.type(1));
		return new Ast.MapType(getTokens(ctx), first, second);
	}

	@Override
	public Ast.FunctionType visitFunc_header(AntlerScriptParser.Func_headerContext ctx) {
		assert ctx != null;

		Ast.Type returnType = ctx.type() == null ? null : visitType(ctx.type());
		List<Ast.FunctionParameter> params = ctx.func_params() == null ? null : visitFunc_params(ctx.func_params());
		return new Ast.FunctionType(getTokens(ctx), params, returnType);
	}

	@Override
	public List<Ast.FunctionParameter> visitFunc_params(AntlerScriptParser.Func_paramsContext ctx) {
		assert ctx != null;

		List<Ast.FunctionParameter> params = new ArrayList<>(ctx.func_param_elm().stream().map(this::visitFunc_param_elm).toList());

		if (ctx.var_args() != null) {
			params.add(new Ast.FunctionParameter(visitVar_args(ctx.var_args())));
		}

		return params;
	}

	@Override
	public Ast.FunctionParameter visitFunc_param_elm(AntlerScriptParser.Func_param_elmContext ctx) {
		assert ctx != null;

		Ast.Type type = visitType(ctx.type());
		String symbol = ctx.symbol().getText();
		Ast.Expression expr = ctx.expression() == null ? null : visitExpression(ctx.expression());
		return new Ast.FunctionParameter(type, symbol, expr, false);
	}

	@Override
	public Ast.LambdaExpression visitLambda(AntlerScriptParser.LambdaContext ctx) {
		assert ctx != null;

		Ast.FunctionType type = visitFunc_header(ctx.func_header());
		Ast.StatementBlock block = visitStatement_block(ctx.statement_block());
		return new Ast.LambdaExpression(getTokens(ctx), type, block);
	}

	@Override
	public Ast.ClassType visitClass_header(AntlerScriptParser.Class_headerContext ctx) {
		assert ctx != null;

		if (ctx.class_header_inside() == null) {
			return new Ast.ClassType(getTokens(ctx), null, null);
		}
		return visitClass_header_inside(ctx.class_header_inside());
	}

	@Override
	public Ast.EnumType visitEnum_header(AntlerScriptParser.Enum_headerContext ctx) {
		assert ctx != null;

		return visitEnum_header_inside(ctx.enum_header_inside());
	}

	// === EXPRESSIONS ===

	// Helper, not an override
	public Ast.Expression visitExpressionAtom(AntlerScriptParser.Expression_atomContext ctx) {
		assert ctx != null;

		return (Ast.Expression) visit(ctx);
	}

	@Override
	public Ast.Expression visitExpression(AntlerScriptParser.ExpressionContext ctx) {
		assert ctx != null;

		return visitExpression_assignment(ctx.expression_assignment());
	}

	@Override
	public Ast.Expression visitExpression_assignment(AntlerScriptParser.Expression_assignmentContext ctx) {
		assert ctx != null;

		var left = ctx.expression_logical_or();
		var right = ctx.expression_assignment_right();

		if (right == null) {
			return visitExpression_logical_or(left);
		}

		return new Ast.BinaryExpression(
			getTokens(ctx), visitExpression_assignment_right(right), visitExpression_logical_or(left), visitExpression_assignment(right.expression_assignment())
		);
	}

	@Override
	public Ast.BinaryExpression.Kind visitExpression_assignment_right(AntlerScriptParser.Expression_assignment_rightContext ctx) {
		assert ctx != null;

		Token op = ctx.operator;

		Ast.BinaryExpression.Kind kind = switch (op.getType()) {
			case AntlerScriptParser.PLUS_EQ -> Ast.BinaryExpression.Kind.PLUS_ASSIGN;
			case AntlerScriptParser.MINUS_EQ -> Ast.BinaryExpression.Kind.MINUS_ASSIGN;
			case AntlerScriptParser.STAR_EQ -> Ast.BinaryExpression.Kind.MULTIPLY_ASSIGN;
			case AntlerScriptParser.DOUBLE_STAR_EQ -> Ast.BinaryExpression.Kind.EXPONENT_ASSIGN;
			case AntlerScriptParser.SLASH_EQ -> Ast.BinaryExpression.Kind.DIVIDE_ASSIGN;
			case AntlerScriptParser.DOUBLE_SLASH_EQ -> Ast.BinaryExpression.Kind.FLOOR_DIVIDE_ASSIGN;
			case AntlerScriptParser.PERCENT_EQ -> Ast.BinaryExpression.Kind.MODULO_ASSIGN;
			case AntlerScriptParser.DOUBLE_PERCENT_EQ -> Ast.BinaryExpression.Kind.REMAINDER_ASSIGN;
			case AntlerScriptParser.PIPE_EQ -> Ast.BinaryExpression.Kind.BIT_OR_ASSIGN;
			case AntlerScriptParser.AMP_EQ -> Ast.BinaryExpression.Kind.BIT_AND_ASSIGN;
			case AntlerScriptParser.TILDE_EQ -> Ast.BinaryExpression.Kind.BIT_NOT_ASSIGN;
			case AntlerScriptParser.CARRET_EQ -> Ast.BinaryExpression.Kind.BIT_XOR_ASSIGN;
			case AntlerScriptParser.BIT_LSHIFT_EQ -> Ast.BinaryExpression.Kind.BIT_LSHIFT_ASSIGN;
			case AntlerScriptParser.BIT_RSHIFT_EQ -> Ast.BinaryExpression.Kind.BIT_RSHIFT_ASSIGN;
			case AntlerScriptParser.DOUBLE_PLUS_EQ -> Ast.BinaryExpression.Kind.CONCAT_ASSIGN;
			case AntlerScriptParser.DOUBLE_QMARK_EQ -> Ast.BinaryExpression.Kind.NULLISH_ASSIGN;
			case AntlerScriptParser.DOT_EQUAL -> Ast.BinaryExpression.Kind.CHAIN_ASSIGN;
			case AntlerScriptParser.EQUAL -> Ast.BinaryExpression.Kind.ASSIGN;
			default -> null;
		};

		assert kind != null;

		return kind;
	}

	@Override
	public Ast.Expression visitExpression_logical_or(AntlerScriptParser.Expression_logical_orContext ctx) {
		assert ctx != null;

		Ast.Expression latest = visitExpression_logical_and(ctx.expression_logical_and());

		for (AntlerScriptParser.Expression_logical_or_rightContext logicalOrCtx : ctx.expression_logical_or_right()) {
			latest = new Ast.BinaryExpression(getTokens(logicalOrCtx), visitExpression_logical_or_right(logicalOrCtx), latest, visitExpression_logical_and(logicalOrCtx.expression_logical_and()));
		}

		return latest;
	}

	@Override
	public Ast.BinaryExpression.Kind visitExpression_logical_or_right(AntlerScriptParser.Expression_logical_or_rightContext ctx) {
		assert ctx != null;

		Token op = ctx.operator;

		Ast.BinaryExpression.Kind kind = switch (op.getType()) {
			case AntlerScriptParser.OR -> Ast.BinaryExpression.Kind.OR;
			case AntlerScriptParser.DOUBLE_QMARK -> Ast.BinaryExpression.Kind.NULLISH;
			default -> null;
		};

		assert kind != null;

		return kind;
	}

	@Override
	public Ast.Expression visitExpression_logical_and(AntlerScriptParser.Expression_logical_andContext ctx) {
		assert ctx != null;

		Ast.Expression latest = visitExpression_logical_not(ctx.expression_logical_not());

		for (AntlerScriptParser.Expression_logical_and_rightContext logicalAndCtx : ctx.expression_logical_and_right()) {
			latest = new Ast.BinaryExpression(getTokens(logicalAndCtx), visitExpression_logical_and_right(logicalAndCtx), latest, visitExpression_logical_not(logicalAndCtx.expression_logical_not()));
		}

		return latest;
	}

	@Override
	public Ast.BinaryExpression.Kind visitExpression_logical_and_right(AntlerScriptParser.Expression_logical_and_rightContext ctx) {
		assert ctx != null;

		Token op = ctx.operator;

		Ast.BinaryExpression.Kind kind = switch (op.getType()) {
			case AntlerScriptParser.AND -> Ast.BinaryExpression.Kind.AND;
			default -> null;
		};

		assert kind != null;

		return kind;
	}

	@Override
	public Ast.Expression visitExpression_logical_not(AntlerScriptParser.Expression_logical_notContext ctx) {
		assert ctx != null;

		Ast.Expression latest = visitExpression_cmp(ctx.expression_cmp());

		var ops = ctx.NOT();
		var iter = ops.listIterator(ops.size());
		while (iter.hasPrevious()) {
			latest = new Ast.UnaryExpression(getTokens(ctx), Ast.UnaryExpression.Kind.NOT, latest);
		}

		return latest;
	}

	@Override
	public Ast.Expression visitExpression_cmp(AntlerScriptParser.Expression_cmpContext ctx) {
		assert ctx != null;

		Ast.Expression latest = visitExpression_bit_or(ctx.expression_bit_or());

		for (AntlerScriptParser.Expression_cmp_rightContext cmpCtx : ctx.expression_cmp_right()) {
			latest = new Ast.BinaryExpression(getTokens(cmpCtx), visitExpression_cmp_right(cmpCtx), latest, visitExpression_bit_or(cmpCtx.expression_bit_or()));
		}

		return latest;
	}

	@Override
	public Ast.BinaryExpression.Kind visitExpression_cmp_right(AntlerScriptParser.Expression_cmp_rightContext ctx) {
		assert ctx != null;

		Token op = ctx.operator;

		Ast.BinaryExpression.Kind kind = switch (op.getType()) {
			case AntlerScriptParser.LESSER_THAN -> Ast.BinaryExpression.Kind.LESSER_THAN;
			case AntlerScriptParser.GREATER_THAN -> Ast.BinaryExpression.Kind.GREATER_THAN;
			case AntlerScriptParser.LESSER_OR_EQ -> Ast.BinaryExpression.Kind.LESSER_OR_EQUAL;
			case AntlerScriptParser.GREATER_OR_EQ -> Ast.BinaryExpression.Kind.GREATER_OR_EQUAL;
			case AntlerScriptParser.DOUBLE_EQUAL -> Ast.BinaryExpression.Kind.EQUAL;
			case AntlerScriptParser.NOT_EQUAL -> Ast.BinaryExpression.Kind.NOT_EQUAL;
			case AntlerScriptParser.IN -> Ast.BinaryExpression.Kind.IN;
			case AntlerScriptParser.IS -> Ast.BinaryExpression.Kind.IS;
			default -> null;
		};

		assert kind != null;

		return kind;
	}

	@Override
	public Ast.Expression visitExpression_bit_or(AntlerScriptParser.Expression_bit_orContext ctx) {
		assert ctx != null;

		Ast.Expression latest = visitExpression_bit_xor(ctx.expression_bit_xor());

		for (AntlerScriptParser.Expression_bit_or_rightContext bitOrCtx : ctx.expression_bit_or_right()) {
			latest = new Ast.BinaryExpression(getTokens(bitOrCtx), visitExpression_bit_or_right(bitOrCtx), latest, visitExpression_bit_xor(bitOrCtx.expression_bit_xor()));
		}

		return latest;
	}

	@Override
	public Ast.BinaryExpression.Kind visitExpression_bit_or_right(AntlerScriptParser.Expression_bit_or_rightContext ctx) {
		assert ctx != null;

		Token op = ctx.operator;

		Ast.BinaryExpression.Kind kind = switch (op.getType()) {
			case AntlerScriptParser.PIPE -> Ast.BinaryExpression.Kind.BIT_OR;
			default -> null;
		};

		assert kind != null;

		return kind;
	}

	@Override
	public Ast.Expression visitExpression_bit_xor(AntlerScriptParser.Expression_bit_xorContext ctx) {
		assert ctx != null;

		Ast.Expression latest = visitExpression_bit_and(ctx.expression_bit_and());

		for (AntlerScriptParser.Expression_bit_xor_rightContext bitXorCtx : ctx.expression_bit_xor_right()) {
			latest = new Ast.BinaryExpression(getTokens(bitXorCtx), visitExpression_bit_xor_right(bitXorCtx), latest, visitExpression_bit_and(bitXorCtx.expression_bit_and()));
		}

		return latest;
	}

	@Override
	public Ast.BinaryExpression.Kind visitExpression_bit_xor_right(AntlerScriptParser.Expression_bit_xor_rightContext ctx) {
		assert ctx != null;

		Token op = ctx.operator;

		Ast.BinaryExpression.Kind kind = switch (op.getType()) {
			case AntlerScriptParser.CARRET -> Ast.BinaryExpression.Kind.BIT_XOR;
			default -> null;
		};

		assert kind != null;

		return kind;
	}

	@Override
	public Ast.Expression visitExpression_bit_and(AntlerScriptParser.Expression_bit_andContext ctx) {
		assert ctx != null;

		Ast.Expression latest = visitExpression_bit_shift(ctx.expression_bit_shift());

		for (AntlerScriptParser.Expression_bit_and_rightContext bitAndCtx : ctx.expression_bit_and_right()) {
			latest = new Ast.BinaryExpression(getTokens(bitAndCtx), visitExpression_bit_and_right(bitAndCtx), latest, visitExpression_bit_shift(bitAndCtx.expression_bit_shift()));
		}

		return latest;
	}

	@Override
	public Ast.BinaryExpression.Kind visitExpression_bit_and_right(AntlerScriptParser.Expression_bit_and_rightContext ctx) {
		assert ctx != null;

		Token op = ctx.operator;

		Ast.BinaryExpression.Kind kind = switch (op.getType()) {
			case AntlerScriptParser.AMP -> Ast.BinaryExpression.Kind.BIT_AND;
			default -> null;
		};

		assert kind != null;

		return kind;
	}

	@Override
	public Ast.Expression visitExpression_bit_shift(AntlerScriptParser.Expression_bit_shiftContext ctx) {
		assert ctx != null;

		Ast.Expression latest = visitExpression_add(ctx.expression_add());

		for (AntlerScriptParser.Expression_bit_shift_rightContext bitShiftCtx : ctx.expression_bit_shift_right()) {
			latest = new Ast.BinaryExpression(getTokens(bitShiftCtx), visitExpression_bit_shift_right(bitShiftCtx), latest, visitExpression_add(bitShiftCtx.expression_add()));
		}

		return latest;
	}

	@Override
	public Ast.BinaryExpression.Kind visitExpression_bit_shift_right(AntlerScriptParser.Expression_bit_shift_rightContext ctx) {
		assert ctx != null;

		Token op = ctx.operator;

		Ast.BinaryExpression.Kind kind = switch (op.getType()) {
			case AntlerScriptParser.BIT_LSHIFT -> Ast.BinaryExpression.Kind.BIT_LSHIFT;
			case AntlerScriptParser.BIT_RSHIFT -> Ast.BinaryExpression.Kind.BIT_RSHIFT;
			default -> null;
		};

		assert kind != null;

		return kind;
	}

	@Override
	public Ast.Expression visitExpression_add(AntlerScriptParser.Expression_addContext ctx) {
		assert ctx != null;

		Ast.Expression latest = visitExpression_mult(ctx.expression_mult());

		for (AntlerScriptParser.Expression_add_rightContext addCtx : ctx.expression_add_right()) {
			latest = new Ast.BinaryExpression(getTokens(addCtx), visitExpression_add_right(addCtx), latest, visitExpression_mult(addCtx.expression_mult()));
		}

		return latest;
	}

	@Override
	public Ast.BinaryExpression.Kind visitExpression_add_right(AntlerScriptParser.Expression_add_rightContext ctx) {
		assert ctx != null;

		Token op = ctx.operator;

		Ast.BinaryExpression.Kind kind = switch (op.getType()) {
			case AntlerScriptParser.PLUS -> Ast.BinaryExpression.Kind.ADD;
			case AntlerScriptParser.MINUS -> Ast.BinaryExpression.Kind.SUBTRACT;
			case AntlerScriptParser.DOUBLE_PLUS -> Ast.BinaryExpression.Kind.CONCAT;
			default -> null;
		};

		assert kind != null;

		return kind;
	}

	@Override
	public Ast.Expression visitExpression_mult(AntlerScriptParser.Expression_multContext ctx) {
		assert ctx != null;

		Ast.Expression latest = visitExpression_unary(ctx.expression_unary());

		for (AntlerScriptParser.Expression_mult_rightContext multCtx : ctx.expression_mult_right()) {
			latest = new Ast.BinaryExpression(getTokens(multCtx), visitExpression_mult_right(multCtx), latest, visitExpression_unary(multCtx.expression_unary()));
		}

		return latest;
	}

	@Override
	public Ast.BinaryExpression.Kind visitExpression_mult_right(AntlerScriptParser.Expression_mult_rightContext ctx) {
		assert ctx != null;

		Token op = ctx.operator;

		Ast.BinaryExpression.Kind kind = switch (op.getType()) {
			case AntlerScriptParser.STAR -> Ast.BinaryExpression.Kind.MULTIPLY;
			case AntlerScriptParser.SLASH -> Ast.BinaryExpression.Kind.DIVIDE;
			case AntlerScriptParser.DOUBLE_SLASH -> Ast.BinaryExpression.Kind.FLOOR_DIVIDE;
			case AntlerScriptParser.PERCENT -> Ast.BinaryExpression.Kind.MODULO;
			case AntlerScriptParser.DOUBLE_PERCENT -> Ast.BinaryExpression.Kind.REMAINDER;
			default -> null;
		};

		assert kind != null;

		return kind;
	}

	@Override
	public Ast.Expression visitExpression_unary(AntlerScriptParser.Expression_unaryContext ctx) {
		assert ctx != null;

		Ast.Expression latest = visitExpression_exp(ctx.expression_exp());

		var ops = ctx.expression_unary_op();
		var iter = ops.listIterator(ops.size());
		while (iter.hasPrevious()) {
			var previous = iter.previous();
			latest = new Ast.UnaryExpression(getTokens(previous), visitExpression_unary_op(previous), latest);
		}

		return latest;
	}

	@Override
	public Ast.UnaryExpression.Kind visitExpression_unary_op(AntlerScriptParser.Expression_unary_opContext ctx) {
		assert ctx != null;

		Token op = ctx.operator;

		Ast.UnaryExpression.Kind kind = switch (op.getType()) {
			case AntlerScriptParser.PLUS -> Ast.UnaryExpression.Kind.PLUS;
			case AntlerScriptParser.MINUS -> Ast.UnaryExpression.Kind.MINUS;
			case AntlerScriptParser.TILDE -> Ast.UnaryExpression.Kind.BIT_NOT;
			default -> null;
		};

		assert kind != null;

		return kind;
	}

	@Override
	public Ast.Expression visitExpression_exp(AntlerScriptParser.Expression_expContext ctx) {
		assert ctx != null;

		Ast.Expression latest = visitExpression_postfix(ctx.expression_postfix());

		for (AntlerScriptParser.Expression_exp_rightContext expCtx : ctx.expression_exp_right()) {
			latest = new Ast.BinaryExpression(getTokens(expCtx), Ast.BinaryExpression.Kind.EXPONENT, latest, visitExpression_postfix(expCtx.expression_postfix()));
		}

		return latest;
	}

	@Override
	public Object visitExpression_exp_right(AntlerScriptParser.Expression_exp_rightContext ctx) {
		assert ctx != null;

		// Should be handled by the caller
		assert false;
		return null;
	}

	@Override
	public Ast.Expression visitExpression_postfix(AntlerScriptParser.Expression_postfixContext ctx) {
		assert ctx != null;

		Ast.Expression latest = visitExpressionAtom(ctx.expression_atom());

		for (AntlerScriptParser.Expression_accessContext accessCtx : ctx.expression_access()) {
			switch (accessCtx) {
				case AntlerScriptParser.IndexAccessContext idxCtx:
					latest = new Ast.IndexExpression(getTokens(idxCtx), latest, visitExpression(idxCtx.expression()));
					break;
				case AntlerScriptParser.FunctionCallContext funcallCtx:
					latest = new Ast.CallExpression(getTokens(funcallCtx), latest, funcallCtx.arguments() == null ? null : visitArguments(funcallCtx.arguments()));
					break;
				case AntlerScriptParser.MemberAccessContext memCtx:
					latest = new Ast.AccessExpression(getTokens(memCtx), latest, memCtx.symbol().getText(), false);
					break;
				case AntlerScriptParser.NullishAccessContext nullCtx:
					latest = new Ast.AccessExpression(getTokens(nullCtx), latest, nullCtx.symbol().getText(), true);
					break;
				default:
					assert false;
			}
		}

		return latest;
	}

	@Override
	public Object visitIndexAccess(AntlerScriptParser.IndexAccessContext ctx) {
		assert ctx != null;

		// Should be handled by the caller
		assert false;
		return null;
	}

	@Override
	public Object visitFunctionCall(AntlerScriptParser.FunctionCallContext ctx) {
		assert ctx != null;

		// Should be handled by the caller
		assert false;
		return null;
	}

	@Override
	public Object visitMemberAccess(AntlerScriptParser.MemberAccessContext ctx) {
		assert ctx != null;

		// Should be handled by the caller
		assert false;
		return null;
	}

	@Override
	public Object visitNullishAccess(AntlerScriptParser.NullishAccessContext ctx) {
		assert ctx != null;

		// Should be handled by the caller
		assert false;
		return null;
	}

	@Override
	public List<Ast.Argument> visitArguments(AntlerScriptParser.ArgumentsContext ctx) {
		assert ctx != null;

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
		assert ctx != null;

		return new Ast.Argument(visitExpression(ctx.expression()), ctx.symbol().getText(), false);
	}

	@Override
	public Ast.SymbolExpression visitSymbolExpression(AntlerScriptParser.SymbolExpressionContext ctx) {
		assert ctx != null;

		return new Ast.SymbolExpression(getTokens(ctx), ctx.getText());
	}

	@Override
	public Ast.StringExpression visitStringExpression(AntlerScriptParser.StringExpressionContext ctx) {
		assert ctx != null;

		return new Ast.StringExpression(getTokens(ctx), ctx.STRING().getText(), false);
	}

	@Override
	public Ast.StringExpression visitRawStringExpression(AntlerScriptParser.RawStringExpressionContext ctx) {
		assert ctx != null;

		return new Ast.StringExpression(getTokens(ctx), ctx.RAW_STRING().getText(), true);
	}

	@Override
	public Ast.FloatExpression visitFloatExpression(AntlerScriptParser.FloatExpressionContext ctx) {
		assert ctx != null;

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
		assert ctx != null;

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
		assert ctx != null;

		return new Ast.BooleanExpression(getTokens(ctx), true);
	}

	@Override
	public Ast.BooleanExpression visitFalseExpression(AntlerScriptParser.FalseExpressionContext ctx) {
		assert ctx != null;

		return new Ast.BooleanExpression(getTokens(ctx), false);
	}

	@Override
	public Ast.NullExpression visitNullExpression(AntlerScriptParser.NullExpressionContext ctx) {
		assert ctx != null;

		return new Ast.NullExpression(getTokens(ctx));
	}

	@Override
	public Ast.SelfExpression visitSelfInstanceExpression(AntlerScriptParser.SelfInstanceExpressionContext ctx) {
		assert ctx != null;

		return new Ast.SelfExpression(getTokens(ctx));
	}

	@Override
	public Ast.SuperExpression visitSuperExpression(AntlerScriptParser.SuperExpressionContext ctx) {
		assert ctx != null;

		return new Ast.SuperExpression(getTokens(ctx));
	}

	@Override
	public Ast.NewObjectExpression visitNewObjectExpression(AntlerScriptParser.NewObjectExpressionContext ctx) {
		assert ctx != null;

		return visitNew_object_instance(ctx.new_object_instance());
	}

	@Override
	public Ast.NewListExpression visitNewListExpression(AntlerScriptParser.NewListExpressionContext ctx) {
		assert ctx != null;

		return visitNew_list_instance(ctx.new_list_instance());
	}

	@Override
	public Ast.NewArrayExpression visitNewArrayExpression(AntlerScriptParser.NewArrayExpressionContext ctx) {
		assert ctx != null;

		return visitNew_array_instance(ctx.new_array_instance());
	}

	@Override
	public Ast.NewMapExpression visitNewMapExpression(AntlerScriptParser.NewMapExpressionContext ctx) {
		assert ctx != null;

		return visitNew_map_instance(ctx.new_map_instance());
	}

	@Override
	public Ast.NewClassInstance visitNewClassInstance(AntlerScriptParser.NewClassInstanceContext ctx) {
		assert ctx != null;

		return visitNew_class_instance(ctx.new_class_instance());
	}

	@Override
	public Ast.LambdaExpression visitLambdaExpression(AntlerScriptParser.LambdaExpressionContext ctx) {
		assert ctx != null;

		return visitLambda(ctx.lambda());
	}

	@Override
	public Ast.SelectExpression visitSelectExpression(AntlerScriptParser.SelectExpressionContext ctx) {
		assert ctx != null;

		return visitSelect(ctx.select());
	}

	@Override
	public Ast.Expression visitGroupedExpression(AntlerScriptParser.GroupedExpressionContext ctx) {
		assert ctx != null;

		return visitExpression(ctx.expression());
	}

	@Override
	public Ast.NewObjectExpression visitNew_object_instance(AntlerScriptParser.New_object_instanceContext ctx) {
		assert ctx != null;

		List<Ast.Argument> args = visitObject_instantiation_args(ctx.object_instantiation_args());
		return new Ast.NewObjectExpression(getTokens(ctx), ctx.symbol().getText(), args);
	}

	@Override
	public Ast.NewListExpression visitNew_list_instance(AntlerScriptParser.New_list_instanceContext ctx) {
		assert ctx != null;

		Ast.ListType type = visitList_header(ctx.list_header());
		List<Ast.Argument> args = visitObject_instantiation_args(ctx.object_instantiation_args());
		return new Ast.NewListExpression(getTokens(ctx), type, args);
	}

	@Override
	public Ast.NewArrayExpression visitNew_array_instance(AntlerScriptParser.New_array_instanceContext ctx) {
		assert ctx != null;

		Ast.ArrayType type = visitArray_header(ctx.array_header());
		List<Ast.Argument> args = visitObject_instantiation_args(ctx.object_instantiation_args());
		return new Ast.NewArrayExpression(getTokens(ctx), type, args);
	}

	@Override
	public Ast.NewClassInstance visitNew_class_instance(AntlerScriptParser.New_class_instanceContext ctx) {
		assert ctx != null;

		List<Ast.Argument> args = visitObject_instantiation_args(ctx.object_instantiation_args());
		Ast.ClassType type = visitClass_header(ctx.class_header());
		return new Ast.NewClassInstance(getTokens(ctx), type, args);
	}

	@Override
	public List<Ast.Argument> visitObject_instantiation_args(AntlerScriptParser.Object_instantiation_argsContext ctx) {
		assert ctx != null;

		return visitArguments(ctx.arguments());
	}

	@Override
	public Ast.NewMapExpression visitNew_map_instance(AntlerScriptParser.New_map_instanceContext ctx) {
		assert ctx != null;

		Ast.MapType type = visitMap_header(ctx.map_header());
		List<Ast.KeyValuePair> pairs = visitKeypair_list(ctx.keypair_list());
		return new Ast.NewMapExpression(getTokens(ctx), type, pairs);
	}

	@Override
	public Ast.SelectExpression visitSelect(AntlerScriptParser.SelectContext ctx) {
		assert ctx != null;

		return new Ast.SelectExpression(getTokens(ctx), visitExpression(ctx.expression()), visitKeypair_list(ctx.keypair_list()));
	}

	@Override
	public List<Ast.KeyValuePair> visitKeypair_list(AntlerScriptParser.Keypair_listContext ctx) {
		assert ctx != null;

		return ctx.keypair_clause().stream().map(this::visitKeypair_clause).toList();
	}

	@Override
	public Ast.KeyValuePair visitKeypair_clause(AntlerScriptParser.Keypair_clauseContext ctx) {
		assert ctx != null;

		var expression = ctx.expression();
		return new Ast.KeyValuePair(visitExpression(expression.getFirst()), visitExpression(expression.getLast()));
	}

	// === STATEMENT ===

	@Override
	public Ast.ExpressionStatement visitExpressionStatement(AntlerScriptParser.ExpressionStatementContext ctx) {
		assert ctx != null;

		return new Ast.ExpressionStatement(getTokens(ctx), visitExpression(ctx.expression()), ctx.DEFER() != null);
	}

	@Override
	public Ast.BreakStatement visitBreakStatement(AntlerScriptParser.BreakStatementContext ctx) {
		assert ctx != null;

		return new Ast.BreakStatement(getTokens(ctx));
	}

	@Override
	public Ast.ContinueStatement visitContinueStatement(AntlerScriptParser.ContinueStatementContext ctx) {
		assert ctx != null;

		return new Ast.ContinueStatement(getTokens(ctx));
	}

	@Override
	public Ast.ReturnStatement visitReturnStatement(AntlerScriptParser.ReturnStatementContext ctx) {
		assert ctx != null;

		Ast.Expression expression = visitExpression(ctx.expression());
		return new Ast.ReturnStatement(getTokens(ctx), expression);
	}

	@Override
	public Ast.LoopStatement visitLoopStatement(AntlerScriptParser.LoopStatementContext ctx) {
		assert ctx != null;

		return visitLoop(ctx.loop());
	}

	@Override
	public Ast.WhileStatement visitWhileStatement(AntlerScriptParser.WhileStatementContext ctx) {
		assert ctx != null;

		return visitWhile(ctx.while_());
	}

	@Override
	public Ast.IterateStatement visitIterateStatement(AntlerScriptParser.IterateStatementContext ctx) {
		assert ctx != null;

		return visitIterate(ctx.iterate());
	}

	// Helper, not an override
	public Ast.VariableDeclaration visitDeclaration(AntlerScriptParser.DeclarationContext ctx) {
		assert ctx != null;

		return (Ast.VariableDeclaration) visit(ctx);
	}

	@Override
	public Ast.VariableDeclaration visitDeclarationStatement(AntlerScriptParser.DeclarationStatementContext ctx) {
		assert ctx != null;

		return visitDeclaration(ctx.declaration());
	}

	@Override
	public Ast.Typedef visitTypedefStatement(AntlerScriptParser.TypedefStatementContext ctx) {
		assert ctx != null;

		return visitTypedef(ctx.typedef());
	}

	@Override
	public Ast.IfStatement visitIfStatement(AntlerScriptParser.IfStatementContext ctx) {
		assert ctx != null;

		return visitIf(ctx.if_());
	}

	@Override
	public Ast.SwitchStatement visitSwitchStatement(AntlerScriptParser.SwitchStatementContext ctx) {
		assert ctx != null;

		return visitSwitch(ctx.switch_());
	}

	@Override
	public Ast.StatementBlock visitStatementBlockStatement(AntlerScriptParser.StatementBlockStatementContext ctx) {
		assert ctx != null;

		return new Ast.StatementBlock(getTokens(ctx), ctx.statement_block().statement().stream().map(this::visitStatement).toList(), ctx.DEFER() != null);
	}

	// Helper, not an override
	public Ast.Statement visitStatement(AntlerScriptParser.StatementContext ctx) {
		assert ctx != null;

		return (Ast.Statement) visit(ctx);
	}

	@Override
	public Ast.StatementBlock visitStatement_block(AntlerScriptParser.Statement_blockContext ctx) {
		assert ctx != null;

		return new Ast.StatementBlock(getTokens(ctx), ctx.statement().stream().map(this::visitStatement).toList(), false);
	}

	@Override
	public Ast.LoopStatement visitLoop(AntlerScriptParser.LoopContext ctx) {
		assert ctx != null;

		Ast.Expression from = visitExpression(ctx.from);
		Ast.Expression to = visitExpression(ctx.to);
		Ast.Expression by = visitExpression(ctx.by);
		String iterationSymbol = ctx.iterator.getText();
		Ast.StatementBlock block = visitStatement_block(ctx.block);
		return new Ast.LoopStatement(getTokens(ctx), from, to, by, iterationSymbol, block);
	}

	@Override
	public Ast.WhileStatement visitWhile(AntlerScriptParser.WhileContext ctx) {
		assert ctx != null;

		Ast.Expression test = visitExpression(ctx.test);
		Ast.StatementBlock block = visitStatement_block(ctx.block);
		return new Ast.WhileStatement(getTokens(ctx), test, block);
	}

	@Override
	public Ast.IterateStatement visitIterate(AntlerScriptParser.IterateContext ctx) {
		assert ctx != null;

		Ast.Expression iterable = visitExpression(ctx.iterable);
		Ast.StatementBlock block = visitStatement_block(ctx.block);
		String indexSymbol = ctx.index.getText();
		String elementSymbol = ctx.element.getText();
		return new Ast.IterateStatement(getTokens(ctx), iterable, indexSymbol, elementSymbol, block);
	}

	@Override
	public Ast.VariableDeclaration visitLetDeclaration(AntlerScriptParser.LetDeclarationContext ctx) {
		assert ctx != null;

		return new Ast.VariableDeclaration(
			getTokens(ctx), false, ctx.isMutable != null, visitType(ctx.type()), ctx.variableName.getText(), null
		);
	}

	@Override
	public Ast.VariableDeclaration visitLetDefinition(AntlerScriptParser.LetDefinitionContext ctx) {
		assert ctx != null;

		var type = ctx.type();
		return new Ast.VariableDeclaration(
			getTokens(ctx), false, ctx.isMutable != null, type != null ? visitType(type) : null, ctx.variableName.getText(), visitExpression(ctx.expression())
		);
	}

	@Override
	public Ast.VariableDeclaration visitConstDefinition(AntlerScriptParser.ConstDefinitionContext ctx) {
		assert ctx != null;

		var type = ctx.type();
		return new Ast.VariableDeclaration(
			getTokens(ctx), true, false, type != null ? visitType(type) : null, ctx.variableName.getText(), visitExpression(ctx.expression())
		);
	}

	@Override
	public Ast.Typedef visitTypedef(AntlerScriptParser.TypedefContext ctx) {
		assert ctx != null;

		Ast.Type type = visitType(ctx.type());
		return new Ast.Typedef(getTokens(ctx), ctx.symbol().getText(), type);
	}

	@Override
	public Ast.IfStatement visitIf(AntlerScriptParser.IfContext ctx) {
		assert ctx != null;

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
		assert ctx != null;

		Ast.Expression test = visitExpression(ctx.test);
		Ast.StatementBlock block = visitStatement_block(ctx.block);
		return new Ast.ElifBranch(getTokens(ctx), test, block);
	}

	@Override
	public Ast.StatementBlock visitElse(AntlerScriptParser.ElseContext ctx) {
		assert ctx != null;

		return visitStatement_block(ctx.block);
	}

	@Override
	public Ast.SwitchStatement visitSwitch(AntlerScriptParser.SwitchContext ctx) {
		assert ctx != null;

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
		assert ctx != null;

		Ast.StatementBlock body = visitStatement_block(ctx.block);

		List<Ast.Expression> values = new ArrayList<>();
		for (AntlerScriptParser.ExpressionContext expr : ctx.expression()) {
			values.add(visitExpression(expr));
		}

		return new Ast.CaseBranch(getTokens(ctx), values, body);
	}
}
