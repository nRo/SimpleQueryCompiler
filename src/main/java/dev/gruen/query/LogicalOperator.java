// SPDX-License-Identifier: MIT
// Copyright (c) 2018 Alexander Grün

package dev.gruen.query;

/**
 * Logic operators are used to concatenate multiple array (e.g. AND, OR, XOR,...)
 */
public class LogicalOperator extends Operator {

    /**
     * Creates a logic operator from a name and array of aliases
     *
     * @param name    operator name
     * @param aliases operator aliases
     */
    public LogicalOperator(String name, String... aliases) {
        super(name, aliases);
    }
}
