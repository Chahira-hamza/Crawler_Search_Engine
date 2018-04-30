
import java.io.File;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
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
       
       Map<String,Float> URLToPageRank = loadPageRankDB();
       
       graphURL.populate(toBeRanked,URLToPageRank);
       
       //writeOutgoingLinksToFile();
        
       for (int i=0; i<10;i++)
       {
            calculatePageRank();
            writeRanksToFile(i);
            
            // switch between oldpageRank and pageRank
            setOldPR();
       }
       
       // update pageRank in DB and RankedBit
       updatePageRankDB();
       
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
                float rank = (parent.getOldPageRank()/outgoing);
                child.updatepageRank(rank);
            }
        }
        
        // add damping factor here for all nodes
        // newrank = 1-d + d(rank)
         for (Iterator<Map.Entry<CustomURL, List<CustomURL>>> it = graphURL.getNeighbors().entrySet().iterator(); it.hasNext();) 
        {
            Map.Entry<CustomURL,List<CustomURL>> entry = it.next();
            CustomURL parent = entry.getKey();
            //int size = graphURL.getNeighbors().size();
            parent.setPageRank((float) (0.15 + parent.getPageRank()*0.85));
            
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
    
    protected void writeOutgoingLinksToFile()
    {
        String path = "OutgoingLinks/children.txt" ;
        File f = new File(path);
        
        try{
        PrintWriter writer = new PrintWriter(path, "UTF-8");

        for (Map.Entry<CustomURL,List<CustomURL>> entry : graphURL.getNeighbors().entrySet()) {
            CustomURL parent = entry.getKey();
            writer.println("parent = "+parent.myURL.toString() +"\tout = "+graphURL.outDegree().get(parent));
        }

        writer.close();
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }
    
    protected void setOldPR()
    {
         for (Iterator<Map.Entry<CustomURL, List<CustomURL>>> it = graphURL.getNeighbors().entrySet().iterator(); it.hasNext();) 
        {
            Map.Entry<CustomURL,List<CustomURL>> entry = it.next();
            CustomURL parent = entry.getKey();
            parent.setOldPageRankNewPR();
            
        }
    }
    
    
    protected Map<String,Float> loadPageRankDB()
    {
    
    try{
     
    con.setAutoCommit(false);
    String load_query = "SELECT URL, pageRank FROM Docs_URL WHERE rankedBit = 1;";
    PreparedStatement load_st =  con.prepareStatement(load_query);
    ResultSet load_result = load_st.executeQuery();
    con.commit();
    
    Map<String,Float> URLToPageRank  = new HashMap<>();
    
    while (load_result.next())
    {
        String url = load_result.getString(1);
        float pageRank = load_result.getFloat(2);
        
        URLToPageRank.put(url, pageRank);
    }
    
    return URLToPageRank;
    
   }
   catch(SQLException sqle) {
       System.out.println("Sql Exception from load PageRank :"+sqle.getMessage());
       return null;
    }
   catch (Exception e)
   {
       System.out.println("Exception from load PageRnak :"+e.getMessage());
       return null;
   }
  }
    
    protected void updatePageRankDB()
    {
        try{
     
        con.setAutoCommit(false);
        String load_query = "UPDATE Docs_URL Set pageRank = ? , rankedBit = ? Where URL = ?;";
        //String load_query = "UPDATE Docs_URL Set rankedBit = ? Where URL = ?;";
        
         for (Map.Entry<CustomURL,List<CustomURL>> entry : graphURL.getNeighbors().entrySet())
         {
            PreparedStatement stp =  con.prepareStatement(load_query);
            CustomURL url = entry.getKey();
            
            stp.setFloat(1,url.getPageRank());
            stp.setBoolean(2, true);
            stp.setString(3, entry.getKey().myURL.toString());
            
            stp.executeUpdate();
            con.commit();

         }
        
        
       }
       catch(SQLException sqle) {
           System.out.println("Sql Exception from Ranker Manager :"+sqle.getMessage());
        }
       catch (Exception e)
       {
           System.out.println("Exception Ranker Manager:"+e.getMessage());
       }
    }
    
}
