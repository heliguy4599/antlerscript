lexer grammar AntlerScriptLexer;

@members {
    val inBracketStack = ArrayDeque<Boolean>()
}

fragment UNICODE_LETTER: [\p{L}\p{Emoji}] ;
fragment UNICODE_NUMBER: [\p{N}] ;
fragment IDENTIFIER: ( UNICODE_LETTER | '_' ) ( UNICODE_LETTER | UNICODE_NUMBER | '_' )* ;
fragment NEWLINE: '\r' | '\r'? '\n' ;

DIRECTIVE: '$' IDENTIFIER ;
ANNOTATION: '@' IDENTIFIER ;

// Numbers
fragment DEC_INTEGER: [0-9] ( '_'? [0-9] )* ;
fragment BIN_INTEGER: '0b' [01] ( '_'? [01] )* ;
fragment HEX_INTEGER: '0x' [0-9A-F] ( '_'? [0-9A-F] )* ;
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
    | HEX_INTEGER INTEGER_SUFFIX?
    | BIN_INTEGER INTEGER_SUFFIX?
    ;

FLOAT
    : DEC_INTEGER ( '.' DEC_INTEGER | EXPONENT_BASE10 ) FLOAT_SUFFIX?
    | DEC_INTEGER '.' DEC_INTEGER EXPONENT_BASE10 FLOAT_SUFFIX?
    | HEX_INTEGER ( '.' HEX_INTEGER | EXPONENT_BASE16 ) FLOAT_SUFFIX?
    | HEX_INTEGER '.' HEX_INTEGER EXPONENT_BASE16 FLOAT_SUFFIX?
    ;

// ANTLR4 doesn't support vertical tabs
WHITESPACE: [ \t\f]+   -> channel(HIDDEN);
COMMENT: '#' ~[\n\r]*  -> channel(HIDDEN);

RAW_STRING: '\\"' .*? '"' ;
STRING: '"' ( ~[\\] | '\\' . )* '"' ;

// Keywords
BREAK: 'break' ;
CONTINUE: 'continue' ;
RETURN: 'return' ;
LET: 'let' ;
FUNC: 'Func' ;
LIST: 'List' ;
ARRAY: 'Array' ;
MAP: 'Map' ;
LOOP: 'loop' ;
CLASS: 'Class' ;
ITERATE: 'iterate' ;
WHILE: 'while' ;
IF: 'if' ;
SWITCH: 'switch' ;
CONST: 'const' ;
AS: 'as' ;
IS: 'is' ;
IN: 'in' ;
AND: 'and' ;
NOT: 'not' ;
OR: 'or' ;
TRUE: 'true';
FALSE: 'false' ;
NULL: 'null' ;
EXTENDS: 'extends' ;
CAPTURE: 'capture' ;
MUT: 'mut' ;
ELSE: 'else' ;
SUPER: 'super' ;
UNDERSCORE: '_';
ELLIPSIS: '...';
CONSTRUCTOR: 'constructor' ;
PRIVATE_CONSTRUCTOR: '__constructor' ;
OPERATOR: 'operator';

// Symbol
SYMBOL: IDENTIFIER ;

// Characters
SEMICOLON: ( ';' | NEWLINE )+ { inBracketStack.lastOrNull() != true /* also ignore newlines in global (null) */ }? ;
LPAREN: '(' { inBracketStack.addLast(true) } ;
RPAREN: ')' { inBracketStack.removeLastOrNull() } ;
LBRACK: '[' { inBracketStack.addLast(true) } ;
RBRACK: ']' { inBracketStack.removeLastOrNull() } ;
LCURLY: '{' { inBracketStack.addLast(false) } ;
RCURLY: '}' { inBracketStack.removeLastOrNull() } ;
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
