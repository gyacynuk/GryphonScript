package interpreter.datatypes;

import error.Result;

import java.util.Map;

public record GStruct(Map<GObject, GObject> value) implements GIndexable {
    @Override
    public Result<GObject, String> getAtIndex(GObject index) {
        return Result.success(value().getOrDefault(index, GNil.INSTANCE));
    }

    @Override
    public Result<GObject, String> setAtIndex(GObject index, GObject value) {
        value().put(index, value);
        return Result.success(this);
    }

    @Override
    public int getSize() {
        return value().size();
    }
}
