// SPDX-License-Identifier: MIT
// Copyright (c) 2018 Alexander Grün

package dev.gruen.query;

/**
 * Simple queries that create a string representation of the query
 */
public abstract class PrintQuery implements Query {

    /**
     * Returns the string representation of this query
     *
     * @return string representation
     */
    @Override
    public abstract String toString();
}