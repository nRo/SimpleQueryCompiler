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

import java.util.Arrays;
import java.util.Objects;

/**
 * Base class for {@link de.alexgruen.query.term.TermOperator} and {@link LogicalOperator}
 */
public class Operator {
    //Name of the operator
    private String name;

    //Aliases of the operator
    private String[] aliases;

    /**
     * Creates an operator from a name and array of aliases
     * @param name operator name
     * @param aliases operator aliases
     */
    public Operator(String name, String... aliases){
        this.name = name;
        this.aliases = aliases;
    }


    /**
     * Returns the aliases
     * @return aliases
     */
    public String[] getAliases() {
        return aliases;
    }

    /**
     * Returns the name
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * True if o equals this operator
     * @param o other operator
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
     * @return hashcode
     */
    @Override
    public int hashCode() {

        int result = Objects.hash(name);
        result = 31 * result + Arrays.hashCode(aliases);
        return result;
    }
}
