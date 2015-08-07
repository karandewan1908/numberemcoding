package co.numberencoding.core.encoding;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import co.numberencoding.core.datastructures.IgnoreDictionary;
import co.numberencoding.core.datastructures.Queue;
import co.numberencoding.core.dictionary.Dictionary;
import co.numberencoding.core.expression.Expression;
import co.numberencoding.core.expression.ExpressionBuilder;
import co.numberencoding.core.phrase.Phrase;
import co.numberencoding.core.term.Term;
import co.numberencoding.log.Logger;
import co.numberencoding.log.Severity;

public class ExpressionFinder implements Callable<Object> {
	private Dictionary dict;
	private Queue<char[]> queue;
	private IgnoreDictionary idict;
	public ExpressionFinder(Dictionary dict,Queue<char[]> queue,IgnoreDictionary idct){
		this.dict = dict;
		this.queue = queue;
		this.idict = idct;
	}
	
	public List<Phrase> findExpressions(char[] chseq) {
	
	List<Phrase> phrases = null;
	
	//This is slow as it also might be repeating some of the phrases..
	//lets make it more intelligent..

	ExpressionBuilder expressionbulder = new ExpressionBuilder(chseq,idict);
	
	int size = this.dict.getMinimumSizeOfWord();
	
	
	expressionbulder.setMinSizeForTerm(size);
	
	Expression expression = expressionbulder.buildExpression();
	
	
	if(expression != null) {
	
		//we just need to valid each expression and print the one that are valid..
		filterExpression(expression);
		
		if(expression.getPhrases().size() > 0) {
			
			
			phrases = expression.getPhrases();	
		
		}
	
	}
	
	return phrases;
	
	}

	public Object call() throws Exception {
		char[] chseq;
		
		List<Phrase> ret = null;
		chseq = this.queue.poll(1, TimeUnit.SECONDS);
		
		if(chseq != null) {
			
			ret = this.findExpressions(chseq);
		}
		return ret;
	}
	
	private void filterExpression(Expression expression) {
		//we just get the expression iterator on phrases and then iterator on term and validate then in dictionary..
		List<Phrase> phrases = expression.getPhrases();
		
		if(phrases != null) {
		
			ArrayList<Phrase> cleanList = new ArrayList<Phrase>();
			int size = phrases.size();
			for(int i=0;i<size;i++) {
				Phrase phrase = phrases.get(i);
			
				boolean valid = true;
				
				List<Term> terms = phrase.getTerms();
				for(Term term : terms) {
					
					if(!dict.containsObject(term)) {
						valid = false;
						this.idict.addIgnoreItem(term);
						break;
					}
					
					
				}
				
				if(valid) {
					cleanList.add(phrase);
				}
				
			}
			
			expression.clear();
			if(cleanList.size() > 0) {
				expression.addAll(cleanList);
			}
		
		}
		
	}
	
}
