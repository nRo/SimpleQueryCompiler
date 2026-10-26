// SPDX-License-Identifier: MIT
// Copyright (c) 2018 Alexander Grün

package dev.gruen.query.creator;

import dev.gruen.query.Operator;
import dev.gruen.query.compiler.QueryCompilerException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Map that links Operators to Creators.
 * Can be used for {@link dev.gruen.query.term.TermOperator} and {@link dev.gruen.query.LogicalOperator}
 *
 * @param <O> operator type ({@link dev.gruen.query.term.TermOperator}, {@link dev.gruen.query.LogicalOperator})
 * @param <H> creator type ({@link TermCreator}, {@link LogicCreator})
 */
public class OperatorCreatorMap<O extends Operator, H extends OperatorCreator> {
    private final Map<String, O> operatorMap = new HashMap<>();
    private final Map<O, H> creatorMap = new HashMap<>();

    /**
     * Returns all names and aliases of the operators saved in this map
     *
     * @return list of names and aliases
     */
    public List<String> getAllAliases() {
        return new ArrayList<>(operatorMap.keySet());
    }

    /**
     * Internal function that adds an operator
     *
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
     *
     * @param operator operator
     * @param creator  creator
     */
    public void add(O operator, H creator) {
        add(operator);
        creatorMap.put(operator, creator);
    }

    /**
     * Returns the creator associated with the input operator.
     * Null is returned if no creator is found
     *
     * @param operator input operator
     * @return creator
     */
    public H getCreator(O operator) {
        return creatorMap.get(operator);
    }

    /**
     * Returns the creator associated with the input operator specified by its name or an alias.
     * null is returned if no operator is found
     *
     */
    public H getCreator(String opName) {
        O operator = getOperator(opName);
        if (operator != null) {
            return getCreator(operator);
        }
        return null;
    }

    /**
     * Returns the operator for an input name or alias
     *
     * @param name input name or alias
     * @return operator
     */
    public O getOperator(String name) {
        return operatorMap.get(name);
    }

    /**
     * Internal function that adds an alias for an operator.
     * If another operator with this alias already exists, a {@link QueryCompilerException} is thrown
     *
     * @param alias alias
     * @param op    operator
     */
    private void addFieldOperationAlias(String alias, O op) {
        if (operatorMap.containsKey(alias)) {
            throw new QueryCompilerException(
                    String.format("field operation alias '%s' already exists", alias));
        }
        operatorMap.put(alias, op);
    }
}
