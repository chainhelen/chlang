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

        LEXER lexer = new LEXER(src);
        PARSER parser = new PARSER(lexer);

        List<ASTNODE> listAstNode = parser.parse();

        EVAL eval = new EVAL();
        eval.run(listAstNode);
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
    }

    @Test
    public void WhileTest() {
        String src = "";
        src +=  "int w = -51;\n";
        src +=  "int s = 100;\n";
        src += "while(w) { w = w + 1; s = s + 1;}\n";
        src += "println(w);\n";
        src += "println(s);\n";

        LEXER lexer = new LEXER(src);
        PARSER parser = new PARSER(lexer);

        List<ASTNODE> listAstNode = parser.parse();

        EVAL eval = new EVAL();
        eval.run(listAstNode);
    }
}
