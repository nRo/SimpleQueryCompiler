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

package de.alexgruen.query.compiler;

import de.alexgruen.query.LogicalOperator;
import de.alexgruen.query.Query;
import de.alexgruen.query.creator.LogicCreator;
import de.alexgruen.query.creator.OperatorCreatorMap;
import de.alexgruen.query.creator.TermCreator;
import de.alexgruen.query.term.TermOperator;

import java.util.List;

/**
 * Contains all creators an information required by the query compiler
 * @param <T> type of compiled query object
 */
public class QueryContext<T extends Query> {
    private OperatorCreatorMap<TermOperator, TermCreator<T>> termCreators;
    private OperatorCreatorMap<LogicalOperator, LogicCreator<T>> logicCreators;
    private TermCreator<T> emptyCreator;
    private Class<T> cl;

    protected QueryContext(OperatorCreatorMap<TermOperator, TermCreator<T>> termCreators,
                           OperatorCreatorMap<LogicalOperator, LogicCreator<T>> logicCreators,
                           TermCreator<T> emptyCreator,
                           Class<T> cl) {
        this.emptyCreator = emptyCreator;
        this.termCreators = termCreators;
        this.logicCreators = logicCreators;
        this.cl = cl;
    }

    /**
     * Returns the {@link TermOperator} defined by the input name or alias
     * @param op input operator name or alias
     * @return term operator
     */
    public TermOperator getTermOperator(String op) {
        return termCreators.getOperator(op);
    }

    /**
     * Returns the {@link LogicalOperator} defined by the input name or alias
     * @param op input operator name or alias
     * @return logic operator
     */
    public LogicalOperator getLogicalOperator(String op) {
        return logicCreators.getOperator(op);
    }

    /**
     * Returns the {@link TermCreator} associated with the respective {@link TermOperator}.
     * Returns null if no creator is found
     * @param operator input operator
     * @return term creator or null if no creator found
     */
    public TermCreator<T> getTermCreator(TermOperator operator){
        return termCreators.getCreator(operator);
    }

    /**
     * Returns the {@link LogicCreator} associated with the respective {@link LogicalOperator}.
     * Returns null if no creator is found
     * @param operator input operator
     * @return logic creator or null if no creator found
     */
    public LogicCreator<T> getLogicCreator(LogicalOperator operator){
        return logicCreators.getCreator(operator);
    }

    /**
     * Return all names and aliases of available {@link TermOperator}
     * @return list of names and aliases
     */
    public List<String> getAllTermOperatorAliases(){
        return termCreators.getAllAliases();
    }

    /**
     * Returns the {@link TermCreator} assigned for empty terms (match all)
     * @return term creator
     */
    public TermCreator<T> getEmptyCreator() {
        return emptyCreator;
    }

    /**
     * Returns the class of resulting queries
     * @return class of resulting queries
     */
    public Class<T> getCl() {
        return cl;
    }
}
