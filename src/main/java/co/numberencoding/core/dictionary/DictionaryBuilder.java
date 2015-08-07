/**
 * 
 * Factory would be good here.. to load dictionary created with different rules..
 * but since we have only one dictionary we keep it simple here..
 */
package co.numberencoding.core.dictionary;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import co.numberencoding.core.encoding.EncodingMapper;
import co.numberencoding.exception.T360Exception;
import co.numberencoding.log.Severity;

public class DictionaryBuilder {
	
	private Dictionary dict = null;

	
	public synchronized Dictionary getDictionary() {
		return this.dict;
	}
	
	public synchronized Dictionary buildDictionaryFromFile(InputStream dictFile,EncodingMapper mapper) throws T360Exception {

		
		
		if(dictFile == null) {
			throw new T360Exception(new NullPointerException(),Severity.Error);
		}
		
		
		
		dict = new PhoneNumberEncodingDictionary();
		
		
		
		
		//lets go through input check with mapper if word is valid and if it then add to dictionary..
		
		String wordstr = new String();
		
		BufferedReader br = null;
		
		
		
		
		try {
			
			br = new BufferedReader(new InputStreamReader(dictFile));
			
			while((wordstr = br.readLine()) != null) {
				
				Word w = new Word(wordstr);
				
				if(mapper.isValidWord(w.getCleanWord())) {
					dict.addWord(w);
				}
				
			}
		} catch (IOException e) {
			throw new T360Exception(e, Severity.Error);
		}finally {
			try {
				if(br != null) {
					br.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return dict;
		
	}
	
	
}
