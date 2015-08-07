package co.numberencoding.core.expression;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import co.numberencoding.core.phrase.Phrase;
import co.numberencoding.core.phrase.PhraseImpl;

public class ExpressionImpl implements Expression {
	
	private List<Phrase> phrases;
	public ExpressionImpl() {
		this.phrases = new CopyOnWriteArrayList<Phrase>();
	}
	
	public ExpressionImpl(List<Phrase> p) {
		this.phrases = new CopyOnWriteArrayList<Phrase>(p);
	}
	
	public void addPhraseToList(Phrase p) {
		this.phrases.add(p);
	}
	
	public List<Phrase> getPhrases() {
		return this.phrases;
	}
	
	public boolean clear() {
		boolean ret = false;
		
		if(this.phrases != null) {
			this.phrases.clear();
			ret  = true;
		}
		
		return ret;
	}
	
	public void addAll(Collection<Phrase> phrases) {
		this.phrases.addAll(phrases);
	}
	
	public String toString() {
		String ret = null;
		
		if(this.phrases != null && this.phrases.size() > 0) {
			
			StringBuilder builder = new StringBuilder();
			
			for(Phrase p : this.phrases) {
				builder.append(p.toString()+" ");
			}
			
			ret = builder.toString();
			
		}
		return ret;
	}

	public boolean removePhrase(Phrase p) {
		boolean ret = false;
		if(this.phrases != null && this.phrases.contains(p)) {
			ret = this.phrases.remove(p);
		}
		return ret;
	}

	public boolean removePhraseAtIndex(int index) {
		// TODO Auto-generated method stub
		boolean ret = false;
		if(this.phrases != null && this.phrases.size() > index) {
			this.phrases.remove(index);
			ret = true;
		}
		return ret;
	}
	
	

}
