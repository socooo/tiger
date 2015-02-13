package elaborator;

import ast.Ast.Type;

public class CheckableType{
	String id;
	boolean isUsed;
	Type.T type=null;
	int lineNum;
	public CheckableType(String id,Type.T type,boolean isUsed,int lineNum){
		this.isUsed = isUsed;
		this.type = type;
		this.id = id;
		this.lineNum = lineNum;
	}
}
