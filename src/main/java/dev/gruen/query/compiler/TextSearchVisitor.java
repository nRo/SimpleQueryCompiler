// SPDX-License-Identifier: MIT
// Copyright (c) 2018 Alexander Grün

package dev.gruen.query.compiler;


import dev.gruen.query.LogicalOperators;
import dev.gruen.query.QueryNode;
import dev.gruen.query.generated.QueryBaseVisitor;
import dev.gruen.query.generated.QueryParser;
import dev.gruen.query.term.Field;
import dev.gruen.query.term.Term;
import dev.gruen.query.term.TermOperators;
import dev.gruen.query.term.Value;
import dev.gruen.query.util.CompilerUtil;

import java.util.ArrayList;
import java.util.List;

public class TextSearchVisitor extends QueryBaseVisitor<QueryNode> {

    /**
     * Creates the root query node from a full-text search context
     * A text search query tree only consists of a root node and leaves.
     * All children are joined using the AND operation.
     *
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


        if (!required.isEmpty()) {
            for (Value value : required) {
                node.getChildren().add(
                        new QueryNode(
                                new Term(
                                        Field.ALL_FIELDS,
                                        TermOperators.FULL_TEXT,
                                        value
                                )
                        )
                );
            }
        }

        if (!forbidden.isEmpty()) {
            for (Value value : forbidden) {
                QueryNode childNode = new QueryNode(
                        new Term(
                                Field.ALL_FIELDS,
                                TermOperators.FULL_TEXT,
                                value
                        )
                );
                childNode.setNegate(true);
                node.getChildren().add(childNode);

            }
        }

        return node;
    }
}
