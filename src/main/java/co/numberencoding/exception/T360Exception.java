package co.numberencoding.exception;

import co.numberencoding.log.Logger;
import co.numberencoding.log.Severity;

public class T360Exception extends Exception {

	private Severity sev;
	private Exception ex;
	public T360Exception(Exception e,Severity m) {
		super(e);
		ex =e;
		sev = m;
	}
	
	public void logException() {
		Logger.log(ex.getMessage(),sev);
	}
	
}
