package interpreter.nativeFunctions;

import interpreter.Interpreter;
import interpreter.datatypes.GDouble;
import interpreter.datatypes.GLambda;
import interpreter.datatypes.GObject;
import interpreter.lambda.Invokable;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

@NoArgsConstructor
public class NativeFunctions {
    public record NativeFunction(String name, GLambda lambda) {}

    public List<NativeFunction> getNativeFunctions() {
        return Arrays.asList(
                print(),
                milliTime());
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
                return "<native-lambda>";
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
                return "<native-lambda>";
            }
        };

        return new NativeFunction("milliTime", new GLambda(lambda));
    }
}
