package config;

import com.google.inject.AbstractModule;
import interpreter.Interpreter;
import interpreter.TreeWalkInterpreter;
import parser.BaseParser;
import parser.Parser;
import parser.RecursiveDescentParser;
import resolver.Resolver;
import resolver.SemanticVariableResolver;
import tokenizer.LexicalTokenizer;
import tokenizer.Tokenizer;

public class GryphonScriptModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(Tokenizer.class).to(LexicalTokenizer.class);
        bind(Parser.class).to(RecursiveDescentParser.class);
        bind(Interpreter.class).to(TreeWalkInterpreter.class);
        bind(Resolver.class).to(SemanticVariableResolver.class);
    }
}
