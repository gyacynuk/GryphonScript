package interpreter.evaluators;

import interpreter.Interpreter;
import interpreter.data.GObject;
import model.Expression;

public class GroupEvaluator implements ExpressionEvaluator<Expression.Group> {
    @Override
    public GObject evaluateExpression(Interpreter interpreter, Expression.Group expression) {
        return interpreter.evaluateExpression(expression.expression());
    }
}
