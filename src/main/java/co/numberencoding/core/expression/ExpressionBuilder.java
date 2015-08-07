package co.numberencoding.core.expression;

import java.util.ArrayList;
import java.util.HashSet;

import co.numberencoding.core.datastructures.IgnoreDictionary;
import co.numberencoding.core.phrase.Phrase;
import co.numberencoding.core.phrase.PhraseImpl;
import co.numberencoding.core.term.Term;
import co.numberencoding.core.term.TermImpl;



public class ExpressionBuilder {

	private int maxChars;
	private char[] chars;
	private IgnoreDictionary invalidTerms;
	private int minSizeTerm = 0;
	public ExpressionBuilder(char[] chars) {
		maxChars = chars.length ;
		this.chars = chars;
		
	}
	public ExpressionBuilder(char[] chars,IgnoreDictionary idic) {
		maxChars = chars.length ;
		this.chars = chars;
		this.invalidTerms = idic;
		
	}
	
	public void setMinSizeForTerm(int size) {
		this.minSizeTerm = size;
	}
	
	
	public Expression buildExpression() {
		
		Expression expression = null;
		
		
		//step1: lets make terms from the chars..
		
		//this is move forward terms..
		//term length can be anywhere from 1 character term to chars.length..
		
		ArrayList<Term> terms = buildTerms();
		
		
		ArrayList<Phrase> phrases = buildPhrases(terms);
		
		if(phrases != null && phrases.size() > 0) {
			expression = new ExpressionImpl(phrases);
			
		}
		
		
		
		return expression;
		
		
	}
	
	
	private ArrayList<Phrase> buildPhrases(ArrayList<Term> terms) {
		
		
		ArrayList<Phrase> phrases = null;
		
		if(terms != null && terms.size() > 0) {
			
			phrases = new ArrayList<Phrase>();
			for(Term t : terms) {
				
				
				int startind = t.getStartIndex();
				
				if(startind == 0) {
				
					ArrayList<Phrase> fewPhrases = searchTermsToBuildPhrase(t,terms);
					
					if(fewPhrases != null) {
						phrases.addAll(fewPhrases);
					}
				}
			}
			
			
			
		}
		
		return phrases;
	}
	
	
	private ArrayList<Phrase> searchTermsToBuildPhrase(Term startTerm,ArrayList<Term> terms) {
		
		ArrayList<Phrase> ret = null;
		
		if(startTerm != null && terms != null && terms.size() > 0) {
			
			ret = new ArrayList<Phrase>();
			
			int endidx = startTerm.getEndIndex();
			
			
			if((endidx +1) < this.maxChars) {
					
				for(Term t: terms) {
					
					int startindex = t.getStartIndex();
					
					if(endidx +1 == startindex) {
						
						ArrayList<Phrase> otherSubPhrases = searchTermsToBuildPhrase(t,terms);
						
						if(otherSubPhrases != null) {
							
							for(Phrase p : otherSubPhrases) {
								
								Phrase newp = new PhraseImpl();
								
								newp.addTermToList(startTerm);
								
								for(Term ep : p.getTerms()) {
									
									newp.addTermToList(ep);
									
								}
								
								ret.add(newp);
								
							}
							
							
							
						}
						
						
					}
					
				}
			}else{
				
				Phrase p = new PhraseImpl();
				p.addTermToList(startTerm);
				ret.add(p);
			}
			
			
		}

		return ret;
		
	}
	
	private ArrayList<Term> buildTerms() {
		
		ArrayList<Term> terms = null;
		
		if(chars != null && chars.length > 0) {
			//we will form terms by combining each characters.. 
			terms = new ArrayList<Term> ();
			int length = chars.length;
			for(int i=0;i<length;i++) {
				
				char[] c = new char[] {chars[i]};
				
				Term t = new TermImpl(c);
				
				
				
				t.setStartIndex(i);
				
				t.setEndIndex(i);
				//single character term..
				
				if(!this.invalidTerms.containTerm(t) && t.getSize() >= this.minSizeTerm) {
					terms.add(t);
				}
				
				
				
				
				for(int j=i + 1;j<length; j++) {
				
					char cc = chars[j];
					
					char[] newc = new char[c.length + 1];
					
					System.arraycopy(c, 0, newc, 0, c.length);
					
					newc[c.length] = cc;
					
					Term newterm = new TermImpl(newc);
					
					newterm.setStartIndex(i);
					
					newterm.setEndIndex(j);
					
					if(!this.invalidTerms.containTerm(newterm) && newterm.getSize() >= this.minSizeTerm) {
					
						terms.add(newterm);
					}
					
					c = newc;
					
				}
			}
		}
		
		return terms;
		
	}

}
