package VM;

/**
 * Created by chainhelen on 2017/5/15.
 */
public enum OPCODE_TYPE {
//Mov => Imm, Lc, Li, Sc, Si
//Imm <num> : put the <num> into Ax
    Imm,
//Lc : put the address of String into Ax
    Lc,
//Li : put the address of Int into Ax
    Li,
//Sc : from c4(https://github.com/rswier/c4)
    Sc,
//Si : from c4
    Si,
//Push : push the data  into stack, push data
    Push,
// Jmp <addr> change pc to the data from pc that as address;
    Jmp,
// Jz jump if zero
    Jz,
// Jnz jump if not zero
    Jnz,
//Ent : make new call frame
    Ent,
//Adj, remove arguments from frame
    Adj,
//Lev : restore all frame and Pc
    Lev,
//Lea : load address for arguments
    Lea,
//Outscope
    Outscope,
    Entscope,
    AddvarDef,

    Div,
    Mul,
    Add,
    Sub,

    UnkonwnOpcodeType
}
