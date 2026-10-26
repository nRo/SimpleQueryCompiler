// SPDX-License-Identifier: MIT
// Copyright (c) 2018 Alexander Grün

package dev.gruen.query.compiler;

import dev.gruen.query.LogicalOperator;
import dev.gruen.query.Query;
import dev.gruen.query.creator.LogicCreator;
import dev.gruen.query.creator.OperatorCreatorMap;
import dev.gruen.query.creator.TermCreator;
import dev.gruen.query.term.TermOperator;

import java.util.List;

/**
 * Contains all creators an information required by the query compiler
 *
 * @param <T> type of compiled query object
 */
public class QueryContext<T extends Query> {
    private final OperatorCreatorMap<TermOperator, TermCreator<T>> termCreators;
    private final OperatorCreatorMap<LogicalOperator, LogicCreator<T>> logicCreators;
    private final TermCreator<T> emptyCreator;
    private final Class<T> cl;
    private final boolean columnValues;

    protected QueryContext(OperatorCreatorMap<TermOperator, TermCreator<T>> termCreators,
                           OperatorCreatorMap<LogicalOperator, LogicCreator<T>> logicCreators,
                           TermCreator<T> emptyCreator,
                           Class<T> cl) {
        this(termCreators, logicCreators, emptyCreator, cl, false);
    }

    protected QueryContext(OperatorCreatorMap<TermOperator, TermCreator<T>> termCreators,
                           OperatorCreatorMap<LogicalOperator, LogicCreator<T>> logicCreators,
                           TermCreator<T> emptyCreator,
                           Class<T> cl,
                           boolean columnValues) {
        this.emptyCreator = emptyCreator;
        this.termCreators = termCreators;
        this.logicCreators = logicCreators;
        this.cl = cl;
        this.columnValues = columnValues;
    }

    /**
     * Returns the {@link TermOperator} defined by the input name or alias
     *
     * @param op input operator name or alias
     * @return term operator
     */
    public TermOperator getTermOperator(String op) {
        return termCreators.getOperator(op);
    }

    /**
     * Returns the {@link LogicalOperator} defined by the input name or alias
     *
     * @param op input operator name or alias
     * @return logic operator
     */
    public LogicalOperator getLogicalOperator(String op) {
        return logicCreators.getOperator(op);
    }

    /**
     * Returns the {@link TermCreator} associated with the respective {@link TermOperator}.
     * Returns null if no creator is found
     *
     * @param operator input operator
     * @return term creator or null if no creator found
     */
    public TermCreator<T> getTermCreator(TermOperator operator) {
        return termCreators.getCreator(operator);
    }

    /**
     * Returns the {@link LogicCreator} associated with the respective {@link LogicalOperator}.
     * Returns null if no creator is found
     *
     * @param operator input operator
     * @return logic creator or null if no creator found
     */
    public LogicCreator<T> getLogicCreator(LogicalOperator operator) {
        return logicCreators.getCreator(operator);
    }

    /**
     * Return all names and aliases of available {@link TermOperator}
     *
     * @return list of names and aliases
     */
    public List<String> getAllTermOperatorAliases() {
        return termCreators.getAllAliases();
    }

    /**
     * Returns the {@link TermCreator} assigned for empty terms (match all)
     *
     * @return term creator
     */
    public TermCreator<T> getEmptyCreator() {
        return emptyCreator;
    }

    /**
     * Returns the class of resulting queries
     *
     * @return class of resulting queries
     */
    public Class<T> getCl() {
        return cl;
    }

    /**
     * Returns true if column references are allowed as right-hand value of a term (<code>.a &gt; .b</code>).
     * Values of such terms have the type {@link dev.gruen.query.term.Value.Type#FIELD}.
     *
     * @return true if column values are enabled
     */
    public boolean isColumnValues() {
        return columnValues;
    }
}
