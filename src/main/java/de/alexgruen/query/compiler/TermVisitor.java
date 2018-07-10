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
import de.alexgruen.query.term.Field;
import de.alexgruen.query.term.Term;
import de.alexgruen.query.term.TermOperator;
import de.alexgruen.query.term.Value;
import de.alexgruen.query.util.CompilerUtil;

/**
 * Created by Alex on 21.05.2017.
 */
public class TermVisitor extends QueryBaseVisitor<QueryNode> {

    private QueryContext context;

    public TermVisitor(QueryContext context) {
        this.context = context;
    }


    /**
     * Creates a query node from a term context.
     * Term query nodes represent the leafs in the query tree
     * @param ctx term context
     * @return query node
     */
    @Override
    public QueryNode visitTerm(QueryParser.TermContext ctx) {
        QueryNode queryNode;
        if (ctx.regex_term() != null) {
            //use regex visitor if necessary
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
     * Creates a query node from a {@link Field}, operator and {@link Value}
     * @param field term field
     * @param value term value
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
