package control;

import ast.Ast.Type;

public class Error {
	public Type.T wanted;
	public Type.T got;
	public Error(Type.T wanted,Type.T got){
		this.wanted = wanted;
		this.got = got;
	}
}
