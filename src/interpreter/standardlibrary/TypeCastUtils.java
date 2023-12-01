package interpreter.standardlibrary;

import interpreter.datatypes.*;
import interpreter.lambda.InvocationExecutionError;

import java.util.List;

public class TypeCastUtils {
    public static GInteger toGInteger(List<GObject> arguments, int index, String lambdaName) {
        if (!(arguments.get(index) instanceof GInteger integer)) {
            String errorMessage = generateErrorMessageForWrongLambdaArgumentType(index, lambdaName, GInteger.TYPE_NAME);
            throw new InvocationExecutionError(errorMessage);
        }
        return integer;
    }

    public static GString toGString(List<GObject> arguments, int index, String lambdaName) {
        if (!(arguments.get(index) instanceof GString gString)) {
            String errorMessage = generateErrorMessageForWrongLambdaArgumentType(index, lambdaName, GString.TYPE_NAME);
            throw new InvocationExecutionError(errorMessage);
        }
        return gString;
    }

    public static GList toGList(List<GObject> arguments, int index, String lambdaName) {
        if (!(arguments.get(index) instanceof GList list)) {
            String errorMessage = generateErrorMessageForWrongLambdaArgumentType(index, lambdaName, GList.TYPE_NAME);
            throw new InvocationExecutionError(errorMessage);
        }
        return list;
    }

    public static GLambda toGLambda(List<GObject> arguments, int index, String lambdaName) {
        if (!(arguments.get(index) instanceof GLambda lambda)) {
            String errorMessage = generateErrorMessageForWrongLambdaArgumentType(index, lambdaName, GLambda.TYPE_NAME);
            throw new InvocationExecutionError(errorMessage);
        }
        return lambda;
    }

    public static GStruct toGStruct(List<GObject> arguments, int index, String lambdaName) {
        if (!(arguments.get(index) instanceof GStruct struct)) {
            String errorMessage = generateErrorMessageForWrongLambdaArgumentType(index, lambdaName, GStruct.TYPE_NAME);
            throw new InvocationExecutionError(errorMessage);
        }
        return struct;
    }

    private static String generateErrorMessageForWrongLambdaArgumentType(int index, String lambdaName, String expectedType) {
        return String.format("Argument at position %d in call to lambda '%s' must be of type %s",
                index, lambdaName, expectedType);
    }
}
