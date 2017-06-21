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
    public void VariableTest() {
        String src = "string s = \"sss\";\n";
        src += "1 + 2;\n";
        src += "string s1 = s + 1;\n";
        src += "int n = 0;\n";
        src += "println(s);\n";
        src += "println(s1);\n";
        src += "println(n);\n";

        LEXER lexer = new LEXER(src);
        PARSER parser = new PARSER(lexer);

        List<ASTNODE> listAstNode = parser.parse();

        EVAL eval = new EVAL();
        eval.run(listAstNode);

        String s = eval.getProgramExeEndOutput();
        assert(s.equals("sss\n" + "sss1\n" + "0\n"));
    }

    @Test
    public void FuncTest() {
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

        String s = eval.getProgramExeEndOutput();
        assert(s.equals("sss1\n" + "hello world\n" + "3\n"));
    }

    @Test
    public void IfTest() {
        String src = "int w = -1;\n";
        src += "if(w) {println(-1);} elif(1) {println(1);} else {println(0);}\n";
        src += "w = w + 1;\n";
        src += "if(w) {println(-1);} elif(1) {println(1);} else {println(0);}\n";
        src += "w = w + 1;\n";
        src += "if(w) {println(w);} elif(1) {println(1);} else {println(0);}\n";
        src += "if(0) {println(-1);} elif(0) {println(1);} else {println(0);}\n";

        LEXER lexer = new LEXER(src);
        PARSER parser = new PARSER(lexer);

        List<ASTNODE> listAstNode = parser.parse();

        EVAL eval = new EVAL();
        eval.run(listAstNode);

        String s = eval.getProgramExeEndOutput();
        assert(s.equals("-1\n" + "1\n" + "1\n" + "0\n"));
    }

    @Test
    public void ReturnTest() {
        String src = "";
        src += "function addOne(s) { return s + \"1\"; s = s + 2;}\n";
        src += "string s = addOne(10);";
        src += "println(s);\n";

        src += "function addOne(s) { return s + 1; s = s + 2;}\n";
        src += "int s = addOne(10);";
        src += "println(s);\n";

        LEXER lexer = new LEXER(src);
        PARSER parser = new PARSER(lexer);

        List<ASTNODE> listAstNode = parser.parse();

        EVAL eval = new EVAL();
        eval.run(listAstNode);

        String s = eval.getProgramExeEndOutput();
        assert(s.equals("101\n" + "11\n"));
    }

    @Test
    public void WhileTest() {
        String src = "";
        src +=  "int w = -51;\n";
        src +=  "int s = 100;\n";
        src += "while(w < 100) { w = w + 1; s = s + 1;}\n";
        src += "println(w);\n";
        src += "println(s);\n";

        LEXER lexer = new LEXER(src);
        PARSER parser = new PARSER(lexer);

        List<ASTNODE> listAstNode = parser.parse();

        EVAL eval = new EVAL();
        eval.run(listAstNode);

        String s = eval.getProgramExeEndOutput();
        assert(s.equals("100\n" + "251\n"));
    }

    @Test
    public void RelationExpressionTest() {
        String src = "";
        src += "if(2 >= 1) {println(\"2 >= 1\");}\n";
        src += "if(2 > 1) {println(\"2 > 1\");}\n";
        src += "if(1 < 2) {println(\"1 < 2\");}\n";
        src += "if(1 <= 2) {println(\"1 <= 2\");}\n";
        src += "if(1 > 2) {println(\"No\");} else { println(\"Yes\");}\n";

        LEXER lexer = new LEXER(src);
        PARSER parser = new PARSER(lexer);

        List<ASTNODE> listAstNode = parser.parse();

        EVAL eval = new EVAL();
        eval.run(listAstNode);

        String s = eval.getProgramExeEndOutput();
        assert(s.equals("2 >= 1\n" + "2 > 1\n" + "1 < 2\n" + "1 <= 2\n" + "Yes\n"));
    }

    @Test
    public void ClosureTest() {
        String src = "";
        src += "function add(a) {\n" +
                    "int b = a + 1;\n" +
                    "function mul() {\n" +
                        "b = b + 1;\n" +
                        "println(b);\n" +
                    "}\n" +
                    "return mul;\n" +
                "}\n";
        src += "function w = add(100);\n";
        src += "w();\n";
        src += "w();\n";
        src += "function s = add(200);\n";
        src += "s();\n";
        src += "s();\n";

        LEXER lexer = new LEXER(src);
        PARSER parser = new PARSER(lexer);

        List<ASTNODE> listAstNode = parser.parse();

        EVAL eval = new EVAL();
        eval.run(listAstNode);

        String s = eval.getProgramExeEndOutput();
        assert(s.equals("102\n" + "103\n" + "202\n" + "203\n"));
    }
}
