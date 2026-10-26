// SPDX-License-Identifier: MIT
// Copyright (c) 2018 Alexander Grün

package dev.gruen.query.compiler;

import dev.gruen.query.*;
import dev.gruen.query.creator.LogicCreator;
import dev.gruen.query.creator.OperatorCreatorMap;
import dev.gruen.query.creator.TermCreator;
import dev.gruen.query.optimization.QueryOptimization;
import dev.gruen.query.optimization.RemoveRedundantBrackets;
import dev.gruen.query.term.TermOperator;
import dev.gruen.query.term.TermOperators;

import java.util.ArrayList;
import java.util.List;

public class QueryCompilerBuilder<T extends Query> {
    private final OperatorCreatorMap<TermOperator, TermCreator<T>> termCreators = new OperatorCreatorMap<>();
    private final OperatorCreatorMap<LogicalOperator, LogicCreator<T>> logicCreators = new OperatorCreatorMap<>();
    private final Class<T> cl;
    private final List<QueryOptimization> optimizations = new ArrayList<>();
    private TermCreator<T> emptyCreator;
    private boolean columnValues;

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
     * Create a new {@link QueryCompilerBuilder} for a specified query type using a {@link QueryCreator}.
     * All default logic and term operations can be defined using the {@link QueryCreator}.
     *
     * @param cl             query class
     * @param queryCreator default creator
     * @param <T>            query type
     * @return query compiler builder
     */
    public static <T extends Query> QueryCompilerBuilder<T> createDefault(Class<T> cl, QueryCreator<T> queryCreator) {
        QueryCompilerBuilder<T> queryCompilerBuilder = create(cl);
        queryCompilerBuilder.withQueryCreator(queryCreator);
        queryCompilerBuilder.withOptimization(new RemoveRedundantBrackets());
        return queryCompilerBuilder;
    }

    /**
     * Use a {@link QueryCreator} to define all default logic and term operations
     *
     * @param dc default creator
     * @return <code>self</code> for method chaining
     */
    public QueryCompilerBuilder<T> withQueryCreator(QueryCreator<T> dc) {
        withANDCreator(dc::and);
        withORCreator(dc::or);
        withXORCreator(dc::xor);
        withNORCreator(dc::nor);
        withNOTCreator((node, children) -> dc.not(node, children[0]));
        withTermCreator(TermOperators.EQ, dc::eq);
        withTermCreator(TermOperators.NE, dc::ne);
        withTermCreator(TermOperators.LT, dc::lt);
        withTermCreator(TermOperators.LE, dc::le);
        withTermCreator(TermOperators.GT, dc::gt);
        withTermCreator(TermOperators.GE, dc::ge);
        withTermCreator(TermOperators.REGEX, dc::regex);
        withTermCreator(TermOperators.TEXT, dc::text);
        withTermCreator(TermOperators.IN, dc::in);
        withTermCreator(TermOperators.NOT_IN, dc::notIn);
        withTermCreator(TermOperators.FULL_TEXT, (n, f, v) -> dc.fullSearch(v));
        withEmptyCreator((n, f, v) -> dc.empty());
        return this;
    }

    /**
     * Adds a {@link QueryOptimization} that is used to optimize the resulting {@link dev.gruen.query.QueryTree}
     * before the final query is compiled.
     * The order of added optimizations is important and can influence the result.
     *
     * @param optimization query optimization
     * @return <code>self</code> for method chaining
     */
    public QueryCompilerBuilder<T> withOptimization(QueryOptimization optimization) {
        this.optimizations.add(optimization);
        return this;
    }

    /**
     * Adds an {@link TermCreator} that is used to create 'empty' terms (match all)
     *
     * @param creator term creator
     * @return <code>self</code> for method chaining
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
     * @return <code>self</code> for method chaining
     */
    public QueryCompilerBuilder<T> withTermCreator(TermOperator op, TermCreator<T> creator) {
        termCreators.add(op, creator);
        return this;
    }

    /**
     * Adds an {@link LogicCreator} that is used to create a new term by joining terms using the logic operator <code>AND</code>
     *
     * @param creator logic creator
     * @return <code>self</code> for method chaining
     */
    public QueryCompilerBuilder<T> withANDCreator(LogicCreator<T> creator) {
        logicCreators.add(LogicalOperators.AND, creator);
        return this;
    }

    /**
     * Adds an {@link LogicCreator} that is used to create a new term by joining terms using the logic operator <code>OR</code>
     *
     * @param creator logic creator
     * @return <code>self</code> for method chaining
     */
    public QueryCompilerBuilder<T> withORCreator(LogicCreator<T> creator) {
        logicCreators.add(LogicalOperators.OR, creator);
        return this;
    }

    /**
     * Adds an {@link LogicCreator} that is used to create a new term by joining terms using the logic operator <code>XOR</code>
     *
     * @param creator logic creator
     * @return <code>self</code> for method chaining
     */
    public QueryCompilerBuilder<T> withXORCreator(LogicCreator<T> creator) {
        logicCreators.add(LogicalOperators.XOR, creator);
        return this;
    }

    /**
     * Adds an {@link LogicCreator} that is used to create a new term by joining terms using the logic operator <code>NOR</code>
     *
     * @param creator logic creator
     * @return <code>self</code> for method chaining
     */
    public QueryCompilerBuilder<T> withNORCreator(LogicCreator<T> creator) {
        logicCreators.add(LogicalOperators.NOR, creator);
        return this;
    }

    /**
     * Adds an {@link LogicCreator} that is used to create a new term by negating an input term
     *
     * @param creator logic creator
     * @return <code>self</code> for method chaining
     */
    public QueryCompilerBuilder<T> withNOTCreator(LogicCreator<T> creator) {
        logicCreators.add(LogicalOperators.NOT, creator);
        return this;
    }

    /**
     * Allows column references as right-hand value of a term, e.g. <code>.a &gt; .b</code> or
     * <code>price lt .limit</code>. Creators receive such a value as {@link dev.gruen.query.term.Value.Type#FIELD}.
     * Without this option a column on the right-hand side is a syntax error.
     *
     * @return <code>self</code> for method chaining
     */
    public QueryCompilerBuilder<T> withColumnValues() {
        return withColumnValues(true);
    }

    /**
     * Enables or disables column references as right-hand value of a term, see {@link #withColumnValues()}.
     * Disabled by default.
     *
     * @param enabled true to allow column references as values
     * @return <code>self</code> for method chaining
     */
    public QueryCompilerBuilder<T> withColumnValues(boolean enabled) {
        this.columnValues = enabled;
        return this;
    }

    /**
     * Creates the query context used by the resulting compiler
     *
     * @return query context
     */
    private QueryContext<T> createContext() {
        return new QueryContext<>(
                termCreators, logicCreators, emptyCreator, cl, columnValues
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
