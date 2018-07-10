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

package de.alexgruen.query.term;

/**
 * Represents a term within the query (field operator value)
 */
public class Term {
    private Field field;
    private TermOperator operator;
    private Value value;

    /**
     * Creates a new term object from a field, operator and value
     * @param field field
     * @param termOperator operator
     * @param value value
     */
    public Term(Field field, TermOperator termOperator, Value value) {
        this.field = field;
        this.operator = termOperator;
        this.value = value;
    }

    /**
     * Returns the field from this term
     * @return field
     */
    public Field getField() {
        return field;
    }

    /**
     * Sets the field ({@link Field}
     * @param field field
     */
    public void setField(Field field) {
        this.field = field;
    }

    /**
     * Returns the operator ({@link TermOperator} from this term
     * @return operator
     */
    public TermOperator getOperator() {
        return operator;
    }

    /**
     * Sets the operator
     * @param operator
     */
    public void setOperator(TermOperator operator) {
        this.operator = operator;
    }

    /**
     * Returns the value ({@link Value} from this term
     * @return value
     */
    public Value getValue() {
        return value;
    }

    /**
     * Sets the value
     * @param value value
     */
    public void setValue(Value value) {
        this.value = value;
    }

    /**
     * Returns a string representation of this term (field operation value)
     * @return
     */
    @Override
    public String toString() {
        return String.format("(%s %s %s)",
                getField().toString(),
                getOperator().getName(),
                getValue().toString());
    }
}
