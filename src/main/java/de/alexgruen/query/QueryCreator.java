package de.alexgruen.query;

import de.alexgruen.query.term.Field;
import de.alexgruen.query.term.Value;

import java.util.List;

public interface QueryCreator<T> {
    /**
     * Query creator for != operator
     *
     * @param queryNode current query node
     * @param field     input field
     * @param value     input value
     * @return created query object
     */
    T ne(QueryNode queryNode, Field field, Value value);

    /**
     * Query creator for == operator
     *
     * @param queryNode current query node
     * @param field     input field
     * @param value     input value
     * @return created query object
     */
    T eq(QueryNode queryNode, Field field, Value value);

    /**
     * Query creator for >= operator
     *
     * @param queryNode current query node
     * @param field     input field
     * @param value     input value
     * @return created query object
     */
    T ge(QueryNode queryNode, Field field, Value value);

    /**
     * Query creator for > operator
     *
     * @param queryNode current query node
     * @param field     input field
     * @param value     input value
     * @return created query object
     */
    T gt(QueryNode queryNode, Field field, Value value);

    /**
     * Query creator for < operator
     *
     * @param queryNode current query node
     * @param field     input field
     * @param value     input value
     * @return created query object
     */
    T lt(QueryNode queryNode, Field field, Value value);

    /**
     * Query creator for <= operator
     *
     * @param queryNode current query node
     * @param field     input field
     * @param value     input value
     * @return created query object
     */
    T le(QueryNode queryNode, Field field, Value value);

    /**
     * Query creator for the regex operator
     *
     * @param queryNode current query node
     * @param field     input field
     * @param value     input value
     * @return created query object
     */
    T regex(QueryNode queryNode, Field field, Value value);

    /**
     * Query creator for the text operator
     *
     * @param queryNode current query node
     * @param field     input field
     * @param value     input value
     * @return created query object
     */
    T text(QueryNode queryNode, Field field, Value value);


    /**
     * Query creator for the in operator
     *
     * @param queryNode current query node
     * @param field     input field
     * @param list     input value list
     * @return created query object
     */
    T in(QueryNode queryNode, Field field, Value list);

    /**
     * Query creator for the not in operator
     *
     * @param queryNode current query node
     * @param field     input field
     * @param list     input value list
     * @return created query object
     */
    T notIn(QueryNode queryNode, Field field, Value list);

    /**
     * Logic creator for the negation of a query
     *
     * @param queryNode current query node
     * @param v         input query
     * @return created query object
     */
    T not(QueryNode queryNode, T v);

    /**
     * Logic creator for the AND concatenation of queries
     *
     * @param queryNode current query node
     * @param v         input queries
     * @return created query object
     */
    T and(QueryNode queryNode, T[] v);

    /**
     * Logic creator for the OR concatenation of queries
     *
     * @param queryNode current query node
     * @param v         input queries
     * @return created query object
     */
    T or(QueryNode queryNode, T[] v);

    /**
     * Logic creator for the XOR concatenation of queries
     *
     * @param queryNode current query node
     * @param v         input queries
     * @return created query object
     */
    T xor(QueryNode queryNode, T[] v);

    /**
     * Logic creator for the NOR concatenation of queries
     *
     * @param queryNode current query node
     * @param v         input queries
     * @return created query object
     */
    T nor(QueryNode queryNode, T[] v);

    /**
     * Term creator for fulltext search queries
     *
     * @param value input value
     * @return created query object
     */
    T fullSearch(Value value);

    /**
     * Term creator for empty queries (match all)
     *
     * @return created query object
     */
    T empty();

}
