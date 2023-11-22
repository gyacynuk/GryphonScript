package desugarer;

import jakarta.inject.Singleton;
import lombok.NoArgsConstructor;
import model.Expression;
import model.Token;
import model.TokenType;

import java.util.ArrayList;
import java.util.List;

@Singleton
@NoArgsConstructor
public class ListLambdaHoleDesugarer implements Desugarer {

    private static final String GENERATED_TOKEN_LEXEME_TEMPLATE = "generated-list-lambda-parameter-token-%d";

    @Override
    public List<Expression> desugar(List<Expression> expressions) {
        return expressions.stream()
                .map(this::desugarExpression)
                .toList();
    }

    private Expression desugarExpression(Expression expression) {
        return switch (expression) {
            case Expression.Literal literal -> literal;
            case Expression.ListLiteral listLiteral -> desugarListLiteralIfContainsHoles(listLiteral);
            case Expression.Assignment assignment -> new Expression.Assignment(
                    assignment.variable(),
                    desugarExpression(assignment.value()));
            case Expression.IndexAssignment indexAssignment -> new Expression.IndexAssignment(
                    desugarExpression(indexAssignment.assignee()),
                    desugarExpression(indexAssignment.index()),
                    desugarExpression(indexAssignment.value()),
                    indexAssignment.closingBracket());
            case Expression.Index index -> new Expression.Index(
                    desugarExpression(index.callee()),
                    index.closingBracket(),
                    desugarExpression(index.index()));
            case Expression.Declaration declaration -> new Expression.Declaration(
                    declaration.variable(),
                    desugarExpression(declaration.initializer()));
            case Expression.Variable variable -> variable;
            case Expression.Group group -> new Expression.Group(desugarExpression(group.expression()));
            case Expression.Unary unary -> new Expression.Unary(unary.operator(), desugarExpression(unary.right()));
            case Expression.Binary.Operation binary -> new Expression.Binary.Operation(
                    desugarExpression(binary.left()),
                    desugarExpression(binary.right()),
                    binary.operator());
            case Expression.Binary.Logical binary -> new Expression.Binary.Logical(
                    desugarExpression(binary.left()),
                    desugarExpression(binary.right()),
                    binary.operator());
            case Expression.Binary.Infix binary -> new Expression.Binary.Infix(
                    desugarExpression(binary.left()),
                    desugarExpression(binary.right()),
                    binary.operator());
            case Expression.Block block -> new Expression.Block(block.expressions().stream()
                    .map(this::desugarExpression)
                    .toList());
            case Expression.If ifExpression -> new Expression.If(
                    desugarExpression(ifExpression.condition()),
                    desugarExpression(ifExpression.thenBranch()),
                    desugarExpression(ifExpression.elseBranch()));
            case Expression.While whileExpression -> new Expression.While(
                    desugarExpression(whileExpression.condition()),
                    desugarExpression(whileExpression.body()));
            case Expression.Invocation invocation -> new Expression.Invocation(
                    desugarExpression(invocation.callee()),
                    invocation.closingBracket(),
                    invocation.arguments().stream()
                            .map(this::desugarExpression)
                            .toList());
            case Expression.Lambda lambda -> new Expression.Lambda(
                    lambda.parameters(),
                    desugarExpression(lambda.body()));
            case null -> null;
        };
    }

    private Expression desugarListLiteralIfContainsHoles(Expression.ListLiteral expression) {
        if (expression.values().contains(Expression.HOLE)) {
            return generateListLambda(expression);
        } else {
            return expression;
        }
    }

    private Expression.Lambda generateListLambda(Expression.ListLiteral expression) {
        // Generate params
        int runningCount = 0;
        List<Token> holeTokens = new ArrayList<>();
        List<Expression> bodyElementExpressions = new ArrayList<>();
        for (Expression elementExpression : expression.values()) {
            if (elementExpression != Expression.HOLE) {
                bodyElementExpressions.add(elementExpression);
            } else {
                Token paramIdentifierToken = new Token(
                        TokenType.IDENTIFIER,
                        String.format(GENERATED_TOKEN_LEXEME_TEMPLATE, runningCount++),
                        null,
                        expression.closingBracket().line());
                holeTokens.add(paramIdentifierToken);
                bodyElementExpressions.add(new Expression.Variable(paramIdentifierToken));
            }
        }

        // Generate body
        Expression.ListLiteral body = new Expression.ListLiteral(bodyElementExpressions, expression.closingBracket());

        return new Expression.Lambda(holeTokens, body);
    }
}

