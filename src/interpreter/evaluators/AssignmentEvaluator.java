package interpreter.evaluators;

import interpreter.Interpreter;
import interpreter.datatypes.GObject;
import model.Expression;

public class AssignmentEvaluator implements ExpressionEvaluator<Expression.Assignment> {
    @Override
    public GObject evaluateExpression(Interpreter interpreter, Expression.Assignment expression) {
        GObject value = interpreter.evaluateExpression(expression.value());
        interpreter.assignStackVariable(expression, value);
        return value;
    }
}
