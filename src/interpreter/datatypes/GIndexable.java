package interpreter.datatypes;

import error.Result;

public sealed interface GIndexable extends GObject permits GList, GString, GStruct {
    GBoolean hasIndex(GObject index);
    Result<GObject, String> getAtIndex(GObject index);
    Result<GObject, String> setAtIndex(GObject index, GObject value);
    int getSize();
}
