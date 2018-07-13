# Simple Query Compiler
![travis](https://travis-ci.org/nRo/SimpleQueryCompiler.svg?branch=master)
[![codecov](https://codecov.io/gh/nRo/SimpleQueryCompiler/branch/master/graph/badge.svg)](https://codecov.io/gh/nRo/SimpleQueryCompiler)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/a03214f7198f4461ab341adecb75e0da)](https://www.codacy.com/app/nRo/DataFrame?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=nRo/DataFrame&amp;utm_campaign=Badge_Grade)

A Java library to compile and parse simple filter queries.

The following query formats can be parsed:
```
// multiple terms with AND and OR operator
(x > 0 && !(y < 1 || z == 'test')) 
```
```
//full text search
"text search" test -example
```

Default term operators:
```
==, !=, <, <=, >, >=, text, regex
```

Logical operators:
```
AND, OR, XOR, NOR
```

Install
-------
Add this to you pom.xml

```xml
<repositories>
    <repository>
        <id>alexgruen-snapshot</id>
        <name>alexgruen-snapshot</name>
        <url>http://maven.alexgruen.de/artifactory/public-snapshot</url>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
    </repository>
    <repository>
        <id>alexgruen-release</id>
        <name>alexgruen-release</name>
        <url>http://maven.alexgruen.de/artifactory/public-release</url>
        <snapshots>
            <enabled>false</enabled>
        </snapshots>
    </repository>
    ...
</repositories>
...
<dependencies>
...
    <dependency>
        <groupId>de.alexgruen</groupId>
        <artifactId>querycompiler</artifactId>
        <version>0.3-SNAPSHOT</version>
    </dependency>
...
</dependencies>
```

Build
-----
To build the library from sources:

1) Clone github repository

    $ git clone https://github.com/nRo/SimpleQueryCompiler.git

2) Change to the created folder and run `mvn install`

    $ cd SimpleQueryCompiler
    
    $ mvn install

3) Include it by adding the following to your project's `pom.xml`:

```xml
<dependencies>
...
    <dependency>
        <groupId>de.alexgruen</groupId>
        <artifactId>querycompiler</artifactId>
        <version>0.3-SNAPSHOT</version>
    </dependency>
...
</dependencies>
```

Usage
-----
This library works by defining by building a QueryCompiler for the desired output type.
Building a QueryCompiler requires the definition of Creators for the supported 
term operators (==, !=, <, <=, ...) and logical operators (AND, OR,...).
The Creators are responsible to create the target object, for example SQL queries, elasticsearch queries,...

There are two ways to create a QueryCompiler.
For the following examples be specify example ElasticSearchQuery as target class:
```java
public abstract class ElasticSearchQuery implements Query{
    //Every query must create an ElasticSearch QueryBuilder
    public abstract QueryBuilder create();
    ...
}
```
If a QueryCompiler is built, input strings can be parsed with:
```java
QueryCompiler<ElasticSearchQuery> esCompiler = ...
ElasticSearchQuery esQuery = esCompiler.compile("(x > 1 && y < 1)");
```
##### Override DefaultCreator
DefaultCreator is an abstract class that contains functions for all standard operations.
If such a method is not overridden, an error is thrown if the input query string contains the respective operator.
```java
public class ElasticSearchQueryCreator extends DefaultCreator<ElasticSearchQuery> {
    //override eq(field,value) to support == operator in input queries
    //text search is implemented by overriding the fullSearch(Value value) method
     @Override
        public ElasticSearchQuery eq(Field field, Value value) {
            return new ElasticSearchQuery() {
                @Override
                public QueryBuilder create() {
                    return QueryBuilders.termQuery(field.toString(), value.getValue());
                }
            };
        }

    
    //override other term operator methods: ne (!=), le(>=), lt(>),...
    
    ...
    
    //override and(query....) to support AND concatenation of terms
    @Override
        public ElasticSearchQuery and(ElasticSearchQuery... queries) {
            return new ElasticSearchQuery() {
                @Override
                public QueryBuilder create() {
                    BoolQueryBuilder boolQueryBuilder = boolQuery();
                    for(ElasticSearchQuery q : queries){
                        boolQueryBuilder.must(q.create());
                    }
    
                    return boolQueryBuilder;
                }
            };
        }
        //override other logic operator methods: OR, XOR, NOR,...
}
```
The compiler can then be created using the following Builder methods:
```java
QueryCompiler<ElasticSearchQuery> esCompiler
            = QueryCompiler.create(ElasticSearchQuery.class)
            .withDefaultCreator(new ElasticSearchQueryCreator())
            .build();
```

##### Define creators using QueryCompilerBuilder

```java
  QueryCompiler<ElasticSearchQuery> compiler = QueryCompiler
        .create(ElasticSearchQuery.class)
        .withTermCreator(
               TermOperators.EQ,
               (field,value) -> new ElasticSearchQuery() {
                   @Override
                   public String create() {
                    return QueryBuilders.termQuery(field.toString(), value.getValue());
                   }
               }
       )
       .withANDCreator(
               (queries) -> new ElasticSearchQuery() {
                   BoolQueryBuilder boolQueryBuilder = boolQuery();
                   for(ElasticSearchQuery q : queries){
                       boolQueryBuilder.must(q.create());
                   }

                   return boolQueryBuilder;
               }
       )
    ).build()
```


#### Custom term operators

Custom term operators can be added to the QueryCompilerBuilder.
In this example we add a PHRASE operator that creates an matchPhrase query for elasticsearch.
User can than input queries like:;
```
x PHRASE 'test phrase' || y == 'test'
```

```java
  QueryCompiler<ElasticSearchQuery> compiler = QueryCompiler
        .create(ElasticSearchQuery.class)
        .withTermCreator(
               .withTermCreator(
                   new TermOperator("PHRASE"),
                   (f,v) -> new ElasticSearchQuery() {
                      @Override
                     public String create() {
                      return QueryBuilders.matchPhraseQuery(field.toString(), value.getValue());
                     }
                   }
               )
       )
    )
```

#### Print queries

Queries are first compiled to a tree before being converted to the target object.
This tree can be printed

```java
   QueryTree tree = compiler.compileTree(
           "(x > 0 && y < 1 || (u == 2 && (h != 1 || z < 2))) || z == 2");
   String str = QueryPrinter.DEFAULT.toString(tree.getRoot());
   /**
   * ──┐ OR
       ├──┐ AND
       │  ├── (x > 0)
       │  └── (y < 1)
       ├──┐ AND
       │  ├── (u == 2)
       │  └──┐ OR
       │     ├── (h != 1)
       │     └── (z < 2)
       └── (z == 2)
   **/
   //The tree can then be converted to the target object
   ElasticSearchQuery esQuery = compiler.compile(tree);
```