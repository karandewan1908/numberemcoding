package co.numberencoding.core.datastructures;

import java.util.ArrayList;
import java.util.List;

public class SequenceTree <T> {
	
	private List<T[]> data;
	private List<List<T>> sequences = new ArrayList<List<T>>();

	public SequenceTree(List<T[]> data) {
		this.data = data;
	}
	
	public List<List<T>> getSequences() {

		return this.sequences;
	}
	
	public void buildSequences() {
		
		int size = data.size();
		
		for(int i = size - 1;i>=0;i--) {
			
			T[] t = data.get(i);
			if(sequences.size() == 0) {
			
				
				for(int j=0;j<t.length;j++) {
					List<T> sequence = new ArrayList<T>();
					sequence.add(t[j]);
					this.sequences.add(sequence);
				}
				
				
			}else{
				
				List<List<T>> newsequences = new ArrayList<List<T>>();
				
				for(List<T> s : sequences) {
					
					for(int j=0;j<t.length;j++) {
						List<T> sequence = new ArrayList<T>(s);
						sequence.add(0,t[j]);
						newsequences.add(sequence);
					}
					
				}
				
				sequences = newsequences;
				
			}
			
		}
		
		
	}
	
	
}
