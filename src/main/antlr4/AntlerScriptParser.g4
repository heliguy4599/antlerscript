parser grammar ExprParser;
options { tokenVocab=ExprLexer; }

//-----------------------
// FILES
//-----------------------

program
    : file_directives* ( class_body | (statement SEMICOLON)+ )? EOF
    ;

file_directive: DIRECTIVE ID ;

//-----------------------
// CLASSES
//-----------------------

class_body
    : class_extends? class_member* constructor? class_member* private_constructor? class_member*
    | class_extends? class_member* private_constructor? class_member* constructor? class_member*
    ;

class_extends
    : EXTENDS class_extends_access ( COMMA class_extends_access )*
    ;

class_extends_access
    : SYMBOL ( DOT SYMBOL )*
    ;

constructor
    : CONSTRUCTOR constructor_params statement_block
    ;

private_constructor
    : PRIVATE_CONSTRUCTOR constructor_params statement_block
    ;

constructor_params
    : LPAREN constructor_params_elm ( COMMA constructor_params_elm )* ( COMMA ellipsis )? RPAREN
    | LPAREN RPAREN
    | LPAREN ellipsis RPAREN
    ;

constructor_params_elm
    : symbol
    | type symbol ( EQUAL expression )?
    ;

ellipsis
    : type ELLIPSIS symbol
    ;

// TODO
class_member
    :
    ;

//-----------------------
// TYPES
//-----------------------

type
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
    | LPAREN type LPAREN
    ;

list_header
    : LIST LPAREN type? RPAREN
    ;

array_header
    : ARRAY LPAREN type? RPAREN
    ;

map_header
    : MAP LPAREN ( type COMMA type )? RPAREN
    ;

// TODO: wait, if newlines are ignored inside of parentheses, what does that mean for inside of Class()? 
class_header
    : CLASS LPAREN

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
    : PLUS_EQ expression_assignment
    | MINUS_EQ expression_assignment
    | STAR_EQ expression_assignment
    | DOUBLE_STAR_EQ expression_assignment
    | SLASH_EQ expression_assignment
    | DOUBLE_SLASH_EQ expression_assignment
    | PERCENT_EQ expression_assignment
    | DOUBLE_PERCENT_EQ expression_assignment
    | PIPE_EQ expression_assignment
    | AMP_EQ expression_assignment
    | TILDE_EQ expression_assignment
    | CARRET_EQ expression_assignment
    | BIT_LSHIFT_EQ expression_assignment
    | BIT_RSHIFT_EQ expression_assignment
    | DOUBLE_PLUS_EQ expression_assignment
    | DOUBLE_QMARK_EQ expression_assignment
    | EQUAL expression_assignment
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
    : LESSER_THAN expression_bit_or
    | GREATER_THAN expression_bit_or
    | LESSER_THAN_EQ expression_bit_or
    | GREATER_THAN_EQ expression_bit_or
    | DOUBLE_EQUAL expression_bit_or
    | NOT_EQUAL expression_bit_or
    | IN expression_bit_or
    | IS expression_bit_or
    ;

expression_bit_or
    : expression_bit_xor ( PIPE expression_bit_xor )*
    ;

expression_bit_xor
    : expression_bit_and ( CARRET expression_bit_and )*
    ;

expression_bit_and
    : expression_bit_shift ( AMP expression_bit_shift )*
    ;

expression_bit_shift
    : expression_add expression_bit_shift_right*
    ;

expression_bit_shift_right
    : BIT_LSHIFT expression_add
    | BIT_RSHIFT expression_add
    ;

expression_add
    : expression_mult expression_add_right*
    ;

expression_add_right
    : PLUS expression_mult
    | MINUS expression_mult
    | DOUBLE_PLUS expression_mult
    ;

expression_mult
    : expression_unary expression_mult_right*
    ;

expression_mult_right
    : STAR expression_unary
    | DOUBLE_STAR expression_unary
    | SLASH expression_unary
    | DOUBLE_SLASH expression_unary
    | PERCENT expression_unary
    | DOUBLE_PERCENT expression_unary
    ;

expression_unary
    : expression_unary_op? expression_exp
    ;

expression_unary_op
    : PLUS
    | MINUS
    | TILDE
    | NOT
    ;

expression_exp
    : expression_postfix ( DOUBLE_STAR expression_postfix )*

expression_postfix
    : expression_atom expression_access*
    ;

expression_access
    : LBRACK expression RBRACK
    | LPAREN arguments? RPAREN
    | LCURLY arguments? RCURLY
    | DOT SYMBOL
    | NULL_ACCESS SYMBOL
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
    | LPAREN expression RPAREN
    ; 

//-----------------------
// STATEMENTS
//-----------------------

statement
    : 
    | expression
    | BREAK
    | CONTINUE
    | RETURN expression?
    | loop
    | while
    | iterate
    | declaration
    | statement_block
    ;

statement_block
    : LCURLY SEMICOLON* ( statement ( SEMICOLON+ statement )* SEMICOLON* )? RCURLY
    ;

loop
    : LOOP ( 'from' expression )? 'to' expression ( 'by' expression' )? ( RARROW SYMBOL )? statement_block
    ;

while
    : WHILE expression statement_block
    ;
    
iterate
    : ITERATE expression ( RARROW SYMBOL ( COMMA SYMBOL )? )? statement_block
    ;

declaration
    : ( CONST | LET MUT? ) type? SYMBOL ASSIGN expression
    | ( CONST | LET MUT? ) type SYMBOL
    ;