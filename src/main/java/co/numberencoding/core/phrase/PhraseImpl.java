package co.numberencoding.core.phrase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import co.numberencoding.core.term.Term;
import co.numberencoding.core.term.TermImpl;

public class PhraseImpl implements Phrase {

	
	private List<Term> terms;
	
	
	public PhraseImpl() {
		this.terms = new CopyOnWriteArrayList();
	}
	
	public void addTermToList(Term t) {
		this.terms.add(t);
	}
	
	public List<Term> getTerms() {
		return this.terms;
	}
	
	public String toString() {
		String ret = null;
		
		if(this.terms != null && this.terms.size() > 0) {
			
			StringBuilder builder = new StringBuilder();
			
			for(Term t : this.terms) {
				builder.append(t.toString()+" ");
			}
			
			ret = builder.toString();
			
		}
		return ret;
	}

	public Phrase combinePhrases(List<Phrase> phrases) {
		//get all terms and make new phrase..
		
		Phrase ret = new PhraseImpl();
		for(Phrase p : phrases) {
			List<Term> terms = p.getTerms();
			for(Term t: terms) {
				ret.addTermToList(t);
			}
		}
		
		return ret;		
	}

	public Phrase convertObjectToPhrase(Object o) {
		Phrase ret = new PhraseImpl();
		Term t = new TermImpl(o.toString().toCharArray());
		ret.addTermToList(t);;
		return ret;
	}
	
}
