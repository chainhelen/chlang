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

enum CtrFlowFlag {
    RETURN,
    BREAK,
    CONTINUE,
    NEXT
}

class ScopeNode {
    public SCOPE getScopeValue() {
        return scopeValue;
    }

    public void setScopeValue(SCOPE scopeValue) {
        this.scopeValue = scopeValue;
    }

    private SCOPE scopeValue;
    private List<ScopeNode> scopeNodeChildren;
    private ScopeNode parentScopeNode;

    public ScopeNode() {
        scopeValue = new SCOPE();
        scopeNodeChildren = new ArrayList<ScopeNode>();
        parentScopeNode = null;
    }

    public ScopeNode insertChildrenScopNode(SCOPE scope) {
        ScopeNode scopeNode = new ScopeNode();
        scopeNode.scopeValue = scope;
        return insertChildrenScopNode(scopeNode);
    }
    public ScopeNode insertChildrenScopNode(ScopeNode scopeNode) {
        if(null == scopeNodeChildren || 0 == scopeNodeChildren.size()) {
            scopeNodeChildren.add(scopeNode);
        }
        scopeNode.parentScopeNode = this;
        return  scopeNode;
    }
    public void insertSymbol(SYMBOL symbol) {
        SCOPE curScope = this.getScopeValue();
        curScope.insertSymbol(symbol);
    }
    public ScopeNode getParentScopeNode() {
        return this.parentScopeNode;
    }
    public SYMBOL getSymbolByNameFromScopeNode(String name) {
        SCOPE curScope = this.getScopeValue();
        SYMBOL symbol = curScope.getSymbolByName(name);
        return symbol;
    }
}

class ScopeCtr {
    private ScopeNode scopeRootNode;
    private ScopeNode curScopeNode;
    private Stack<Object> scopeNodeStack = new Stack<Object>();

    public ScopeNode getCurScopeNode() {
        return curScopeNode;
    }

    protected void initScopeList() {
        scopeRootNode = new ScopeNode();
        curScopeNode = scopeRootNode;
    }

    protected void addSymbolInCurScope(SYMBOL symbol) {
        if(null == scopeRootNode) {
            return;
        }
        curScopeNode.insertSymbol(symbol);
    }

    protected void enterNewScope() {
        curScopeNode = curScopeNode.insertChildrenScopNode(new ScopeNode());
    }

//    protected SCOPE getCurScope() {
//        if(null == curScopeNode) {
//            System.out.println("null == curScope");
//            System.exit(0);
//        }
//        SCOPE curScope = curScopeNode.getScopeValue();
//        return curScope;
//    }

    protected SYMBOL getSymbolByName(String name) {
        ScopeNode scopeNode = curScopeNode;
        SYMBOL symbol =  null;

        while(null != scopeNode) {
            symbol = scopeNode.getSymbolByNameFromScopeNode(name);
            if(null != symbol) {
                return symbol;
            }
            scopeNode = scopeNode.getParentScopeNode();
        }
        return symbol;
    }

//    protected void removeCurScope() {
//        int index = scopeList.size() - 1;
//        if(index >= 0) {
//            scopeList.remove(index);
//        }
//    }

    protected void backTraceCurScope() {
        curScopeNode = curScopeNode.getParentScopeNode();
    }

//    protected void clearScopeList()  {
//        while(true) {
//            if(scopeList.size() == 0) {
//                return ;
//            }
//            scopeList.remove(0);
//        }
//    }

    public void storeCurScopeNode(Object newScope) {
        if(null != newScope) {
            scopeNodeStack.push(curScopeNode);
            curScopeNode = (ScopeNode)newScope;
        }
    }

    public void restoreCurScopeNode() {
        curScopeNode = (ScopeNode)scopeNodeStack.pop();
    }
}

public class EVAL {
    private ScopeCtr scopeCtr = new ScopeCtr();
    private Stack expResStack = new Stack();
    private CtrFlowFlag ctrFlowFlag = CtrFlowFlag.NEXT;
    private String outPutString = null;
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    private void recursionEvalAstNode(ASTNODE parentNode) {
        if(null == parentNode){
            System.exit(0);
        }

        //control flow
        //return
        if(CtrFlowFlag.RETURN == ctrFlowFlag) {
            return;
        }

        //control flow
        //break
        if(CtrFlowFlag.BREAK == ctrFlowFlag) {
            return;
        }

        //control flow
        //continue
        if(CtrFlowFlag.CONTINUE == ctrFlowFlag) {
            return;
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

            if(ASTNODE_TYPE.ParameterList == astNodeList.get(1).getAstNodeType()) {
                SYMBOL symbol = new SYMBOL(identifier.getValue().toString(),
                        SYMBOL_TYPE.Function,
                        parentNode, scopeCtr.getCurScopeNode());
                scopeCtr.addSymbolInCurScope(symbol);
            } else if(ASTNODE_TYPE.Assign == astNodeList.get(1).getAstNodeType()) {
                ASTNODE expressionAstNode = astNodeList.get(2);
                recursionEvalAstNode(expressionAstNode);
                Object resObj = this.expResStack.pop();
                if(resObj.getClass() == SYMBOL.class){
                    SYMBOL expressionSymbol = (SYMBOL)resObj;
                    SYMBOL symbol = new SYMBOL(identifier.getValue().toString(),
                            SYMBOL_TYPE.Function,
                            expressionSymbol.getValue(), expressionSymbol.getScope());
                    scopeCtr.addSymbolInCurScope(symbol);
                } else {
                    System.out.printf("ASTNODE_TYPE FunctionDefinition Expect the type of result from stack is SYMBOL");
                    System.out.printf("\t but get the type = " + resObj.getClass());
                    System.exit(0);
                }
            } else {
                System.out.printf("ASTNODE_TYPE FunctionDefinition Expect Parameter || Assign");
                System.out.printf("\t but get the ASTNODE_TYPE = " + astNodeList.get(1).getAstNodeType());
                System.exit(0);
            }

        } else if(ASTNODE_TYPE.ParameterList == nodeType) {

            List<ASTNODE> astNodeList = parentNode.getAllChildreNodeList();
            for(int i = 0;i < astNodeList.size();i++) {
                ASTNODE parameter = astNodeList.get(i);
                Object resObj = this.expResStack.pop();
                if(java.lang.Integer.class == resObj.getClass()) {
                    SYMBOL symbol = new SYMBOL(parameter.getValue().toString(),
                            SYMBOL_TYPE.VariableInt,
                            (int)resObj);
                    scopeCtr.addSymbolInCurScope(symbol);
                }
                if(java.lang.String.class == resObj.getClass()) {
                    SYMBOL symbol = new SYMBOL(parameter.getValue().toString(),
                            SYMBOL_TYPE.VariableStr,
                            (String)resObj);
                    scopeCtr.addSymbolInCurScope(symbol);
                }
                if(SYMBOL.class == resObj.getClass()) {
                    SYMBOL symbol = new SYMBOL(parameter.getValue().toString(),
                            SYMBOL_TYPE.Function,
                            resObj);
                    scopeCtr.addSymbolInCurScope(symbol);
                }
            }

        } else if(ASTNODE_TYPE.ArgumentList == nodeType) {

            List<ASTNODE> astNodeList = parentNode.getAllChildreNodeList();
            List res = new ArrayList();
            for(int i = astNodeList.size() - 1;i >= 0;i--) {
                recursionEvalAstNode(astNodeList.get(i));
                res.add(this.expResStack.pop());
            }
            for(int i = 0;i < res.size();i++) {
                this.expResStack.push(res.get(i));
            }

        } else if(ASTNODE_TYPE.StatementList == nodeType) {

            List<ASTNODE> astNodeList = parentNode.getAllChildreNodeList();
            for(int i = 0;i < astNodeList.size();i++) {
                recursionEvalAstNode(astNodeList.get(i));
            }

        } else if(ASTNODE_TYPE.Expression == nodeType) {
            //expression
            //    : logical_or_expression   --  //考虑到程序简单性，先直接跳过or等等逻辑运算符
            //    | TYPE(int | string) IDENTIFIER ASSIGN expression
            //    | IDENTIFIER ASSIGN expression
            //    | additive_expression --  //为了添加关系运算符, relation 比 additive more primitive
            //    | equality_expression ++

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

            } else if(ASTNODE_TYPE.EqualityExpression == childrenNode.getAstNodeType()) {
                recursionEvalAstNode(childrenNode);
            } else {
                System.out.printf("Expect the first astNode is Identifier | logicalOrExperssion but get %s",
                        childrenNode.getAstNodeType().toString()
                );
                System.exit(0);
            }
        } else if(ASTNODE_TYPE.EqualityExpression == nodeType) {
            //equality_expression
            //    : relational_expression
            //    | relation_expression EQ equality_expression
            //    | relation_expression NE equality_expression
            List<ASTNODE> astNodeList = parentNode.getAllChildreNodeList();
            recursionEvalAstNode(astNodeList.get(0));
            Object relRes = this.expResStack.pop();
            Object res = relRes;

            if(astNodeList.size() >= 3) {
                recursionEvalAstNode(astNodeList.get(2));
                Object equRes = this.expResStack.pop();

                if(java.lang.Integer.class == relRes.getClass()
                        && java.lang.Integer.class == equRes.getClass()) { // int int
                    if((int)relRes == (int)equRes) {
                        res = 1;
                    } else {
                        res = 0;
                    }
                } else if(java.lang.Integer.class == relRes.getClass()
                        && java.lang.String.class == equRes.getClass()) {  // int string
                    res = 0;
                } else if(java.lang.String.class == relRes.getClass()
                        && java.lang.Integer.class == equRes.getClass()) { // string int
                    res = 0;
                } else if(java.lang.String.class == equRes.getClass()
                        && java.lang.String.class == relRes.getClass()) { // string string
                    if(((String)equRes).equals((String)relRes)) {
                        res = 1;
                    } else {
                        res = 0;
                    }
                } else {
                    res = 0;
                }

                if(ASTNODE_TYPE.Eq == astNodeList.get(1).getAstNodeType()) {
                    res = res;
                } else if(ASTNODE_TYPE.Ne == astNodeList.get(1).getAstNodeType()){
                    res = -1 * (int)res;
                }
            }

            this.expResStack.push(res);
        } else if(ASTNODE_TYPE.RelationExpression == nodeType) {
            //relational_expression
            //    : additive_expression
            //    | additive_expression GT relation_expression
            //    | additive_expression GE relation_expression
            //    | additive_expression LT relation_expression
            //    | additive_expression LE relation_expression
            List<ASTNODE> astNodeList = parentNode.getAllChildreNodeList();
            recursionEvalAstNode(astNodeList.get(0));
            Object addRes = this.expResStack.pop();
            Object res = addRes;

            if(astNodeList.size() >= 3) {
                recursionEvalAstNode(astNodeList.get(2));
                Object relRes = this.expResStack.pop();

                ASTNODE operAstNode = astNodeList.get(1);
                if(java.lang.Integer.class != addRes.getClass() ||
                        java.lang.Integer.class != relRes.getClass()) {
                    logger.error("Cound not Support the Compare between " + addRes.getClass() + " " + relRes.getClass());
                        System.exit(0);
                }

                if(ASTNODE_TYPE.Ge == operAstNode.getAstNodeType()) {
                    res = (int)addRes >= (int)relRes ? 1 : 0;
                }

                if(ASTNODE_TYPE.Gt == operAstNode.getAstNodeType()) {
                    res = (int)addRes > (int)relRes ? 1 : 0;
                }

                if(ASTNODE_TYPE.Le == operAstNode.getAstNodeType()) {
                    res = (int)addRes <= (int)relRes ? 1 : 0;
                }

                if(ASTNODE_TYPE.Lt == operAstNode.getAstNodeType()) {
                    res = (int)addRes < (int)relRes ? 1 : 0;
                }
            }

            this.expResStack.push(res);
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
                }else if(java.lang.String.class == unaryExpRes.getClass()) {
                    res = "0" + (String)unaryExpRes;
                } else {
                }
                this.expResStack.push(res);
            }
        } else if(ASTNODE_TYPE.PrimaryExpression == nodeType) {
            List<ASTNODE> astNodeList = parentNode.getAllChildreNodeList();

            //IDENTIFIER
            //IDENTIFIER LP argument_list RP
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

                        //Function call
                        if (SYMBOL_TYPE.Function == symbol.getType()) {
                            List<ASTNODE> funcNodeList = ((ASTNODE) symbol.getValue()).getAllChildreNodeList();
                            ASTNODE parameterList = funcNodeList.get(1);
                            ASTNODE funcBody = funcNodeList.get(2);

                            scopeCtr.storeCurScopeNode(symbol.getScope());
                            scopeCtr.enterNewScope();
                            recursionEvalAstNode(parameterList);
                            recursionEvalAstNode(funcBody);
                            ctrFlowFlag = CtrFlowFlag.NEXT;
                            scopeCtr.backTraceCurScope();
                            scopeCtr.restoreCurScopeNode();
                        }
                        if (SYMBOL_TYPE.INT == symbol.getType()) {
                            execIntFunction((int)symbol.getValue(), astNodeList.get(1).getAllChildreNodeList().size());
                        }
                    } else {
                        this.expResStack.push(symbol);
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
        } else if(ASTNODE_TYPE.WhileStatement == nodeType) {
            List<ASTNODE> astNodeList = parentNode.getAllChildreNodeList();
            while(true) {
                ASTNODE expressionAstNode = astNodeList.get(0);
                ASTNODE blockAstNode = astNodeList.get(1);
                if(CtrFlowFlag.BREAK == ctrFlowFlag) {
                    ctrFlowFlag = CtrFlowFlag.NEXT;
                    break;
                }
                if(CtrFlowFlag.CONTINUE == ctrFlowFlag) {
                    ctrFlowFlag = CtrFlowFlag.NEXT;
                }
                recursionEvalAstNode(expressionAstNode);
                Object res = this.expResStack.pop();
                if (!(res.getClass() == java.lang.Integer.class && 0 == (int) res)) {
                    recursionEvalAstNode(blockAstNode);
                } else {
                    break;
                }
            }
        } else if(ASTNODE_TYPE.BreakStatement == nodeType) {
            ctrFlowFlag = CtrFlowFlag.BREAK;
        } else if(ASTNODE_TYPE.ContinueStatement == nodeType) {
            ctrFlowFlag = CtrFlowFlag.CONTINUE;
        } else if(ASTNODE_TYPE.ReturnStatement == nodeType) {
            ASTNODE expressionAstNode  = parentNode.getFirstChildrenNode();
            recursionEvalAstNode(expressionAstNode);
            //check the path to ancestor include functionStatement
            ctrFlowFlag = CtrFlowFlag.RETURN;
        } else if(ASTNODE_TYPE.Statement == nodeType) {
            List<ASTNODE> astNodeList = parentNode.getAllChildreNodeList();
            ASTNODE theFirstAstNode = astNodeList.get(0);

            if(ASTNODE_TYPE.Expression == theFirstAstNode.getAstNodeType()) {
                recursionEvalAstNode(astNodeList.get(0));
            } else if(ASTNODE_TYPE.IfStatement == theFirstAstNode.getAstNodeType()) {
                recursionEvalAstNode(astNodeList.get(0));
            } else if(ASTNODE_TYPE.WhileStatement == theFirstAstNode.getAstNodeType()) {
                recursionEvalAstNode(astNodeList.get(0));
            } else if(ASTNODE_TYPE.BreakStatement == theFirstAstNode.getAstNodeType()) {
                recursionEvalAstNode(astNodeList.get(0));
            } else if(ASTNODE_TYPE.ContinueStatement == theFirstAstNode.getAstNodeType()) {
                recursionEvalAstNode(astNodeList.get(0));
            } else if(ASTNODE_TYPE.ReturnStatement == theFirstAstNode.getAstNodeType()) {
                recursionEvalAstNode(astNodeList.get(0));
            }
        } else if(ASTNODE_TYPE.Block == nodeType) {
            List<ASTNODE> astNodeList = parentNode.getAllChildreNodeList();
            scopeCtr.enterNewScope();
            recursionEvalAstNode(astNodeList.get(0));
            scopeCtr.backTraceCurScope();
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
            outPutString = null == outPutString ? s : outPutString + s;
            this.expResStack.push(count);
        }
        if(1 == index) {
             String s = "";
            for(int i = 0;i < count;i++) {
                s += this.expResStack.pop();
            }
            System.out.println(s);
            outPutString = null == outPutString ? (s + "\n") : outPutString + (s + "\n");
            this.expResStack.push(count);
        }
    }

    public void init() {
        this.scopeCtr = new ScopeCtr();
        this.scopeCtr.initScopeList();
        addIntFunction();
        this.expResStack = new Stack();
    }

    private void run(ASTNODE rootNode) {
        if(null == rootNode) {
            return ;
        }
        recursionEvalAstNode(rootNode);
    }

    public void run(List<ASTNODE> rootNodeList) {
        if(null == rootNodeList) {
            return ;
        }
        init();
        for(int i = 0;i < rootNodeList.size();i++)
            run(rootNodeList.get(i));
    }

    public String getProgramExeEndOutput(){
        return this.outPutString;
    }
}
