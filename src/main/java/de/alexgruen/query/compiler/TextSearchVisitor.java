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


import de.alexgruen.query.LogicalOperators;
import de.alexgruen.query.QueryNode;
import de.alexgruen.query.generated.QueryBaseVisitor;
import de.alexgruen.query.generated.QueryParser;
import de.alexgruen.query.term.Field;
import de.alexgruen.query.term.Term;
import de.alexgruen.query.term.TermOperators;
import de.alexgruen.query.term.Value;
import de.alexgruen.query.util.CompilerUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 21.05.2017.
 */
public class TextSearchVisitor extends QueryBaseVisitor<QueryNode> {

    /**
     * Creates the root query node from a full text search context
     * A text search query tree only consists of a root node and leafs.
     * All children are joined using the AND operation.
     * @param ctx text search context
     * @return root query node
     */
    @Override
    public QueryNode visitFull_search(QueryParser.Full_searchContext ctx) {
        List<Value> required = new ArrayList<>();
        List<Value> forbidden = new ArrayList<>();
        for (QueryParser.Full_search_partContext vctx : ctx.full_search_part()) {
            Value val = CompilerUtil.createValue(vctx.full_search_value());
            if (vctx.full_search_modifier() == null) {
                required.add(val);
                continue;
            }
            if (vctx.full_search_modifier().NEGATE() != null) {
                forbidden.add(val);
            } else {
                required.add(val);
            }

        }
        QueryNode node = new QueryNode();
        node.setOperator(LogicalOperators.AND);


        if (required.size() > 0) {
            for (int i = 0; i < required.size(); i++) {
                node.getChildren().add(
                        new QueryNode(
                                new Term(
                                        Field.ALL_FIELDS,
                                        TermOperators.FULL_TEXT,
                                        required.get(i)
                                )
                        )
                );
            }
        }

        if (forbidden.size() > 0) {
            for (int i = 0; i < forbidden.size(); i++) {

                QueryNode childNode = new QueryNode(
                        new Term(
                                Field.ALL_FIELDS,
                                TermOperators.FULL_TEXT,
                                forbidden.get(i)
                        )
                );
                childNode.setNegate(true);
                node.getChildren().add(childNode);

            }
        }

        return node;
    }
}
