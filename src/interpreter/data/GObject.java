package interpreter.data;

import java.util.List;

public sealed interface GObject {
    public static final GNil NIL = new GNil();
    record GNil() implements GObject {}
    record GInteger(Integer value) implements GObject {}
    record GDouble(Double value) implements GObject {}
    record GString(String value) implements GObject {}

    sealed interface Heap extends GObject {
        long uuid();
        record GList (long uuid, List<GObject> value) implements Heap {}
    }
}
