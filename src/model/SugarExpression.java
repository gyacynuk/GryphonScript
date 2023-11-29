package model;

import java.util.List;

public sealed interface SugarExpression extends Expression {
    Token getErrorReportingToken();

    sealed interface DestructureLambdaParam permits Token, Destructure {}
    record DestructureLambda(List<DestructureLambdaParam> parameters, Expression body, Token arrow) implements SugarExpression {
        @Override
        public Token getErrorReportingToken() {
            return arrow;
        }
    }

    record ArrayDestructureField(Token nullableVariable, List<Expression> context, Destructure nullableNestedDestructure) implements SugarExpression {
        @Override
        public Token getErrorReportingToken() {
            return nullableVariable == null
                    ? new Token(TokenType.NIL, "generated-token", null, -1, false)
                    : nullableVariable;
        }
    }

    record StructDestructureField(Token variable, List<Expression> context, Destructure nullableNestedDestructure) implements SugarExpression {
        @Override
        public Token getErrorReportingToken() {
            return variable;
        }
    }

    sealed interface Destructure extends SugarExpression, DestructureLambdaParam {
        List<? extends SugarExpression> fields();
        record ArrayDestructure(List<ArrayDestructureField> fields, Token closingBracket) implements Destructure {
            @Override
            public Token getErrorReportingToken() {
                return closingBracket;
            }
        }
        record StructDestructure(List<StructDestructureField> fields, Token closingBracket) implements Destructure {
            @Override
            public Token getErrorReportingToken() {
                return closingBracket;
            }
        }
    }

    record DestructureDeclaration(Destructure destructureExpression, Token equals, Expression initializer) implements SugarExpression {
        @Override
        public Token getErrorReportingToken() {
            return equals;
        }
    }
}
