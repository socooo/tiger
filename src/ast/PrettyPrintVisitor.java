package ast;

import java.util.LinkedList;

import ast.Ast.Class.ClassSingle;
import ast.Ast.Dec;
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
import ast.Ast.Program;
import ast.Ast.Stm;
import ast.Ast.Stm.Assign;
import ast.Ast.Stm.AssignArray;
import ast.Ast.Stm.Block;
import ast.Ast.Stm.If;
import ast.Ast.Stm.Print;
import ast.Ast.Stm.T;
import ast.Ast.Stm.While;
import ast.Ast.Type.Boolean;
import ast.Ast.Type.ClassType;
import ast.Ast.Type.Int;
import ast.Ast.Type.IntArray;

public class PrettyPrintVisitor implements Visitor {
	private int indentLevel;

	public PrettyPrintVisitor() {
		this.indentLevel = 4;
	}

	private void indent() {
		this.indentLevel += 2;
	}

	private void unIndent() {
		this.indentLevel -= 2;
	}

	private void printSpaces() {
		int i = this.indentLevel;
		while (i-- != 0)
			this.say(" ");
	}

	private void sayln(String s) {
		System.out.println(s);
	}

	private void say(String s) {
		System.out.print(s);
	}

	// /////////////////////////////////////////////////////
	// expressions
	@Override
	public void visit(Add e) {
		// Lab2, exercise4: filling in missing code.
		// Similar for other methods with empty bodies.
		// Your code here:
		e.left.accept(this);
		say(" + ");
		e.right.accept(this);
		return;
	}

	@Override
	public void visit(And e) {
		e.left.accept(this);
		say("&&");
		e.right.accept(this);
		return;
	}

	@Override
	public void visit(ArraySelect e) {
	}

	@Override
	public void visit(Call e) {
		e.exp.accept(this);
		this.say("." + e.id + "(");
		if (!e.args.isEmpty()) {
			((Exp.T) e.args.get(0)).accept(this);
			e.args.remove(0);
		}
		for (Exp.T x : e.args) {
			this.say(", ");
			x.accept(this);
		}
		this.say(")");
		return;
	}

	@Override
	public void visit(False e) {
		say("false");
	}

	@Override
	public void visit(Id e) {
		this.say(e.id);
	}

	@Override
	public void visit(Length e) {
		say("length");
	}

	@Override
	public void visit(Lt e) {
		e.left.accept(this);
		this.say(" < ");
		e.right.accept(this);
		return;
	}

	@Override
	public void visit(NewIntArray e) {
		this.say("int[");
		e.exp.accept(this);
		this.say("]");
	}

	@Override
	public void visit(NewObject e) {
		this.say("new " + e.id + "()");
		return;
	}

	@Override
	public void visit(Not e) {
		say("!");
		e.exp.accept(this);
	}

	@Override
	public void visit(Num e) {
		System.out.print(e.num);
		return;
	}

	@Override
	public void visit(Sub e) {
		e.left.accept(this);
		this.say(" - ");
		e.right.accept(this);
		return;
	}

	@Override
	public void visit(This e) {
		this.say("this");
	}

	@Override
	public void visit(Times e) {
		e.left.accept(this);
		this.say(" * ");
		e.right.accept(this);
		return;
	}

	@Override
	public void visit(True e) {
		this.say("true");
		return;
	}

	// statements
	@Override
	public void visit(Assign s) {
		this.printSpaces();
		this.say(s.id + " = ");
		s.exp.accept(this);
		this.sayln(";");
		return;
	}

	@Override
	public void visit(AssignArray s) {
		this.printSpaces();
		this.say(s.id+"[");
		s.index.accept(this);
		this.say("]");
		this.say(" = ");
		s.exp.accept(this);
		this.sayln(";");
	}

	@Override
	public void visit(Block s) {
		LinkedList<ast.Ast.Stm.T> blocks = s.stms;
		for(T stm:blocks){
			printSpaces();
			stm.accept(this);
		}
	}

	@Override
	public void visit(If s) {
		this.printSpaces();
		this.say("if (");
		s.condition.accept(this);
		this.sayln("){");
		this.indent();
		s.thenn.accept(this);
		this.unIndent();
		this.printSpaces();
		this.sayln("}");
		this.printSpaces();
		this.sayln("else{");
		this.indent();
		s.elsee.accept(this);
		this.printSpaces();
		this.sayln("}");
		this.unIndent();
		return;
	}

	@Override
	public void visit(Print s) {
		this.printSpaces();
		this.say("System.out.println (");
		s.exp.accept(this);
		this.sayln(");");
		return;
	}

	@Override
	public void visit(While s) {
		this.printSpaces();
		this.say("while (");
		s.condition.accept(this);
		this.sayln("){");
		s.body.accept(this);
		this.printSpaces();
		sayln("}");
	}

	// type
	@Override
	public void visit(Boolean t) {
		say("boolean");
	}

	@Override
	public void visit(ClassType t) {
		this.say(t.id);
	}

	@Override
	public void visit(Int t) {
		this.say("int");
	}

	@Override
	public void visit(IntArray t) {
		this.say("int[]");
	}

	// dec
	@Override
	public void visit(Dec.DecSingle d) {
		this.printSpaces();
		this.sayln(d.type+" "+d.id);
	}

	// method
	@Override
	public void visit(MethodSingle m) {
		this.say("  public ");
		m.retType.accept(this);
		this.say(" " + m.id + "(");
		if (!m.formals.isEmpty()) {
			Dec.DecSingle dec = (Dec.DecSingle) m.formals.get(0);
			dec.type.accept(this);
			this.say(" " + dec.id);
			m.formals.remove(0);
		}
		for (Dec.T d : m.formals) {
			this.say(",");
			Dec.DecSingle dec = (Dec.DecSingle) d;
			dec.type.accept(this);
			this.say(" " + dec.id);
		}
		this.sayln(")");
		this.sayln("  {");

		for (Dec.T d : m.locals) {
			Dec.DecSingle dec = (Dec.DecSingle) d;
			this.say("    ");
			dec.type.accept(this);
			this.say(" " + dec.id + ";\n");
		}
		this.sayln("");
		for (Stm.T s : m.stms)
			s.accept(this);
		this.say("    return ");
		m.retExp.accept(this);
		this.sayln(";");
		this.sayln("  }");
		return;
	}

	// class
	@Override
	public void visit(ClassSingle c) {
		this.say("class " + c.id);
		if (c.extendss != null)
			this.sayln(" extends " + c.extendss);
		else
			this.sayln("");

		this.sayln("{");

		for (Dec.T d : c.decs) {
			Dec.DecSingle dec = (Dec.DecSingle) d;
			this.say("  ");
			dec.type.accept(this);
			this.say(" ");
			this.sayln(dec.id + ";");
		}
		for (Method.T mthd : c.methods)
			mthd.accept(this);
		this.sayln("}");
		return;
	}

	// main class
	@Override
	public void visit(MainClass.MainClassSingle c) {
		this.sayln("class " + c.id);
		this.sayln("{");
		this.sayln("  public static void main (String [] " + c.arg + ")");
		this.sayln("  {");
		c.stm.accept(this);
		this.sayln("  }");
		this.sayln("}");
		return;
	}

	// program
	@Override
	public void visit(Program.ProgramSingle p) {
		p.mainClass.accept(this);
		this.sayln("");
		for (ast.Ast.Class.T classs : p.classes) {
			classs.accept(this);
		}
		System.out.println("\n\n");
	}
}
