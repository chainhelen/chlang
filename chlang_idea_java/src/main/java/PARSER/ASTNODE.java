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
    private List<ASTNODE> astChidrenNodeList = new ArrayList<ASTNODE>();

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
        return this.astChidrenNodeList;
    }

    public boolean hasChildrenNode() {
        return this.astChidrenNodeList.size() > 0 ? true : false;
    }

    public ASTNODE getLastChildrenNode() {
        if(0 == this.astChidrenNodeList.size())
            return null;
        int size = this.astChidrenNodeList.size();
        return this.astChidrenNodeList.get(size - 1);
    }

    public ASTNODE getFirstChildrenNode() {
        if(0 == this.astChidrenNodeList.size())
            return null;
        return this.astChidrenNodeList.get(0);
    }

    public String getNodeTypeString() {
        return astNodeType.toString();
    }

    public void insertChildrenNode(ASTNODE node) {
        if(null != node) {
            this.astChidrenNodeList.add(node);
        }
    }

    public void insertChildrenNodeList(List list) {
        Iterator it = list.iterator();
        while(it.hasNext()) {
            ASTNODE parameter = (ASTNODE)it.next();
            this.insertChildrenNode(parameter);
        }
    }
}
