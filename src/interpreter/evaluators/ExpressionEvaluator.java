package interpreter.evaluators;

import interpreter.Interpreter;
import model.Expression;

public interface ExpressionEvaluator<T extends Expression> {
    Object evaluateExpression(Interpreter interpreter, T expression);
}
