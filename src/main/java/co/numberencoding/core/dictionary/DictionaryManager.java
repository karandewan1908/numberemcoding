/**
 * This is the manager that returns the right dictionary needed for making lookups while encoding
 * This also know encoding library..
 * So it help dictionary builder to build dictionary object using encoding library.
 * 
 *  
 * author: Karan Dewan
 * 
 */
package co.numberencoding.core.dictionary;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URISyntaxException;

import co.numberencoding.core.encoding.EncodingMapper;
import co.numberencoding.exception.T360Exception;
import co.numberencoding.log.Severity;

public class DictionaryManager {

	
	private DictionaryBuilder builder;
	private Dictionary dict;
	private EncodingMapper mapper;
	private InputStream dictionaryFile;
	
	
	public DictionaryManager(EncodingMapper mapper, File dictionaryFile) throws T360Exception {
		builder = new DictionaryBuilder();
		this.mapper = mapper;
		if(dictionaryFile == null || !dictionaryFile.exists()) {
			throw new T360Exception(new Exception("Invalid dictionary file"),Severity.Error);
		}
		try {
			this.dictionaryFile = new FileInputStream(dictionaryFile);
		} catch (FileNotFoundException e) {
			throw new T360Exception(e,Severity.Error);
		}
	}
	
	
	public DictionaryManager(EncodingMapper mapper, InputStream dictionaryFile) {
		builder = new DictionaryBuilder();
		this.mapper = mapper;
		this.dictionaryFile = dictionaryFile;
	}
	
	
	//probably name of dictionary could be used in future to builder different encoders and not just phone numer..
	//but for now we keep it simple..
	
	public Dictionary getDictionary() throws T360Exception {
		
		if(this.dict == null) {
			this.buildDictionary();
		}
		return this.dict;
		
		
	}
	
	private void buildDictionary() throws T360Exception {
		
		this.dict = builder.buildDictionaryFromFile(this.dictionaryFile,mapper);
		
	}
	
	
}
