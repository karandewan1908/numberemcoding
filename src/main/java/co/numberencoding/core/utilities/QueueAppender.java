package co.numberencoding.core.utilities;

import java.util.List;

import co.numberencoding.core.datastructures.Queue;
import co.numberencoding.exception.T360Exception;

public class QueueAppender<T> implements Runnable {

	
	private Queue<T> queue;
	private List<T> itemsList;
	
	public QueueAppender(Queue<T> queue,List<T> itemList) {
		this.queue = queue;
		this.itemsList = itemList;
	}
	public void run() {
		
		for(T item: itemsList) {
			if(Thread.currentThread().isInterrupted()) {
				break;
			}
			try {
				this.queue.put(item);
			} catch (T360Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

}
