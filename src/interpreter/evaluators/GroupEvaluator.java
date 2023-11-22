package interpreter.evaluators;

import interpreter.Interpreter;
import model.Expression;

public class GroupEvaluator implements ExpressionEvaluator<Expression.Group> {
    @Override
    public Object evaluateExpression(Interpreter interpreter, Expression.Group expression) {
        return interpreter.evaluateExpression(expression.expression());
    }
}
