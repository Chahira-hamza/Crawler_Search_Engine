
import java.util.HashSet;
import java.util.LinkedList;


/**
 *
 * @author chahira
 */
public class CrawlerResources {
    
    protected static LinkedList<CustomURL> extracted;
    protected static LinkedList<CustomURL> crawled;
    protected static LinkedList<CustomURL> currently;
    protected static LinkedList<CustomURL> visited;
        
    protected static LinkedList<CustomURL> disallowedRobots;
    //protected static LinkedList<CustomURL> allowedRobots;
    
    protected static HashSet <String> hostParsedbyRobots;

    protected static int currentIteration;
    
    public CrawlerResources() {
    
    crawled             = new LinkedList<>();
    extracted           = new LinkedList<>();
    currently           = new LinkedList<>();
    visited             = new LinkedList<>(); 
    disallowedRobots    = new LinkedList<>();
   // allowedRobots       = new LinkedList<>();
    hostParsedbyRobots  = new HashSet<>();
    
    currentIteration = 1;
    
    }
    
    
    public void printData()
    {
        System.out.println("Crawled sites = " + crawled.size());
        System.out.println("Extracted sites = " + extracted.size());
        System.out.println("Visited sites = " + visited.size());
        System.out.println("Disallowed sites = " + disallowedRobots.size());
        System.out.println("Hosts Parsed by Robots sites = " + hostParsedbyRobots.size());
    }
    
    protected synchronized boolean isNewUrl (CustomURL url)
    {
        if (crawled.contains(url) || extracted.contains(url) || visited.contains(url) || currently.contains(url))
            return false;
        else
            return true;
    }
    
    protected synchronized boolean isDisllowed(CustomURL url)
    {
       return disallowedRobots.contains(url);
    }
}
