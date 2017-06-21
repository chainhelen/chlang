# chlang

interpreter, but just tool

[相关介绍看这里](http://blog.hacking.pub/2017/06/21/chlang-chu-ban/)

____

能够使用如下代码，进行运算
```
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
```
___

MIT
