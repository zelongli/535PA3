package pa3;

import java.util.HashSet;
import java.util.LinkedList;

public class WeightedQ { // un-weighted Q is also OK
	LinkedList<Element> queue = new LinkedList<>();
	HashSet<String> visited = new HashSet<String>();
	
	public WeightedQ() {
		//do nothing
	}
	
	public void add(Element e) {
		
		if(!visited.contains(e.item)) {//if this is the first time item i is visited
			visited.add(e.item);
			
			int counter = queue.size();
			for (int i = counter; i >0 ; --i) {
				if(e.weight > queue.get(i-1).weight ) {
					--counter;
				}
			}
			queue.add(counter, e); // insert into that position
		}
//		for(int i = 0; i < queue.size(); ++i) {
//			System.out.println(queue.get(i).weight);
//		}
//		System.out.println("end");
	}
	
	public Element extract() {
		return queue.poll();
	}
}
