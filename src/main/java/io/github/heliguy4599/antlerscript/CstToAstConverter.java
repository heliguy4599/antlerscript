package io.github.heliguy4599.antlerscript;

import java.util.*;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public final class CstToAstConverter extends AbstractParseTreeVisitor<Object> implements
AntlerScriptParserVisitor<Object> {
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
		assert parseTrees != null;
		assert out != null;

		for (ParseTree pt : parseTrees) {
			Object pl = pt.getPayload();

			if (pl instanceof Token token) {
				// Set the token's `text` field instead of
				// extracting from the input for more
				// information during debugging
				if (token instanceof CommonToken ctoken) {
					ctoken.setText(token.getText());
				}

				out.add(token);

				continue;
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

	// === MISC ===

	@Override
	public List<Ast.Type> visitGeneric_args(AntlerScriptParser.Generic_argsContext ctx) {
		assert ctx != null;

		return ctx.type().stream().map(this::visitType).toList();
	}

	@Override
	public List<Ast.GenericParameter> visitGeneric_parameters(AntlerScriptParser.Generic_parametersContext ctx) {
		assert ctx != null;

		List<Ast.GenericParameter> params = new ArrayList<>();

		for (int i = 0; i < ctx.type().size(); i++) {
			var param = new Ast.GenericParameter(visitType(ctx.type(i)), ctx.symbol(i).getText());
			params.add(param);
		}

		return params;
	}

	// Invalid, should be handled by the caller with symbol().getText()
	@Override
	public Object visitSymbol(AntlerScriptParser.SymbolContext ctx) {
		assert ctx != null;

		assert false;
		return null;
	}

	// Invalid, should be handled by the caller
	@Override
	public Object visitSemicolon(AntlerScriptParser.SemicolonContext ctx) {
		assert ctx != null;

		assert false;
		return null;
	}

	// Invalid, should be handled by the caller
	@Override
	public Ast.ClassType visitClass_header_inside(AntlerScriptParser.Class_header_insideContext ctx) {
		assert ctx != null;

		assert false;
		return null;
	}

	// Invalid, should be handled by the caller
	@Override
	public Object visitCoroutine_header_yield(AntlerScriptParser.Coroutine_header_yieldContext ctx) {
		assert ctx != null;

		assert false;
		return null;
	}

	// Invalid, should be handled by the caller
	@Override
	public Object visitLoop_capture_2(AntlerScriptParser.Loop_capture_2Context ctx) {
		assert ctx != null;

		assert false;
		return null;
	}

	// Invalid, should be handled by the caller
	@Override
	public Object visitLoop_header_inside(AntlerScriptParser.Loop_header_insideContext ctx) {
		assert ctx != null;

		assert false;
		return null;
	}

	// Invalid, should be handled by the caller
	@Override
	public Ast.EnumType visitEnum_header_inside(AntlerScriptParser.Enum_header_insideContext ctx) {
		assert ctx != null;

		assert false;
		return null;
	}

	// Invalid, should be handled by the caller
	@Override
	public Object visitLoop_range(AntlerScriptParser.Loop_rangeContext ctx) {
		assert ctx != null;

		assert false;
		return null;
	}

	// Invalid, should be handled by the caller
	@Override
	public Object visitLoop_iteration(AntlerScriptParser.Loop_iterationContext ctx) {
		assert ctx != null;

		assert false;
		return null;
	}

	// === FILES ===

	@Override
	public Ast.Program visitProgram(AntlerScriptParser.ProgramContext ctx) {
		assert ctx != null;

		if (ctx.main_program() != null) {
			return visitMain_program(ctx.main_program());
		} else if (ctx.class_program() != null) {
			return visitClass_program(ctx.class_program());
		} else if (ctx.namespace_program() != null) {
			return visitNamespace_program(ctx.namespace_program());
		} else if (ctx.implicit_namespace_program() != null) {
			return visitImplicit_namespace_program(ctx.implicit_namespace_program());
		} else {
			assert false;
			return null;
		}
	}

	@Override
	public List<List<String>> visitUsing_directive(AntlerScriptParser.Using_directiveContext ctx) {
		assert ctx != null;

		List<Ast.SymbolChain> symbolChains = ctx.symbol_chain().stream().map(this::visitSymbol_chain).toList();

		List<List<String>> ret = new ArrayList<>();
		for (Ast.SymbolChain symbolChain : symbolChains) {
			ret.add(symbolChain.symbols());
		}

		return ret;
	}

	@Override
	public Object visitRepeatable_directive(AntlerScriptParser.Repeatable_directiveContext ctx) {
		assert ctx != null;

		return visit(ctx.using_directive() != null ? ctx.using_directive() : ctx.other_directive());
	}

	@Override
	public Ast.FileDirective visitOther_directive(AntlerScriptParser.Other_directiveContext ctx) {
		assert ctx != null;

		String string = null;

		if (ctx.STRING() != null) {
			string = ctx.STRING().getText();

			assert string.length() >= 2;
			assert string.charAt(0) == '"';
			assert string.charAt(string.length() - 1) == '"';
		} else if (ctx.RAW_STRING() != null) {
			string = ctx.RAW_STRING().getText();

			assert string.length() >= 2;
			assert string.charAt(0) == '`';
			assert string.charAt(string.length() - 1) == '`';
		}

		return new Ast.FileDirective(ctx.symbol().getText(), string);
	}

	@Override
	public String visitNamespace_directive(AntlerScriptParser.Namespace_directiveContext ctx) {
		assert ctx != null;

		return ctx.symbol().getText();
	}

	@Override
	public String visitClassname_directive(AntlerScriptParser.Classname_directiveContext ctx) {
		assert ctx != null;

		return ctx.symbol().getText();
	}

	@Override
	public Ast.FileDirective visitMain_directive(AntlerScriptParser.Main_directiveContext ctx) {
		assert ctx != null;

		return new Ast.FileDirective(ctx.MAIN_DIRECTIVE().getText(), null);
	}

	public static void segregateDirectives(List<Object> inDirectives, List<Ast.SymbolChain> outUsing, List<Ast.FileDirective> outOther) {
		assert inDirectives != null;
		assert outUsing != null;
		assert outOther != null;

		for (Object directive : inDirectives) {
			if (directive instanceof Ast.FileDirective) {
				outOther.add((Ast.FileDirective) directive);
			} else { // List<SymbolChain> (using directive)
				@SuppressWarnings("unchecked")
				List<Ast.SymbolChain> usingDirective = (List<Ast.SymbolChain>) directive;
				outUsing.addAll(usingDirective);
			}
		}
	}

	@Override
	public Ast.MainProgram visitMain_program(AntlerScriptParser.Main_programContext ctx) {
		assert ctx != null;

		List<Ast.Statement> statements = ctx.statement().stream().map(this::visitStatement).toList();

		List<Object> directives = ctx.repeatable_directive().stream().map(this::visitRepeatable_directive).toList();
		List<Ast.SymbolChain> using = new ArrayList<>();
		List<Ast.FileDirective> other = new ArrayList<>();
		segregateDirectives(directives, using, other);

		return new Ast.MainProgram(getTokens(ctx), using, other, statements);
	}

	@Override
	public Ast.ClassProgram visitClass_program(AntlerScriptParser.Class_programContext ctx) {
		assert ctx != null;

		String namespace = ctx.namespace_directive() == null
			? null
			: visitNamespace_directive(ctx.namespace_directive());
		String classname = visitClassname_directive(ctx.classname_directive());
		Ast.ClassType topLevel = ctx.class_top_level() == null
			? new Ast.ClassType(getTokens(ctx), null, null, null)
			: visitClass_top_level(ctx.class_top_level());

		List<Object> directives = ctx.repeatable_directive().stream().map(this::visitRepeatable_directive).toList();
		List<Ast.SymbolChain> using = new ArrayList<>();
		List<Ast.FileDirective> other = new ArrayList<>();
		segregateDirectives(directives, using, other);

		return new Ast.ClassProgram(getTokens(ctx), using, other, namespace, classname, topLevel);
	}

	@Override
	public Ast.NamespaceProgram visitNamespace_program(AntlerScriptParser.Namespace_programContext ctx) {
		assert ctx != null;

		String namespace = visitNamespace_directive(ctx.namespace_directive());
		List<Ast.NamespaceMember> members = ctx.namespace_member().stream().map(this::visitNamespace_member).toList();
		List<Object> directives = ctx.repeatable_directive() == null
			? null
			: ctx.repeatable_directive().stream().map(this::visitRepeatable_directive).toList();
		List<Ast.SymbolChain> using = new ArrayList<>();
		List<Ast.FileDirective> other = new ArrayList<>();
		segregateDirectives(directives, using, other);

		return new Ast.NamespaceProgram(getTokens(ctx), using, other, namespace, members);
	}

	@Override
	public Ast.NamespaceProgram visitImplicit_namespace_program(AntlerScriptParser.Implicit_namespace_programContext ctx) {
		assert ctx != null;

		List<Ast.NamespaceMember> members = ctx.namespace_member().stream().map(this::visitNamespace_member).toList();

		List<Object> directives = ctx.repeatable_directive().stream().map(this::visitRepeatable_directive).toList();
		List<Ast.SymbolChain> using = new ArrayList<>();
		List<Ast.FileDirective> other = new ArrayList<>();
		segregateDirectives(directives, using, other);

		return new Ast.NamespaceProgram(getTokens(ctx), using, other, null, members);
	}

	@Override
	public Ast.NamespaceMember visitNamespace_member(AntlerScriptParser.Namespace_memberContext ctx) {
		assert ctx != null;

		if (ctx.declaration() != null) {
			return new Ast.NamespaceDeclaration(visitDeclaration(ctx.declaration()));
		}

		return new Ast.NamespaceTypedef(visitTypedef(ctx.typedef()));
	}

	// === CLASSES ===

	// Helper, not an override
	public Ast.ClassMember visitClassMember(AntlerScriptParser.Class_memberContext ctx) {
		assert ctx != null;

		return (Ast.ClassMember) visit(ctx);
	}

	@Override
	public Ast.ClassType visitClass_top_level(AntlerScriptParser.Class_top_levelContext ctx) {
		assert ctx != null;

		List<Ast.SymbolChain> extendsAccess = ctx.class_extends() == null
			? null
			: visitClass_extends(ctx.class_extends());
		List<Ast.ClassMember> members = ctx.class_member().stream().map(this::visitClassMember).toList();

		return new Ast.ClassType(getTokens(ctx), null, extendsAccess, members);
	}

	@Override
	public List<Ast.SymbolChain> visitClass_extends(AntlerScriptParser.Class_extendsContext ctx) {
		assert ctx != null;

		return ctx.symbol_chain().stream().map(this::visitSymbol_chain).toList();
	}

	@Override
	public Ast.SymbolChain visitSymbol_chain(AntlerScriptParser.Symbol_chainContext ctx) {
		assert ctx != null;

		return new Ast.SymbolChain(ctx.symbol().stream().map(RuleContext::getText).toList());
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

		Ast.Type type = ctx.type() == null ? null : visitType(ctx.type());
		Ast.Expression expr = ctx.expression() == null ? null : visitExpression(ctx.expression());

		return new Ast.ConstructorParameter(type, ctx.symbol().getText(), expr, false);
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
	public Ast.AliasClassMember visitAliasClassMember(AntlerScriptParser.AliasClassMemberContext ctx) {
		assert ctx != null;

		return visitAlias(ctx.alias());
	}

	@Override
	public Ast.ExtendsAssignClassMember visitExtendsClassMember(AntlerScriptParser.ExtendsClassMemberContext ctx) {
		assert ctx != null;

		return visitExtends_assign(ctx.extends_assign());
	}

	@Override
	public Ast.CastClassMember visitCast(AntlerScriptParser.CastContext ctx) {
		assert ctx != null;

		Ast.StatementBlock block = ctx.statement_block() == null ? null : visitStatement_block(ctx.statement_block());

		return new Ast.CastClassMember(getTokens(ctx), visitType(ctx.type()), block);
	}

	@Override
	public Ast.OperatorOverloadClassMember visitOperator_overload(AntlerScriptParser.Operator_overloadContext ctx) {
		assert ctx != null;

		Ast.StatementBlock block = ctx.statement_block() == null ? null : visitStatement_block(ctx.statement_block());

		return new Ast.OperatorOverloadClassMember(
			getTokens(ctx), visitOverridable(ctx.overridable()), visitType(ctx.rightType), ctx.symbol().getText(), visitType(ctx.returnType), block
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
	public Ast.AliasClassMember visitAlias(AntlerScriptParser.AliasContext ctx) {
		assert ctx != null;

		String target = ctx.target == null ? null : ctx.target.getText();
		var extendsAssign = ctx.extends_assign() == null ? null : visitExtends_assign(ctx.extends_assign());
		return new Ast.AliasClassMember(getTokens(ctx), visitSymbol_chain(ctx.symbol_chain()), ctx.origin.getText(), target, extendsAssign);
	}

	@Override
	public Ast.ExtendsAssignClassMember visitExtends_assign(AntlerScriptParser.Extends_assignContext ctx) {
		assert ctx != null;

		return new Ast.ExtendsAssignClassMember(getTokens(ctx), ctx.symbol().getText(), visitExpression(ctx.expression()));
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
	public Object visitType_or_right(AntlerScriptParser.Type_or_rightContext ctx) {
		assert ctx != null;

		// Should be handled by the caller
		assert false;
		return null;
	}

	@Override
	public Ast.Type visitType_and(AntlerScriptParser.Type_andContext ctx) {
		assert ctx != null;

		if (ctx.right == null) {
			return visitType_nullable(ctx.left);
		}

		return new Ast.UnionType(getTokens(ctx), Ast.UnionType.Kind.AND, visitType_nullable(ctx.left), visitType_and(ctx.right.type_and()));
	}

	@Override
	public Object visitType_and_right(AntlerScriptParser.Type_and_rightContext ctx) {
		assert ctx != null;

		// Should be handled by the caller
		assert false;
		return null;
	}

	@Override
	public Ast.Type visitType_nullable(AntlerScriptParser.Type_nullableContext ctx) {
		assert ctx != null;

		Ast.Type atomic = visitTypeAtomic(ctx.type_atomic());
		if (ctx.nullable == null) {
			return atomic;
		}

		List<Token> questionMark = Arrays.asList(ctx.nullable);
		return new Ast.UnionType(
			getTokens(ctx),
			Ast.UnionType.Kind.OR,
			atomic,
			new Ast.SymbolType(questionMark, "Null", null)
		);
	}

	// Helper, not an override
	public Ast.Type visitTypeAtomic(AntlerScriptParser.Type_atomicContext ctx) {
		assert ctx != null;

		return (Ast.Type) visit(ctx);
	}

	@Override
	public Ast.SymbolType visitSymbolType(AntlerScriptParser.SymbolTypeContext ctx) {
		assert ctx != null;

		List<Ast.Type> genericArgs = null;
		if (ctx.generic_args() != null) {
			genericArgs = visitGeneric_args(ctx.generic_args());
		}

		return new Ast.SymbolType(getTokens(ctx), ctx.symbol().getText(), genericArgs);
	}

	@Override
	public Ast.ArrayType visitArrayType(AntlerScriptParser.ArrayTypeContext ctx) {
		assert ctx != null;

		return visitArray_header(ctx.array_header());
	}

	@Override
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
	public Ast.CoroutineType visitCoroutineType(AntlerScriptParser.CoroutineTypeContext ctx) {
		assert ctx != null;

		return visitCoroutine_header(ctx.coroutine_header());
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
	public Ast.ArrayType visitArray_header(AntlerScriptParser.Array_headerContext ctx) {
		assert ctx != null;

		Ast.Type type = null;
		Ast.Expression size = null;

		if (ctx.type() != null) {
			assert ctx.expression() != null;
			type = visitType(ctx.type());
			size = visitExpression(ctx.expression());
		}

		return new Ast.ArrayType(getTokens(ctx), type, size);
	}

	@Override
	public Ast.FunctionType visitFunc_header(AntlerScriptParser.Func_headerContext ctx) {
		assert ctx != null;

		if (ctx.full != null) {
			return visitFunc_header_full(ctx.full);
		} else if (ctx.inferred != null) {
			return visitFunc_header_inferred(ctx.inferred);
		}

		assert(false);
		return null;
	}

	@Override
	public Ast.FullFunctionType visitFunc_header_full(AntlerScriptParser.Func_header_fullContext ctx) {
		assert ctx != null;

		List<Ast.GenericParameter> genericParams = null;
		if (ctx.generic_parameters() != null) {
			genericParams = visitGeneric_parameters(ctx.generic_parameters());
		}

		Ast.Type returnType = ctx.returnType == null
			? null
			: visitType(ctx.returnType);
		List<Ast.FunctionParameter> params = ctx.func_params() == null
			? null
			: visitFunc_params(ctx.func_params());
		Ast.Type errorType = ctx.errorType == null
			? null
			: visitType(ctx.errorType);

		return new Ast.FullFunctionType(getTokens(ctx), genericParams, params, returnType, errorType);
	}

	@Override
	public Ast.InferredFunctionType visitFunc_header_inferred(AntlerScriptParser.Func_header_inferredContext ctx) {
		assert ctx != null;

		List<String> parameters = ctx.symbol().stream().map(RuleContext::getText).toList();
		String varArgs = ctx.varargs == null
			? null
			: ctx.varargs.getText();
		boolean canThrow = ctx.EXCLAIM() != null;

		return new Ast.InferredFunctionType(getTokens(ctx), parameters, varArgs, canThrow);
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
	public Ast.CoroutineType visitCoroutine_header(AntlerScriptParser.Coroutine_headerContext ctx) {
		assert ctx != null;

		if (ctx.full != null) {
			return visitCoroutine_header_full(ctx.full);
		} else if (ctx.inferred != null) {
			return visitCoroutine_header_inferred(ctx.inferred);
		}

		assert(false);
		return null;
	}

	@Override
	public Ast.FullCoroutineType visitCoroutine_header_full (AntlerScriptParser.Coroutine_header_fullContext ctx) {
		assert ctx != null;

		List<Ast.GenericParameter> genericParams = null;
		if (ctx.generic_parameters() != null) {
			genericParams = visitGeneric_parameters(ctx.generic_parameters());
		}

		List<Ast.FunctionParameter> params = ctx.func_params() == null ? null : visitFunc_params(ctx.func_params());
		Ast.Type returnType = ctx.returnType == null ? null : visitType(ctx.returnType);

		Ast.Type yieldIn = null;
		Ast.Type yieldOut = null;
		if (ctx.coroutine_header_yield() != null) {
			AntlerScriptParser.Coroutine_header_yieldContext yieldCtx = ctx.coroutine_header_yield();
			yieldIn = yieldCtx.yieldIn == null ? null : visitType(yieldCtx.yieldIn);
			yieldOut = yieldCtx.yieldOut == null ? null : visitType(yieldCtx.yieldOut);
		}

		return new Ast.FullCoroutineType(getTokens(ctx), genericParams, params, returnType, yieldIn, yieldOut);
	}

	@Override
	public Ast.InferredCoroutineType visitCoroutine_header_inferred(AntlerScriptParser.Coroutine_header_inferredContext ctx) {
		assert ctx != null;

		List<String> parameters = ctx.symbol().stream().map(RuleContext::getText).toList();
		String varArgs = ctx.varargs == null
			? null
			: ctx.varargs.getText();
		boolean canYield = ctx.YIELD() != null;

		return new Ast.InferredCoroutineType(getTokens(ctx), parameters, varArgs, canYield);
	}

	@Override
	public Ast.CompositeExpression visitComposite(AntlerScriptParser.CompositeContext ctx) {
		assert ctx != null;

		List<Ast.Type> genericArgs = null;
		if (ctx.generic_args() != null) {
			genericArgs = visitGeneric_args(ctx.generic_args());
		}

		Ast.ListArgsOrKeyValuePairs list = null;

		if (ctx.keypair_list_map() != null) {
			list = new Ast.ListKeyValuePairs(visitKeypair_list_map(ctx.keypair_list_map()));
		} else if (ctx.arguments() != null) {
			list = new Ast.ListArgs(visitArguments(ctx.arguments()));
		}

		return new Ast.CompositeExpression(getTokens(ctx), genericArgs, list);
	}

	@Override
	public Ast.LambdaExpression visitLambda(AntlerScriptParser.LambdaContext ctx) {
		assert ctx != null;

		Ast.FunctionType type = visitFunc_header(ctx.func_header());
		Ast.StatementBlock block = visitStatement_block(ctx.statement_block());

		return new Ast.LambdaExpression(getTokens(ctx), type, block);
	}

	@Override
	public Ast.CoroutineExpression visitCoroutine(AntlerScriptParser.CoroutineContext ctx) {
		assert ctx != null;

		Ast.CoroutineType type = visitCoroutine_header(ctx.coroutine_header());
		Ast.StatementBlock block = visitStatement_block(ctx.statement_block());

		return new Ast.CoroutineExpression(getTokens(ctx), type, block);
	}

	@Override
	public Ast.ClassType visitClass_header(AntlerScriptParser.Class_headerContext ctx) {
		assert ctx != null;

		List<Ast.GenericParameter> params = null;
		if (ctx.generic_parameters() != null) {
			params = visitGeneric_parameters(ctx.generic_parameters());
		}

		if (ctx.class_header_inside() == null) {
			return new Ast.ClassType(getTokens(ctx), params, null, null);
		}

		AntlerScriptParser.Class_header_insideContext ctx2 = ctx.class_header_inside();
		List<Ast.SymbolChain> extendsAccess = ctx2.class_extends() == null
			? null
			: visitClass_extends(ctx2.class_extends());
		List<Ast.ClassMember> members = ctx2.class_member().stream().map(this::visitClassMember).toList();

		return new Ast.ClassType(getTokens(ctx), params, extendsAccess, members);
	}

	@Override
	public Ast.EnumType visitEnum_header(AntlerScriptParser.Enum_headerContext ctx) {
		assert ctx != null;

		var ctx2 = ctx.enum_header_inside();

		Ast.SymbolChain extendsAccess = ctx2.symbol_chain() == null ? null : visitSymbol_chain(ctx2.symbol_chain());
		List<String> memberSymbols = ctx2.symbol().stream().map(AntlerScriptParser.SymbolContext::getText).toList();

		return new Ast.EnumType(getTokens(ctx), extendsAccess, memberSymbols);
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

		return visitExpression_yield(ctx.expression_yield());
	}

	// If the tokens of the yield expression are wrong, replace this grammar
	// rule with a recursive one
	@Override
	public Ast.Expression visitExpression_yield(AntlerScriptParser.Expression_yieldContext ctx) {
		assert ctx != null;

		Ast.Expression expr = visitExpression_assignment(ctx.expression_assignment());
		int yieldCount = ctx.YIELD() == null ? 0 : ctx.YIELD().size();

		if (yieldCount <= 0) {
			return expr;
		}

		List<Token> allTokens = getTokens(ctx);
		List<Token> tokens = new ArrayList<>(getTokens(ctx.expression_assignment()));

		for (int i = 0; i < yieldCount; i++) {
			tokens.add(0, allTokens.get(yieldCount - i - 1));
			expr = new Ast.YieldExpression(new ArrayList<>(tokens), expr);
		}

		return expr;
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
			getTokens(ctx),
			visitExpression_assignment_right(right),
			visitExpression_logical_or(left),
			visitExpression_assignment(right.expression_assignment())
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
			case AntlerScriptParser.PERCENT_EQ -> Ast.BinaryExpression.Kind.REMAINDER_ASSIGN;
			case AntlerScriptParser.DOUBLE_PERCENT_EQ -> Ast.BinaryExpression.Kind.MODULO_ASSIGN;
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

		List<Token> tokens = getTokens(ctx.expression_logical_and());

		for (AntlerScriptParser.Expression_logical_or_rightContext logicalOrCtx : ctx.expression_logical_or_right()) {
			var acc = getTokens(logicalOrCtx);
			tokens.addAll(acc);
			latest = new Ast.BinaryExpression(new ArrayList<Token>(tokens), visitExpression_logical_or_right(logicalOrCtx), latest, visitExpression_logical_and(logicalOrCtx.expression_logical_and()));
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

		List<Token> tokens = getTokens(ctx.expression_logical_not());

		for (AntlerScriptParser.Expression_logical_and_rightContext logicalAndCtx : ctx.expression_logical_and_right()) {
			var acc = getTokens(logicalAndCtx);
			tokens.addAll(acc);
			latest = new Ast.BinaryExpression(new ArrayList<Token>(tokens), visitExpression_logical_and_right(logicalAndCtx), latest, visitExpression_logical_not(logicalAndCtx.expression_logical_not()));
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

		if (ctx.operand != null) {
			return visitExpression_cmp(ctx.operand);
		}

		return new Ast.UnaryExpression(
			getTokens(ctx),
			Ast.UnaryExpression.Kind.NOT,
			visitExpression_logical_not(ctx.recursiveOperand)
		);
	}

	@Override
	public Ast.Expression visitExpression_cmp(AntlerScriptParser.Expression_cmpContext ctx) {
		assert ctx != null;

		Ast.Expression latest = visitExpression_func_pipe(ctx.expression_func_pipe());

		List<Token> tokens = getTokens(ctx.expression_func_pipe());

		for (AntlerScriptParser.Expression_cmp_rightContext cmpCtx : ctx.expression_cmp_right()) {
			var acc = getTokens(cmpCtx);
			tokens.addAll(acc);
			latest = new Ast.BinaryExpression(new ArrayList<Token>(tokens), visitExpression_cmp_right(cmpCtx), latest, visitExpression_func_pipe(cmpCtx.expression_func_pipe()));
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
			case AntlerScriptParser.AS -> Ast.BinaryExpression.Kind.AS;
			default -> null;
		};

		assert kind != null;

		return kind;
	}

	@Override
	public Ast.Expression visitExpression_func_pipe(AntlerScriptParser.Expression_func_pipeContext ctx) {
		assert ctx != null;

		Ast.Expression latest = visitExpression_bit_or(ctx.expression_bit_or());

		List<Token> tokens = getTokens(ctx.expression_bit_or());

		for (AntlerScriptParser.Expression_func_pipe_rightContext funcPipeCtx : ctx.expression_func_pipe_right()) {
			var acc = getTokens(funcPipeCtx);
			tokens.addAll(acc);
			latest = new Ast.BinaryExpression(new ArrayList<Token>(tokens), visitExpression_func_pipe_right(funcPipeCtx), latest, visitExpression_bit_or(funcPipeCtx.expression_bit_or()));
		}

		return latest;
	}

	@Override
	public Ast.BinaryExpression.Kind visitExpression_func_pipe_right(AntlerScriptParser.Expression_func_pipe_rightContext ctx) {
		assert ctx != null;

		Token op = ctx.operator;

		Ast.BinaryExpression.Kind kind = switch (op.getType()) {
			case AntlerScriptParser.FUNC_PIPE -> Ast.BinaryExpression.Kind.FUNC_PIPE;
			default -> null;
		};

		assert kind != null;

		return kind;
	}

	@Override
	public Ast.Expression visitExpression_bit_or(AntlerScriptParser.Expression_bit_orContext ctx) {
		assert ctx != null;

		Ast.Expression latest = visitExpression_bit_xor(ctx.expression_bit_xor());

		List<Token> tokens = getTokens(ctx.expression_bit_xor());

		for (AntlerScriptParser.Expression_bit_or_rightContext bitOrCtx : ctx.expression_bit_or_right()) {
			var acc = getTokens(bitOrCtx);
			tokens.addAll(acc);
			latest = new Ast.BinaryExpression(new ArrayList<Token>(tokens), visitExpression_bit_or_right(bitOrCtx), latest, visitExpression_bit_xor(bitOrCtx.expression_bit_xor()));
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

		List<Token> tokens = getTokens(ctx.expression_bit_and());

		for (AntlerScriptParser.Expression_bit_xor_rightContext bitXorCtx : ctx.expression_bit_xor_right()) {
			var acc = getTokens(bitXorCtx);
			tokens.addAll(acc);
			latest = new Ast.BinaryExpression(new ArrayList<Token>(tokens), visitExpression_bit_xor_right(bitXorCtx), latest, visitExpression_bit_and(bitXorCtx.expression_bit_and()));
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

		List<Token> tokens = getTokens(ctx.expression_bit_shift());

		for (AntlerScriptParser.Expression_bit_and_rightContext bitAndCtx : ctx.expression_bit_and_right()) {
			var acc = getTokens(bitAndCtx);
			tokens.addAll(acc);
			latest = new Ast.BinaryExpression(new ArrayList<Token>(tokens), visitExpression_bit_and_right(bitAndCtx), latest, visitExpression_bit_shift(bitAndCtx.expression_bit_shift()));
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

		List<Token> tokens = getTokens(ctx.expression_add());

		for (AntlerScriptParser.Expression_bit_shift_rightContext bitShiftCtx : ctx.expression_bit_shift_right()) {
			var acc = getTokens(bitShiftCtx);
			tokens.addAll(acc);
			latest = new Ast.BinaryExpression(new ArrayList<Token>(tokens), visitExpression_bit_shift_right(bitShiftCtx), latest, visitExpression_add(bitShiftCtx.expression_add()));
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

		List<Token> tokens = getTokens(ctx.expression_mult());

		for (AntlerScriptParser.Expression_add_rightContext addCtx : ctx.expression_add_right()) {
			var acc = getTokens(addCtx);
			tokens.addAll(acc);
			latest = new Ast.BinaryExpression(new ArrayList<Token>(tokens), visitExpression_add_right(addCtx), latest, visitExpression_mult(addCtx.expression_mult()));
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

		List<Token> tokens = getTokens(ctx.expression_unary());

		for (AntlerScriptParser.Expression_mult_rightContext multCtx : ctx.expression_mult_right()) {
			var acc = getTokens(multCtx);
			tokens.addAll(acc);
			latest = new Ast.BinaryExpression(new ArrayList<Token>(tokens), visitExpression_mult_right(multCtx), latest, visitExpression_unary(multCtx.expression_unary()));
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
			case AntlerScriptParser.PERCENT -> Ast.BinaryExpression.Kind.REMAINDER;
			case AntlerScriptParser.DOUBLE_PERCENT -> Ast.BinaryExpression.Kind.MODULO;
			default -> null;
		};

		assert kind != null;

		return kind;
	}

	// BUG: Token collection strategy is likely wrong, switch the grammar
	// rule to being recursive instead to fix or see the yield target
	@Override
	public Ast.Expression visitExpression_unary(AntlerScriptParser.Expression_unaryContext ctx) {
		assert ctx != null;

		if (ctx.operand != null) {
			return visitExpression_exp(ctx.operand);
		}

		Ast.UnaryExpression.Kind kind = switch (ctx.operator.getType()) {
		case AntlerScriptParser.PLUS -> Ast.UnaryExpression.Kind.PLUS;
		case AntlerScriptParser.MINUS -> Ast.UnaryExpression.Kind.MINUS;
		case AntlerScriptParser.TILDE -> Ast.UnaryExpression.Kind.BIT_NOT;
		default -> null;
		};

		assert kind != null;

		return new Ast.UnaryExpression(
			getTokens(ctx),
			kind,
			visitExpression_unary(ctx.recursiveOperand)
		);
	}

	@Override
	public Ast.Expression visitExpression_exp(AntlerScriptParser.Expression_expContext ctx) {
		assert ctx != null;

		Ast.Expression latest = visitExpression_postfix(ctx.expression_postfix());

		List<Token> tokens = getTokens(ctx.expression_postfix());

		for (AntlerScriptParser.Expression_exp_rightContext expCtx : ctx.expression_exp_right()) {
			var acc = getTokens(expCtx);
			tokens.addAll(acc);
			latest = new Ast.BinaryExpression(new ArrayList<Token>(tokens), Ast.BinaryExpression.Kind.EXPONENT, latest, visitExpression_postfix(expCtx.expression_postfix()));
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
			case AntlerScriptParser.IndexAccessContext idxCtx: {
				List<Ast.Type> genericArgs = null;
				if (idxCtx.generic_args() != null) {
					genericArgs = visitGeneric_args(idxCtx.generic_args());
				}
				latest = new Ast.IndexExpression(getTokens(ctx), latest, visitExpression(idxCtx.expression()), genericArgs);
				break;
			}
			case AntlerScriptParser.FunctionCallContext funcallCtx: {
				latest = new Ast.CallExpression(getTokens(ctx), latest, funcallCtx.arguments() == null ? null : visitArguments(funcallCtx.arguments()));
				break;
			}
			case AntlerScriptParser.MemberAccessContext memCtx: {
				List<Ast.Type> genericArgs = null;
				if (memCtx.generic_args() != null) {
					genericArgs = visitGeneric_args(memCtx.generic_args());
				}
				latest = new Ast.AccessExpression(getTokens(ctx), latest, memCtx.symbol().getText(), false, genericArgs);
				break;
			}
			case AntlerScriptParser.NullishAccessContext nullCtx: {
				List<Ast.Type> genericArgs = null;
				if (nullCtx.generic_args() != null) {
					genericArgs = visitGeneric_args(nullCtx.generic_args());
				}
				latest = new Ast.AccessExpression(getTokens(ctx), latest, nullCtx.symbol().getText(), true, genericArgs);
				break;
			}
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

		String symbol = ctx.symbol() == null ? null : ctx.symbol().getText();

		return new Ast.Argument(visitExpression(ctx.expression()), symbol, false);
	}

	@Override
	public Ast.SymbolExpression visitSymbolExpression(AntlerScriptParser.SymbolExpressionContext ctx) {
		assert ctx != null;

		List<Ast.Type> genericArgs = null;
		if (ctx.generic_args() != null) {
			genericArgs = visitGeneric_args(ctx.generic_args());
		}

		return new Ast.SymbolExpression(getTokens(ctx), ctx.symbol().getText(), genericArgs);
	}

	@Override
	public Ast.StringExpression visitStringExpression(AntlerScriptParser.StringExpressionContext ctx) {
		assert ctx != null;

		String string = ctx.STRING().getText();

		assert string.length() >= 2;
		assert string.charAt(0) == '"';
		assert string.charAt(string.length() - 1) == '"';

		return new Ast.StringExpression(getTokens(ctx), string.substring(1, string.length() - 1), false);
	}

	@Override
	public Ast.StringExpression visitRawStringExpression(AntlerScriptParser.RawStringExpressionContext ctx) {
		assert ctx != null;

		String string = ctx.RAW_STRING().getText();

		assert string.length() >= 2;
		assert string.charAt(0) == '`';
		assert string.charAt(string.length() - 1) == '`';

		return new Ast.StringExpression(getTokens(ctx), string.substring(1, string.length() - 1), true);
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
	public Ast.CompositeExpression visitCompositeExpression(AntlerScriptParser.CompositeExpressionContext ctx) {
		assert ctx != null;

		return visitComposite(ctx.composite());
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
	public Ast.CoroutineExpression visitCoroutineExpression(AntlerScriptParser.CoroutineExpressionContext ctx) {
		assert ctx != null;

		return visitCoroutine(ctx.coroutine());
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
	public Ast.NewArrayExpression visitNew_array_instance(AntlerScriptParser.New_array_instanceContext ctx) {
		assert ctx != null;

		Ast.ArrayType type = visitArray_header(ctx.array_header());
		List<Ast.Argument> args = visitObject_instantiation_args(ctx.object_instantiation_args());
		return new Ast.NewArrayExpression(getTokens(ctx), type, args);
	}

	@Override
	public Ast.NewClassInstance visitNew_class_instance(AntlerScriptParser.New_class_instanceContext ctx) {
		assert ctx != null;

		List<Ast.Type> genericArgs = null;
		if (ctx.generic_args() != null) {
			genericArgs = visitGeneric_args(ctx.generic_args());
		}

		List<Ast.Argument> args = visitObject_instantiation_args(ctx.object_instantiation_args());
		Ast.ClassType type = visitClass_header(ctx.class_header());
		return new Ast.NewClassInstance(getTokens(ctx), type, genericArgs, args);
	}

	@Override
	public List<Ast.Argument> visitObject_instantiation_args(AntlerScriptParser.Object_instantiation_argsContext ctx) {
		assert ctx != null;

		if (ctx.arguments() == null) {
			return new ArrayList<>();
		}

		return visitArguments(ctx.arguments());
	}

	@Override
	public Ast.NewMapExpression visitNew_map_instance(AntlerScriptParser.New_map_instanceContext ctx) {
		assert ctx != null;

		Ast.Type keyType = null;
		Ast.Type valueType = null;
		List<Ast.KeyValuePair> pairs = null;

		if (ctx.key != null || ctx.value != null) {
			assert ctx.key != null && ctx.value != null;
			keyType = visitType(ctx.key);
			valueType = visitType(ctx.value);
		}

		if (ctx.keypair_list_map() != null) {
			pairs = visitKeypair_list_map(ctx.keypair_list_map());
		}

		return new Ast.NewMapExpression(getTokens(ctx), keyType, valueType, pairs);
	}

	@Override
	public Ast.SelectExpression visitSelect(AntlerScriptParser.SelectContext ctx) {
		assert ctx != null;

		Ast.Expression expr = null;
		if (ctx.value != null) {
			expr = visitExpression(ctx.value);
		}

		return new Ast.SelectExpression(getTokens(ctx), expr, visitKeypair_list_select(ctx.keypair_list_select()));
	}

	@Override
	public Ast.NewObjectLiteralExpression visitObjectLiteralExpression(AntlerScriptParser.ObjectLiteralExpressionContext ctx) {
		assert ctx != null;

		return visitObject_literal(ctx.object_literal());
	}

	@Override
	public Ast.NewObjectLiteralExpression visitObject_literal(AntlerScriptParser.Object_literalContext ctx) {
		assert ctx != null;

		Ast.ClassType topLevel = ctx.class_top_level() == null
			? new Ast.ClassType(getTokens(ctx), null, null, null)
			: visitClass_top_level(ctx.class_top_level());

		return new Ast.NewObjectLiteralExpression(getTokens(ctx), topLevel);
	}

	@Override
	public Ast.TryElseExpression visitTryElseExpression(AntlerScriptParser.TryElseExpressionContext ctx) {
		assert ctx != null;

		return visitTry_else(ctx.try_else());
	}

	@Override
	public Ast.TryElseExpression visitTry_else(AntlerScriptParser.Try_elseContext ctx) {
		assert ctx != null;

		Ast.Expression call = visitExpression_postfix(ctx.expression_postfix());
		String caught = ctx.symbol() == null ? null : ctx.symbol().getText();
		Ast.StatementBlock block = ctx.statement_block() == null ? null : visitStatement_block(ctx.statement_block());

		return new Ast.TryElseExpression(getTokens(ctx), call, caught, block);
	}

	@Override
	public List<Ast.KeyValuePair> visitKeypair_list_select(AntlerScriptParser.Keypair_list_selectContext ctx) {
		assert ctx != null;

		List<Ast.KeyValuePair> keypairList = new ArrayList<>(ctx.keypair_clause().stream().map(this::visitKeypair_clause).toList());

		if (ctx.elseClause != null) {
			assert ctx.elseToken != null;

			List<Token> elseToken = Arrays.asList(ctx.elseToken);

			Ast.KeyValuePair pair = new Ast.KeyValuePair(
				new Ast.BooleanExpression(elseToken, true),
				visitExpression(ctx.elseClause)
			);

			keypairList.add(pair);
		}

		return keypairList;
	}

	@Override
	public List<Ast.KeyValuePair> visitKeypair_list_map(AntlerScriptParser.Keypair_list_mapContext ctx) {
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

		Ast.Expression expression = ctx.expression() == null
			? null
			: visitExpression(ctx.expression());
		return new Ast.ReturnStatement(getTokens(ctx), expression);
	}

	@Override
	public Ast.LoopStatement visitLoopStatement(AntlerScriptParser.LoopStatementContext ctx) {
		assert ctx != null;

		return visitLoop(ctx.loop());
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

		List<Token> tokens = getTokens(ctx);
		Ast.StatementBlock block = visitStatement_block(ctx.block);

		if (ctx.loop_header_inside() == null) {
			if (ctx.loop_while() == null) {
				return new Ast.LoopInfiniteStatement(tokens, block, null);
			}

			Ast.Expression test = visitLoop_while(ctx.loop_while());
			return new Ast.LoopWhileStatement(tokens, block, test, null);
		}

		return visitLoopHeaderInside(ctx.loop_header_inside(), tokens, block);
	}

	// Helper, not an override
	public Ast.LoopStatement visitLoopHeaderInside(AntlerScriptParser.Loop_header_insideContext ctx, List<Token> tokens, Ast.StatementBlock block) {
		assert ctx != null;
		assert tokens != null;
		assert block != null;

		// While
		Ast.Expression test = null;
		Ast.LoopStatement.TestPosition testPosition = null;
		if (ctx.left_while != null) {
			test = visitLoop_while(ctx.left_while);
			testPosition = Ast.LoopStatement.TestPosition.LEFT;
		} else if (ctx.right_while != null) {
			test = visitLoop_while(ctx.right_while);
			testPosition = Ast.LoopStatement.TestPosition.RIGHT;
		}

		// Infinite loops
		if (ctx.loop_capture() != null) {
			String capture = visitLoop_capture(ctx.loop_capture());
			if (test == null) {
				return new Ast.LoopInfiniteStatement(tokens, block, capture);
			}
			return new Ast.LoopWhileStatement(tokens, block, test, capture);
		}

		// Ranges
		if (ctx.loop_range() != null) {
			AntlerScriptParser.Loop_rangeContext loopCtx = ctx.loop_range();
			String capture = null;
			Ast.Expression from = null;
			Ast.Expression to = null;
			Ast.Expression by = null;

			if (loopCtx.loop_capture() != null) {
				capture = visitLoop_capture(loopCtx.loop_capture());
			}
			if (loopCtx.from != null) {
				from = visitExpression(loopCtx.from);
			}
			if (loopCtx.to != null) {
				to = visitExpression(loopCtx.to);
			}
			if (loopCtx.by != null) {
				by = visitExpression(loopCtx.by);
			}

			return new Ast.LoopRangeStatement(tokens, block, capture, from, to, by, test, testPosition);
		}

		// Collection iteration
		if (ctx.loop_iteration() != null) {
			AntlerScriptParser.Loop_iterationContext loopCtx = ctx.loop_iteration();
			Ast.Expression collection = visitExpression(loopCtx.collection);
			String indexCapture = null;
			String elementCapture = null;

			if (loopCtx.loop_capture_2() != null) {
				AntlerScriptParser.Loop_capture_2Context capCtx = loopCtx.loop_capture_2();
				if (capCtx.right != null) {
					elementCapture = capCtx.right.getText();
					indexCapture = capCtx.left.getText();
				} else {
					elementCapture = capCtx.left.getText();
				}
			}

			return new Ast.LoopIterationStatement(tokens, block, collection, indexCapture, elementCapture, test, testPosition);
		}

		// Can't reach here, should have returned earlier
		assert false;
		return null;
	}

	@Override
	public String visitLoop_capture(AntlerScriptParser.Loop_captureContext ctx) {
		assert ctx != null;

		return ctx.symbol().getText();
	}

	@Override
	public Ast.Expression visitLoop_while(AntlerScriptParser.Loop_whileContext ctx) {
		assert ctx != null;

		return visitExpression(ctx.expression());
	}

	@Override
	public Ast.VariableDeclaration visitLetDeclaration(AntlerScriptParser.LetDeclarationContext ctx) {
		assert ctx != null;

		Ast.Type type = visitType(ctx.type());
		String name = ctx.variableName.getText();
		List<Ast.Decorator> decorators = ctx.decorator_chain() == null
			? null
			: visitDecorator_chain(ctx.decorator_chain());

		return new Ast.VariableDeclaration(
			getTokens(ctx), false, ctx.isMutable != null, ctx.isSealed != null, type, name, null, decorators
		);
	}

	@Override
	public Ast.VariableDeclaration visitLetDefinition(AntlerScriptParser.LetDefinitionContext ctx) {
		assert ctx != null;

		Ast.Type type = ctx.type() != null ? visitType(ctx.type()) : null;
		Ast.Expression expression = visitExpression(ctx.expression());
		String name = ctx.variableName.getText();
		List<Ast.Decorator> decorators = ctx.decorator_chain() == null
			? null
			: visitDecorator_chain(ctx.decorator_chain());

		return new Ast.VariableDeclaration(
			getTokens(ctx), false, ctx.isMutable != null, ctx.isSealed != null, type, name, expression, decorators
		);
	}

	@Override
	public Ast.VariableDeclaration visitConstDefinition(AntlerScriptParser.ConstDefinitionContext ctx) {
		assert ctx != null;

		Ast.Type type = ctx.type() != null ? visitType(ctx.type()) : null;
		Ast.Expression expression = visitExpression(ctx.expression());
		String name = ctx.variableName.getText();
		List<Ast.Decorator> decorators = ctx.decorator_chain() == null
			? null
			: visitDecorator_chain(ctx.decorator_chain());

		return new Ast.VariableDeclaration(
			getTokens(ctx), true, false, false, type, name, expression, decorators
		);
	}

	@Override
	public Ast.Decorator visitDecorator(AntlerScriptParser.DecoratorContext ctx) {
		assert ctx != null;

		Ast.SymbolChain symbolChain = visitSymbol_chain(ctx.symbol_chain());
		List<Ast.Argument> arguments = ctx.arguments() == null
			? null
			: visitArguments(ctx.arguments());

		return new Ast.Decorator(symbolChain, arguments);
	}

	@Override
	public List<Ast.Decorator> visitDecorator_chain(AntlerScriptParser.Decorator_chainContext ctx) {
		assert ctx != null;

		return ctx.decorator().stream().map(this::visitDecorator).toList();
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
		Ast.StatementBlock elseBranch = ctx.else_() == null
			? null
			: visitElse(ctx.else_());

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

		Ast.StatementBlock defaultCase = ctx.catchAll == null
			? null
			: visitElse(ctx.catchAll);

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

	@Override
	public Ast.ThrowStatement visitThrow(AntlerScriptParser.ThrowContext ctx) {
		assert ctx != null;

		return new Ast.ThrowStatement(getTokens(ctx), visitExpression(ctx.expression()));
	}

	@Override
	public Ast.ThrowStatement visitThrowStatement(AntlerScriptParser.ThrowStatementContext ctx) {
		assert ctx != null;

		return visitThrow(ctx.throw_());
	}
}
