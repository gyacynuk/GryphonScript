package config;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import desugarer.Desugarer;
import desugarer.ArgumentHoleDesugarer;
import desugarer.DesugaringOrchestrator;
import interpreter.Interpreter;
import interpreter.TreeWalkInterpreter;
import model.Expression;
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
    public Desugarer desugaringOrchestratorFactory() {
        final List<Desugarer> orderedDesugarers = Arrays.asList(
                new ArgumentHoleDesugarer());
        return new DesugaringOrchestrator(orderedDesugarers);
    }
}
