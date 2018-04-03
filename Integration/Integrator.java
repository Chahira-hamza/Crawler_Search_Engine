
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;


/**
 *
 * @author chahira
 */
public class Integrator {
    
    private static final String DBCLASSNAME = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    private static final String CONNECTION =
			"jdbc:sqlserver://localhost:1433;databaseName=;user=;password=;";
    
    private static int threadNum;
    private static int depth;
    private static int maxDocs;
    public static Connection con;
    public static Object lock;
    
    public static void main(String[] args) {
    
        try
        {
            con = connecttoDB();
  
            readInputfromUser();
            
//          threadNum = 2;
//          depth = 1;
//          maxDocs = 5;
            lock = new Object();

            Thread CrawlerManag = new Thread(new CrawlerManager(depth,threadNum,maxDocs,con,lock));
            Thread IndexerThread = new Thread(new Indexer(con,lock));
            
            CrawlerManag.start();
            IndexerThread.start();
           
            CrawlerManag.join();
            
            synchronized(lock)
            {
                IndexerThread.interrupt();
                lock.notify();
            }
            
            
            IndexerThread.join();
            
        System.out.println("Closing connection!!!");
        con.close();
        }
    catch(Exception e)
    {
         System.out.println(e.getMessage());
    }
    
}
    
private static void readInputfromUser()
{
    // get input from user thread num, depth, max docs to crawl
    Scanner reader = new Scanner(System.in);  // Reading from System.in
    System.out.println("Enter the desired number of threads: ");
    threadNum = reader.nextInt(); // Scans the next token of the input as an int.

    System.out.println("Enter the desired deph and the maximum documents to download: ");
    depth = reader.nextInt(); // Scans the next token of the input as an int.
    maxDocs = reader.nextInt();

    reader.close();
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
