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
	: class_extends ( ',' class_member )* ','?
	| class_member ( ',' class_member )* ','?
	;

class_extends
	: EXTENDS symbol_chain ( ',' symbol_chain )*
	;

symbol_chain
	: symbol ( '.' symbol )*
	;

constructor
	: CONSTRUCTOR constructor_params statement_block?
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
	: CAST '(' type ')' statement_block
	;

operator_overload
	: OPERATOR overridable '(' rightType=type symbol ':' returnType=type ')' statement_block
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
	: left=type_atomic right=type_and_right?
	;

type_and_right
	: '&' type_and
	;

type_atomic
	: symbol        # symbolType
	| list_header   # listType
	| array_header  # arrayType
	| map_header    # mapType
	| class_header  # classType
	| enum_header   # enumType
	| func_header   # funcType
	| SELF_CLASS    # selfType
	| template      # templateType
	| '(' type ')'  # typeGroup
	;

// TODO: maybe abstract List(GenericType), Map, and Array into a general generic type?
// TODO: check for the generic types to have the target typing feature (e.g. let DataHolder(Int) implicit = DataHolder(){ 10 })

list_header
	: LIST '(' type? ')'
	;

array_header
	: ARRAY '(' ( type | expression )? ')'
	| ARRAY '(' type ',' expression ')'
	;

map_header
	: MAP '(' ( type ',' type )? ')'
	;

func_header
	: FUNC '(' func_header_body? ':' type? ')'
	;

func_header_body
	: t_definitions ',' func_params
	| t_definitions ','?
	| func_params
	;

func_params
	: func_param_elm ( ',' func_param_elm )* ( ',' var_args )? ','?
	;

func_param_elm
	: type symbol ( '=' expression )?
	;

template
	: TEMPLATE '(' t_definitions ':' type ')'
	;

t_definitions
	: t_definition ( ',' t_definition )*
	;

t_definition
	: '<' type '>' symbol
	;

lambda
	: func_header statement_block
	;

class_header
	: CLASS '(' class_header_inside? ')'
	;

enum_header
	: ENUM '(' enum_header_inside ')'
	;

//-----------------------
// EXPRESSIONS
//-----------------------

expression
	: expression_assignment
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
	: '[' expression ']'      # indexAccess
	| '(' func_call_body? ')' # functionCall
	| '.' symbol              # memberAccess
	| '?.' symbol             # nullishAccess
	;

func_call_body
	: t_definitions ',' arguments
	| t_definitions ','?
	| arguments
	;

arguments
	: argument_elm ( ',' argument_elm )* ','?
	;

argument_elm
	: '_'                           # discardArgument
	| ( symbol '=' )? expression    # expressionArgument
	;

expression_atom
	: symbol                # symbolExpression
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
	| new_list_instance     # newListExpression
	| new_array_instance    # newArrayExpression
	| new_map_instance      # newMapExpression
	| new_class_instance    # newClassInstance
	| lambda                # lambdaExpression
	| select                # selectExpression
	| object_literal        # objectLiteralExpression
	| '(' expression ')'    # groupedExpression
	;

new_object_instance
	: symbol object_instantiation_args
	;

new_list_instance
	: list_header object_instantiation_args
	;

new_array_instance
	: array_header object_instantiation_args
	;

new_class_instance
	: class_header object_instantiation_args
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
	: map_header
	'{'
	{
		AntlerScriptLexer lexer = (AntlerScriptLexer)_input.getTokenSource();
		lexer.ignoreSemicolons.pollFirst();
		lexer.ignoreSemicolons.push(true);
	}
	keypair_list?
	'}'
	;

select
	: SELECT ( '[' value=expression ']' )? '(' keypair_list ')'
	;

object_literal
	: OBJECT '{' semicolon* class_top_level? semicolon* '}'
	;

keypair_list
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
	| while                         # whileStatement
	| iterate                       # iterateStatement
	| declaration                   # declarationStatement
	| typedef                       # typedefStatement
	| if                            # ifStatement
	| switch                        # switchStatement
	| DEFER? statement_block        # statementBlockStatement
	;

statement_block
	: '{' semicolon* ( statement ( semicolon+ statement )* semicolon* )? '}'
	;

loop
	: LOOP ( 'from' from=expression )? 'to' to=expression ( 'by' by=expression )? ( '->' iterator=symbol )? block=statement_block
	;

while
	: WHILE test=expression block=statement_block
	;

iterate
	: ITERATE iterable=expression ( '->' ( element=symbol | index=symbol ',' element=symbol ) )? block=statement_block
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
