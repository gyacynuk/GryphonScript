package interpreter.standardlibrary;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import interpreter.Interpreter;
import interpreter.datatypes.GLambda;
import interpreter.datatypes.GObject;
import interpreter.datatypes.GString;
import interpreter.datatypes.GStruct;
import interpreter.lambda.Invokable;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class LibraryStructFactory {
    private final List<Library> libraries;

    public String getStandardLibraryName() {
        return "StdLib";
    }

    public GStruct buildStandardLibraryStruct() {
        GStruct standardLibrary = GStruct.initEmptyStruct();
        libraries.forEach(library ->
                library.getFunctions().forEach(libraryFunction ->
                        addLibraryLambda(standardLibrary, libraryFunction, library.getLibraryPath())));
        return standardLibrary;
    }

    private void addLibraryLambda(GStruct library, LibraryFunction libraryFunction, List<String> path) {
        if (path.isEmpty()) {
            GObject lambdaName = new GString(libraryFunction.name());
            GObject lambdaFunction = libraryFunctionToGLambda(libraryFunction);
            library.setAtIndex(lambdaName, lambdaFunction);
        } else {
            GStruct subLibrary = GStruct.initEmptyStruct();
            GObject subLibraryName = new GString(path.get(0));
            library.setAtIndex(subLibraryName, subLibrary);
            addLibraryLambda(subLibrary, libraryFunction, path.subList(1, path.size()));
        }
    }

    private GLambda libraryFunctionToGLambda(LibraryFunction libraryFunction) {
        return new GLambda(new Invokable() {
            @Override
            public int arity() {
                return libraryFunction.arity();
            }

            @Override
            public GObject call(Interpreter interpreter, List<GObject> arguments) {
                return libraryFunction.function().apply(interpreter, arguments);
            }
        });
    }
}
