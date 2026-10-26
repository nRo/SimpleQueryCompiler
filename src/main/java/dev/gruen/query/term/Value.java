// SPDX-License-Identifier: MIT
// Copyright (c) 2018 Alexander Grün

package dev.gruen.query.term;


import dev.gruen.query.compiler.QueryCompilerException;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Represents a value within a term (field operator value)
 */
public class Value {
    public enum Type {
        DOUBLE, LONG, STRING, BOOLEAN, PATTERN, NULL, LIST, FIELD
    }

    private Object value;
    private Type type;

    /**
     * Creates a new value object from an input object.
     * The type is reconized automatically, if the type is not supported a {@link QueryCompilerException} is thrown.
     *
     * @param value input object
     */
    public Value(Object value) {
        setValue(value);
    }


    /**
     * Creates a value that references a field (column), e.g. the right-hand side of <code>.a &gt; .b</code>.
     * The resulting value has the type {@link Type#FIELD}.
     *
     * @param field referenced field
     * @return value of type {@link Type#FIELD}
     */
    public static Value ofField(Field field) {
        if (field == null) {
            throw new QueryCompilerException("field must not be null");
        }
        return new Value(field);
    }

    /**
     * Sets the value object and updates the type.
     * If the type is not supported a {@link QueryCompilerException} is thrown.
     *
     * @param value input object
     */
    public void setValue(Object value) {
        this.value = value;
        updateType();
    }

    /**
     * Returns the value object
     *
     * @return value object
     */
    public Object getValue() {
        return value;
    }

    /**
     * Returns the type of the value object
     *
     * @return value type
     */
    public Type getType() {
        return type;
    }

    /**
     * Returns true if the value is null
     *
     * @return true if null
     */
    public boolean isNull() {
        return value == null;
    }

    /**
     * Returns true if the value is a number (Double or Long)
     *
     * @return true if number
     */
    public boolean isNumber() {
        return type == Type.DOUBLE || type == Type.LONG;
    }

    /**
     * Returns true if the value is a string
     *
     * @return true if string
     */
    public boolean isString() {
        return type == Type.STRING;
    }

    /**
     * Returns true if the value is boolean
     *
     * @return true if boolean
     */
    public boolean isBoolean() {
        return type == Type.BOOLEAN;
    }

    /**
     * Returns true if the value is a {@link Pattern}.
     *
     * @return true if pattern
     */
    public boolean isPattern() {
        return type == Type.PATTERN;
    }

    /**
     * Returns true if the value is a {@link List<Value>}.
     *
     * @return true if pattern
     */
    public boolean isList() {
        return type == Type.LIST;
    }

    /**
     * Returns true if the value is a reference to a {@link Field} (column).
     *
     * @return true if field
     */
    public boolean isField() {
        return type == Type.FIELD;
    }

    /**
     * Returns the value as Number. If the value is not a number a {@link QueryCompilerException} is thrown.
     *
     * @return double value
     */
    public Number getNumber() {
        if (type == Type.DOUBLE || type == Type.LONG) {
            return (Number) value;
        }
        throw new QueryCompilerException(String.format("value is not available as number (%s)", type.name()));
    }

    /**
     * Returns the value as Double. If the value is neither Long or Double a {@link QueryCompilerException} is thrown.
     *
     * @return double value
     */
    public Double getDouble() {
        if (type == Type.DOUBLE) {
            return (Double) value;
        }
        if (type == Type.LONG) {
            return ((Long) value).doubleValue();
        }
        throw new QueryCompilerException(String.format("value is not available as double (%s)", type.name()));
    }


    /**
     * Returns the value as Long. If the value is neither Long or Double a {@link QueryCompilerException} is thrown.
     *
     * @return long value
     */
    public Long getLong() {
        if (type == Type.LONG) {
            return (Long) value;
        }
        if (type == Type.DOUBLE) {
            return ((Double) value).longValue();
        }
        throw new QueryCompilerException(String.format("value is not available as long (%s)", type.name()));
    }

    /**
     * Returns the value as Boolean. If the value is not a Boolean a {@link QueryCompilerException} is thrown.
     *
     * @return boolean value
     */
    public Boolean getBoolean() {
        if (type == Type.BOOLEAN) {
            return (Boolean) value;
        }

        throw new QueryCompilerException(String.format("value is not available as boolean (%s)", type.name()));
    }

    /**
     * Returns the value as Pattern. If the value is not a Pattern a {@link QueryCompilerException} is thrown.
     *
     * @return Pattern value
     */
    public Pattern getPattern() {
        if (type == Type.PATTERN) {
            return (Pattern) value;
        }

        throw new QueryCompilerException(String.format("value is not available as pattern (%s)", type.name()));
    }

    /**
     * Returns the referenced {@link Field}. If the value is not a field a {@link QueryCompilerException} is thrown.
     *
     * @return field value
     */
    public Field getField() {
        if (type == Type.FIELD) {
            return (Field) value;
        }

        throw new QueryCompilerException(String.format("value is not available as field (%s)", type.name()));
    }

    /**
     * Returns the value as String. If the value is not a String the respective <code>toString()</code> value is returned.
     * For values of type {@link Type#FIELD} the field name is returned as it was written in the query
     * (e.g. <code>.'my col'</code>).
     *
     * @return String value
     */
    public String getString() {
        if (type == Type.NULL) {
            return null;
        }
        if (type == Type.FIELD) {
            return getField().getFullPath();
        }
        return value.toString();
    }

    /**
     * Returns the value as Double. If the value is neither Long or Double a {@link QueryCompilerException} is thrown.
     *
     * @return double value
     */
    @SuppressWarnings("unchecked")
    public List<Value> getList() {
        if (type == Type.LIST) {
            return (List<Value>) value;
        }
        throw new QueryCompilerException(String.format("value is not available as list (%s)", type.name()));
    }

    /**
     * Updates the type of the value.
     * Supported types are: Double, Long, String, Boolean, Integer, Float, Pattern, List and Field.
     */
    private void updateType() {
        if (value == null) {
            type = Type.NULL;
        }
        else if (value instanceof List) {
            ((List<?>) value).forEach((v -> {
                if (!(v instanceof Value)) {
                    throw new QueryCompilerException(String.format("unsupported list value type %s", v.getClass()));
                }
            }));
            type = Type.LIST;
        }
        else if (value instanceof Double) {
            type = Type.DOUBLE;
        } else if (value instanceof Long) {
            type = Type.LONG;
        } else if (value instanceof String) {
            type = Type.STRING;
        } else if (value instanceof Boolean) {
            type = Type.BOOLEAN;
        } else if (value instanceof Pattern) {
            type = Type.PATTERN;
        } else if (value instanceof Field) {
            type = Type.FIELD;
        } else if (value instanceof Integer intValue) {
            type = Type.LONG;
            value = intValue.longValue();
        } else if (value instanceof Float floatValue) {
            type = Type.DOUBLE;
            value = floatValue.doubleValue();
        } else {
            throw new QueryCompilerException(String.format("unknown value type %s", value.getClass()));
        }
    }

    /**
     * Returns the string representation of the value
     *
     * @return value string
     */
    @Override
    public String toString() {
        if (isNull()) {
            return null;
        }
        return isField() ? getString() : value.toString();
    }

    /**
     * Two values are equal if they have the same type and the same value.
     * Patterns are compared by their regular expression and flags, fields by their path.
     *
     * @param o other object
     * @return true if equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Value other = (Value) o;
        if (type != other.type) {
            return false;
        }
        if (type == Type.PATTERN) {
            Pattern a = (Pattern) value;
            Pattern b = (Pattern) other.value;
            return a.pattern().equals(b.pattern()) && a.flags() == b.flags();
        }
        return Objects.equals(value, other.value);
    }

    /**
     * Calculates the hashcode of this value, consistent with {@link #equals(Object)}
     *
     * @return hashcode
     */
    @Override
    public int hashCode() {
        if (type == Type.PATTERN) {
            Pattern p = (Pattern) value;
            return Objects.hash(type, p.pattern(), p.flags());
        }
        return Objects.hash(type, value);
    }
}
