package interpreter.standardlibrary;

import interpreter.Interpreter;
import interpreter.datatypes.GObject;

import java.util.List;
import java.util.function.BiFunction;

public record LibraryFunction(
        String name,
        int arity,
        BiFunction<Interpreter, List<GObject>, GObject> function
) {}
