package interpreter.evaluators;

import interpreter.Interpreter;
import interpreter.datatypes.GObject;
import model.Expression;

public class BlockEvaluator implements ExpressionEvaluator<Expression.Block> {
    @Override
    public GObject evaluateExpression(Interpreter interpreter, Expression.Block expression) {
        return interpreter.evaluateExpressionInNewScope(() -> {
            GObject finalEvaluation = null;
            for (Expression subExpression : expression.expressions()) {
                finalEvaluation = interpreter.evaluateExpression(subExpression);
            }
            return finalEvaluation;
        });
    }
}
