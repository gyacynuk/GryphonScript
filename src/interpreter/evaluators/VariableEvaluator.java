package interpreter.evaluators;

import interpreter.Interpreter;
import interpreter.data.GObject;
import model.Expression;

public class VariableEvaluator implements ExpressionEvaluator<Expression.Variable> {
    @Override
    public GObject evaluateExpression(Interpreter interpreter, Expression.Variable expression) {
        return interpreter.lookUpStackVariable(expression);
    }
}
