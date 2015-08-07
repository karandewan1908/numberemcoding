package co.numberencoding.core.phrase;

import java.util.List;

import co.numberencoding.core.term.Term;

public interface Phrase {
	public void addTermToList(Term t);
	public List<Term> getTerms();
	public Phrase combinePhrases(List<Phrase> phrases);
	public Phrase convertObjectToPhrase(Object o);
}
