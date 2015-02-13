package elaborator;

import java.util.Hashtable;
import java.util.LinkedList;

import ast.Ast;
import ast.Ast.Class;
import ast.Ast.Class.ClassSingle;
import ast.Ast.Dec;
import ast.Ast.Dec.DecSingle;
import ast.Ast.Exp;
import ast.Ast.Exp.Add;
import ast.Ast.Exp.And;
import ast.Ast.Exp.ArraySelect;
import ast.Ast.Exp.Call;
import ast.Ast.Exp.False;
import ast.Ast.Exp.Id;
import ast.Ast.Exp.Length;
import ast.Ast.Exp.Lt;
import ast.Ast.Exp.NewIntArray;
import ast.Ast.Exp.NewObject;
import ast.Ast.Exp.Not;
import ast.Ast.Exp.Num;
import ast.Ast.Exp.Sub;
import ast.Ast.Exp.This;
import ast.Ast.Exp.Times;
import ast.Ast.Exp.True;
import ast.Ast.MainClass;
import ast.Ast.Method;
import ast.Ast.Method.MethodSingle;
import ast.Ast.Program.ProgramSingle;
import ast.Ast.Stm;
import ast.Ast.Stm.Assign;
import ast.Ast.Stm.AssignArray;
import ast.Ast.Stm.Block;
import ast.Ast.Stm.If;
import ast.Ast.Stm.Print;
import ast.Ast.Stm.While;
import ast.Ast.Type;
import ast.Ast.Type.ClassType;
import control.Control.ConAst;
//<<<<<<< HEAD
//
//public class ElaboratorVisitor implements ast.Visitor
//{
//  public ClassTable classTable; // symbol table for class
//  public MethodTable methodTable; // symbol table for each method
//  public String currentClass; // the class name being elaborated
//  public Type.T type; // type of the expression being elaborated
//
//  public ElaboratorVisitor()
//  {
//    this.classTable = new ClassTable();
//    this.methodTable = new MethodTable();
//    this.currentClass = null;
//    this.type = null;
//  }
//
//  private void error()
//  {
//    System.out.println("type mismatch");
//    System.exit(1);
//  }
//
//  // /////////////////////////////////////////////////////
//  // expressions
//  @Override
//  public void visit(Add e)
//  {
//  }
//
//  @Override
//  public void visit(And e)
//  {
//  }
//
//  @Override
//  public void visit(ArraySelect e)
//  {
//  }
//
//  @Override
//  public void visit(Call e)
//  {
//    Type.T leftty;
//    Type.ClassType ty = null;
//
//    e.exp.accept(this);
//    leftty = this.type;
//    if (leftty instanceof ClassType) {
//      ty = (ClassType) leftty;
//      e.type = ty.id;
//    } else
//      error();
//    MethodType mty = this.classTable.getm(ty.id, e.id);
//
//    java.util.LinkedList<Type.T> declaredArgTypes
//    = new java.util.LinkedList<Type.T>();
//    for (Dec.T dec: mty.argsType){
//      declaredArgTypes.add(((Dec.DecSingle)dec).type);
//    }
//    java.util.LinkedList<Type.T> argsty = new LinkedList<Type.T>();
//    for (Exp.T a : e.args) {
//      a.accept(this);
//      argsty.addLast(this.type);
//    }
//    if (declaredArgTypes.size() != argsty.size())
//      error();
//    // For now, the following code only checks that
//    // the types for actual and formal arguments should
//    // be the same. However, in MiniJava, the actual type
//    // of the parameter can also be a subtype (sub-class) of the 
//    // formal type. That is, one can pass an object of type "A"
//    // to a method expecting a type "B", whenever type "A" is
//    // a sub-class of type "B".
//    // Modify the following code accordingly:
//    for (int i = 0; i < argsty.size(); i++) {
//      Dec.DecSingle dec = (Dec.DecSingle) mty.argsType.get(i);
//      if (dec.type.toString().equals(argsty.get(i).toString()))
//        ;
//      else
//        error();
//    }
//    this.type = mty.retType;
//    // the following two types should be the declared types.
//    e.at = declaredArgTypes;
//    e.rt = this.type;
//    return;
//  }
//
//  @Override
//  public void visit(False e)
//  {
//  }
//
//  @Override
//  public void visit(Id e)
//  {
//    // first look up the id in method table
//    Type.T type = this.methodTable.get(e.id);
//    // if search failed, then s.id must be a class field.
//    if (type == null) {
//      type = this.classTable.get(this.currentClass, e.id);
//      // mark this id as a field id, this fact will be
//      // useful in later phase.
//      e.isField = true;
//    }
//    if (type == null)
//      error();
//    this.type = type;
//    // record this type on this node for future use.
//    e.type = type;
//    return;
//  }
//
//  @Override
//  public void visit(Length e)
//  {
//  }
//
//  @Override
//  public void visit(Lt e)
//  {
//    e.left.accept(this);
//    Type.T ty = this.type;
//    e.right.accept(this);
//    if (!this.type.toString().equals(ty.toString()))
//      error();
//    this.type = new Type.Boolean();
//    return;
//  }
//
//  @Override
//  public void visit(NewIntArray e)
//  {
//  }
//
//  @Override
//  public void visit(NewObject e)
//  {
//    this.type = new Type.ClassType(e.id);
//    return;
//  }
//
//  @Override
//  public void visit(Not e)
//  {
//  }
//
//  @Override
//  public void visit(Num e)
//  {
//    this.type = new Type.Int();
//    return;
//  }
//
//  @Override
//  public void visit(Sub e)
//  {
//    e.left.accept(this);
//    Type.T leftty = this.type;
//    e.right.accept(this);
//    if (!this.type.toString().equals(leftty.toString()))
//      error();
//    this.type = new Type.Int();
//    return;
//  }
//
//  @Override
//  public void visit(This e)
//  {
//    this.type = new Type.ClassType(this.currentClass);
//    return;
//  }
//
//  @Override
//  public void visit(Times e)
//  {
//    e.left.accept(this);
//    Type.T leftty = this.type;
//    e.right.accept(this);
//    if (!this.type.toString().equals(leftty.toString()))
//      error();
//    this.type = new Type.Int();
//    return;
//  }
//
//  @Override
//  public void visit(True e)
//  {
//  }
//
//  // statements
//  @Override
//  public void visit(Assign s)
//  {
//    // first look up the id in method table
//    Type.T type = this.methodTable.get(s.id);
//    // if search failed, then s.id must
//    if (type == null)
//      type = this.classTable.get(this.currentClass, s.id);
//    if (type == null)
//      error();
//    s.exp.accept(this);
//    s.type = type;
//    this.type.toString().equals(type.toString());
//    return;
//  }
//
//  @Override
//  public void visit(AssignArray s)
//  {
//  }
//
//  @Override
//  public void visit(Block s)
//  {
//  }
//
//  @Override
//  public void visit(If s)
//  {
//    s.condition.accept(this);
//    if (!this.type.toString().equals("@boolean"))
//      error();
//    s.thenn.accept(this);
//    s.elsee.accept(this);
//    return;
//  }
//
//  @Override
//  public void visit(Print s)
//  {
//    s.exp.accept(this);
//    if (!this.type.toString().equals("@int"))
//      error();
//    return;
//  }
//
//  @Override
//  public void visit(While s)
//  {
//  }
//
//  // type
//  @Override
//  public void visit(Type.Boolean t)
//  {
//  }
//
//  @Override
//  public void visit(Type.ClassType t)
//  {
//  }
//
//  @Override
//  public void visit(Type.Int t)
//  {
//    System.out.println("aaaa");
//  }
//
//  @Override
//  public void visit(Type.IntArray t)
//  {
//  }
//
//  // dec
//  @Override
//  public void visit(Dec.DecSingle d)
//  {
//  }
//
//  // method
//  @Override
//  public void visit(Method.MethodSingle m)
//  {
//    // construct the method table
//    this.methodTable.put(m.formals, m.locals);
//
//    if (ConAst.elabMethodTable)
//      this.methodTable.dump();
//
//    for (Stm.T s : m.stms)
//      s.accept(this);
//    m.retExp.accept(this);
//    return;
//  }
//
//  // class
//  @Override
//  public void visit(Class.ClassSingle c)
//  {
//    this.currentClass = c.id;
//
//    for (Method.T m : c.methods) {
//      m.accept(this);
//    }
//    return;
//  }
//
//  // main class
//  @Override
//  public void visit(MainClass.MainClassSingle c)
//  {
//    this.currentClass = c.id;
//    // "main" has an argument "arg" of type "String[]", but
//    // one has no chance to use it. So it's safe to skip it...
//
//    c.stm.accept(this);
//    return;
//  }
//
//  // ////////////////////////////////////////////////////////
//  // step 1: build class table
//  // class table for Main class
//  private void buildMainClass(MainClass.MainClassSingle main)
//  {
//    this.classTable.put(main.id, new ClassBinding(null));
//  }
//
//  // class table for normal classes
//  private void buildClass(ClassSingle c)
//  {
//    this.classTable.put(c.id, new ClassBinding(c.extendss));
//    for (Dec.T dec : c.decs) {
//      Dec.DecSingle d = (Dec.DecSingle) dec;
//      this.classTable.put(c.id, d.id, d.type);
//    }
//    for (Method.T method : c.methods) {
//      MethodSingle m = (MethodSingle) method;
//      this.classTable.put(c.id, m.id, new MethodType(m.retType, m.formals));
//    }
//  }
//
//  // step 1: end
//  // ///////////////////////////////////////////////////
//
//  // program
//  @Override
//  public void visit(ProgramSingle p)
//  {
//    // ////////////////////////////////////////////////
//    // step 1: build a symbol table for class (the class table)
//    // a class table is a mapping from class names to class bindings
//    // classTable: className -> ClassBinding{extends, fields, methods}
//    buildMainClass((MainClass.MainClassSingle) p.mainClass);
//    for (Class.T c : p.classes) {
//      buildClass((ClassSingle) c);
//    }
//
//    // we can double check that the class table is OK!
//    if (control.Control.ConAst.elabClassTable) {
//      this.classTable.dump();
//    }
//
//    // ////////////////////////////////////////////////
//    // step 2: elaborate each class in turn, under the class table
//    // built above.
//    p.mainClass.accept(this);
//    for (Class.T c : p.classes) {
//      c.accept(this);
//    }
//
//  }
//=======
import control.ErrorContrl;
import control.WarnningControl;

public class ElaboratorVisitor implements ast.Visitor {
	public ClassTable classTable; // symbol table for class
	public MethodTable methodTable; // symbol table for each method
	public String currentClass; // the class name being elaborated
	public Type.T type; // type of the expression being elaborated
	MethodTable currentMethod;
	public ErrorContrl errc;
	public WarnningControl warnc;

	public ElaboratorVisitor() {
		this.classTable = new ClassTable();
		this.methodTable = new MethodTable();
		this.currentClass = null;
		this.type = null;
		errc = new ErrorContrl();
		this.warnc = new WarnningControl();
	}

	// private void error(Type.T wanted,Type.T got) {
	// System.out.println("Error in elaborator visitor : type mismatch");
	// System.out.println("Wanted:"+wanted.toString());
	// System.out.println("But got:"+got.toString());
	// System.out.println("in line "+got.lineNum);
	// }
	//
	// private void error(String errInfo) {
	// System.out.println(errInfo);
	// }

	// /////////////////////////////////////////////////////
	// expressions
	@Override
	public void visit(Add e) {
		e.left.accept(this);
		if (!this.type.toString().equals("@int")) {
			errc.addError(new Type.Int(0), this.type);
			// error(new Type.Int(0),this.type);
		}
		e.right.accept(this);
		if (!this.type.toString().equals("@int")) {
			// error(new Type.Int(0),this.type);
			errc.addError(new Type.Int(0), this.type);
		}
		this.type = new Type.Int(0);
		return;
	}

	@Override
	public void visit(And e) {
		e.left.accept(this);
		if (!this.type.toString().equals("@boolean")) {
			// error(new Type.Boolean(0),this.type);
			errc.addError(new Type.Boolean(0), this.type);
		}
		e.right.accept(this);
		if (!this.type.toString().equals("@boolean")) {
			// error(new Type.Boolean(0),this.type);
			errc.addError(new Type.Boolean(0), this.type);
		}
		this.type = new Type.Boolean(this.type.lineNum);
		return;
	}

	@Override
	public void visit(ArraySelect e) {
		e.index.accept(this);
		if (!this.type.toString().equals("@int")) {
			errc.addError(new Type.Int(0), this.type);
		}
		this.type = new Type.Int(this.type.lineNum);
		return;
	}

	@Override
	public void visit(Call e) {
		Type.T leftty;
		Type.ClassType ty = null;
		e.exp.accept(this);
		leftty = this.type;		//Tree
		if (leftty instanceof ClassType) {
			ty = (ClassType) leftty;
//			String idid = ((ClassType) leftty).id;
//			System.out.println(idid);
			e.type = ty.id;
		} else
			errc.addError(new Type.ClassType("class type", 0), leftty);
		MethodType mty = this.classTable.getm(ty.id, e.id);
		java.util.LinkedList<Type.T> argsty = new LinkedList<Type.T>();
		for (Exp.T a : e.args) {
			a.accept(this);
			argsty.addLast(this.type);
		}
		if (mty.argsType.size() != argsty.size())
			errc.addErrorMsg("Number of the call method's args doesn't match!\n in line "
					+ this.type.lineNum);
		for (int i = 0; i < argsty.size(); i++) {
			Dec.DecSingle dec = (Dec.DecSingle) mty.argsType.get(i);
			String argType = argsty.get(i).toString();
			if (dec.type.toString().equals(argType))
				;
			else {
				boolean typeMatch = false;
				String classFather = classTable.get(argType).extendss;
				if (classFather != null) {
					while (classFather != null) {
						if(dec.type.toString().equals(classFather)){
							typeMatch = true;
							break;
						}
						else{
							classFather = classTable.get(classFather).extendss;
						}
					}
					
					if(typeMatch);
					else
						errc.addError(argsty.get(i), dec.type);
				}
				else{
					errc.addError(argsty.get(i), dec.type);
				}
			}
		}
		this.type = mty.retType;
		e.at = argsty;
		e.rt = this.type;
		return;
	}

	@Override
	public void visit(False e) {
		this.type = new Type.Boolean(e.lineNum);
	}

	@Override
	public void visit(Id e) {
		// first look up the id in method table
		Type.T type = this.currentMethod.get(e.id);
		// if search failed, then s.id must be a class field.
		if (type == null) {
			type = this.classTable.get(this.currentClass, e.id);
			// mark this id as a field id, this fact will be
			// useful in later phase.
			e.isField = true;
		}
		if (type == null)
			errc.addErrorMsg("Has no such type!\n in line" + this.type.lineNum);
		this.type = type;
		// record this type on this node for future use.
		e.type = type;
		return;
	}

	@Override
	public void visit(Length e) {
		this.type = new Type.Int(e.lineNum);
		return;
	}

	@Override
	public void visit(Lt e) {
		e.left.accept(this);
		Type.T ty = this.type;
		e.right.accept(this);
		if (!this.type.toString().equals(ty.toString()))
			errc.addError(ty, this.type);
		this.type = new Type.Boolean(this.type.lineNum);
		return;
	}

	@Override
	public void visit(NewIntArray e) {
		this.type = new Type.IntArray(e.lineNum);
	}

	@Override
	public void visit(NewObject e) {
		this.type = new Type.ClassType(e.id, e.lineNum);
		return;
	}

	@Override
	public void visit(Not e) {
		e.exp.accept(this);
		if (!this.type.toString().equals("@boolean")) {
			errc.addError(new Type.Boolean(0), this.type);
		}
		this.type = new Type.Boolean(this.type.lineNum);
		return;
	}

	@Override
	public void visit(Num e) {
		this.type = new Type.Int(e.lineNum);
		return;
	}

	@Override
	public void visit(Sub e) {
		e.left.accept(this);
		Type.T leftty = this.type;
		e.right.accept(this);
		if (!this.type.toString().equals(leftty.toString()))
			errc.addError(leftty, this.type);
		this.type = new Type.Int(this.type.lineNum);
		return;
	}

	@Override
	public void visit(This e) {
		this.type = new Type.ClassType(this.currentClass, e.lineNum);
		return;
	}

	@Override
	public void visit(Times e) {
		e.left.accept(this);
		Type.T leftty = this.type;
		e.right.accept(this);
		if (!this.type.toString().equals(leftty.toString()))
			errc.addError(leftty, this.type);
		this.type = new Type.Int(e.lineNum);
		return;
	}

	@Override
	public void visit(True e) {
		this.type = new Type.Boolean(e.lineNum);
		return;
	}

	// statements
	@Override
	public void visit(Assign s) {
		// first look up the id in method table
		Type.T type = this.currentMethod.get(s.id);
		// if search failed, then s.id must
		if (type == null)
			type = this.classTable.get(this.currentClass, s.id);
		if (type == null)
			errc.addErrorMsg("Has no such type");
		s.exp.accept(this);
		s.type = type;
		if (!this.type.toString().equals(type.toString())) {
			errc.addError(type, this.type);
		}
		return;
	}

	@Override
	public void visit(AssignArray s) {
		Type.T assType = this.type;
		s.index.accept(this);
		if (!this.type.toString().equals("@int")) {
			errc.addError(new Type.Int(0), this.type);
		}
		s.exp.accept(this);
		if (!assType.toString().equals(this.type.toString())) {
			errc.addError(assType, this.type);
		}
		return;
	}

	@Override
	public void visit(Block s) {
		for (Stm.T elem : s.stms) {
			elem.accept(this);
		}
		return;
	}

	@Override
	public void visit(If s) {
		s.condition.accept(this);
		if (!this.type.toString().equals("@boolean"))
			errc.addError(new Type.Boolean(0), this.type);
		s.thenn.accept(this);
		s.elsee.accept(this);
		return;
	}

	@Override
	public void visit(Print s) {
		s.exp.accept(this);
		if (!this.type.toString().equals("@int"))
			errc.addError(new Type.Int(this.type.lineNum), this.type);
		return;
	}

	@Override
	public void visit(While s) {
		s.condition.accept(this);
		if (!this.type.toString().equals("@boolean")) {
			errc.addError(new Type.Boolean(0), this.type);
		}
		s.body.accept(this);
		return;
	}

	// type
	@Override
	public void visit(Type.Boolean t) {
		this.type = t;
		return;
	}

	@Override
	public void visit(Type.ClassType t) {
		this.type = t;
		return;
	}

	@Override
	public void visit(Type.Int t) {
		this.type = t;
		return;
	}

	@Override
	public void visit(Type.IntArray t) {
		this.type = t;
		return;
	}

	// dec
	@Override
	public void visit(Dec.DecSingle d) {
		this.type = d.type;
		return;
	}

	// method
	@Override
	public void visit(Method.MethodSingle m) {
		// construct the method table
		MethodTable mt = new MethodTable();
		// boolean i = m.formals.isEmpty();
		// Ast.Dec.DecSingle formaldec = (Ast.Dec.DecSingle)m.formals.getLast();
		mt.put(m.formals, m.locals);

		if (ConAst.elabMethodTable) {
			System.out.println("Now in class " + currentClass + "'s method: "
					+ m.id);
			System.out.print("there's have ");
			mt.dump();
			System.out.println();
		}

		currentMethod = mt;
		for (Stm.T s : m.stms)
			s.accept(this);
		m.retExp.accept(this);
		LinkedList<CheckableType> warnningListMethod = mt.findUnused();
		warnc.addWarning(warnningListMethod);
		currentMethod = null;
		return;
	}

	// class
	@Override
	public void visit(Class.ClassSingle c) {
		this.currentClass = c.id;
		for (Method.T m : c.methods) {
			// String methodid = ((Method.MethodSingle)m).id;
			// boolean hasmethodformal
			// =((Method.MethodSingle)m).formals.isEmpty();
			// Ast.Dec.DecSingle methodformal = (DecSingle)
			// ((Method.MethodSingle)m).formals.get(0);
			// String formalid = methodformal.id;
			// System.out.println(formalid+" "+methodid);
			m.accept(this);
		}
		LinkedList<CheckableType> warnningListClass = classTable.findUnused();
		warnc.addWarning(warnningListClass);
		return;
	}

	// main class
	@Override
	public void visit(MainClass.MainClassSingle c) {
		this.currentClass = c.id;
		// "main" has an argument "arg" of type "String[]", but
		// one has no chance to use it. So it's safe to skip it...

		c.stm.accept(this);
		return;
	}

	// ////////////////////////////////////////////////////////
	// step 1: build class table
	// class table for Main class
	private void buildMainClass(MainClass.MainClassSingle main) {
		this.classTable.put(main.id, new ClassBinding(null));
	}

	// class table for normal classes
	private void buildClass(ClassSingle c) {
		this.classTable.put(c.id, new ClassBinding(c.extendss));
		for (Dec.T dec : c.decs) {
			Dec.DecSingle d = (Dec.DecSingle) dec;
			this.classTable.put(c.id, d.id, d.type, d.isUsed);
		}
		for (Method.T method : c.methods) {
			MethodSingle m = (MethodSingle) method;
			this.classTable.put(c.id, m.id,
					new MethodType(m.retType, m.formals));
		}
	}

	// step 1: end
	// ///////////////////////////////////////////////////

	// program
	@Override
	public void visit(ProgramSingle p) {
		// ////////////////////////////////////////////////
		// step 1: build a symbol table for class (the class table)
		// a class table is a mapping from class names to class bindings
		// classTable: className -> ClassBinding{extends, fields, methods}
		buildMainClass((MainClass.MainClassSingle) p.mainClass);
		for (Class.T c : p.classes) {
			buildClass((ClassSingle) c);
		}

		// we can double check that the class table is OK!
		if (control.Control.ConAst.elabClassTable) {
			this.classTable.dump();
		}

		// ////////////////////////////////////////////////
		// step 2: elaborate each class in turn, under the class table
		// built above.
		p.mainClass.accept(this);
		for (Class.T c : p.classes) {
			c.accept(this);
		}
		LinkedList<control.Error> errorList = errc.getErrors();
		LinkedList<String> errMsgList = errc.getErrorMsgs();
		if ((!errorList.isEmpty()) && (!errMsgList.isEmpty())) {
			System.out.println(errorList.size() + errMsgList.size()
					+ " errors occur!\n");
			for (control.Error e : errorList) {
				System.out
						.println("Error in elaborator visitor : type mismatch");
				System.out.println("Wanted:" + e.wanted.toString());
				System.out.println("But got:" + e.got.toString());
				System.out.println("in line " + e.got.lineNum);
				System.out.println();
			}
			for (String emsg : errMsgList) {
				System.out.println(emsg);
			}
		} else if (!errorList.isEmpty()) {
			System.out.println(errorList.size() + " errors occur!\n");
			for (control.Error e : errorList) {
				System.out
						.println("Error in elaborator visitor : type mismatch");
				System.out.println("Wanted:" + e.wanted.toString());
				System.out.println("But got:" + e.got.toString());
				System.out.println("in line " + e.got.lineNum);
				System.out.println();
			}
		} else if (!errMsgList.isEmpty()) {
			System.out.println(errMsgList.size() + " errors occur!\n");
			for (String emsg : errMsgList) {
				System.out.println(emsg);
			}
		}

		if (!warnc.getWarnning().isEmpty()) {
			System.out
					.println(warnc.getWarnning().size() + " warnning occurs:");
			for (CheckableType c : warnc.getWarnning()) {
				System.out.println("Variable " + c.id + " in line " + c.lineNum
						+ " has not used...");
			}
			System.out.println();
		}
	}
}
