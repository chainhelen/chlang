package VM;

import PARSER.ASTNODE;
import PARSER.ASTNODE_TYPE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

/**
 * Created by chainhelen on 2017/5/15.
 */
public class VM {

    private Stack stack = new Stack();
    private List scope = new ArrayList();

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    public void runVM(ASMCODE asmCode) {
        if(null == asmCode) {
            logger.error("curVm has no any asmCode");
            System.exit(0);
        }
        while(true) {
            return ;
        }
    }

    /**
     * stack
     *
     * Push data stack.
     * @param address the address
     */
    public void pushDataStackSec(int address) {
        this.stack.push(address);
    }

    /**
     * stack
     *
     * Remove data stack sec.
     */
    public void removeDataStackSec() {
        this.stack.pop();
    }

    /**
     *  stack
     *
     * get the last n data
     */

    public Object getLastNDataStackSec(int n) {
        int size = this.stack.size();
        if(0 <= size + n) {
            return this.stack.get(size + n);
        }
        return null;
    }
}
