package interpreter.evaluators;

import interpreter.Interpreter;
import model.Expression;

public class AssignmentEvaluator implements ExpressionEvaluator<Expression.Assignment> {
    @Override
    public Object evaluateExpression(Interpreter interpreter, Expression.Assignment expression) {
        Object value = interpreter.evaluateExpression(expression.value());

        // TODO: if value instanceOf Heap, then mutate heap value instead of assigning to local scope
        interpreter.assignStackVariable(expression, value);
        return value;
    }
}
