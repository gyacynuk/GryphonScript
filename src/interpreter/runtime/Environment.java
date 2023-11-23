package interpreter.runtime;

import interpreter.datatypes.GObject;
import interpreter.errors.RuntimeError;
import lombok.RequiredArgsConstructor;
import model.Token;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class Environment {
    private static final String UNDEFINED_VARIABLE_ERROR_MESSAGE_TEMPLATE = "Undefined variable '%s'";

    private final Environment enclosingEnvironment;
    private final Map<String, GObject> values = new HashMap<>();

    public static Environment createGlobalEnvironment() {
        return new Environment(null);
    }

    public void define(String name, GObject value) {
        values.put(name, value);
    }

    public void assign(Token name, GObject value) {
        var key = name.lexeme();
        if (values.containsKey(key)) {
            values.put(key, value);
        }
        // Try assigning in enclosing scope
        else if (enclosingEnvironment != null) {
            enclosingEnvironment.assign(name, value);
        }
        // Prevent assignment to undefined variables
        else {
            throw new RuntimeError(name, String.format(UNDEFINED_VARIABLE_ERROR_MESSAGE_TEMPLATE, key));
        }
    }

    public GObject get(Token name) {
        var key = name.lexeme();
        if (values.containsKey(key)) {
            return values.get(key);
        }

        // Check enclosing scope for variable
        if (enclosingEnvironment != null) return enclosingEnvironment.get(name);
        else throw new RuntimeError(name, String.format(UNDEFINED_VARIABLE_ERROR_MESSAGE_TEMPLATE, key));
    }

    public GObject getAt(int distance, String name) {
        return getAncestorAtDistance(distance).values.get(name);
    }

    public void assignAtAncestor(int distance, Token name, GObject value) {
        getAncestorAtDistance(distance).values.put(name.lexeme(), value);
    }

    private Environment getAncestorAtDistance(int distance) {
        Environment environment = this;
        for (int i = 0; i < distance; i ++) {
            environment = environment.enclosingEnvironment;
        }

        return environment;
    }
}
