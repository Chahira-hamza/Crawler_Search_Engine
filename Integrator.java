
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 *
 * @author chahira
 */
public class Integrator {
    
    private static final String DBCLASSNAME = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    private static final String CONNECTION =
			"jdbc:sqlserver://localhost:1433;databaseName=search_engine;user=sa;password=gam3astuff*;";
    
    private static int threadNum;
    private static int depth;
    private static int maxDocs;
    public static Connection con;
    public static Object lock;
    
    public static void main(String[] args) throws Exception {
    
    con = connecttoDB();
    
    // get input from user thread num, depth, max docs to crawl
    threadNum = 4;
    depth = 1;
    maxDocs = 10;
    lock = new Object();
    
    Thread CrawlerManag = new Thread(new CrawlerManager(depth,threadNum,maxDocs,con,lock));
    Thread IndexerThread = new Thread(new Indexer(con,lock));
    CrawlerManag.start();
    IndexerThread.start();
    IndexerThread.join();
    CrawlerManag.join();
    
        System.out.println("Closing connection!!!");
    con.close();
    
}
    
    //Establish connection with the Database   
private static Connection connecttoDB() throws Exception
{
    try{   
        Class.forName(DBCLASSNAME);
        
        Connection con = DriverManager.getConnection(CONNECTION);
        System.out.println("Connected to database !");
        return con;
    }
    catch(ClassNotFoundException e) 
    {
        System.out.println("Class Not Found Exception :" + e.getMessage());
        throw new Exception();
    }
    catch(SQLException sqle) {
       System.out.println("Sql Exception :"+sqle.getMessage());
       throw new Exception();
    }
}
}