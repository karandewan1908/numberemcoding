package co.numberencoding.core.datastructures;

import java.util.concurrent.TimeUnit;

import co.numberencoding.exception.T360Exception;

public interface Queue<T> {

	public T peek();
	public T poll(long timeout,TimeUnit unit) throws T360Exception;
	public void add(T item);
	public void put(T item) throws T360Exception;
	public boolean isEmpty();
	public T poll();
}
