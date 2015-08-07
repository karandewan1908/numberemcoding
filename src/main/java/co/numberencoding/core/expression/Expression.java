package co.numberencoding.core.expression;

import java.util.Collection;
import java.util.List;

import co.numberencoding.core.phrase.Phrase;

public interface Expression {
	public void addPhraseToList(Phrase p) ;
	public List<Phrase> getPhrases();
	public boolean removePhrase(Phrase p);
	public boolean removePhraseAtIndex(int index);
	public void addAll(Collection<Phrase> phrases);
	public boolean clear();
	
	
}