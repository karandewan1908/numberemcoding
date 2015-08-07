

package co.numberencoding.core.encoding;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Pattern;

import co.numberencoding.core.datastructures.IgnoreDictionary;
import co.numberencoding.core.datastructures.IgnoringDictionary;
import co.numberencoding.core.datastructures.ItemQueue;
import co.numberencoding.core.datastructures.Queue;
import co.numberencoding.core.datastructures.SequenceBuilder;
import co.numberencoding.core.dictionary.Dictionary;
import co.numberencoding.core.phrase.Phrase;
import co.numberencoding.core.phrase.PhraseImpl;
import co.numberencoding.core.term.Term;
import co.numberencoding.core.utilities.QueueAppender;
import co.numberencoding.core.utilities.Utilities;
import co.numberencoding.exception.T360Exception;
import co.numberencoding.log.Severity;

public class PhoneStringEncoder implements Encoder<String> {

	
	private EncodingMapper mapper;
	private Dictionary dict;
	private Printer printer;
	private WithDigitEncoding digitizer;
	private HashSet<Integer> allStartingIndex = new HashSet<Integer>();
	private IgnoreDictionary<Term> invalidTerms = new IgnoringDictionary<Term>();
	
	
	private String pattern = "[^0-9\\-\\/]+";
	private Pattern inputValidtor = Pattern.compile(pattern);
	private SequenceBuilder sequenceBuilder = new SequenceBuilder();
	private boolean foundEncoding = false;
	
	public PhoneStringEncoder(Dictionary dict) {
		mapper = new PhoneStringMapping();

		this.dict = dict;
		
		printer = new Printer();
		
		digitizer = new WithDigitEncoding();
		
	}
	
	
	
	private List<char[]> allPossibleCharSeq(String input) throws T360Exception {
		return mapper.getListOfAllPossibleCharSequence(input);
	}
 	 
	public boolean encode(String maininput) throws T360Exception {
		
		if(!isInputValid(maininput)) {
			throw new T360Exception(new Exception("Invalid input"),Severity.Error);
		}
		
	
		
		//lets just remove the unwanted characters..
		String input = cleanTheInput(maininput);
		
		
		List<Phrase> phrases = findAllPhrases(input);
		if(phrases!= null) {
			foundEncoding = true;
			for(Phrase phrase : phrases) {
			
				printer.printResultAndEachWordStartIndex(maininput,phrase);
			}
		}
		
		digitizer.setActualInput(maininput);
		digitizer.setCleanInput(input);
		digitizer.doDigitEncoding();
		
		boolean founddigitalEncoding = digitizer.foundedEncoding();
		
		if(!foundEncoding && !founddigitalEncoding) {
			printer.printResultAndEachWordStartIndex(maininput,null);
		}
		
		return foundEncoding;
	}
	
	
	private List<Phrase> findAllPhrases(String input) throws T360Exception {
		
		List<Phrase> ret = null;
		
		final List<char[]> chseq = this.allPossibleCharSeq(input);
		
		int totalThread = 50;
		
		final int totalChSeq = chseq.size();
		int queuesize = Math.floorDiv(totalChSeq,2);
		if(chseq.size() < 5) {
			totalThread = chseq.size();
			queuesize = totalThread;
		}
		
		Queue<char[]> chQueue = new ItemQueue<char[]>(queuesize);
		
		QueueAppender<char[]> appender = new QueueAppender<char[]>(chQueue,chseq);
		
		Thread appendThread = new Thread(appender);
		
		appendThread.start();
		
		List<Callable<Object>> expressionFinder = new ArrayList<Callable<Object>>();
		
		ExecutorService executorService = Executors.newFixedThreadPool(totalThread);
		
		for(int i=0;i<totalThread;i++) {
			ExpressionFinder finder = new ExpressionFinder(this.dict,chQueue,this.invalidTerms);
			expressionFinder.add(finder);
		}
		List<Future<Object>> futures = null;
		try{
			do{
				
				try {
					futures = executorService.invokeAll(expressionFinder);
					
					for(Future<Object> f: futures) {
						
						try {
							List<Phrase> result =(List<Phrase>) f.get();
							
							if(result != null) {
								
								if(ret == null) {
									ret = new ArrayList<Phrase>();
								}
							
								ret.addAll(result);
							}
							
						} catch (ExecutionException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}while(!chQueue.isEmpty());
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			executorService.shutdownNow();
		}
		
		return ret;
	}
	
	
	
	
	
	
	private String cleanTheInput(String input) {
		return Utilities.cleanInput(input);
	}
	
	
	private boolean isInputValid(String input) {
		boolean ret = true;
		
		if(this.inputValidtor.matcher(input).find()) {
			ret = false;
		}
		
		return ret;
	}
	
	private class Printer {
		
		private void printResultAndEachWordStartIndex(String input, Phrase phrase) {
			
			if(phrase == null) {
				System.out.println(input+": ");
				return;
			}
			List<Term> terms = phrase.getTerms();
			List<Object[]> allPossibleTerms = new ArrayList<Object[]>();
				
			for(Term t : terms) {
				
				List<Object> list = dict.getSetWordsMatching(t);
				
				Object[] termarr = null;
				
				if(list == null) {
					
					termarr = new Object[]{t};
					
				}else{
					
					termarr = list.toArray();
				
				}
				
				allPossibleTerms.add(termarr);
			
			}
				
			List<List<Object>> allPhrases = sequenceBuilder.buildSequence(allPossibleTerms);
				
			for(List<Object> pl : allPhrases) {
					
				StringBuilder actualPharse = new StringBuilder();
				
				boolean fa = false;
				
				int pointOfTermStart = 0;
				
				for(Object o : pl) {
					
					if(dict.containsObject(o)) {
		
						allStartingIndex.add(pointOfTermStart);
					
					}
					
					if(fa) {
						
						actualPharse.append(" ");
					
					}
						
					String term = o.toString();
					
					
					actualPharse.append(term);
						
					pointOfTermStart += dict.getSizeOfWord(o);
						
					fa = true;
						
				}		
				
				System.out.println(input+": "+ actualPharse.toString());
					
					
			}
			
			
		}
		
	}
	
	
	private class WithDigitEncoding {
		
		private Phrase helperp = new PhraseImpl();
		
		private String actualInput;
		
		private String cleanInput;
		
		private boolean foundEncoding = false;
		
		private WithDigitEncoding(String ai, String ci) {
			cleanInput = ci;
			actualInput = ai;
		}
		
		public boolean foundedEncoding() {
			
			return foundEncoding;
		}

		public void setCleanInput(String input) {
			this.cleanInput = input;
			
		}

		public void setActualInput(String maininput) {
			this.actualInput = maininput;
		}

		private WithDigitEncoding(){
			
		}
		
		//according to rules of bucket that each digit should be at least 1 elements away from each other plus they shouldn't be one at which valid word can be found in dict..
		//for second we have hashset from outer class..
		private List<HashSet<Integer>> bucketBuild(int startingElement, int bucketSize) {
			List<HashSet<Integer>> buckets = new ArrayList<HashSet<Integer>>();
			
			if(bucketSize == 0 || allStartingIndex.contains(startingElement)) {
				return null;
			}
			
			if(bucketSize == 1) {
				
				HashSet<Integer> bucket = new HashSet<Integer>();
				bucket.add(startingElement);
				buckets.add(bucket);
				
			}else{
				int startingPoint = startingElement - 2;
				int loopindex = startingPoint;
				
				while(true) {
				
					HashSet<Integer> bucket = new HashSet<Integer>();
					
					
					int bucketindex = bucketSize - 2;
					int previousElemnt = -1;
					
					while(loopindex>= 0 && bucketindex >= 0) {
						if(!allStartingIndex.contains(loopindex) && ((previousElemnt - loopindex) != 1)) {
							bucket.add(loopindex);
							previousElemnt = loopindex;
							bucketindex--;
							
						}
						loopindex--;
						
					}
					
					
					if(bucketindex == -1 ) {
						bucket.add(startingElement);
						buckets.add(bucket);
						startingPoint = loopindex;
						
					}else{
						break;
					}
					
				}
				
			}
		
			
			return buckets;
			
			
		}
		
	
		
		private void buildExpressionsWithSkipping(HashSet<Integer> skippingBuckets) throws T360Exception {
			
			StringBuilder substr = new StringBuilder();
			boolean print = true;
			List<List<Phrase>> phrases = new ArrayList<List<Phrase>>();
			
			for(int i=0;i<cleanInput.length();i++) {
				
				if(skippingBuckets.contains(i)){
					if(substr.length() > 0) {
						List<Phrase> pharsespart = findAllPhrases(substr.toString());
						if(pharsespart != null &&pharsespart.size() > 0) {
							phrases.add(pharsespart);
						}else{
							print = false;
							break;
						}
					}
					List<Phrase> pp = new ArrayList<Phrase>();
					Phrase numberPhrase = helperp.convertObjectToPhrase(cleanInput.charAt(i)+"");
					pp.add(numberPhrase);
					phrases.add(pp);
					
					substr  = new StringBuilder();
					
				}else{
					
					substr.append(cleanInput.charAt(i));
				}
			}
			
			if(substr.length() > 0) {
				
				List<Phrase> pharsespart = findAllPhrases(substr.toString());
				if(pharsespart != null &&pharsespart.size() > 0) {
					phrases.add(pharsespart);
				}else{
					print = false;
				}
			}
			
			
			if(print && phrases != null && phrases.size() > 0) {
			
				List<List<Phrase>> allphrases= sequenceBuilder.buildSequenceFromList(phrases);
					
				//just need to print them now..
					
				if(allphrases!= null) {
					
					foundEncoding = true;
					
					for(List<Phrase> phrase : allphrases) {
							
							
						Phrase printablePhrase = helperp.combinePhrases(phrase);
						
						printer.printResultAndEachWordStartIndex(this.actualInput,printablePhrase);
							
					}
				}
				
			}
			
		}
		
		public void doDigitEncoding() throws T360Exception {
			
			if(cleanInput == null || cleanInput.isEmpty()) {
				throw new T360Exception(new Exception("Invalid input for digit encoding"), Severity.Error);
			}
			
			
			
			int maxBucketSize = (int) Math.ceil(cleanInput.length()/ 2); //this is maximum numbers that can exists together..
			
			int currentBucketSize = 1;
			
			
			while(currentBucketSize <= maxBucketSize) {
			
				for(int i = cleanInput.length() - 1;i >=0;i--) {
					
					List<HashSet<Integer>> buckets = bucketBuild(i, currentBucketSize);
					
					if(buckets != null && buckets.size() > 0) {
						
						for(HashSet<Integer> arr : buckets) {
							
							this.buildExpressionsWithSkipping( arr);
							
							
						}
						
						
					}
					
					
				}
				currentBucketSize += 1;
			}
			
			
			
		}
		
	}
	
}
