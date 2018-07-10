/*
 * MIT License
 *
 * Copyright (c) 2018 Alexander Grün
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

package de.alexgruen.query;

import de.alexgruen.query.compiler.QueryCompilerException;
import de.alexgruen.query.term.Field;
import de.alexgruen.query.term.Value;

public abstract class DefaultCreator<T> {

    /**
     * Query creator for != operator
     * @param field input field
     * @param value input value
     * @return created query object
     */
    public T ne(Field field, Value value) {
        throw new QueryCompilerException("'ne' operator is not allowed");
    }

    /**
     * Query creator for == operator
     * @param field input field
     * @param value input value
     * @return created query object
     */
    public T eq(Field field, Value value) {
        throw new QueryCompilerException("'eq' operator is not allowed");
    }

    /**
     * Query creator for >= operator
     * @param field input field
     * @param value input value
     * @return created query object
     */
    public T ge(Field field, Value value) {
        throw new QueryCompilerException("'ge' operator is not allowed");
    }

    /**
     * Query creator for > operator
     * @param field input field
     * @param value input value
     * @return created query object
     */
    public T gt(Field field, Value value) {
        throw new QueryCompilerException("'gt' operator is not allowed");
    }

    /**
     * Query creator for < operator
     * @param field input field
     * @param value input value
     * @return created query object
     */
    public T lt(Field field, Value value) {
        throw new QueryCompilerException("'lt' operator is not allowed");
    }

    /**
     * Query creator for <= operator
     * @param field input field
     * @param value input value
     * @return created query object
     */
    public T le(Field field, Value value) {
        throw new QueryCompilerException("'le' operator is not allowed");
    }

    /**
     * Query creator for the regex operator
     * @param field input field
     * @param value input value
     * @return created query object
     */
    public T regex(Field field, Value value) {
        throw new QueryCompilerException("'regex' operator is not allowed");
    }

    /**
     * Query creator for the text operator
     * @param field input field
     * @param value input value
     * @return created query object
     */
    public T text(Field field, Value value) {
        throw new QueryCompilerException("'text' operator is not allowed");
    }

    /**
     * Logic creator for the negation of a query
     * @param v input query
     * @return created query object
     */
    public T not(T v) {
        throw new QueryCompilerException("'not' operator is not allowed");
    }

    /**
     * Logic creator for the AND concatenation of queries
     * @param v input queries
     * @return created query object
     */
    public T and(T... v) {
        throw new QueryCompilerException("'and' operator is not allowed");
    }

    /**
     * Logic creator for the OR concatenation of queries
     * @param v input queries
     * @return created query object
     */
    public T or(T... v) {
        throw new QueryCompilerException("'or' operator is not allowed");
    }

    /**
     * Logic creator for the XOR concatenation of queries
     * @param v input queries
     * @return created query object
     */
    public T xor(T... v) {
        throw new QueryCompilerException("'xor' operator is not allowed");
    }

    /**
     * Logic creator for the NOR concatenation of queries
     * @param v input queries
     * @return created query object
     */
    public T nor(T... v) {
        throw new QueryCompilerException("'nor' operator is not allowed");
    }

    /**
     * Term creator for fulltext search queries
     * @param value input value
     * @return created query object
     */
    public T fullSearch(Value value) {
        throw new QueryCompilerException("fullSearch is not allowed");
    }

    /**
     * Term creator for empty queries (match all)
     * @return created query object
     */
    public T empty() {
        throw new QueryCompilerException("empty is not defined");
    }


}
