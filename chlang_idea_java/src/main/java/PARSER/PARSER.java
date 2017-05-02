package PARSER;

import LEXER.*;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by chainhelen on 2017/4/28.
 */

public class PARSER {
    private  LEXER lexer;
    private List<ASTNODE> astNodeList;
    private TOKEN curTok;
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    public void setLexer(LEXER lexer) {
        this.lexer = lexer;
    }
    public PARSER() {
    }
    public PARSER(LEXER lexer) {
        setLexer(lexer);
    }

    public List<ASTNODE> parse() {
        if(null == lexer) {
            System.out.println("the lexer not init in parser\n");
            System.exit(0);
        }
        this.astNodeList = new ArrayList<ASTNODE>();

        curTok = lexer.getNextToken();
        TOKEN_TYPE token_type = curTok.getToken_type();
        while(curTok.getToken_type() != TOKEN_TYPE.Eof) {
            ASTNODE definitionOrStatementAstNode = definitionOrStatement();
            astNodeList.add(definitionOrStatementAstNode);
        }
//        copy list, need
//        {
//            List<ASTNODE> astRetList = new ArrayList<ASTNODE>();
//            Iterator it = astNodeList.iterator();
//            while(it.hasNext()) {
//                ASTNODE tmpNode = (ASTNODE)it.next();
//            }
//            return this.astNodeList;
//        }
        return this.astNodeList;
    }

// definition_or_statment
//    : function_definition
//    | statement
//    DefinitionOrStatement
    private ASTNODE definitionOrStatement() {
        ASTNODE astRootNode = new ASTNODE();

        astRootNode.setAstNodeType(ASTNODE_TYPE.DefinitionOrStatement);
        astRootNode.setValue("DefinitionOrStatement");

        if(TOKEN_TYPE.Function == curTok.getToken_type()) {
            ASTNODE functionDefinitionNode = functionDefinition();
            astRootNode.insertChildrenNode(functionDefinitionNode);
        } else {
            ASTNODE statementNode = statement();
            astRootNode.insertChildrenNode(statementNode);
        }

        return astRootNode;
    }

//function_definition
//    : FUNCTION IDENTIFIER LP parameter_list RP block
    private ASTNODE functionDefinition() {
        // rootAST
        curTok = lexer.getNextToken(); // eat 'function'
        ASTNODE astRootNode = new ASTNODE();
        astRootNode.setAstNodeType(ASTNODE_TYPE.FunctionDefinition);
        astRootNode.setValue("FunctionDefinition");
        curTok = lexer.getNextToken(); // eat 'function'


        // IDENTIFIER
        {
            ASTNODE identifierAstNode = new ASTNODE();
            identifierAstNode.setAstNodeType(ASTNODE_TYPE.Identifier);
            identifierAstNode.setValue(curTok.getStr());
            curTok = lexer.getNextToken();
        }

        // LP
        {
            if (TOKEN_TYPE.LParen != curTok.getToken_type()) {
                logger.error("functionDefinition expect the LP");
                logger.error("but get the token_type" + curTok.getToken_type());
                System.exit(0);
            }
            curTok = lexer.getNextToken();
        }

        // parameter_list
        {
            ASTNODE parameterList = parameterList();
            astRootNode.insertChildrenNode(parameterList);
        }

        //RP
        {
            if (TOKEN_TYPE.RParen != curTok.getToken_type()) {
                logger.error("functionDefinition expect the LP");
                logger.error("but get the token_type " + curTok.getToken_type().toString() + " line " + lexer.getLine());
                System.exit(0);
            }
            curTok = lexer.getNextToken();
        }

        //block
        {
            ASTNODE blockAstNode = block();
            astRootNode.insertChildrenNode(blockAstNode);
        }

        return astRootNode;
    }

//parameter_list
//    : NULL
//    | IDENTIFIER
//    | parameter_list COMMA IDENTIFIER
    private ASTNODE parameterList() {
        if(TOKEN_TYPE.RParen == curTok.getToken_type()) {
            return null;
        }

        ASTNODE parameterListAstRootNode = new ASTNODE();
        parameterListAstRootNode.setAstNodeType(ASTNODE_TYPE.ParameterList);
        parameterListAstRootNode.setValue("ParameterList");

        if(TOKEN_TYPE.Identifier == curTok.getToken_type()) {
            while(true) {

                if(TOKEN_TYPE.Identifier != curTok.getToken_type()) {
                    logger.error("parameterList expect identifiert");
                    logger.error("get the token_type " + curTok.getToken_type().toString() + " line " + lexer.getLine());
                    System.exit(0);
                }

                ASTNODE parameterNode = new ASTNODE();
                parameterNode.setAstNodeType(ASTNODE_TYPE.Identifier);
                parameterNode.setValue(curTok.getStr());
                parameterListAstRootNode.insertChildrenNode(parameterNode);

                curTok = lexer.getNextToken();
                if(TOKEN_TYPE.RParen == curTok.getToken_type()) {
                    break;
                } else if(TOKEN_TYPE.COMMA == curTok.getToken_type()) {
                    curTok = lexer.getNextToken(); // eac ','
                } else {
                    logger.error("parameterList expect Rparen | COMMA ");
                    logger.error("get the token_type " + curTok.getToken_type().toString() + " line " + lexer.getLine());
                    System.exit(0);
                }
            }
        }

        return parameterListAstRootNode;
    }

//arugument_list
//    : NULL
//    | expression
//    | expression COMMA argument_list
    private ASTNODE argumentList() {
        // NULL
        if(TOKEN_TYPE.RParen == curTok.getToken_type()) {
            return (new ASTNODE());
        }

        //expression
        //expression COMMA argumnt_list
        ASTNODE argumentList = new ASTNODE();
        argumentList.setAstNodeType(ASTNODE_TYPE.ArgumentList);
        argumentList.setValue("ArgumentList");

        {
            while(true) {
                ASTNODE expressionNode = expression();
                argumentList.insertChildrenNode(expressionNode);

                if(TOKEN_TYPE.RBrace == curTok.getToken_type()) {
                    break;
                } else if(TOKEN_TYPE.COMMA == curTok.getToken_type()){
                    curTok = lexer.getNextToken();
                } else{
                    logger.error("argumentList expect Rparen | COMMA ");
                    logger.error("get the token_type " + curTok.getToken_type().toString() + "line " + lexer.getLine());
                    System.exit(0);
                }
            }
        }

        return argumentList;
    }
//statement_list
//    : NULL
//    | statement
//    | statement statemen_list
    private ASTNODE statementList() {
        ASTNODE statementListAstRootNode = new ASTNODE();
        statementListAstRootNode.setAstNodeType(ASTNODE_TYPE.StatementList);
        statementListAstRootNode.setValue("StatementList");

        while(TOKEN_TYPE.RBrace != curTok.getToken_type()) {
            ASTNODE statementAstNode = statement();
            statementAstNode.insertChildrenNode(statementAstNode);
        }
        return statementListAstRootNode;
    }

 //expression
//    : logical_or_expression   --
//    | TYPE(int | string) IDENTIFIER ASSIGN expression
//    | IDENTIFIER ASSIGN expression
//    | additive_expression ++
    private ASTNODE expression() {
        ASTNODE expressionAstRootNode = new ASTNODE();
        expressionAstRootNode.setAstNodeType(ASTNODE_TYPE.Expression);
        expressionAstRootNode.setValue("expression");

        //need to write
        {
        }

        // String | Int
        if(TOKEN_TYPE.RW_Int == curTok.getToken_type()) {
            ASTNODE astIntNode = new ASTNODE();
            astIntNode.setAstNodeType(ASTNODE_TYPE.TInt);
            astIntNode.setValue("int");
            expressionAstRootNode.insertChildrenNode(astIntNode);

            curTok = lexer.getNextToken(); // eat "int"
        } else if(TOKEN_TYPE.RW_String == curTok.getToken_type()) {
            ASTNODE astStringNode = new ASTNODE();
            astStringNode.setAstNodeType(ASTNODE_TYPE.TString);
            astStringNode.setValue("string");
            expressionAstRootNode.insertChildrenNode(astStringNode);

            curTok = lexer.getNextToken(); // eat "string"
        }

        TOKEN lookHeadToken = lexer.lookHead();
        // IDENTIFIER ASSIGN expression
        if(TOKEN_TYPE.Assign == lookHeadToken.getToken_type()){
            //IDENTIFIER
            if(TOKEN_TYPE.Identifier != curTok.getToken_type()) {
                logger.error("expression ");
                logger.error("get the token_type " + curTok.getToken_type().toString() + " line " + lexer.getLine());
                System.exit(0);
            }
            ASTNODE identifierAstNode = new ASTNODE();
            identifierAstNode.setAstNodeType(ASTNODE_TYPE.Identifier);
            identifierAstNode.setValue(curTok.getStr());
            expressionAstRootNode.insertChildrenNode(identifierAstNode);
            curTok = lexer.getNextToken();

            //ASSIGN
            if(TOKEN_TYPE.Assign != curTok.getToken_type()) {
                logger.error("expression ");
                logger.error("get the token_type " + curTok.getToken_type().toString() + " line " + lexer.getLine());
                System.exit(0);
            }
            ASTNODE assignAstNode = new ASTNODE();
            assignAstNode.setAstNodeType(ASTNODE_TYPE.Assign);
            assignAstNode.setValue("=");
            expressionAstRootNode.insertChildrenNode(assignAstNode);
            curTok = lexer.getNextToken(); // eat '='

            //expression
            ASTNODE lastExpressionAstNode = expression();
            expressionAstRootNode.insertChildrenNode(lastExpressionAstNode);

            return expressionAstRootNode;
        } else { // additive_expression ++
            ASTNODE addExpressionAstNode = additiveExpression();
            expressionAstRootNode.insertChildrenNode(addExpressionAstNode);
        }
        return expressionAstRootNode;
    }

//additive_expression
//    : multiplicative_expression
//    | multiplicative_expression ADD additive_expresson
//    | multiplicative_expression SUB additive_expresson
    private ASTNODE additiveExpression() {
        ASTNODE addAstRootNode = new ASTNODE();
        addAstRootNode.setAstNodeType(ASTNODE_TYPE.AddExpression);
        addAstRootNode.setValue("AddExpression");

        // multiplicative_expression
        {
            ASTNODE mulExpresisonAstNode = multiplicativeExpression();
            addAstRootNode.insertChildrenNode(mulExpresisonAstNode);
        }

        if(TOKEN_TYPE.Add == curTok.getToken_type() || TOKEN_TYPE.Sub == curTok.getToken_type()) {
            if(TOKEN_TYPE.Add == curTok.getToken_type()) {
                ASTNODE addOperNode = new ASTNODE();
                addOperNode.setAstNodeType(ASTNODE_TYPE.Add);
                addOperNode.setValue("+");
                addAstRootNode.insertChildrenNode(addOperNode);
            }
            if(TOKEN_TYPE.Sub == curTok.getToken_type()) {
                ASTNODE subOperNode = new ASTNODE();
                subOperNode.setAstNodeType(ASTNODE_TYPE.Sub);
                subOperNode.setValue("-");
                addAstRootNode.insertChildrenNode(subOperNode);
            }
            curTok = lexer.getNextToken();// eat '+' or '-'

            ASTNODE addAstNode = additiveExpression();
            addAstRootNode.insertChildrenNode(addAstNode);
        }

        return addAstRootNode;
    }

//multiplicative_expression
//    : unary_expression
//    | unary_expression MUL  multiplicative_expression
//    | unary_expression DIV multiplicative_expression
//    | unary_expression MOD multiplicative_expression --
    private ASTNODE multiplicativeExpression() {
        ASTNODE mulAstRootNode = new ASTNODE();
        mulAstRootNode.setAstNodeType(ASTNODE_TYPE.MulExpression);
        mulAstRootNode.setValue("MulExpression");

        //unary_expression
        {
            ASTNODE unaryExpressionAstNode =  unaryExpression();
            mulAstRootNode.insertChildrenNode(unaryExpressionAstNode);
        }

        if(TOKEN_TYPE.Mul == curTok.getToken_type()|| TOKEN_TYPE.Div == curTok.getToken_type()) {
            if(TOKEN_TYPE.Mul == curTok.getToken_type()) {
                ASTNODE mulOperNode = new ASTNODE();
                mulOperNode.setAstNodeType(ASTNODE_TYPE.Mul);
                mulOperNode.setValue("*");
                mulAstRootNode.insertChildrenNode(mulOperNode);
            }
            if(TOKEN_TYPE.Div == curTok.getToken_type()) {
                ASTNODE divOperNode = new ASTNODE();
                divOperNode.setAstNodeType(ASTNODE_TYPE.Div);
                divOperNode.setValue("/");
                mulAstRootNode.insertChildrenNode(divOperNode);
            }
            curTok = lexer.getNextToken();// eat '*' or '/'

            ASTNODE mulAstNode = multiplicativeExpression();
            mulAstRootNode.insertChildrenNode(mulAstNode);
        }

        return mulAstRootNode;
    }

//unary_expression
//    : primary_expression
//    | SUB unary_expression
//need to write
    private ASTNODE unaryExpression() {
        ASTNODE unaryExpressionAstRootNode = new ASTNODE();
        unaryExpressionAstRootNode.setAstNodeType(ASTNODE_TYPE.UnaryExpression);
        unaryExpressionAstRootNode.setValue("UnaryExpression");

        //SUB unary_expression
        if(TOKEN_TYPE.Sub == curTok.getToken_type())
        {
            //SUB
            ASTNODE subOperNode = new ASTNODE();
            subOperNode.setAstNodeType(ASTNODE_TYPE.Sub);
            subOperNode.setValue("-");
            unaryExpressionAstRootNode.insertChildrenNode(subOperNode);
            curTok = lexer.getNextToken(); // eat '-'

            // unary_expression
            ASTNODE unaryExpressionAstNode = unaryExpression();
            unaryExpressionAstRootNode.insertChildrenNode(unaryExpressionAstNode);

        } else { // primary_expression
            ASTNODE primaryExpressionAstNode = primaryExpression();
            unaryExpressionAstRootNode.insertChildrenNode(primaryExpressionAstNode);
        }

        return unaryExpressionAstRootNode;
    }

//primary_expression
//    : IDENTIFIER LP argument_list RP
//    | IDENTIFIER LP RP
//    | LP expression RP
//    | IDENTIFIER
//    | INT_LITERAL
//    | STRING_LITERAL
    private ASTNODE primaryExpression() {
        ASTNODE primaryExpressionAstRootNode = new ASTNODE();
        primaryExpressionAstRootNode.setAstNodeType(ASTNODE_TYPE.PrimaryExpression);
        primaryExpressionAstRootNode.setValue("PrimaryExpression");

        //STRING_LITERAL
        {
            if(TOKEN_TYPE.String == curTok.getToken_type()) {
                ASTNODE stringAstNode = new ASTNODE();
                stringAstNode.setValue(curTok.getStr());
                stringAstNode.setAstNodeType(ASTNODE_TYPE.String);
                primaryExpressionAstRootNode.insertChildrenNode(stringAstNode);

                curTok = lexer.getNextToken();
                return primaryExpressionAstRootNode;
            }
        }

        //INT_LITERAL
        {
            if(TOKEN_TYPE.Num == curTok.getToken_type()) {
                ASTNODE intAstNode = new ASTNODE();
                intAstNode.setValue(curTok.getNumber());
                intAstNode.setAstNodeType(ASTNODE_TYPE.Number);
                primaryExpressionAstRootNode.insertChildrenNode(intAstNode);

                curTok = lexer.getNextToken();
                return primaryExpressionAstRootNode;
            }
        }

        //IDENTIFIER
        //IDENTIFIER LP argument_list RP
        //IDENTIFIER LP RP
        {
            if(TOKEN_TYPE.Identifier == curTok.getToken_type()) {
                //IDENTIFIER
                ASTNODE identifierAstNode = new ASTNODE();
                identifierAstNode.setAstNodeType(ASTNODE_TYPE.Identifier);
                identifierAstNode.setValue("Identifier");
                primaryExpressionAstRootNode.insertChildrenNode(identifierAstNode);

                //if next is LP
                curTok = lexer.getNextToken();
                if(TOKEN_TYPE.LParen == curTok.getToken_type()) {
                    curTok = lexer.getNextToken(); //eat '('

                    ASTNODE argumentListNode = argumentList();
                    primaryExpressionAstRootNode.insertChildrenNode(argumentListNode);

                    curTok = lexer.getNextToken(); //eat ')'
                }
                return primaryExpressionAstRootNode;
            }
        }

        //LP expression RP
        {
            if(TOKEN_TYPE.LParen == curTok.getToken_type()) {
                curTok = lexer.getNextToken(); // eat '('
                ASTNODE expressionAstNode = expression();
                primaryExpressionAstRootNode.insertChildrenNode(expressionAstNode);

                curTok = lexer.getNextToken(); // eat ')'
            }
            return primaryExpressionAstRootNode;
        }

    }

//statement
//    : expression SEM
//    | global_statement --
//    | if_statiement --
//    | while_statement --
//    | for_statement --
//    | return_statement --
//    | break_statement --
//    | continue_statement --
    private ASTNODE statement() {
        ASTNODE statementAstNode = new ASTNODE();
        statementAstNode.setAstNodeType(ASTNODE_TYPE.Statement);
        statementAstNode.setValue("Statement");

        // expression SEM
        {
            //expression
            ASTNODE expressionAstNode = expression();
            expressionAstNode.setAstNodeType(ASTNODE_TYPE.Expression);
            expressionAstNode.setValue("Expression");
            statementAstNode.insertChildrenNode(expressionAstNode);

            // SEM
            if(TOKEN_TYPE.Sem != curTok.getToken_type()) {
                logger.error("statement Sem expect token_type " + TOKEN_TYPE.Sem.toString());
                logger.error("get the token_type " + curTok.getToken_type().toString() + " line " + lexer.getLine());
                System.exit(0);
            }
            curTok = lexer.getNextToken();

            return statementAstNode;
        }
    }

//block
//    : LC statement_list RC
//    | LC RC
    private ASTNODE block() {
        ASTNODE blockAstNode = new ASTNODE();
        blockAstNode.setAstNodeType(ASTNODE_TYPE.Block);
        blockAstNode.setValue("block");

        if(TOKEN_TYPE.LBrace != curTok.getToken_type()) {
            logger.error("block expect LBrace, but get type = " + curTok.getStr());
            logger.error("get the token_type " + curTok.getToken_type().toString() + " line " + lexer.getLine());
            System.exit(0);
        }
        curTok = lexer.getNextToken(); // eat '{'

        ASTNODE statementListAstNode = statementList();
        blockAstNode.insertChildrenNode(statementListAstNode);

        curTok = lexer.getNextToken(); // eat '}'

        return blockAstNode;
    }

    private void recursionXmlNode(List<ASTNODE> astNodeList, Element root) {
        if(null != astNodeList && 0 != astNodeList.size()) {
            Iterator it = astNodeList.iterator();
            while(it.hasNext()) {
                ASTNODE astNode = (ASTNODE)it.next();

                Element ele = new Element(astNode.getNodeType());
                ele.addContent(new Element("value").setText(astNode.getValue()));

                List<ASTNODE> childrenNodeList = astNode.getAllChildreNodeList();
                if(null != childrenNodeList && 0 != childrenNodeList.size()) {
                    recursionXmlNode(childrenNodeList, ele);
                }
                root.addContent(ele);
            }
        }
    }

    public void BuildXmlDOC() throws IOException, JDOMException{
        //create dom and root
        Element root = new Element("parser").setAttribute("clang", "output");
        Document Doc = new Document(root);

        if(null != astNodeList && 0 != astNodeList.size()) {
            recursionXmlNode(astNodeList, root);
        }

        //save
        Format format = Format.getPrettyFormat();
        XMLOutputter XMLOut = new XMLOutputter(format);
        XMLOut.output(Doc, new FileOutputStream("./parser.xml"));
    }
}
