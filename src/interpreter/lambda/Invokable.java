package interpreter.lambda;

import interpreter.Interpreter;
import interpreter.data.GObject;

import java.util.List;

public interface Invokable {
    int arity();
    GObject call(Interpreter interpreter, List<GObject> arguments);
}
