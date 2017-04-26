package ast;

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
import ast.Ast.Stm.While;
import ast.Ast.Type.Boolean;
import ast.Ast.Type.ClassType;
import ast.Ast.Type.Int;
import ast.Ast.Type.IntArray;

import java.util.LinkedList;

public class PrettyPrintVisitor implements Visitor {
    private int indentLevel;

    public PrettyPrintVisitor() {
        this.indentLevel = 0;
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
        say("+");
        e.right.accept(this);
    }

    @Override
    public void visit(And e) {
        e.left.accept(this);
        say("&&");
        e.right.accept(this);
    }

    @Override
    public void visit(ArraySelect e) {
        e.array.accept(this);
        say("[");
        e.index.accept(this);
        say("]");
    }

    @Override
    public void visit(Call e) {
        e.exp.accept(this);
        this.say("." + e.id + "(");
        if (e.args != null) {
            e.args.get(0).accept(this);
            for (int i = 1; i < e.args.size(); i++) {
                this.say(", ");
                e.args.get(i).accept(this);
            }
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
        e.array.accept(this);
        say(".length");
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
        say("new int [");
        e.exp.accept(this);
        sayln("]");
    }

    @Override
    public void visit(NewObject e) {
        this.say("new " + e.id + "()");
        return;
    }

    @Override
    public void visit(Not e) {
        say("!");
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
        say("true");
    }

    // statements
    @Override
    public void visit(Assign s) {
        this.printSpaces();
        this.say(s.id.id + " = ");
        s.exp.accept(this);
        this.sayln(";");
        return;
    }

    @Override
    public void visit(AssignArray s) {
        printSpaces();
        say(s.id.id);
        say("[");
        s.index.accept(this);
        say("] = ");
        s.exp.accept(this);
        sayln(";");
    }

    @Override
    public void visit(Block s) {
        LinkedList<Stm.T> slist = s.stms;
        int size = slist.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                slist.get(i).accept(this);
            }
        }
    }

    @Override
    public void visit(If s) {
        this.printSpaces();
        this.say("if (");
        s.condition.accept(this);
        this.sayln(") {");
        indent();
        s.thenn.accept(this);
        unIndent();
        printSpaces();
        this.sayln("}");
        this.printSpaces();
        this.sayln("else {");
        indent();
        s.elsee.accept(this);
        unIndent();
        printSpaces();
        this.sayln("}");
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
        printSpaces();
        say("while(");
        s.condition.accept(this);
        sayln("){");
        indent();
        s.body.accept(this);
        unIndent();
        printSpaces();
        sayln("}");
    }

    // type
    @Override
    public void visit(Boolean t) {
        say("boolean");
    }

    @Override
    public void visit(ClassType t) {
        say("public class " + t.id);
    }

    @Override
    public void visit(Int t) {
        this.say("int");
    }

    @Override
    public void visit(IntArray t) {
        say("int[]");
    }

    // dec
    @Override
    public void visit(Dec.DecSingle d) {
        d.type.accept(this);
        say(" " + d.id);
    }

    // method
    @Override
    public void visit(MethodSingle m) {
        printSpaces();
        this.say("public ");
        m.retType.accept(this);
        this.say(" " + m.id + "(");
        if (m.formals.size() > 0) {
            Dec.DecSingle tmpDec = (Dec.DecSingle) m.formals.get(0);
            tmpDec.type.accept(this);
            say(" " + tmpDec.id);
            for (int i = 1; i < m.formals.size(); i++) {
                say(", ");
                m.formals.get(i);
                tmpDec.type.accept(this);
                this.say(" " + tmpDec.id);
            }
        }
        this.sayln(")");
        this.sayln("  {");
        indent();
        for (Dec.T d : m.locals) {
            Dec.DecSingle dec = (Dec.DecSingle) d;
            printSpaces();
            dec.type.accept(this);
            this.sayln(" " + dec.id + ";");
        }
        this.sayln("");
        for (Stm.T s : m.stms)
            s.accept(this);
        sayln("");
        printSpaces();
        this.say("return ");
        m.retExp.accept(this);
        this.sayln(";");
        unIndent();
        printSpaces();
        this.sayln("}");
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
        indent();
        for (Dec.T d : c.decs) {
            Dec.DecSingle dec = (Dec.DecSingle) d;
            dec.type.accept(this);
            this.sayln(dec.id + ";");
        }
        for (Method.T mthd : c.methods)
            mthd.accept(this);
        unIndent();
        this.sayln("}");
        return;
    }

    // main class
    @Override
    public void visit(MainClass.MainClassSingle c) {
        this.sayln("class " + c.id);
        this.sayln("{");
        indent();
        printSpaces();
        this.sayln("public static void main (String [] " + c.arg + ") {");
        indent();
        c.stm.accept(this);
        unIndent();
        printSpaces();
        this.sayln("}");
        unIndent();
        printSpaces();
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
