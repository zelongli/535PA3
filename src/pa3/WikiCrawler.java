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
	public void BFS(String url) {
		
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
		Pattern p = Pattern.compile("(.{1,60}?)(<a href=\")(/wiki/.*?)(\")(.*?)(>)(.*?)(</a>)(.{1,60})");
		Matcher m = p.matcher(fulltext);
		while(m.find()){
			String currentURL = m.group(3);//group 3 is the wiki part
///			System.out.println(currentURL+" OMGOMG");
			if(currentURL.contains("#") || currentURL.contains(":")) {//be polite,be elegant
				continue;
			}else {
				double weight = 0.0;
				boolean flag = true;
				
				for(int i = 0; i < this.keywords.length;++i) {
					if(currentURL.toLowerCase().contains(keywords[i]) || m.group(7).toLowerCase().contains(keywords[i])){
						Wqueue.add(new Element(currentURL,1));
						flag = false;
//						System.out.println("weigtht 1");
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
						Wqueue.add(new Element(currentURL,(1/(shortestDistance+2))));
					}
//					System.out.println("back distance is " + backdistance);
//					System.out.println("flag section length " + frontWords.length);
//					System.out.println("final distance is "+ Math.min(frontdistance, backdistance));
//					System.out.println("flag section " + frontWords[0]);
//					System.out.println("flag section " + frontWords[1]);
//					System.out.println("flag section " + frontWords[2]);
//					System.out.println("flag section " + frontWords[3]);
//					System.out.println("flag section " + frontWords[4]);
//					System.out.println("flag section " + frontWords[5]);
//					System.out.println("flag section " + frontWords[6]);
//					System.out.println("flag section " + frontWords[7]);
					
					
				}
				
				
			}
		} 
		    
	}
}



