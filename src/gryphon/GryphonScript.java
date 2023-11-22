package gryphon;

import com.google.inject.Inject;
import error.ErrorReporter;
import interpreter.Interpreter;
import lombok.RequiredArgsConstructor;
import model.Expression;
import model.Token;
import parser.Parser;
import resolver.Resolver;
import tokenizer.Tokenizer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@RequiredArgsConstructor(onConstructor_ = { @Inject })
public class GryphonScript {
    private static final boolean DEBUG = true;
    private static final String REPL_PREFIX = "> ";

    private final ErrorReporter errorReporter;
    private final Tokenizer tokenizer;
    private final Parser parser;
    private final Resolver resolver;
    private final Interpreter interpreter;

    public void executeFile(String filePath) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(filePath));
        run(new String(bytes, Charset.defaultCharset()));

        // Indicate an error occurred in the exit code
        if (errorReporter.isInError()) System.exit(65);
        if (errorReporter.isInRuntimeError()) System.exit(70);
    }

    public void executeREPL() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        while (true) {
            System.out.print(REPL_PREFIX);
            String line = reader.readLine();

            if (line == null) {
                break;
            } else {
                run(line);
                errorReporter.clearErrors();
            }
        }
    }

    private void run(String source) {
        List<Token> tokens = tokenizer.tokenize(source);

        // Stop if there was a tokenization error.
        if (errorReporter.isInError()) return;

        if (DEBUG) System.out.println(tokens);

        List<Expression> expressions = parser.parse(tokens);

        // Stop if there was a syntax error.
        if (errorReporter.isInError()) return;

        if (DEBUG) System.out.println(expressions);

        resolver.resolveProgram(interpreter, expressions);

        // Stop if there was a resolution error.
        if (errorReporter.isInError()) return;

        interpreter.executeProgram(expressions);
    }
}
