package interpreter.evaluators;

import interpreter.Interpreter;
import interpreter.InterpreterUtils;
import model.Expression;

public class WhileEvaluator implements ExpressionEvaluator<Expression.While> {
    @Override
    public Object evaluateExpression(Interpreter interpreter, Expression.While expression) {
        Object finalEvaluation = null;
        while (InterpreterUtils.isTruthy(interpreter.evaluateExpression(expression.condition()))) {
            finalEvaluation = interpreter.evaluateExpression(expression.body());
        }
        return finalEvaluation;
    }
}
