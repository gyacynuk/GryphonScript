package interpreter.evaluators;

import interpreter.Interpreter;
import interpreter.datatypes.GLambda;
import interpreter.datatypes.GObject;
import interpreter.lambda.LambdaFunction;
import model.Expression;

public class LambdaEvaluator implements ExpressionEvaluator<Expression.Lambda> {
    @Override
    public GObject evaluateExpression(Interpreter interpreter, Expression.Lambda expression) {
        return new GLambda(new LambdaFunction(expression, interpreter.getCurrentScope()));
    }
}
