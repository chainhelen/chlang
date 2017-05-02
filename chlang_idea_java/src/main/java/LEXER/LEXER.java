package LEXER;

import java.util.*;

/**
 * Created by chainhelen on 2017/4/23.
 */
public class LEXER{
    private int line = 0;
    private int srcCurPos = 0;
    private String src = "";

    private final static String reservedWords = "int string while if function";
    private Map<String, TOKEN> reservedWordTable = new TreeMap<String, TOKEN>();

    public LEXER(String src) {
        this.src = reservedWords;

        for(TOKEN_TYPE token_type : TOKEN_TYPE.values()) {
            String name = token_type.toString();
            if(name.substring(0, 2).equals("RW")) {
                TOKEN token = getNextToken();
                if(TOKEN_TYPE.Eof == token.getToken_type()) {
                    break;
                }
                token.setToken_type(token_type);
                this.reservedWordTable.put(token.getStr(), token);
            }
        }
        this.src = src;
        this.srcCurPos = 0;
    }

    public TOKEN lookHead() {
        int linebak = line;
        int srcCurPosBak = srcCurPos;

        TOKEN token = getNextToken();

        this.line = linebak;
        this.srcCurPos = srcCurPosBak;

        return token;
    }

    public TOKEN getNextToken() {
        int srcLen = src.length();
        TOKEN token = new TOKEN();
        char c;

        if(srcCurPos >= srcLen){
            token.setToken_type(TOKEN_TYPE.Eof);
            token.setStr("<Eof>");
            return token;
        }

        c = src.charAt(srcCurPos);
        while(' ' == c) {
            srcCurPos++;
            if(srcCurPos > srcLen) {
                token.setToken_type(TOKEN_TYPE.Eof);
                token.setStr("<Eof>");
                return token;
            }
            c = src.charAt(srcCurPos);
        }

        c = src.charAt(srcCurPos);
        if(Character.isLetter(c)) {
            int firstPos = srcCurPos;
            int lastPos;

            while( srcCurPos < srcLen &&
                    (Character.isLetter(src.charAt(srcCurPos)) || Character.isDigit(src.charAt(srcCurPos)) )){
                srcCurPos++;
            }
            lastPos = srcCurPos;
            token.setStr(src.substring(firstPos, lastPos));

            TOKEN findToken = reservedWordTable.get(token.getStr());
            if(null != findToken) {
                token.setToken_type(findToken.getToken_type());
            } else {
                token.setToken_type(TOKEN_TYPE.Identifier);
            }

            return token;
        }

        if(Character.isDigit(c)) {
            int firstPos = srcCurPos;
            int lastPos;

            while(srcCurPos < srcLen && (Character.isDigit(src.charAt(srcCurPos)))) {
                srcCurPos++;
            }
            lastPos = srcCurPos;

            int number = Integer.parseInt(src.substring(firstPos, lastPos));
            token.setNumber(number);
            token.setToken_type(TOKEN_TYPE.Num);

            return token;
        }

        if('\"' == c) {
            int firstPos = srcCurPos;
            int lastPos;

            while(srcCurPos < srcLen) {
                if('\n' == src.charAt(srcCurPos)) {
                    line++;
                }

                srcCurPos++;
                if('\"' == src.charAt(srcCurPos)) {
                    break;
                }
            }
            if(srcCurPos >= srcLen || '\"' != src.charAt(srcCurPos)) {
                System.out.printf("\"String want to get other \" at the line = %d", line);
                System.exit(0);
            }
            lastPos = ++srcCurPos;

            token.setToken_type(TOKEN_TYPE.String);
            token.setStr(src.substring(firstPos, lastPos));

            return token;
        }

        if('\n' == c) {
            token.setToken_type(TOKEN_TYPE.Ent);
            token.setStr("\\n");
            line++;

            srcCurPos++;

            return token;
        }

        if('+' == c) {
            token.setToken_type(TOKEN_TYPE.Add);
            token.setStr("+");
            srcCurPos++;

            return token;
        }

        if('*' == c) {
            token.setToken_type(TOKEN_TYPE.Mul);
            token.setStr("*");
            srcCurPos++;

            return token;
        }

        if('/' == c) {
            token.setToken_type(TOKEN_TYPE.Div);
            token.setStr("/");
            srcCurPos++;

            return token;
        }

        if('-' == c) {
            token.setToken_type(TOKEN_TYPE.Sub);
            token.setStr("-");
            srcCurPos++;

            return token;
        }

        if(';' == c) {
            token.setToken_type(TOKEN_TYPE.Sem);
            token.setStr(";");
            srcCurPos++;

            return token;
        }

        if('=' == c) {
            token.setToken_type(TOKEN_TYPE.Assign);
            token.setStr("=");
            srcCurPos++;

            return token;
        }

        if('(' == c) {
            token.setToken_type(TOKEN_TYPE.LParen);
            token.setStr("(");
            srcCurPos++;

            return token;
        }

        if(')' == c) {
            token.setToken_type(TOKEN_TYPE.RParen);
            token.setStr(")");
            srcCurPos++;

            return token;
        }

        if('{' == c) {
            token.setToken_type(TOKEN_TYPE.LBrace);
            token.setStr("{");
            srcCurPos++;

            return token;
        }

        if('}' == c) {
            token.setToken_type(TOKEN_TYPE.RBrace);
            token.setStr("}");
            srcCurPos++;

            return token;
        }

        if(',' == c) {
            token.setToken_type(TOKEN_TYPE.COMMA);
            token.setStr(",");
            srcCurPos++;

            return token;
        }

        System.out.println("the unknown token\n");
        return null;
    }

    public int getLine() {
        return this.line;
    }
};

