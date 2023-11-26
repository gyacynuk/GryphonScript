package desugarer.argumenthole;

import com.google.inject.Singleton;
import desugarer.BaseDesugarer;
import interpreter.datatypes.GString;
import lombok.NoArgsConstructor;
import model.BinaryExpressionInitializer;
import model.Expression;
import model.Token;
import model.TokenType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Singleton
@NoArgsConstructor
public class ArgumentHoleLambdaGenerator extends BaseDesugarer {
    // TODO: Add generated TokenType as well for improved debugging
    // TODO: make lambda grab the outermost expression (or group) for an argument hole, such that _ % 2 == 0 is a valid
    //       expression, instead of evaluating to (\(a) -> a % 2) == 0 as it currently does
    //       Can do this in a second pass of a desugarer, which looks if an argument is a generated function, and if so
    //       extracts the body out as the operand.
    //       Need to make sure generated args are unique, so use a global static counter. Also can use a monad to extract
    //       args from an expression/lambda when combining
    private static int counter = 0;
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
                    Token interpolationToken = generateParameterToken(-1);
                    holeTokenParams.add(interpolationToken);
                    body = new Expression.Binary.Operation(
                            new Expression.Binary.Operation(
                                    body,
                                    new Expression.Variable(interpolationToken),
                                    new Token(TokenType.STRING_CONCAT, null, null, -1, false)),
                            new Expression.Literal(new GString(subStrings.get(i))),
                            new Token(TokenType.STRING_CONCAT, null, null, -1, false));
                }

                yield new Expression.Lambda(holeTokenParams, body, true);
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
    protected Expression desugarUnary(Expression.Unary unary) {
        if (Expression.HOLE.equals(unary.right())) {
            return generateUnaryLambda(unary);
        }
        return new Expression.Unary(
                unary.operator(),
                desugarExpression(unary.right()));
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
        List<Token> holeTokenParams = new ArrayList<>();
        List<Expression> bodyElementExpressions = new ArrayList<>();
        for (Expression elementExpression : expression.values()) {
            if (elementExpression != Expression.HOLE) {
                bodyElementExpressions.add(desugarExpression(elementExpression));
            } else {
                Token paramIdentifierToken = generateParameterToken(expression.closingBracket().line());
                holeTokenParams.add(paramIdentifierToken);
                bodyElementExpressions.add(new Expression.Variable(paramIdentifierToken));
            }
        }

        // Generate body
        Expression.ListLiteral body = new Expression.ListLiteral(bodyElementExpressions, expression.closingBracket());

        return new Expression.Lambda(holeTokenParams, body, true);
    }

    private Expression.Lambda generateUnaryLambda(Expression.Unary unary) {
        // Right
        Token rightTokenParam = generateParameterToken(unary.operator().line());
        List<Token> holeTokenParams = Collections.singletonList(rightTokenParam);
        Expression desugaredRight = new Expression.Variable(rightTokenParam);

        // Generate body
        Expression.Unary body = new Expression.Unary(
                unary.operator(),
                desugaredRight);

        return new Expression.Lambda(holeTokenParams, body, true);
    }

    private Expression.Lambda generateBinaryLambda(Expression.Binary expression) {
        List<Token> holeTokenParams = new ArrayList<>();

        // Left
        Expression desugaredLeft;
        if (expression.left() == Expression.HOLE) {
            Token leftTokenParam = generateParameterToken(expression.operator().line());
            holeTokenParams.add(leftTokenParam);
            desugaredLeft = new Expression.Variable(leftTokenParam);
        } else {
            desugaredLeft = desugarExpression(expression.left());
        }

        // Right
        Expression desugaredRight;
        if (expression.right() == Expression.HOLE) {
            Token rightTokenParam = generateParameterToken(expression.operator().line());
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

        return new Expression.Lambda(holeTokenParams, body, true);
    }

    private Expression.Lambda generateInvocationLambda(Expression.Invocation expression) {
        List<Token> holeTokenParams = new ArrayList<>();

        // Callee
        Expression desugaredCallee;
        if (expression.callee() == Expression.HOLE) {
            Token calleeTokenParam = generateParameterToken(expression.closingBracket().line());
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
                Token paramIdentifierToken = generateParameterToken(expression.closingBracket().line());
                holeTokenParams.add(paramIdentifierToken);
                desugaredArguments.add(new Expression.Variable(paramIdentifierToken));
            }
        }

        // Generate body
        Expression.Invocation body = new Expression.Invocation(
                desugaredCallee,
                expression.closingBracket(),
                desugaredArguments);

        // Invocation does not allow combinable lambdas to propagate upwards from it in the AST
        return new Expression.Lambda(holeTokenParams, body, false);
    }

    private Token generateParameterToken(int lineNumber) {
        return new Token(
                TokenType.IDENTIFIER,
                String.format(GENERATED_TOKEN_LEXEME_TEMPLATE, counter ++),
                null,
                lineNumber,
                false);
    }
}
