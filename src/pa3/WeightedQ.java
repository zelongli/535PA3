package pa3;

import java.util.HashSet;
import java.util.LinkedList;

public class WeightedQ {
	LinkedList<Element> queue = new LinkedList<>();
	HashSet<String> visited = new HashSet<String>();
	
	public WeightedQ() {
		//do nothing
	}
	
	public void add(Element e) {
		
		if(visited.contains(e.item)) {
			visited.add(e.item);
			int counter = 0;
			for(Element qe : queue) {
				if(qe.weight >= e.weight) {//even if = should ++ since fcfs
					++counter;
				}else {
					break;
				}
			}
			queue.add(e);
		}
	}
	
	public Element extract() {
		int index = 0;
		double currentmax = 0.0;
		
		for(int i = 0; i < queue.size(); ++i) {
			if(currentmax<queue.get(i).weight) {
				index = 0;
			}
		}
		
		Element temp = queue.get(index);
		queue.remove(index);
		
		return temp;
		
	}
}
