
package co.numberencoding.core.dictionary;

import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class PhoneNumberEncodingDictionary implements Dictionary<Word> {
	
	
	private ConcurrentHashMap<Character, SetOfWords> dictionary;
	
	private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock(true);
	
	private int min = -1;
	
	PhoneNumberEncodingDictionary() {
		this.dictionary = new ConcurrentHashMap<Character, SetOfWords>();
	}
	
	public int getMinimumSizeOfWord() {
		return min;
	}
	
	public boolean containsObject(Object obj) {
		
		
		boolean ret = false;
		
		Word searchableTerm = this.getSearbleTerm(obj);
		
		if(searchableTerm != null) {
			
			ret = this.conatinsWord(searchableTerm);
		}
		
		return ret;
	}
	
	public void addWord(Word w) {
		
		String cw = (String) w.getCleanWord();
		if(cw != null && cw.length() > 0) {
			
			char fc = cw.charAt(0);
			fc = Character.toLowerCase(fc);
			SetOfWords words = null;
			try {
				rwl.writeLock().lock();
				
				if(!this.dictionary.containsKey(fc)) {
					words = new SetOfWords(fc);
					this.dictionary.put(fc, words);
				}else{
					words = this.dictionary.get(fc);
				}
				
			}finally{
				rwl.writeLock().unlock();
			}
			//add is again locked to we release the write lock so that reads and write can continue..
			if(words != null) {
				if(this.min == -1 || this.min > w.getSize()) {
					this.min = w.getSize();
				}
				words.add(w);
			}
		}
	}
	
	
	public boolean conatinsWord(Word w) {

		boolean ret = false;
		String cw = (String) w.getCleanWord();
		if(cw != null && cw.length() > 0) {
			char fc = cw.charAt(0);
			fc = Character.toLowerCase(fc);
			SetOfWords word = null;
			try {
				rwl.readLock().lock();
				if(this.dictionary.containsKey(fc)) {
					
					word = this.dictionary.get(fc);
				}
				
				
			}finally{
				rwl.readLock().unlock();
			}
			if(word != null) {
				ret = word.hasWord(w);
			}
			
			
		}
		return ret;
	}
	
	public String toString() {
		return this.dictionary.toString();
	}
	

	public Word getSearbleTerm(Object o) {
		Word w = null;
		
		if(o != null) {
			//this is just 
			w = new Word(Word.getCleanSearchableWord(o.toString()));
		}
		
		return w;
	}

	public List<Word> getSetWordsMatching(Object o) {
		List<Word> ret = null;
		Word searchableword = this.getSearbleTerm(o);
		if(searchableword != null) {
			
			SetOfWords set = null;
			String cw = (String) searchableword.getCleanWord();
			
			if(cw != null && cw.length() > 0) {
				char fc = cw.charAt(0);
				fc = Character.toLowerCase(fc);
				try {
					rwl.readLock().lock();
					if(this.dictionary.containsKey(fc)) {
						set = this.dictionary.get(fc);
				
					}
	
				}finally{
					rwl.readLock().unlock();
				}
				
				if(set != null) {
					
					ret = set.getAllSimilarWords(searchableword);
					
				}
			}
		}
		return ret;
	}


	
	public int getSizeOfWord(Object word) {
		Word w = getSearbleTerm(word);
		int ret = w.getCleanWord().length();
		return ret;
	}


}

class SetOfWords {
	
	private char c;
	
	private TreeSet<Word> setofWords = new TreeSet<Word>(new WordCom());
	
	private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock(true);
	
	SetOfWords(char c) {
		this.c = c;
	}
	
	
	public boolean hasWord(Word word){
		
		boolean ret = false;
		
		if(setofWords != null) {
			try{
				rwl.readLock().lock();
			
				if(setofWords.contains(word)){
					ret = true;
				}
			}finally {
				rwl.readLock().unlock();
			}
		}
		
		return ret;
	}
	
	public List<Word> getAllSimilarWords(Word w) {
		List<Word> ret = null;
		try{
			//benefit of reentrant lock..
			//since we have this now state wont change between contain and get now..
			rwl.readLock().lock();
			
			Word searchAbleWord = Word.getCleanSearchableWord(w.getWord());
			
			Word winset = this.setofWords.floor(searchAbleWord);
			
			if(this.hasWord(winset)) {
				//we should do clean the word and lower it too.
				
				ret = winset.getSameWords();
				
			}
		}finally {
			rwl.readLock().unlock();
		}
		return ret;
		
	}
	
	public void add(Word w) {
		
		
		boolean foundsimilar = false;
		Word sword = null;
		try{
			rwl.writeLock().lock();
			//we should just check for existence if its there then compare with non clean verison and add to the list in word..
			if(setofWords.contains(w)) {
				//we can relase lock and add to word thats all..
				foundsimilar = true;
				sword = setofWords.floor(w);
			}else{
				
				setofWords.add(w);
			}
		}finally {
			rwl.writeLock().unlock();
		}
		if(foundsimilar && sword != null) {
			sword.addWord(w);
		}
	}
	
	private class WordCom implements Comparator<Word>{

		public int compare(Word o1, Word o2) {
			String s1 = o1.getCleanWord();
			String s2 = o2.getCleanWord();

			return s1.compareTo(s2);

		}
	}
	
	public String toString() {
		return this.setofWords.toString();
	}
	
	
	
	
	
	
}




