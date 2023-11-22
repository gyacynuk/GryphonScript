package interpreter.evaluators;

import interpreter.Interpreter;
import interpreter.InterpreterUtils;
import interpreter.datatypes.GObject;
import model.Expression;

public class WhileEvaluator implements ExpressionEvaluator<Expression.While> {
    @Override
    public GObject evaluateExpression(Interpreter interpreter, Expression.While expression) {
        GObject finalEvaluation = null;
        while (InterpreterUtils.isTruthy(interpreter.evaluateExpression(expression.condition()))) {
            finalEvaluation = interpreter.evaluateExpression(expression.body());
        }
        return finalEvaluation;
    }
}
