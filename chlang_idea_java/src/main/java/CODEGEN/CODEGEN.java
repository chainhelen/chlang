package CODEGEN;

import PARSER.ASTNODE;
import PARSER.ASTNODE_TYPE;
import VM.ASMCODE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;

import static VM.OPCODE_TYPE.*;

/**
 * Created by chainhelen on 2017/5/19.
 */
public class CODEGEN {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    private ASMCODE asmCode = new ASMCODE();

    private void generateChildrenCode(ASTNODE parentNode) {
        if(null == parentNode) {
            logger.error("generateChildrenCode get parentNode == null ");
            System.exit(0);
        }
        List<ASTNODE> childrenNodeList = parentNode.getAllChildreNodeList();
        Iterator it = childrenNodeList.iterator();
        while(it.hasNext()) {
            recursionCode((ASTNODE)(it.next()));
        }
    }

    private void recursionCode(ASTNODE parentNode) {
        if(null == parentNode) {
            return ;
        }

        String nodeType = parentNode.getNodeTypeString();

        if(ASTNODE_TYPE.DefinitionOrStatement.toString() == nodeType) {
            generateChildrenCode(parentNode);
        } else if(ASTNODE_TYPE.FunctionDefinition.toString() == parentNode.getNodeTypeString()) {

            List<ASTNODE> childrenNodeList = parentNode.getAllChildreNodeList();
            {
                // identifier
                ASTNODE identifier = childrenNodeList.get(1);
                //store function name in data
                this.asmCode.insertData(identifier.getValue());
                //store function identifier in scope
                this.asmCode.insertText(AddvarDef);
                this.asmCode.insertText(3); // count parameter
                this.asmCode.insertText(this.asmCode.getDataLength() - 1); // the address of name
                this.asmCode.insertText(0); // type 0 ==> Function
                this.asmCode.insertText(this.asmCode.getTextLength()); // the address of var
            }
            {
                //parameterList
                ASTNODE parameterList = childrenNodeList.get(2);
                recursionCode(parameterList);
            }
        } else if(ASTNODE_TYPE.ParameterList.toString() == parentNode.getNodeTypeString()) {
            this.asmCode.insertText(Entscope);
            int index = 0;
            List<ASTNODE> parameterList = parentNode.getAllChildreNodeList();
            for (ASTNODE parameter : parameterList) {
                this.asmCode.insertData(parameter.getValue().toString());

                this.asmCode.insertText(AddvarDef);
                this.asmCode.insertText(3); //count parameters
                this.asmCode.insertText(this.asmCode.getDataLength() - 1); // the address of name
                //need to write

                this.asmCode.insertText(0); // type 0 ==> Function
                this.asmCode.insertText(this.asmCode.getDataLength()); //the address of var

                index += 3;
            }
        } else if(ASTNODE_TYPE.ArgumentList.toString() == parentNode.getNodeTypeString()) {
        } else if(ASTNODE_TYPE.StatementList.toString() == parentNode.getNodeTypeString()) {
        } else if(ASTNODE_TYPE.Expression.toString() == parentNode.getNodeTypeString()) {
        } else if(ASTNODE_TYPE.AddExpression.toString() == parentNode.getNodeTypeString()) {
        } else if(ASTNODE_TYPE.MulExpression.toString() == parentNode.getNodeTypeString()) {
        } else if(ASTNODE_TYPE.UnaryExpression.toString() == parentNode.getNodeTypeString()) {
        } else if(ASTNODE_TYPE.PrimaryExpression.toString() == parentNode.getNodeTypeString()) {
        } else if(ASTNODE_TYPE.Statement.toString() == parentNode.getNodeTypeString()) {
        } else if(ASTNODE_TYPE.Block.toString() == parentNode.getNodeTypeString()) {
        } else {
            logger.error("curVm has no any asmCode");
            System.exit(0);
        }
    }

    public ASMCODE generateAsmCodeDependentVm(ASTNODE astRoot) {
        if(null == astRoot) {
            logger.error("curVm has no any astNode");
            System.exit(0);
        }
        this.asmCode.clearAll();

        recursionCode(astRoot);
        return this.asmCode;
    }
}
