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

import de.alexgruen.query.util.StringUtil;

import java.util.function.Function;

public class QueryPrinter {
    public static final QueryPrinter DEFAULT = QueryPrinter.create().build();

    private String horizontalLine = "──";
    private String verticalLine = "│";
    private String tLine = "├";
    private String cornerLast = "└";
    private String cornerNext = "┐";
    private String labelPrefix = "";
    private String labelPostfix = "";
    private String lineSeparator = "\n";
    private Function<String,String> indentFunction;
    private QueryPrinter(){

    }

    public static QueryPrinter.Builder create(){
        return Builder.create();
    }

    public String toString(QueryNode root) {
        StringBuilder sb = new StringBuilder();
        printRec("",true,root,sb,false);
        return sb.toString();
    }

    private void printRec(String prefix,boolean isRoot, QueryNode node, StringBuilder sb, boolean lastChild){
        StringBuilder nodeLine = new StringBuilder();
        if(!isRoot){
            nodeLine.append(lastChild ? cornerLast : tLine);
        }

        nodeLine.append(horizontalLine);
        if(!node.getChildren().isEmpty()){
            nodeLine.append(cornerNext);
        }
        nodeLine.append(" ");
        String indentation = indentFunction.apply(nodeLine.toString());
        nodeLine.append(labelPrefix);
        nodeLine.append(node.getLabel());
        nodeLine.append(labelPostfix);
        sb.append(prefix);
        sb.append(nodeLine);
        sb.append(lineSeparator);
        String verticalConnection = String.format("%s%s",verticalLine,
                StringUtil.repeatChar(
                        cornerNext.length()+horizontalLine.length()-verticalLine.length(), ' '
                ));
        int i = 0;
        for(QueryNode child : node.getChildren()){
            printRec(prefix + (lastChild || isRoot ? indentation : verticalConnection), false,child,sb, i++ == node.getChildren().size() - 1);
        }

    }



    public static final class Builder {
        private String horizontalLine   = "──";
        private String verticalLine     = "│";
        private String tLine            = "├";
        private String cornerLast = "└";
        private String cornerNext = "┐";
        private String labelPrefix = "";
        private String labelPostfix = "";
        private String lineSeparator = "\n";
        private Function<String,String> indentFunction = (l) -> StringUtil.repeatChar(l.length() - 2,' ');

        private Builder() {
        }

        public static Builder create() {
            return new Builder();
        }

        public Builder withHorizontalLine(String horizontalLine) {
            this.horizontalLine = horizontalLine;
            return this;
        }

        public Builder withVerticalLine(String verticalLine) {
            this.verticalLine = verticalLine;
            return this;
        }

        public Builder withTLine(String tLine) {
            this.tLine = tLine;
            return this;
        }

        public Builder withCornerNext(String cornerNext) {
            this.cornerNext = cornerNext;
            return this;
        }
        public Builder withCornerLast(String cornerLast) {
            this.cornerLast = cornerLast;
            return this;
        }
        public Builder witIndentFunction(Function<String,String> indentFunction) {
            this.indentFunction = indentFunction;
            return this;
        }
        public Builder withLabelPrefix(String labelPrefix) {
            this.labelPrefix = labelPrefix;
            return this;
        }

        public Builder withLabelPostfix(String labelPostfix) {
            this.labelPostfix = labelPostfix;
            return this;
        }

        public Builder withLineSeparator(String lineSeparator) {
            this.lineSeparator = lineSeparator;
            return this;
        }



        public QueryPrinter build() {
            QueryPrinter queryPrinter = new QueryPrinter();
            queryPrinter.labelPrefix = this.labelPrefix;
            queryPrinter.indentFunction = this.indentFunction;
            queryPrinter.tLine = this.tLine;
            queryPrinter.cornerLast = this.cornerLast;
            queryPrinter.cornerNext = this.cornerNext;
            queryPrinter.horizontalLine = this.horizontalLine;
            queryPrinter.labelPostfix = this.labelPostfix;
            queryPrinter.lineSeparator = this.lineSeparator;
            queryPrinter.verticalLine = this.verticalLine;
            return queryPrinter;
        }
    }
}
