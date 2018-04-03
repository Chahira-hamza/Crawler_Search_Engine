
import java.util.HashSet;
import java.util.LinkedList;


/**
 *
 * @author chahira
 */
public class CrawlerResources {
    
    protected  LinkedList<CustomURL> extracted;
    protected  LinkedList<CustomURL> crawled;
    protected  LinkedList<CustomURL> currently;
    protected  LinkedList<CustomURL> visited;
        
    protected  LinkedList<CustomURL> disallowedRobots;
    //protected static LinkedList<CustomURL> allowedRobots;
    
    protected  HashSet <String> hostParsedbyRobots;

    protected  int currentIteration;
    int depth;
    int maxcrawled;
    
    public CrawlerResources(int depth_, int maxdoc) {
    
    crawled             = new LinkedList<>();
    extracted           = new LinkedList<>();
    currently           = new LinkedList<>();
    visited             = new LinkedList<>(); 
    disallowedRobots    = new LinkedList<>();
   // allowedRobots       = new LinkedList<>();
    hostParsedbyRobots  = new HashSet<>();
    
    currentIteration = 1;
    depth = depth_;
    maxcrawled = maxdoc;
    
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

protected synchronized boolean isRobotsParsed(String host)
{
   return hostParsedbyRobots.contains(host);
}


protected synchronized void addtoCurrentlyCrawling(CustomURL url)
{
    currently.add(url);
}

protected synchronized void addtoVisited(CustomURL url)
{
    visited.add(url);
    currently.remove(url);
}

protected synchronized boolean addtoExtracted(CustomURL url)
{
    return extracted.add(url);
}

protected synchronized boolean addtoCrawled(CustomURL url)
{
    return crawled.add(url);
}

protected synchronized boolean addDisallowed(CustomURL url)
{
    return disallowedRobots.add(url);
}

protected synchronized boolean addHostParsed(String url)
{
    return hostParsedbyRobots.add(url);
}

protected synchronized boolean currentIterationRunning()
{
    
    if ( ((currentIteration%2) != 0) )
    {
        return (!extracted.isEmpty());
    }
    else
        return (!crawled.isEmpty());
}
   
protected synchronized void incrementIteration()
{
    currentIteration ++;
}

protected synchronized boolean depthNotReached()
{
    if (currentIteration >= depth)
        return false;
    else
        return true;
}

protected synchronized boolean docsNotReached()
{
    if (visited.size() >= maxcrawled)
        return false;
    else
        return true;
}

protected synchronized CustomURL getLinktoCrawl()
{
    if ((currentIteration%2) != 0)
    {
        return (extracted.pollFirst());
    }
    else
    {
        return (crawled.pollFirst());
    }
       
}

protected synchronized boolean addasExtracted(CustomURL url)
{
    if ((currentIteration%2) != 0)
    {
        return addtoCrawled(url);
    }
    else
    {
        return addtoExtracted(url);
    }
}

}
