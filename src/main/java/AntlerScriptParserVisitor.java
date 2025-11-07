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
	 * Visit a parse tree produced by {@link AntlerScriptParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProgram(AntlerScriptParser.ProgramContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#file_directive}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFile_directive(AntlerScriptParser.File_directiveContext ctx);
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
	 * Visit a parse tree produced by {@link AntlerScriptParser#private_constructor}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrivate_constructor(AntlerScriptParser.Private_constructorContext ctx);
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
	 * Visit a parse tree produced by {@link AntlerScriptParser#ellipsis}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEllipsis(AntlerScriptParser.EllipsisContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#class_member}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClass_member(AntlerScriptParser.Class_memberContext ctx);
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
	 * Visit a parse tree produced by {@link AntlerScriptParser#type_atomic}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType_atomic(AntlerScriptParser.Type_atomicContext ctx);
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
	 * Visit a parse tree produced by {@link AntlerScriptParser#expression_logical_and}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression_logical_and(AntlerScriptParser.Expression_logical_andContext ctx);
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
	 * Visit a parse tree produced by {@link AntlerScriptParser#expression_bit_xor}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression_bit_xor(AntlerScriptParser.Expression_bit_xorContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#expression_bit_and}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression_bit_and(AntlerScriptParser.Expression_bit_andContext ctx);
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
	 * Visit a parse tree produced by {@link AntlerScriptParser#expression_postfix}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression_postfix(AntlerScriptParser.Expression_postfixContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#expression_access}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression_access(AntlerScriptParser.Expression_accessContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#arguments}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArguments(AntlerScriptParser.ArgumentsContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#argument_elm}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArgument_elm(AntlerScriptParser.Argument_elmContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#dictionary_arguments}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDictionary_arguments(AntlerScriptParser.Dictionary_argumentsContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#expression_atom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression_atom(AntlerScriptParser.Expression_atomContext ctx);
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
	 * Visit a parse tree produced by {@link AntlerScriptParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatement(AntlerScriptParser.StatementContext ctx);
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
	 * Visit a parse tree produced by {@link AntlerScriptParser#declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDeclaration(AntlerScriptParser.DeclarationContext ctx);
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