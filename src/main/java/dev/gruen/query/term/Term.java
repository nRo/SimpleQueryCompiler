// SPDX-License-Identifier: MIT
// Copyright (c) 2018 Alexander Grün

package dev.gruen.query.term;

/**
 * Represents a term within the query (field operator value)
 */
public class Term {
    private Field field;
    private TermOperator operator;
    private Value value;

    /**
     * Creates a new term object from a field, operator and value
     *
     * @param field        field
     * @param termOperator operator
     * @param value        value
     */
    public Term(Field field, TermOperator termOperator, Value value) {
        this.field = field;
        this.operator = termOperator;
        this.value = value;
    }

    /**
     * Returns the field from this term
     *
     * @return field
     */
    public Field getField() {
        return field;
    }

    /**
     * Sets the field ({@link Field})
     *
     * @param field field
     */
    public void setField(Field field) {
        this.field = field;
    }

    /**
     * Returns the operator ({@link TermOperator}) from this term
     *
     * @return operator
     */
    public TermOperator getOperator() {
        return operator;
    }

    /**
     * Sets the operator
     *
     * @param operator operator
     */
    public void setOperator(TermOperator operator) {
        this.operator = operator;
    }

    /**
     * Returns the value ({@link Value}) from this term
     *
     * @return value
     */
    public Value getValue() {
        return value;
    }

    /**
     * Sets the value
     *
     * @param value value
     */
    public void setValue(Value value) {
        this.value = value;
    }

    /**
     * Returns a string representation of this term (field operation value)
     *
     * @return string representation
     */
    @Override
    public String toString() {
        return String.format("(%s %s %s)",
                getField().toString(),
                getOperator().getName(),
                getValue().toString());
    }
}
