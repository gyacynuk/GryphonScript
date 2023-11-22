package interpreter.data;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GHole implements GObject {
    public static final GHole INSTANCE = new GHole();
    public Object value() {
        return null;
    }

    @Override
    public String stringify() {
        return "<argument-hole>";
    }
}
