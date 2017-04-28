package LEXER;

import java.util.*;

/**
 * Created by chainhelen on 2017/4/23.
 */

enum TOKEN_TYPE {
    Identifier,
    RW_Int,
    RW_String,
    RW_While,
    RW_If,

    Mul,
    Add,
    Sub,
    Div,
    Ent,
    Sem,
    Equ,

    LParen, // "("
    RParen, // ")"
    LBrace, // "{"
    RBrace, // "}"
    Num,
    String,
    Eof,
    Function,
    UnkonwnToken
};

class TOKEN {
    private TOKEN_TYPE token_type = TOKEN_TYPE.UnkonwnToken;
    private int number = 0;
    private String str = "";

    public TOKEN_TYPE getToken_type() {
        return token_type;
    }
    public void setToken_type(TOKEN_TYPE token_type) {
        this.token_type = token_type;
    }

    public int getNumber() {
        return number;
    }
    public void setNumber(int number) {
        this.number = number;
    }

    public String getStr() {
        return str;
    }
    public void setStr(String str) {
        this.str = str;
    }


    public void priToken()
    {
        TOKEN token = this;
        if (TOKEN_TYPE.Identifier == token.token_type ||

                TOKEN_TYPE.RW_Int == token.token_type ||
                TOKEN_TYPE.RW_String == token.token_type ||
                TOKEN_TYPE.RW_While == token.token_type ||
                TOKEN_TYPE.RW_If == token.token_type ||

                TOKEN_TYPE.Mul == token.token_type ||
                TOKEN_TYPE.Add == token.token_type ||
                TOKEN_TYPE.Sub == token.token_type ||
                TOKEN_TYPE.Div == token.token_type ||
                TOKEN_TYPE.Ent == token.token_type ||
                TOKEN_TYPE.Sem == token.token_type ||
                TOKEN_TYPE.Equ == token.token_type ||

                TOKEN_TYPE.LParen == token.token_type ||
                TOKEN_TYPE.RParen == token.token_type ||
                TOKEN_TYPE.LBrace == token.token_type ||
                TOKEN_TYPE.RBrace == token.token_type ||

                TOKEN_TYPE.String == token.token_type ||
                TOKEN_TYPE.Eof == token.token_type ||
                TOKEN_TYPE.Function == token.token_type)
        {
            System.out.printf("The token : type = \"%s\", value = \"%s\"\n", token.getToken_type().name(), token.getStr());
        }
        else if (TOKEN_TYPE.Num == token.token_type)
        {
            System.out.printf("The token : type = \"%s\", value = \"%d\"\n", token.getToken_type().name(), token.getNumber());
        }
        else
        {
            System.out.println("The token : type = ???, value = ???\n");
        }
    }
};



public class LEXER{
    private int line = 0;
    private int srcCurPos = 0;
    private String src = "";

    private final static String reservedWords = "int string while if\0";
    private Map<String, TOKEN> reservedWordTable = new TreeMap<String, TOKEN>();

    private LEXER(String src) {
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

    public TOKEN getNextToken() {
        int srcLen = src.length();
        TOKEN token = new TOKEN();
        char c;

        if(srcCurPos >= srcLen){
            token.setToken_type(TOKEN_TYPE.Eof);
            token.setStr("<Eof>\0");
            return token;
        }

        c = src.charAt(srcCurPos);
        while(' ' == c) {
            srcCurPos++;
            if(srcCurPos > srcLen) {
                token.setToken_type(TOKEN_TYPE.Eof);
                token.setStr("<Eof>\0");
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
            token.setToken_type(TOKEN_TYPE.Equ);
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

        System.out.println("the unknown token\n");
        return null;
    }

    public static void main(String args[]) {
        LEXER lexer = new LEXER("int s = 10;string w = \"ws\"");
        while(true) {
            TOKEN token = lexer.getNextToken();
            token.priToken();

            if(TOKEN_TYPE.Eof == token.getToken_type()) {
                break;
            }
        }
        String a =  "wwwa";
//        StringBuilder sb = new StringBuilder ( "" );
//        for(int i = 0;i < a.length();i++) {
//            sb.append(a.charAt(i));
//        }
//        String b = sb.toString();
        String b = new String(a);
//        b = a;

        System.out.println(a.hashCode());
        System.out.println(b.hashCode());
        if(a == b) {
            System.out.println("==");
        } else {
            System.out.println("!=");
        }
    }
}

