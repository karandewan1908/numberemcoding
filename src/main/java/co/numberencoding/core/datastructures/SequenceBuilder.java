package co.numberencoding.core.datastructures;

import java.util.ArrayList;
import java.util.List;

public class SequenceBuilder {

	//we use the tree structure to build the sequence.. 
	//its actually not a tree data structure but if you try to imagine what it is doing.. 
	//it build the sequence from the leaf then adds parent to each leaf and build new sequence and remove the previoud sequence..
	//then add parent parent to these new sequence and create new sequence.. 
	//	1		 / \
	//	2	   /\  /\	so its start from 3 keep all elements in a each sequence then moves to level 2 add every element to all sequence from level 3
	//	3	  /\/\/\/\	and remove all sequences build at level 3 and keep hold of new sequences.. repeat till the root and hence you get all possible sequences..
	public<T> List<List<T>> buildSequence(List<T[]> data ){
		
		//step 1
		SequenceTree<T> tree = new SequenceTree<T>(data);
		
		tree.buildSequences();
		
		return tree.getSequences();
		
	}
	
	public<T> List<List<T>> buildSequenceFromList(List<List<T>> data) {
		
		 List<T[]> arrList = new ArrayList<T[]>();
		 
		 for(List<T> eachArr : data){
			 T[] newArr = (T[]) eachArr.toArray();
			 arrList.add(newArr);
			 
		 }
		 
		 return buildSequence(arrList);
	}
	
	
	
}
