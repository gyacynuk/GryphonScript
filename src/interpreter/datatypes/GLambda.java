package interpreter.datatypes;

import interpreter.lambda.Invokable;

public record GLambda(Invokable value) implements GObject {
    @Override
    public String typeName() {
        return "lambda";
    }

    @Override
    public String stringify() {
        return "<lambda-arity-%d>".formatted(value.arity());
    }
}
