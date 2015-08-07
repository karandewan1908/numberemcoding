package co.numberencoding.core.term;

public class TermImpl implements Term {
	
	
	private char[] term;
	private int startIndex;
	private int endIndex;
	private String termst;
	
	
	public TermImpl(char[] term) {
		this.term = term;
	}
	
	public String getTerm() {
		
		if(termst == null) {
			termst = new String(term);
		}
		return termst;
	}
	
	public int getSize() {
		return this.term.length;
	}
	
	public void setStartIndex(int ind) {
		this.startIndex = ind;
	}
	
	public void setEndIndex(int ind) {
		this.endIndex = ind;
	}
	
	public int getStartIndex() {
		return this.startIndex;
	}
	
	public int getEndIndex() {
		return this.endIndex;
	}
	
	public String toString() {
		return getTerm();
	}
	
	public int hashCode() {
		return getTerm().hashCode();
	}
	
	public boolean equals(Object t) {
		boolean ret = false;
		if(t == null) {
			ret = false;
		}else if(!(t instanceof Term)) {
			ret = false;
		}else{
		
			Term ct = (Term) t;
			
			 ret = this.getTerm().equals(ct.toString());
		}
		
		return ret;
	}

}
