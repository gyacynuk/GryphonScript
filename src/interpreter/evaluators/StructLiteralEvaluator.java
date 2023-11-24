package interpreter.evaluators;

import interpreter.Interpreter;
import interpreter.datatypes.GObject;
import interpreter.datatypes.GString;
import interpreter.datatypes.GStruct;
import interpreter.errors.RuntimeError;
import model.Expression;

public class StructLiteralEvaluator implements ExpressionEvaluator<Expression.StructLiteral> {
    @Override
    public GObject evaluateExpression(Interpreter interpreter, Expression.StructLiteral expression) {
        GStruct struct = GStruct.initEmptyStruct();

        for (Expression fieldExpression : expression.fields()) {
            if (fieldExpression instanceof Expression.StructFieldDeclaration field) {
                GString fieldName = new GString(field.variable().lexeme());
                GObject fieldValue = interpreter.evaluateExpression(field.initializer());
                struct.setAtIndex(fieldName, fieldValue);
            } else {
                throw new RuntimeError(expression.closingBracket(), "Invalid struct literal. Fields must be in the form of \"fieldName\", or \"fieldName: expression\"");
            }
        }

        return struct;
    }
}
