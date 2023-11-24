package interpreter;

import interpreter.datatypes.*;
import interpreter.errors.RuntimeError;
import model.Token;

import java.util.function.BiFunction;
import java.util.function.Function;

public class InterpreterUtils {
    /**
     * Ruby-style truthiness: false and nil are falsey, and everything else is truthy
     *
     * @param o object under truthiness evaluation
     * @return true if truthy, false otherwise
     */
    public static boolean isTruthy(GObject o) {
        return switch (o) {
            case null -> false;
            case GNil ignored -> false;
            case GBoolean gBoolean -> gBoolean.value();
            default -> true;
        };
    }

    public static Function<Function<GNumeric, GObject>, GObject> numericEnforcementFunctionDecorator(
            Token operator, Object operand) {
        return (Function<GNumeric, GObject> numericOperation) -> {
            if (operand instanceof GNumeric numeric) {
                return numericOperation.apply(numeric);
            }
            throw new RuntimeError(operator, "Operand must be a number");
        };
    }

    public static Function<BiFunction<GNumeric, GNumeric, GObject>, GObject> numericEnforcementBiFunctionDecorator(
            Token operator, GObject operandLeft, GObject operandRight) {
        return (BiFunction<GNumeric, GNumeric, GObject> numericOperation) -> {
            if (operandLeft instanceof GNumeric numericLeft && operandRight instanceof GNumeric numericRight) {
                return numericOperation.apply(numericLeft, numericRight);
            }
            throw new RuntimeError(operator, "Operands must be numbers");
        };
    }

    public static Function<BiFunction<GList, GList, GObject>, GObject> listEnforcementBiFunctionDecorator(
            Token operator, GObject operandLeft, GObject operandRight) {
        return (BiFunction<GList, GList, GObject> listOperation) -> {
            if (operandLeft instanceof GList listLeft && operandRight instanceof GList listRight) {
                return listOperation.apply(listLeft, listRight);
            }
            throw new RuntimeError(operator, "Operands must be lists");
        };
    }
}
