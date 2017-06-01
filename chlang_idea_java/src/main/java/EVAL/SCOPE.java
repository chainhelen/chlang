package EVAL;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by chainhelen on 2017/5/18.
 */
enum SYMBOL_TYPE {
    Function, //0
    VariableStr, // 1
    VariableInt, // 2
    INT, // 系统中断

    UnknownType // 100
}

class SYMBOL {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SYMBOL_TYPE getType() {
        return type;
    }

    public void setType(SYMBOL_TYPE type) {
        this.type = type;
    }

    public void setType(Object o) {
        if(o.getClass().getName().equals("Integer"))  {
            this.type = SYMBOL_TYPE.VariableInt;
        } else if(o.getClass().getName().equals("String")){
            this.type = SYMBOL_TYPE.VariableStr;
        }
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public boolean checkType(Object o) {
        if(o.getClass() == java.lang.Integer.class && SYMBOL_TYPE.VariableInt == this.type) {
            return true;
        }
        if(o.getClass() == java.lang.String.class && SYMBOL_TYPE.VariableStr == this.type) {
            return true;
        }
        return false;
    }

    public SYMBOL deepClone() {
        SYMBOL newSymbol = new SYMBOL();

        newSymbol.name = this.name;
        newSymbol.type = this.type;
        newSymbol.value = this.value;

        return newSymbol;
    }

    public SYMBOL(String name, SYMBOL_TYPE type, Object value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }
    public SYMBOL() {
    }

    private String name;
    private SYMBOL_TYPE type;
    private Object value;
}

public class SCOPE {
    private List<SYMBOL> symbols = new ArrayList<SYMBOL>();

    public SYMBOL getSymbolByName(String name) {
        if(null == symbols || 0 == symbols.size()) {
            return null;
        }
        int index = symbols.size() - 1;
        while(index >= 0) {
            SYMBOL symbol = symbols.get(index);
            if(symbol.getName().equals(name)) {
                return symbol;
            }
            index--;
        }
        return null;
    }

    public void insertSymbol(SYMBOL symbol) {
        SYMBOL newSymbol = symbol.deepClone();
        this.symbols.add(newSymbol);
    }
}

