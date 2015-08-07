package co.numberencoding.core.encoding;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import co.numberencoding.core.datastructures.SequenceBuilder;
import co.numberencoding.exception.T360Exception;
import co.numberencoding.log.Severity;

public class PhoneStringMapping implements EncodingMapper{

	private final ArrayList<char[]> listOfNumberToLetter = new ArrayList<char[]> ();
	
	private Pattern vPatt;
	
	private SequenceBuilder sequenceBuilder = new SequenceBuilder();
	
	public PhoneStringMapping() {
		buildTheList();
	}
	
	private void buildTheList() {
		
		listOfNumberToLetter.add(new char[] {'e' ,'E'});
		listOfNumberToLetter.add(new char[] {'j','n', 'q','J','N','Q' });
		listOfNumberToLetter.add(new char[] { 'r', 'w', 'x','R','W','X' });
		listOfNumberToLetter.add(new char[] { 'd', 's', 'y','D','S','Y'});
		listOfNumberToLetter.add(new char[] { 'f', 't','T','F'});
		listOfNumberToLetter.add(new char[] {'a', 'M','m','A'});
		listOfNumberToLetter.add(new char[] { 'c', 'i', 'v','C','I','V'});
		listOfNumberToLetter.add(new char[] {'b','B', 'k', 'u','K','U' });
		listOfNumberToLetter.add(new char[] {'l', 'o', 'p','L','O','P' });
		listOfNumberToLetter.add(new char[] {'g' ,'h', 'z' ,'G','H','Z'});
		
		String validityChecker= new String();
		
		for(char[] c : listOfNumberToLetter) {
			
			validityChecker += new String(c);
		}
		
		vPatt = Pattern.compile("["+validityChecker+"]");
		
		
		
	}
	
	
	//this will return arraylist of all possible characters sequences for a number
	
	//number of possibilties are {number of characters for digit one }*{number of characters for digit two} *...* {number of characters for digit n}
	//we can test this function using this..
	public List<char[]> getListOfAllPossibleCharSequence(String phNum) throws T360Exception {
		
		List<char[]> charSequence = new ArrayList<char[]>();
		List<Character[]> digitsCharSeq = new ArrayList<Character[]>();
		char[] phchs = phNum.toCharArray();
		for(char ch : phchs) {
			
			try{
				
				int chint = Character.getNumericValue(ch);
				
				char[] letters = this.listOfNumberToLetter.get(chint);
				Character[] charr = new Character[letters.length];
				for(int i=0;i<letters.length;i++) {
					charr[i] = letters[i];
				}
				digitsCharSeq.add(charr);
			}catch(Exception e) {
				throw new T360Exception(e,Severity.Error);
			}
			
		}
		
		//now we have digits chars lets make char sequences..
		
		//we can use expression t build sequences here..
		
		if(digitsCharSeq.size() > 0) {
			
			List<List<Character>> buildUpList = sequenceBuilder.buildSequence(digitsCharSeq);
			if(buildUpList.size() > 0) {
				
				for(List<Character> c : buildUpList) {
					int csize = c.size();
					
					char[] carr = new char[csize];
					
					for(int i =0;i<csize;i++) {
						carr[i] = c.get(i);
						
					}
					
					charSequence.add(carr);
					
				}
				
			}
			
		}
		
		return charSequence;
		
		
	}
	
	
	
	public char[] getCharactersForNumber(int i) {
		char[] ret = null;
		
		if(i > 0 && i < this.listOfNumberToLetter.size()) {
			
			ret = this.listOfNumberToLetter.get(i);
			
		}
		
		return ret;
	}

	public boolean isValidWord(Object word) {
		boolean ret = false;
		
		if(word != null && (word instanceof String)) {
			String ws = (String) word;
			String afValidCh = vPatt.matcher(ws).replaceAll("");
			if(afValidCh.isEmpty()) {
				ret = true;
			}
			
		}
		
		return ret;
	}
	
	
	
}
