// SPDX-License-Identifier: MIT
// Copyright (c) 2018 Alexander Grün

package dev.gruen.query.term;


import dev.gruen.query.Operator;

/**
 * Represents a term operator (==, !=, >, ...)
 */
public class TermOperator extends Operator {

    /**
     * Creates a new Term operator from a name and array of aliases
     *
     * @param name    term name
     * @param aliases term aliases
     */
    public TermOperator(String name, String... aliases) {
        super(name, aliases);
    }


}
