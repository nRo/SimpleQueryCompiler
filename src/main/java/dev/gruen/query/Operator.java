// SPDX-License-Identifier: MIT
// Copyright (c) 2018 Alexander Grün

package dev.gruen.query;

import java.util.Arrays;
import java.util.Objects;

/**
 * Base class for {@link dev.gruen.query.term.TermOperator} and {@link LogicalOperator}
 */
public class Operator {
    //Name of the operator
    private final String name;

    //Aliases of the operator
    private final String[] aliases;

    /**
     * Creates an operator from a name and array of aliases
     *
     * @param name    operator name
     * @param aliases operator aliases
     */
    public Operator(String name, String... aliases) {
        this.name = name;
        this.aliases = aliases;
    }


    /**
     * Returns the aliases
     *
     * @return aliases
     */
    public String[] getAliases() {
        return aliases;
    }

    /**
     * Returns the name
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * True if o equals this operator
     *
     * @param o another operator
     * @return true if equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Operator operator = (Operator) o;
        return Objects.equals(name, operator.name) &&
               Arrays.equals(aliases, operator.aliases);
    }

    /**
     * Calculates the hashcode of this operator object
     *
     * @return hashcode
     */
    @Override
    public int hashCode() {

        int result = Objects.hash(name);
        result = 31 * result + Arrays.hashCode(aliases);
        return result;
    }
}
