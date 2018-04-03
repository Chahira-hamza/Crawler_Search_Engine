
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


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
        
       for (int i=0;i<myCrawlerResources.depth;i++)
       {
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
            
            myCrawlerResources.incrementIteration();
            System.out.println("Incremented Iteration = "+ myCrawlerResources.currentIteration);
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
    
}
