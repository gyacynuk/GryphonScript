package interpreter.datatypes;

public record GBoolean(Boolean value) implements GObject {
    @Override
    public String typeName() {
        return "boolean";
    }
}
