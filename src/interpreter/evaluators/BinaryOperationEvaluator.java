package interpreter.evaluators;

import interpreter.Interpreter;
import interpreter.InterpreterUtils;
import interpreter.data.GBoolean;
import interpreter.data.GObject;
import interpreter.data.GString;
import interpreter.errors.RuntimeError;
import model.Expression;

import java.util.Objects;

public class BinaryOperationEvaluator implements ExpressionEvaluator<Expression.Binary.Operation> {
    @Override
    public GObject evaluateExpression(Interpreter interpreter, Expression.Binary.Operation expression) {
        GObject left = interpreter.evaluateExpression(expression.left());
        GObject right = interpreter.evaluateExpression(expression.right());

        var numericEnforcementDecorator = InterpreterUtils
                .numericEnforcementBiFunctionDecorator(expression.operator(), left, right);
        return switch (expression.operator().type()) {
            case GREATER -> numericEnforcementDecorator.apply(GObject.Numeric::greaterThan);
            case GREATER_EQUAL -> numericEnforcementDecorator.apply(GObject.Numeric::greaterThanOrEqualTo);
            case LESS -> numericEnforcementDecorator.apply(GObject.Numeric::lessThan);
            case LESS_EQUAL -> numericEnforcementDecorator.apply(GObject.Numeric::lessThanOrEqualTo);
            case PLUS -> numericEnforcementDecorator.apply(GObject.Numeric::add);
            case MINUS -> numericEnforcementDecorator.apply(GObject.Numeric::subtract);
            case SLASH -> numericEnforcementDecorator.apply(GObject.Numeric::divide);
            case STAR -> numericEnforcementDecorator.apply(GObject.Numeric::multiply);
            case CONCAT -> {
                if (left instanceof GString leftString) {
                    yield new GString(leftString.value() + right.stringify());
                } else {
                    throw new RuntimeError(expression.operator(), "Concat operator '@' must have a string as the left-hand operand");
                }
            }
            case EQUAL_EQUAL -> new GBoolean(Objects.equals(left.value(), right.value()));
            case BANG_EQUAL -> new GBoolean(!Objects.equals(left.value(), right.value()));
            default -> throw new RuntimeError(expression.operator(), "Unknown binary operator");
        };
    }
}
