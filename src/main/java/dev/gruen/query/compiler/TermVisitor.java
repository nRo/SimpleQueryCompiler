// SPDX-License-Identifier: MIT
// Copyright (c) 2018 Alexander Grün

package dev.gruen.query.compiler;


import dev.gruen.query.QueryNode;
import dev.gruen.query.generated.QueryBaseVisitor;
import dev.gruen.query.generated.QueryParser;
import dev.gruen.query.term.Field;
import dev.gruen.query.term.Term;
import dev.gruen.query.term.TermOperator;
import dev.gruen.query.term.Value;
import dev.gruen.query.util.CompilerUtil;

public class TermVisitor extends QueryBaseVisitor<QueryNode> {

    private final QueryContext<?> context;

    public TermVisitor(QueryContext<?> context) {
        this.context = context;
    }


    /**
     * Creates a query node from a term context.
     * Term query nodes represent the leafs in the query tree
     *
     * @param ctx term context
     * @return query node
     */
    @Override
    public QueryNode visitTerm(QueryParser.TermContext ctx) {
        QueryNode queryNode;
        if(ctx.value_list() != null) {
            queryNode = createFieldListFilterNode(ctx);
        }
        else if (ctx.regex_term() != null) {
            //use a regex visitor if necessary
            RegexTermVisitor regexTermVisitor = new RegexTermVisitor();
            queryNode = regexTermVisitor.visitRegex_term(ctx.regex_term());
        } else {
            queryNode = createFieldFilterNode(ctx);
        }
        if (ctx.NEGATE() != null) {
            queryNode.setNegate(true);
        }
        return queryNode;
    }

    /**
     * Creates a query node from a single term (field OPERATOR value)
     *
     * @param ctx term context
     * @return query node
     */
    private QueryNode createFieldFilterNode(QueryParser.TermContext ctx) {
        Field field = CompilerUtil.createField(ctx.variable());
        String operation = ctx.term_operation().getText();
        Value value = CompilerUtil.createValue(ctx.value());
        return createFieldFilterNode(field, value, operation);
    }

    /**
     * Creates a query node from a single term (field OPERATOR value)
     *
     * @param ctx term context
     * @return query node
     */
    private QueryNode createFieldListFilterNode(QueryParser.TermContext ctx) {
        Field field = CompilerUtil.createField(ctx.variable());
        String operation = ctx.term_list_operation().getText();
        Value value = CompilerUtil.createListValue(ctx.value_list());
        return createFieldFilterNode(field, value, operation);
    }

    /**
     * Creates a query node from a {@link Field}, operator and {@link Value}
     *
     * @param field     term field
     * @param value     term value
     * @param operation term operator
     * @return query node
     */
    private QueryNode createFieldFilterNode(Field field, Value value, String operation) {
        TermOperator termOperator = context.getTermOperator(operation);
        if (termOperator == null) {
            throw new QueryCompilerException(String.format("unsupported filter operation '%s'", operation));
        }
        return new QueryNode(new Term(field, termOperator, value));
    }
}
