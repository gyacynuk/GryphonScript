package interpreter.evaluators;

import interpreter.Interpreter;
import interpreter.InterpreterUtils;
import interpreter.datatypes.GNil;
import interpreter.datatypes.GObject;
import model.Expression;

public class IfEvaluator implements ExpressionEvaluator<Expression.If> {
    @Override
    public GObject evaluateExpression(Interpreter interpreter, Expression.If expression) {
        if (InterpreterUtils.isTruthy(interpreter.evaluateExpression(expression.condition()))) {
            return interpreter.evaluateExpression(expression.thenBranch());
        } else if (expression.elseBranch() != null) {
            return interpreter.evaluateExpression(expression.elseBranch());
        } else {
            return GNil.INSTANCE;
        }
    }
}
