package interpreter.nativeFunctions;

import interpreter.Interpreter;
import interpreter.InterpreterUtils;
import interpreter.lambda.Invokable;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

@NoArgsConstructor
public class NativeFunctions {
    public record NativeFunction(String name, Invokable lambda) {}

    public List<NativeFunction> getNativeFunctions() {
        return Arrays.asList(
                printFunction(),
                milliTime());
    }

    private NativeFunction printFunction() {
        var lambda = new Invokable() {
            @Override
            public int arity() { return 1; }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                Object arg = arguments.get(0);
                System.out.println(InterpreterUtils.stringify(arg));
                return arg;
            }
        };

        return new NativeFunction("print", lambda);
    }

    private NativeFunction milliTime() {
        var lambda = new Invokable() {
            @Override
            public int arity() { return 0; }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                return (double) System.currentTimeMillis();
            }
        };

        return new NativeFunction("milliTime", lambda);
    }
}
