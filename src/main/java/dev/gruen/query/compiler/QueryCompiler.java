// SPDX-License-Identifier: MIT
// Copyright (c) 2018 Alexander Grün

package dev.gruen.query.compiler;

import dev.gruen.query.*;
import dev.gruen.query.creator.LogicCreator;
import dev.gruen.query.creator.TermCreator;
import dev.gruen.query.optimization.QueryOptimization;
import dev.gruen.query.term.Term;
import dev.gruen.query.util.CompilerUtil;

import java.util.List;

/**
 * Compiles query strings to a target class, see {@link QueryCompilerBuilder} for creating instances.
 * <p>
 * A compiler can be shared between threads if the configured creators and optimizations are thread-safe.
 *
 * @param <T> target class
 */
public class QueryCompiler<T extends Query> {

    private final QueryContext<T> context;
    private final List<QueryOptimization> optimizations;
    private final QueryTreeCompiler<T> queryTreeCompiler;

    protected QueryCompiler(QueryContext<T> context, List<QueryOptimization> optimizations) {
        this.context = context;
        this.optimizations = List.copyOf(optimizations);
        this.queryTreeCompiler = new QueryTreeCompiler<>(context);
    }

    /**
     * Returns the {@link QueryContext}
     *
     * @return context
     */
    public QueryContext<T> getContext() {
        return context;
    }

    /**
     * Returns all assigned optimizations ({@link QueryOptimization}).
     *
     * @return unmodifiable list of optimizations
     */
    public List<QueryOptimization> getOptimizations() {
        return optimizations;
    }

    /**
     * Compiles an input string to the target class
     *
     * @param str input string
     * @return object of target class
     */
    public T compile(String str) {
        QueryTree tree = compileTree(str);
        return compile(tree);
    }

    /**
     * Compiles an input string to a query tree ({@link QueryTree}).
     * All assigned optimizations are applied to the freshly created tree in place.
     *
     * @param str input string
     * @return query tree
     */
    public QueryTree compileTree(String str) {
        QueryTree tree = queryTreeCompiler.compile(str);
        optimize(tree);
        return tree;
    }


    /**
     * Converts a query tree to the target class
     *
     * @param tree input query tree
     * @return object of target class
     */
    public T compile(QueryTree tree) {
        return compileTreeRec(tree.getRoot());
    }

    /**
     * Recursive function for converting query trees to target objects
     *
     * @param node current node
     * @return target object
     */
    private T compileTreeRec(QueryNode node) {
        if (node.getChildren().isEmpty() && node.getTerm() == null) {
            if (context.getEmptyCreator() == null) {
                throw new QueryCompilerException("no empty creator defined");
            }
            T t = context.getEmptyCreator().create(node, null, null);
            if (node.isNegate()) {
                t = negate(node, t);
            }
            return t;
        }
        //return if node contains term
        if (node.getTerm() != null) {
            T t = createTerm(node, node.getTerm());
            //negate term if required
            if (node.isNegate()) {
                t = negate(node, t);
            }
            return t;
        }

        //Create an array for results of child terms
        T[] terms = CompilerUtil.createArray(context.getCl(), node.getChildren().size());

        //recursive calculation of child terms
        for (int i = 0; i < node.getChildren().size(); i++) {
            terms[i] = compileTreeRec(node.getChildren().get(i));
        }

        //Create a new term using child terms and logical operation
        LogicCreator<T> logicCreator = context.getLogicCreator(node.getOperator());
        T t = logicCreator.create(node, terms);
        if (node.isNegate()) {
            t = negate(node, t);
        }
        return t;
    }

    /**
     * Use {@link LogicCreator} to negate a target object
     *
     * @param node node that is negated
     * @param t input object
     * @return negated object
     */
    private T negate(QueryNode node, T t) {
        T[] a = CompilerUtil.createArray(context.getCl(), 1);
        a[0] = t;
        return context.getLogicCreator(LogicalOperators.NOT).create(node, a);
    }

    /**
     * Create an object of target class for a term using the creator ({@link TermCreator}) specified in the {@link QueryContext}
     *
     * @param node node containing the term
     * @param term input term
     * @return object of target class
     */
    private T createTerm(QueryNode node, Term term) {
        TermCreator<T> creator = context.getTermCreator(term.getOperator());
        return creator.create(node, term.getField(), term.getValue());
    }

    /**
     * Apply all optimizations ({@link QueryOptimization}) specified in the {@link QueryContext} to a {@link QueryTree}.
     * The tree is modified in place.
     */
    private void optimize(QueryTree tree) {
        for (QueryOptimization optimization : optimizations) {
            optimization.apply(tree);
        }
    }
}
