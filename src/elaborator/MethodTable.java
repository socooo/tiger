package elaborator;

import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Set;

import ast.Ast.Dec;
import ast.Ast.Type;

public class MethodTable {
	private java.util.Hashtable<String, CheckableType> table;

	public MethodTable() {
		this.table = new java.util.Hashtable<String, CheckableType>();
	}

	// Duplication is not allowed
	public void put(LinkedList<Dec.T> formals, LinkedList<Dec.T> locals) {
		for (Dec.T dec : formals) {
			Dec.DecSingle decc = (Dec.DecSingle) dec;
			if (this.table.get(decc.id) != null) {
				System.out.println("duplicated parameter: " + decc.id);
				System.exit(1);
			}
			CheckableType tmpFormals = new CheckableType(decc.id,decc.type,decc.isUsed,decc.lineNum);
			String test = decc.id;
			System.out.println(test);
			this.table.put(decc.id, tmpFormals);
		}

		for (Dec.T dec : locals) {
			Dec.DecSingle decc = (Dec.DecSingle) dec;
			if (this.table.get(decc.id) != null) {
				System.out.println("duplicated variable: " + decc.id);
				System.exit(1);
			}
			CheckableType tmpLocals = new CheckableType(decc.id,decc.type,decc.isUsed,decc.lineNum);
			this.table.put(decc.id, tmpLocals);
		}

	}

	// return null for non-existing keys
	public Type.T get(String id) {
		if(this.table.containsKey(id)){
			this.table.get(id).isUsed = true;
			CheckableType t = this.table.get(id);
			Type.T type = t.type;
			return type;
		}
		else return null;
	}

	public void dump() {
		Set<Entry<String, CheckableType>> methodTable = table.entrySet();
		if (methodTable.isEmpty()) {
			System.out.println("no variable in this method...");
		} else {
			System.out.println("variables as follows:");
			for (Entry<String, CheckableType> elem : methodTable) {
				System.out.println("\t" + elem.getKey() + " type:"
						+ elem.getValue().type.toString());
			}
		}
	}
	
	public LinkedList<CheckableType> findUnused(){
		Set<Entry<String,CheckableType>> all = table.entrySet();
		LinkedList<CheckableType> unused = new LinkedList<CheckableType>();
		for(Entry<String,CheckableType> elem : all){
			if(!(elem.getValue().isUsed)){
				unused.add(elem.getValue());
			}
		}
		return unused;
	}
	@Override
	public String toString() {
		return this.table.toString();
	}
}
