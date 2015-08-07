package co.numberencoding.core.encoding;

import java.util.List;

import co.numberencoding.exception.T360Exception;

public interface EncodingMapper {
	
	public boolean isValidWord(Object word);
	public List<char[]> getListOfAllPossibleCharSequence(String str) throws T360Exception;
	
}
