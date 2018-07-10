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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LogicalOperators {
    /**
     * AND operator
     */
    public static final LogicalOperator AND = new LogicalOperator("AND", "and", "&", "&&");

    /**
     * OR operator
     */
    public static final LogicalOperator OR = new LogicalOperator("OR", "or", "|", "||");

    /**
     * XOR operator
     */
    public static final LogicalOperator XOR = new LogicalOperator("XOR", "xor");

    /**
     * NOR operator
     */
    public static final LogicalOperator NOR = new LogicalOperator("NOR", "nor");

    /**
     * NOT operator
     */
    public static final LogicalOperator NOT = new LogicalOperator("NOT", "!");


    /**
     * Default operators
     */
    private static final LogicalOperator[] DEFAULT_OPERATIONS =
            new LogicalOperator[]{
                    AND,
                    OR,
                    XOR,
                    NOR,
                    NOT
            };


    /**
     * Returns a list with the default operators
     * @return default operators
     */
    public static List<LogicalOperator> getDefaultOperations() {
        return new ArrayList<>(Arrays.asList(DEFAULT_OPERATIONS));
    }
}
