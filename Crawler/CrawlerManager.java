
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author chahira
 */
public class CrawlerManager implements Runnable {
    
    Object lock;
    int threadNum;
    Connection con;
    Thread workers[];
    boolean tasksNotFinished;
    CrawlerResources myCrawlerResources;
    Future resultWorkers[];
    
    public CrawlerManager(int depth, int threadnum, int maxDoc,Connection connection, Object lock_) {
        
    System.out.println("Crawler Manager created");
    
    threadNum   = threadnum;
    con         = connection;
    lock        = lock_;
    
    myCrawlerResources  = new CrawlerResources(depth,maxDoc);
    workers             = new Thread[threadNum];
    resultWorkers       = new Future[threadNum];
    
    }
    
    public void run()
    {
        loadStatefromDB();
        System.out.println("finished loading DB");
        
        // thread pool
        ExecutorService Executor =  Executors.newFixedThreadPool(threadNum);
        //int taskNum = myCrawlerResources.depth * threadNum;
        
        Thread ranker;
       for (int i=0;i<myCrawlerResources.depth;i++)
       {
            myCrawlerResources.setListToBeRanked();
            
            for (int j=0; j<threadNum;j++)
                resultWorkers[j] = Executor.submit(new Crawler(myCrawlerResources,con,lock));
            
            for (int j=0;j<threadNum; j++)
            {
               try{
                   resultWorkers[j].get();
                }
               catch(Exception e)
               {
                   System.out.println(e.getMessage());
               }
            }
            
            if (!myCrawlerResources.docsNotReached())
                break;
                
            checkNeedRecrawl();
            
            LinkedList<CustomURL> [] toBeRanked = myCrawlerResources.getListToBeRanked();
            // send it to Ranker
            
            ranker = new Thread(new RankerManager(myCrawlerResources,con,toBeRanked));
            ranker.start();
            try {
                ranker.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(CrawlerManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            myCrawlerResources.incrementIteration();
            System.out.println("Incremented Iteration = "+ myCrawlerResources.currentIteration);

            
        }
       
        if (!myCrawlerResources.docsNotReached())
        {
            LinkedList<CustomURL> [] toBeRanked = myCrawlerResources.getListToBeRanked();
            ranker = new Thread(new RankerManager(myCrawlerResources,con,toBeRanked));
            ranker.start();
            try {
                ranker.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(CrawlerManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        Executor.shutdown();

        try{
        Executor.awaitTermination(1,TimeUnit.HOURS);
        }
        catch(InterruptedException e)
        {
            System.out.println(e.getMessage());
        }
        
        
        myCrawlerResources.printData();
        
        
            // creating threads
//       while (myCrawlerResources.depthNotReached() && myCrawlerResources.docsNotReached())
//       {
//           for (int i=0; i<threadNum; i++)
//        {
//           workers[i] = new Thread(new Crawler(myCrawlerResources, con, lock));
//           workers[i].start();
//        } 
//            try
//            {
//                for (int i=0; i<threadNum; i++)
//                {
//                   workers[i].join();
//                }
//            }
//            catch(Exception e)
//            {
//                System.err.println(e.getMessage());
//            }
//           
//            myCrawlerResources.currentIteration++;
//       }
     
       
      
        
    }
    
private void loadStatefromDB()
{
 
   try{
     
    con.setAutoCommit(false);
    String load_query = "SELECT URL,Visited from Docs_URL ORDER BY ID;";
    PreparedStatement load_st =  con.prepareStatement(load_query);
    ResultSet load_result = load_st.executeQuery();
    con.commit();
    
    while(load_result.next())
    {        
        CustomURL url = new CustomURL(load_result.getString(1));
       
        int downloadflag = load_result.getInt(2);
        
        switch (downloadflag) {
            case 2:
                myCrawlerResources.visited.add(url);
                break;
            default:
                url.setVisited(downloadflag);
                myCrawlerResources.crawled.add(url);
                break;
        }
    }
   }
   catch(SQLException sqle) {
       System.out.println("Sql Exception from load state :"+sqle.getMessage());
    }
   catch (Exception e)
   {
       System.out.println("Exception from load state :"+e.getMessage());
   }
    
}
    
private void checkNeedRecrawl()
{
    try
    {
        List<String> recrawlLinks = getRecrawlLinksDB();

        if (recrawlLinks != null)
        {
            for (int i=0; i<recrawlLinks.size(); i++)
            {
               // CustomURL u = myCrawlerResources.removeAndGetFromVisited(recrawlLinks.get(i));
                CustomURL u = myCrawlerResources.removeAndGet(recrawlLinks.get(i));
                u.setRecrawling(true);
                u.setLinkRank(loadLinkRank(u));
                u.setVisited(0);
                myCrawlerResources.addasExtracted(u);
                updateVisitedDB(u);
            }
        }
        else
            System.out.println("Null Recrawling List");
    }
    catch (Exception e)
    {
        System.out.println(e.getMessage());
    }
}

private List<String> getRecrawlLinksDB()
{
    try
    {
        con.setAutoCommit(false);
        String loadQuery = "SELECT URL from Docs_URL WHERE Visited = 1 and linkRank > "
                + "( SELECT Avg(linkRank) "
                + "FROM (SELECT distinct linkRank from Docs_URL) TMP );";
        PreparedStatement loadSt =  con.prepareStatement(loadQuery);
        ResultSet loadResult = loadSt.executeQuery();
        con.commit();
        
        List<String> recrawlingLinks = new ArrayList<>();
        
        while (loadResult.next())
        {         
            recrawlingLinks.add(loadResult.getString(1));
        }
        
        return recrawlingLinks;
    }
    catch(SQLException sqle) {
       System.out.println("Sql Exception from load state :"+sqle.getMessage());
       return null;
    }
   catch (Exception e)
   {
       System.out.println("Exception from load state :"+e.getMessage());
       return null;
   }
}


private void updateVisitedDB(CustomURL url)
{
  String query;
    
    try{   
        con.setAutoCommit(false);
        PreparedStatement stp;     
        
        //query = "Update Docs_URL Set Visited = ? , linkRank = ? Where URL = ?";
        query = "Update Docs_URL Set Visited = ?  Where URL = ?";
        stp =  con.prepareStatement(query);
        
        stp.setInt(1, 0);
        stp.setString(2, url.myURL.toString());
        stp.executeUpdate();
        con.commit();

    }
    catch (SQLException sqle)
    {
        System.out.println("Sql Exception from UpdateVisitedDB :"+sqle.getMessage());
    }   
}


private int loadLinkRank(CustomURL u)
{
    try{
     
    con.setAutoCommit(false);
    String load_query = "SELECT linkRank from Docs_URL where URL = ?;";
    PreparedStatement load_st =  con.prepareStatement(load_query);
    load_st.setString(1, u.myURL.toString());
    ResultSet load_result = load_st.executeQuery();
    con.commit();
    
    load_result.next();
    return load_result.getInt(1);
    
    }
    catch(Exception e)
    {
        System.out.println(e.getMessage());
        return 0;
    }
}

}