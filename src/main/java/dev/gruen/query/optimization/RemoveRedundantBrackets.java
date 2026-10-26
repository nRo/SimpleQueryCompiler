// SPDX-License-Identifier: MIT
// Copyright (c) 2018 Alexander Grün

package dev.gruen.query.optimization;

import dev.gruen.query.QueryNode;
import dev.gruen.query.QueryTree;

import java.util.ArrayList;
import java.util.List;

public class RemoveRedundantBrackets implements QueryOptimization {
    /**
     * Removes redundant brackets from a query tree.
     * <pre>{@code
     * ── AND
     * ├── (x > 1)
     * └── AND
     *     ├── (y > 2)
     *     └── (z < 3)
     * }</pre>
     * becomes
     * <pre>{@code
     * ── AND
     * ├── (x > 1)
     * ├── (y > 2)
     * └── (z < 3)
     * }</pre>
     *
     * @param queryTree input query tree
     */
    @Override
    public void apply(QueryTree queryTree) {
        compressRec(queryTree.getRoot());
    }

    /**
     * Recursive function used to remove all redundant brackets
     *
     * @param parent parent node
     */
    private void compressRec(QueryNode parent) {
        List<QueryNode> children = new ArrayList<>(parent.getChildren());
        int c = -1;
        for (QueryNode child : children) {
            c++;
            if (child.getOperator() == null) {
                continue;
            }
            compressRec(child);
            if (child.getOperator().equals(parent.getOperator()) && !child.isNegate() && !parent.isNegate()) {
                parent.getChildren().remove(child);
                for (int i = 0; i < child.getChildren().size(); i++) {

                    parent.getChildren().add(c + i, child.getChildren().get(i));
                }
                c += child.getChildren().size() - 1;
            }
        }
    }
}
