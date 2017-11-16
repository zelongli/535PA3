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
			for(Element qe : queue) {
				if(e.weight > qe.weight) {//FCFS when e's weight
					--counter;			// move ahead 1 place
				}else {
					break;
				}
			}
			queue.add(counter, e); // insert into that position
		}
	}
	
	public Element extract() {
		return queue.poll();
	}
}
