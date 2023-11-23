package interpreter.datatypes;

import error.Result;

public sealed interface GIndexable extends GObject permits GList {
    Result<GObject, String> getAtIndex(GObject index);
    Result<GObject, String> setAtIndex(GObject index, GObject value);
    int getSize();
}
