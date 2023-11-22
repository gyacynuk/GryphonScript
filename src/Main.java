import com.google.inject.Guice;
import com.google.inject.Injector;
import config.GryphonScriptModule;
import gryphon.GryphonScript;

import java.io.IOException;

public class Main {
    private static final String USAGE_MESSAGE = "Usage: holy [path]";

    public static void main(String[] args) throws IOException {
        Injector injector = Guice.createInjector(new GryphonScriptModule());
        GryphonScript script = injector.getInstance(GryphonScript.class);

        switch (args.length) {
            case 0 -> script.executeREPL();
            case 1 -> script.executeFile(args[0]);
            default -> promptUsageAndExit();
        }
    }

    private static void promptUsageAndExit() {
        System.out.println(USAGE_MESSAGE);
        System.exit(64);
    }
}