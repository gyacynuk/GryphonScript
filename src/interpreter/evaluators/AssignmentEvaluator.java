package interpreter.evaluators;

import interpreter.Interpreter;
import interpreter.data.GObject;
import model.Expression;

public class AssignmentEvaluator implements ExpressionEvaluator<Expression.Assignment> {
    @Override
    public GObject evaluateExpression(Interpreter interpreter, Expression.Assignment expression) {
        GObject value = interpreter.evaluateExpression(expression.value());

        // TODO: if value instanceOf Heap, then mutate heap value instead of assigning to local scope
        interpreter.assignStackVariable(expression, value);
        return value;
    }
}
