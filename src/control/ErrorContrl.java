package control;

import java.util.LinkedList;

import ast.Ast.Type;

public class ErrorContrl {
	LinkedList<Error> errorList;
	LinkedList<String> errorMsgList;
	public ErrorContrl(){
		errorList = new LinkedList<Error>();
		errorMsgList = new LinkedList<String>();
	}
	public void addError(Type.T wanted,Type.T got){
		Error e = new Error(wanted,got);
		errorList.add(e);
	}
	public void addErrorMsg(String msg){
		errorMsgList.add(msg);
	}
	public LinkedList<Error> getErrors(){
		return errorList;
	}
	public LinkedList<String> getErrorMsgs(){
		return errorMsgList;
	}
}
