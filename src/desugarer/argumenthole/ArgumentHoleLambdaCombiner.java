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
    protected Expression desugarBinary(Expression.Binary binary) {
        // Recurse on sub-expressions first, which will bubble up any descendant generated lambdas to this level
        Expression left = this.desugarExpression(binary.left());
        Expression right = this.desugarExpression(binary.right());

        var leftLambda = getCombinableLambda(left);
        var rightLambda = getCombinableLambda(right);

        if (leftLambda.isPresent() || rightLambda.isPresent()) {
            List<Token> leftArguments = getCombinableArguments(leftLambda);
            List<Token> rightArguments = getCombinableArguments(rightLambda);
            Expression leftBody = getCombinableBody(leftLambda, left);
            Expression rightBody = getCombinableBody(rightLambda, right);

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

    private List<Token> getCombinableArguments(Optional<Expression.Lambda> generatedLambda) {
        return generatedLambda
                .map(Expression.Lambda::parameters)
                .orElseGet(Collections::emptyList);
    }

    private Expression getCombinableBody(Optional<Expression.Lambda> generatedLambda, Expression fallbackExpression) {
        return generatedLambda
                .map(Expression.Lambda::body)
                .orElse(fallbackExpression);
    }

    private Optional<Expression.Lambda> getCombinableLambda(Expression expression) {
        if (expression instanceof Expression.Lambda lambda && lambda.combinable()) {
            return Optional.of(lambda);
        }
        return Optional.empty();
    }
}
