parser grammar AntlerScriptParser;

@header {
package io.github.heliguy4599.antlerscript;
}

options { tokenVocab=AntlerScriptLexer; }

symbol
	: name=SYMBOL
	| name=FROM
	| name=TO
	| name=BY
	| name=OVER
	| name=WHILE
	;

semicolon
	: NEWLINE
	| SEMICOLON
	;

//-----------------------
// FILES
//-----------------------

program
	: main_program
	| class_program
	| namespace_program
	| implicit_namespace_program
	;

other_directive
	: OTHER_DIRECTIVE symbol ( STRING | RAW_STRING )?
	;

namespace_directive
	: NAMESPACE_DIRECTIVE symbol
	;

classname_directive
	: CLASSNAME_DIRECTIVE symbol
	;

main_directive
	: MAIN_DIRECTIVE
	;

using_directive
	: USING_DIRECTIVE symbol_chain ( ',' symbol_chain )*
	;

repeatable_directive
	: other_directive
	| using_directive
	;

main_program
	: semicolon* main_directive ( semicolon+ repeatable_directive )* ( semicolon+ statement )* semicolon* EOF
	;

class_program
	: semicolon* ( namespace_directive semicolon+ )? classname_directive ( semicolon+ repeatable_directive )* ( semicolon+ class_top_level )? semicolon* EOF
	;

namespace_program
	: semicolon* namespace_directive ( semicolon+ repeatable_directive )* ( semicolon+ namespace_member )* semicolon* EOF
	;

implicit_namespace_program
	: semicolon* ( repeatable_directive semicolon+ )* namespace_member ( semicolon+ namespace_member )* semicolon* EOF
	| semicolon* repeatable_directive ( semicolon+ repeatable_directive )* ( namespace_member ( semicolon+ namespace_member )* )? semicolon* EOF
	| semicolon* EOF
	;

namespace_member
	: declaration
	| typedef
	;

//-----------------------
// CLASSES
//-----------------------

class_top_level
	: class_extends ( semicolon+ class_member )*
	| class_member ( semicolon+ class_member )*
	;

class_header_inside
	: class_extends ( ','? class_member )* ','?
	| class_member ( ','? class_member )* ','?
	;

class_extends
	: EXTENDS symbol_chain ( ',' symbol_chain )*
	;

symbol_chain
	: symbol ( '.' symbol )*
	;

constructor
	: CONSTRUCTOR constructor_params statement_block
	;

constructor_params
	: '(' constructor_params_elm ( ',' constructor_params_elm )* ( ',' var_args )? ')'
	| '(' ')'
	| '(' var_args ')'
	;

constructor_params_elm
	: symbol
	| type symbol ( '=' expression )?
	;

var_args
	: type '...' symbol
	;

class_member
	: cast                  # castClassMember
	| declaration           # declarationClassMember
	| operator_overload     # operatorOverloadClassMember
	| constructor           # constructorClassMember
	| alias                 # aliasClassMember
	| extends_assign        # extendsClassMember
	;

cast
	: CAST '(' type ')' statement_block?
	;

operator_overload
	: OPERATOR overridable '(' rightType=type symbol ':' returnType=type ')' statement_block?
	;

overridable
	: operator='+'
	| operator='-'
	| operator='*'
	| operator='/'
	| operator='%'
	| operator='<'
	| operator='>'
	| operator='++'
	| operator='**'
	| operator='//'
	| operator='%%'
	| operator='=='
	| operator='[' ']'
	;

alias
	: ALIAS '(' symbol_chain ')' '.' origin=symbol '->' ( target=symbol | extends_assign )
	;

extends_assign
	: symbol '=' expression
	;

//-----------------------
// ENUMS
//-----------------------

enum_header_inside
	: EXTENDS symbol_chain ( ',' symbol ( ',' symbol )* ','? )?
	| symbol ( ',' symbol )* ','?
	;

//-----------------------
// TYPES
//-----------------------

type
	: type_or
	;

type_or
	: left=type_and right=type_or_right?
	;

type_or_right
	: '|' type_or
	;

type_and
	: left=type_nullable right=type_and_right?
	;

type_and_right
	: '&' type_and
	;

type_nullable
	: type_atomic nullable='?'?
	;

type_atomic
	: symbol generic_args? # symbolType
	| array_header         # arrayType
	| class_header         # classType
	| enum_header          # enumType
	| func_header          # funcType
	| coroutine_header     # coroutineType
	| SELF_CLASS           # selfType
	| '(' type ')'         # typeGroup
	;

array_header
	: ARRAY ( '[' type ',' expression ']' )?
	;

func_header
	: full=func_header_full
	| inferred=func_header_inferred
	;

func_header_full
	: FUNC generic_parameters? '(' func_params? ':' returnType=type? ')' ( '!' errorType=type )?
	;

func_header_inferred
	: FUNC '(' symbol ( ',' symbol )* ( ',' '...' varargs=symbol )? ','? ')' '!'?
	| FUNC '(' '...' varargs=symbol ','? ')' '!'?
	| FUNC '(' ')' '!'?
	;

func_params
	: func_param_elm ( ',' func_param_elm )* ( ',' var_args )? ','?
	;

func_param_elm
	: type symbol ( '=' expression )?
	;

coroutine_header
	: full=coroutine_header_full
	| inferred=coroutine_header_inferred
	;

coroutine_header_full
	: COROUTINE generic_parameters? '(' func_params? ':' returnType=type? ')' coroutine_header_yield?
	;

coroutine_header_inferred
	: COROUTINE '(' symbol ( ',' symbol )* ( ',' '...' varargs=symbol )? ','? ')' YIELD?
	| COROUTINE '(' '...' varargs=symbol ','? ')' YIELD?
	| COROUTINE '(' ')' YIELD?
	;

coroutine_header_yield
	: YIELD ':' yieldIn=type
	| YIELD yieldOut=type ':'
	| YIELD yieldOut=type ':' yieldIn=type
	;

composite
	:
	'{'
	{
		AntlerScriptLexer lexer = (AntlerScriptLexer)_input.getTokenSource();
		lexer.ignoreSemicolons.pollFirst();
		lexer.ignoreSemicolons.push(true);
	}
	( keypair_list_map | arguments )?
	'}'
	;

lambda
	: func_header statement_block
	;

coroutine
	: coroutine_header statement_block
	;

class_header
	: CLASS generic_parameters? '(' class_header_inside? ')'
	;

enum_header
	: ENUM '(' enum_header_inside ')'
	;

generic_parameters
	: '[' type symbol ( ',' type symbol )* ']'
	;

//-----------------------
// EXPRESSIONS
//-----------------------

expression
	: expression_yield
	;

expression_yield
	: YIELD* expression_assignment
	;

expression_assignment
	: expression_logical_or expression_assignment_right?
	;

expression_assignment_right
	: operator='+=' expression_assignment
	| operator='-=' expression_assignment
	| operator='*=' expression_assignment
	| operator='**=' expression_assignment
	| operator='/=' expression_assignment
	| operator='//=' expression_assignment
	| operator='%=' expression_assignment
	| operator='%%=' expression_assignment
	| operator='|=' expression_assignment
	| operator='&=' expression_assignment
	| operator='~=' expression_assignment
	| operator='^=' expression_assignment
	| operator='<<=' expression_assignment
	| operator='>>=' expression_assignment
	| operator='++=' expression_assignment
	| operator='??=' expression_assignment
	| operator='.=' expression_assignment
	| operator='=' expression_assignment
	;

expression_logical_or
	: expression_logical_and expression_logical_or_right*
	;

expression_logical_or_right
	: operator=OR expression_logical_and
	| operator='??' expression_logical_and
	;

expression_logical_and
	: expression_logical_not expression_logical_and_right*
	;

expression_logical_and_right
	: operator=AND expression_logical_not
	;

expression_logical_not
	: operators=NOT* operand=expression_cmp
	;

expression_cmp
	: expression_func_pipe expression_cmp_right*
	;

expression_cmp_right
	: operator='<' expression_func_pipe
	| operator='>' expression_func_pipe
	| operator='<=' expression_func_pipe
	| operator='>=' expression_func_pipe
	| operator='==' expression_func_pipe
	| operator='!=' expression_func_pipe
	| operator=IN expression_func_pipe
	| operator=IS expression_func_pipe
	| operator=AS expression_func_pipe
	;

expression_func_pipe
	: expression_bit_or expression_func_pipe_right*
	;

expression_func_pipe_right
	: operator='|>' expression_bit_or
	;

expression_bit_or
	: expression_bit_xor expression_bit_or_right*
	;

expression_bit_or_right
	: operator='|' expression_bit_xor
	;

expression_bit_xor
	: expression_bit_and expression_bit_xor_right*
	;

expression_bit_xor_right
	: operator='^' expression_bit_and
	;

expression_bit_and
	: expression_bit_shift expression_bit_and_right*
	;

expression_bit_and_right
	: operator='&' expression_bit_shift
	;

expression_bit_shift
	: expression_add expression_bit_shift_right*
	;

expression_bit_shift_right
	: operator='<<' expression_add
	| operator='>>' expression_add
	;

expression_add
	: expression_mult expression_add_right*
	;

expression_add_right
	: operator='+' expression_mult
	| operator='-' expression_mult
	| operator='++' expression_mult
	;

expression_mult
	: expression_unary expression_mult_right*
	;

expression_mult_right
	: operator='*' expression_unary
	| operator='/' expression_unary
	| operator='//' expression_unary
	| operator='%' expression_unary
	| operator='%%' expression_unary
	;

expression_unary
	: expression_unary_op* expression_exp
	;

expression_unary_op
	: operator='+'
	| operator='-'
	| operator='~'
	;

expression_exp
	: expression_postfix expression_exp_right*
	;

expression_exp_right
	: '**' expression_postfix
	;

expression_postfix
	: expression_atom expression_access*
	;

expression_access
	: '[' expression ']' generic_args? # indexAccess
	| '(' arguments? ')'               # functionCall
	| '.' symbol generic_args?         # memberAccess
	| '?.' symbol generic_args?        # nullishAccess
	;

arguments
	: argument_elm ( ',' argument_elm )* ','?
	;

argument_elm
	: '_'                           # discardArgument
	| ( symbol '=' )? expression    # expressionArgument
	;

expression_atom
	: symbol generic_args?  # symbolExpression
	| STRING                # stringExpression
	| RAW_STRING            # rawStringExpression
	| FLOAT                 # floatExpression
	| INTEGER               # integerExpression
	| TRUE                  # trueExpression
	| FALSE                 # falseExpression
	| NULL                  # nullExpression
	| SUPER                 # superExpression
	| SELF_INSTANCE         # selfInstanceExpression
	| new_object_instance   # newObjectExpression
	| new_array_instance    # newArrayExpression
	| new_map_instance      # newMapExpression
	| new_class_instance    # newClassInstance
	| composite             # compositeExpression
	| lambda                # lambdaExpression
	| coroutine             # coroutineExpression
	| select                # selectExpression
	| object_literal        # objectLiteralExpression
	| try_else		# tryElseExpression
	| '(' expression ')'    # groupedExpression
	;

generic_args
	: '[' type ( ',' type )* ']'
	;

new_object_instance
	: symbol generic_args? object_instantiation_args
	;

new_array_instance
	: array_header object_instantiation_args
	;

new_class_instance
	: class_header generic_args? object_instantiation_args
	;

object_instantiation_args
	:
	'{'
	{
		AntlerScriptLexer lexer = (AntlerScriptLexer)_input.getTokenSource();
		lexer.ignoreSemicolons.pollFirst();
		lexer.ignoreSemicolons.push(true);
	}
	arguments?
	'}'
	;

new_map_instance
	: MAP ( '[' key=type ',' value=type ']' )?
	'{'
	{
		AntlerScriptLexer lexer = (AntlerScriptLexer)_input.getTokenSource();
		lexer.ignoreSemicolons.pollFirst();
		lexer.ignoreSemicolons.push(true);
	}
	keypair_list_map?
	'}'
	;

select
	: SELECT  value=expression? '(' keypair_list_select ')'
	;

object_literal
	: OBJECT '{' semicolon* class_top_level? semicolon* '}'
	;

try_else
	: TRY expression_postfix ( ELSE symbol statement_block )?
	;

keypair_list_select
	: keypair_clause ( ',' keypair_clause )* ( ',' elseToken=ELSE ':' elseClause=expression )? ','?
	| elseToken=ELSE ':' elseClause=expression ','?
	;

keypair_list_map
	: keypair_clause ( ',' keypair_clause )* ','?
	;

keypair_clause
	: key=expression ':' value=expression
	;

//-----------------------
// STATEMENTS
//-----------------------

statement
	: DEFER? expression             # expressionStatement
	| BREAK                         # breakStatement
	| CONTINUE                      # continueStatement
	| RETURN expression?            # returnStatement
	| loop                          # loopStatement
	| declaration                   # declarationStatement
	| typedef                       # typedefStatement
	| if                            # ifStatement
	| switch                        # switchStatement
	| DEFER? statement_block        # statementBlockStatement
	| throw				# throwStatement
	;

statement_block
	: '{' semicolon* ( statement ( semicolon+ statement )* semicolon* )? '}'
	;

loop
	: LOOP loop_header_inside block=statement_block
	| LOOP loop_while? block=statement_block
	;

loop_header_inside
	// Index
	: left_while=loop_while loop_capture
	| loop_capture right_while=loop_while
	| loop_capture

	// Ranges
	| left_while=loop_while loop_range
	| loop_range right_while=loop_while
	| loop_range

	// Iterations
	| left_while=loop_while loop_iteration
	| loop_iteration right_while=loop_while
	| loop_iteration
	;

loop_capture
	: '->' symbol
	;

loop_capture_2
	: '->' left=symbol ( ',' right=symbol )?
	;

loop_range
	: FROM from=expression ( TO to=expression ( BY by=expression )? | BY by=expression ( TO to=expression )? )? loop_capture?
	| TO to=expression ( FROM from=expression ( BY by=expression )? | BY by=expression ( FROM from=expression )? )? loop_capture?
	| BY by=expression ( FROM from=expression ( TO to=expression )? | TO to=expression ( FROM from=expression )? )? loop_capture?
	;

loop_while
	: WHILE expression
	;

loop_iteration
	: OVER collection=expression loop_capture_2?
	;

decorator
	: '@' symbol_chain ( '(' arguments? ')' )?
	;

decorator_chain
	: decorator ( semicolon decorator )* semicolon
	;

declaration
	: decorator_chain? LET ( isMutable=MUT | isSealed=SEALED )? variableType=type variableName=symbol                              # letDeclaration
	| decorator_chain? LET ( isMutable=MUT | isSealed=SEALED )? variableType=type? variableName=symbol '=' initialValue=expression # letDefinition
	| decorator_chain? CONST variableType=type? variableName=symbol '=' initialValue=expression                                    # constDefinition
	;

typedef
	: TYPE symbol '=' type
	;

if
	: IF test=expression block=statement_block elif* else?
	;

elif
	: ELIF test=expression block=statement_block
	;

else
	: ELSE block=statement_block
	;

switch
	: SWITCH test=expression case+ catchAll=else?
	;

case
	: CASE expression ( ',' expression )* block=statement_block
	;

throw
	: THROW expression
	;