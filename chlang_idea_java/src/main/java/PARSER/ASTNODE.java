package PARSER;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by chainhelen on 2017/4/28.
 */


public class ASTNODE {
    private ASTNODE_TYPE astNodeType = ASTNODE_TYPE.UnknownAstNode;
    private Object value = new Object();
    private List<ASTNODE> astChildrenNodeList = new ArrayList<ASTNODE>();
    private ASTNODE parentNode = null;
    private ASTNODE nextNode = null;
    private ASTNODE prevNode = null;

    public ASTNODE getNextNode() {
        return nextNode;
    }

    private void setNextNode(ASTNODE nextNode) {
        this.nextNode = nextNode;
    }

    public ASTNODE getPrevNode() {
        return prevNode;
    }

    private void setPrevNode(ASTNODE prevNode) {
        this.prevNode = prevNode;
    }

    public ASTNODE getParentNode() {
        return parentNode;
    }

    private void setParentNode(ASTNODE parentNode) {
        this.parentNode = parentNode;
    }


    public void setValue(Object value) {
        this.value = value;
    }

    public Object getValue() {
        if(ASTNODE_TYPE.Number == this.astNodeType) {
            return (this.value);
        }
        if(ASTNODE_TYPE.UnknownAstNode == this.astNodeType) {
            return "<UnknkownAstNode>";
        }
        return this.value;
    }

    public ASTNODE_TYPE getAstNodeType() {
        return astNodeType;
    }

    public void setAstNodeType(ASTNODE_TYPE astNodeType) {
        this.astNodeType = astNodeType;
    }

    public List<ASTNODE>getAllChildreNodeList () {
        return this.astChildrenNodeList;
    }

    public boolean hasChildrenNode() {
        return this.astChildrenNodeList.size() > 0 ? true : false;
    }

    public ASTNODE getLastChildrenNode() {
        if(0 == this.astChildrenNodeList.size())
            return null;
        int size = this.astChildrenNodeList.size();
        return this.astChildrenNodeList.get(size - 1);
    }

    public ASTNODE getFirstChildrenNode() {
        if(0 == this.astChildrenNodeList.size())
            return null;
        return this.astChildrenNodeList.get(0);
    }

    public String getNodeTypeString() {
        return astNodeType.toString();
    }

    public void insertChildrenNode(ASTNODE curNode) {
        if(null != curNode) {
            if(true == hasChildrenNode()) {
                //handle lastNode : Next
                ASTNODE lastNode = getLastChildrenNode();
                lastNode.setNextNode(curNode);
                //handle curNode : Next Prev
                curNode.setNextNode(null);
                curNode.setPrevNode(lastNode);
            }
            //handle curNode : parent
            curNode.setParentNode(this);

            this.astChildrenNodeList.add(curNode);
        }
    }

    /*
    public void insertChildrenNodeList(List list) {
        Iterator it = list.iterator();
        while(it.hasNext()) {
            ASTNODE parameter = (ASTNODE)it.next();
            this.insertChildrenNode(parameter);
        }
    }*/
}
