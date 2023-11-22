package interpreter.lambda;

import interpreter.Interpreter;
import interpreter.datatypes.GObject;

import java.util.List;

public interface Invokable {
    int arity();
    GObject call(Interpreter interpreter, List<GObject> arguments);
}
