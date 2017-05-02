
/**
 * Created by ch on 17-4-24.
 */
import org.junit.*;
import LEXER.*;

import java.util.*;

public class LEXER_TEST {
    @Test
    public void testLexer(){
        LEXER lexer = new LEXER("int s = 10;\nstring w = \"ws\"");
        List<TOKEN> expectTokenList = new LinkedList<TOKEN>();
        expectTokenList.add(new TOKEN(TOKEN_TYPE.RW_Int, 0, "int"));
        expectTokenList.add(new TOKEN(TOKEN_TYPE.Identifier, 0, "s"));
        expectTokenList.add(new TOKEN(TOKEN_TYPE.Assign, 0, "="));
        expectTokenList.add(new TOKEN(TOKEN_TYPE.Num, 10, ""));
        expectTokenList.add(new TOKEN(TOKEN_TYPE.Sem, 0, ";"));
        expectTokenList.add(new TOKEN(TOKEN_TYPE.RW_String, 0, "string"));
        expectTokenList.add(new TOKEN(TOKEN_TYPE.Identifier, 0, "w"));
        expectTokenList.add(new TOKEN(TOKEN_TYPE.Assign, 0, "="));
        expectTokenList.add(new TOKEN(TOKEN_TYPE.String, 0, "\"ws\""));
        expectTokenList.add(new TOKEN(TOKEN_TYPE.Eof, 0, "<Eof>"));

        List<TOKEN> generatorTokenList = new LinkedList<TOKEN>();

        while(true) {
            TOKEN token = lexer.getNextToken();
            generatorTokenList.add(token);
            if(TOKEN_TYPE.Eof == token.getToken_type()) {
                break;
            }
        }

        assert (generatorTokenList.size() == expectTokenList.size());
        Iterator it1 = expectTokenList.iterator();
        Iterator it2 = generatorTokenList.iterator();
        while(it1.hasNext()) {
            TOKEN t1 = (TOKEN)it1.next();
            TOKEN t2 = (TOKEN)it2.next();
            assert (t1.equals(t2));
        }
    }
}
