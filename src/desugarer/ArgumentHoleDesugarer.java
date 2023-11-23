package desugarer;

import interpreter.datatypes.GString;
import jakarta.inject.Singleton;
import lombok.NoArgsConstructor;
import model.BinaryExpressionInitializer;
import model.Expression;
import model.Token;
import model.TokenType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Singleton
@NoArgsConstructor
public class ArgumentHoleDesugarer extends BaseDesugarer {
    private static final String GENERATED_TOKEN_LEXEME_TEMPLATE = "gen-param-token-%d";

    @Override
    protected Expression desugarLiteral(Expression.Literal expression) {
        return switch (expression.value()) {
            case GString gString -> {
                List<String> subStrings = splitStringAtHoles(gString.value());
                if (subStrings.size() == 1) {
                    yield expression;
                }

                List<Token> holeTokenParams = new ArrayList<>();
                Expression body = new Expression.Literal(new GString(subStrings.get(0)));
                for (int i = 1; i < subStrings.size(); i ++) {
                    Token interpolationToken = generateParameterToken(i, -1);
                    holeTokenParams.add(interpolationToken);
                    body = new Expression.Binary.Operation(
                            new Expression.Binary.Operation(
                                    body,
                                    new Expression.Variable(interpolationToken),
                                    new Token(TokenType.CONCAT, null, null, -1)),
                            new Expression.Literal(new GString(subStrings.get(i))),
                            new Token(TokenType.CONCAT, null, null, -1));
                }

                yield new Expression.Lambda(holeTokenParams, body);
            }
            default -> expression;
        };
    }

    @Override
    protected Expression desugarListLiteral(Expression.ListLiteral expression) {
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

    @Override
    protected Expression desugarBinary(Expression.Binary expression) {
        if (Arrays.asList(expression.left(), expression.right()).contains(Expression.HOLE)) {
            return generateBinaryLambda(expression);
        }
        return BinaryExpressionInitializer.getInitializerForExpression(expression).apply(
                new BinaryExpressionInitializer.Args(
                        desugarExpression(expression.left()),
                        desugarExpression(expression.right()),
                        expression.operator()));
    }

    @Override
    protected Expression desugarInvocation(Expression.Invocation expression) {
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

    private List<String> splitStringAtHoles(String s) {
        int i = 0;
        List<String> result = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        while (i < s.length()) {
            if (s.charAt(i) == '_') {
                result.add(sb.toString());
                sb = new StringBuilder();
                i ++;
            }
            else if (s.charAt(i) == '\\' && i+1 < s.length() && s.charAt(i+1) == '_') {
                sb.append('_');
                i += 2; // Increment by 2 since we consume a backslash and the following character
            }
            else {
                sb.append(s.charAt(i));
                i ++;
            }
        }
        result.add(sb.toString());
        return result;
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

    private Expression.Lambda generateBinaryLambda(Expression.Binary expression) {
        int runningCount = 0;
        List<Token> holeTokenParams = new ArrayList<>();

        // Left
        Expression desugaredLeft;
        if (expression.left() == Expression.HOLE) {
            Token leftTokenParam = generateParameterToken(runningCount++, expression.operator().line());
            holeTokenParams.add(leftTokenParam);
            desugaredLeft = new Expression.Variable(leftTokenParam);
        } else {
            desugaredLeft = desugarExpression(expression.left());
        }

        // Right
        Expression desugaredRight;
        if (expression.right() == Expression.HOLE) {
            Token rightTokenParam = generateParameterToken(runningCount++, expression.operator().line());
            holeTokenParams.add(rightTokenParam);
            desugaredRight = new Expression.Variable(rightTokenParam);
        } else {
            desugaredRight = desugarExpression(expression.right());
        }

        // Generate body
        Expression.Binary body = BinaryExpressionInitializer.getInitializerForExpression(expression).apply(
                new BinaryExpressionInitializer.Args(
                        desugaredLeft,
                        desugaredRight,
                        expression.operator()));

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
