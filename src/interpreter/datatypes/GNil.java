package interpreter.datatypes;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GNil implements GObject {
    public static final String TYPE_NAME = "nil";
    public static final GNil INSTANCE = new GNil();

    @Override
    public String typeName() {
        return TYPE_NAME;
    }

    public Object value() {
        return null;
    }

    @Override
    public String stringify() {
        return "nil";
    }
}
