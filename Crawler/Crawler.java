package crawler;

import java.sql.*;
import java.net.*;  
import java.util.Properties;
import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.HttpStatusException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.*;
import java.util.HashSet;

public class Crawler {

    private static final String DBCLASSNAME = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    private static final String CONNECTION =
			"jdbc:sqlserver://localhost:1433;databaseName=;user=;password=;";
   
   
    
    public static void main(String[] args) throws SQLException, ClassNotFoundException, IOException {
 
    String query_;
    Statement st2;
    ResultSet rt2;
    
    HashSet<String> crawled_sites = new HashSet<String>();
    HashSet<String> status404_sites = new HashSet<String>();
    
    try{
        // DB Connection
        Class.forName(DBCLASSNAME);
        Connection con = DriverManager.getConnection(CONNECTION);
        System.out.println("Connected to database !");
  
        // Insert seed into database
        String seed_ = "http://www.javatpoint.com/java-tutorial";
        Document doc = Jsoup.connect(seed_).get();
        
        if (crawled_sites.add(seed_))
        {
            String insert_seed_query = "Insert into Docs_URL (URL) " + "Values ('" + seed_ + "')";
            Statement st = con.createStatement();
            st.executeUpdate(insert_seed_query);

            // creating html document named with corresponding id.html 
            query_ = "Select ID from Docs_URL";
            st2 = con.createStatement();
            rt2 = st2.executeQuery(query_);
            rt2.next();
            PrintWriter writer = new PrintWriter("html_docs/"+ Integer.toBinaryString(rt2.getInt(1)) +".html", "UTF-8");
            writer.print(doc);
            writer.close();
        }
        
       
        // if we will need to add title uncomment these and add query for it
//        String title = doc.title();
//        System.out.println(title);
        

        // printing links size  and imports - not important
        Elements links = doc.select("a[href]");
        System.out.println("imports = "+ links.size());
        print("\nLinks: (%d)", links.size());
        
        // Inserting urls into hyperlinks table in ms sql database
        
        // creating html docs for the fetched urls - should not be done here
        // error: java.net.SocketTimeoutException: Read timed out

        // another error 404 Not found
        /* Exception in thread "main" org.jsoup.HttpStatusException: HTTP error fetching URL. Status=404, URL=https://www.javatpoint.com/java-tutorial#java-applications
        at org.jsoup.helper.HttpConnection$Response.execute(HttpConnection.java:537)
        */ 
                
        for (Element link : links) 
        {
            if (crawled_sites.add(link.attr("abs:href")) && !status404_sites.contains(link.attr("abs:href")))
            {
                print(" * a: <%s>  (%s)", link.attr("abs:href"), trim(link.text(), 35));
                String query = "Insert into Docs_URL (URL) " + "Values ('" + link.attr("abs:href")+ "')";
                Statement st_ = con.createStatement();
                st_.executeUpdate(query);
               
                query_ = "Select ID from Docs_URL WHERE URL = '" + link.attr("abs:href")+"' ";
                st2 = con.createStatement();
                rt2 = st2.executeQuery(query_);
                rt2.next();
                try
                {
                doc = Jsoup.connect(link.attr("abs:href")).get();
                PrintWriter writer_ = new PrintWriter("html_docs/"+ Integer.toBinaryString(rt2.getInt(1)) +".html", "UTF-8");
                writer_.print(doc);
                writer_.close();
                }
                catch (HttpStatusException http_e)
                {
                    System.out.println("HTTP Status Exception:"+http_e.getMessage());
                    status404_sites.add(link.attr("abs:href"));
                    crawled_sites.remove(link.attr("abs:href"));
                }

                if (rt2.getInt(1) == 105)
                    break;

            }
        }

        System.out.println(crawled_sites.size());
        System.out.println(status404_sites.size());
        System.out.println("It works !");
        con.close();

    }
    catch(SQLException sqle) {
       System.out.println("Sql Exception :"+sqle.getMessage());
    }
    catch(ClassNotFoundException e) {
     System.out.println("Class Not Found Exception :" + e.getMessage());
    }
    catch (HttpStatusException http_e)
    {
        System.out.println("HTTP Status Exception:"+http_e.getMessage());
    }
 }
    
private static void print(String msg, Object... args) {
        System.out.println(String.format(msg, args));
}

private static String trim(String s, int width) {
        if (s.length() > width)
                return s.substring(0, width-1) + ".";
        else
                return s;
}


}
