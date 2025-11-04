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
	 * Visit a parse tree produced by {@link AntlerScriptParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStat(AntlerScriptParser.StatContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#def}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDef(AntlerScriptParser.DefContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpr(AntlerScriptParser.ExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#func}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunc(AntlerScriptParser.FuncContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#params}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParams(AntlerScriptParser.ParamsContext ctx);
	/**
	 * Visit a parse tree produced by {@link AntlerScriptParser#args}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArgs(AntlerScriptParser.ArgsContext ctx);
}