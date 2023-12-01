package config;

import com.google.inject.AbstractModule;
import interpreter.standardlibrary.libraries.FileLibrary;
import interpreter.standardlibrary.libraries.ListLibrary;
import interpreter.standardlibrary.libraries.RootLibrary;
import interpreter.standardlibrary.libraries.StringLibrary;
import interpreter.standardlibrary.libraries.types.StructLibrary;
import interpreter.standardlibrary.libraries.types.TypeLibrary;

import java.util.Arrays;

public class StandardLibraryModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(StructLibrary.class);
        bind(TypeLibrary.class);
        bind(FileLibrary.class);
        bind(ListLibrary.class);
        bind(RootLibrary.class);
        bind(StringLibrary.class);
    }
}
