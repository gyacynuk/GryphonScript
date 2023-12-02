package config;

import com.google.inject.AbstractModule;
import interpreter.standardlibrary.libraries.*;
import interpreter.standardlibrary.libraries.types.StructLibrary;
import interpreter.standardlibrary.libraries.types.TypeLibrary;

public class StandardLibraryModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(StructLibrary.class);
        bind(TypeLibrary.class);
        bind(FileLibrary.class);
        bind(ListLibrary.class);
        bind(RootLibrary.class);
        bind(StringLibrary.class);
        bind(MathLibrary.class);
    }
}
