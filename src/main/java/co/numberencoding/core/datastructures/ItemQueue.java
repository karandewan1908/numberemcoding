package co.numberencoding.core.datastructures;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import co.numberencoding.exception.T360Exception;
import co.numberencoding.log.Severity;

public class ItemQueue<T> implements Queue<T> {

	//we will use array blocking queue..
	
	private ArrayBlockingQueue<T> queue;
	
	private int queueSize = 0;
	
	public ItemQueue(int size) {
		this.queueSize = size;
		queue = new ArrayBlockingQueue<T>(queueSize);
		
	}

	public T peek() {
		return (T) this.queue.peek();
	}

	public T poll(long timeout,TimeUnit unit) throws T360Exception {
		try {
			return (T) this.queue.poll(timeout,unit);
		} catch (InterruptedException e) {
			throw new T360Exception(e,Severity.Error);
		}
	}

	public void add(T item) {
		
		this.queue.add(item);
	}

	public void put(T item) throws T360Exception {
		try {
			this.queue.put(item);
		} catch (InterruptedException e) {
			throw new T360Exception(e,Severity.Error);
		}
	}
	
	public boolean isEmpty() {
		return this.queue.isEmpty();
	}

	public T poll() {
		return (T) this.queue.poll();
		
	}
	
	
}
