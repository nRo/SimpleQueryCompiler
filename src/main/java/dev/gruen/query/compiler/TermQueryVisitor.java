// SPDX-License-Identifier: MIT
// Copyright (c) 2018 Alexander Grün

package dev.gruen.query.compiler;

import dev.gruen.query.QueryNode;
import dev.gruen.query.generated.QueryBaseVisitor;
import dev.gruen.query.generated.QueryParser;

public class TermQueryVisitor extends QueryBaseVisitor<QueryNode> {

    private final QueryContext<?> context;

    public TermQueryVisitor(QueryContext<?> context) {
        this.context = context;
    }


    /**
     * Converts the root compilation context to the root query node
     *
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
     *
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
     *
     * @param ctx root query context
     * @return query node
     */
    @Override
    public QueryNode visitQuery(QueryParser.QueryContext ctx) {
        if (ctx.term() != null) {
            return new TermVisitor(context).visitTerm(ctx.term());
        }

        if (ctx.query().size() == 1) {
            // bracketed query, optionally negated
            QueryNode node = visitQuery(ctx.query(0));
            if (ctx.NEGATE() != null) {
                node.setNegate(!node.isNegate());
            }
            return node;
        }

        // binary alternative: query <operator> query, the operator is the second child
        QueryNode parent = new QueryNode();
        parent.getChildren().add(visitQuery(ctx.query(0)));
        parent.getChildren().add(visitQuery(ctx.query(1)));
        parent.setOperator(context.getLogicalOperator(ctx.getChild(1).getText()));
        return parent;
    }

}
