package interpreter.lambda;

import interpreter.Interpreter;

import java.util.List;

public interface Invokable {
    int arity();
    Object call(Interpreter interpreter, List<Object> arguments);
}
