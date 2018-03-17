
package crawler;

import java.sql.*;
import java.net.*;  
import java.util.Properties;
import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.*;

public class Crawler {

    private static final String DBCLASSNAME = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    private static final String CONNECTION =
			"jdbc:sqlserver://localhost:1433;databaseName=;user=;password=;";
   
    public static void main(String[] args) throws SQLException, ClassNotFoundException, IOException {
 
    try{
        // DB Connection
        Class.forName(DBCLASSNAME);
        Connection con = DriverManager.getConnection(CONNECTION);
        System.out.println("Connected to database !");
  
        // Insert seed into database
        String seed_ = "http://www.javatpoint.com/java-tutorial";
        Document doc = Jsoup.connect(seed_).get();
        String insert_seed_query = "Insert into Docs_URL (URL) " + "Values ('" + seed_ + "')";
        Statement st = con.createStatement();
        st.executeUpdate(insert_seed_query);
        
        // if we will need to add title uncomment these and add query for it
//        String title = doc.title();
//        System.out.println(title);
        

        // creating html document named with corresponding id.html 
        String query_ = "Select ID from Docs_URL";
        Statement st2 = con.createStatement();
        ResultSet rt2 = st2.executeQuery(query_);
        rt2.next();
        PrintWriter writer = new PrintWriter(""+ rt2.getInt(1) +".html", "UTF-8");
        writer.print(doc);
        writer.close();
        
        // printing links size  and imports - not important
        Elements links = doc.select("a[href]");
        System.out.println("imports = "+ links.size());
        print("\nLinks: (%d)", links.size());
        
        // Inserting urls into hyperlinks table in ms sql database
        for (Element link : links) {
            print(" * a: <%s>  (%s)", link.attr("abs:href"), trim(link.text(), 35));
            String query = "Insert into Docs_URL (URL) " + "Values ('" + link.attr("abs:href")+ "')";
            Statement st_ = con.createStatement();
            st_.executeUpdate(query);
            
            // creating html docs for the fetched urls - should not be done here
            // error: java.net.SocketTimeoutException: Read timed out
            query_ = "Select ID from Docs_URL WHERE URL = '" + link.attr("abs:href")+"' ";
            st2 = con.createStatement();
            rt2 = st2.executeQuery(query_);
            rt2.next();
            doc = Jsoup.connect(link.attr("abs:href")).get();
            PrintWriter writer_ = new PrintWriter(""+ rt2.getInt(1) +".html", "UTF-8");
            writer_.print(doc);
            writer_.close();
            
            if (rt2.getInt(1) == 10)
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
