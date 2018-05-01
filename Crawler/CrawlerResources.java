
import java.net.MalformedURLException;
import java.net.URISyntaxException;
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
    
    protected LinkedList<CustomURL> [] toBeRanked;
    protected int indexToBeRanked;
    
    public CrawlerResources(int depth_, int maxdoc) {
    
    crawled             = new LinkedList<>();
    extracted           = new LinkedList<>();
    currently           = new LinkedList<>();
    visited             = new LinkedList<>(); 
    disallowedRobots    = new LinkedList<>();
   // allowedRobots       = new LinkedList<>();
    hostParsedbyRobots  = new HashSet<>();
    
   
    
    currentIteration = 0;
    depth = depth_;
    maxcrawled = maxdoc;
    
    }
    
    
    public void printData()
    {
        System.out.println("Last Iteration = " + currentIteration);
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
    synchronized(this)
    {
    
    if ( ((currentIteration%2) != 0) )
    {
        return (!extracted.isEmpty());
    }
    else
        return (!crawled.isEmpty());
    }
}
   
protected synchronized void incrementIteration()
{  
    currentIteration ++;
}

protected synchronized   boolean depthNotReached()
{
    synchronized(this)
    {
    if (currentIteration >= depth)
        return false;
    else
        return true;
    }
}

protected synchronized boolean docsNotReached()
{
    synchronized(this)
    {
    if (visited.size() >= maxcrawled)
        return false;
    else
        return true;
    }
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


protected synchronized int getCountLinkstoCrawl()
{
    if ((currentIteration%2) != 0)
    {
        return extracted.size();
    }
    else
    {
        return crawled.size();
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

protected synchronized CustomURL removeAndGetFromVisited(String url) throws MalformedURLException, URISyntaxException
{
 
    CustomURL u = new CustomURL(url);
    if (!visited.remove(u))
        System.out.println("Error in removing url from visited  " + url );
   
    return u;
}


protected synchronized CustomURL removeAndGet(String url) throws MalformedURLException, URISyntaxException
{

    CustomURL u = new CustomURL(url);
    
    if (extracted.contains(u))
        extracted.remove(u);
    else if (crawled.contains(u))
        crawled.remove(u);
    else
        visited.remove(u);
    
    return u;
}

protected synchronized void setListToBeRanked()
{
    int linksToCrawl = getCountLinkstoCrawl();
    toBeRanked = new LinkedList[linksToCrawl];
    
    for (int i=0; i<linksToCrawl; i++)
    {
        toBeRanked[i] = new LinkedList<>();
    }
    
    indexToBeRanked = 0;
}

protected synchronized int addElementToBeRanked(CustomURL url)
{

    toBeRanked[indexToBeRanked].add(url);
    
    return indexToBeRanked ++;
}

protected synchronized void addChildLinkToBeRanked(int indexParent, CustomURL url)
{

    toBeRanked[indexParent].add(url);
}

protected synchronized LinkedList<CustomURL>[] getListToBeRanked()
{
    return toBeRanked;
}

protected LinkedList<CustomURL> getListToBeRanked(int parent)
{
    return toBeRanked[parent];
}

protected void setListToBeRanked(int parent, LinkedList<CustomURL> list)
{
    toBeRanked[parent] = list;
}

}
