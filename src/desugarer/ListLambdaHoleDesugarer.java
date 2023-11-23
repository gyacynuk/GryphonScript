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
    private static final String GENERATED_TOKEN_LEXEME_TEMPLATE = "gen-param-token-%d";

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
            case Expression.Invocation invocation -> desugarInvocationIfContainsHoles(invocation);
            case Expression.Lambda lambda -> new Expression.Lambda(
                    lambda.parameters(),
                    desugarExpression(lambda.body()));
            case null -> null;
        };
    }

    private Expression desugarListLiteralIfContainsHoles(Expression.ListLiteral expression) {
        if (expression.values().contains(Expression.HOLE)) {
            return generateListLambda(expression);
        }
        // Recurse on elements
        return new Expression.ListLiteral(
                expression.values().stream()
                        .map(this::desugarExpression)
                        .toList(),
                expression.closingBracket());
    }

    private Expression desugarInvocationIfContainsHoles(Expression.Invocation expression) {
        if (Expression.HOLE.equals(expression.callee()) || expression.arguments().contains(Expression.HOLE)) {
            return generateInvocationLambda(expression);
        }
        // Recurse on subexpressions
        return new Expression.Invocation(
                desugarExpression(expression.callee()),
                expression.closingBracket(),
                expression.arguments().stream()
                        .map(this::desugarExpression)
                        .toList());
    }

    private Expression.Lambda generateListLambda(Expression.ListLiteral expression) {
        // Generate params
        int runningCount = 0;
        List<Token> holeTokenParams = new ArrayList<>();
        List<Expression> bodyElementExpressions = new ArrayList<>();
        for (Expression elementExpression : expression.values()) {
            if (elementExpression != Expression.HOLE) {
                bodyElementExpressions.add(desugarExpression(elementExpression));
            } else {
                Token paramIdentifierToken = generateParameterToken(runningCount++, expression.closingBracket().line());
                holeTokenParams.add(paramIdentifierToken);
                bodyElementExpressions.add(new Expression.Variable(paramIdentifierToken));
            }
        }

        // Generate body
        Expression.ListLiteral body = new Expression.ListLiteral(bodyElementExpressions, expression.closingBracket());

        return new Expression.Lambda(holeTokenParams, body);
    }

    private Expression.Lambda generateInvocationLambda(Expression.Invocation expression) {
        int runningCount = 0;
        List<Token> holeTokenParams = new ArrayList<>();

        // Callee
        Expression desugaredCallee;
        if (expression.callee() == Expression.HOLE) {
            Token calleeTokenParam = generateParameterToken(runningCount++, expression.closingBracket().line());
            holeTokenParams.add(calleeTokenParam);
            desugaredCallee = new Expression.Variable(calleeTokenParam);
        } else {
            desugaredCallee = desugarExpression(expression.callee());
        }

        // Arguments
        List<Expression> desugaredArguments = new ArrayList<>();
        for (Expression argumentExpression : expression.arguments()) {
            if (argumentExpression != Expression.HOLE) {
                desugaredArguments.add(desugarExpression(argumentExpression));
            } else {
                Token paramIdentifierToken = generateParameterToken(runningCount++, expression.closingBracket().line());
                holeTokenParams.add(paramIdentifierToken);
                desugaredArguments.add(new Expression.Variable(paramIdentifierToken));
            }
        }

        // Generate body
        Expression.Invocation body = new Expression.Invocation(
                desugaredCallee,
                expression.closingBracket(),
                desugaredArguments);

        return new Expression.Lambda(holeTokenParams, body);
    }

    private Token generateParameterToken(int tokenIndex, int lineNumber) {
        return new Token(
                TokenType.IDENTIFIER,
                String.format(GENERATED_TOKEN_LEXEME_TEMPLATE, tokenIndex),
                null,
                lineNumber);
    }
}

