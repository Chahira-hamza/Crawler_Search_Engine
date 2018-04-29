
import java.io.File;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


/**
 *
 * @author chahira
 */
public class RankerManager implements Runnable {
    
    private Connection con;
    private CrawlerResources ourResources;
    private LinkedList<CustomURL> [] toBeRanked;
    private int threadNum;
    DirectedGraph<CustomURL> graphURL;
    
    RankerManager(CrawlerResources myCrawlerResources, Connection connect, LinkedList<CustomURL>[] list) {
  
        con = connect;
        ourResources = myCrawlerResources;
        toBeRanked = list;
        
    }
     
    public void run()
    {
       System.out.println("From Ranker size of toBeRanked = "+toBeRanked.length);
       
       
       graphURL = new DirectedGraph<>();
       
       graphURL.populate(toBeRanked);
       
       for (int i=0; i<5;i++)
       {
            calculatePageRank();
            writeRanksToFile(i);
       
       }
        System.out.println("Ranking is complete ! ");
        
    }
    
     public void calculatePageRank()
    {
        for (Iterator<Map.Entry<CustomURL, List<CustomURL>>> it = graphURL.getNeighbors().entrySet().iterator(); it.hasNext();) 
        {
            Map.Entry<CustomURL,List<CustomURL>> entry = it.next();
            CustomURL parent = entry.getKey();
            
            int outgoing = graphURL.outDegree().get(parent);
            
            for (int i=0;i<outgoing;i++)
            { 
                CustomURL child = graphURL.getNeighbors().get(parent).get(i);
                float rank = (parent.getOldPageRank()/graphURL.outDegree().get(parent));
                child.updatepageRank(rank);
            }
        }
    }
    
     
    protected void writeRanksToFile(int i)
     {
        String path = "Ranker_Docs/ranks"+i+".txt" ;
        File f = new File(path);
        
        try{
        PrintWriter writer = new PrintWriter(path, "UTF-8");

        for (Map.Entry<CustomURL,List<CustomURL>> entry : graphURL.getNeighbors().entrySet()) {
            CustomURL parent = entry.getKey();
            writer.println(parent.myURL.toString() + "\tRank = " + parent.getPageRank());
        }

        writer.close();
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
     }
}
