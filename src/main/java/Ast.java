import java.util.*;

import org.antlr.v4.runtime.Token;

import org.apache.commons.text.StringEscapeUtils;

public class Ast {
	public static abstract class Node {
		public final List<Token> tokens;

		public Node(List<Token> tokens) {
			assert tokens != null;

			this.tokens = tokens;
		}

		public abstract <T> T accept(Visitor<T> visitor);
	}

	// ====================
	// PROGRAM & TOP LEVEL
	// ====================

	public static abstract class Program extends Node {
		public Program(List<Token> tokens) { super(tokens); }
	}

	public static class MainProgram extends Program {
		public final List<FileDirective> directives;
		public final List<Statement> statements;

		public MainProgram(List<Token> tokens, List<FileDirective> directives, List<Statement> statements) {
			super(tokens);

			if (directives == null || directives.isEmpty()) {
				assert statements != null && !statements.isEmpty();
			}
			if (statements == null || statements.isEmpty()) {
				assert directives != null && !directives.isEmpty();
			}

			this.directives = directives != null ? directives : new ArrayList<>();
			this.statements = statements != null ? statements : new ArrayList<>();
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitMainProgram(this);
		}
	}

	public static class ClassProgram extends Program {
		public final String namespace;
		public final String className;
		public final List<ClassExtendsAccess> classExtends;
		public final List<FileDirective> directives;
		public final ClassType topLevel;

		public ClassProgram(List<Token> tokens, String namespace, String className, List<ClassExtendsAccess> classExtends, List<FileDirective> directives, ClassType topLevel) {
			super(tokens);

			assert className != null && !className.isEmpty();

			this.namespace = namespace;
			this.className = className;
			this.classExtends = classExtends != null ? classExtends : new ArrayList<>();
			this.directives = directives != null ? directives : new ArrayList<>();
			this.topLevel = topLevel;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitClassProgram(this);
		}
	}

	public static class NamespaceProgram extends Program {
		public final String name;
		public final List<FileDirective> directives;
		public final List<NamespaceMember> members;

		public NamespaceProgram(List<Token> tokens, String name, List<FileDirective> directives, List<NamespaceMember> members) {
			super(tokens);

			if (name != null) {
				assert !name.isEmpty();
			} else {
				assert (directives != null && !directives.isEmpty()) || (members != null && !members.isEmpty());
			}

			this.name = name;
			this.directives = directives != null ? directives : new ArrayList<>();
			this.members = members != null ? members : new ArrayList<>();
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitNamespaceProgram(this);
		}
	}

	// ====================
	// TYPES
	// ====================

	public static abstract class Type extends Node {
		public Type(List<Token> tokens) { super(tokens); }
	}

	public static class UnionType extends Type {
		public enum Kind {
			AND, OR
		}

		public final Kind kind;
		public final Type left;
		public final Type right;

		public UnionType(List<Token> tokens, Kind kind, Type left, Type right) {
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

	public static class SymbolType extends Type {
		public final String name;

		public SymbolType(List<Token> tokens, String name) {
			super(tokens);

			assert name != null;

			this.name = name;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitSymbolType(this);
		}
	}

	public static class ListType extends Type {
		public final Type items;

		public ListType(List<Token> tokens, Type items) {
			super(tokens);
			this.items = items;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitListType(this);
		}
	}

	public static class ArrayType extends Type {
		public final Type items;

		public ArrayType(List<Token> tokens, Type items) {
			super(tokens);
			this.items = items;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitArrayType(this);
		}
	}

	public static class MapType extends Type {
		public final Type keys;
		public final Type values;

		public MapType(List<Token> tokens, Type keys, Type values) {
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

	public static class FunctionType extends Type {
		public final List<FunctionParameter> parameters;
		public final Type returnType;

		public FunctionType(List<Token> tokens, List<FunctionParameter> parameters, Type returnType) {
			super(tokens);

			this.parameters = parameters != null ? parameters : new ArrayList<>();
			this.returnType = returnType;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitFunctionType(this);
		}
	}

	public static class EnumType extends Type {
		public final ClassExtendsAccess extendsAccess;
		public final List<String> memberSymbols;

		public EnumType(List<Token> tokens, ClassExtendsAccess extendsAccess, List<String> memberSymbols) {
			super(tokens);

			if (extendsAccess != null) {
				assert memberSymbols != null && !memberSymbols.isEmpty();
			} else if (memberSymbols == null) {
				memberSymbols = new ArrayList<>();
			}

			this.extendsAccess = extendsAccess;
			this.memberSymbols = memberSymbols;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitEnumType(this);
		}
	}

	public static class ClassType extends Type {
		public final List<ClassMember> members;

		public ClassType(List<Token> tokens, List<ClassMember> members) {
			super(tokens);

			this.members = members != null ? members : new ArrayList<>();
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitClassType(this);
		}
	}

	public static class SelfClassType extends Type {
		public SelfClassType(List<Token> tokens) { super(tokens); }

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitSelfClassType(this);
		}
	}

	// ====================
	// CLASS DEFINITIONS
	// ====================

	public static abstract class ClassMember extends Node {
		public ClassMember(List<Token> tokens) { super(tokens); }
	}

	public static class CastClassMember extends ClassMember {
		public final Type castedType;
		public final StatementBlock block;

		public CastClassMember(List<Token> tokens, Type castedType, StatementBlock block) {
			super(tokens);

			assert castedType != null;
			assert block != null;

			this.castedType = castedType;
			this.block = block;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitCastClassMember(this);
		}
	}

	public static class DeclarationClassMember extends ClassMember {
		public final VariableDeclaration declaration;

		public DeclarationClassMember(List<Token> tokens, VariableDeclaration declaration) {
			super(tokens);

			assert declaration != null;

			this.declaration = declaration;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitDeclarationClassMember(this);
		}
	}

	public static class OperatorOverloadClassMember extends ClassMember {
		public enum Kind {
			PLUS, MINUS, MULTIPLY, DIVIDE, REMAINDER, LESSER_THAN, GREATER_THAN, CONCAT, EXPONENT, FLOOR_DIVIDE, MODULO, EQUAL, INDEX
		}

		public final Kind operator;
		public final Type rightType;
		public final Type returnType;
		public final String symbol;
		public final StatementBlock block;

		public OperatorOverloadClassMember(List<Token> tokens, Kind operator, Type rightType, String symbol, Type returnType, StatementBlock block) {
			super(tokens);

			assert operator != null;
			assert rightType != null;
			assert symbol != null;
			assert !symbol.isEmpty();
			assert returnType != null;
			assert block != null;

			this.operator = operator;
			this.rightType = rightType;
			this.symbol = symbol;
			this.returnType = returnType;
			this.block = block;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitOperatorOverloadClassMember(this);
		}
	}

	public static class ConstructorClassMember extends ClassMember {
		public final List<ConstructorParameter> parameters;
		public final StatementBlock statementBlock;

		public ConstructorClassMember(List<Token> tokens, List<ConstructorParameter> parameters, StatementBlock statementBlock) {
			super(tokens);

			this.parameters = parameters != null ? parameters : new ArrayList<>();
			this.statementBlock = statementBlock;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitConstructorClassMember(this);
		}
	}

	public static class CaptureClassMember extends ClassMember {
		public final ClassExtendsAccess extendsAccess;
		public final String originSymbol;
		public final String targetSymbol;
		public final ExtendsAssignClassMember extendsAssign;

		public CaptureClassMember(
			List<Token> tokens, ClassExtendsAccess extendsAccess, String originSymbol, String targetSymbol, ExtendsAssignClassMember extendsAssign
		) {
			super(tokens);

			assert extendsAccess != null;
			assert originSymbol != null && !originSymbol.isEmpty();
			assert (targetSymbol != null && !targetSymbol.isEmpty() && extendsAssign == null) || (targetSymbol == null && extendsAssign != null);

			this.extendsAccess = extendsAccess;
			this.originSymbol = originSymbol;
			this.targetSymbol = targetSymbol;
			this.extendsAssign = extendsAssign;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitCaptureClassMember(this);
		}
	}

	public static class ExtendsAssignClassMember extends ClassMember {
		public final String symbol;
		public final Expression expression;

		public ExtendsAssignClassMember(List<Token> tokens, String symbol, Expression expression) {
			super(tokens);

			assert symbol != null && !symbol.isEmpty();
			assert expression != null;

			this.symbol = symbol;
			this.expression = expression;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitExtendsAssignClassMember(this);
		}
	}

	// ====================
	// STATEMENTS
	// ====================

	public static abstract class Statement extends Node {
		public Statement(List<Token> tokens) { super(tokens); }
	}

	public static class ExpressionStatement extends Statement {
		public final Expression expression;
		public final boolean isDeferred;

		public ExpressionStatement(List<Token> tokens, Expression expression, boolean isDeferred) {
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

	public static class StatementBlock extends Statement {
		public final List<Statement> statements;
		public final boolean isDeferred;

		public StatementBlock(List<Token> tokens, List<Statement> statements, boolean isDeferred) {
			super(tokens);

			this.statements = statements != null ? statements : new ArrayList<>();
			this.isDeferred = isDeferred;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitStatementBlock(this);
		}
	}

	public static class VariableDeclaration extends Statement {
		public final boolean isConst;
		public final boolean isMutable;
		public final Type type;
		public final String name;
		public final Expression initializer;

		public VariableDeclaration(List<Token> tokens, boolean isConst, boolean isMutable, Type type, String name, Expression initializer) {
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

	public static class Typedef extends Statement {
		public final String name;
		public final Type type;

		public Typedef(List<Token> tokens, String name, Type type) {
			super(tokens);

			assert name != null;
			assert type != null;

			this.name = name;
			this.type = type;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitTypedef(this);
		}
	}

	public static class ReturnStatement extends Statement {
		public final Expression value;

		public ReturnStatement(List<Token> tokens, Expression value) {
			super(tokens);
			this.value = value;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitReturnStatement(this);
		}
	}

	public static class BreakStatement extends Statement {
		public BreakStatement(List<Token> tokens) { super(tokens); }

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitBreakStatement(this);
		}
	}

	public static class ContinueStatement extends Statement {
		public ContinueStatement(List<Token> tokens) { super(tokens); }

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitContinueStatement(this);
		}
	}

	public static class IfStatement extends Statement {
		public final Expression test;
		public final StatementBlock thenBranch;
		public final List<ElifBranch> elifBranches;
		public final StatementBlock elseBranch;

		public IfStatement(List<Token> tokens, Expression test, StatementBlock thenBranch, List<ElifBranch> elifBranches, StatementBlock elseBranch) {
			super(tokens);

			assert test != null;
			assert thenBranch != null;

			this.test = test;
			this.thenBranch = thenBranch;
			this.elifBranches = elifBranches != null ? elifBranches : new ArrayList<>();
			this.elseBranch = elseBranch;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitIfStatement(this);
		}
	}

	public static class ElifBranch extends Node {
		public final Expression test;
		public final StatementBlock body;

		public ElifBranch(List<Token> tokens, Expression test, StatementBlock body) {
			super(tokens);

			assert test != null;
			assert body != null;

			this.test = test;
			this.body = body;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitElifBranch(this);
		}
	}

	public static class SwitchStatement extends Statement {
		public final Expression value;
		public final List<CaseBranch> cases;
		public final StatementBlock defaultCase;

		public SwitchStatement(List<Token> tokens, Expression value, List<CaseBranch> cases, StatementBlock defaultCase) {
			super(tokens);

			assert value != null;
			assert cases != null;
			assert !cases.isEmpty();

			this.value = value;
			this.cases = cases;
			this.defaultCase = defaultCase;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitSwitchStatement(this);
		}
	}

	public static class CaseBranch extends Node {
		public final List<Expression> values;
		public final StatementBlock body;

		public CaseBranch(List<Token> tokens, List<Expression> values, StatementBlock body) {
			super(tokens);

			assert values != null;
			assert !values.isEmpty();
			assert body != null;

			this.values = values;
			this.body = body;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitCaseBranch(this);
		}
	}

	public static class WhileStatement extends Statement {
		public final Expression test;
		public final StatementBlock body;

		public WhileStatement(List<Token> tokens, Expression test, StatementBlock body) {
			super(tokens);

			assert test != null;
			assert body != null;

			this.test = test;
			this.body = body;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitWhileStatement(this);
		}
	}

	public static class LoopStatement extends Statement {
		public final Expression from;
		public final Expression to;
		public final Expression by;
		public final String variable;
		public final StatementBlock body;

		public LoopStatement(List<Token> tokens, Expression from, Expression to, Expression by, String variable, StatementBlock body) {
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

	public static class IterateStatement extends Statement {
		public final Expression iterable;
		public final String indexVariable;
		public final String elementVariable;
		public final StatementBlock body;

		public IterateStatement(List<Token> tokens, Expression iterable, String indexVariable, String elementVariable, StatementBlock body) {
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

	public static abstract class Expression extends Node {
		public Expression(List<Token> tokens) { super(tokens); }
	}

	public static class BinaryExpression extends Expression {
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

		public BinaryExpression(List<Token> tokens, Kind operation, Expression left, Expression right) {
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

	public static class UnaryExpression extends Expression {
		public enum Kind {
			NOT, BIT_NOT, PLUS, MINUS,
		}

		public final Kind operation;
		public final Expression operand;

		public UnaryExpression(List<Token> tokens, Kind operation, Expression operand) {
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

	public static class IndexExpression extends Expression {
		public final Expression base;
		public final Expression index;

		public IndexExpression(List<Token> tokens, Expression base, Expression index) {
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

	public static class AccessExpression extends Expression {
		public final Expression object;
		public final String member;
		public final boolean optional;

		public AccessExpression(List<Token> tokens, Expression object, String member, boolean optional) {
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

	public static class CallExpression extends Expression {
		public final Expression function;
		public final List<Argument> arguments;

		public CallExpression(List<Token> tokens, Expression function, List<Argument> arguments) {
			super(tokens);

			assert function != null;

			this.function = function;
			this.arguments = arguments != null ? arguments : new ArrayList<>();
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitCallExpression(this);
		}
	}

	public static class SymbolExpression extends Expression {
		public final String symbol;

		public SymbolExpression(List<Token> tokens, String symbol) {
			super(tokens);

			assert symbol != null;
			assert !symbol.isEmpty();

			this.symbol = symbol;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitSymbolExpression(this);
		}
	}

	public static class IntExpression extends Expression {
		public final long value;
		public final byte precision;
		public final boolean signed;

		public IntExpression(List<Token> tokens, long value, byte precision, boolean signed) {
			super(tokens);

			assert precision == 8 || precision == 16 || precision == 32 || precision == 64;

			this.value = value;
			this.precision = precision;
			this.signed = signed;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitIntExpression(this);
		}
	}

	public static class FloatExpression extends Expression {
		public final double value;
		public final byte precision;

		public FloatExpression(List<Token> tokens, double value, byte precision) {
			super(tokens);

			assert precision == 8 || precision == 16 || precision == 32 || precision == 64;

			this.value = value;
			this.precision = precision;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitFloatExpression(this);
		}
	}

	public static class NullExpression extends Expression {
		public NullExpression(List<Token> tokens) {
			super(tokens);
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitNullExpression(this);
		}
	}

	public static class SelfExpression extends Expression {
		public SelfExpression(List<Token> tokens) {
			super(tokens);
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitSelfExpression(this);
		}
	}

	public static class SuperExpression extends Expression {
		public SuperExpression(List<Token> tokens) {
			super(tokens);
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitSuperExpression(this);
		}
	}

	public static class BooleanExpression extends Expression {
		public final boolean value;

		public BooleanExpression(List<Token> tokens, boolean value) {
			super(tokens);
			this.value = value;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitBooleanExpression(this);
		}
	}

	public static class StringExpression extends Expression {
		public final String value;

		// Will unescape string if not raw
		public StringExpression(List<Token> tokens, String value, boolean raw) {
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

	public static class LambdaExpression extends Expression {
		public final FunctionType type;
		public final StatementBlock body;

		public LambdaExpression(List<Token> tokens, FunctionType type, StatementBlock body) {
			super(tokens);

			assert type != null;
			assert body != null;

			this.type = type;
			this.body = body;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitLambdaExpression(this);
		}
	}

	public static class SelectExpression extends Expression {
		public final Expression match;
		public final List<KeyValuePair> branches;

		public SelectExpression(List<Token> tokens, Expression match, List<KeyValuePair> branches) {
			super(tokens);

			assert branches != null;
			assert !branches.isEmpty();

			this.match = match;
			this.branches = branches;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitSelectExpression(this);
		}
	}

	public static class NewMapExpression extends Expression {
		public final MapType type;
		public final List<KeyValuePair> keyValuePairs;

		public NewMapExpression(List<Token> tokens, MapType type, List<KeyValuePair> keyValuePairs) {
			super(tokens);

			assert type != null;

			this.type = type;
			this.keyValuePairs = keyValuePairs != null ? keyValuePairs : new ArrayList<>();
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitNewMapExpression(this);
		}
	}

	public static class NewListExpression extends Expression {
		public final ListType type;
		public final List<Argument> elements;

		public NewListExpression(List<Token> tokens, ListType type, List<Argument> elements) {
			super(tokens);

			assert type != null;

			this.type = type;
			this.elements = elements != null ? elements : new ArrayList<>();
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitNewListExpression(this);
		}
	}

	public static class NewArrayExpression extends Expression {
		public final ArrayType type;
		public final List<Argument> elements;

		public NewArrayExpression(List<Token> tokens, ArrayType type, List<Argument> elements) {
			super(tokens);

			assert type != null;

			this.type = type;
			this.elements = elements != null ? elements : new ArrayList<>();
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitNewArrayExpression(this);
		}
	}

	public static class NewObjectExpression extends Expression {
		public final String symbol;
		public final List<Argument> arguments;

		public NewObjectExpression(List<Token> tokens, String symbol, List<Argument> arguments) {
			super(tokens);

			assert symbol != null;
			assert !symbol.isEmpty();

			this.symbol = symbol;
			this.arguments = arguments != null ? arguments : new ArrayList<>();
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitNewObjectExpression(this);
		}
	}

	public static class NewClassInstance extends Expression {
		public final ClassType classType;
		public final List<Argument> arguments;

		public NewClassInstance(List<Token> tokens, ClassType classType, List<Argument> arguments) {
			super(tokens);

			assert classType != null;

			this.classType = classType;
			this.arguments = arguments != null ? arguments : new ArrayList<>();
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitNewClassInstance(this);
		}
	}

	// ====================
	// UTILITIES
	// ====================

	public record FileDirective(
		String name,
		String argument
	) {
		public FileDirective {
			assert name != null && !name.isEmpty();
		}
	}

	public sealed interface NamespaceMember permits NamespaceDeclaration, NamespaceTypedef {}

	record NamespaceDeclaration(VariableDeclaration declaration) implements NamespaceMember {}

	record NamespaceTypedef(Typedef typedef) implements NamespaceMember {}

	public record ConstructorParameter(
		Type type,
		String symbol,
		Expression initialValue,
		boolean isVarArgs
	) {
		public ConstructorParameter {
			assert symbol != null;
			assert !symbol.isEmpty();

			if (type == null) {
				assert !isVarArgs;
				assert initialValue == null;
			} else if (isVarArgs) {
				assert initialValue == null;
			}
		}
	}

	public record ClassExtendsAccess(
		List<String> symbols
	) {
		public ClassExtendsAccess {
			assert symbols != null;
			assert !symbols.isEmpty();
		}
	}

	public record FunctionParameter(
		Type type,
		String symbol,
		Expression initialValue,
		boolean isVarArgs
	) {
		FunctionParameter(ConstructorParameter param) {
			this(param.type, param.symbol, param.initialValue, param.isVarArgs);
		}

		public FunctionParameter {
			assert type != null;
			assert symbol != null;
			assert !symbol.isEmpty();
			assert (isVarArgs && initialValue == null) || !isVarArgs;
		}
	}

	public record Argument(
		Expression value,
		String keyword,
		boolean isBlank
	) {
		public Argument {
			assert (value != null && !isBlank) || (value == null && keyword == null && isBlank);
		}
	}

	public record ClassName(List<String> symbols) {
		public ClassName {
			assert symbols != null;
			assert !symbols.isEmpty();
		}

		@Override
		public String toString() {
			return String.join(".", symbols);
		}
	}

	public record KeyValuePair(Expression left, Expression right) {
		public KeyValuePair {
			assert left != null;
			assert right != null;
		}
	}

	// ====================
	// VISITOR PATTERN
	// ====================

	public static interface Visitor<T> {
		T visitMainProgram(Program node);

		T visitClassProgram(Program node);

		T visitNamespaceProgram(NamespaceProgram node);

		// Types
		T visitSymbolType(SymbolType node);

		T visitListType(ListType node);

		T visitArrayType(ArrayType node);

		T visitMapType(MapType node);

		T visitFunctionType(FunctionType node);

		T visitEnumType(EnumType node);

		T visitClassType(ClassType node);

		T visitUnionType(UnionType node);

		T visitSelfClassType(SelfClassType node);

		T visitCastClassMember(CastClassMember node);

		T visitDeclarationClassMember(DeclarationClassMember node);

		T visitConstructorClassMember(ConstructorClassMember node);

		T visitCaptureClassMember(CaptureClassMember node);

		T visitExtendsAssignClassMember(ExtendsAssignClassMember node);

		T visitOperatorOverloadClassMember(OperatorOverloadClassMember node);

		// Statements
		T visitExpressionStatement(ExpressionStatement node);

		T visitStatementBlock(StatementBlock node);

		T visitVariableDeclaration(VariableDeclaration node);

		T visitTypedef(Typedef node);

		T visitReturnStatement(ReturnStatement node);

		T visitBreakStatement(BreakStatement node);

		T visitContinueStatement(ContinueStatement node);

		T visitIfStatement(IfStatement node);

		T visitElifBranch(ElifBranch node);

		T visitSwitchStatement(SwitchStatement node);

		T visitCaseBranch(CaseBranch node);

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

		T visitLambdaExpression(LambdaExpression node);

		T visitSelectExpression(SelectExpression node);

		T visitNewMapExpression(NewMapExpression node);

		T visitNewListExpression(NewListExpression node);

		T visitNewArrayExpression(NewArrayExpression node);

		T visitNewObjectExpression(NewObjectExpression node);

		T visitNewClassInstance(NewClassInstance node);
	}
}
