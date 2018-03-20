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

public class Crawler {

    private static final String DBCLASSNAME = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    private static final String CONNECTION =
			"jdbc:sqlserver://localhost:1433;databaseName=;user=;password=;";
   
   
    
    public static void main(String[] args) throws SQLException, ClassNotFoundException, IOException {
 
    String query_;
    Statement st2;
    ResultSet rt2;
    
    try{
        // DB Connection
        Class.forName(DBCLASSNAME);
        Connection con = DriverManager.getConnection(CONNECTION);
        System.out.println("Connected to database !");
  
        // Insert seed into database
        String seed_ = "http://www.javatpoint.com/java-tutorial";
        Document doc = Jsoup.connect(seed_).get();
        
        try {
        String check_id_seed = "Select ID from Docs_URL WHERE URL = '" + seed_ + "'" ;
        Statement id_st_seed = con.createStatement();
        ResultSet id_result_seed = id_st_seed.executeQuery(check_id_seed);
        id_result_seed.next();
        int temp_seed = id_result_seed.getInt(1);
        
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
        catch(SQLException sqle) 
        {
            System.out.println("Sql Exception :"+sqle.getMessage());
        }
       
        // if we will need to add title uncomment these and add query for it
//        String title = doc.title();
//        System.out.println(title);
        

        // printing links size  and imports - not important
        Elements links = doc.select("a[href]");
        System.out.println("imports = "+ links.size());
        print("\nLinks: (%d)", links.size());
        
        // Inserting urls into hyperlinks table in ms sql database
        for (Element link : links) 
        {
            try
            {
                String check_id = "Select ID from Docs_URL WHERE URL = '" + link.attr("abs:href") + "'" ;
                Statement id_st = con.createStatement();
                ResultSet id_result = id_st.executeQuery(check_id);
                id_result.next();
                int temp = id_result.getInt(1);
                continue;
                
            }
            catch(SQLException sqle) 
            {
                System.out.println("Sql Exception :"+sqle.getMessage());
            }
            
            print(" * a: <%s>  (%s)", link.attr("abs:href"), trim(link.text(), 35));
            String query = "Insert into Docs_URL (URL) " + "Values ('" + link.attr("abs:href")+ "')";
            Statement st_ = con.createStatement();
            try
            {
            st_.executeUpdate(query);
            }
            catch(SQLException sqle) {
            System.out.println("Sql Exception :"+sqle.getMessage());
            continue;
            }
            // creating html docs for the fetched urls - should not be done here
            // error: java.net.SocketTimeoutException: Read timed out
            
            // another error 404 Not found
            /* Exception in thread "main" org.jsoup.HttpStatusException: HTTP error fetching URL. Status=404, URL=https://www.javatpoint.com/java-tutorial#java-applications
            at org.jsoup.helper.HttpConnection$Response.execute(HttpConnection.java:537)
            */ 
            query_ = "Select ID from Docs_URL WHERE URL = '" + link.attr("abs:href")+"' ";
            st2 = con.createStatement();
            rt2 = st2.executeQuery(query_);
            rt2.next();
            try
            {
            doc = Jsoup.connect(link.attr("abs:href")).get();

            //System.out.println(Integer.toBinaryString(rt2.getInt(1)));
            
            PrintWriter writer_ = new PrintWriter("html_docs/"+ Integer.toBinaryString(rt2.getInt(1)) +".html", "UTF-8");
            writer_.print(doc);
            writer_.close();
            }
            catch (HttpStatusException http_e)
            {
                System.out.println("HTTP Status Exception:"+http_e.getMessage());
            }
            
            if (rt2.getInt(1) == 16)
                break;
        }

        try {
            
//            String q2 = "Select * from hyperlinks";
//            Statement st2_ = con.createStatement();
//            ResultSet rt2_ = st2_.executeQuery(q2);
        }

        catch (Exception e)
        {
                System.err.println("Got an exception! ");
                System.err.println(e.getMessage());
        }

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