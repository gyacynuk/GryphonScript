package interpreter.standardlibrary;

import java.util.List;

public interface Library {
    List<String> getLibraryPath();
    List<LibraryFunction> getFunctions();
}
