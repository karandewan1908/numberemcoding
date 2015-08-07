package co.numberencoding.core.dictionary;

import java.util.List;

public interface Dictionary<T> {
	
	public boolean conatinsWord(T word);
	public void addWord(T word);
	public T getSearbleTerm(Object o);
	public boolean containsObject(Object obj);
	public List<T> getSetWordsMatching(Object o);
	public int getSizeOfWord(Object word);
	public int getMinimumSizeOfWord();
	
}
