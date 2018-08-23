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

import de.alexgruen.query.*;
import de.alexgruen.query.creator.LogicCreator;
import de.alexgruen.query.creator.TermCreator;
import de.alexgruen.query.optimization.QueryOptimization;
import de.alexgruen.query.term.Term;
import de.alexgruen.query.util.CompilerUtil;

import java.util.List;

public class QueryCompiler<T extends Query> {

    private QueryContext<T> context;
    private List<QueryOptimization> optimizations;
    private QueryTreeCompiler queryTreeCompiler;

    protected QueryCompiler(QueryContext<T> context, List<QueryOptimization> optimizations) {
        this.context = context;
        this.optimizations = optimizations;
        this.queryTreeCompiler = new QueryTreeCompiler(context);
    }

    /**
     * Create a new {@link QueryCompilerBuilder}
     * @param cl target class
     * @param <T> target type
     * @return query compiler instance
     */
    public static <T extends Query> QueryCompilerBuilder<T> create(Class<T> cl) {
        return QueryCompilerBuilder.create(cl);
    }


    /**
     * Create a new {@link QueryCompilerBuilder} using a {@link DefaultCreator}.
     * @param cl target class
     * @param defaultCreator default operation creator
     * @param <T> target type
     * @return query compiler instance
     */
    public static <T extends Query> QueryCompilerBuilder<T> createDefault(Class<T> cl, DefaultCreator<T> defaultCreator) {
        return QueryCompilerBuilder.createDefault(cl, defaultCreator);
    }

    /**
     * Returns the {@link QueryContext}
     * @return context
     */
    public QueryContext<T> getContext() {
        return context;
    }

    /**
     * Returns all assigned optimizations ({@link QueryOptimization}
     * @return optimizations
     */
    public List<QueryOptimization> getOptimizations() {
        return optimizations;
    }

    /**
     * Compiles an input string to the target class
     * @param str input string
     * @return object of target class
     */
    public T compile(String str) {
        QueryTree tree = compileTree(str);
        return compile(tree);
    }

    /**
     * Compiles an input string to a query tree ({@link QueryTree}
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
     * @param tree input query tree
     * @return object of target class
     */
    public T compile(QueryTree tree) {
        return compileTreeRec(tree.getRoot());
    }

    /**
     * Recursive function for converting query trees to target objects
     * @param node current node
     * @return target object
     */
    private T compileTreeRec(QueryNode node) {
        if(node.getChildren().isEmpty() && node.getTerm() == null){
            if(context.getEmptyCreator() == null){
                throw new QueryCompilerException("no empty creator defined");
            }
            return context.getEmptyCreator().create(node,null,null);
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

        //Create array for results of child terms
        T[] terms = CompilerUtil.createArray(context.getCl(), node.getChildren().size());

        //recursive calculation of child terms
        for (int i = 0; i < node.getChildren().size(); i++) {
            terms[i] = compileTreeRec(node.getChildren().get(i));
        }

        //Create new term using child terms and logical operation
        LogicCreator<T> logicCreator = context.getLogicCreator(node.getOperator());
        T t = logicCreator.create(node, terms);
        if (node.isNegate()) {
            t = negate(node, t);
        }
        return t;
    }

    /**
     * Use {@link LogicCreator} to negate target object
     * @param t input object
     * @return negated object
     */
    private T negate(QueryNode node, T t) {
        T[] a = CompilerUtil.createArray(context.getCl(), 1);
        a[0] = t;
        return context.getLogicCreator(LogicalOperators.NOT).create(node, a);
    }

    /**
     * Create object of target class for a term using the creator ({@link TermCreator}) specified in the {@link QueryContext}
     * @param term input term
     * @return object of target class
     */
    private T createTerm(QueryNode node, Term term) {
        TermCreator<T> creator = context.getTermCreator(term.getOperator());
        return creator.create(node, term.getField(), term.getValue());
    }

    /**
     * Apply all optimizations ({@link QueryOptimization}) specified in the {@link QueryContext} to a {@link QueryTree}
     * @param tree
     */
    private void optimize(QueryTree tree) {
        for (QueryOptimization optimization : optimizations) {
            optimization.apply(tree);
        }
    }
}
