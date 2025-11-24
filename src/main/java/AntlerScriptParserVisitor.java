// Generated from AntlerScriptParser.g4 by ANTLR 4.13.2
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link AntlerScriptParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface AntlerScriptParserVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#symbol}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSymbol(AntlerScriptParser.SymbolContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#semicolon}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSemicolon(AntlerScriptParser.SemicolonContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProgram(AntlerScriptParser.ProgramContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#main_program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMain_program(AntlerScriptParser.Main_programContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#class_program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClass_program(AntlerScriptParser.Class_programContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#namespace_program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNamespace_program(AntlerScriptParser.Namespace_programContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#namespace_member}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNamespace_member(AntlerScriptParser.Namespace_memberContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#class_top_level}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClass_top_level(AntlerScriptParser.Class_top_levelContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#class_header_inside}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClass_header_inside(AntlerScriptParser.Class_header_insideContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#class_extends}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClass_extends(AntlerScriptParser.Class_extendsContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#class_extends_access}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClass_extends_access(AntlerScriptParser.Class_extends_accessContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#constructor}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstructor(AntlerScriptParser.ConstructorContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#constructor_params}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstructor_params(AntlerScriptParser.Constructor_paramsContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#constructor_params_elm}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstructor_params_elm(AntlerScriptParser.Constructor_params_elmContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#var_args}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVar_args(AntlerScriptParser.Var_argsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code castClassMember}
	 * labeled alternative in {@link AntlerScriptParser#class_member}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCastClassMember(AntlerScriptParser.CastClassMemberContext ctx);
	/**
	 * Visit a parse tree produced by the {@code declarationClassMember}
	 * labeled alternative in {@link AntlerScriptParser#class_member}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDeclarationClassMember(AntlerScriptParser.DeclarationClassMemberContext ctx);
	/**
	 * Visit a parse tree produced by the {@code operatorOverloadClassMember}
	 * labeled alternative in {@link AntlerScriptParser#class_member}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOperatorOverloadClassMember(AntlerScriptParser.OperatorOverloadClassMemberContext ctx);
	/**
	 * Visit a parse tree produced by the {@code constructorClassMember}
	 * labeled alternative in {@link AntlerScriptParser#class_member}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstructorClassMember(AntlerScriptParser.ConstructorClassMemberContext ctx);
	/**
	 * Visit a parse tree produced by the {@code captureClassMember}
	 * labeled alternative in {@link AntlerScriptParser#class_member}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCaptureClassMember(AntlerScriptParser.CaptureClassMemberContext ctx);
	/**
	 * Visit a parse tree produced by the {@code extendsClassMember}
	 * labeled alternative in {@link AntlerScriptParser#class_member}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExtendsClassMember(AntlerScriptParser.ExtendsClassMemberContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#cast}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCast(AntlerScriptParser.CastContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#operator_overload}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOperator_overload(AntlerScriptParser.Operator_overloadContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#overridable}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOverridable(AntlerScriptParser.OverridableContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#capture}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCapture(AntlerScriptParser.CaptureContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#extends_assign}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExtends_assign(AntlerScriptParser.Extends_assignContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#enum_header_inside}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEnum_header_inside(AntlerScriptParser.Enum_header_insideContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType(AntlerScriptParser.TypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#type_or}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType_or(AntlerScriptParser.Type_orContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#type_and}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType_and(AntlerScriptParser.Type_andContext ctx);
	/**
	 * Visit a parse tree produced by the {@code symbolType}
	 * labeled alternative in {@link AntlerScriptParser#type_atomic}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSymbolType(AntlerScriptParser.SymbolTypeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code listType}
	 * labeled alternative in {@link AntlerScriptParser#type_atomic}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitListType(AntlerScriptParser.ListTypeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code arrayType}
	 * labeled alternative in {@link AntlerScriptParser#type_atomic}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArrayType(AntlerScriptParser.ArrayTypeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code mapType}
	 * labeled alternative in {@link AntlerScriptParser#type_atomic}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMapType(AntlerScriptParser.MapTypeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code classType}
	 * labeled alternative in {@link AntlerScriptParser#type_atomic}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClassType(AntlerScriptParser.ClassTypeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code enumType}
	 * labeled alternative in {@link AntlerScriptParser#type_atomic}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEnumType(AntlerScriptParser.EnumTypeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code funcType}
	 * labeled alternative in {@link AntlerScriptParser#type_atomic}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFuncType(AntlerScriptParser.FuncTypeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code selfType}
	 * labeled alternative in {@link AntlerScriptParser#type_atomic}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelfType(AntlerScriptParser.SelfTypeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code typeGroup}
	 * labeled alternative in {@link AntlerScriptParser#type_atomic}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeGroup(AntlerScriptParser.TypeGroupContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#list_header}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitList_header(AntlerScriptParser.List_headerContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#array_header}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArray_header(AntlerScriptParser.Array_headerContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#map_header}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMap_header(AntlerScriptParser.Map_headerContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#func_header}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunc_header(AntlerScriptParser.Func_headerContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#func_params}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunc_params(AntlerScriptParser.Func_paramsContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#func_param_elm}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunc_param_elm(AntlerScriptParser.Func_param_elmContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#lambda}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLambda(AntlerScriptParser.LambdaContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#class_header}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClass_header(AntlerScriptParser.Class_headerContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#enum_header}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEnum_header(AntlerScriptParser.Enum_headerContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression(AntlerScriptParser.ExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#expression_assignment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression_assignment(AntlerScriptParser.Expression_assignmentContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#expression_assignment_right}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression_assignment_right(AntlerScriptParser.Expression_assignment_rightContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#expression_logical_or}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression_logical_or(AntlerScriptParser.Expression_logical_orContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#expression_logical_or_right}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression_logical_or_right(AntlerScriptParser.Expression_logical_or_rightContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#expression_logical_and}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression_logical_and(AntlerScriptParser.Expression_logical_andContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#expression_logical_and_right}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression_logical_and_right(AntlerScriptParser.Expression_logical_and_rightContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#expression_logical_not}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression_logical_not(AntlerScriptParser.Expression_logical_notContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#expression_cmp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression_cmp(AntlerScriptParser.Expression_cmpContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#expression_cmp_right}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression_cmp_right(AntlerScriptParser.Expression_cmp_rightContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#expression_bit_or}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression_bit_or(AntlerScriptParser.Expression_bit_orContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#expression_bit_or_right}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression_bit_or_right(AntlerScriptParser.Expression_bit_or_rightContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#expression_bit_xor}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression_bit_xor(AntlerScriptParser.Expression_bit_xorContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#expression_bit_xor_right}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression_bit_xor_right(AntlerScriptParser.Expression_bit_xor_rightContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#expression_bit_and}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression_bit_and(AntlerScriptParser.Expression_bit_andContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#expression_bit_and_right}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression_bit_and_right(AntlerScriptParser.Expression_bit_and_rightContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#expression_bit_shift}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression_bit_shift(AntlerScriptParser.Expression_bit_shiftContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#expression_bit_shift_right}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression_bit_shift_right(AntlerScriptParser.Expression_bit_shift_rightContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#expression_add}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression_add(AntlerScriptParser.Expression_addContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#expression_add_right}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression_add_right(AntlerScriptParser.Expression_add_rightContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#expression_mult}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression_mult(AntlerScriptParser.Expression_multContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#expression_mult_right}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression_mult_right(AntlerScriptParser.Expression_mult_rightContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#expression_unary}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression_unary(AntlerScriptParser.Expression_unaryContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#expression_unary_op}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression_unary_op(AntlerScriptParser.Expression_unary_opContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#expression_exp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression_exp(AntlerScriptParser.Expression_expContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#expression_exp_right}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression_exp_right(AntlerScriptParser.Expression_exp_rightContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#expression_postfix}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression_postfix(AntlerScriptParser.Expression_postfixContext ctx);
	/**
	 * Visit a parse tree produced by the {@code indexAccess}
	 * labeled alternative in {@link AntlerScriptParser#expression_access}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIndexAccess(AntlerScriptParser.IndexAccessContext ctx);
	/**
	 * Visit a parse tree produced by the {@code functionCall}
	 * labeled alternative in {@link AntlerScriptParser#expression_access}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionCall(AntlerScriptParser.FunctionCallContext ctx);
	/**
	 * Visit a parse tree produced by the {@code memberAccess}
	 * labeled alternative in {@link AntlerScriptParser#expression_access}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMemberAccess(AntlerScriptParser.MemberAccessContext ctx);
	/**
	 * Visit a parse tree produced by the {@code nullishAccess}
	 * labeled alternative in {@link AntlerScriptParser#expression_access}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNullishAccess(AntlerScriptParser.NullishAccessContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#arguments}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArguments(AntlerScriptParser.ArgumentsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code discardArgument}
	 * labeled alternative in {@link AntlerScriptParser#argument_elm}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDiscardArgument(AntlerScriptParser.DiscardArgumentContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expressionArgument}
	 * labeled alternative in {@link AntlerScriptParser#argument_elm}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpressionArgument(AntlerScriptParser.ExpressionArgumentContext ctx);
	/**
	 * Visit a parse tree produced by the {@code symbolExpression}
	 * labeled alternative in {@link AntlerScriptParser#expression_atom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSymbolExpression(AntlerScriptParser.SymbolExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code stringExpression}
	 * labeled alternative in {@link AntlerScriptParser#expression_atom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStringExpression(AntlerScriptParser.StringExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code rawStringExpression}
	 * labeled alternative in {@link AntlerScriptParser#expression_atom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRawStringExpression(AntlerScriptParser.RawStringExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code floatExpression}
	 * labeled alternative in {@link AntlerScriptParser#expression_atom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFloatExpression(AntlerScriptParser.FloatExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code integerExpression}
	 * labeled alternative in {@link AntlerScriptParser#expression_atom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIntegerExpression(AntlerScriptParser.IntegerExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code trueExpression}
	 * labeled alternative in {@link AntlerScriptParser#expression_atom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTrueExpression(AntlerScriptParser.TrueExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code falseExpression}
	 * labeled alternative in {@link AntlerScriptParser#expression_atom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFalseExpression(AntlerScriptParser.FalseExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code nullExpression}
	 * labeled alternative in {@link AntlerScriptParser#expression_atom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNullExpression(AntlerScriptParser.NullExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code superExpression}
	 * labeled alternative in {@link AntlerScriptParser#expression_atom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSuperExpression(AntlerScriptParser.SuperExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code selfInstanceExpression}
	 * labeled alternative in {@link AntlerScriptParser#expression_atom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelfInstanceExpression(AntlerScriptParser.SelfInstanceExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code newObjectExpression}
	 * labeled alternative in {@link AntlerScriptParser#expression_atom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNewObjectExpression(AntlerScriptParser.NewObjectExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code newListExpression}
	 * labeled alternative in {@link AntlerScriptParser#expression_atom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNewListExpression(AntlerScriptParser.NewListExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code newArrayExpression}
	 * labeled alternative in {@link AntlerScriptParser#expression_atom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNewArrayExpression(AntlerScriptParser.NewArrayExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code newMapExpression}
	 * labeled alternative in {@link AntlerScriptParser#expression_atom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNewMapExpression(AntlerScriptParser.NewMapExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code newClassInstance}
	 * labeled alternative in {@link AntlerScriptParser#expression_atom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNewClassInstance(AntlerScriptParser.NewClassInstanceContext ctx);
	/**
	 * Visit a parse tree produced by the {@code lambdaExpression}
	 * labeled alternative in {@link AntlerScriptParser#expression_atom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLambdaExpression(AntlerScriptParser.LambdaExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code selectExpression}
	 * labeled alternative in {@link AntlerScriptParser#expression_atom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelectExpression(AntlerScriptParser.SelectExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code groupedExpression}
	 * labeled alternative in {@link AntlerScriptParser#expression_atom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGroupedExpression(AntlerScriptParser.GroupedExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#new_object_instance}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNew_object_instance(AntlerScriptParser.New_object_instanceContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#new_list_instance}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNew_list_instance(AntlerScriptParser.New_list_instanceContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#new_array_instance}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNew_array_instance(AntlerScriptParser.New_array_instanceContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#new_class_instance}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNew_class_instance(AntlerScriptParser.New_class_instanceContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#object_instantiation_args}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitObject_instantiation_args(AntlerScriptParser.Object_instantiation_argsContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#new_map_instance}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNew_map_instance(AntlerScriptParser.New_map_instanceContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#select}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelect(AntlerScriptParser.SelectContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#keypair_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitKeypair_list(AntlerScriptParser.Keypair_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#keypair_clause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitKeypair_clause(AntlerScriptParser.Keypair_clauseContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expressionStatement}
	 * labeled alternative in {@link AntlerScriptParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpressionStatement(AntlerScriptParser.ExpressionStatementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code breakStatement}
	 * labeled alternative in {@link AntlerScriptParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBreakStatement(AntlerScriptParser.BreakStatementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code continueStatement}
	 * labeled alternative in {@link AntlerScriptParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitContinueStatement(AntlerScriptParser.ContinueStatementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code returnStatement}
	 * labeled alternative in {@link AntlerScriptParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReturnStatement(AntlerScriptParser.ReturnStatementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code loopStatement}
	 * labeled alternative in {@link AntlerScriptParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLoopStatement(AntlerScriptParser.LoopStatementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code whileStatement}
	 * labeled alternative in {@link AntlerScriptParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhileStatement(AntlerScriptParser.WhileStatementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code iterateStatement}
	 * labeled alternative in {@link AntlerScriptParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIterateStatement(AntlerScriptParser.IterateStatementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code declarationStatement}
	 * labeled alternative in {@link AntlerScriptParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDeclarationStatement(AntlerScriptParser.DeclarationStatementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code typedefStatement}
	 * labeled alternative in {@link AntlerScriptParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypedefStatement(AntlerScriptParser.TypedefStatementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ifStatement}
	 * labeled alternative in {@link AntlerScriptParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfStatement(AntlerScriptParser.IfStatementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code switchStatement}
	 * labeled alternative in {@link AntlerScriptParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSwitchStatement(AntlerScriptParser.SwitchStatementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code statementBlockStatement}
	 * labeled alternative in {@link AntlerScriptParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatementBlockStatement(AntlerScriptParser.StatementBlockStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#statement_block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatement_block(AntlerScriptParser.Statement_blockContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#loop}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLoop(AntlerScriptParser.LoopContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#while}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhile(AntlerScriptParser.WhileContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#iterate}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIterate(AntlerScriptParser.IterateContext ctx);
	/**
	 * Visit a parse tree produced by the {@code letDeclaration}
	 * labeled alternative in {@link AntlerScriptParser#declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLetDeclaration(AntlerScriptParser.LetDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by the {@code letDefinition}
	 * labeled alternative in {@link AntlerScriptParser#declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLetDefinition(AntlerScriptParser.LetDefinitionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code constDeclaration}
	 * labeled alternative in {@link AntlerScriptParser#declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstDeclaration(AntlerScriptParser.ConstDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by the {@code constDefinition}
	 * labeled alternative in {@link AntlerScriptParser#declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstDefinition(AntlerScriptParser.ConstDefinitionContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#typedef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypedef(AntlerScriptParser.TypedefContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#if}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIf(AntlerScriptParser.IfContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#elif}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitElif(AntlerScriptParser.ElifContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#else}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitElse(AntlerScriptParser.ElseContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#switch}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSwitch(AntlerScriptParser.SwitchContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#case}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCase(AntlerScriptParser.CaseContext ctx);
}