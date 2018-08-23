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

package de.alexgruen.query.compiler;

import de.alexgruen.query.DefaultCreator;
import de.alexgruen.query.LogicalOperator;
import de.alexgruen.query.LogicalOperators;
import de.alexgruen.query.Query;
import de.alexgruen.query.creator.LogicCreator;
import de.alexgruen.query.creator.OperatorCreatorMap;
import de.alexgruen.query.creator.TermCreator;
import de.alexgruen.query.optimization.QueryOptimization;
import de.alexgruen.query.optimization.RemoveRedundantBrackets;
import de.alexgruen.query.term.TermOperator;
import de.alexgruen.query.term.TermOperators;

import java.util.ArrayList;
import java.util.List;

public class QueryCompilerBuilder<T extends Query> {
    private OperatorCreatorMap<TermOperator, TermCreator<T>> termCreators = new OperatorCreatorMap<>();
    private OperatorCreatorMap<LogicalOperator, LogicCreator<T>> logicCreators = new OperatorCreatorMap<>();
    private Class<T> cl;
    private List<QueryOptimization> optimizations = new ArrayList<>();
    private TermCreator<T> emptyCreator;

    private QueryCompilerBuilder(Class<T> cl) {
        this.cl = cl;
    }

    /**
     * Create a new {@link QueryCompilerBuilder} for a specified query type
     *
     * @param cl  query class
     * @param <T> query type
     * @return query compiler builder
     */
    public static <T extends Query> QueryCompilerBuilder<T> create(Class<T> cl) {
        return new QueryCompilerBuilder<>(cl);
    }

    /**
     * Create a new {@link QueryCompilerBuilder} for a specified query type using a {@link DefaultCreator}.
     * All default logic and term operations can be defined using the {@link DefaultCreator}.
     *
     * @param cl             query class
     * @param defaultCreator default creator
     * @param <T>            query type
     * @return query compiler builder
     */
    public static <T extends Query> QueryCompilerBuilder<T> createDefault(Class<T> cl, DefaultCreator<T> defaultCreator) {
        QueryCompilerBuilder<T> queryCompilerBuilder = create(cl);
        queryCompilerBuilder.withDefaultCreator(defaultCreator);
        queryCompilerBuilder.withOptimization(new RemoveRedundantBrackets());
        return queryCompilerBuilder;
    }

    /**
     * Use a {@link DefaultCreator} to define all default logic and term operations
     *
     * @param dc default creator
     * @return <tt>self</tt> for method chaining
     */
    public QueryCompilerBuilder<T> withDefaultCreator(DefaultCreator<T> dc) {
        withANDCreator((dc::and));
        withORCreator((dc::or));
        withXORCreator((dc::xor));
        withNORCreator((dc::nor));
        withNOTCreator((node, children) -> dc.not(children[0]));
        withTermCreator(TermOperators.EQ, dc::eq);
        withTermCreator(TermOperators.NE, dc::ne);
        withTermCreator(TermOperators.LT, dc::lt);
        withTermCreator(TermOperators.LE, dc::le);
        withTermCreator(TermOperators.GT, dc::gt);
        withTermCreator(TermOperators.GE, dc::ge);
        withTermCreator(TermOperators.REGEX, dc::regex);
        withTermCreator(TermOperators.TEXT, dc::text);
        withTermCreator(TermOperators.FULL_TEXT, (n, f, v) -> dc.fullSearch(v));
        withEmptyCreator((n, f, v) -> dc.empty());
        return this;
    }

    /**
     * Adds a {@link QueryOptimization} that is used to optimize the resulting {@link de.alexgruen.query.QueryTree}
     * before the final query is compiled.
     * The order of added optimizations is important and can influence the result.
     *
     * @param optimization query optimization
     * @return <tt>self</tt> for method chaining
     */
    public QueryCompilerBuilder<T> withOptimization(QueryOptimization optimization) {
        this.optimizations.add(optimization);
        return this;
    }

    /**
     * Adds an {@link TermCreator} that is used to create 'empty' terms (match all)
     *
     * @param creator term creator
     * @return <tt>self</tt> for method chaining
     */
    public QueryCompilerBuilder<T> withEmptyCreator(TermCreator<T> creator) {
        this.emptyCreator = creator;
        return this;
    }

    /**
     * Adds an {@link TermCreator} that is used to create terms containing the respective operator
     *
     * @param op      term operator
     * @param creator term creator
     * @return <tt>self</tt> for method chaining
     */
    public QueryCompilerBuilder<T> withTermCreator(TermOperator op, TermCreator<T> creator) {
        termCreators.add(op, creator);
        return this;
    }

    /**
     * Adds an {@link LogicCreator} that is used to create a new term by joining terms using the logic operator <tt>AND</tt>
     *
     * @param creator logic creator
     * @return <tt>self</tt> for method chaining
     */
    public QueryCompilerBuilder<T> withANDCreator(LogicCreator<T> creator) {
        logicCreators.add(LogicalOperators.AND, creator);
        return this;
    }

    /**
     * Adds an {@link LogicCreator} that is used to create a new term by joining terms using the logic operator <tt>OR</tt>
     *
     * @param creator logic creator
     * @return <tt>self</tt> for method chaining
     */
    public QueryCompilerBuilder<T> withORCreator(LogicCreator<T> creator) {
        logicCreators.add(LogicalOperators.OR, creator);
        return this;
    }

    /**
     * Adds an {@link LogicCreator} that is used to create a new term by joining terms using the logic operator <tt>XOR</tt>
     *
     * @param creator logic creator
     * @return <tt>self</tt> for method chaining
     */
    public QueryCompilerBuilder<T> withXORCreator(LogicCreator<T> creator) {
        logicCreators.add(LogicalOperators.XOR, creator);
        return this;
    }

    /**
     * Adds an {@link LogicCreator} that is used to create a new term by joining terms using the logic operator <tt>NOR</tt>
     *
     * @param creator logic creator
     * @return <tt>self</tt> for method chaining
     */
    public QueryCompilerBuilder<T> withNORCreator(LogicCreator<T> creator) {
        logicCreators.add(LogicalOperators.NOR, creator);
        return this;
    }

    /**
     * Adds an {@link LogicCreator} that is used to create a new term by negating an input term
     *
     * @param creator logic creator
     * @return <tt>self</tt> for method chaining
     */
    public QueryCompilerBuilder<T> withNOTCreator(LogicCreator<T> creator) {
        logicCreators.add(LogicalOperators.NOT, creator);
        return this;
    }

    /**
     * Creates the query context used by the resulting compiler
     *
     * @return query context
     */
    private QueryContext<T> createContext() {
        return new QueryContext<T>(
                termCreators, logicCreators, emptyCreator, cl
        );
    }

    /**
     * Creates a compiler using all variables set in the builder
     *
     * @return query compiler
     */
    public QueryCompiler<T> build() {
        return new QueryCompiler<>(createContext(), optimizations);
    }

}
