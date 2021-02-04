import java.util.ArrayList;
import java.util.HashMap;

public class SymbolTable {
    private HashMap<String, Tuple> classTable;
    private HashMap<String, Tuple> subroutineTable;
    private int StaticID = 0;
    private int FieldID = 0;
    private int VarID = 0;
    private int ArgID = 0;

    public SymbolTable() { // creates new symbol table
        classTable = new HashMap<>();
        subroutineTable = new HashMap<>();
    }

    public void startSubroutine() {
        subroutineTable = new HashMap<>();
        VarID = 0;
        ArgID = 0;
    }

    public void define(String name, String type, int kind) {

        if (classTable.containsKey(name) || subroutineTable.containsKey(name)) {
            System.out.println("This name is not valid, it's already exist.");
            return;
        }
        if (kind == Kind.STATIC) {
            classTable.put(name, new Tuple(name, type, kind, StaticID++));
        } else if (kind == Kind.FIELD) {
            classTable.put(name, new Tuple(name, type, kind, FieldID++));
        } else if (kind == Kind.VAR) {
            subroutineTable.put(name, new Tuple(name, type, kind, VarID++));
        } else if (kind == Kind.ARG) {
            subroutineTable.put(name, new Tuple(name, type, kind, ArgID++));
        }
    }

    /**
     * returns NONE if the name is not found.
     *
     * @param name
     * @return
     */
    public String TypeOf(String name) {
        if (classTable.containsKey(name)) {
            return classTable.get(name).getType();
        } else if (subroutineTable.containsKey(name)) {
            return subroutineTable.get(name).getType();
        }
        return "NONE";
    }

    /**
     * returns -1 if the name is not found.
     *
     * @param name
     * @return
     */
    public int KindOf(String name) {
        if (classTable.containsKey(name)) {
            return classTable.get(name).getKind();
        } else if (subroutineTable.containsKey(name)) {
            return subroutineTable.get(name).getKind();
        }
        return Kind.NONE;
    }

    /**
     * returns -1 if the name is not found.
     *
     * @param name
     * @return
     */
    public int IndexOf(String name) {
        if (classTable.containsKey(name)) {
            return classTable.get(name).getIndex();
        } else if (subroutineTable.containsKey(name)) {
            return subroutineTable.get(name).getIndex();
        }
        return -1;
    }
}
