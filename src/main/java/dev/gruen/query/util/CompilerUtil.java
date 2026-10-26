// SPDX-License-Identifier: MIT
// Copyright (c) 2018 Alexander Grün

package dev.gruen.query.util;

import dev.gruen.query.compiler.QueryCompilerException;
import dev.gruen.query.compiler.parser.ParserUtil;
import dev.gruen.query.generated.QueryParser;
import dev.gruen.query.term.Field;
import dev.gruen.query.term.Value;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.lang.reflect.Array;
import java.util.List;


public class CompilerUtil {
    private CompilerUtil() {
        throw new IllegalStateException("utility class");
    }
    /**
     * Creates an array of an input type and specified length
     *
     * @param cl     class of array objects
     * @param length array length
     * @param <T>    type of array objects
     * @return array of type T[]
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] createArray(Class<T> cl, int length) {
        return (T[]) Array.newInstance(cl, length);
    }

    /**
     * Creates a Value object from a value context
     *
     * @param ctx value context
     * @return value object
     */
    public static Value createValue(QueryParser.ValueContext ctx) {
        return createValue(ctx.getText(), ctx.NULL(), ctx.NUMBER(), ctx.BOOLEAN_VALUE());
    }

    /**
     * Creates a Value object from a value context
     *
     * @param ctx value context
     * @return value object
     */
    public static Value createListValue(QueryParser.Value_listContext ctx) {
        List<Value> values = ctx.value().stream().map(CompilerUtil::createValue).toList();
        return new Value(values);
    }

    /**
     * Creates a Value object from a full text search context
     *
     * @param ctx text search context
     * @return value object
     */
    public static Value createValue(QueryParser.Full_search_valueContext ctx) {
        return createValue(ctx.getText(), ctx.NULL(), ctx.NUMBER(), ctx.BOOLEAN_VALUE());
    }

    /**
     * Creates a Value object from the nodes in a value context.
     * A <code>null</code> literal results in a {@link Value} of type {@link Value.Type#NULL}.
     *
     * @param text       node inner text
     * @param nullNode   null node
     * @param numberNode number node
     * @param boolNode   boolean node
     * @return value object
     */
    public static Value createValue(String text, TerminalNode nullNode, TerminalNode numberNode, TerminalNode boolNode) {
        if (nullNode != null) {
            return new Value(null);
        }
        if (numberNode != null) {
            return createNumberValue(numberNode);
        }
        if (boolNode != null) {
            return createBoolValue(boolNode);
        }
        return createTextValue(text);
    }

    private static Value createNumberValue(TerminalNode node) {
        String n = node.getText();
        try {
            if (n.contains(".")) {
                return new Value(ParserUtil.parse(Double.class, n));
            } else {
                return new Value(ParserUtil.parse(Long.class, n));
            }
        } catch (Exception e) {
            throw new QueryCompilerException(String.format("error parsing value '%s'", n));
        }
    }

    private static Value createBoolValue(TerminalNode node) {
        try {
            return new Value(
                    ParserUtil.parse(Boolean.class, node.getText())
            );
        } catch (Exception e) {
            throw new QueryCompilerException(String.format("error parsing value '%s'", node.getText()));
        }
    }

    private static Value createTextValue(String value) {
        if (StringUtil.isQuoted(value)) {
            value = StringUtil.stripQuotes(value);
            return new Value(value);
        }
        Number numberValue = StringUtil.toNumberIfValid(value);
        if (numberValue == null) {
            return new Value(value);
        }
        if (numberValue instanceof Double) {
            return new Value(numberValue);
        }
        if (numberValue instanceof Long) {
            return new Value(numberValue);
        }
        return new Value(value);
    }



    /**
     * Creates a field from a variable context
     *
     * @param ctx variable context
     * @return field object
     */
    public static Field createField(QueryParser.VariableContext ctx) {
        String fullPath = ctx.getText();
        String[] path = StringUtil.splitQuoted(fullPath, '.');
        return new Field(fullPath, path);
    }


}
