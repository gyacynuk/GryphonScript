package resolver;

import interpreter.Interpreter;
import model.Expression;

import java.util.List;

public interface Resolver {
     void resolveProgram(Interpreter interpreter, List<Expression> expressions);
}
