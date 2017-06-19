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
            logger.error("the lexer not init in parser\n");
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
        return deepCopyAstNodeList(astNodeList);
    }

    private List<ASTNODE> deepCopyAstNodeList(List<ASTNODE> astNodeList) {
        List<ASTNODE> reAstNodeList = new ArrayList<ASTNODE>();
        {
            Iterator it = astNodeList.iterator();
            while(it.hasNext()) {
                ASTNODE tmpNode = (ASTNODE)it.next();
                tmpNode = deepCopyAstNode(tmpNode);
                reAstNodeList.add(tmpNode);
            }
        }
        return reAstNodeList;
    }

    private ASTNODE deepCopyAstNode(ASTNODE li) {
        ASTNODE res = new ASTNODE();

        res.setAstNodeType(li.getAstNodeType());
        res.setValue(li.getValue());

        {
            List<ASTNODE> astTmpNodeList = li.getAllChildreNodeList();
            Iterator it = astTmpNodeList.iterator();

            while(it.hasNext()) {
                ASTNODE curNode = (ASTNODE)it.next();
                ASTNODE childrenCurNode = deepCopyAstNode(curNode);
                res.insertChildrenNode(childrenCurNode);
            }
        }

        return res;
    }

// definition_or_statment
//    : function_definition
//    | statement
//    DefinitionOrStatement
    private ASTNODE definitionOrStatement() {
        ASTNODE astRootNode = new ASTNODE();

        astRootNode.setAstNodeType(ASTNODE_TYPE.DefinitionOrStatement);
        astRootNode.setValue("DefinitionOrStatement");

        if(TOKEN_TYPE.RW_Function == curTok.getToken_type()) {
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
        ASTNODE astRootNode = new ASTNODE();
        astRootNode.setAstNodeType(ASTNODE_TYPE.FunctionDefinition);
        astRootNode.setValue("FunctionDefinition");
        curTok = lexer.getNextToken(); // eat 'function'

        // IDENTIFIER
        {
            ASTNODE identifierAstNode = new ASTNODE();
            identifierAstNode.setAstNodeType(ASTNODE_TYPE.Identifier);
            identifierAstNode.setValue(curTok.getStr());
            astRootNode.insertChildrenNode(identifierAstNode);
            curTok = lexer.getNextToken();
        }

        // LP
        {
            if (TOKEN_TYPE.LParen != curTok.getToken_type()) {
                logger.error("functionDefinition expect the LP");
                logger.error("\tbut get the token_type " + curTok.getToken_type());
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
            ASTNODE argumentList = new ASTNODE();
            argumentList.setAstNodeType(ASTNODE_TYPE.ArgumentList);
            argumentList.setValue("ArgumentList");
            return argumentList;
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

                if(TOKEN_TYPE.RParen == curTok.getToken_type()) {
                    break;
                } else if(TOKEN_TYPE.COMMA == curTok.getToken_type()){
                    curTok = lexer.getNextToken();
                } else{
                    logger.error("argumentList expect Rparen | COMMA ");
                    logger.error("\tget the token_type " + curTok.getToken_type().toString() + "line " + lexer.getLine());
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
            statementListAstRootNode.insertChildrenNode(statementAstNode);
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
        } else {
            if(TOKEN_TYPE.RW_String == curTok.getToken_type()) {
                ASTNODE astStringNode = new ASTNODE();
                astStringNode.setAstNodeType(ASTNODE_TYPE.TString);
                astStringNode.setValue("string");
                expressionAstRootNode.insertChildrenNode(astStringNode);

                curTok = lexer.getNextToken(); // eat "string"
            }
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
                identifierAstNode.setValue(curTok.getStr());
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

//if_statement
//  |  IfExpressionBlock (ElifExpressionBlock)*
//  |  IfExpressionBlock (ElifExpressionBlock)* ElseExpressionBlock
    private ASTNODE ifStatement() {
        ASTNODE ifStatementAstNode = new ASTNODE();
        ifStatementAstNode.setAstNodeType(ASTNODE_TYPE.IfStatement);
        ifStatementAstNode.setValue("IfStatement");

        // IfExpressionBlock
        {
            ASTNODE ifExpressionBlock = new ASTNODE();
            ifExpressionBlock.setAstNodeType(ASTNODE_TYPE.IfExpressionBlock);
            ifExpressionBlock.setValue("IfExpressionBlock");

            curTok = lexer.getNextToken(); // eat RW_If
            if(TOKEN_TYPE.LParen != curTok.getToken_type()) { //check the LParen
                logger.error("IfExpressionBlock Expect the \"(\" ,  the type = " + TOKEN_TYPE.LParen.toString());
                logger.error("\tget the type = " + curTok.getToken_type());
                System.exit(0);
            }
            curTok = lexer.getNextToken(); // eat LParen

            ASTNODE expressionAstNode = expression();
            ifExpressionBlock.insertChildrenNode(expressionAstNode);

            if(TOKEN_TYPE.RParen != curTok.getToken_type()) { //check the RParen
                logger.error("IfExpressionBlock Expect the \")\" ,  the type = " + TOKEN_TYPE.RParen.toString());
                logger.error("\tget the type = " + curTok.getToken_type());
                System.exit(0);
            }
            curTok = lexer.getNextToken(); // eat RParen

             if(TOKEN_TYPE.LBrace != curTok.getToken_type()) { //block statement, check the LBrace
                logger.error("IfExpressionBlock Expect block,  the first token type = " + TOKEN_TYPE.LBrace.toString());
                logger.error("\tget the type = " + curTok.getToken_type());
                System.exit(0);
            }

            ASTNODE blockAstNode = block();
            ifExpressionBlock.insertChildrenNode(blockAstNode);

            ifStatementAstNode.insertChildrenNode(ifExpressionBlock);
        }

        //ElifExpressionBlock
        {
            while(TOKEN_TYPE.RW_Elif == curTok.getToken_type()) {
                ASTNODE elifExpressionBlock = new ASTNODE();
                elifExpressionBlock.setAstNodeType(ASTNODE_TYPE.ElifExpressionBlock);
                elifExpressionBlock.setValue("ElifExpressionBlock");

                curTok = lexer.getNextToken(); // eat RW_Elif
                if(TOKEN_TYPE.LParen != curTok.getToken_type()) { //check the LParen
                    logger.error("ElifExpressionBlock Expect the \"(\" ,  the type = " + TOKEN_TYPE.LParen.toString());
                    logger.error("\tget the type = " + curTok.getToken_type());
                    System.exit(0);
                }
                curTok = lexer.getNextToken(); // eat LParen

                ASTNODE expressionAstNode = expression();
                elifExpressionBlock.insertChildrenNode(expressionAstNode);
                if(TOKEN_TYPE.RParen != curTok.getToken_type()) { //check the RParen
                    logger.error("ElifExpressionBlock Expect the \")\" ,  the type = " + TOKEN_TYPE.RParen.toString());
                    logger.error("\tget the type = " + curTok.getToken_type());
                    System.exit(0);
                }
                curTok = lexer.getNextToken(); // eat RParen

                if(TOKEN_TYPE.LBrace != curTok.getToken_type()) { //block statement, check the LBrace
                    logger.error(" ElifExpressionBlock Expect block,  the first token type = " + TOKEN_TYPE.LBrace.toString());
                    logger.error("\tget the type = " + curTok.getToken_type());
                    System.exit(0);
                }

                ASTNODE blockAstNode = block();
                elifExpressionBlock.insertChildrenNode(blockAstNode);

                ifStatementAstNode.insertChildrenNode(elifExpressionBlock);
            }
        }

        {
            if(TOKEN_TYPE.RW_Else == curTok.getToken_type()) {
                ASTNODE elseExpressionBlock = new ASTNODE();
                elseExpressionBlock.setAstNodeType(ASTNODE_TYPE.ElseExpressionBlock);
                elseExpressionBlock.setValue("ElseExpressionBlock");

                curTok = lexer.getNextToken(); // eat RW_Else
                if(TOKEN_TYPE.LBrace != curTok.getToken_type()) { //block statement, check the LBrace
                    logger.error(" ElseExpressionBlock Expect block,  the first token type = " + TOKEN_TYPE.LBrace.toString());
                    logger.error("\tget the type = " + curTok.getToken_type());
                    System.exit(0);
                }

                ASTNODE blockAstNode = block();
                elseExpressionBlock.insertChildrenNode(blockAstNode);

                ifStatementAstNode.insertChildrenNode(elseExpressionBlock);
            }
        }

        return ifStatementAstNode;
    }

    private ASTNODE whileStatement() {
        ASTNODE whileStatementAstNode = new ASTNODE();
        whileStatementAstNode.setAstNodeType(ASTNODE_TYPE.WhileStatement);
        whileStatementAstNode.setValue("WhileStatement");

        curTok = lexer.getNextToken(); //eat "while"

        //check LP
        if(TOKEN_TYPE.LParen != curTok.getToken_type()) {
            logger.error(" WhileStatement Expect token type = " + TOKEN_TYPE.LParen);
            logger.error("\tbut get the type = " + curTok.getToken_type());
            System.exit(0);
        }
        curTok = lexer.getNextToken(); //eat LP

        //expression
        ASTNODE expressionAstNode = expression();
        whileStatementAstNode.insertChildrenNode(expressionAstNode);

        //check RP
        if(TOKEN_TYPE.RParen != curTok.getToken_type()) {
            logger.error(" WhileStatement Expect token type = " + TOKEN_TYPE.RParen);
            logger.error("\tbut get the type = " + curTok.getToken_type());
            System.exit(0);
        }
        curTok = lexer.getNextToken(); //eat RP

        //block
        ASTNODE blockAstNode = block();
        whileStatementAstNode.insertChildrenNode(blockAstNode);

        return whileStatementAstNode;
    }

    private ASTNODE breakStatement() {
        ASTNODE breakStatementAstNode = new ASTNODE();
        breakStatementAstNode.setAstNodeType(ASTNODE_TYPE.BreakStatement);
        breakStatementAstNode.setValue("BreakStatement");

        curTok = lexer.getNextToken(); //eat "break"
        //check Sem
        if(TOKEN_TYPE.Sem != curTok.getToken_type()) {
            logger.error("BreakStatement Expect token type = " + TOKEN_TYPE.Sem);
            logger.error("\tbut get the type = " + curTok.getToken_type());
            System.exit(0);
        }
        curTok = lexer.getNextToken(); // eat ";"

        return breakStatementAstNode;
    }

    private ASTNODE continueStatement() {
        ASTNODE continueStatementAstNode = new ASTNODE();
        continueStatementAstNode.setAstNodeType(ASTNODE_TYPE.ContinueStatement);
        continueStatementAstNode.setValue("ContinueStatement");

        curTok = lexer.getNextToken(); //eat "continue"
        //check Sem
        if(TOKEN_TYPE.Sem != curTok.getToken_type()) {
            logger.error("ContinueStatement Expect token type = " + TOKEN_TYPE.Sem);
            logger.error("\tbut get the type = " + curTok.getToken_type());
            System.exit(0);
        }
        curTok = lexer.getNextToken(); // eat ";"

        return continueStatementAstNode;
    }

    private ASTNODE returnStatement() {
        ASTNODE returnStatementAstNode = new ASTNODE();
        returnStatementAstNode.setAstNodeType(ASTNODE_TYPE.ReturnStatement);
        returnStatementAstNode.setValue("ReturnStatement");

        curTok = lexer.getNextToken(); //eat "return"

        ASTNODE expressionAstNode = expression();

        //check Sem
        if(TOKEN_TYPE.Sem != curTok.getToken_type()) {
            logger.error("ReturnStatement Expect token type = " + TOKEN_TYPE.Sem);
            logger.error("\tbut get the type = " + curTok.getToken_type());
            System.exit(0);
        }
        curTok = lexer.getNextToken(); // eat ";"

        returnStatementAstNode.insertChildrenNode(expressionAstNode);
        return returnStatementAstNode;
    }

//statement
//    : expression SEM
//    | global_statement --
//    | if_statiement ++
//    | for_statement --
//    | while_statement ++
//    | break_statement ++
//    | continue_statement ++
//    | return_statement ++

    private ASTNODE statement() {
        ASTNODE statementAstNode = new ASTNODE();
        statementAstNode.setAstNodeType(ASTNODE_TYPE.Statement);
        statementAstNode.setValue("Statement");

        //if_statement
        if(TOKEN_TYPE.RW_If == curTok.getToken_type())
        {
            ASTNODE ifStatementAstNode = ifStatement();
            statementAstNode.insertChildrenNode(ifStatementAstNode);
            return statementAstNode;
        }

        //while_statement
        if(TOKEN_TYPE.RW_While == curTok.getToken_type()) {
            ASTNODE whileStatementAstNode = whileStatement();
            statementAstNode.insertChildrenNode(whileStatementAstNode);
            return statementAstNode;
        }

        //break_statment
        if(TOKEN_TYPE.RW_Break == curTok.getToken_type()) {
            ASTNODE breakStatementAstNode = breakStatement();
            statementAstNode.insertChildrenNode(breakStatementAstNode);
            return statementAstNode;
        }

        //continue_statement
        if(TOKEN_TYPE.RW_Continue == curTok.getToken_type()) {
            ASTNODE continueStatementAstNode = continueStatement();
            statementAstNode.insertChildrenNode(continueStatementAstNode);
            return statementAstNode;
        }

        //return_statement
        if(TOKEN_TYPE.RW_Return == curTok.getToken_type()) {
            ASTNODE returnStatementAstNode = returnStatement();
            statementAstNode.insertChildrenNode(returnStatementAstNode);
            return statementAstNode;
        }

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
                logger.error("\tget the token_type => " + curTok.getToken_type().toString()
                        + " ; line = " + lexer.getLine()
                        + " ; columne = " + lexer.getCurColumn()
                );
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

                Element ele = new Element(astNode.getNodeTypeString());
                ele.addContent(new Element("value").setText(astNode.getValue().toString()));

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
        XMLOut.output(Doc, new FileOutputStream("./target/parser.output.xml"));
    }
}
