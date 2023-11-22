package interpreter.evaluators;

import interpreter.Interpreter;
import interpreter.lambda.LambdaFunction;
import model.Expression;

public class LambdaEvaluator implements ExpressionEvaluator<Expression.Lambda> {
    @Override
    public Object evaluateExpression(Interpreter interpreter, Expression.Lambda expression) {
        return new LambdaFunction(expression, interpreter.getCurrentScope());
    }
}
