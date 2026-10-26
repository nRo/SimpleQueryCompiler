// SPDX-License-Identifier: MIT
// Copyright (c) 2018 Alexander Grün

grammar Query;

@header {
    package dev.gruen.query.generated;
    import java.util.*;
}

@members {
    Set<String> customOperators = new HashSet<>();
    boolean columnValues = false;

    public Set<String> getCustomOperators(){
        return customOperators;
    }
     public boolean validOperator(String id){
        return customOperators.contains(id);
    }

    public boolean isColumnValues(){
        return columnValues;
    }

    public void setColumnValues(boolean columnValues){
        this.columnValues = columnValues;
    }
}

compilationUnit   : (query|full_search) EOF ;

term :
NEGATE? OPEN_BRACKET term CLOSE_BRACKET |
variable term_operation value |
{columnValues}? variable term_operation column_value |
variable term_list_operation value_list |
regex_term
;

regex_term :
variable MATCH REGEX;

value_list:
OPEN_BRACKET value (COMMA value)* CLOSE_BRACKET;

// Alternatives are listed from highest to lowest precedence: AND binds tighter than XOR, XOR tighter than OR/NOR.
// All binary operators are left-associative.
query:
NEGATE? OPEN_BRACKET query CLOSE_BRACKET |
query AND query |
query XOR query |
query (OR | NOR) query |
term;


value: (NUMBER | BOOLEAN_VALUE | TEXT_VALUE | NULL);
// column reference as right-hand value, only active if the parser flag columnValues is set
column_value: COLUMN;
variable: VAR | COLUMN | TEXT_VALUE;

term_operation: TERM_OPERATOR | custom_operator;

term_list_operation: TERM_LIST_OPERATOR;

custom_operator: VAR {validOperator($VAR.getText())}?;

full_search: full_search_part+;
full_search_modifier: NEGATE;
full_search_part: full_search_modifier? full_search_value;
full_search_value: VAR | NUMBER | BOOLEAN_VALUE | TEXT_VALUE | NULL;
/*
 * Lexer Rules
 */
fragment DIGIT : [0-9];
fragment COL_PREFIX : '.';

fragment CHAR : [a-zA-Z];
fragment STRING : '\'' (~('\'')|'\\\'') * '\''|'"' (~('"')|'\\"')* '"';

fragment UNESCAPED_STRING :~('.' | ' '|')' | '(' | '!' | '-' | '+' | '"' | '\'' | ',') ~('\''|' '|')' | '(' | ',')*;
fragment EQ : ('EQ' | 'eq' | '=' | '==');
fragment NE : ('NE' | 'ne' | '!=');
fragment LE : ('LE' | 'le' | '<=');
fragment LT : ('LT' | 'lt' | '<');
fragment GE : ('GE' | 'ge' | '>=');
fragment GT : ('GT' | 'gt' | '>');
fragment TM : ('TEXT' | 'text' | '*=');

REGEX : '/' (~('/') | '\\/')+ '/';
MATCH : ('~=' | '~' );
fragment IN : ('IN' | 'in');
fragment NOT_IN : ('!IN' | '!in');

fragment TEXT : UNESCAPED_STRING|STRING;
fragment VAR_NAME : TEXT ('.' TEXT)*;

OPEN_BRACKET: '(';
CLOSE_BRACKET: ')';
COMMA : ',';

NEGATE: ('!'|'-');
POSITIVE: '+';

AND : 'AND' | 'and' | '&' | '&&';
OR : 'OR' | 'or' | '|' | '||';
NOR : 'NOR' | 'nor';
XOR : 'XOR' | 'xor';

TERM_OPERATOR : EQ | NE | LE | LT | GT | GE | TM;

TERM_LIST_OPERATOR : IN | NOT_IN;

NUMBER : '-'? DIGIT+([.]DIGIT+)?;
BOOLEAN_VALUE: 'true' | 'false';
TEXT_VALUE : STRING;
NULL: 'null' | 'NULL' | 'NA' | 'na';

COLUMN : COL_PREFIX VAR_NAME;
VAR: VAR_NAME;
WS : (' ' | '\t' | '\n')+ -> channel(HIDDEN);
