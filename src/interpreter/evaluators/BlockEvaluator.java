package interpreter.evaluators;

import interpreter.Interpreter;
import model.Expression;

public class BlockEvaluator implements ExpressionEvaluator<Expression.Block> {
    @Override
    public Object evaluateExpression(Interpreter interpreter, Expression.Block expression) {
        return interpreter.evaluateExpressionInNewScope(() -> {
            Object finalEvaluation = null;
            for (Expression subExpression : expression.expressions()) {
                finalEvaluation = interpreter.evaluateExpression(subExpression);
            }
            return finalEvaluation;
        });
    }
}
