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

    @Override
    public int arity() {
        return lambda.parameters().size();
    }

    @Override
    public GObject call(Interpreter interpreter, List<GObject> arguments) {
        return interpreter.evaluateExpressionInGivenScope(() -> {
            var lambdaScope = interpreter.getCurrentScope();

            // Define lambda parameters from  arguments
            for (int i = 0; i < arguments.size(); i ++) {
                var parameter = lambda.parameters().get(i);
                var argument = arguments.get(i);
                lambdaScope.define(parameter.lexeme(), argument);
            }

            return interpreter.evaluateExpression(lambda.body());
        }, new Environment(closure));
    }
}
