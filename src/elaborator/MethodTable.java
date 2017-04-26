package elaborator;

import java.util.Enumeration;
import java.util.LinkedList;

import ast.Ast.Dec;
import ast.Ast.Type;
import util.Todo;

public class MethodTable {
    private java.util.Hashtable<String, Type.T> table;

    private void error(Dec.DecSingle dec){
        System.out.println("duplicated parameter: " + dec.id);
        System.exit(1);
    }

    public MethodTable() {
        this.table = new java.util.Hashtable<String, Type.T>();
    }

    // Duplication is not allowed
    public void put(LinkedList<Dec.T> formals,
                    LinkedList<Dec.T> locals) {
        for (Dec.T dec : formals) {
            Dec.DecSingle decc = (Dec.DecSingle) dec;
            if (this.table.get(decc.id) != null) {
                error(decc);
            }
            this.table.put(decc.id, decc.type);
        }

        for (Dec.T dec : locals) {
            Dec.DecSingle decc = (Dec.DecSingle) dec;
            if (this.table.get(decc.id) != null) {
                System.out.println("duplicated variable: " + decc.id);
                System.exit(1);
            }
            this.table.put(decc.id, decc.type);
        }

    }

    public void clear(){
        this.table.clear();
    }
    // return null for non-existing keys
    public Type.T get(String id) {
        return this.table.get(id);
    }

    public void dump() {
        Enumeration<Type.T> typeEnumeration = this.table.elements();
        while(typeEnumeration.hasMoreElements()){
            Type.T type = typeEnumeration.nextElement();
        }
    }

    @Override
    public String toString() {
        return this.table.toString();
    }
}
