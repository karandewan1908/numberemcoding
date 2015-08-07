package co.numberencoding.log;

public enum Severity {

	Error(1),
	
	Debug(3),
	
	Warning(2);
	
	private int severity;
	private Severity(int s) {
		severity = s;
	}
	
	public int getSeverity() {
		return this.severity;
	}
	
	
}
