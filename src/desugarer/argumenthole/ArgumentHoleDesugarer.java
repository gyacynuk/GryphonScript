package desugarer.argumenthole;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import desugarer.Desugarer;
import lombok.RequiredArgsConstructor;
import model.Expression;
import util.DebugPrinter;

import java.util.List;

@Singleton
@RequiredArgsConstructor(onConstructor_ = { @Inject})
public class ArgumentHoleDesugarer implements Desugarer {
    private final ArgumentHoleLambdaGenerator argumentHoleLambdaGenerator;
    private final ArgumentHoleLambdaCombiner argumentHoleLambdaCombiner;

    @Override
    public List<Expression> desugarAll(List<Expression> expressions) {
        List<Expression> expressionsWithUncombinedGeneratedLambdas = argumentHoleLambdaGenerator.desugarAll(expressions);
        return argumentHoleLambdaCombiner.desugarAll(expressionsWithUncombinedGeneratedLambdas);
    }
}
