// SPDX-License-Identifier: MIT
// Copyright (c) 2018 Alexander Grün

package dev.gruen.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LogicalOperators {
    private LogicalOperators() {
        throw new IllegalStateException("utility class");
    }

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
     *
     * @return default operators
     */
    public static List<LogicalOperator> getDefaultOperations() {
        return new ArrayList<>(Arrays.asList(DEFAULT_OPERATIONS));
    }
}
