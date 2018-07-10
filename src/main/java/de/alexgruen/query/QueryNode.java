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

package de.alexgruen.query;

import de.alexgruen.query.term.Term;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QueryNode {
    private boolean negate;
    private LogicalOperator operator;
    private Term term;
    private List<QueryNode> children = new ArrayList<>();


    public QueryNode() {
    }

    /**
     * Creates a node form all possible variables
     * @param negate negate
     * @param operator operator
     * @param term term
     * @param children children
     */
    public QueryNode(boolean negate, LogicalOperator operator, Term term, List<QueryNode> children) {
        this.negate = negate;
        this.operator = operator;
        this.term = term;
        this.children = children;
    }

    /**
     * Creates a new leaf node from a term
     * @param term term
     */
    public QueryNode(Term term) {
        this(false, null, term, new ArrayList<>(0));
    }

    /**
     * Creates a new node from an operator and a list of children
     * @param operator operator
     * @param children children
     */
    public QueryNode(LogicalOperator operator, QueryNode... children) {
        this(false, operator, null, Arrays.asList(children));
    }


    /**
     * Returns <tt>true</tt> if this node is negated
     * @return true if negated
     */
    public boolean isNegate() {
        return negate;
    }


    /**
     * Negates this node
     * @param negate negate
     */
    public void setNegate(boolean negate) {
        this.negate = negate;
    }

    /**
     * Returns the operator {@link LogicalOperator} of this node
     * @return operator
     */
    public LogicalOperator getOperator() {
        return operator;
    }

    /**
     * Sets the operator {@link LogicalOperator} of this node
     * @param operator oprator
     */
    public void setOperator(LogicalOperator operator) {
        this.operator = operator;
    }


    /**
     * Returns the term {@link Term} for this node
     * @return term
     */
    public Term getTerm() {
        return term;
    }


    /**
     * Sets the term {@link Term} for this node
     * @param term term
     */
    public void setTerm(Term term) {
        this.term = term;
    }


    /**
     * Returns all child nodes
     * @return all child nodes
     */
    public List<QueryNode> getChildren() {
        return children;
    }


    /**
     * Sets the child nodes
     * @param children child nodes
     */
    public void setChildren(List<QueryNode> children) {
        this.children = children;
    }

    /**
     * Returns the label (in the query tree) for this node.
     * @return label of the node
     */
    public String getLabel() {
        if(term == null){
            return operator.getName();
        }
        return term.toString();
    }

}
