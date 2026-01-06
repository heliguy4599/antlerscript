parser grammar AntlerScriptParser;
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
	: OTHER_DIRECTIVE SYMBOL ( STRING | RAW_STRING )?
	;

namespace_directive
	: NAMESPACE_DIRECTIVE SYMBOL
	;

classname_directive
	: CLASSNAME_DIRECTIVE SYMBOL
	;

main_directive
	: MAIN_DIRECTIVE
	;

main_program
	: semicolon* main_directive ( semicolon+ other_directive )* ( semicolon+ statement )* semicolon* EOF
	;

class_program
	: semicolon* ( namespace_directive semicolon+ )? classname_directive ( semicolon+ other_directive )* ( semicolon+ class_top_level )? semicolon* EOF
	;

namespace_program
	: semicolon* namespace_directive ( semicolon+ other_directive )* ( semicolon+ namespace_member )* semicolon* EOF
	;

implicit_namespace_program
	: semicolon* ( other_directive semicolon+ )* namespace_member ( semicolon+ namespace_member )* semicolon* EOF
	| semicolon* other_directive ( semicolon+ other_directive )* ( namespace_member ( semicolon+ namespace_member )* )? semicolon* EOF
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
	: EXTENDS class_extends_access ( ',' class_extends_access )*
	;

class_extends_access
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
	| capture               # captureClassMember
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

capture
	: CAPTURE '(' class_extends_access ')' '.' origin=symbol '->' ( target=symbol | extends_assign )
	;

extends_assign
	: symbol '=' expression
	;

//-----------------------
// ENUMS
//-----------------------

enum_header_inside
	: EXTENDS class_extends_access ( ',' symbol ( ',' symbol )* ','? )?
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
	: PIPE type_or
	;

type_and
	: left=type_atomic right=type_and_right?
	;

type_and_right
	: PIPE type_and
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
	| '(' type ')'  # typeGroup
	;

list_header
	: LIST '(' type? ')'
	;

array_header
	: ARRAY '(' type? ')'
	;

map_header
	: MAP '(' ( type ',' type )? ')'
	;

func_header
	: FUNC '(' func_params? ':' type? ')'
	;

func_params
	: func_param_elm ( ',' func_param_elm )* ( ',' var_args )? ','?
	;

func_param_elm
	: type symbol ( '=' expression )?
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
	: expression_bit_or expression_cmp_right*
	;

expression_cmp_right
	: operator='<' expression_bit_or
	| operator='>' expression_bit_or
	| operator='<=' expression_bit_or
	| operator='>=' expression_bit_or
	| operator='==' expression_bit_or
	| operator='!=' expression_bit_or
	| operator=IN expression_bit_or
	| operator=IS expression_bit_or
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
	: '[' expression ']'    # indexAccess
	| '(' arguments? ')'    # functionCall
	| '.' symbol            # memberAccess
	| '?.' symbol           # nullishAccess
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

declaration
	: LET isMutable=MUT? variableType=type variableName=symbol                              # letDeclaration
	| LET isMutable=MUT? variableType=type? variableName=symbol '=' initialValue=expression # letDefinition
	| CONST variableType=type? variableName=symbol '=' initialValue=expression              # constDefinition
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
