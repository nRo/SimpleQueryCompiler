# Query Compiler

[![Build](https://github.com/nRo/query-compiler/actions/workflows/build.yml/badge.svg)](https://github.com/nRo/query-compiler/actions/workflows/build.yml)
[![codecov](https://codecov.io/gh/nRo/query-compiler/branch/master/graph/badge.svg)](https://codecov.io/gh/nRo/query-compiler)
[![License: MIT](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE.txt)

A Java library that parses filter queries and turns them into any target object
(SQL, Elasticsearch queries, in-memory predicates, ...).
The grammar is built with [ANTLR 4](https://www.antlr.org/).

```
String ──► ANTLR lexer/parser ──► QueryTree ──► optimizations ──► your creators ──► target object
```

Supported query formats:

```
// terms combined with logical operators
(x > 0 && !(y < 1 || z == 'test'))

// full text search
"text search" test -example
```

Term operators: `==`, `!=`, `<`, `<=`, `>`, `>=`, `text`, `regex`, `in`, `!in`  
Logical operators, from highest to lowest precedence: `AND` (`&`, `&&`), `XOR`, `OR` (`|`, `||`) and `NOR`.
Operators of the same level are grouped from left to right; use brackets to override: `(a || b) && c`.

## Build

Requires JDK 21 or newer.

```
git clone https://github.com/nRo/query-compiler.git
cd query-compiler
./gradlew build
```

The library is not published to a public repository yet. To use it in another project, run
`./gradlew publishToMavenLocal` and add:

```kotlin
repositories { mavenLocal() }
dependencies { implementation("dev.gruen:query-compiler:0.0.1-SNAPSHOT") }
```

## Usage

A `QueryCompiler<T>` is built for a target type `T` (any class implementing `Query`).
You tell the builder how to create a `T` for each term operator (`==`, `>`, ...) and each logical operator
(`AND`, `OR`, ...). The compiler then turns query strings into `T` objects.

The following example compiles queries into a filter that is evaluated against a row of values
(the complete code is in [`ReadmeExampleTest`](src/test/java/dev/gruen/query/ReadmeExampleTest.java)):

```java
interface RowFilter extends Query {
    boolean test(Map<String, Object> row);
}

QueryCompiler<RowFilter> compiler = QueryCompilerBuilder.create(RowFilter.class)
        .withTermCreator(TermOperators.EQ,
                (node, field, value) -> row -> row.containsKey(field.toString())
                        && compare(row.get(field.toString()), value) == 0)
        .withTermCreator(TermOperators.GT,
                (node, field, value) -> row -> row.containsKey(field.toString())
                        && compare(row.get(field.toString()), value) > 0)
        .withANDCreator((node, children) -> row -> Arrays.stream(children).allMatch(c -> c.test(row)))
        .withORCreator((node, children) -> row -> Arrays.stream(children).anyMatch(c -> c.test(row)))
        .withOptimization(new RemoveRedundantBrackets())
        .build();

RowFilter filter = compiler.compile("age > 18 && (country == 'DE' || country == 'AT')");
filter.test(Map.of("age", 30, "country", "DE")); // true
filter.test(Map.of("age", 30, "country", "FR")); // false
```

### Implement `QueryCreator`

Instead of registering every operator separately, implement `QueryCreator<T>`. It contains one method per operator
(`eq`, `ne`, `lt`, `le`, `gt`, `ge`, `regex`, `text`, `in`, `notIn`, `not`, `and`, `or`, `xor`, `nor`,
`fullSearch`, `empty`).
`PrintQueryCreator` is a ready-made implementation that turns a query back into a string:

```java
QueryCompiler<PrintQuery> compiler = QueryCompilerBuilder
        .createDefault(PrintQuery.class, new PrintQueryCreator())
        .build();

PrintQuery query = compiler.compile("x > 0 && (y < 1 || z == 'test')");
// ((x > 0) && ((y < 1) || (z == 'test')))
```

### Custom term operators

Custom operators can be added with `withTermCreator`. Afterwards they can be used in queries:

```java
builder.withTermCreator(new TermOperator("PHRASE"), (node, field, value) -> ...);
```

```
x PHRASE 'test phrase' || y == 'test'
```

### Column values (column-to-column comparison)

By default the right-hand side of a term must be a value (number, text, boolean or `null`).
Call `withColumnValues()` to also allow a column reference there:

```java
QueryCompiler<PrintQuery> compiler = QueryCompilerBuilder
        .createDefault(PrintQuery.class, new PrintQueryCreator())
        .withColumnValues()
        .build();

compiler.compile(".a > .b && price lt .limit && 'my col' != .'other col'");
// ((.a > .b) && (price < .limit) && ('my col' != .'other col'))
```

* The right-hand column is written like any other column with a leading `.` prefix; quoted names are allowed
  (`.'other col'`, `.a.'b c'`). A name without the prefix (`a > b`) is still a syntax error.
* It works with all term operators including custom operators registered with `withTermCreator`
  (`.a like .b`) and can be combined with `and`, `or`, `xor`, `nor`, `!` and brackets.
* Regex terms (`~`) and `in` / `!in` lists do not support columns.
* Without `withColumnValues()` (the default) a column on the right-hand side is rejected with a syntax error,
  exactly as before. `withColumnValues(false)` switches it off again.

Creators (`eq`, `ne`, `lt`, `le`, `gt`, `ge`, `text` and custom term creators) receive the column as a `Value` of type
`Value.Type.FIELD`:

```java
builder.withTermCreator(TermOperators.GT, (node, field, value) -> {
    if (value.isField()) {
        Field other = value.getField(); // built the same way as the field on the left-hand side
        return compareColumns(field, other);
    }
    return compareWithValue(field, value);
});
```

`value.getString()` and `value.toString()` return the column name as it was written in the query (`.'other col'`).
`PrintQueryCreator` prints such values the same way, so printed queries can be parsed again.

### Print the query tree

Queries are first compiled to a `QueryTree` before the target object is created. The tree can be printed:

```java
QueryTree tree = compiler.compileTree("x > 0 && (y < 1 || z == 'test')");
String str = QueryPrinter.create().build().toString(tree.getRoot());
```

```
──┐ AND
  ├── (x > 0)
  └──┐ OR
     ├── (y < 1)
     └── (z == test)
```

The tree can then be converted to the target object with `compiler.compile(tree)`.


## Release

Releases are published to Maven Central with `./gradlew publishToMavenCentral`. The
[vanniktech maven-publish plugin](https://vanniktech.github.io/gradle-maven-publish-plugin/) attaches the sources and
javadoc jars, signs the artifacts and uploads them to the Central Portal; the deployment is then released manually there.
The GitHub Actions workflow `release.yml` runs this for tags named `v*` (tag `v1.0.0` publishes version `1.0.0`) and
needs the secrets `CENTRAL_USERNAME`, `CENTRAL_PASSWORD`, `GPG_PRIVATE_KEY` and `GPG_PASSPHRASE`.

## License

[MIT](LICENSE.txt)
