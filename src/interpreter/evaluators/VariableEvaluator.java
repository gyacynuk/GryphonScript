package interpreter.evaluators;

import interpreter.Interpreter;
import model.Expression;

public class VariableEvaluator implements ExpressionEvaluator<Expression.Variable> {
    @Override
    public Object evaluateExpression(Interpreter interpreter, Expression.Variable expression) {
        return interpreter.lookUpStackVariable(expression);
    }
}
