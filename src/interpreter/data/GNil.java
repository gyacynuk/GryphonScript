package interpreter.data;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GNil implements GObject {
    public static final GNil INSTANCE = new GNil();
    public Object value() {
        return null;
    }

    @Override
    public String stringify() {
        return "nil";
    }
}
