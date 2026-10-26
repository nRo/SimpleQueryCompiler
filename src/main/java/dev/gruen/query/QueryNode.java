// SPDX-License-Identifier: MIT
// Copyright (c) 2018 Alexander Grün

package dev.gruen.query;

import dev.gruen.query.term.Term;

import java.util.*;

public class QueryNode {
    private boolean negate;
    private LogicalOperator operator;
    private Term term;
    private List<QueryNode> children = new ArrayList<>();
    private final Map<String, Object> attributes = new HashMap<>();

    public QueryNode() {
    }

    /**
     * Creates a node form all possible variables
     *
     * @param negate   negate
     * @param operator operator
     * @param term     term
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
     *
     * @param term term
     */
    public QueryNode(Term term) {
        this(false, null, term, new ArrayList<>(0));
    }

    /**
     * Creates a new node from an operator and a list of children
     *
     * @param operator operator
     * @param children children
     */
    public QueryNode(LogicalOperator operator, QueryNode... children) {
        this(false, operator, null, Arrays.asList(children));
    }

    /**
     * Returns the attributes map
     *
     * @return attributes map
     */
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    /**
     * Returns an attribute value or null if no attribute with the specified name is found.
     *
     * @param name attribute name
     * @return value
     */
    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    /**
     * Sets the value of an attribute. Returns the previously set value or null
     *
     * @param name  attribute name
     * @param value attribute value
     * @return previous value or null
     */
    public Object setAttribute(String name, Object value) {
        return attributes.put(name, value);
    }

    /**
     * Returns <code>true</code> if this node is negated
     *
     * @return true if negated
     */
    public boolean isNegate() {
        return negate;
    }


    /**
     * Negates this node
     *
     * @param negate negate
     */
    public void setNegate(boolean negate) {
        this.negate = negate;
    }

    /**
     * Returns the operator {@link LogicalOperator} of this node
     *
     * @return operator
     */
    public LogicalOperator getOperator() {
        return operator;
    }

    /**
     * Sets the operator {@link LogicalOperator} of this node
     *
     * @param operator oprator
     */
    public void setOperator(LogicalOperator operator) {
        this.operator = operator;
    }


    /**
     * Returns the term {@link Term} for this node
     *
     * @return term
     */
    public Term getTerm() {
        return term;
    }


    /**
     * Sets the term {@link Term} for this node
     *
     * @param term term
     */
    public void setTerm(Term term) {
        this.term = term;
    }


    /**
     * Returns all child nodes
     *
     * @return all child nodes
     */
    public List<QueryNode> getChildren() {
        return children;
    }


    /**
     * Sets the child nodes
     *
     * @param children child nodes
     */
    public void setChildren(List<QueryNode> children) {
        this.children = children;
    }

    /**
     * Returns the label (in the query tree) for this node.
     *
     * @return label of the node
     */
    public String getLabel() {
        if (term == null) {
            return operator.getName();
        }
        return term.toString();
    }

}
