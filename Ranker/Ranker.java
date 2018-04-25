
import java.sql.Connection;
import java.util.LinkedList;


/**
 *
 * @author chahira
 */
public class Ranker implements Runnable {
    
    private Connection con;
    private CrawlerResources ourResources;
    private LinkedList<CustomURL> [] toBeRanked;

    Ranker(CrawlerResources myCrawlerResources, Connection connect, LinkedList<CustomURL>[] list) {
  
        con = connect;
        ourResources = myCrawlerResources;
        toBeRanked = list;
    
    }
     
    public void run()
    {
       System.out.println("From Ranker size of toBeRanked = "+toBeRanked.length);
        
       int sum = 0;
       
       for (int i=0; i<toBeRanked.length; i++)
       {
           if (toBeRanked[i].size() == 0)
               break;
           
           //System.out.println("Size of toBeRanked["+i+"] = "+toBeRanked[i].size());
           //sum += toBeRanked[i].size();
       }
       
       System.out.println("sum = " + sum);
    }
    
    
    
}
