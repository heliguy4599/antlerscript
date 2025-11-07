parser grammar AntlerScriptParser;
options { tokenVocab=AntlerScriptLexer; }

@header {
import static AntlerScriptLexer.ignoreSemicolons;
}

//-----------------------
// FILES
//-----------------------

program
    : file_directive* ( class_top_level | ( statement SEMICOLON )+ ) EOF
    ;

file_directive: DIRECTIVE SYMBOL ;

//-----------------------
// CLASSES
//-----------------------

class_top_level
    : SEMICOLON* class_extends ( SEMICOLON+ class_member ( SEMICOLON+ class_member )* SEMICOLON* )?
    | SEMICOLON* class_member ( SEMICOLON+ class_member )* SEMICOLON*
    ;

class_header_inside
    : class_extends ( ',' class_member ( ',' class_member )* ','? )?
    | class_member ( ',' class_member )* ','?
    ;

class_extends
    : EXTENDS class_extends_access ( ',' class_extends_access )*
    ;

class_extends_access
    : SYMBOL ( '.' SYMBOL )*
    ;

constructor
    : CONSTRUCTOR constructor_params statement_block
    ;

private_constructor
    : PRIVATE_CONSTRUCTOR constructor_params statement_block
    ;

constructor_params
    : '(' constructor_params_elm ( ',' constructor_params_elm )* ( ',' ellipsis )? ')'
    | '(' ')'
    | '(' ellipsis ')'
    ;

constructor_params_elm
    : SYMBOL
    | type SYMBOL ( '=' expression )?
    ;

ellipsis
    : type '...' SYMBOL
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
    : OPERATOR overridable '(' type SYMBOL ':' type ')' statement_block
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
    : CAPTURE '(' class_extends_access ')' '.' SYMBOL '->' ( SYMBOL | extends_assign )
    ;

extends_assign
    : SYMBOL '=' expression
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
    : SYMBOL
    | list_header
    | array_header
    | map_header
    | class_header
    | func_header
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
    : func_param_elm ( ',' func_param_elm )* ( ',' ellipsis )? ','?
    ;

func_param_elm
    : type SYMBOL ( '==' expression )?
    ;

lambda
    : func_header statement_block
    ;

class_header
    : CLASS '(' class_header_inside ')'
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
    | '=' expression_assignment
    ;

expression_logical_or
    : expression_logical_and ( OR expression_logical_and )*
    ;

expression_logical_and
    : expression_logical_not ( AND expression_logical_not )*
    ;

expression_logical_not
    : NOT? expression_cmp
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
    : expression_unary_op? expression_exp
    ;

expression_unary_op
    : '+'
    | '-'
    | '~'
    | NOT 
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
    | '{' ( keypair_list | arguments )? '}'
    | '.' SYMBOL
    | '?.' SYMBOL
    ;

arguments
    : argument_elm ( ',' argument_elm )* ','?
    ;

argument_elm
    : '_'
    | ( SYMBOL '=' )? expression
    ;

dictionary_arguments
    : 
    ;

expression_atom
    : SYMBOL
    | STRING
    | FLOAT
    | INTEGER
    | TRUE
    | FALSE
    | NULL
    | SUPER
    | lambda
    | select
    | '(' expression ')'
    ;
    
select
    : SELECT ( '[' expression ']' )? '(' keypair_list ')'
    ;

keypair_list
    : { ignoreSemicolons.pollFirst(); ignoreSemicolons.push(true); }
    keypair_clause ( ',' keypair_clause )* ','?
    ;

keypair_clause
    : expression ':' expression
    ;

//-----------------------
// STATEMENTS
//-----------------------

statement
    : 
    | DEFER? expression
    | BREAK
    | CONTINUE
    | RETURN expression?
    | loop
    | while
    | iterate
    | declaration
    | typedef
    | if
    | switch
    | DEFER? statement_block
    ;

statement_block
    : '{' SEMICOLON* ( statement ( SEMICOLON+ statement )* SEMICOLON* )? '}'
    ;

loop
    : LOOP ( 'from' expression )? 'to' expression ( 'by' expression )? ( '->' SYMBOL )? statement_block
    ;

while
    : WHILE expression statement_block
    ;
    
iterate
    : ITERATE expression ( '->' SYMBOL ( ',' SYMBOL )? )? statement_block
    ;

declaration
    : ( CONST | LET MUT? ) type? SYMBOL '=' expression
    | ( CONST | LET MUT? ) type SYMBOL
    ;

typedef
    : TYPE SYMBOL '=' type
    ;

if
    : IF expression statement_block elif* else?
    ;

elif
    : ELIF expression statement_block
    ;

else
    : ELSE statement_block
    ;

switch
    : SWITCH expression case+ else?
    ;

case
    : CASE expression ( ',' expression )* statement_block
    ;
