parser grammar AntlerScriptParser;
options { tokenVocab=AntlerScriptLexer; }

symbol
    : SYMBOL
    | FROM
    | TO
    | BY
    ;

semicolon
    : NEWLINE
    | SEMICOLON
    ;

//-----------------------
// FILES
//-----------------------

program
    : file_directive* ( class_top_level | statement ( semicolon statement )* semicolon* ) EOF
    ;

file_directive: DIRECTIVE symbol ;

//-----------------------
// CLASSES
//-----------------------

class_top_level
    : semicolon* class_extends ( semicolon+ class_member ( semicolon+ class_member )* semicolon* )?
    | semicolon* class_member ( semicolon+ class_member )* semicolon*
    ;

class_header_inside
    : class_extends ( ',' class_member ( ',' class_member )* ','? )?
    | class_member ( ',' class_member )* ','?
    ;

class_extends
    : EXTENDS class_extends_access ( ',' class_extends_access )*
    ;

class_extends_access
    : symbol ( '.' symbol )*
    ;

constructor
    : CONSTRUCTOR constructor_params statement_block
    ;

private_constructor
    : PRIVATE_CONSTRUCTOR constructor_params statement_block
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
    : cast
    | declaration
    | operator_overload
    | constructor
    | private_constructor
    | capture
    | extends_assign
    ;

cast
    : CAST '(' type ')' statement_block
    ;

operator_overload
    : OPERATOR overridable '(' type symbol ':' type ')' statement_block
    ;

overridable
    : '+'
    | '-'
    | '*'
    | '/'
    | '%'
    | '<'
    | '>'
    | '++'
    | '**'
    | '//'
    | '%%'
    | '=='
    | '[' ']'
    ;

capture
    : CAPTURE '(' class_extends_access ')' '.' symbol '->' ( symbol | extends_assign )
    ;

extends_assign
    : symbol '=' expression
    ;

//-----------------------
// TYPES
//-----------------------

type
    : type_or
    ;

type_or
    : type_and ( PIPE type_and )*
    ;

type_and
    : type_atomic ( AMP type_atomic )*
    ;

type_atomic
    : symbol
    | list_header
    | array_header
    | map_header
    | class_header
    | func_header
    | SELF_CLASS
    | '(' type ')'
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
    : '+=' expression_assignment
    | '-=' expression_assignment
    | '*=' expression_assignment
    | '**=' expression_assignment
    | '/=' expression_assignment
    | '//=' expression_assignment
    | '%=' expression_assignment
    | '%%=' expression_assignment
    | '|=' expression_assignment
    | '&=' expression_assignment
    | '~=' expression_assignment
    | '^=' expression_assignment
    | '<<=' expression_assignment
    | '>>=' expression_assignment
    | '++=' expression_assignment
    | '??=' expression_assignment
    | '.=' expression_assignment
    | '=' expression_assignment
    ;

expression_logical_or
    : expression_logical_and expression_logical_or_right?
    ;

expression_logical_or_right
    : OR expression_logical_or
    | '??' expression_logical_or
    ;

expression_logical_and
    : expression_logical_not ( AND expression_logical_not )*
    ;

expression_logical_not
    : NOT* expression_cmp
    ;

expression_cmp
    : expression_bit_or expression_cmp_right*
    ;

expression_cmp_right
    : '<' expression_bit_or
    | '>' expression_bit_or
    | '<=' expression_bit_or
    | '>=' expression_bit_or
    | '==' expression_bit_or
    | '!=' expression_bit_or
    | IN expression_bit_or
    | IS expression_bit_or
    ;

expression_bit_or
    : expression_bit_xor ( '|' expression_bit_xor )*
    ;

expression_bit_xor
    : expression_bit_and ( '^' expression_bit_and )*
    ;

expression_bit_and
    : expression_bit_shift ( '&' expression_bit_shift )*
    ;

expression_bit_shift
    : expression_add expression_bit_shift_right*
    ;

expression_bit_shift_right
    : '<<' expression_add
    | '>>' expression_add
    ;

expression_add
    : expression_mult expression_add_right*
    ;

expression_add_right
    : '+' expression_mult
    | '-' expression_mult
    | '++' expression_mult
    ;

expression_mult
    : expression_unary expression_mult_right*
    ;

expression_mult_right
    : '*' expression_unary
    | '**' expression_unary
    | '/' expression_unary
    | '//' expression_unary
    | '%' expression_unary
    | '%%' expression_unary
    ;

expression_unary
    : expression_unary_op* expression_exp
    ;

expression_unary_op
    : '+'
    | '-'
    | '~'
    ;

expression_exp
    : expression_postfix ( '**' expression_postfix )*
    ;

expression_postfix
    : expression_atom expression_access*
    ;

expression_access
    : '[' expression ']'
    | '(' arguments? ')'
    | '.' symbol
    | '?.' symbol
    ;

arguments
    : argument_elm ( ',' argument_elm )* ','?
    ;

argument_elm
    : '_'
    | ( symbol '=' )? expression
    ;

expression_atom
    : symbol
    | STRING
    | RAW_STRING
    | FLOAT
    | INTEGER
    | TRUE
    | FALSE
    | NULL
    | SUPER
    | SELF_INSTANCE
    | new_object_instance
    | new_list_instance
    | new_array_instance
    | new_map_instance
    | new_class_instance
    | lambda
    | select
    | '(' expression ')'
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
    : SELECT ( '[' expression ']' )? '(' keypair_list ')'
    ;

keypair_list
    : keypair_clause ( ',' keypair_clause )* ','?
    ;

keypair_clause
    : expression ':' expression
    ;

//-----------------------
// STATEMENTS
//-----------------------

statement
    : DEFER? expression		# expressionStatement
    | BREAK  			# breakStatement
    | CONTINUE			# continueStatement
    | RETURN expression?	# returnStatement
    | loop   			# loopStatement
    | while			# whileStatement
    | iterate			# iterateStatement
    | declaration		# declarationStatement
    | typedef			# typedefStatement
    | if			# ifStatement
    | switch			# switchStatement
    | DEFER? statement_block	# statementBlock
    ;

statement_block
    : '{' semicolon* ( stmt=statement ( semicolon+ stmt=statement )* semicolon* )? '}'
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
    : LET isMutable=MUT? variableType=type variableName=symbol					# letDeclaration
    | LET isMutable=MUT? variableType=type? variableName=symbol '=' initialValue=expression	# letDefinition
    | CONST variableType=type variableName=symbol						# constDeclaration
    | CONST variableType=type? variableName=symbol '=' initialValue=expression			# constDefinition
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
    : SWITCH test=expression cases=case+ catchAll=else?
    ;

case
    : CASE matches=expression ( ',' matches=expression )* block=statement_block
    ;
