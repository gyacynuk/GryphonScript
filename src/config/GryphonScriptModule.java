package config;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import desugarer.Desugarer;
import desugarer.argumenthole.ArgumentHoleDesugarer;
import desugarer.argumenthole.ArgumentHoleLambdaGenerator;
import desugarer.DesugaringOrchestrator;
import desugarer.ExpansionDesugarer;
import interpreter.Interpreter;
import interpreter.TreeWalkInterpreter;
import interpreter.standardlibrary.LibraryStructFactory;
import interpreter.standardlibrary.libraries.FileLibrary;
import interpreter.standardlibrary.libraries.ListLibrary;
import interpreter.standardlibrary.libraries.RootLibrary;
import interpreter.standardlibrary.libraries.StringLibrary;
import interpreter.standardlibrary.libraries.types.StructLibrary;
import interpreter.standardlibrary.libraries.types.TypeLibrary;
import parser.Parser;
import parser.RecursiveDescentParser;
import resolver.Resolver;
import resolver.SemanticVariableResolver;
import tokenizer.LexicalTokenizer;
import tokenizer.Tokenizer;

import java.util.Arrays;
import java.util.List;

public class GryphonScriptModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(Tokenizer.class).to(LexicalTokenizer.class);
        bind(Parser.class).to(RecursiveDescentParser.class);
        bind(Interpreter.class).to(TreeWalkInterpreter.class);
        bind(Resolver.class).to(SemanticVariableResolver.class);
    }

    @Provides
    @Singleton
    public Desugarer desugaringOrchestratorFactory(
            ExpansionDesugarer expansionDesugarer,
            ArgumentHoleDesugarer argumentHoleDesugarer) {
        final List<Desugarer> orderedDesugarers = Arrays.asList(
                expansionDesugarer,
                argumentHoleDesugarer);
        return new DesugaringOrchestrator(orderedDesugarers);
    }

    @Provides
    @Singleton
    public LibraryStructFactory libraryStructFactory(
            StructLibrary structLibrary,
            TypeLibrary typeLibrary,
            FileLibrary fileLibrary,
            ListLibrary listLibrary,
            RootLibrary rootLibrary,
            StringLibrary stringLibrary) {
        return new LibraryStructFactory(Arrays.asList(
                structLibrary,
                typeLibrary,
                fileLibrary,
                listLibrary,
                rootLibrary,
                stringLibrary));
    }
}
