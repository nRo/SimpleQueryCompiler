/*
 * Copyright (c) 2016 Alexander Grün
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

grammar Query;
 
/*
 * Parser Rules
 */
 @header {
   import java.util.*;
 }
@members {
    Set<String> customOperators = new HashSet<>();

    public Set<String> getCustomOperators(){
        return customOperators;
    }
     public boolean validOperator(String id){
        return customOperators.contains(id);
    }
}
compilationUnit   : (query|full_search) EOF ;

term :
NEGATE? OPEN_BRACKET term CLOSE_BRACKET|
variable term_operation value|
regex_term
;


regex_term :
variable MATCH REGEX;

query:
NEGATE? OPEN_BRACKET query CLOSE_BRACKET |
OPEN_BRACKET query LOGICAL_OPERATOR query CLOSE_BRACKET|
query LOGICAL_OPERATOR query|
term;


value: (NUMBER | BOOLEAN_VALUE | TEXT_VALUE | NULL);
variable: VAR | COLUMN | TEXT_VALUE;

term_operation: TERM_OPERATOR | custom_operator;
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

fragment UNESCAPED_STRING :~('.' | ' '|')' | '(' | '!' | '-' | '+' | '"' | '\'') ~('\''|' '|')' | '(')*;
fragment EQ : ('EQ' | 'eq' | '=' | '==');
fragment NE : ('NE' | 'ne' | '!=');
fragment LE : ('LE' | 'le' | '<=');
fragment LT : ('LT' | 'lt' | '<');
fragment GE : ('GE' | 'ge' | '>=');
fragment GT : ('GT' | 'gt' | '>');
fragment TM : ('TEXT' | 'text' | '*=');

REGEX : '/' (~('/') | '\\/')+ '/';
MATCH : ('~=' | '~' );


fragment AND : ('AND' | 'and' | '&' | '&&');
fragment OR : ('OR' | 'or' | '|' | '||');
fragment NOR : ('NOR' | 'nor') ;
fragment TEXT : UNESCAPED_STRING|STRING;
fragment VAR_NAME : TEXT ('.' TEXT)*;

OPEN_BRACKET: '(';
CLOSE_BRACKET: ')';

NEGATE: ('!'|'-');
POSITIVE: '+';

LOGICAL_OPERATOR : AND | OR | NOR;

TERM_OPERATOR : EQ | NE | LE | LT | GT | GE | TM;

NUMBER : '-'? DIGIT+([.,]DIGIT+)?;
BOOLEAN_VALUE: 'true' | 'false';
TEXT_VALUE : STRING;
NULL: 'null' | 'NULL' | 'NA' | 'na';

COLUMN : COL_PREFIX VAR_NAME;
VAR: VAR_NAME;
WS : (' ' | '\t' | '\n')+ -> channel(HIDDEN);
