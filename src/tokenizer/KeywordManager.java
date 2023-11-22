package tokenizer;

import model.TokenType;

import static model.TokenType.*;

public class KeywordManager {
    public TokenType getKeywordOrIdentifier(String lexeme) {
        return switch (lexeme) {
            case "true" -> TRUE;
            case "false" -> FALSE;

            case "if" -> IF;
            case "else" -> ELSE;
            case "while" -> WHILE;

            case "let" -> LET;
            case "nil" -> NIL;

            // If nothing was matched then this is an identifier
            default -> IDENTIFIER;
        };
    }
}
