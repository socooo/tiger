package ast;

import ast.Ast.Dec;
import ast.Ast.Exp;
import ast.Ast.Exp.Call;
import ast.Ast.Exp.Id;
import ast.Ast.Exp.Lt;
import ast.Ast.Exp.NewObject;
import ast.Ast.Exp.Num;
import ast.Ast.Exp.Sub;
import ast.Ast.Exp.This;
import ast.Ast.Exp.Times;
import ast.Ast.MainClass;
import ast.Ast.MainClass.MainClassSingle;
import ast.Ast.Method;
import ast.Ast.Program;
import ast.Ast.Program.ProgramSingle;
import ast.Ast.Stm;
import ast.Ast.Stm.Assign;
import ast.Ast.Stm.If;
import ast.Ast.Stm.Print;
import ast.Ast.Type;

public class Fac {
	// Lab2, exercise 2: read the following code and make
	// sure you understand how the sample program "test/Fac.java" is
	// represented.

	// /////////////////////////////////////////////////////
	// To represent the "Fac.java" program in memory manually
	// this is for demonstration purpose only, and
	// no one would want to do this in reality (boring and error-prone).
	/*
	 * class Factorial { public static void main(String[] a) {
	 * System.out.println(new Fac().ComputeFac(10)); } } class Fac { public int
	 * ComputeFac(int num) { int num_aux; if (num < 1) num_aux = 1; else num_aux
	 * = num * (this.ComputeFac(num-1)); return num_aux; } }
	 */

	// // main class: "Factorial"
	static MainClass.T factorial = new MainClassSingle("Factorial", "a",
			new Print(new Call(new NewObject("Fac",3), "ComputeFac",
					new util.Flist<Exp.T>().list(new Num(10,3)))));

	// // class "Fac"
	static ast.Ast.Class.T fac = new ast.Ast.Class.ClassSingle("Fac", null,
			new util.Flist<Dec.T>().list(),
			new util.Flist<Method.T>().list(new Method.MethodSingle(
					new Type.Int(7), "ComputeFac", new util.Flist<Dec.T>()
							.list(new Dec.DecSingle(new Type.Int(7), "num",7)),
					new util.Flist<Dec.T>().list(new Dec.DecSingle(
							new Type.Int(8), "num_aux",8)),
					new util.Flist<Stm.T>().list(new If(new Lt(new Id("num",9),
							new Num(1,9)), new Assign("num_aux", new Num(1,10)),
							new Assign("num_aux", new Times(new Id("num",12),
									new Call(new This(12), "ComputeFac",
											new util.Flist<Exp.T>()
													.list(new Sub(
															new Id("num",12),
															new Num(1,12)))))))),
					new Id("num_aux",13))));

	// program
	public static Program.T prog = new ProgramSingle(factorial,
			new util.Flist<ast.Ast.Class.T>().list(fac));

	// Lab2, exercise 2: you should write some code to
	// represent the program "test/Sum.java".
	// Your code here:
	static MainClass.T sum = new MainClassSingle("Sum", "a", new Print(
			new Call(new NewObject("Doit",3), "doit",
					new util.Flist<Exp.T>().list(new Num(101,3)))));

	static ast.Ast.Class.T sumClass = new ast.Ast.Class.ClassSingle("Doit",
			null, new util.Flist<Dec.T>().list(),
			new util.Flist<Method.T>().list(new Method.MethodSingle(
					new Type.Int(9), "doit", new util.Flist<Dec.T>()
							.list(new Dec.DecSingle(new Type.Int(8), "n",8)),
					new util.Flist<Dec.T>().list(new Dec.DecSingle(
							new Type.Int(9), "sum",9), new Dec.DecSingle(
							new Type.Int(10), "i",10)),
					new util.Flist<Stm.T>().list(
							new Assign("i", new Num(0,12)),
							new Assign("sum", new Num(0,13)),
							new ast.Ast.Stm.While(new Lt(new Id("i",14), new Id("n",14)),
							new Stm.Block(new util.Flist<Stm.T>().list(
									new Assign("sum", new ast.Ast.Exp.Add(new Id("sum",15),
											new Num(1,15))), new Assign("i", new ast.Ast.Exp.Add(new Id("i",16),
													new Num(1,16))))))),new Id("sum",18))));

	
	public static Program.T progSum = new ProgramSingle(sum,
			new util.Flist<ast.Ast.Class.T>().list(sumClass));
}

