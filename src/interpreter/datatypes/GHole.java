package interpreter.datatypes;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GHole implements GObject {
    public static final String TYPE_NAME = "hole";
    public static final GHole INSTANCE = new GHole();

    @Override
    public String typeName() {
        return TYPE_NAME;
    }

    public Object value() {
        return null;
    }

    @Override
    public String stringify() {
        return "<argument-hole>";
    }
}
