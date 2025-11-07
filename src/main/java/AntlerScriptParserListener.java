// Generated from AntlerScriptParser.g4 by ANTLR 4.13.2

import static AntlerScriptLexer.ignoreSemicolons;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link AntlerScriptParser}.
 */
public interface AntlerScriptParserListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link AntlerScriptParser#program}.
	 * @param ctx the parse tree
	 */
	void enterProgram(AntlerScriptParser.ProgramContext ctx);
	/**
	 * Exit a parse tree produced by {@link AntlerScriptParser#program}.
	 * @param ctx the parse tree
	 */
	void exitProgram(AntlerScriptParser.ProgramContext ctx);
	/**
	 * Enter a parse tree produced by {@link AntlerScriptParser#file_directive}.
	 * @param ctx the parse tree
	 */
	void enterFile_directive(AntlerScriptParser.File_directiveContext ctx);
	/**
	 * Exit a parse tree produced by {@link AntlerScriptParser#file_directive}.
	 * @param ctx the parse tree
	 */
	void exitFile_directive(AntlerScriptParser.File_directiveContext ctx);
	/**
	 * Enter a parse tree produced by {@link AntlerScriptParser#class_top_level}.
	 * @param ctx the parse tree
	 */
	void enterClass_top_level(AntlerScriptParser.Class_top_levelContext ctx);
	/**
	 * Exit a parse tree produced by {@link AntlerScriptParser#class_top_level}.
	 * @param ctx the parse tree
	 */
	void exitClass_top_level(AntlerScriptParser.Class_top_levelContext ctx);
	/**
	 * Enter a parse tree produced by {@link AntlerScriptParser#class_header_inside}.
	 * @param ctx the parse tree
	 */
	void enterClass_header_inside(AntlerScriptParser.Class_header_insideContext ctx);
	/**
	 * Exit a parse tree produced by {@link AntlerScriptParser#class_header_inside}.
	 * @param ctx the parse tree
	 */
	void exitClass_header_inside(AntlerScriptParser.Class_header_insideContext ctx);
	/**
	 * Enter a parse tree produced by {@link AntlerScriptParser#class_extends}.
	 * @param ctx the parse tree
	 */
	void enterClass_extends(AntlerScriptParser.Class_extendsContext ctx);
	/**
	 * Exit a parse tree produced by {@link AntlerScriptParser#class_extends}.
	 * @param ctx the parse tree
	 */
	void exitClass_extends(AntlerScriptParser.Class_extendsContext ctx);
	/**
	 * Enter a parse tree produced by {@link AntlerScriptParser#class_extends_access}.
	 * @param ctx the parse tree
	 */
	void enterClass_extends_access(AntlerScriptParser.Class_extends_accessContext ctx);
	/**
	 * Exit a parse tree produced by {@link AntlerScriptParser#class_extends_access}.
	 * @param ctx the parse tree
	 */
	void exitClass_extends_access(AntlerScriptParser.Class_extends_accessContext ctx);
	/**
	 * Enter a parse tree produced by {@link AntlerScriptParser#constructor}.
	 * @param ctx the parse tree
	 */
	void enterConstructor(AntlerScriptParser.ConstructorContext ctx);
	/**
	 * Exit a parse tree produced by {@link AntlerScriptParser#constructor}.
	 * @param ctx the parse tree
	 */
	void exitConstructor(AntlerScriptParser.ConstructorContext ctx);
	/**
	 * Enter a parse tree produced by {@link AntlerScriptParser#private_constructor}.
	 * @param ctx the parse tree
	 */
	void enterPrivate_constructor(AntlerScriptParser.Private_constructorContext ctx);
	/**
	 * Exit a parse tree produced by {@link AntlerScriptParser#private_constructor}.
	 * @param ctx the parse tree
	 */
	void exitPrivate_constructor(AntlerScriptParser.Private_constructorContext ctx);
	/**
	 * Enter a parse tree produced by {@link AntlerScriptParser#constructor_params}.
	 * @param ctx the parse tree
	 */
	void enterConstructor_params(AntlerScriptParser.Constructor_paramsContext ctx);
	/**
	 * Exit a parse tree produced by {@link AntlerScriptParser#constructor_params}.
	 * @param ctx the parse tree
	 */
	void exitConstructor_params(AntlerScriptParser.Constructor_paramsContext ctx);
	/**
	 * Enter a parse tree produced by {@link AntlerScriptParser#constructor_params_elm}.
	 * @param ctx the parse tree
	 */
	void enterConstructor_params_elm(AntlerScriptParser.Constructor_params_elmContext ctx);
	/**
	 * Exit a parse tree produced by {@link AntlerScriptParser#constructor_params_elm}.
	 * @param ctx the parse tree
	 */
	void exitConstructor_params_elm(AntlerScriptParser.Constructor_params_elmContext ctx);
	/**
	 * Enter a parse tree produced by {@link AntlerScriptParser#ellipsis}.
	 * @param ctx the parse tree
	 */
	void enterEllipsis(AntlerScriptParser.EllipsisContext ctx);
	/**
	 * Exit a parse tree produced by {@link AntlerScriptParser#ellipsis}.
	 * @param ctx the parse tree
	 */
	void exitEllipsis(AntlerScriptParser.EllipsisContext ctx);
	/**
	 * Enter a parse tree produced by {@link AntlerScriptParser#class_member}.
	 * @param ctx the parse tree
	 */
	void enterClass_member(AntlerScriptParser.Class_memberContext ctx);
	/**
	 * Exit a parse tree produced by {@link AntlerScriptParser#class_member}.
	 * @param ctx the parse tree
	 */
	void exitClass_member(AntlerScriptParser.Class_memberContext ctx);
	/**
	 * Enter a parse tree produced by {@link AntlerScriptParser#cast}.
	 * @param ctx the parse tree
	 */
	void enterCast(AntlerScriptParser.CastContext ctx);
	/**
	 * Exit a parse tree produced by {@link AntlerScriptParser#cast}.
	 * @param ctx the parse tree
	 */
	void exitCast(AntlerScriptParser.CastContext ctx);
	/**
	 * Enter a parse tree produced by {@link AntlerScriptParser#operator_overload}.
	 * @param ctx the parse tree
	 */
	void enterOperator_overload(AntlerScriptParser.Operator_overloadContext ctx);
	/**
	 * Exit a parse tree produced by {@link AntlerScriptParser#operator_overload}.
	 * @param ctx the parse tree
	 */
	void exitOperator_overload(AntlerScriptParser.Operator_overloadContext ctx);
	/**
	 * Enter a parse tree produced by {@link AntlerScriptParser#overridable}.
	 * @param ctx the parse tree
	 */
	void enterOverridable(AntlerScriptParser.OverridableContext ctx);
	/**
	 * Exit a parse tree produced by {@link AntlerScriptParser#overridable}.
	 * @param ctx the parse tree
	 */
	void exitOverridable(AntlerScriptParser.OverridableContext ctx);
	/**
	 * Enter a parse tree produced by {@link AntlerScriptParser#capture}.
	 * @param ctx the parse tree
	 */
	void enterCapture(AntlerScriptParser.CaptureContext ctx);
	/**
	 * Exit a parse tree produced by {@link AntlerScriptParser#capture}.
	 * @param ctx the parse tree
	 */
	void exitCapture(AntlerScriptParser.CaptureContext ctx);
	/**
	 * Enter a parse tree produced by {@link AntlerScriptParser#extends_assign}.
	 * @param ctx the parse tree
	 */
	void enterExtends_assign(AntlerScriptParser.Extends_assignContext ctx);
	/**
	 * Exit a parse tree produced by {@link AntlerScriptParser#extends_assign}.
	 * @param ctx the parse tree
	 */
	void exitExtends_assign(AntlerScriptParser.Extends_assignContext ctx);
	/**
	 * Enter a parse tree produced by {@link AntlerScriptParser#type}.
	 * @param ctx the parse tree
	 */
	void enterType(AntlerScriptParser.TypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link AntlerScriptParser#type}.
	 * @param ctx the parse tree
	 */
	void exitType(AntlerScriptParser.TypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link AntlerScriptParser#type_or}.
	 * @param ctx the parse tree
	 */
	void enterType_or(AntlerScriptParser.Type_orContext ctx);
	/**
	 * Exit a parse tree produced by {@link AntlerScriptParser#type_or}.
	 * @param ctx the parse tree
	 */
	void exitType_or(AntlerScriptParser.Type_orContext ctx);
	/**
	 * Enter a parse tree produced by {@link AntlerScriptParser#type_and}.
	 * @param ctx the parse tree
	 */
	void enterType_and(AntlerScriptParser.Type_andContext ctx);
	/**
	 * Exit a parse tree produced by {@link AntlerScriptParser#type_and}.
	 * @param ctx the parse tree
	 */
	void exitType_and(AntlerScriptParser.Type_andContext ctx);
	/**
	 * Enter a parse tree produced by {@link AntlerScriptParser#type_atomic}.
	 * @param ctx the parse tree
	 */
	void enterType_atomic(AntlerScriptParser.Type_atomicContext ctx);
	/**
	 * Exit a parse tree produced by {@link AntlerScriptParser#type_atomic}.
	 * @param ctx the parse tree
	 */
	void exitType_atomic(AntlerScriptParser.Type_atomicContext ctx);
	/**
	 * Enter a parse tree produced by {@link AntlerScriptParser#list_header}.
	 * @param ctx the parse tree
	 */
	void enterList_header(AntlerScriptParser.List_headerContext ctx);
	/**
	 * Exit a parse tree produced by {@link AntlerScriptParser#list_header}.
	 * @param ctx the parse tree
	 */
	void exitList_header(AntlerScriptParser.List_headerContext ctx);
	/**
	 * Enter a parse tree produced by {@link AntlerScriptParser#array_header}.
	 * @param ctx the parse tree
	 */
	void enterArray_header(AntlerScriptParser.Array_headerContext ctx);
	/**
	 * Exit a parse tree produced by {@link AntlerScriptParser#array_header}.
	 * @param ctx the parse tree
	 */
	void exitArray_header(AntlerScriptParser.Array_headerContext ctx);
	/**
	 * Enter a parse tree produced by {@link AntlerScriptParser#map_header}.
	 * @param ctx the parse tree
	 */
	void enterMap_header(AntlerScriptParser.Map_headerContext ctx);
	/**
	 * Exit a parse tree produced by {@link AntlerScriptParser#map_header}.
	 * @param ctx the parse tree
	 */
	void exitMap_header(AntlerScriptParser.Map_headerContext ctx);
	/**
	 * Enter a parse tree produced by {@link AntlerScriptParser#func_header}.
	 * @param ctx the parse tree
	 */
	void enterFunc_header(AntlerScriptParser.Func_headerContext ctx);
	/**
	 * Exit a parse tree produced by {@link AntlerScriptParser#func_header}.
	 * @param ctx the parse tree
	 */
	void exitFunc_header(AntlerScriptParser.Func_headerContext ctx);
	/**
	 * Enter a parse tree produced by {@link AntlerScriptParser#func_params}.
	 * @param ctx the parse tree
	 */
	void enterFunc_params(AntlerScriptParser.Func_paramsContext ctx);
	/**
	 * Exit a parse tree produced by {@link AntlerScriptParser#func_params}.
	 * @param ctx the parse tree
	 */
	void exitFunc_params(AntlerScriptParser.Func_paramsContext ctx);
	/**
	 * Enter a parse tree produced by {@link AntlerScriptParser#func_param_elm}.
	 * @param ctx the parse tree
	 */
	void enterFunc_param_elm(AntlerScriptParser.Func_param_elmContext ctx);
	/**
	 * Exit a parse tree produced by {@link AntlerScriptParser#func_param_elm}.
	 * @param ctx the parse tree
	 */
	void exitFunc_param_elm(AntlerScriptParser.Func_param_elmContext ctx);
	/**
	 * Enter a parse tree produced by {@link AntlerScriptParser#lambda}.
	 * @param ctx the parse tree
	 */
	void enterLambda(AntlerScriptParser.LambdaContext ctx);
	/**
	 * Exit a parse tree produced by {@link AntlerScriptParser#lambda}.
	 * @param ctx the parse tree
	 */
	void exitLambda(AntlerScriptParser.LambdaContext ctx);
	/**
	 * Enter a parse tree produced by {@link AntlerScriptParser#class_header}.
	 * @param ctx the parse tree
	 */
	void enterClass_header(AntlerScriptParser.Class_headerContext ctx);
	/**
	 * Exit a parse tree produced by {@link AntlerScriptParser#class_header}.
	 * @param ctx the parse tree
	 */
	void exitClass_header(AntlerScriptParser.Class_headerContext ctx);
	/**
	 * Enter a parse tree produced by {@link AntlerScriptParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpression(AntlerScriptParser.ExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link AntlerScriptParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpression(AntlerScriptParser.ExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link AntlerScriptParser#expression_assignment}.
	 * @param ctx the parse tree
	 */
	void enterExpression_assignment(AntlerScriptParser.Expression_assignmentContext ctx);
	/**
	 * Exit a parse tree produced by {@link AntlerScriptParser#expression_assignment}.
	 * @param ctx the parse tree
	 */
	void exitExpression_assignment(AntlerScriptParser.Expression_assignmentContext ctx);
	/**
	 * Enter a parse tree produced by {@link AntlerScriptParser#expression_assignment_right}.
	 * @param ctx the parse tree
	 */
	void enterExpression_assignment_right(AntlerScriptParser.Expression_assignment_rightContext ctx);
	/**
	 * Exit a parse tree produced by {@link AntlerScriptParser#expression_assignment_right}.
	 * @param ctx the parse tree
	 */
	void exitExpression_assignment_right(AntlerScriptParser.Expression_assignment_rightContext ctx);
	/**
	 * Enter a parse tree produced by {@link AntlerScriptParser#expression_logical_or}.
	 * @param ctx the parse tree
	 */
	void enterExpression_logical_or(AntlerScriptParser.Expression_logical_orContext ctx);
	/**
	 * Exit a parse tree produced by {@link AntlerScriptParser#expression_logical_or}.
	 * @param ctx the parse tree
	 */
	void exitExpression_logical_or(AntlerScriptParser.Expression_logical_orContext ctx);
	/**
	 * Enter a parse tree produced by {@link AntlerScriptParser#expression_logical_and}.
	 * @param ctx the parse tree
	 */
	void enterExpression_logical_and(AntlerScriptParser.Expression_logical_andContext ctx);
	/**
	 * Exit a parse tree produced by {@link AntlerScriptParser#expression_logical_and}.
	 * @param ctx the parse tree
	 */
	void exitExpression_logical_and(AntlerScriptParser.Expression_logical_andContext ctx);
	/**
	 * Enter a parse tree produced by {@link AntlerScriptParser#expression_logical_not}.
	 * @param ctx the parse tree
	 */
	void enterExpression_logical_not(AntlerScriptParser.Expression_logical_notContext ctx);
	/**
	 * Exit a parse tree produced by {@link AntlerScriptParser#expression_logical_not}.
	 * @param ctx the parse tree
	 */
	void exitExpression_logical_not(AntlerScriptParser.Expression_logical_notContext ctx);
	/**
	 * Enter a parse tree produced by {@link AntlerScriptParser#expression_cmp}.
	 * @param ctx the parse tree
	 */
	void enterExpression_cmp(AntlerScriptParser.Expression_cmpContext ctx);
	/**
	 * Exit a parse tree produced by {@link AntlerScriptParser#expression_cmp}.
	 * @param ctx the parse tree
	 */
	void exitExpression_cmp(AntlerScriptParser.Expression_cmpContext ctx);
	/**
	 * Enter a parse tree produced by {@link AntlerScriptParser#expression_cmp_right}.
	 * @param ctx the parse tree
	 */
	void enterExpression_cmp_right(AntlerScriptParser.Expression_cmp_rightContext ctx);
	/**
	 * Exit a parse tree produced by {@link AntlerScriptParser#expression_cmp_right}.
	 * @param ctx the parse tree
	 */
	void exitExpression_cmp_right(AntlerScriptParser.Expression_cmp_rightContext ctx);
	/**
	 * Enter a parse tree produced by {@link AntlerScriptParser#expression_bit_or}.
	 * @param ctx the parse tree
	 */
	void enterExpression_bit_or(AntlerScriptParser.Expression_bit_orContext ctx);
	/**
	 * Exit a parse tree produced by {@link AntlerScriptParser#expression_bit_or}.
	 * @param ctx the parse tree
	 */
	void exitExpression_bit_or(AntlerScriptParser.Expression_bit_orContext ctx);
	/**
	 * Enter a parse tree produced by {@link AntlerScriptParser#expression_bit_xor}.
	 * @param ctx the parse tree
	 */
	void enterExpression_bit_xor(AntlerScriptParser.Expression_bit_xorContext ctx);
	/**
	 * Exit a parse tree produced by {@link AntlerScriptParser#expression_bit_xor}.
	 * @param ctx the parse tree
	 */
	void exitExpression_bit_xor(AntlerScriptParser.Expression_bit_xorContext ctx);
	/**
	 * Enter a parse tree produced by {@link AntlerScriptParser#expression_bit_and}.
	 * @param ctx the parse tree
	 */
	void enterExpression_bit_and(AntlerScriptParser.Expression_bit_andContext ctx);
	/**
	 * Exit a parse tree produced by {@link AntlerScriptParser#expression_bit_and}.
	 * @param ctx the parse tree
	 */
	void exitExpression_bit_and(AntlerScriptParser.Expression_bit_andContext ctx);
	/**
	 * Enter a parse tree produced by {@link AntlerScriptParser#expression_bit_shift}.
	 * @param ctx the parse tree
	 */
	void enterExpression_bit_shift(AntlerScriptParser.Expression_bit_shiftContext ctx);
	/**
	 * Exit a parse tree produced by {@link AntlerScriptParser#expression_bit_shift}.
	 * @param ctx the parse tree
	 */
	void exitExpression_bit_shift(AntlerScriptParser.Expression_bit_shiftContext ctx);
	/**
	 * Enter a parse tree produced by {@link AntlerScriptParser#expression_bit_shift_right}.
	 * @param ctx the parse tree
	 */
	void enterExpression_bit_shift_right(AntlerScriptParser.Expression_bit_shift_rightContext ctx);
	/**
	 * Exit a parse tree produced by {@link AntlerScriptParser#expression_bit_shift_right}.
	 * @param ctx the parse tree
	 */
	void exitExpression_bit_shift_right(AntlerScriptParser.Expression_bit_shift_rightContext ctx);
	/**
	 * Enter a parse tree produced by {@link AntlerScriptParser#expression_add}.
	 * @param ctx the parse tree
	 */
	void enterExpression_add(AntlerScriptParser.Expression_addContext ctx);
	/**
	 * Exit a parse tree produced by {@link AntlerScriptParser#expression_add}.
	 * @param ctx the parse tree
	 */
	void exitExpression_add(AntlerScriptParser.Expression_addContext ctx);
	/**
	 * Enter a parse tree produced by {@link AntlerScriptParser#expression_add_right}.
	 * @param ctx the parse tree
	 */
	void enterExpression_add_right(AntlerScriptParser.Expression_add_rightContext ctx);
	/**
	 * Exit a parse tree produced by {@link AntlerScriptParser#expression_add_right}.
	 * @param ctx the parse tree
	 */
	void exitExpression_add_right(AntlerScriptParser.Expression_add_rightContext ctx);
	/**
	 * Enter a parse tree produced by {@link AntlerScriptParser#expression_mult}.
	 * @param ctx the parse tree
	 */
	void enterExpression_mult(AntlerScriptParser.Expression_multContext ctx);
	/**
	 * Exit a parse tree produced by {@link AntlerScriptParser#expression_mult}.
	 * @param ctx the parse tree
	 */
	void exitExpression_mult(AntlerScriptParser.Expression_multContext ctx);
	/**
	 * Enter a parse tree produced by {@link AntlerScriptParser#expression_mult_right}.
	 * @param ctx the parse tree
	 */
	void enterExpression_mult_right(AntlerScriptParser.Expression_mult_rightContext ctx);
	/**
	 * Exit a parse tree produced by {@link AntlerScriptParser#expression_mult_right}.
	 * @param ctx the parse tree
	 */
	void exitExpression_mult_right(AntlerScriptParser.Expression_mult_rightContext ctx);
	/**
	 * Enter a parse tree produced by {@link AntlerScriptParser#expression_unary}.
	 * @param ctx the parse tree
	 */
	void enterExpression_unary(AntlerScriptParser.Expression_unaryContext ctx);
	/**
	 * Exit a parse tree produced by {@link AntlerScriptParser#expression_unary}.
	 * @param ctx the parse tree
	 */
	void exitExpression_unary(AntlerScriptParser.Expression_unaryContext ctx);
	/**
	 * Enter a parse tree produced by {@link AntlerScriptParser#expression_unary_op}.
	 * @param ctx the parse tree
	 */
	void enterExpression_unary_op(AntlerScriptParser.Expression_unary_opContext ctx);
	/**
	 * Exit a parse tree produced by {@link AntlerScriptParser#expression_unary_op}.
	 * @param ctx the parse tree
	 */
	void exitExpression_unary_op(AntlerScriptParser.Expression_unary_opContext ctx);
	/**
	 * Enter a parse tree produced by {@link AntlerScriptParser#expression_exp}.
	 * @param ctx the parse tree
	 */
	void enterExpression_exp(AntlerScriptParser.Expression_expContext ctx);
	/**
	 * Exit a parse tree produced by {@link AntlerScriptParser#expression_exp}.
	 * @param ctx the parse tree
	 */
	void exitExpression_exp(AntlerScriptParser.Expression_expContext ctx);
	/**
	 * Enter a parse tree produced by {@link AntlerScriptParser#expression_postfix}.
	 * @param ctx the parse tree
	 */
	void enterExpression_postfix(AntlerScriptParser.Expression_postfixContext ctx);
	/**
	 * Exit a parse tree produced by {@link AntlerScriptParser#expression_postfix}.
	 * @param ctx the parse tree
	 */
	void exitExpression_postfix(AntlerScriptParser.Expression_postfixContext ctx);
	/**
	 * Enter a parse tree produced by {@link AntlerScriptParser#expression_access}.
	 * @param ctx the parse tree
	 */
	void enterExpression_access(AntlerScriptParser.Expression_accessContext ctx);
	/**
	 * Exit a parse tree produced by {@link AntlerScriptParser#expression_access}.
	 * @param ctx the parse tree
	 */
	void exitExpression_access(AntlerScriptParser.Expression_accessContext ctx);
	/**
	 * Enter a parse tree produced by {@link AntlerScriptParser#arguments}.
	 * @param ctx the parse tree
	 */
	void enterArguments(AntlerScriptParser.ArgumentsContext ctx);
	/**
	 * Exit a parse tree produced by {@link AntlerScriptParser#arguments}.
	 * @param ctx the parse tree
	 */
	void exitArguments(AntlerScriptParser.ArgumentsContext ctx);
	/**
	 * Enter a parse tree produced by {@link AntlerScriptParser#argument_elm}.
	 * @param ctx the parse tree
	 */
	void enterArgument_elm(AntlerScriptParser.Argument_elmContext ctx);
	/**
	 * Exit a parse tree produced by {@link AntlerScriptParser#argument_elm}.
	 * @param ctx the parse tree
	 */
	void exitArgument_elm(AntlerScriptParser.Argument_elmContext ctx);
	/**
	 * Enter a parse tree produced by {@link AntlerScriptParser#dictionary_arguments}.
	 * @param ctx the parse tree
	 */
	void enterDictionary_arguments(AntlerScriptParser.Dictionary_argumentsContext ctx);
	/**
	 * Exit a parse tree produced by {@link AntlerScriptParser#dictionary_arguments}.
	 * @param ctx the parse tree
	 */
	void exitDictionary_arguments(AntlerScriptParser.Dictionary_argumentsContext ctx);
	/**
	 * Enter a parse tree produced by {@link AntlerScriptParser#expression_atom}.
	 * @param ctx the parse tree
	 */
	void enterExpression_atom(AntlerScriptParser.Expression_atomContext ctx);
	/**
	 * Exit a parse tree produced by {@link AntlerScriptParser#expression_atom}.
	 * @param ctx the parse tree
	 */
	void exitExpression_atom(AntlerScriptParser.Expression_atomContext ctx);
	/**
	 * Enter a parse tree produced by {@link AntlerScriptParser#select}.
	 * @param ctx the parse tree
	 */
	void enterSelect(AntlerScriptParser.SelectContext ctx);
	/**
	 * Exit a parse tree produced by {@link AntlerScriptParser#select}.
	 * @param ctx the parse tree
	 */
	void exitSelect(AntlerScriptParser.SelectContext ctx);
	/**
	 * Enter a parse tree produced by {@link AntlerScriptParser#keypair_list}.
	 * @param ctx the parse tree
	 */
	void enterKeypair_list(AntlerScriptParser.Keypair_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link AntlerScriptParser#keypair_list}.
	 * @param ctx the parse tree
	 */
	void exitKeypair_list(AntlerScriptParser.Keypair_listContext ctx);
	/**
	 * Enter a parse tree produced by {@link AntlerScriptParser#keypair_clause}.
	 * @param ctx the parse tree
	 */
	void enterKeypair_clause(AntlerScriptParser.Keypair_clauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link AntlerScriptParser#keypair_clause}.
	 * @param ctx the parse tree
	 */
	void exitKeypair_clause(AntlerScriptParser.Keypair_clauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link AntlerScriptParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatement(AntlerScriptParser.StatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link AntlerScriptParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatement(AntlerScriptParser.StatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link AntlerScriptParser#statement_block}.
	 * @param ctx the parse tree
	 */
	void enterStatement_block(AntlerScriptParser.Statement_blockContext ctx);
	/**
	 * Exit a parse tree produced by {@link AntlerScriptParser#statement_block}.
	 * @param ctx the parse tree
	 */
	void exitStatement_block(AntlerScriptParser.Statement_blockContext ctx);
	/**
	 * Enter a parse tree produced by {@link AntlerScriptParser#loop}.
	 * @param ctx the parse tree
	 */
	void enterLoop(AntlerScriptParser.LoopContext ctx);
	/**
	 * Exit a parse tree produced by {@link AntlerScriptParser#loop}.
	 * @param ctx the parse tree
	 */
	void exitLoop(AntlerScriptParser.LoopContext ctx);
	/**
	 * Enter a parse tree produced by {@link AntlerScriptParser#while}.
	 * @param ctx the parse tree
	 */
	void enterWhile(AntlerScriptParser.WhileContext ctx);
	/**
	 * Exit a parse tree produced by {@link AntlerScriptParser#while}.
	 * @param ctx the parse tree
	 */
	void exitWhile(AntlerScriptParser.WhileContext ctx);
	/**
	 * Enter a parse tree produced by {@link AntlerScriptParser#iterate}.
	 * @param ctx the parse tree
	 */
	void enterIterate(AntlerScriptParser.IterateContext ctx);
	/**
	 * Exit a parse tree produced by {@link AntlerScriptParser#iterate}.
	 * @param ctx the parse tree
	 */
	void exitIterate(AntlerScriptParser.IterateContext ctx);
	/**
	 * Enter a parse tree produced by {@link AntlerScriptParser#declaration}.
	 * @param ctx the parse tree
	 */
	void enterDeclaration(AntlerScriptParser.DeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link AntlerScriptParser#declaration}.
	 * @param ctx the parse tree
	 */
	void exitDeclaration(AntlerScriptParser.DeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link AntlerScriptParser#typedef}.
	 * @param ctx the parse tree
	 */
	void enterTypedef(AntlerScriptParser.TypedefContext ctx);
	/**
	 * Exit a parse tree produced by {@link AntlerScriptParser#typedef}.
	 * @param ctx the parse tree
	 */
	void exitTypedef(AntlerScriptParser.TypedefContext ctx);
	/**
	 * Enter a parse tree produced by {@link AntlerScriptParser#if}.
	 * @param ctx the parse tree
	 */
	void enterIf(AntlerScriptParser.IfContext ctx);
	/**
	 * Exit a parse tree produced by {@link AntlerScriptParser#if}.
	 * @param ctx the parse tree
	 */
	void exitIf(AntlerScriptParser.IfContext ctx);
	/**
	 * Enter a parse tree produced by {@link AntlerScriptParser#elif}.
	 * @param ctx the parse tree
	 */
	void enterElif(AntlerScriptParser.ElifContext ctx);
	/**
	 * Exit a parse tree produced by {@link AntlerScriptParser#elif}.
	 * @param ctx the parse tree
	 */
	void exitElif(AntlerScriptParser.ElifContext ctx);
	/**
	 * Enter a parse tree produced by {@link AntlerScriptParser#else}.
	 * @param ctx the parse tree
	 */
	void enterElse(AntlerScriptParser.ElseContext ctx);
	/**
	 * Exit a parse tree produced by {@link AntlerScriptParser#else}.
	 * @param ctx the parse tree
	 */
	void exitElse(AntlerScriptParser.ElseContext ctx);
	/**
	 * Enter a parse tree produced by {@link AntlerScriptParser#switch}.
	 * @param ctx the parse tree
	 */
	void enterSwitch(AntlerScriptParser.SwitchContext ctx);
	/**
	 * Exit a parse tree produced by {@link AntlerScriptParser#switch}.
	 * @param ctx the parse tree
	 */
	void exitSwitch(AntlerScriptParser.SwitchContext ctx);
	/**
	 * Enter a parse tree produced by {@link AntlerScriptParser#case}.
	 * @param ctx the parse tree
	 */
	void enterCase(AntlerScriptParser.CaseContext ctx);
	/**
	 * Exit a parse tree produced by {@link AntlerScriptParser#case}.
	 * @param ctx the parse tree
	 */
	void exitCase(AntlerScriptParser.CaseContext ctx);
}