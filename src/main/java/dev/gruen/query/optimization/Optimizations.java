// SPDX-License-Identifier: MIT
// Copyright (c) 2018 Alexander Grün

package dev.gruen.query.optimization;

public class Optimizations {
    private Optimizations() {
        throw new IllegalStateException("utility class");
    }

    /**
     * Removes redundant brackets from a query tree.
     */
    public static final QueryOptimization RemoveRedundantBrackets = new RemoveRedundantBrackets();
}
