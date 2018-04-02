
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 *
 * @author chahira
 */
public class CrawlerManager implements Runnable {
    
    int CrawledMax;
    int depthIteration;
    int threadNum;
    Connection con;

    CrawlerResources myCrawlerResources;
    
    public CrawlerManager(int depth, int threadnum, int maxDoc,Connection connection ) {
        
    CrawledMax      = maxDoc;
    depthIteration  = depth;
    threadNum       = threadnum;
    con             = connection;
    
    }
    
    public void run()
    {
        // object that will passed to all threads
        myCrawlerResources = new CrawlerResources();
        
        loadStatefromDB();
        
        // thread pool
        ExecutorService Executor =  Executors.newFixedThreadPool(threadNum);
    
        while(tasksNotFinished)
        {
            Executor.submit(new Crawler(0,0,myCrawlerResources));
        }
    
        
        myCrawlerResources.printData();
       
     
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
            case 0:
                myCrawlerResources.extracted.add(url);
                break;
            case 1:
                myCrawlerResources.crawled.add(url);
                break;
            default:
                myCrawlerResources.visited.add(url);
                break;
        }
    }
   }
   catch(SQLException sqle) {
       System.out.println("Sql Exception from load state :"+sqle.getMessage());
    }
    
}
}
