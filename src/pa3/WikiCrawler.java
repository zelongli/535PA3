package pa3;

import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;

public class WikiCrawler {
	String site = "https://en.wikipedia.org";
	WeightedQ queue = new WeightedQ();
	HashSet<String> kwords;;
	int max;
	
	public WikiCrawler(String seedURL, String[] keywords, int max, String fileName) {
		Element seed = new Element(seedURL, 1);
		queue.add(seed);
		this.kwords = new HashSet<String>(Arrays.asList(keywords));
		this.max = max;
	}
	
	public void crawl(){
		
	}
}
