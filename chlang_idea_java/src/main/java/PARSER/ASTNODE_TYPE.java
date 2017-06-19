package PARSER;

import com.sun.org.apache.bcel.internal.classfile.Unknown;

import javax.crypto.spec.RC2ParameterSpec;

/**
 * Created by chainhelen on 2017/4/28.
 */
public enum ASTNODE_TYPE {
//token_type
    Identifier,
    Assign,
    Add,
    Sub,
    Mul,
    Div,
    String,
    Number,
    TString,
    TInt,

//definition_or_statment
//    : function_definition
//    | statement
    DefinitionOrStatement,

//function_definition
//    : Rw_Function IDENTIFIER LP parameter_`list RP block
    FunctionDefinition,

//parameter_list
//    : NULL
//    | IDENTIFIER
//    | IDENTIFIER COMMA parameter_list
    ParameterList,

//arugument_list
//    : NULL
//    | expression
//    | expression COMMA argument_list
    ArgumentList,

//statement_list
//    : NULL
//    | statement
//    | statement statemen_list
    StatementList,

//expression
//    : logical_or_expression   --  //考虑到程序简单性，先直接跳过or等等逻辑运算符
//    | TYPE(int | string) IDENTIFIER ASSIGN expression
//    | IDENTIFIER ASSIGN expression
//    | additive_expression ++  //直接使用add表达子式
    Expression,

//logical_or_expression
//    : logical_and_expression
//    | logical_or_expression LOGICAL_OR logical_and_expression
// need to write
    LogicalOrExpression,

//logical_and_expression
//    : equality_expression
//    | logical_and_expression LOGICAL_AND equality_expression
// need to write
    LogicalAndExpression,

//equality_expression
//    : relational_expression
//    | equality_expression EQ relation_expression
//    | equality_expression NE relation_expression
// need to write
    EqualityExpression,

//relational_expression
//    : additive_expression
//    | relation_expression GT additive_expression
//    | relation_expression GE additive_expression
//    | relation_expression LT additive_expression
//    | relation_expression LE additive_expression
    RelationExpression,

//additive_expression
//    : multiplicative_expression
//    | multiplicative_expression ADD additive_expresson
//    | multiplicative_expression SUB additive_expresson
    AddExpression,

//multiplicative_expression
//    : unary_expression
//    | unary_expression MUL  multiplicative_expression
//    | unary_expression DIV multiplicative_expression
//    | unary_expression MOD multiplicative_expression --
    MulExpression,

//unary_expression
//    : primary_expression
//    | SUB unary_expression
    UnaryExpression,

//primary_expression
//    : IDENTIFIER LP argument_list RP
//    | IDENTIFIER LP RP
//    | LP expression RP
//    | IDENTIFIER
//    | INT_LITERAL
//    | STRING_LITERAL
    PrimaryExpression,

//IfExpressionBlock
// | RW_If LP expression RP block
    IfExpressionBlock,

//ElifExpressionBlock
// | RW_Elif LP expression RP block
    ElifExpressionBlock,

//ElseExpressionBlock
// | RW_Else block
    ElseExpressionBlock,

//If_statement
//  |  IfExpressionBlock (ElifExpressionBlock)*
//  |  IfExpressionBlock (ElifExpressionBlock)* ElseExpressionBlock
    IfStatement,

//while_statement
//  |  RW_While LP expression RP Block
    WhileStatement,

//break_statement ++
// | RW_Break Sem
    BreakStatement,

//    | continue_statement ++
// | RW_Continue Sem
    ContinueStatement,

//Return_statement
// | RW_Return expression SEM
// don't support lambd, but can return function and finish closure
    ReturnStatement,

//statement
//    : expression SEM
//    | global_statement --
//    | if_statiement ++
//    | while_statement ++
//    | for_statement --
//    | return_statement ++
//    | break_statement ++
//    | continue_statement ++
    Statement,

//block
//    : LC statement_list RC
//    | LC RC
    Block,

    UnknownAstNode
}
