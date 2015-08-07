package co.numberencoding.core.datastructures;

import java.util.HashSet;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class IgnoringDictionary<T> implements IgnoreDictionary<T> {

	private HashSet<T> ignoreSet;
	
	private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock(true);
	
	public IgnoringDictionary() {
		this.ignoreSet = new HashSet<T>();
	}
	
	public void addIgnoreItem(T item) {
		rwl.writeLock().lock();
		this.ignoreSet.add(item);
		rwl.writeLock().unlock();
	}
	
	public boolean containTerm(T t) {
		boolean ret = false;
		rwl.readLock().lock();
		ret = this.ignoreSet.contains(t);
		rwl.readLock().unlock();
		
		return ret;
	}
	
	
	
}
