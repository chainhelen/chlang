package VM;

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

    public int getAddress() {
        return address;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public SYMBOL deepClone() {
        SYMBOL newSymbol = new SYMBOL();

        newSymbol.name = this.name;
        newSymbol.type = this.type;
        newSymbol.address = this.address;

        return newSymbol;
    }

    private String name;
    private SYMBOL_TYPE type;
    private int address;
}

public class SCOPE {
    private List<SYMBOL> symbols = new ArrayList<SYMBOL>();

    public SYMBOL getSymbolByName(String name) {
        if(null == symbols || 0 == symbols.size()) {
            return null;
        }
        Iterator it = symbols.iterator();
        while(it.hasNext()) {
            SYMBOL symbol = (SYMBOL)it.next();
            if(name == symbol.getName()) {
                return symbol;
            }
        }
        return null;
    }

    public void insertSymbol(SYMBOL symbol) {
        SYMBOL newSymbol = symbol.deepClone();
        this.symbols.add(newSymbol);
    }
}

