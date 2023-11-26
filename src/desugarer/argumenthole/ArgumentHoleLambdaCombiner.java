package desugarer.argumenthole;

import com.google.inject.Singleton;
import desugarer.BaseDesugarer;
import lombok.NoArgsConstructor;
import model.BinaryExpressionInitializer;
import model.Expression;
import model.Token;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Singleton
@NoArgsConstructor
public class ArgumentHoleLambdaCombiner extends BaseDesugarer {

    @Override
    protected Expression desugarUnary(Expression.Unary unary) {
        // Recurse on sub-expression first, which will bubble up any descendant generated lambdas to this level
        Expression right = this.desugarExpression(unary.right());
        return getCombinableLambda(right)
                .map(rightLambda -> {
                    List<Token> rightArguments = rightLambda.parameters();
                    Expression rightBody = rightLambda.body();

                    Expression combinedBody = new Expression.Unary(
                            unary.operator(),
                            rightBody);

                    return (Expression) new Expression.Lambda(rightArguments, combinedBody, true);
                })
                .orElse(unary);
    }

    @Override
    protected Expression desugarBinary(Expression.Binary binary) {
        // Recurse on sub-expressions first, which will bubble up any descendant generated lambdas to this level
        Expression left = this.desugarExpression(binary.left());
        Expression right = this.desugarExpression(binary.right());

        var leftLambda = getCombinableLambda(left);
        var rightLambda = getCombinableLambda(right);

        if (leftLambda.isPresent() || rightLambda.isPresent()) {
            List<Token> leftArguments = leftLambda
                    .map(Expression.Lambda::parameters)
                    .orElseGet(Collections::emptyList);
            List<Token> rightArguments = rightLambda
                    .map(Expression.Lambda::parameters)
                    .orElseGet(Collections::emptyList);

            Expression leftBody = leftLambda
                    .map(Expression.Lambda::body)
                    .orElse(left);
            Expression rightBody = rightLambda
                    .map(Expression.Lambda::body)
                    .orElse(right);

            List<Token> combinedArguments = new ArrayList<>(leftArguments);
            combinedArguments.addAll(rightArguments);
            Expression combinedBody = BinaryExpressionInitializer
                    .getInitializerForExpression(binary)
                    .apply(new BinaryExpressionInitializer.Args(leftBody, rightBody, binary.operator()));

            return new Expression.Lambda(combinedArguments, combinedBody, true);
        }

        // No generated lambdas in the expression subtree
        return BinaryExpressionInitializer
                .getInitializerForExpression(binary)
                .apply(new BinaryExpressionInitializer.Args(left, right, binary.operator()));
    }

    private Optional<Expression.Lambda> getCombinableLambda(Expression expression) {
        if (expression instanceof Expression.Lambda lambda && lambda.combinable()) {
            return Optional.of(lambda);
        }
        return Optional.empty();
    }
}
