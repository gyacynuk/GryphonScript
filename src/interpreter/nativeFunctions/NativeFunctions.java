package interpreter.nativeFunctions;

import interpreter.Interpreter;
import interpreter.datatypes.*;
import interpreter.errors.RuntimeError;
import interpreter.lambda.InvocationExecutionError;
import interpreter.lambda.Invokable;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

@NoArgsConstructor
public class NativeFunctions {
    private static final String NATIVE_LAMBDA_STRING_REPRESENTATION_TEMPLATE = "<native-lambda-arity-%d>";
    public record NativeFunction(String name, GLambda lambda) {}

    public List<NativeFunction> getNativeFunctions() {
        return Arrays.asList(
                print(),
                milliTime(),
                size());
    }

    private NativeFunction print() {
        var lambda = new Invokable() {
            @Override
            public int arity() { return 1; }

            @Override
            public GObject call(Interpreter interpreter, List<GObject> arguments) {
                GObject arg = arguments.get(0);
                System.out.println(arg.stringify());
                return arg;
            }

            @Override
            public String toString() {
                return String.format(NATIVE_LAMBDA_STRING_REPRESENTATION_TEMPLATE, arity());
            }
        };

        return new NativeFunction("print", new GLambda(lambda));
    }

    private NativeFunction milliTime() {
        var lambda = new Invokable() {
            @Override
            public int arity() { return 0; }

            @Override
            public GObject call(Interpreter interpreter, List<GObject> arguments) {
                return new GDouble((double) System.currentTimeMillis());
            }

            @Override
            public String toString() {
                return String.format(NATIVE_LAMBDA_STRING_REPRESENTATION_TEMPLATE, arity());
            }
        };

        return new NativeFunction("milliTime", new GLambda(lambda));
    }

    private NativeFunction size() {
        var lambda = new Invokable() {
            @Override
            public int arity() { return 1; }

            @Override
            public GObject call(Interpreter interpreter, List<GObject> arguments) {
                if (arguments.get(0) instanceof GIndexable indexable) {
                    return new GInteger(indexable.getSize());
                }
                throw new InvocationExecutionError("Can only invoke lambda 'call' on lists and structs");
            }

            @Override
            public String toString() {
                return String.format(NATIVE_LAMBDA_STRING_REPRESENTATION_TEMPLATE, arity());
            }
        };

        return new NativeFunction("size", new GLambda(lambda));
    }
}
