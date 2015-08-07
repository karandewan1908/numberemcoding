
/**
 * Entry point for the system..
 * 
 * Problem : Convert number string into phrases using the dictionary and character encoding provided..
 * Algorithm: Step1: Take All possible characters combinations and try to make all possible words (Term.java) 
 * Step2: Combine all the words to make phrase from them (Phrase.java)
 * Step3: Expression is collection of phrases that mean same thing (Encoding of the same number) (Expression.java)
 * Step4: Repeat same whole logic by replacing characters by digit. 
 * 
 * 
 * We are using Executors and also used locking of java to make it much more concurrent.. Otherwise one can see lot of bbottleneck in performance.
 * Refer Readme for detail about whole problem.. main/resource/read.me
 */

package co.numberencoding.NumberEncoding;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import co.numberencoding.core.datastructures.Item;
import co.numberencoding.core.datastructures.ItemQueue;
import co.numberencoding.core.datastructures.Queue;
import co.numberencoding.core.datastructures.StringItem;
import co.numberencoding.core.dictionary.Dictionary;
import co.numberencoding.core.dictionary.DictionaryManager;
import co.numberencoding.core.encoding.EncodingMapper;
import co.numberencoding.core.encoding.PhoneStringEncoder;
import co.numberencoding.core.encoding.PhoneStringMapping;
import co.numberencoding.exception.T360Exception;
import co.numberencoding.log.Logger;
import co.numberencoding.log.Severity;

public class NumberEncoderImpl implements NumberEncoder{
	
	private InputStream numberEncodingFile;
	private InputStream dictionaryFile;
	private static final int concurrency = 5;//this is the number threads we will build.. also we will read this many elements each time and add to the queue
	private Queue<Item> queue;
	private Dictionary dict;
	
	public NumberEncoderImpl(InputStream numberEncodingFile, InputStream dictionaryFile) throws T360Exception {
		
		if(numberEncodingFile == null ) {
			throw new T360Exception(new IOException("Number encoding file stream null"), Severity.Error);
		}
		
		if(dictionaryFile == null ) {
			throw new T360Exception(new IOException("Dictionary file stream null"), Severity.Error);
		}
		
		this.numberEncodingFile = numberEncodingFile;
		
		this.dictionaryFile = dictionaryFile;
		
		this.queue = new ItemQueue<Item>(concurrency);
		
		EncodingMapper mapper = new PhoneStringMapping();
		
		DictionaryManager manager = new DictionaryManager(mapper,dictionaryFile);
		
		this.dict = manager.getDictionary();
		
		Logger.setOutputStream(System.out, true);
		
		Logger.setErrorOutputStream(System.err, true);
	}
	
	
	public int encode() throws T360Exception{
		
		
		int ret = 0;
		//thread to read file 
		//thread to encode each number..
		
		EncodingFileReader fileReader = new EncodingFileReader(this.numberEncodingFile,this.queue);
		ExecutorService executorService = Executors.newFixedThreadPool(concurrency);
		
		List<Callable<Object>> encoders = new ArrayList<Callable<Object>>();
		
		for(int i=0;i<concurrency;i++) {
			
			Encoder enc = new Encoder(this.queue,this.dict);
			encoders.add(enc);
		}
		try {
			
			fileReader.start();
			
			do{
				List<Future<Object>> futures = executorService.invokeAll(encoders);
			}while( !this.queue.isEmpty());
			
			
		} catch (InterruptedException e) {
			throw new T360Exception(e, Severity.Error);
		} finally{
			executorService.shutdownNow();
			
			
		}
		
		return ret;
		
	}
	
	public static void main(String[] args) throws T360Exception, URISyntaxException {
		
		InputStream phoneNumbers = NumberEncoderImpl.class.getResourceAsStream("/PhoneNumber.txt");
		InputStream dictFile = NumberEncoderImpl.class.getResourceAsStream("/phonenumber.dict");
		
		
		
		NumberEncoder enc = new NumberEncoderImpl(phoneNumbers,dictFile);
		enc.encode();
		System.exit(0);
	}
	
	
	

}

class EncodingFileReader extends Thread{
	
	private BufferedReader input;
	private Queue<Item> queue;
	
	EncodingFileReader(InputStream input, Queue<Item> queue) throws T360Exception {
		this.queue = queue;
		if(input == null ) {
			throw new T360Exception(new IOException("Input stream invalid "), Severity.Error);
		}
		
		this.input = new BufferedReader(new InputStreamReader(input));
		
		
	}
	
	public void run() {
		
		//we will just read till we dont reach end...
		
		
		String readLine = null;
		
		try {
			while((readLine = this.input.readLine()) != null && !this.isInterrupted()) {
				
				Item item = new StringItem(readLine);
				
				try {
					this.queue.put(item);
				} catch (T360Exception e) {
					Logger.log(e.getMessage()+" Line read: "+readLine, Severity.Warning);
				}
				
				
			}
			
		} catch (IOException e) {
			Logger.log(e.getMessage(), Severity.Error);
		}finally {
			try {
				this.input.close();
			} catch (IOException e) {
				Logger.log(e.getMessage(), Severity.Error);
			}
		}
		
		
		
	}
	
	
}

class Encoder implements Callable<Object> {
	
	private Queue<Item> queue;
	private Dictionary dict;
	
	Encoder(Queue<Item> queue, Dictionary dict){
		this.queue = queue;
		this.dict = dict;
	}
	

	public Object call() throws Exception {
		
		Item item = null;
		
		try {
			
			while((item = this.queue.poll()) != null&& !Thread.currentThread().isInterrupted() ) {
				
				PhoneStringEncoder encoder = new PhoneStringEncoder(dict);
				encoder.encode(item.toString());
				
			}
		} catch (T360Exception e) {
			Logger.log(e.getMessage(), Severity.Warning);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	
}