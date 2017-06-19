package LEXER;

/**
 * Created by chainhelen on 2017/4/28.
 */
public class TOKEN {
    private TOKEN_TYPE token_type = TOKEN_TYPE.UnkonwnToken;
    private int number = 0;
    private String str = "";

    @Override
    public boolean equals(Object anObject) {
        if (this == anObject) {
            return true;
        }
        if (anObject instanceof TOKEN) {
            TOKEN token = (TOKEN)anObject;
            if(token.getToken_type() != this.getToken_type()) {
                return false;
            }
            if(token.getNumber() != this.getNumber()) {
                return false;
            }
            if(false == token.getStr().equals(this.getStr())) {
                return false;
            }
        }
        return true;
    }

    public TOKEN() {
        this.token_type = TOKEN_TYPE.UnkonwnToken;
        this.number = 0;
        this.str = "";
    }

    public TOKEN(TOKEN_TYPE token_type, int number, String str) {
        this.token_type = token_type;
        this.number = number;
        this.str = str;
    }

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
                TOKEN_TYPE.RW_Break == token.token_type ||
                TOKEN_TYPE.RW_Continue == token.token_type ||
                TOKEN_TYPE.RW_If == token.token_type ||
                TOKEN_TYPE.RW_Elif == token.token_type ||
                TOKEN_TYPE.RW_Else == token.token_type ||
                TOKEN_TYPE.RW_Return == token.token_type ||
                TOKEN_TYPE.RW_Function == token.token_type ||

                TOKEN_TYPE.Mul == token.token_type ||
                TOKEN_TYPE.Add == token.token_type ||
                TOKEN_TYPE.Sub == token.token_type ||
                TOKEN_TYPE.Div == token.token_type ||
                TOKEN_TYPE.Ent == token.token_type ||
                TOKEN_TYPE.Sem == token.token_type ||
                TOKEN_TYPE.Assign == token.token_type ||
                TOKEN_TYPE.Eq == token.token_type ||
                TOKEN_TYPE.Ne == token.token_type ||
                TOKEN_TYPE.Gt == token.token_type ||
                TOKEN_TYPE.Ge == token.token_type ||
                TOKEN_TYPE.Lt == token.token_type ||
                TOKEN_TYPE.Le == token.token_type ||

                TOKEN_TYPE.LParen == token.token_type ||
                TOKEN_TYPE.RParen == token.token_type ||
                TOKEN_TYPE.LBrace == token.token_type ||
                TOKEN_TYPE.RBrace == token.token_type ||

                TOKEN_TYPE.String == token.token_type ||
                TOKEN_TYPE.Eof == token.token_type)

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
