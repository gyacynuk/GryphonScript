package interpreter.nativeFunctions;

import interpreter.Interpreter;
import interpreter.InterpreterUtils;
import interpreter.datatypes.*;
import interpreter.lambda.InvocationExecutionError;
import interpreter.lambda.Invokable;
import lombok.NoArgsConstructor;

import java.util.*;

@NoArgsConstructor
public class NativeFunctions {
    private static final String NATIVE_LAMBDA_STRING_REPRESENTATION_TEMPLATE = "<native-lambda-arity-%d>";
    public record NativeFunction(String name, GLambda lambda) {}

    public List<NativeFunction> getNativeFunctions() {
        return Arrays.asList(
                print(),
                milliTime(),
                size(),
                add(),
                filter());
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

    private NativeFunction add() {
        var lambda = new Invokable() {
            @Override
            public int arity() { return 2; }

            @Override
            public GObject call(Interpreter interpreter, List<GObject> arguments) {
                if (!(arguments.get(0) instanceof GList list)) {
                    throw new InvocationExecutionError("First argument to lambda 'add' must be a list");
                }
                return list.add(arguments.get(1));
            }

            @Override
            public String toString() {
                return String.format(NATIVE_LAMBDA_STRING_REPRESENTATION_TEMPLATE, arity());
            }
        };

        return new NativeFunction("add", new GLambda(lambda));
    }

    private NativeFunction filter() {
        var lambda = new Invokable() {
            @Override
            public int arity() { return 2; }

            @Override
            public GObject call(Interpreter interpreter, List<GObject> arguments) {
                if (!(arguments.get(0) instanceof GList list)) {
                    throw new InvocationExecutionError("First argument to lambda 'filter' must be a list");
                }
                if (!(arguments.get(1) instanceof GLambda predicate)) {
                    throw new InvocationExecutionError("Second argument to lambda 'filter' must be a lambda");
                }

                // Implement using a for-loop instead of using Stream APIs for performance improvement
                List<GObject> filteredList = new ArrayList<>();
                for (GObject element : list.value()) {
                    if (InterpreterUtils.isTruthy(predicate.value().call(interpreter, Collections.singletonList(element)))) {
                        filteredList.add(element);
                    }
                }

                return new GList(filteredList);
            }

            @Override
            public String toString() {
                return String.format(NATIVE_LAMBDA_STRING_REPRESENTATION_TEMPLATE, arity());
            }
        };

        return new NativeFunction("filter", new GLambda(lambda));
    }
}
