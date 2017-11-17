package pa3;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WikiCrawler {
	static final String BASE_URL = "https://en.wikipedia.org";
	int max;
	WeightedQ Wqueue = new WeightedQ();
	HashSet<String> disallowedURL;
	String[] keywords;
	ArrayList<String[]> edges;
	int visitedCounter = 0;
	HashSet<String> graphNodes;
	boolean weighted;
	String fileName;
	ArrayList<String> nodesingraph= new ArrayList<String>(100);
	
	public WikiCrawler(String seedURL, String[] keywords, int max, String fileName, boolean isWeighted) {
//		System.out.println(Wqueue.queue.size());
		this.max = max;
		this.keywords = keywords;
		this.edges = new ArrayList<String[]>();
		
		this.graphNodes = new HashSet<String>(max);
		nodesingraph.add(seedURL);
		graphNodes.add(seedURL);
		this.weighted = isWeighted;
		Element seed = new Element(seedURL, 1);
		Wqueue.add(seed);//add seed as the first node to crawl
		
		this.fileName = fileName;
		disallowedURL = new HashSet<String>();
		Pattern robotPattern = Pattern.compile("^(Disallow: )(/wiki/.*)");//robot disallowed URL pattern
		//first read robots.txt to be polite//be elegant, don't WU
		
		try {
			URL robotURL = new URL(BASE_URL + "/robots.txt");
			InputStream isRobot = robotURL.openStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(isRobot));
			String line;
			
			while((line = br.readLine()) != null){//read line by line
				Matcher robotMatcher = robotPattern.matcher(line);//match the disallow page
				String prohibitURL;
				if(robotMatcher.find()){
					prohibitURL = robotMatcher.group(2);//group(2) is the /wiki/*** 
				
					if((!prohibitURL.contains("#")) & (!prohibitURL.contains(":"))){//if is the link we need
						this.disallowedURL.add(prohibitURL);
//						System.out.println("disallowed URL find " + prohibitURL);
					}
				}
			}	
		}catch (MalformedURLException mue) {
	         mue.printStackTrace();
	    }catch (IOException ioe) {
	         ioe.printStackTrace();
	    }
	}
	

	
	
	
	
	
	public void BFS(String wikiURL) {
		
		if(++visitedCounter % 10 == 0) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		
		String line;
		StringBuilder sb = new StringBuilder(1024000);//build the entire doc
		HashSet<String> linksincurrentpage= new HashSet<String>();//to delete duplicate edges from same source
		try {
			URL url = new URL(BASE_URL + wikiURL);
			InputStream is = url.openStream();
		    BufferedReader br =new BufferedReader(new InputStreamReader(is));
		    
		    while ((line = br.readLine()) != null) {//skip before <p>
	            if(line.contains("<p>")) {
	            	break;
	            }
	        }
		    
		    sb.append(line);
		    sb.append(" ");
		    
		    while ((line = br.readLine()) != null) {
	            sb.append(line);
	            sb.append(" ");
	        }
		    br.close();
		   
		    }catch (MalformedURLException mue) {
		         mue.printStackTrace();
		    }catch (IOException ioe) {
		         ioe.printStackTrace();
		    }
		
		String fulltext = sb.toString();
		
		Pattern p = Pattern.compile("(.{1,50}?)(<a href=\")(/wiki/.*?)(\")(.*?)(>)(.*?)(</a>)(.{1,50})");
		Matcher m = p.matcher(fulltext);
		
		while(m.find()){
			String currentURL = m.group(3);//group 3 is the wiki part
///			System.out.println(currentURL+" OMGOMG");
			if(currentURL.contains("#") || currentURL.contains(":")) { //be polite,be elegant
				continue;
			}else {
				if(linksincurrentpage.contains(currentURL)) {//if the link has appeared in this page before
					continue;
				}
				
				linksincurrentpage.add(currentURL);
//				String[] temp = {wikiURL,currentURL};
//				edges.add(temp);
//			double weight = 0.0;
				boolean flag = true;
				
				if(!weighted) {
					Wqueue.add(new Element(currentURL,0));
					continue;
				}
							
				for(int i = 0; i < this.keywords.length;++i) {
					if(currentURL.toLowerCase().contains(keywords[i]) || m.group(7).toLowerCase().contains(keywords[i])){
						Wqueue.add(new Element(currentURL,1));
						flag = false;
						System.out.println("addin3g " + currentURL);
						break;
					}
					
				}
			
				if(flag) {
					String[] frontWords = m.group(1).toLowerCase().split("\\W+");
					int frontdistance = 30;
			
				outter_loop:
					for(int i = frontWords.length -1; i > 0; --i) {
						for(int j = 0; j < keywords.length; ++j) {
							if(frontWords[i].contains(keywords[j])) {
//								System.out.println("front words is " + frontWords[i]);
								frontdistance = frontWords.length -i;
								break outter_loop;
							}
						}
						
					}
//					System.out.println("front distance is " + frontdistance);
				
					String[] backWords = m.group(9).toLowerCase().split("\\W+");
					int backdistance = 30;
//					System.out.println("back words.leng is "+backWords.length);
		
				outter_loop:
					for(int i = 0 ; i < backWords.length; ++i) {
						for(int j = 0; j < keywords.length;++j) {
							if(backWords[i].contains(keywords[j])) {
//								System.out.println("back words is " + backWords[i]);
								backdistance = i + 1;
								break outter_loop;
								}
						}
					
					}
					
					int shortestDistance = Math.min(frontdistance, backdistance);
					System.out.println("adding " + currentURL);
					
					if(shortestDistance > 20) {
						Wqueue.add(new Element(currentURL,0));
					}else {
						Wqueue.add(new Element(currentURL,(1.0/(shortestDistance+2.0))));
					}				
				}
			}
		} 
		
		if(Wqueue.queue.size() < max) {
			String next = Wqueue.extract().item;
			nodesingraph.add(next);
			graphNodes.add(next);
	
			BFS(Wqueue.extract().item);

		}else {
			
			for(int i = nodesingraph.size(); i < max ; ++i) {
				graphNodes.add(Wqueue.queue.get(i).item);
				nodesingraph.add(Wqueue.queue.get(i).item);
			}
			
			String source=" ";
			for(int i = 0; i < max ; ++i) {
				StringBuilder sb1 = new StringBuilder(1024000);
				try {
					source = nodesingraph.get(i);
					URL url = new URL(BASE_URL + source);
					InputStream is = url.openStream();
				    BufferedReader br =new BufferedReader(new InputStreamReader(is));
				    
				    while ((line = br.readLine()) != null) {//skip before <p>
			            if(line.contains("<p>")) {
			            	break;
			            }
			        }
				    
				    sb.append(line);
				    sb.append(" ");
				    
				    while ((line = br.readLine()) != null) {
			            sb1.append(line);
			            sb1.append(" ");
			        }
				    br.close();
				   
				    }catch (MalformedURLException mue) {
				         mue.printStackTrace();
				    }catch (IOException ioe) {
				         ioe.printStackTrace();
				    }
			
			
			String text = sb1.toString();
			
			Pattern p1 = Pattern.compile("(.{1,50}?)(<a href=\")(/wiki/.*?)(\")(.*?)(>)(.*?)(</a>)(.{1,50})");
			Matcher m1 = p1.matcher(text);
			
			while(m1.find()){
				String currentURL = m1.group(3);//group 3 is the wiki part
;
				if(currentURL.contains("#") || currentURL.contains(":")) { //be polite,be elegant
					continue;
				}else {
//					if(linksincurrentpage.contains(currentURL)) {//if the linke has appeared in this page
//						continue;
//					}					
					if(graphNodes.contains(currentURL)) {
						String[] temp = {source,currentURL};
						edges.add(temp);
					}
								
					}
				}
			} 
			
			writeresult();
		}
	}
	
	public void writeresult() {
		try {
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fileName)));
		out.println(max);
		for(int i = 0; i < edges.size(); ++i) {
			out.println(edges.get(i)[0]+" "+edges.get(i)[1]);
		}
		out.close();
		}catch(IOException e) {
		}
	}
	
	
	public void crawl(){
		BFS(Wqueue.extract().item);

	}
}



