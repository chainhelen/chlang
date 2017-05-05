import PARSER.*;
import LEXER.LEXER;
import org.junit.Test;

import java.util.Iterator;
import java.util.List;

/**
 * Created by chainhelen on 2017/5/2.
 */
public class PARSER_TEST {
    @Test
    public void testParser0() {
        LEXER lexer = new LEXER("int s = 10;string b = \"sss\";");
        PARSER parser = new PARSER(lexer);

        List<ASTNODE> listAstNode = parser.parse();
        Iterator it = listAstNode.iterator();

        while(it.hasNext()) {
            ASTNODE astNode = (ASTNODE)it.next();
        }

        try {
            parser.BuildXmlDOC();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testParser1() {
        LEXER lexer = new LEXER("int s = 10;\nstring b = \"ssw\";");
        PARSER parser = new PARSER(lexer);

        List<ASTNODE> listAstNode = parser.parse();
        Iterator it = listAstNode.iterator();

        while(it.hasNext()) {
            ASTNODE astNode = (ASTNODE)it.next();
        }
    }
}
