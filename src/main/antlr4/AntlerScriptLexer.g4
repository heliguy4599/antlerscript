lexer grammar AntlerScriptLexer;

@header {
import java.util.Deque;
import java.util.ArrayDeque;
}

@members {
	public final Deque<Boolean> ignoreSemicolons = new ArrayDeque<>();
}

fragment UNICODE_LETTER: [\p{L}\p{Emoji}] ;
fragment UNICODE_NUMBER: [\p{N}] ;
fragment IDENTIFIER: ( UNICODE_LETTER | '_' ) ( UNICODE_LETTER | UNICODE_NUMBER | '_' )* ;

DIRECTIVE: '$' IDENTIFIER ;
ANNOTATION: '@' IDENTIFIER ;

// Numbers
fragment DEC_INTEGER: [0-9] ( '_'? [0-9] )* ;
fragment BIN_INTEGER: [01] ( '_'? [01] )* ;
fragment HEX_INTEGER: [0-9A-F] ( '_'? [0-9A-F] )* ;
fragment EXPONENT_BASE10: 'e' ( '+' | '-' )? DEC_INTEGER;
fragment EXPONENT_BASE16: ( 'e' | 'p' )  ( '+' | '-' )? DEC_INTEGER;
fragment INTEGER_SUFFIX
	: 'i' ( '8' | '16' | '32' | '64' | '128' )?
	| 'u' ( '8' | '16' | '32' | '64' | '128' )?
	;
fragment FLOAT_SUFFIX
	: 'f' ( '8' | '16' | '32' | '64' | '128' )?
	;

INTEGER
	: DEC_INTEGER INTEGER_SUFFIX?
	| '0x' HEX_INTEGER INTEGER_SUFFIX?
	| '0b' BIN_INTEGER INTEGER_SUFFIX?
	;

FLOAT
	: DEC_INTEGER ( '.' DEC_INTEGER | EXPONENT_BASE10 ) FLOAT_SUFFIX?
	| DEC_INTEGER '.' DEC_INTEGER EXPONENT_BASE10 FLOAT_SUFFIX?
	| '0x' HEX_INTEGER ( '.' HEX_INTEGER | EXPONENT_BASE16 ) FLOAT_SUFFIX?
	| '0x' HEX_INTEGER '.' HEX_INTEGER EXPONENT_BASE16 FLOAT_SUFFIX?
	;

// ANTLR4 doesn't support vertical tabs
WHITESPACE: [ \t\f\u000B]+   -> channel(HIDDEN);
COMMENT: '#' ~[\n\r]*  -> channel(HIDDEN);

RAW_STRING: '`' .*? '`' ;
STRING: '"' ( ~[\\] | '\\' . )* '"' ;

// Keywords
AND: 'and' ;
ARRAY: 'Array' ;
AS: 'as' ;
BREAK: 'break' ;
BY: 'by' ;
CAPTURE: 'capture' ;
CASE: 'case' ;
CAST: 'cast' ;
CLASS: 'Class' ;
CONST: 'const' ;
CONSTRUCTOR: 'constructor' ;
CONTINUE: 'continue' ;
DEFER: 'defer' ;
ELIF: 'elif' ;
ELLIPSIS: '...' ;
ELSE: 'else' ;
ENUM: 'Enum' ;
EXTENDS: 'extends' ;
FALSE: 'false' ;
FROM: 'from' ;
FUNC: 'Func' ;
IF: 'if' ;
IN: 'in' ;
IS: 'is' ;
ITERATE: 'iterate' ;
LET: 'let' ;
LIST: 'List' ;
LOOP: 'loop' ;
MAP: 'Map' ;
MUT: 'mut' ;
NOT: 'not' ;
NULL: 'null' ;
OPERATOR: 'operator';
OR: 'or' ;
PRIVATE_CONSTRUCTOR: '__constructor' ;
RETURN: 'return' ;
SELF_CLASS: 'Self' ;
SELF_INSTANCE: 'self' ;
SELECT: 'select' ;
SUPER: 'super' ;
SWITCH: 'switch' ;
TO: 'to' ;
TRUE: 'true' ;
TYPE: 'type' ;
UNDERSCORE: '_' ;
WHILE: 'while' ;

// Characters

NEWLINE : ( '\r' | '\r'? '\n' ) { ignoreSemicolons.peekFirst() != Boolean.TRUE }? ;
NEWLINE_SKIP : ( '\r' | '\r'? '\n' ) -> channel(HIDDEN) ;

SEMICOLON: ';';
LPAREN: '(' { ignoreSemicolons.push(true); } ;
RPAREN: ')' { ignoreSemicolons.pollFirst(); } ;
LBRACK: '[' { ignoreSemicolons.push(true); } ;
RBRACK: ']' { ignoreSemicolons.pollFirst(); } ;
LCURLY: '{' { ignoreSemicolons.push(false); } ;
RCURLY: '}' { ignoreSemicolons.pollFirst(); } ;
NULL_ACCESS: '?.' ;
QMARK: '?' ;
RARROW: '->' ;
COLON: ':' ;
COMMA: ',' ;
DOT: '.' ;
EQUAL: '=' ;
PLUS: '+' ;
MINUS: '-' ;
STAR: '*' ;
SLASH: '/' ;
PERCENT: '%' ;
LESSER_THAN: '<';
GREATER_THAN: '>';
PIPE: '|' ;
AMP: '&' ;
CARRET: '^' ;
TILDE: '~' ;
BIT_LSHIFT: '<<' ;
BIT_RSHIFT: '>>' ;
DOUBLE_QMARK: '??' ;
DOUBLE_PLUS: '++';
DOUBLE_STAR: '**' ;
DOUBLE_SLASH: '//' ;
DOUBLE_PERCENT: '%%' ;
DOUBLE_EQUAL: '==' ;
DOT_EQUAL: '.=' ;
NOT_EQUAL: '!=' ;
PLUS_EQ: '+=';
MINUS_EQ: '-=';
STAR_EQ: '*=';
DOUBLE_STAR_EQ: '**=';
SLASH_EQ: '/=';
DOUBLE_SLASH_EQ: '//=';
PERCENT_EQ: '%=';
DOUBLE_PERCENT_EQ: '%%=';
LESSER_THAN_EQ: '<=' ;
GREATER_THAN_EQ: '>=' ;
PIPE_EQ: '|=';
AMP_EQ: '&=';
TILDE_EQ: '~=';
CARRET_EQ: '^=';
BIT_LSHIFT_EQ: '<<=';
BIT_RSHIFT_EQ: '>>=';
DOUBLE_PLUS_EQ: '++=';
DOUBLE_QMARK_EQ: '??=';

// Symbol
SYMBOL: IDENTIFIER ;
