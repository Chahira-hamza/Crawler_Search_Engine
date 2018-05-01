
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.SocketTimeoutException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


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
    private static Map<String,String> map;
    
    public static void main(String[] args) {
    
        try
        {
            con = connecttoDB();
            readInputfromUser();
            
//          threadNum = 2;
//          depth = 1;
//          maxDocs = 5;
            lock = new Object();

            //Thread CrawlerManag = new Thread(new CrawlerManager(depth,threadNum,maxDocs,con,lock));
            Thread IndexerThread = new Thread(new Indexer(con,lock));
            
            //CrawlerManag.start();
            
            DownloadDocsToIndex();
            IndexerThread.start();
           
            //CrawlerManag.join();
            
            synchronized(lock)
            {
                lock.notify();
                IndexerThread.interrupt();
               
            }
            
            
            IndexerThread.join();
            
            updateDB();
            
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

protected static void updateDB()
{
    try {
        con.setAutoCommit(false);

       for (Iterator<Map.Entry<String, String>> it = map.entrySet().iterator(); it.hasNext();) 
        {
            Map.Entry<String,String> entry = it.next();
            String key = entry.getKey();
            
            String query = "Update Docs_URL Set Title = ? , Visited = ? Where URL = ?";
            PreparedStatement stp = con.prepareStatement(query);

            stp.setString(1, trim(entry.getValue(),50));
            stp.setInt(2, 1);
            stp.setString(3, key);
            stp.executeUpdate();
            
            System.out.println(entry.getKey());
        }
       
        con.commit();
        } catch (SQLException ex) {
            Logger.getLogger(Integrator.class.getName()).log(Level.SEVERE, null, ex);
        }
}

    protected static void DownloadDocsToIndex()
{
      try {
          int count = 0;
          
            String query = "select URL from Docs_URL where Visited = 0 and linkRank > 20 and url like '%https://en.wikipedia.org/wiki%';";
            Statement st = con.createStatement();
            ResultSet rt = st.executeQuery(query);
            
           map = new HashMap<>();
            
            while (rt.next())
            {
                try{
                Document doc = jsoupConnect(rt.getString(1));
                downloadHtml(doc,rt.getString(1));
                map.put(rt.getString(1), doc.title());
                }
                catch (Exception ex) {
                Logger.getLogger(Integrator.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                
            }
            
            
        } 
        catch (Exception ex) {
            Logger.getLogger(Integrator.class.getName()).log(Level.SEVERE, null, ex);
            
        }
}

protected static Document jsoupConnect(String urlseed) throws Exception {
        try {
            Document doc = Jsoup.connect(urlseed).get();
            return doc;
        } catch (HttpStatusException http_e) {
            System.out.println("HTTP Status Exception:" + http_e.getMessage());
            throw new Exception();
        } catch (SocketTimeoutException se) {
            System.out.println("Socket Timeout Exception:" + se.getMessage());
            throw new Exception();
        } catch (IOException ioe) {
            System.out.println("IOException:" + ioe.getMessage());
            throw new Exception();
        }
    }

protected static void downloadHtml(Document doc, String url) throws Exception {
        try {
            int id = getUrlId(url);
            String path = "html_docs/" + Integer.toBinaryString(id) + ".html";

            File f = new File(path);

            // if the file exits: either we downloaded it before
            // or it is being used now by the indexer, so better leave it alone
            // but in case of re-crawling we should return a boolean then !
            if (!f.exists()) {
                PrintWriter writer = new PrintWriter(path, "UTF-8");
                writer.print(doc);
                writer.close();
            }
        } catch (FileNotFoundException fe) {
            System.out.println("File not found Exception :" + fe.getMessage());
            throw new Exception();
        }
    }

    protected static int getUrlId(String url) {
        try {
            String query = "Select ID from Docs_URL Where URL = '" + url + "';";
            Statement st = con.createStatement();
            ResultSet rt = st.executeQuery(query);
            rt.next();
            return rt.getInt(1);
        } catch (SQLException sqle) {
            return 0;
        }

    }

    private static String trim(String s, int width) {
        if (s.length() > width) {
            return s.substring(0, width - 1) + ".";
        } else {
            return s;
        }
    }
    
}