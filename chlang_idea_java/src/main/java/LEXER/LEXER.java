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
};

class TOKENCOMPARATOR implements Comparator {
    public int compare(Object o1, Object o2) {
        TOKEN to1 = (TOKEN) o1;
        TOKEN to2 = (TOKEN) o2;

        int res = to2.getStr().compareTo(to1.getStr());
        if(0 == res) {
            return to2.getNumber() - to1.getNumber();
        }
        return res;
    }
}

public class LEXER{
    private int line = 0;
    private String src = "";
    private int srcCurPos = 0;

    private final static String reservedWords = "int string while if\0";
    private TreeSet<TOKEN> reservedWordTable = new TreeSet<TOKEN>(new TOKENCOMPARATOR());
    private TOKEN token = null;

    public TOKEN getToken() {
        return this.token;
    }

    public LEXER(String src) {
        this.src = src;

        for(TOKEN_TYPE token_type : TOKEN_TYPE.values()) {
            String name = token_type.toString();
            if(name.substring(0, 2).equals("RW")) {
                getNextToken();
                if(TOKEN_TYPE.Eof == token.getToken_type()) {
                    break;
                }
                reservedWordTable.add();
            }
        }
    }

    public void getNextToken() {
        int size = src.length();
        char c = ' ';

        if(srcCurPos >= size){
            token.setToken_type(TOKEN_TYPE.Eof);
            token.setStr("<Eof>\0");
            return;
        }

        c = src.charAt(srcCurPos);
        while(' ' == c) {
            srcCurPos++;
            if(srcCurPos >= size) {
                token.setToken_type(TOKEN_TYPE.Eof);
                token.setStr("<Eof>\0");
                return;
            }
            c = src.charAt(srcCurPos);
        }

        c = src.charAt(srcCurPos);
        if(Character.isLetter(c)) {

            int firstPos = srcCurPos;
            int lastPos = srcCurPos;

            while( srcCurPos < size &&
                    (Character.isLetter(src.charAt(srcCurPos)) || Character.isDigit(src.charAt(srcCurPos)) )){
                srcCurPos++;
            }
            lastPos = srcCurPos;
            token.setStr(src.substring(firstPos, lastPos));

            //reservedWordTable.contains()
        }
    }

    public static void main(String args[]) {
        //LEXER lexer = new LEXER("ww");

        String a = "aw";
        String b = "ayz";

        System.out.println(a.toString().compareTo(b.toString()));
    }
}
