package pa3;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Lets's start");
		String[] keywords = {"tennis", "grand slam", "french open", "australian open", "wimbledon", "US open", "masters"};
		
		WikiCrawler wkcrl = new WikiCrawler("/wiki/Tennis",keywords,100,"WikiTennisGraph.txt",true);
		wkcrl.crawl();
	}

}
