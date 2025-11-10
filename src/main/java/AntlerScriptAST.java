import java.util.*;

import org.antlr.v4.runtime.*;

import org.apache.commons.text.StringEscapeUtils;

public class AntlerScriptAST {
	public abstract class Node {
		public final Token[] tokens;

		public Node(Token[] tokens) {
			assert tokens != null;

			this.tokens = tokens;
		}

		public abstract <T> T accept(Visitor<T> visitor);
	}

	// ====================
	// PROGRAM & TOP LEVEL
	// ====================

	public class Program extends Node {
		public final List<FileDirective> directives;
		public final List<Statement> statements;  // For scripts without classes
		public final ClassDefinition classDefinition;  // For class files

		public Program(Token[] tokens, List<FileDirective> directives, List<Statement> statements, ClassDefinition classDefinition) {
			super(tokens);

			// TODO: figure out minimum input program
			assert (statements != null && classDefinition == null) || (statements == null && classDefinition != null);

			this.directives = directives;
			this.statements = statements;
			this.classDefinition = classDefinition;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitProgram(this);
		}
	}

	public class FileDirective extends Node {
		public final String name;  // "namespace", "classname", etc.
		public final String value;

		public FileDirective(Token[] tokens, String name, String value) {
			super(tokens);

			assert name != null;
			assert value != null;

			this.name = name;
			this.value = value;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitFileDirective(this);
		}
	}

	// ====================
	// TYPES
	// ====================

	public abstract class Type extends Node {
		public Type(Token[] tokens) { super(tokens); }
	}

	public class UnionType extends Type {
		public enum Kind {
			AND, OR
		}

		public final Kind kind;
		public final Type left;
		public final Type right;

		public UnionType(Token[] tokens, Kind kind, Type left, Type right) {
			super(tokens);

			assert kind != null;
			assert left != null;
			assert right != null;

			this.kind = kind;
			this.left = left;
			this.right = right;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitUnionType(this);
		}
	}

	public class SymbolType extends Type {
		public final String name;

		public SymbolType(Token[] tokens, String name) {
			super(tokens);

			assert name != null;

			this.name = name;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitSymbolType(this);
		}
	}

	public class ListType extends Type {
		public final Type items;

		public ListType(Token[] tokens, Type items) {
			super(tokens);
			this.items = items;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitListType(this);
		}
	}

	public class ArrayType extends Type {
		public final Type items;

		public ArrayType(Token[] tokens, Type items) {
			super(tokens);
			this.items = items;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitArrayType(this);
		}
	}

	public class MapType extends Type {
		public final Type keys;
		public final Type values;

		public MapType(Token[] tokens, Type keys, Type values) {
			super(tokens);

			assert (keys == null && values == null) || (keys != null && values != null);

			this.keys = keys;
			this.values = values;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitMapType(this);
		}
	}

	public class FunctionType extends Type {
		public final List<FunctionParameter> parameters;
		public final Type returnType;

		public FunctionType(Token[] tokens, List<FunctionParameter> parameters, Type returnType) {
			super(tokens);
			this.parameters = parameters;
			this.returnType = returnType;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitFunctionType(this);
		}
	}

	public class ClassType extends Type {
		public final List<ClassMember> members;

		public ClassType(Token[] tokens, List<ClassMember> members) {
			super(tokens);
			this.members = members;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitClassType(this);
		}
	}

	public class SelfClassType extends Type {
		public SelfClassType(Token[] tokens) { super(tokens); }

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitSelfClassType(this);
		}
	}

	// ====================
	// TODO: CLASS DEFINITIONS
	// ====================

	// ====================
	// STATEMENTS
	// ====================

	public abstract class Statement extends Node {
		public Statement(Token[] tokens) { super(tokens); }
	}

	public class ExpressionStatement extends Statement {
		public final Expression expression;
		public final boolean isDeferred;

		public ExpressionStatement(Token[] tokens, Expression expression, boolean isDeferred) {
			super(tokens);

			assert expression != null;

			this.expression = expression;
			this.isDeferred = isDeferred;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitExpressionStatement(this);
		}
	}

	public class StatementBlock extends Statement {
		public final List<Statement> statements;
		public final boolean isDeferred;

		public StatementBlock(Token[] tokens, List<Statement> statements, boolean isDeferred) {
			super(tokens);
			this.statements = statements;
			this.isDeferred = isDeferred;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitStatementBlock(this);
		}
	}

	public class VariableDeclaration extends Statement {
		public final boolean isConst;
		public final boolean isMutable;
		public final Type type;
		public final String name;
		public final Expression initializer;

		public VariableDeclaration(Token[] tokens, boolean isConst, boolean isMutable, Type type, String name, Expression initializer) {
			super(tokens);

			assert type != null || initializer != null;

			this.isConst = isConst;
			this.isMutable = isMutable;
			this.type = type;
			this.name = name;
			this.initializer = initializer;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitVariableDeclaration(this);
		}
	}

	public class TypeAlias extends Statement {
		public final String name;
		public final Type type;

		public TypeAlias(Token[] tokens, String name, Type type) {
			super(tokens);

			assert name != null;
			assert type != null;

			this.name = name;
			this.type = type;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitTypeAlias(this);
		}
	}

	public class ReturnStatement extends Statement {
		public final Expression value;

		public ReturnStatement(Token[] tokens, Expression value) {
			super(tokens);
			this.value = value;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitReturnStatement(this);
		}
	}

	public class BreakStatement extends Statement {
		public BreakStatement(Token[] tokens) { super(tokens); }

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitBreakStatement(this);
		}
	}

	public class ContinueStatement extends Statement {
		public ContinueStatement(Token[] tokens) { super(tokens); }

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitContinueStatement(this);
		}
	}

	public class IfStatement extends Statement {
		public final Expression condition;
		public final StatementBlock thenBranch;
		public final List<ElifBranch> elifBranches;
		public final StatementBlock elseBranch;

		public IfStatement(Token[] tokens, Expression condition, StatementBlock thenBranch, List<ElifBranch> elifBranches, StatementBlock elseBranch) {
			super(tokens);

			assert condition != null;
			assert thenBranch != null;

			this.condition = condition;
			this.thenBranch = thenBranch;
			this.elifBranches = elifBranches;
			this.elseBranch = elseBranch;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitIfStatement(this);
		}
	}

	public class ElifBranch {
		public final Expression condition;
		public final StatementBlock body;

		public ElifBranch(Expression condition, StatementBlock body) {
			assert condition != null;
			assert body != null;

			this.condition = condition;
			this.body = body;
		}
	}

	public class SwitchStatement extends Statement {
		public final Expression value;
		public final List<CaseBranch> cases;
		public final StatementBlock defaultCase;

		public SwitchStatement(Token[] tokens, Expression value, List<CaseBranch> cases, StatementBlock defaultCase) {
			super(tokens);

			assert value != null;
			assert cases != null;
			assert cases.size() > 0;

			this.value = value;
			this.cases = cases;
			this.defaultCase = defaultCase;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitSwitchStatement(this);
		}
	}

	public class CaseBranch {
		public final List<Expression> values;
		public final StatementBlock body;

		public CaseBranch(List<Expression> values, StatementBlock body) {
			assert values != null;
			assert values.size() > 0;
			assert body != null;

			this.values = values;
			this.body = body;
		}
	}

	public class WhileStatement extends Statement {
		public final Expression condition;
		public final StatementBlock body;

		public WhileStatement(Token[] tokens, Expression condition, StatementBlock body) {
			super(tokens);

			assert condition != null;
			assert body != null;

			this.condition = condition;
			this.body = body;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitWhileStatement(this);
		}
	}

	public class LoopStatement extends Statement {
		public final Expression from;
		public final Expression to;
		public final Expression by;
		public final String variable;
		public final StatementBlock body;

		public LoopStatement(Token[] tokens, Expression from, Expression to, Expression by, String variable, StatementBlock body) {
			super(tokens);

			assert to != null;
			assert body != null;

			this.from = from;
			this.to = to;
			this.by = by;
			this.variable = variable;
			this.body = body;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitLoopStatement(this);
		}
	}

	public class IterateStatement extends Statement {
		public final Expression iterable;
		public final String indexVariable;
		public final String elementVariable;
		public final StatementBlock body;

		public IterateStatement(Token[] tokens, Expression iterable, String indexVariable, String elementVariable, StatementBlock body) {
			super(tokens);

			assert iterable != null;
			assert body != null;

			this.iterable = iterable;
			this.indexVariable = indexVariable;
			this.elementVariable = elementVariable;
			this.body = body;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitIterateStatement(this);
		}
	}

	// ====================
	// EXPRESSIONS
	// ====================

	public abstract class Expression extends Node {
		public Expression(Token[] tokens) { super(tokens); }
	}

	public class BinaryExpression extends Expression {
		public enum Kind {
			// Assignment
			PLUS_ASSIGN, MINUS_ASSIGN, TIMES_ASSIGN, EXPONENT_ASSIGN, DIVIDE_ASSIGN, FLOOR_DIVIDE_ASSIGN, MODULO_ASSIGN, REMAINDER_ASSIGN, BIT_OR_ASSIGN, BIT_AND_ASSIGN, BIT_NOT_ASSIGN, BIT_XOR_ASSIGN, BIT_LSHIFT_ASSIGN, BIT_RSHIFT_ASSIGN, CONCAT_ASSIGN, NULLISH_ASSIGN, CHAIN_ASSIGN, ASSIGN,

			// Comparison
			OR, NULLISH, AND, LESSER_THAN, GREATER_THAN, LESSER_OR_EQUAL, GREATER_OR_EQUAL, EQUAL, NOT_EQUAL, IS, IN,

			// Bitwise
			BIT_OR, BIT_XOR, BIT_AND, BIT_LSHIFT, BIT_RSHIFT,

			// Addition
			ADD, SUBTRACT, CONCAT,

			// Multiplication
			MULTIPLY, DIVIDE, FLOOR_DIVIDE, MODULO, REMAINDER,

			// Exponent
			EXPONENT,
		}

		public final Kind operation;
		public final Expression left;
		public final Expression right;

		public BinaryExpression(Token[] tokens, Kind operation, Expression left, Expression right) {
			super(tokens);

			assert operation != null;
			assert left != null;
			assert right != null;

			this.operation = operation;
			this.left = left;
			this.right = right;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitBinaryExpression(this);
		}
	}

	public class UnaryExpression extends Expression {
		public enum Kind {
			NOT, BIT_NOT, PLUS, MINUS,
		}

		public final Kind operation;
		public final Expression operand;

		public UnaryExpression(Token[] tokens, Kind operation, Expression operand) {
			super(tokens);

			assert operation != null;
			assert operand != null;

			this.operation = operation;
			this.operand = operand;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitUnaryExpression(this);
		}
	}

	public class IndexExpression extends Expression {
		public final Expression base;
		public final Expression index;

		public IndexExpression(Token[] tokens, Expression base, Expression index) {
			super(tokens);

			assert base != null;
			assert index != null;

			this.base = base;
			this.index = index;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitIndexExpression(this);
		}
	}

	public class AccessExpression extends Expression {
		public final Expression object;
		public final String member;
		public final boolean optional;

		public AccessExpression(Token[] tokens, Expression object, String member, boolean optional) {
			super(tokens);

			assert object != null;
			assert member != null;

			this.object = object;
			this.member = member;
			this.optional = optional;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitAccessExpression(this);
		}
	}

	public class CallExpression extends Expression {
		public final Expression function;
		public final List<Argument> arguments;

		public CallExpression(Token[] tokens, Expression function, List<Argument> arguments) {
			super(tokens);

			assert function != null;

			this.function = function;
			this.arguments = arguments;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitCallExpression(this);
		}
	}

	public class SymbolExpression extends Expression {
		public final String symbol;

		public SymbolExpression(Token[] tokens, String symbol) {
			super(tokens);

			assert symbol != null;
			assert symbol.length() > 0;

			this.symbol = symbol;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitSymbolExpression(this);
		}
	}

	public class IntExpression extends Expression {
		public final int value;

		public IntExpression(Token[] tokens, int value) {
			super(tokens);
			this.value = value;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitIntExpression(this);
		}
	}

	public class FloatExpression extends Expression {
		public final double value;

		public FloatExpression(Token[] tokens, double value) {
			super(tokens);
			this.value = value;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitFloatExpression(this);
		}
	}

	public class NullExpression extends Expression {
		public NullExpression(Token[] tokens) {
			super(tokens);
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitNullExpression(this);
		}
	}

	public class SelfExpression extends Expression {
		public SelfExpression(Token[] tokens) {
			super(tokens);
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitSelfExpression(this);
		}
	}

	public class SuperExpression extends Expression {
		public SuperExpression(Token[] tokens) {
			super(tokens);
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitSuperExpression(this);
		}
	}

	public class BooleanExpression extends Expression {
		public final boolean value;
		
		public BooleanExpression(Token[] tokens, boolean value) {
			super(tokens);
			this.value = value;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitBooleanExpression(this);
		}
	}

	public class StringExpression extends Expression {
		public final String value;

		// Will unescape string if not raw
		public StringExpression(Token[] tokens, String value, boolean raw) {
			super(tokens);

			assert value != null;

			if (!raw) {
				this.value = StringEscapeUtils.unescapeJava(value);
			} else {
				this.value = value;
			}
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitStringExpression(this);
		}
	}

	// TODO:
	// new_object_instance
	// new_list_instance
	// new_array_instance
	// new_map_instance
	// new_class_instance
	// lambda
	// select

	// ====================
	// UTILITIES
	// ====================

	// TODO: parameters

	public class Argument {
		public final Expression value;
		public final String keyword;
		public final boolean isBlank;

		Argument(Expression value, String keyword, boolean isBlank) {
			assert (value != null && !isBlank)
				|| (value == null && keyword == null && isBlank);

			this.value = value;
			this.keyword = keyword;
			this.isBlank = isBlank;
		}
	}

	public class ClassName {
		public final List<String> parts;

		public ClassName(List<String> parts) {
			assert parts != null;
			assert parts.size() > 0;

			this.parts = parts;
		}

		public String toString() {
			return String.join(".", parts);
		}
	}

	// ====================
	// VISITOR PATTERN
	// ====================

	public interface Visitor<T> {
		T visitProgram(Program node);

		T visitFileDirective(FileDirective node);

		// Types
		T visitSymbolType(SymbolType node);

		T visitListType(ListType node);

		T visitArrayType(ArrayType node);

		T visitMapType(MapType node);

		T visitFunctionType(FunctionType node);

		T visitClassType(ClassType node);

		T visitUnionType(UnionType node);

		T visitSelfClassType(SelfClassType node);

		// Class members

		// Statements
		T visitExpressionStatement(ExpressionStatement node);

		T visitStatementBlock(StatementBlock node);

		T visitVariableDeclaration(VariableDeclaration node);

		T visitTypeAlias(TypeAlias node);

		T visitReturnStatement(ReturnStatement node);

		T visitBreakStatement(BreakStatement node);

		T visitContinueStatement(ContinueStatement node);

		T visitIfStatement(IfStatement node);

		T visitSwitchStatement(SwitchStatement node);

		T visitWhileStatement(WhileStatement node);

		T visitLoopStatement(LoopStatement node);

		T visitIterateStatement(IterateStatement node);

		// Expressions
		T visitBinaryExpression(BinaryExpression node);

		T visitUnaryExpression(UnaryExpression node);

		T visitSymbolExpression(SymbolExpression node);

		T visitIndexExpression(IndexExpression node);

		T visitCallExpression(CallExpression node);

		T visitAccessExpression(AccessExpression node);

		T visitIntExpression(IntExpression node);

		T visitFloatExpression(FloatExpression node);

		T visitNullExpression(NullExpression node);

		T visitSelfExpression(SelfExpression node);

		T visitSuperExpression(SuperExpression node);

		T visitBooleanExpression(BooleanExpression node);

		T visitStringExpression(StringExpression node);
	}
}
