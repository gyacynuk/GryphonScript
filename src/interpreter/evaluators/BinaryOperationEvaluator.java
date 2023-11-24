package interpreter.evaluators;

import interpreter.Interpreter;
import interpreter.InterpreterUtils;
import interpreter.datatypes.*;
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
        var listEnforcementBiFunctionDecorator = InterpreterUtils
                .listEnforcementBiFunctionDecorator(expression.operator(), left, right);
        return switch (expression.operator().type()) {
            case GREATER -> numericEnforcementDecorator.apply(GNumeric::greaterThan);
            case GREATER_EQUAL -> numericEnforcementDecorator.apply(GNumeric::greaterThanOrEqualTo);
            case LESS -> numericEnforcementDecorator.apply(GNumeric::lessThan);
            case LESS_EQUAL -> numericEnforcementDecorator.apply(GNumeric::lessThanOrEqualTo);
            case PLUS -> numericEnforcementDecorator.apply(GNumeric::add);
            case MINUS -> numericEnforcementDecorator.apply(GNumeric::subtract);
            case SLASH -> numericEnforcementDecorator.apply(GNumeric::divide);
            case STAR -> numericEnforcementDecorator.apply(GNumeric::multiply);
            case STRING_CONCAT -> {
                if (left instanceof GString leftString) {
                    yield new GString(leftString.value() + right.stringify());
                } else {
                    throw new RuntimeError(expression.operator(), "Concat operator '@' must have a string as the left-hand operand");
                }
            }
            case LIST_CONCAT -> listEnforcementBiFunctionDecorator.apply(GList::concat);
            case EQUAL_EQUAL -> new GBoolean(Objects.equals(left.value(), right.value()));
            case BANG_EQUAL -> new GBoolean(!Objects.equals(left.value(), right.value()));
            default -> throw new RuntimeError(expression.operator(), "Unknown binary operator");
        };
    }
}
