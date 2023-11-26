package util;

import model.Expression;
import model.SugarExpression;

import java.util.List;
import java.util.stream.Collectors;

public class DebugPrinter {

    static int tabMultiplier = 0;

    public static String toDebugListString(List<Expression> expressions) {
        tabMultiplier ++;
        String inner = expressions.stream()
                .map(DebugPrinter::toDebugString)
                .map(str -> "\n" + "\t".repeat(tabMultiplier) + str)
                .collect(Collectors.joining(","));
        tabMultiplier--;
        return String.format("[%s\n%s]", inner, "\t".repeat(tabMultiplier));
    }

    public static String toDebugString(Expression expression) {
        tabMultiplier ++;
        String str = switch (expression) {
                case Expression.Literal literal -> String.format("Literal: %s", literal.value());
                case Expression.ListLiteral listLiteral -> String.format("ListLiteral: %s", toDebugListString(listLiteral.values()));
                case Expression.StructFieldDeclaration structFieldDeclaration -> String.format("StructFieldDeclaration:%s%s",
                        newline("variable", structFieldDeclaration.variable().toString()),
                        newline("initializer", toDebugString(structFieldDeclaration.initializer())));
                case Expression.StructLiteral structLiteral -> String.format("StructLiteral:%s",
                        newline("fields", toDebugListString(structLiteral.fields())));
                case Expression.Variable variable -> String.format("Variable: %s", variable.name());
                case Expression.Declaration declaration -> String.format("Declaration:%s%s",
                        newline("variable", declaration.variable().toString()),
                        newline("initializer", toDebugString(declaration.initializer())));
                case Expression.Assignment assignment -> String.format("Assignment:%s%s",
                        newline("variable", assignment.variable().toString()),
                        newline("value", toDebugString(assignment.value())));
                case Expression.IndexAssignment indexAssignment -> String.format("IndexAssignment:%s%s%s",
                        newline("assignee", toDebugString(indexAssignment.assignee())),
                        newline("index", toDebugString(indexAssignment.index())),
                        newline("value", toDebugString(indexAssignment.value())));
                case Expression.Unary unary -> String.format("Unary:%s%s",
                        newline("operator", unary.operator().toString()),
                        newline("right", toDebugString(unary.right())));
                case Expression.Binary binary -> String.format("Binary:%s%s%s",
                        newline("operator", binary.operator().toString()),
                        newline("left", toDebugString(binary.left())),
                        newline("right", toDebugString(binary.right())));
                case Expression.Group group -> String.format("Group:%s",
                        newline("expression", toDebugString(group.expression())));
                case Expression.Block block -> String.format("Block:%s",
                        newline("expressions", toDebugListString(block.expressions())));
                case Expression.If ifExpression -> String.format("If:%s%s%s",
                        newline("condition", toDebugString(ifExpression.condition())),
                        newline("thenBranch", toDebugString(ifExpression.thenBranch())),
                        newline("elseBranch", toDebugString(ifExpression.elseBranch())));
                case Expression.While whileExpression -> String.format("While:%s%s",
                        newline("condition", toDebugString(whileExpression.condition())),
                        newline("body", toDebugString(whileExpression.body())));
                case Expression.Lambda lambda -> String.format("Lambda:%s%s%s",
                        newline("parameters", lambda.parameters().toString()),
                        newline("body", toDebugString(lambda.body())),
                        newline("combinable", String.valueOf(lambda.combinable())));
                case Expression.Invocation invocation -> String.format("Invocation:%s%s",
                        newline("callee", toDebugString(invocation.callee())),
                        newline("arguments", toDebugListString(invocation.arguments())));
                case Expression.Index index -> String.format("Index: %s%s",
                        newline("callee", toDebugString(index.callee())),
                        newline("index", toDebugString(index.index())));
                case SugarExpression ignored -> "Sugar";
                case null -> "null";
            };
        tabMultiplier --;
        return str;
    }

    private static String newline(String prefix, String value) {
        return String.format("\n%s%s: %s",
                "\t".repeat(tabMultiplier),
                prefix,
                value);
    }
}
