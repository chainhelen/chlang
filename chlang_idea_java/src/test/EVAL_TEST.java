import PARSER.*;
import LEXER.*;
import EVAL.*;
import org.junit.Test;

import java.util.Iterator;
import java.util.List;

/**
 * Created by chainhelen on 2017/5/22.
 */
public class EVAL_TEST {
    @Test
    public void EvalTest() {
        String src = "string s = \"sss\";\n";
        src += "1 + 2;\n";
        src += "string s1 = s + 1;\n";
        src += "int n = 0;\n";
        src += "println(s1);\n";
        src += "function link(ni, hao) {println(ni + hao);}\n";
        src += "link(\"hello \", \"world\");\n";
        src += "function sum(a, b) {println(a + b);}\n";
        src += "sum(1, 2);\n";

        LEXER lexer = new LEXER(src);
        PARSER parser = new PARSER(lexer);

        List<ASTNODE> listAstNode = parser.parse();

        EVAL eval = new EVAL();
        eval.run(listAstNode);
    }
}
