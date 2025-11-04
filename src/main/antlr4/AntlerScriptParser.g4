parser grammar AntlerScriptParser;
options { tokenVocab=AntlerScriptLexer; }

program
    : def stat EOF
    ;

stat: ID '=' expr ';'
    | expr ';'
    ;

def : ID '(' params? ')' '{' stat* '}' ;

expr: ID
    | INT
    | func
    | expr '+' expr
    ;

func : ID '(' args? ')' ;

params: ID (',' ID)* ;

args: expr (',' expr)* ;