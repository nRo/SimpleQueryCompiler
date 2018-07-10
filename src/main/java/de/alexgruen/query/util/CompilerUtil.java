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

package de.alexgruen.query.util;

import de.alexgruen.query.compiler.QueryCompilerException;
import de.alexgruen.query.compiler.parser.ParserUtil;
import de.alexgruen.query.generated.QueryParser;
import de.alexgruen.query.term.Field;
import de.alexgruen.query.term.Value;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.lang.reflect.Array;


public class CompilerUtil {

    /**
     * Creates an array of an input type and specified length
     * @param cl class of array objects
     * @param length array length
     * @param <T> type of array objects
     * @return array of type T[]
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] createArray(Class<T> cl, int length){
        return (T[]) Array.newInstance(cl, length);
    }

    /**
     * Creates a Value object from a value context
     * @param ctx value context
     * @return value object
     */
    public static Value createValue(QueryParser.ValueContext ctx) {
        return createValue(ctx.getText(), ctx.NULL(), ctx.NUMBER(), ctx.BOOLEAN_VALUE());
    }

    /**
     * Creates a Value object from a full text search context
     * @param ctx text search context
     * @return value object
     */
    public static Value createValue(QueryParser.Full_search_valueContext ctx) {
        return createValue(ctx.getText(), ctx.NULL(), ctx.NUMBER(), ctx.BOOLEAN_VALUE());
    }

    /**
     * Creates a Value object from the nodes in a value context
     * @param text node inner text
     * @param NULL null node
     * @param NUMBER number node
     * @param BOOL boolean node
     * @return value object
     */
    public static Value createValue(String text, TerminalNode NULL, TerminalNode NUMBER, TerminalNode BOOL) {
        if (NULL != null) {
            return null;
        }
        if (NUMBER != null) {
            String n = NUMBER.getText();
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
        if (BOOL != null) {
            try {
                return new Value(
                        ParserUtil.parse(Boolean.class, BOOL.getText())
                );
            } catch (Exception e) {
                throw new QueryCompilerException(String.format("error parsing value '%s'", BOOL.getText()));
            }
        }
        String value = text;
        if (StringUtil.isQuoted(value)) {
            value = StringUtil.stripQuotes(value);
            return new Value(value);
        }
        Number numberValue = StringUtil.toNumberIfValid(value);
        if (numberValue != null) {
            if (numberValue instanceof Double) {
                return new Value(numberValue);
            }
            if (numberValue instanceof Long) {
                return new Value(numberValue);
            }
        }
        return new Value(value);
    }


    /**
     * Creates a field from a variable context
     * @param ctx variable context
     * @return field object
     */
    public static Field createField(QueryParser.VariableContext ctx) {
        String fullPath = ctx.getText();
        String[] path = StringUtil.splitQuoted(fullPath, '.');
        return new Field(fullPath, path);
    }


}
