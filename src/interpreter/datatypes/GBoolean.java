package interpreter.datatypes;

public record GBoolean(Boolean value) implements GObject {
    public static final String TYPE_NAME = "boolean";

    @Override
    public String typeName() {
        return TYPE_NAME;
    }
}
