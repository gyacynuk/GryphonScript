package gryphon;

import com.google.inject.Inject;
import desugarer.Desugarer;
import error.ErrorReporter;
import interpreter.Interpreter;
import model.Expression;
import model.Token;
import parser.Parser;
import resolver.Resolver;
import tokenizer.Tokenizer;
import util.DebugPrinter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class GryphonScript {
    public static final boolean DEBUG = false;
    private static final String REPL_PREFIX = "> ";

    private final ErrorReporter errorReporter;
    private final Tokenizer tokenizer;
    private final Parser parser;
    private final Desugarer desugarer;
    private final Resolver resolver;
    private final Interpreter interpreter;

    @Inject
    public GryphonScript(ErrorReporter errorReporter, Tokenizer tokenizer, Parser parser, Desugarer desugarer, Resolver resolver, Interpreter interpreter) {
        this.errorReporter = errorReporter;
        this.tokenizer = tokenizer;
        this.parser = parser;
        this.desugarer = desugarer;
        this.resolver = resolver;
        this.interpreter = interpreter;
    }

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
        if (DEBUG) prettyPrint("Parsed", expressions);

        expressions = desugarer.desugarAll(expressions);

        // Stop if there was a syntax error.
        if (errorReporter.isInError()) return;
        if (DEBUG) prettyPrint("Desugared", expressions);

        resolver.resolveProgram(interpreter, expressions);

        // Stop if there was a resolution error.
        if (errorReporter.isInError()) return;

        interpreter.executeProgram(expressions);
    }

    public static void prettyPrint(String name, List<Expression> list) {
        System.out.printf("%s\n-----\n\n", name);
        list.stream()
                .map(DebugPrinter::toDebugString)
                .forEach(System.out::println);
    }
}
