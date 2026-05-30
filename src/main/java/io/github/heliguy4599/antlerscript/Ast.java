package io.github.heliguy4599.antlerscript;

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

		@Override
		public boolean equals(Object object) {
			if (this == object) {
				return true;
			}

			if (object == null || getClass() != object.getClass()) {
				return false;
			}

			Node node = (Node) object;

			if (tokens.size() != node.tokens.size()) {
				return false;
			}

			for (int i = 0; i < tokens.size(); i++) {
				if (
					!tokens
					.get(i)
					.getText()
					.equals(node.tokens.get(i).getText())
				) {
					return false;
				}
			}

			return true;
		}

		public abstract <T> T accept(Visitor<T> visitor);
	}

	// ====================
	// PROGRAM & TOP LEVEL
	// ====================

	public static abstract class Program extends Node {
		// :: using MyNamespace.MyClass, MyNamespace.MyOtherNamespace.MyVar
		// turns into -> [[MyNamespace, MyClass], [MyNamespace, MyOtherNamespace, MyVar]]
		public final List<SymbolChain> using;
		public final List<FileDirective> directives;
		public Program(
			List<Token> tokens,
			List<SymbolChain> using,
			List<FileDirective> directives
		) {
			super(tokens);
			this.using = using != null ? using : new ArrayList<>();
			this.directives = directives != null
				? directives
				: new ArrayList<>();
		}

		@Override public boolean equals(Object object) {
			if (!super.equals(object)) {
				return false;
			}

			var other = (Program) object;

			return Objects.equals(using, other.using)
				&& Objects.equals(directives, other.directives);
		}
	}

	public static class MainProgram extends Program {
		public final List<Statement> statements;

		public MainProgram(
			List<Token> tokens,
			List<SymbolChain> using,
			List<FileDirective> directives,
			List<Statement> statements
		) {
			super(tokens, using, directives);

			this.statements = statements != null
				? statements
				: new ArrayList<>();
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitMainProgram(this);
		}

		@Override
		public boolean equals(Object object) {
			if (!super.equals(object)) {
				return false;
			}

			var other = (MainProgram) object;

			return Objects.equals(statements, other.statements);
		}
	}

	public static class ClassProgram extends Program {
		public final String namespace;
		public final String className;
		public final ClassType topLevel;

		public ClassProgram(
			List<Token> tokens,
			List<SymbolChain> using,
			List<FileDirective> directives,
			String namespace,
			String className,
			ClassType topLevel
		) {
			super(tokens, using, directives);

			assert className != null && !className.isEmpty();
			assert topLevel != null;

			this.namespace = namespace;
			this.className = className;
			this.topLevel = topLevel;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitClassProgram(this);
		}

		@Override
		public boolean equals(Object object) {
			if (!super.equals(object)) {
				return false;
			}

			var other = (ClassProgram) object;

			return Objects.equals(namespace, other.namespace)
				&& Objects.equals(className, other.className)
				&& Objects.equals(topLevel, other.topLevel);
		}
	}

	public static class NamespaceProgram extends Program {
		public final String name;
		public final List<NamespaceMember> members;

		public NamespaceProgram(
			List<Token> tokens,
			List<SymbolChain> using,
			List<FileDirective> directives,
			String name,
			List<NamespaceMember> members
		) {
			super(tokens, using, directives);

			this.name = name == null ? "" : name;
			this.members = members != null
				? members
				: new ArrayList<>();
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitNamespaceProgram(this);
		}

		@Override
		public boolean equals(Object object) {
			if (!super.equals(object)) {
				return false;
			}

			var other = (NamespaceProgram) object;

			return Objects.equals(name, other.name)
				&& Objects.equals(members, other.members);
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

		public UnionType(
			List<Token> tokens,
			Kind kind,
			Type left,
			Type right
		) {
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

		@Override
		public boolean equals(Object object) {
			if (!super.equals(object)) {
				return false;
			}

			var other = (UnionType) object;

			return kind == other.kind
				&& Objects.equals(left, other.left)
				&& Objects.equals(right, other.right);
		}
	}

	public static class SymbolType extends Type {
		public final String name;
		public final List<Type> genericCast;

		public SymbolType(List<Token> tokens, String name, List<Type> genericArgs) {
			super(tokens);

			assert name != null;

			this.name = name;
			this.genericCast = genericArgs != null
				? genericArgs
				: new ArrayList<>();
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitSymbolType(this);
		}

		@Override
		public boolean equals(Object object) {
			if (!super.equals(object)) {
				return false;
			}

			var other = (SymbolType) object;

			return Objects.equals(name, other.name)
				&& Objects.equals(genericCast, other.genericCast);
		}
	}

	public static class ArrayType extends Type {
		public final Type items;
		public final Expression size;

		public ArrayType(
			List<Token> tokens,
			Type items,
			Expression size
		) {
			super(tokens);

			assert items != null;
			assert size != null;

			this.items = items;
			this.size = size;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitArrayType(this);
		}

		@Override
		public boolean equals(Object object) {
			if (!super.equals(object)) {
				return false;
			}

			var other = (ArrayType) object;

			return Objects.equals(items, other.items)
				&& Objects.equals(size, other.size);
		}
	}


	public static abstract class FunctionType extends Type {
		public FunctionType(List<Token> tokens) { super(tokens); }
	}

	public static class FullFunctionType extends FunctionType {
		public final List<GenericParameter> genericParameters;
		public final List<FunctionParameter> parameters;
		public final Type returnType;
		public final Type errorType;

		public FullFunctionType(
			List<Token> tokens,
			List<GenericParameter> genericParameters,
			List<FunctionParameter> parameters,
			Type returnType,
			Type errorType
		) {
			super(tokens);

			this.parameters = parameters != null
				? parameters
				: new ArrayList<>();
			this.genericParameters = genericParameters != null
				? genericParameters
				: new ArrayList<>();
			this.returnType = returnType;
			this.errorType = errorType;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitFullFunctionType(this);
		}

		@Override
		public boolean equals(Object object) {
			if (!super.equals(object)) {
				return false;
			}

			var other = (FullFunctionType) object;

			return Objects.equals(parameters, other.parameters)
				&& Objects.equals(genericParameters, other.genericParameters)
				&& Objects.equals(returnType, other.returnType)
				&& Objects.equals(errorType, other.errorType);
		}
	}

	public static class InferredFunctionType extends FunctionType {
		public final List<String> parameters;
		public final String varArgs;
		public final boolean canThrow;

		public InferredFunctionType(
			List<Token> tokens,
			List<String> parameters,
			// Note: the varArgs arg appears both here and in
			// parameters
			String varArgs,
			boolean canThrow
		) {
			super(tokens);

			this.parameters = parameters != null
				? parameters
				: new ArrayList<>();
			this.varArgs = varArgs;
			this.canThrow = canThrow;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitInferredFunctionType(this);
		}

		@Override
		public boolean equals(Object object) {
			if (!super.equals(object)) {
				return false;
			}

			var other = (InferredFunctionType) object;

			return Objects.equals(parameters, other.parameters)
				&& Objects.equals(varArgs, other.varArgs)
				&& canThrow == other.canThrow;
		}
	}

	public static abstract class CoroutineType extends Type {
		public CoroutineType(List<Token> tokens) { super(tokens); }
	}

	public static class FullCoroutineType extends CoroutineType {
		public final List<FunctionParameter> parameters;
		public final List<GenericParameter> genericParameters;
		public final Type returnType;
		public final Type yieldIn;
		public final Type yieldOut;

		public FullCoroutineType(
			List<Token> tokens,
			List<GenericParameter> genericParameters,
			List<FunctionParameter> parameters,
			Type returnType,
			Type yieldIn,
			Type yieldOut
		) {
			super(tokens);

			this.parameters = parameters != null
				? parameters
				: new ArrayList<>();
			this.genericParameters = genericParameters != null
				? genericParameters
				: new ArrayList<>();
			this.returnType = returnType;
			this.yieldIn = yieldIn;
			this.yieldOut = yieldOut;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitFullCoroutineType(this);
		}

		@Override
		public boolean equals(Object object) {
			if (!super.equals(object)) {
				return false;
			}

			var other = (FullCoroutineType) object;

			return Objects.equals(parameters, other.parameters)
				&& Objects.equals(genericParameters, other.genericParameters)
				&& Objects.equals(returnType, other.returnType)
				&& Objects.equals(yieldIn, other.yieldIn)
				&& Objects.equals(yieldOut, other.yieldOut);
		}
	}

	public static class InferredCoroutineType extends CoroutineType {
		public final List<String> parameters;
		public final String varArgs;
		public final boolean canYield;

		public InferredCoroutineType(
			List<Token> tokens,
			List<String> parameters,
			String varArgs,
			boolean canYield
		) {
			super(tokens);

			this.parameters = parameters != null
				? parameters
				: new ArrayList<>();
			this.varArgs = varArgs;
			this.canYield = canYield;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitInferredCoroutineType(this);
		}

		@Override
		public boolean equals(Object object) {
			if (!super.equals(object)) {
				return false;
			}

			var other = (InferredCoroutineType) object;

			return Objects.equals(parameters, other.parameters)
				&& Objects.equals(varArgs, other.varArgs)
				&& canYield == other.canYield;
		}
	}

	public static class EnumType extends Type {
		public final SymbolChain extendsAccess;
		public final List<String> memberSymbols;

		public EnumType(
			List<Token> tokens,
			SymbolChain extendsAccess,
			List<String> memberSymbols
		) {
			super(tokens);

			if (extendsAccess == null) {
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

		@Override
		public boolean equals(Object object) {
			if (!super.equals(object)) {
				return false;
			}

			var other = (EnumType) object;

			return Objects.equals(extendsAccess, other.extendsAccess)
				&& Objects.equals(memberSymbols, other.memberSymbols);
		}
	}

	public static class ClassType extends Type {
		public final List<GenericParameter> genericParameters;
		public final List<SymbolChain> classExtends;
		public final List<ClassMember> members;

		public ClassType(
			List<Token> tokens,
			List<GenericParameter> genericParameters,
			List<SymbolChain> classExtends,
			List<ClassMember> members
		) {
			super(tokens);

			this.genericParameters = genericParameters != null
				? genericParameters
				: new ArrayList<>();
			this.classExtends = classExtends != null
				? classExtends
				: new ArrayList<>();
			this.members = members != null
				? members
				: new ArrayList<>();
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitClassType(this);
		}

		@Override
		public boolean equals(Object object) {
			if (!super.equals(object)) {
				return false;
			}

			var other = (ClassType) object;

			return Objects.equals(genericParameters, other.genericParameters)
				&& Objects.equals(classExtends, other.classExtends)
				&& Objects.equals(members, other.members);
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

			this.castedType = castedType;
			this.block = block;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitCastClassMember(this);
		}

		@Override
		public boolean equals(Object object) {
			if (!super.equals(object)) {
				return false;
			}

			var other = (CastClassMember) object;

			return Objects.equals(castedType, other.castedType)
				&& Objects.equals(block, other.block);
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

		@Override
		public boolean equals(Object object) {
			if (!super.equals(object)) {
				return false;
			}

			var other = (DeclarationClassMember) object;

			return Objects.equals(declaration, other.declaration);
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

		@Override
		public boolean equals(Object object) {
			if (!super.equals(object)) {
				return false;
			}

			var other = (OperatorOverloadClassMember) object;

			return operator == other.operator
				&& Objects.equals(rightType, other.rightType)
				&& Objects.equals(symbol, other.symbol)
				&& Objects.equals(returnType, other.returnType)
				&& Objects.equals(block, other.block);
		}
	}

	public static class ConstructorClassMember extends ClassMember {
		public final List<ConstructorParameter> parameters;
		public final StatementBlock statementBlock;

		public ConstructorClassMember(
			List<Token> tokens,
			List<ConstructorParameter> parameters,
			StatementBlock statementBlock
		) {
			super(tokens);

			assert statementBlock != null;

			this.parameters = parameters != null ? parameters : new ArrayList<>();
			this.statementBlock = statementBlock;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitConstructorClassMember(this);
		}

		@Override
		public boolean equals(Object object) {
			if (!super.equals(object)) {
				return false;
			}

			var other = (ConstructorClassMember) object;

			return Objects.equals(parameters, other.parameters)
				&& Objects.equals(statementBlock, other.statementBlock);
		}
	}

	public static class AliasClassMember extends ClassMember {
		public final SymbolChain extendsAccess;
		public final String originSymbol;
		public final String targetSymbol;
		public final ExtendsAssignClassMember extendsAssign;

		public AliasClassMember(
			List<Token> tokens, SymbolChain extendsAccess, String originSymbol, String targetSymbol, ExtendsAssignClassMember extendsAssign
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

		@Override
		public boolean equals(Object object) {
			if (!super.equals(object)) {
				return false;
			}

			var other = (AliasClassMember) object;

			return Objects.equals(extendsAccess, other.extendsAccess)
				&& Objects.equals(originSymbol, other.originSymbol)
				&& Objects.equals(targetSymbol, other.targetSymbol)
				&& Objects.equals(extendsAssign, other.extendsAssign);
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

		@Override
		public boolean equals(Object object) {
			if (!super.equals(object)) {
				return false;
			}

			var other = (ExtendsAssignClassMember) object;

			return Objects.equals(symbol, other.symbol)
				&& Objects.equals(expression, other.expression);
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

		@Override
		public boolean equals(Object object) {
			if (!super.equals(object)) {
				return false;
			}

			var other = (ExpressionStatement) object;

			return Objects.equals(expression, other.expression)
				&& isDeferred == other.isDeferred;
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

		@Override
		public boolean equals(Object object) {
			if (!super.equals(object)) {
				return false;
			}

			var other = (StatementBlock) object;

			return Objects.equals(statements, other.statements)
				&& isDeferred == other.isDeferred;
		}
	}

	public static class VariableDeclaration extends Statement {
		public final boolean isConst;
		public final boolean isMutable;
		public final boolean isSealed;
		public final Type type;
		public final String name;
		public final Expression initializer;
		public final List<Decorator> decorators;

		public VariableDeclaration(
			List<Token> tokens,
			boolean isConst,
			boolean isMutable,
			boolean isSealed,
			Type type,
			String name,
			Expression initializer,
			List<Decorator> decorators
		) {
			super(tokens);

			assert type != null || initializer != null;
			assert !(isMutable && isSealed);
			assert name != null && !name.isEmpty();
			assert !isConst || initializer != null;
			assert !isConst || (!isMutable && !isSealed);

			this.isConst = isConst;
			this.isMutable = isMutable;
			this.isSealed = isSealed;
			this.type = type;
			this.name = name;
			this.initializer = initializer;
			this.decorators = decorators == null ? new ArrayList<>() : decorators;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitVariableDeclaration(this);
		}

		@Override
		public boolean equals(Object object) {
			if (!super.equals(object)) {
				return false;
			}

			var other = (VariableDeclaration) object;

			return isConst == other.isConst
				&& isMutable == other.isMutable
				&& isSealed == other.isSealed
				&& Objects.equals(type, other.type)
				&& Objects.equals(name, other.name)
				&& Objects.equals(initializer, other.initializer)
				&& Objects.equals(decorators, other.decorators);
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

		@Override
		public boolean equals(Object object) {
			if (!super.equals(object)) {
				return false;
			}

			var other = (Typedef) object;

			return Objects.equals(name, other.name)
				&& Objects.equals(type, other.type);
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

		@Override
		public boolean equals(Object object) {
			if (!super.equals(object)) {
				return false;
			}

			var other = (ReturnStatement) object;

			return Objects.equals(value, other.value);
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

		public IfStatement(
			List<Token> tokens,
			Expression test,
			StatementBlock thenBranch,
			List<ElifBranch> elifBranches,
			StatementBlock elseBranch
		) {
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

		@Override
		public boolean equals(Object object) {
			if (!super.equals(object)) {
				return false;
			}

			var other = (IfStatement) object;

			return Objects.equals(test, other.test)
				&& Objects.equals(thenBranch, other.thenBranch)
				&& Objects.equals(elifBranches, other.elifBranches)
				&& Objects.equals(elseBranch, other.elseBranch);
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

		@Override
		public boolean equals(Object object) {
			if (!super.equals(object)) {
				return false;
			}

			var other = (ElifBranch) object;

			return Objects.equals(test, other.test)
				&& Objects.equals(body, other.body);
		}
	}

	public static class SwitchStatement extends Statement {
		public final Expression value;
		public final List<CaseBranch> cases;
		public final StatementBlock defaultCase;

		public SwitchStatement(
			List<Token> tokens,
			Expression value,
			List<CaseBranch> cases,
			StatementBlock defaultCase
		) {
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

		@Override
		public boolean equals(Object object) {
			if (!super.equals(object)) {
				return false;
			}

			var other = (SwitchStatement) object;

			return Objects.equals(value, other.value)
				&& Objects.equals(cases, other.cases)
				&& Objects.equals(defaultCase, other.defaultCase);
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

		@Override
		public boolean equals(Object object) {
			if (!super.equals(object)) {
				return false;
			}

			var other = (CaseBranch) object;

			return Objects.equals(values, other.values)
				&& Objects.equals(body, other.body);
		}
	}

	public static abstract class LoopStatement extends Statement {
		public final StatementBlock body;

		public enum TestPosition {
			LEFT, RIGHT
		}

		public LoopStatement(
			List<Token> tokens,
			StatementBlock body
		) {
			super(tokens);

			assert body != null;

			this.body = body;
		}

		@Override
		public boolean equals(Object object) {
			if (!super.equals(object)) {
				return false;
			}

			var other = (LoopStatement) object;

			return Objects.equals(body, other.body);
		}
	}

	public static class LoopInfiniteStatement extends LoopStatement {
		String capture;

		public LoopInfiniteStatement(
			List<Token> tokens,
			StatementBlock body,
			String capture
		) {
			super(tokens, body);

			if (capture != null) {
				assert !capture.isEmpty();
			}

			this.capture = capture;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitLoopInfiniteStatement(this);
		}

		@Override
		public boolean equals(Object object) {
			if (!super.equals(object)) {
				return false;
			}

			var other = (LoopInfiniteStatement) object;

			return Objects.equals(capture, other.capture);
		}
	}

	public static class LoopWhileStatement extends LoopStatement {
		public final Expression test;
		public final String capture;

		public LoopWhileStatement(
			List<Token> tokens,
			StatementBlock body,
			Expression test,
			String capture
		) {
			super(tokens, body);

			assert test != null;
			if (capture != null) {
				assert !capture.isEmpty();
			}

			this.test = test;
			this.capture = capture;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitLoopWhileStatement(this);
		}

		@Override
		public boolean equals(Object object) {
			if (!super.equals(object)) {
				return false;
			}

			var other = (LoopWhileStatement) object;

			return Objects.equals(test, other.test)
				&& Objects.equals(capture, other.capture);
		}
	}

	public static class LoopRangeStatement extends LoopStatement {
		public final String capture;
		public final Expression from;
		public final Expression to;
		public final Expression by;
		public final Expression test;
		public final TestPosition testPosition;

		public LoopRangeStatement(
			List<Token> tokens,
			StatementBlock block,
			String capture,
			Expression from,
			Expression to,
			Expression by,
			Expression test,
			TestPosition testPosition
		) {
			super(tokens, block);

			assert from != null || to != null || by != null;
			if (test != null) {
				assert testPosition != null;
			}
			if (capture != null) {
				assert !capture.isEmpty();
			}

			this.capture = capture;
			this.from = from;
			this.to = to;
			this.by = by;
			this.test = test;
			this.testPosition = testPosition;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitLoopRangeStatement(this);
		}

		@Override
		public boolean equals(Object object) {
			if (!super.equals(object)) {
				return false;
			}

			var other = (LoopRangeStatement) object;

			return Objects.equals(capture, other.capture)
				&& Objects.equals(from, other.from)
				&& Objects.equals(to, other.to)
				&& Objects.equals(by, other.by)
				&& Objects.equals(test, other.test)
				&& testPosition == other.testPosition;
		}
	}

	public static class LoopIterationStatement extends LoopStatement {
		public final Expression collection;
		public final String indexCapture;
		public final String elementCapture;
		public final Expression test;
		public final TestPosition testPosition;

		public LoopIterationStatement(
			List<Token> tokens,
			StatementBlock block,
			Expression collection,
			String indexCapture,
			String elementCapture,
			Expression test,
			TestPosition testPosition
		) {
			super(tokens, block);

			assert collection != null;
			if (indexCapture != null) {
				assert elementCapture != null;
			}
			if (test != null) {
				assert testPosition != null;
			}

			this.collection = collection;
			this.indexCapture = indexCapture;
			this.elementCapture = elementCapture;
			this.test = test;
			this.testPosition = testPosition;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitLoopIterationStatement(this);
		}

		@Override
		public boolean equals(Object object) {
			if (!super.equals(object)) {
				return false;
			}

			var other = (LoopIterationStatement) object;

			return Objects.equals(collection, other.collection)
				&& Objects.equals(indexCapture, other.indexCapture)
				&& Objects.equals(elementCapture, other.elementCapture)
				&& Objects.equals(test, other.test)
				&& testPosition == other.testPosition;
		}
	}

	public static class ThrowStatement extends Statement {
		public final Expression thrown;

		public ThrowStatement(List<Token> tokens, Expression thrown) {
			super(tokens);

			assert thrown != null;

			this.thrown = thrown;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitThrowStatement(this);
		}

		@Override
		public boolean equals(Object object) {
			if (!super.equals(object)) {
				return false;
			}

			var other = (ThrowStatement) object;

			return Objects.equals(thrown, other.thrown);
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
			PLUS_ASSIGN, MINUS_ASSIGN, MULTIPLY_ASSIGN, EXPONENT_ASSIGN, DIVIDE_ASSIGN, FLOOR_DIVIDE_ASSIGN, MODULO_ASSIGN, REMAINDER_ASSIGN, BIT_OR_ASSIGN, BIT_AND_ASSIGN, BIT_NOT_ASSIGN, BIT_XOR_ASSIGN, BIT_LSHIFT_ASSIGN, BIT_RSHIFT_ASSIGN, CONCAT_ASSIGN, NULLISH_ASSIGN, CHAIN_ASSIGN, ASSIGN,

			// Comparison
			OR, NULLISH, AND, LESSER_THAN, GREATER_THAN, LESSER_OR_EQUAL, GREATER_OR_EQUAL, EQUAL, NOT_EQUAL, IS, IN, AS,

			// Function pipe
			FUNC_PIPE,

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

		public BinaryExpression(
			List<Token> tokens,
			Kind operation,
			Expression left,
			Expression right
		) {
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

		@Override
		public boolean equals(Object object) {
			if (!super.equals(object)) {
				return false;
			}

			var other = (BinaryExpression) object;

			return operation == other.operation
				&& Objects.equals(left, other.left)
				&& Objects.equals(right, other.right);
		}
	}

	public static class UnaryExpression extends Expression {
		public enum Kind {
			NOT, BIT_NOT, PLUS, MINUS,
		}

		public final Kind operation;
		public final Expression operand;

		public UnaryExpression(
			List<Token> tokens,
			Kind operation,
			Expression operand
		) {
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

		@Override
		public boolean equals(Object object) {
			if (!super.equals(object)) {
				return false;
			}

			var other = (UnaryExpression) object;

			return operation == other.operation
				&& Objects.equals(operand, other.operand);
		}
	}

	public static class IndexExpression extends Expression {
		public final Expression base;
		public final Expression index;
		public final List<Type> genericCast;

		public IndexExpression(
			List<Token> tokens,
			Expression base,
			Expression index,
			List<Type> genericCast
		) {
			super(tokens);

			assert base != null;
			assert index != null;

			this.base = base;
			this.index = index;
			this.genericCast = genericCast != null
				? genericCast
				: new ArrayList<>();
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitIndexExpression(this);
		}

		@Override
		public boolean equals(Object object) {
			if (!super.equals(object)) {
				return false;
			}

			var other = (IndexExpression) object;

			return Objects.equals(base, other.base)
				&& Objects.equals(index, other.index)
				&& Objects.equals(genericCast, other.genericCast);
		}
	}

	public static class AccessExpression extends Expression {
		public final Expression object;
		public final String member;
		public final boolean optional;
		public final List<Type> genericCast;

		public AccessExpression(
			List<Token> tokens,
			Expression object,
			String member,
			boolean optional,
			List<Type> genericCast
		) {
			super(tokens);

			assert object != null;
			assert member != null;

			this.object = object;
			this.member = member;
			this.optional = optional;
			this.genericCast = genericCast != null
				? genericCast
				: new ArrayList<>();
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitAccessExpression(this);
		}

		@Override
		public boolean equals(Object object) {
			if (!super.equals(object)) {
				return false;
			}

			var other = (AccessExpression) object;

			return Objects.equals(this.object, other.object)
				&& Objects.equals(member, other.member)
				&& optional == other.optional
				&& Objects.equals(genericCast, other.genericCast);
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

		@Override
		public boolean equals(Object object) {
			if (!super.equals(object)) {
				return false;
			}

			var other = (CallExpression) object;

			return Objects.equals(function, other.function)
				&& Objects.equals(arguments, other.arguments);
		}
	}

	public static class SymbolExpression extends Expression {
		public final String symbol;
		public final List<Type> genericCast;

		public SymbolExpression(
			List<Token> tokens,
			String symbol,
			List<Type> genericCast
		) {
			super(tokens);

			assert symbol != null;
			assert !symbol.isEmpty();

			this.symbol = symbol;
			this.genericCast = genericCast != null
				? genericCast
				: new ArrayList<>();
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitSymbolExpression(this);
		}

		@Override
		public boolean equals(Object object) {
			if (!super.equals(object)) {
				return false;
			}

			var other = (SymbolExpression) object;

			return Objects.equals(symbol, other.symbol)
				&& Objects.equals(genericCast, other.genericCast);
		}
	}

	public static class YieldExpression extends Expression {
		public final Expression yieldOut;

		public YieldExpression(List<Token> tokens, Expression yieldOut) {
			super(tokens);

			this.yieldOut = yieldOut;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitYieldExpression(this);
		}

		@Override
		public boolean equals(Object object) {
			if (!super.equals(object)) {
				return false;
			}

			var other = (YieldExpression) object;

			return Objects.equals(yieldOut, other.yieldOut);
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

		@Override
		public boolean equals(Object object) {
			if (!super.equals(object)) {
				return false;
			}

			var other = (IntExpression) object;

			return value == other.value
				&& precision == other.precision
				&& signed == other.signed;
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

		@Override
		public boolean equals(Object object) {
			if (!super.equals(object)) {
				return false;
			}

			var other = (FloatExpression) object;

			return value == other.value
				&& precision == other.precision;
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

		@Override
		public boolean equals(Object object) {
			if (!super.equals(object)) {
				return false;
			}

			var other = (BooleanExpression) object;

			return value == other.value;
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

		@Override
		public boolean equals(Object object) {
			if (!super.equals(object)) {
				return false;
			}

			var other = (StringExpression) object;

			return Objects.equals(value, other.value);
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

		@Override
		public boolean equals(Object object) {
			if (!super.equals(object)) {
				return false;
			}

			var other = (LambdaExpression) object;

			return Objects.equals(type, other.type)
				&& Objects.equals(body, other.body);
		}
	}

	public static class CoroutineExpression extends Expression {
		public final CoroutineType type;
		public final StatementBlock body;

		public CoroutineExpression(
			List<Token> tokens,
			CoroutineType type,
			StatementBlock body
		) {
			super(tokens);

			assert type != null;
			assert body != null;

			this.type = type;
			this.body = body;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitCoroutineExpression(this);
		}

		@Override
		public boolean equals(Object object) {
			if (!super.equals(object)) {
				return false;
			}

			var other = (CoroutineExpression) object;

			return Objects.equals(type, other.type)
				&& Objects.equals(body, other.body);
		}
	}

	public static class SelectExpression extends Expression {
		public final Expression match;
		public final List<KeyValuePair> branches;
		public final Expression elseClause;

		public SelectExpression(
			List<Token> tokens,
			Expression match,
			List<KeyValuePair> branches,
			Expression elseClause
		) {
			super(tokens);

			assert branches != null;
			assert !branches.isEmpty();

			this.match = match;
			this.branches = branches;
			this.elseClause = elseClause;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitSelectExpression(this);
		}

		@Override
		public boolean equals(Object object) {
			if (!super.equals(object)) {
				return false;
			}

			var other = (SelectExpression) object;

			return Objects.equals(match, other.match)
				&& Objects.equals(branches, other.branches)
				&& Objects.equals(elseClause, other.elseClause);
		}
	}

	public static class NewMapExpression extends Expression {
		public final Type keyType;
		public final Type valueType;
		public final List<KeyValuePair> keyValuePairs;

		public NewMapExpression(List<Token> tokens, Type keyType, Type valueType, List<KeyValuePair> keyValuePairs) {
			super(tokens);

			if (keyType != null || valueType != null) {
				assert keyType != null && valueType != null;
			}

			this.keyType = keyType;
			this.valueType = valueType;
			this.keyValuePairs = keyValuePairs != null ? keyValuePairs : new ArrayList<>();
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitNewMapExpression(this);
		}

		@Override
		public boolean equals(Object object) {
			if (!super.equals(object)) {
				return false;
			}

			var other = (NewMapExpression) object;

			return Objects.equals(keyType, other.keyType)
				&& Objects.equals(valueType, other.valueType)
				&& Objects.equals(keyValuePairs, other.keyValuePairs);
		}
	}

	public static class NewArrayExpression extends Expression {
		public final ArrayType type;
		public final List<Argument> elements;

		public NewArrayExpression(
			List<Token> tokens,
			ArrayType type,
			List<Argument> elements
		) {
			super(tokens);

			this.type = type;
			this.elements = elements != null
				? elements
				: new ArrayList<>();
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitNewArrayExpression(this);
		}

		@Override
		public boolean equals(Object object) {
			if (!super.equals(object)) {
				return false;
			}

			var other = (NewArrayExpression) object;

			return Objects.equals(type, other.type)
				&& Objects.equals(elements, other.elements);
		}
	}

	public static class CompositeExpression extends Expression {
		public final List<Type> genericCast;
		public final ListArgsOrKeyValuePairs list;

		public CompositeExpression(
			List<Token> tokens,
			List<Type> genericCast,
			ListArgsOrKeyValuePairs list
		){
			super(tokens);

			this.genericCast = genericCast != null
				? genericCast
				: new ArrayList<>();
			this.list = list;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitCompositeExpression(this);
		}

		@Override
		public boolean equals(Object object) {
			if (!super.equals(object)) {
				return false;
			}

			var other = (CompositeExpression) object;

			return Objects.equals(genericCast, other.genericCast)
				&& Objects.equals(list, other.list);
		}
	}

	public static class NewClassInstance extends Expression {
		public final ClassType classType;
		public final List<Type> genericCast;
		public final List<Argument> arguments;

		public NewClassInstance(
			List<Token> tokens,
			ClassType classType,
			List<Type> genericCast,
			List<Argument> arguments
		) {
			super(tokens);

			assert classType != null;

			this.classType = classType;
			this.genericCast = genericCast != null
				? genericCast
				: new ArrayList<>();
			this.arguments = arguments != null ? arguments : new ArrayList<>();
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitNewClassInstance(this);
		}

		@Override
		public boolean equals(Object object) {
			if (!super.equals(object)) {
				return false;
			}

			var other = (NewClassInstance) object;

			return Objects.equals(classType, other.classType)
				&& Objects.equals(genericCast, other.genericCast)
				&& Objects.equals(arguments, other.arguments);
		}
	}

	public static class NewObjectLiteralExpression extends Expression {
		public final ClassType classType;

		public NewObjectLiteralExpression(
			List<Token> tokens,
			ClassType classType
		) {
			super(tokens);

			assert classType != null;

			this.classType = classType;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitNewObjectLiteralExpression(this);
		}

		@Override
		public boolean equals(Object object) {
			if (!super.equals(object)) {
				return false;
			}

			var other = (NewObjectLiteralExpression) object;

			return Objects.equals(classType, other.classType);
		}
	}

	public static class TryElseExpression extends Expression {
		public final Expression call;
		public final String caught;
		public final StatementBlock block;

		public TryElseExpression(List<Token> tokens, Expression call, String caught, StatementBlock block) {
			super(tokens);

			assert call != null;
			if (caught != null || block != null) {
				assert caught != null;
				assert block != null;
			}

			this.call = call;
			this.caught = caught;
			this.block = block;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitTryElseExpression(this);
		}

		@Override
		public boolean equals(Object object) {
			if (!super.equals(object)) {
				return false;
			}

			var other = (TryElseExpression) object;

			return Objects.equals(call, other.call)
				&& Objects.equals(caught, other.caught)
				&&  Objects.equals(block, other.block);
		}
	}

	// ====================
	// UTILITIES
	// ====================

	public record FileDirective(
		String name,
		String argument // argument string must have quotations in it (e.g: `"\"argument\""`)
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

	public record SymbolChain(
		List<String> symbols
	) {
		public SymbolChain {
			assert symbols != null;
			assert !symbols.isEmpty();
		}
	}

	public record GenericParameter(
		Type type,
		String string
	) {
		public GenericParameter {
			assert type != null;
			assert string != null && !string.isEmpty();
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
			assert (
				value != null && !isBlank
			) || (
				value == null && keyword == null && isBlank
			);
		}
	}

	public record Decorator(
		SymbolChain symbolChain,
		List<Argument> arguments
	) {
		public Decorator {
			assert symbolChain != null;

			if (arguments == null) {
				arguments = new ArrayList<>();
			}
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

	sealed interface ListArgsOrKeyValuePairs permits ListArgs, ListKeyValuePairs {}

	public record ListArgs(List<Argument> args) implements ListArgsOrKeyValuePairs {}
	public record ListKeyValuePairs(List<KeyValuePair> pairs) implements ListArgsOrKeyValuePairs {}

	// ====================
	// VISITOR PATTERN
	// ====================

	public static interface Visitor<T> {
		T visitMainProgram(Program node);

		T visitClassProgram(Program node);

		T visitNamespaceProgram(NamespaceProgram node);

		// Types
		T visitSymbolType(SymbolType node);

		T visitArrayType(ArrayType node);

		T visitFullFunctionType(FullFunctionType node);

		T visitInferredFunctionType(InferredFunctionType node);

		T visitFullCoroutineType(FullCoroutineType node);

		T visitInferredCoroutineType(InferredCoroutineType node);

		T visitEnumType(EnumType node);

		T visitClassType(ClassType node);

		T visitUnionType(UnionType node);

		T visitSelfClassType(SelfClassType node);

		T visitCastClassMember(CastClassMember node);

		T visitDeclarationClassMember(DeclarationClassMember node);

		T visitConstructorClassMember(ConstructorClassMember node);

		T visitCaptureClassMember(AliasClassMember node);

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

		T visitLoopInfiniteStatement(LoopInfiniteStatement node);

		T visitLoopWhileStatement(LoopWhileStatement node);

		T visitLoopRangeStatement(LoopRangeStatement node);

		T visitLoopIterationStatement(LoopIterationStatement node);

		T visitThrowStatement(ThrowStatement node);

		// Expressions
		T visitBinaryExpression(BinaryExpression node);

		T visitUnaryExpression(UnaryExpression node);

		T visitSymbolExpression(SymbolExpression node);

		T visitIndexExpression(IndexExpression node);

		T visitCallExpression(CallExpression node);

		T visitAccessExpression(AccessExpression node);

		T visitYieldExpression(YieldExpression node);

		T visitIntExpression(IntExpression node);

		T visitFloatExpression(FloatExpression node);

		T visitNullExpression(NullExpression node);

		T visitSelfExpression(SelfExpression node);

		T visitSuperExpression(SuperExpression node);

		T visitBooleanExpression(BooleanExpression node);

		T visitStringExpression(StringExpression node);

		T visitLambdaExpression(LambdaExpression node);

		T visitCoroutineExpression(CoroutineExpression node);

		T visitSelectExpression(SelectExpression node);

		T visitNewMapExpression(NewMapExpression node);

		T visitNewArrayExpression(NewArrayExpression node);

		T visitCompositeExpression(CompositeExpression node);

		T visitNewClassInstance(NewClassInstance node);

		T visitNewObjectLiteralExpression(NewObjectLiteralExpression node);

		T visitTryElseExpression(TryElseExpression node);
	}
}
