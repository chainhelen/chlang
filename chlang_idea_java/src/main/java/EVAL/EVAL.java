package EVAL;

import PARSER.ASTNODE;
import PARSER.ASTNODE_TYPE;
import jdk.nashorn.internal.runtime.Scope;
import jdk.nashorn.internal.runtime.regexp.joni.constants.NodeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

/**
 * Created by chainhelen on 2017/5/21.
 */

class ScopeCtr {
    private List<SCOPE> scopeList;

    protected void initScopeList() {
        scopeList = new ArrayList<SCOPE>();
        scopeList.add(new SCOPE());
    }

    protected void addSymbolInCurScope(SYMBOL symbol) {
        int index = scopeList.size() - 1;
        if(index < 0) {
            return ;
        }
        SCOPE curScope = scopeList.get(index);
        curScope.insertSymbol(symbol);
    }

    protected void enterNewScope() {
        scopeList.add(new SCOPE());
    }

    protected SCOPE getCurScope() {
        int index = scopeList.size() - 1;
        if(index < 0) {
            return new SCOPE();
        }
        return scopeList.get(index);
    }

    protected SYMBOL getSymbolByName(String name) {
        int scopeIndex = scopeList.size() - 1;
        while(scopeIndex >= 0) {
            SCOPE curScope = scopeList.get(scopeIndex);
            SYMBOL symbol = curScope.getSymbolByName(name);
            if(null != symbol) {
                return symbol;
            }
            scopeIndex--;
        }
        return null;
    }

    protected void removeCurScope() {
        int index = scopeList.size() - 1;
        if(index >= 0) {
            scopeList.remove(index);
        }
    }

    protected void clearScopeList()  {
        while(true) {
            if(scopeList.size() == 0) {
                return ;
            }
            scopeList.remove(0);
        }
    }
}

public class EVAL {
    private ScopeCtr scopeCtr = new ScopeCtr();
    private Stack expResStack = new Stack();
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    private void recursionEvalAstNode(ASTNODE parentNode) {
        if(null == parentNode){
            System.exit(0);
        }

        ASTNODE_TYPE nodeType = parentNode.getAstNodeType();

        if(ASTNODE_TYPE.DefinitionOrStatement == nodeType) {
            List<ASTNODE> astNodeList = parentNode.getAllChildreNodeList();
            for(Iterator it = astNodeList.iterator();it.hasNext();) {
                recursionEvalAstNode((ASTNODE)it.next());
            }
        } else if(ASTNODE_TYPE.FunctionDefinition == nodeType) {

            List<ASTNODE> astNodeList = parentNode.getAllChildreNodeList();
            ASTNODE identifier = astNodeList.get(0);
            scopeCtr.addSymbolInCurScope(new SYMBOL(identifier.getValue().toString(),
                    SYMBOL_TYPE.Function,
                    parentNode));

        } else if(ASTNODE_TYPE.ParameterList == nodeType) {

            List<ASTNODE> astNodeList = parentNode.getAllChildreNodeList();
            for(int i = 0;i < astNodeList.size();i++) {
                ASTNODE parameter = astNodeList.get(i);
                scopeCtr.addSymbolInCurScope(new SYMBOL(parameter.getValue().toString(),
                        SYMBOL_TYPE.VariableStr,
                        this.expResStack.pop()));
            }

        } else if(ASTNODE_TYPE.ArgumentList == nodeType) {

            List<ASTNODE> astNodeList = parentNode.getAllChildreNodeList();
            for(int i = astNodeList.size() - 1;i >= 0;i--) {
                recursionEvalAstNode(astNodeList.get(i));
            }

        } else if(ASTNODE_TYPE.StatementList == nodeType) {

            List<ASTNODE> astNodeList = parentNode.getAllChildreNodeList();
            for(int i = 0;i < astNodeList.size();i++) {
                recursionEvalAstNode(astNodeList.get(i));
            }

        } else if(ASTNODE_TYPE.Expression == nodeType) {
             //expression
            //    : logical_or_expression   --
            //    | TYPE(int | string) IDENTIFIER ASSIGN expression
            //    | IDENTIFIER ASSIGN expression
            //    | additive_expression ++

            List<ASTNODE> astNodeList = parentNode.getAllChildreNodeList();
            if(astNodeList.size() <= 0) {
                System.out.printf("Expression astNodeList.size() <= 0");
                System.exit(0);
            }
            ASTNODE childrenNode = astNodeList.get(0);
            //    | TYPE(int | string) IDENTIFIER ASSIGN expression
            if(ASTNODE_TYPE.TInt == childrenNode.getAstNodeType() ||
                    ASTNODE_TYPE.TString == childrenNode.getAstNodeType()) {
                ASTNODE identifierNode = astNodeList.get(1);

                ASTNODE expressionNode = astNodeList.get(3);
                recursionEvalAstNode(expressionNode);
                Object expResult = this.expResStack.pop();

                SYMBOL symbol = new SYMBOL();
                symbol.setName(identifierNode.getValue().toString());
                symbol.setType(ASTNODE_TYPE.TInt == childrenNode.getAstNodeType() ? SYMBOL_TYPE.VariableInt : SYMBOL_TYPE.VariableStr);
                if(symbol.checkType(expResult)) {
                    symbol.setValue(expResult);
                } else {
                    System.out.printf("Expect type %s, but get %s by variable=%s",
                            symbol.getType().toString(),
                            expResult.getClass().toString(),
                            symbol.getName()
                    );
                    System.exit(0);
                }
                scopeCtr.addSymbolInCurScope(symbol);
            }else if(ASTNODE_TYPE.Identifier == childrenNode.getAstNodeType()){ //    | IDENTIFIER ASSIGN expression
                SYMBOL symbol = scopeCtr.getSymbolByName(childrenNode.getValue().toString());
                recursionEvalAstNode(astNodeList.get(2));
                Object obj = this.expResStack.pop();

                if(symbol.checkType(obj)) {
                    symbol.setValue(obj);
                } else {
                    System.out.printf("Expect type %s, but get %s by variable=%s",
                            symbol.getType().toString(),
                            obj.getClass().toString(),
                            symbol.getName()
                    );
                    System.exit(0);
                }

            } else if(ASTNODE_TYPE.AddExpression == childrenNode.getAstNodeType()) {  //logical_or_expression
                recursionEvalAstNode(childrenNode);
            } else {
                System.out.printf("Expect the first astNode is Identifier | logicalOrExperssion but get %s",
                        childrenNode.getAstNodeType().toString()
                );
                System.exit(0);
            }
        } else if(ASTNODE_TYPE.AddExpression == nodeType) {
            List<ASTNODE> astNodeList = parentNode.getAllChildreNodeList();
            recursionEvalAstNode(astNodeList.get(0));
            Object mulRes = this.expResStack.pop();
            Object res = mulRes;

            if(astNodeList.size() >= 3) {
                ASTNODE sign = astNodeList.get(1);
                recursionEvalAstNode(astNodeList.get(2));
                Object addRes = this.expResStack.pop();

                if(ASTNODE_TYPE.Add == sign.getAstNodeType()) {
                    // int "+" int
                    if(java.lang.Integer.class == mulRes.getClass()
                            && java.lang.Integer.class == addRes.getClass()) {
                        res = (int)mulRes + (int)addRes;
                    }
                    // int "+" string
                    if(java.lang.Integer.class == mulRes.getClass()
                            && java.lang.String.class == addRes.getClass()) {
                        res = "" + (int)mulRes + (String)addRes;
                    }
                    //string "+" string
                    if(java.lang.String.class == mulRes.getClass()
                            && java.lang.String.class == addRes.getClass()) {
                        res = (String)mulRes + (String)addRes;
                    }
                    // string "+" int
                    if(java.lang.String.class == mulRes.getClass()
                            && java.lang.Integer.class == addRes.getClass()) {
                        res = (String)mulRes + (int)addRes;
                    }
                }
                if(ASTNODE_TYPE.Sub == sign.getAstNodeType()) {
                    // int "-" int
                    if(java.lang.Integer.class == mulRes.getClass()
                            && java.lang.String.class == addRes.getClass()) {
                        res = (int)mulRes - (int)addRes;
                    }else {
                        System.out.println("string - int");
                        System.exit(0);
                    }
                }
            }
            this.expResStack.push(res);
        } else if(ASTNODE_TYPE.MulExpression == nodeType) {
            List<ASTNODE> astNodeList = parentNode.getAllChildreNodeList();

            recursionEvalAstNode(astNodeList.get(0));
            Object unaryExpRes = this.expResStack.pop();
            Object res = unaryExpRes;

            if(astNodeList.size() > 1) {
                ASTNODE sign = astNodeList.get(1);
                recursionEvalAstNode(astNodeList.get(2));
                Object mulExpRes = this.expResStack.pop();
                if(!(java.lang.Integer.class == unaryExpRes.getClass()) ||
                        !(java.lang.Integer.class == unaryExpRes.getClass())) {
                    System.out.printf("mul not ingeter * | /");
                    System.exit(0);
                }
                if(ASTNODE_TYPE.Mul == sign.getAstNodeType()) {
                    res = (int) unaryExpRes * (int) mulExpRes;
                } else if(ASTNODE_TYPE.Div == sign.getAstNodeType()) {
                    if (0 == (int) mulExpRes) {
                        System.out.printf("div 0 is not permit");
                        System.exit(0);
                    }
                    res = (int)unaryExpRes / (int)mulExpRes;
                }
            }

            this.expResStack.push(res);
        } else if(ASTNODE_TYPE.UnaryExpression == nodeType) {
            List<ASTNODE> astNodeList = parentNode.getAllChildreNodeList();
            if(1 == astNodeList.size()) {
                recursionEvalAstNode(astNodeList.get(0));
//                Object primaryExpRes = this.expResStack.pop();
//                Object res = primaryExpRes;
//                this.expResStack.push(res);
            } else if(2 == astNodeList.size()) {
                recursionEvalAstNode(astNodeList.get(1));
                Object unaryExpRes = this.expResStack.pop();
                Object res = unaryExpRes;
                if(java.lang.Integer.class == unaryExpRes.getClass()) {
                    res = 0 - (int)unaryExpRes;
                }else if(java.lang.Integer.class == unaryExpRes.getClass()) {
                    res = "0" + (String)unaryExpRes;
                } else {
                }
                this.expResStack.push(res);
            }
        } else if(ASTNODE_TYPE.PrimaryExpression == nodeType) {
            List<ASTNODE> astNodeList = parentNode.getAllChildreNodeList();

            //IDENTIFIER
            //IDENTIFIER LP argument_list RP
            //IDENTIFIER LP RP
            if(ASTNODE_TYPE.Identifier == astNodeList.get(0).getAstNodeType()) {
                ASTNODE identifierNode = astNodeList.get(0);
                SYMBOL symbol = scopeCtr.getSymbolByName(identifierNode.getValue().toString());
                if(null == symbol) {
                    System.out.printf("Unknown symbol " + identifierNode.getValue().toString());
                    System.exit(0);
                }
                if(SYMBOL_TYPE.VariableInt == symbol.getType() || SYMBOL_TYPE.VariableStr == symbol.getType()) {
                    this.expResStack.push(symbol.getValue());
                } else {
                    if (SYMBOL_TYPE.Function != symbol.getType() && SYMBOL_TYPE.INT != symbol.getType()) {
                        logger.error("Expect the Function/INT but get type " + symbol.getType().toString());
                        System.exit(0);
                    }

                    //argument_list, not only Function but INT
                    if (astNodeList.size() >= 2) {
                        ASTNODE argumentNodeList = astNodeList.get(1);
                        recursionEvalAstNode(argumentNodeList);
                    }

                    //Function
                    if (SYMBOL_TYPE.Function == symbol.getType()) {
                        List<ASTNODE> funcNodeList = ((ASTNODE) symbol.getValue()).getAllChildreNodeList();
                        ASTNODE parameterList = funcNodeList.get(1);
                        ASTNODE funcBody = funcNodeList.get(2);

                        scopeCtr.enterNewScope();
                        recursionEvalAstNode(parameterList);
                        recursionEvalAstNode(funcBody);
                        scopeCtr.removeCurScope();
                    }
                    if (SYMBOL_TYPE.INT == symbol.getType()) {
                        execIntFunction((int)symbol.getValue(), astNodeList.get(1).getAllChildreNodeList().size());
                    }
                }
            } else if(ASTNODE_TYPE.Expression == astNodeList.get(0).getAstNodeType()) { // LP expression RP
                recursionEvalAstNode(astNodeList.get(0));
            } else if(ASTNODE_TYPE.Number == astNodeList.get(0).getAstNodeType()) {
                this.expResStack.push(astNodeList.get(0).getValue());
            } else if(ASTNODE_TYPE.String == astNodeList.get(0).getAstNodeType()) {
                String res = (String)astNodeList.get(0).getValue();
                this.expResStack.push(res.substring(1, res.length() - 1));
            } else {
                logger.error("Error at primaryExpression , Not get any vaild AstNodeType");
                System.exit(0);
            }
        } else if(ASTNODE_TYPE.IfStatement == nodeType) {
            List<ASTNODE> astNodeList = parentNode.getAllChildreNodeList();
            int len = astNodeList.size();
            for(int i = 0;i < len;i++) {
                ASTNODE curAstNode = astNodeList.get(i);
                if (ASTNODE_TYPE.IfExpressionBlock == curAstNode.getAstNodeType()
                        || ASTNODE_TYPE.ElifExpressionBlock == curAstNode.getAstNodeType()) {
                    ASTNODE expressionAstNode = curAstNode.getFirstChildrenNode();
                    recursionEvalAstNode(expressionAstNode);
                    Object res = this.expResStack.pop();
                    if(!(res.getClass() == java.lang.Integer.class && 0 == (int)res)) {
                        ASTNODE blockAstNode = curAstNode.getLastChildrenNode();
                        recursionEvalAstNode(blockAstNode);
                        break;
                    }
                }
                if (ASTNODE_TYPE.ElseExpressionBlock == curAstNode.getAstNodeType()){
                    ASTNODE blockAstNode = curAstNode.getLastChildrenNode();
                    recursionEvalAstNode(blockAstNode);
                }
            }
        } else if(ASTNODE_TYPE.Statement == nodeType) {
            List<ASTNODE> astNodeList = parentNode.getAllChildreNodeList();
            ASTNODE theFirstAstNode = astNodeList.get(0);

            if(ASTNODE_TYPE.Expression == theFirstAstNode.getAstNodeType()) {
                recursionEvalAstNode(astNodeList.get(0));
            } else if(ASTNODE_TYPE.IfStatement == theFirstAstNode.getAstNodeType()) {
                recursionEvalAstNode(astNodeList.get(0));
            }

        } else if(ASTNODE_TYPE.Block == nodeType) {
            List<ASTNODE> astNodeList = parentNode.getAllChildreNodeList();
            scopeCtr.enterNewScope();
            recursionEvalAstNode(astNodeList.get(0));
            scopeCtr.removeCurScope();
        } else {
            System.exit(0);
        }
    }

    private void addIntFunction() {
        scopeCtr.addSymbolInCurScope(new SYMBOL("print", SYMBOL_TYPE.INT, 0));
        scopeCtr.addSymbolInCurScope(new SYMBOL("println", SYMBOL_TYPE.INT, 1));
    }
    private void execIntFunction(int index, int count) {
        //index : 0, print
        if(0 == index) {
            String s = "";
            for(int i = 0;i < count;i++) {
                s += this.expResStack.pop();
            }
            System.out.print(s);
            this.expResStack.push(count);
        }
        if(1 == index) {
             String s = "";
            for(int i = 0;i < count;i++) {
                s += this.expResStack.pop();
            }
            System.out.println(s);
            this.expResStack.push(count);
        }
    }

    public void init() {
        this.scopeCtr = new ScopeCtr();
        this.scopeCtr.initScopeList();
        addIntFunction();
        this.expResStack = new Stack();
    }

    public void run(ASTNODE rootNode) {
        if(null == rootNode) {
            return ;
        }
        init();
        recursionEvalAstNode(rootNode);
    }

    public void run(List<ASTNODE> rootNodeList) {
        if(null == rootNodeList) {
            return ;
        }
        init();
        for(int i = 0;i < rootNodeList.size();i++)
            recursionEvalAstNode(rootNodeList.get(i));
    }
}
