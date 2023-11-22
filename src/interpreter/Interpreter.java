package interpreter;

import interpreter.lambda.Invokable;
import interpreter.runtime.Environment;
import model.Expression;
import model.Token;

import java.util.List;
import java.util.function.Supplier;

public interface Interpreter {
    Object executeProgram(List<Expression> expressions);
    Object evaluateExpression(Expression expression);
    Object evaluateExpressionInNewScope(Supplier<Object> expressionEvaluator);
    Object evaluateExpressionInGivenScope(Supplier<Object> expressionEvaluator, Environment scope);
    Environment getCurrentScope();
    void resolveStackVariableAtDepth(Expression expression, int depth);
    void assignStackVariable(Expression.Assignment expression, Object value);
    Object lookUpStackVariable(Expression.Variable variable);
    Object invokeLambda(Invokable invokable, List<Object> arguments, Token token);
}
