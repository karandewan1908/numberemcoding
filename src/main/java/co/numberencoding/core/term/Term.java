package co.numberencoding.core.term;

public interface Term {
	public String getTerm();
	public void setStartIndex(int ind);
	public void setEndIndex(int ind);
	public int getStartIndex();
	public int getEndIndex();
	public int getSize();
	
}
