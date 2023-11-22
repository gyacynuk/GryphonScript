package interpreter.datatypes;

import interpreter.lambda.Invokable;

public record GLambda(Invokable value) implements GObject {}
