// SPDX-License-Identifier: MIT
// Copyright (c) 2018 Alexander Grün

package dev.gruen.query.optimization;

import dev.gruen.query.QueryTree;

public interface QueryOptimization {
    /**
     * Optimizes an input query tree.
     *
     * @param queryTree input query tree
     */
    void apply(QueryTree queryTree);
}
