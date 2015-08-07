package co.numberencoding.core.datastructures;

public interface IgnoreDictionary<T> {
	public void addIgnoreItem(T item);
	public boolean containTerm(T t);
}