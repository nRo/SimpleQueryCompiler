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

import de.alexgruen.query.QueryNode;
import de.alexgruen.query.generated.QueryBaseVisitor;
import de.alexgruen.query.generated.QueryParser;

/**
 * Created by Alex on 21.05.2017.
 */
public class TermQueryVisitor extends QueryBaseVisitor<QueryNode> {

    private QueryContext context;

    public TermQueryVisitor(QueryContext context) {
        this.context = context;
    }


    /**
     * Converts the root compilation context to the root query node
     * @param ctx root context
     * @return root node
     */
    @Override
    public QueryNode visitCompilationUnit(QueryParser.CompilationUnitContext ctx) {
        if (ctx.query() == null && ctx.full_search() == null) {
            throw new QueryCompilerException("no valid query found");
        }
        if (ctx.query() != null) {
            return visitQuery(ctx.query());
        } else {
            return visitFull_search(ctx.full_search());
        }
    }

    /**
     * Uses a {@link TextSearchVisitor} to convert a full text search context to a query node
     * @param ctx full search context
     * @return query node
     */
    @Override
    public QueryNode visitFull_search(QueryParser.Full_searchContext ctx) {
        TextSearchVisitor textSearchVisitor = new TextSearchVisitor();
        return textSearchVisitor.visitFull_search(ctx);
    }


    /**
     * Creates a query node from a query context.
     * Recursively creates all child query nodes for the root query context.
     * @param ctx root query context
     * @return query node
     */
    public QueryNode visitQuery(QueryParser.QueryContext ctx) {
        if (ctx.term() != null) {
            TermVisitor termVisitor = new TermVisitor(context);
            return termVisitor.visitTerm(ctx.term());
        }

        if (ctx.query().size() == 1) {
            TermQueryVisitor termQueryVisitor = new TermQueryVisitor(context);
            QueryNode queryNode = termQueryVisitor.visit(ctx.query(0));
            if (ctx.NEGATE() != null) {
                queryNode.setNegate(true);
            }
            return queryNode;
        }


        QueryNode parent = new QueryNode();
        QueryNode left = visitQueryRecursive(ctx.query(0));
        QueryNode right = visitQueryRecursive(ctx.query(1));
        parent.getChildren().add(left);
        parent.getChildren().add(right);
        parent.setOperator(context.getLogicalOperator(ctx.LOGICAL_OPERATOR().getText()));
        return parent;
    }


    /**
     * Recursive function to create query nodes from a query context
     * @param ctx query context
     * @return query node with all child query nodes
     */
    private QueryNode visitQueryRecursive(QueryParser.QueryContext ctx) {
        if (ctx.term() != null) {
            TermVisitor termVisitor = new TermVisitor(context);
            QueryNode n = termVisitor.visitTerm(ctx.term());
            if (ctx.NEGATE() != null) {
                n.setNegate(true);
            }
            return n;
        }

        if (ctx.query().size() == 1) {
            QueryNode n =  visitQueryRecursive(ctx.query(0));
            if (ctx.NEGATE() != null) {
                n.setNegate(true);
            }
            return n;
        }


        QueryNode parent = new QueryNode();
        QueryNode left = visitQueryRecursive(ctx.query(0));
        QueryNode right = visitQueryRecursive(ctx.query(1));
        parent.getChildren().add(left);
        parent.getChildren().add(right);
        parent.setOperator(context.getLogicalOperator(ctx.LOGICAL_OPERATOR().getText()));

        if (ctx.NEGATE() != null) {
            parent.setNegate(true);
        }
        return parent;
    }

}
