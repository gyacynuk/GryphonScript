package interpreter.datatypes;

import error.Result;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public record GStruct(Map<GObject, GObject> value) implements GIndexable {
    public static GStruct initEmptyStruct() {
        return new GStruct(new LinkedHashMap<>());
    }

    @Override
    public String typeName() {
        return "struct";
    }

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

    @Override
    public String stringify() {
        String stringifiedFields = value().entrySet().stream()
                .map(entry -> entry.getKey().stringify() + ":" + entry.getValue().stringify())
                .collect(Collectors.joining(","));
        return "{" + stringifiedFields + "}";
    }
}
