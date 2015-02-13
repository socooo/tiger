package elaborator;

import java.util.Map.Entry;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Set;

import ast.Ast.Dec;
import ast.Ast.Type;
import ast.PrettyPrintVisitor;

public class ClassTable {
	// map each class name (a string), to the class bindings.
	private java.util.Hashtable<String, ClassBinding> table;

	public ClassTable() {
		this.table = new java.util.Hashtable<String, ClassBinding>();
	}

	// Duplication is not allowed
	public void put(String c, ClassBinding cb) {
		if (this.table.get(c) != null) {
			System.out.println("duplicated class: " + c);
			System.exit(1);
		}
		this.table.put(c, cb);
	}

	// put a field into this table
	// Duplication is not allowed
	public void put(String c, String id, Type.T type, boolean isUsed) {
		ClassBinding cb = this.table.get(c);
		cb.put(id, type, isUsed);
		return;
	}

	// put a method into this table
	// Duplication is not allowed.
	// Also note that MiniJava does NOT allow overloading.
	public void put(String c, String id, MethodType type) {
		ClassBinding cb = this.table.get(c);
		cb.put(id, type);
		return;
	}

	// return null for non-existing class
	public ClassBinding get(String className) {
		return this.table.get(className);
	}

	// get type of some field
	// return null for non-existing field.
	public Type.T get(String className, String xid) {
		Type.T type = null;
		ClassBinding cb = this.table.get(className);
		if (cb.fields.containsKey(xid)) {
			CheckableType ct = cb.fields.get(xid);
			type = ct.type;
			this.table.get(className).fields.get(xid).isUsed = true;
			return type;
		} else {
			while (type == null) { // search all parent classes until found or
									// fail
				if (cb.extendss == null)
					return null;
				cb = this.table.get(cb.extendss);
				CheckableType parentCT = cb.fields.get(xid);
				type = parentCT.type;
			}
			cb.fields.get(xid).isUsed = true;
			return type;
		}
	}

	// get type of some method
	// return null for non-existing method
	public MethodType getm(String className, String mid) {
		ClassBinding cb = this.table.get(className);
		MethodType type = cb.methods.get(mid);
		while (type == null) { // search all parent classes until found or fail
			if (cb.extendss == null)
				return type;

			cb = this.table.get(cb.extendss);
			type = cb.methods.get(mid);
		}
		return type;
	}

	public void dump() {
		Set<Entry<String, ClassBinding>> tableSet = table.entrySet();
		for (Entry<String, ClassBinding> elem : tableSet) {
			ClassBinding tmp = elem.getValue();
			System.out.println("class name:" + elem.getKey());
			System.out.println("class extends:" + tmp.extendss);
			Set<Entry<String, CheckableType>> classFieldSet = tmp.fields.entrySet();
			if (classFieldSet.isEmpty()) {
				System.out.println("This class has no filed...");
			} else {
				System.out.println("field:\n");
				for (Entry<String,CheckableType> fieldElem : classFieldSet) {
					System.out.print("\tid:" + fieldElem.getKey());
					System.out.println("\ttype:"
							+ fieldElem.getValue().type.toString());
				}
			}
			Set<Entry<String, MethodType>> classMethodSet = tmp.methods
					.entrySet();
			if (classMethodSet.isEmpty()) {
				System.out.println("This class has no method...");
			} else {
				int i = 1;
				System.out.println("This class has " + classMethodSet.size()
						+ " methods :");
				for (Entry<String, MethodType> methodElem : classMethodSet) {
					System.out.print("\tMethod " + i + " name:"
							+ methodElem.getKey());
					LinkedList<Dec.T> argsList = methodElem.getValue().argsType;
					if (argsList.isEmpty()) {
						System.out.println(".It has no parameters...");
					} else {
						System.out.println(".It has " + argsList.size()
								+ " parameters,their type and ID are:");
						for (Dec.T argsElem : argsList) {
							System.out.print("\t");
							argsElem.accept(new PrettyPrintVisitor());
						}
					}
					System.out.println("\tIts return type is"
							+ methodElem.getValue().retType.toString() + "\n");
					i++;
				}
			}
		}
	}
	
	public LinkedList<CheckableType> findUnused(){
		Set<Entry<String,ClassBinding>> all = table.entrySet();
		LinkedList<CheckableType> unused = new LinkedList<CheckableType>();
		for(Entry<String, ClassBinding> cbElem : all){
			Hashtable<String, CheckableType> field = cbElem.getValue().fields;
			Set<Entry<String,CheckableType>> fieldAll = field.entrySet();
			for(Entry<String, CheckableType> ctElem : fieldAll){
				if(!(ctElem.getValue().isUsed)){
					unused.add(ctElem.getValue());
				}
			}
		}
		
		return unused;
	}

	@Override
	public String toString() {
		return this.table.toString();
	}
}
