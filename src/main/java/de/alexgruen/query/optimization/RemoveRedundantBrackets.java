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

package de.alexgruen.query.optimization;

import de.alexgruen.query.QueryNode;
import de.alexgruen.query.QueryTree;

import java.util.ArrayList;
import java.util.List;

public class RemoveRedundantBrackets implements QueryOptimization {


    /**
     * Removes redundant brackets from a query tree.
     *
     * ── AND
     *    ├── (x > 1)
     *    └── AND
     *        ├── (y > 2)
     *        └── (z < 3)
     * ---->
     *
     * ── AND
     *    ├── (x > 1)
     *    ├── (y > 2)
     *    └── (z < 3)
     * @param queryTree input query tree
     */
    @Override
    public void apply(QueryTree queryTree) {
        compressRec(queryTree.getRoot());
    }

    /**
     * Recursive function used to remove all redundant brackets
     * @param parent parent node
     */
    private void compressRec(QueryNode parent){
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
