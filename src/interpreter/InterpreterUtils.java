package interpreter;

import interpreter.data.GBoolean;
import interpreter.data.GNil;
import interpreter.data.GObject;
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

    public static Function<Function<GObject.Numeric, GObject>, GObject> numericEnforcementFunctionDecorator(
            Token operator, Object operand) {
        return (Function<GObject.Numeric, GObject> numericOperation) -> {
            if (operand instanceof GObject.Numeric numeric) {
                return numericOperation.apply(numeric);
            }
            throw new RuntimeError(operator, "Operand must be a number");
        };
    }

    public static Function<BiFunction<GObject.Numeric, GObject.Numeric, GObject>, GObject> numericEnforcementBiFunctionDecorator(
            Token operator, GObject operandLeft, GObject operandRight) {
        return (BiFunction<GObject.Numeric, GObject.Numeric, GObject> numericOperation) -> {
            if (operandLeft instanceof GObject.Numeric numericLeft && operandRight instanceof GObject.Numeric numericRight) {
                return numericOperation.apply(numericLeft, numericRight);
            }
            throw new RuntimeError(operator, "Operands must be numbers");
        };
    }
}
