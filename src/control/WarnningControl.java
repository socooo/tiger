package control;

import java.util.LinkedList;

import elaborator.CheckableType;

public class WarnningControl {
	LinkedList<CheckableType> warnningList;
	public WarnningControl(){
		this.warnningList = new LinkedList<CheckableType>();
	}
	public void addWarning(LinkedList<CheckableType> c){
		warnningList.addAll(c);
	}
	
	public LinkedList<CheckableType> getWarnning(){
		return warnningList;
	}
}
