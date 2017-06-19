package LEXER;

/**
 * Created by chainhelen on 2017/4/28.
 */
public enum TOKEN_TYPE {
    Identifier,

    RW_Int,
    RW_String,
    RW_While,
    RW_Break,
    RW_Continue,
    RW_If,
    RW_Elif,
    RW_Else,
    RW_Return,
    RW_Function,

    Mul,
    Add,
    Sub,
    Div,
    Ent,
    Sem, // ';'
    COMMA, // ','
    Assign, // '='

    LParen, // "('
    RParen, // ')'
    LBrace, // '{'
    RBrace, // '}'
    Num,
    String,
    Eof,
    UnkonwnToken
}
