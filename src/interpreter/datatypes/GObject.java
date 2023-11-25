package interpreter.datatypes;

public sealed interface GObject permits GHole, GNil, GBoolean, GString, GLambda, GNumeric, GIndexable  {
    Object value();
    String typeName();
    default String stringify() {
        return value().toString();
    }
}
