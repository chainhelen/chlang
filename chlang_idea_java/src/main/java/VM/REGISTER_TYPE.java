package VM;

/**
 * Created by chainhelen on 2017/5/15.
 */
public enum REGISTER_TYPE {
//store next opcode
    Pc,

//pointer to top address of cur stack
    Sp,

//pointer to base address of cur stack
    Bp,

//store the result
    Ax,
    UnknownRegisterType
}
