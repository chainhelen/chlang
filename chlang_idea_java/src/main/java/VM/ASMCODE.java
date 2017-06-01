package VM;

import java.util.*;

/**
 * Created by chainhelen on 2017/5/15.
 */


public class ASMCODE {
    private List data = new ArrayList();
    private List text = new ArrayList();

    public void insertText(Object o) {
        this.text.add(o);
    }

    public void insertData(Object o) {
        this.text.add(o);
    }

    public int getDataLength() {
        return this.data.size();
    }

    public int getTextLength() {
        return this.text.size();
    }

    /**
     * Clear all.
     * the method clear all variables of class
     */
    public void clearAll() {
        data.clear();
        text.clear();
    }

}
