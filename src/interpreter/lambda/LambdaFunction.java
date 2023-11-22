package interpreter.lambda;

import interpreter.Interpreter;
import interpreter.datatypes.GHole;
import interpreter.datatypes.GLambda;
import interpreter.datatypes.GObject;
import interpreter.runtime.Environment;
import lombok.RequiredArgsConstructor;
import model.Expression;
import model.Token;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class LambdaFunction implements Invokable {
    private final Expression.Lambda lambda;
    private final Environment closure;
    private final Environment partialArguments;

    public LambdaFunction(Expression.Lambda lambda, Environment closure) {
        this(lambda, closure, new Environment(null));
    }

    @Override
    public int arity() {
        return lambda.parameters().size();
    }

    @Override
    public GObject call(Interpreter interpreter, List<GObject> arguments) {
        return interpreter.evaluateExpressionInGivenScope(() -> {
            var lambdaScope = interpreter.getCurrentScope();

            // Copy over any existing partially applied arguments
            lambdaScope.shallowCopyFrom(partialArguments);

            // Define lambda parameters from provided arguments
            List<Token> argumentHoles = new ArrayList<>();
            for (int i = 0; i < arguments.size(); i ++) {
                var parameter = lambda.parameters().get(i);
                var argument = arguments.get(i);

                if (argument == GHole.INSTANCE) {
                    argumentHoles.add(parameter);
                } else {
                    lambdaScope.define(parameter.lexeme(), argument);
                }
            }

            // All args provided, evaluate lambda
            if (argumentHoles.isEmpty()) return interpreter.evaluateExpression(lambda.body());
            // Argument holes provided, partially apply
            else return new GLambda(partiallyApply(argumentHoles, lambdaScope));
        }, new Environment(closure));
    }

    private LambdaFunction partiallyApply(List<Token> argumentHoles, Environment partialArgumentScope) {
        Expression.Lambda partiallyAppliedLambda = new Expression.Lambda(argumentHoles, lambda.body());
        return new LambdaFunction(partiallyAppliedLambda, closure, partialArgumentScope);
    }
}
