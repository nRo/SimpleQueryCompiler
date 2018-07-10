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

package de.alexgruen.query.creator;

import de.alexgruen.query.Operator;
import de.alexgruen.query.compiler.QueryCompilerException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Map that links Operators to Creators.
 * Can be used for {@link de.alexgruen.query.term.TermOperator} and {@link de.alexgruen.query.LogicalOperator}
 * @param <O> operator type ({@link de.alexgruen.query.term.TermOperator}, {@link de.alexgruen.query.LogicalOperator})
 * @param <H> creator type ({@link TermCreator}, {@link LogicCreator})
 */
public class OperatorCreatorMap<O extends Operator, H extends OperatorCreator> {
    private Map<String, O> operatorMap = new HashMap<>();
    private Map<O, H> operatorCreatorMap = new HashMap<>();

    /**
     * Returns all names and aliases of the operators saved in this map
     * @return list of names and aliases
     */
    public List<String> getAllAliases(){
        return new ArrayList<>(operatorMap.keySet());
    }

    /**
     * Internal function that adds an operator
     * @param op added operator
     */
    private void add(O op) {
        addFieldOperationAlias(op.getName(), op);
        for (String alias : op.getAliases()) {
            addFieldOperationAlias(alias, op);
        }
    }


    /**
     * Adds an operator and the corresponding creator to this map
     * @param operator operator
     * @param creator creator
     */
    public void add(O operator, H creator){
        add(operator);
        operatorCreatorMap.put(operator,creator);
    }

    /**
     * Returns the creator associated with the input operator.
     * null is returned if no creator is found
     * @param operator input operator
     * @return creator
     */
    public H getCreator(O operator){
        return operatorCreatorMap.get(operator);
    }

    /**
     * Returns the creator associated with the input operator specified by its name or an alias.
     * null is returned if no operator is found
     * @param opName
     * @return
     */
    public H getCreator(String opName){
        O operator = getOperator(opName);
        if(operator != null){
            return getCreator(operator);
        }
        return null;
    }

    /**
     * Returns the operator for an input name or alias
     * @param name input name or alias
     * @return operator
     */
    public O getOperator(String name){
        return operatorMap.get(name);
    }

    /**
     * Internal function that adds an alias for an operator.
     * If another operator with this alias already exists, a {@link QueryCompilerException} is thrown
     * @param alias alias
     * @param op operator
     */
    private void addFieldOperationAlias(String alias, O op) {
        if (operatorMap.containsKey(alias)) {
            throw new QueryCompilerException(
                    String.format("field operation alias '%s' already exists", alias));
        }
        operatorMap.put(alias, op);
    }
}
