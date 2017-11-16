package pa3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WikiCrawler {
	static final String BASE_URL = "https://en.wikipedia.org";
	int max;
	WeightedQ Wqueue = new WeightedQ();
	HashSet<String> kwords;
	HashSet<String> disallowedURL;
	String[] keywords;
	
	public WikiCrawler(String seedURL, String[] keywords, int max, String fileName, boolean isWeighted) {
//		System.out.println(Wqueue.queue.size());
//		this.kwords = new HashSet<String>(Arrays.asList(keywords));
		this.max = max;
		this.keywords = keywords;
		Element seed = new Element(seedURL, 1);
		Wqueue.add(seed);//add seed as the first node to crawl
		
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
	
	public void crawl(){
		String line;
		StringBuilder sb = new StringBuilder(102400);//build the entire doc
		
		try {
		URL url = new URL(BASE_URL + Wqueue.extract().item);
		InputStream is = url.openStream();
	    BufferedReader br =new BufferedReader(new InputStreamReader(is));
	    
	    while ((line = br.readLine()) != null) {
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
//		System.out.print(fulltext);
		Pattern p = Pattern.compile("(.{1,400}?)(<a href=\")(/wiki/.*?)(\")(.*?)(>)(.*?)(</a>)(.{1,400}?)");
		Matcher m = p.matcher(fulltext);
		while(m.find()){
			String currentURL = m.group(3);//group 3 is the wiki part
//			System.out.println(currentURL+" OMGOMG");
			if(currentURL.contains("#") || currentURL.contains(":")) {//be polite,be elegant
				continue;
			}else {
				double weight = 0.0;
				boolean flag = true;
				
				for(int i = 0; i < this.keywords.length;++i) {
					if(currentURL.toLowerCase().contains(keywords[i]) || m.group(7).toLowerCase().contains(keywords[i])){
						Wqueue.add(new Element(currentURL,1));
						flag = false;
					}
				}
				
				if(flag) {
					String[] frontWords = m.group(1).toLowerCase().split("\\W+");
					System.out.println("flag section length " + frontWords.length);
					System.out.println("flag section " + frontWords[0]);
					System.out.println("flag section " + frontWords[1]);
					System.out.println("flag section " + frontWords[2]);
					System.out.println("flag section " + frontWords[3]);
					System.out.println("flag section " + frontWords[4]);
				}
				
//				for(int i = 0; i< this.keywords.length; ++i){
//					int weightCounter = 20;
//					if(currentURL.toLowerCase().contains(keywords[i]) || m.group(7).toLowerCase().contains(keywords[i])){
//						//grpup 7 is the anchor word
//						//when anchor text or http address contain keywords, weight is assigned to 1
//						weightCounter = 0;
//					}else{//if cannot find a key words in URL nor anchor word, do computer distance
//						String[] frontWords = m.group(1).toLowerCase().split("\\W+");
//						System.out.println("front words sieze " + frontWords[0]);
//						for(int k1 = 0; k1< frontWords.length; k1++){
//							if(frontWords[k1].contains(keywords[i])){
//								weightCounter = Math.min(weightCounter, Math.abs(20-k1));
//							}
//						}
//						String[] backWords = m.group(9).toLowerCase().split("\\W+");
//						for(int k2 = 0; k2< backWords.length; k2++){
//							if(backWords[k2].contains(keywords[i])){
//								weightCounter = Math.min(weightCounter, k2 + 1);
//							}
//						}			
//					}
//					if(weightCounter == 20){
//						weight = Math.max(weight, 0);
//					}else if(weightCounter == 0){
//						weight = Math.max(weight, 1);
//					}else{
//						weight = Math.max(weight, 1/(weightCounter + 2));
//					}
//				}
				
			}
		} 
		    
	}
//	public double 
}



