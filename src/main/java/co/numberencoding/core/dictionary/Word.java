package co.numberencoding.core.dictionary;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;

/**
 * We just keep each word in its raw form..
 * While comparing we will use the clean word i.e, without the quotes and dash
 * 
 * Assumption: two words exactly same expect quotes or dashes?? Is that possible? Assumed its not a plausible case..
 * @author karan dewan
 *
 */
public class Word {
	
	private String word;
	private static Pattern unwantedCh = Pattern.compile("[\"-]");
	private List<Word> similarWords = new CopyOnWriteArrayList<Word>();
	
	public Word(String s) {
		this.word = s;
		similarWords.add(this);
	}
	
	public Word(Word w) {
		this.word = w.getWord();
		similarWords.add(this);
	}
	
	
	public void addWord(Word w) {
		
		String strw = w.getWord();
		
		if(!strw.equals(this.word)) {
			this.similarWords.add(w);
		}
	}
	
	public List<Word> getSameWords() {
		return similarWords;
	}
	
	public String getWord() {
		return this.word;
	}
	
	
	public int hashCode() {
		return this.getCleanWord().hashCode();
	}
	
	public boolean equal(Object w) {
		
		if(w == null) {
			return false;
		}
		
		if(!(w instanceof Word)) {
			return false;
		}
		
		Word w2 = (Word) w;
		//is this what we want
		return this.getCleanWord().equals(w2.getCleanWord());
	}
	
	public static Word getCleanSearchableWord(String s) {
		
		Word w = null;
		
		String cleanStr = getCleanString(s);
		
		if(cleanStr != null) {
			w = new Word(cleanStr.trim());
		}
		
		return w;
		
	}
	
	private static String getCleanString(String s) {
		return unwantedCh.matcher(s).replaceAll("");
	}
	
	public String getCleanWord() {
		String ret = null;
		
		if(word != null) {
			ret = Word.getCleanString(this.word);
			
		}
		
		return ret;
	}

	public String toString() {
		return this.word;
	}
	
	public int getSize(){
		return this.getCleanWord().length();
	}
}
