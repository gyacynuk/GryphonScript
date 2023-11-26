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
    public Desugarer desugaringOrchestratorFactory(ArgumentHoleDesugarer argumentHoleDesugarer) {
        final List<Desugarer> orderedDesugarers = Arrays.asList(
                new ExpansionDesugarer(),
                argumentHoleDesugarer);
        return new DesugaringOrchestrator(orderedDesugarers);
    }
}
