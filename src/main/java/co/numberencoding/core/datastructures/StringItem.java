package co.numberencoding.core.datastructures;

public class StringItem implements Item{

	private String item = null;
	
	
	public StringItem(String item) {
		this.item = item;
	}
	
	public String get() {
		return this.item;
	}
	
	public String toString() {
		return this.item;
	}
	
	
}
