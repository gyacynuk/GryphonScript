package interpreter;

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
    public static boolean isTruthy(Object o) {
        return switch (o) {
            case null -> false;
            case Boolean b -> b;
            default -> true;
        };
    }

    public static String stringify(Object o) {
        return switch (o) {
            case null -> "nil";
            case String s -> s;
            case Double d -> {
                String text = d.toString();
                if (text.endsWith(".0")) {
                    text = text.substring(0, text.length() - 2);
                }
                yield text;
            }
            default -> o.toString();
        };
    }

    public static Function<Function<Double, Object>, Object> numericEnforcementFunctionDecorator(
            Token operator, Object operand) {
        return (Function<Double, Object> doubleOperation) -> {
            if (operand instanceof Double doubleOperand) {
                return doubleOperation.apply(doubleOperand);
            }
            throw new RuntimeError(operator, "Operand must be a number");
        };
    }

    public static Function<BiFunction<Double, Double, Object>, Object> numericEnforcementBiFunctionDecorator(
            Token operator, Object operandLeft, Object operandRight) {
        return (BiFunction<Double, Double, Object> doubleOperation) -> {
            if (operandLeft instanceof Double doubleOperandLeft && operandRight instanceof Double doubleOperandRight) {
                return doubleOperation.apply(doubleOperandLeft, doubleOperandRight);
            }
            throw new RuntimeError(operator, "Operands must be numbers");
        };
    }
}
