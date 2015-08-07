package co.numberencoding.core.encoding;

import co.numberencoding.exception.T360Exception;

public interface Encoder<T> {

	public boolean encode(T str) throws T360Exception;
	
	
}
