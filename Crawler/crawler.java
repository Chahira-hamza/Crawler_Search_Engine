

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class crawler_v2 {

    private static final String DBCLASSNAME = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    private static final String CONNECTION =
			"jdbc:sqlserver://localhost:1433;databaseName=;user=sa;password=;";
   
   
    
    public static void main(String[] args) throws SQLException, ClassNotFoundException, IOException {
 
    System.out.println("From Crawler version 2 HashSets!");  
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
  
        // load any previous data first from the database 
        // Note: Load only crawled sites, not the status404
        String load_query = "SELECT URL from Docs_URL ORDER BY ID;";
        Statement load_st = con.createStatement();
        ResultSet load_result = load_st.executeQuery(load_query);
        
        while(load_result.next())
        {
            crawled_sites.add(load_result.getString(1));
            System.out.println(load_result.getString(1));
        }
        
        
        // Insert seed into database
        String seed_ = "https://www.javatpoint.com/java-tutorial";
        Document doc = Jsoup.connect(seed_).get();
        String title = doc.title();
        
        Pattern pattseed = Pattern.compile("http(s)?://(www\\.)?");
        Matcher matchseed = pattseed.matcher(seed_);
        String resultseed = seed_;
        if (matchseed.find())
        {
           resultseed = seed_.replaceFirst(matchseed.group(0),"");
        }
        
        if (crawled_sites.add(resultseed))
        {
            String insert_seed_query = "Insert into Docs_URL (Title,Protocol,URL) " + "Values ('" + trim(title,50) + "','" + matchseed.group(0) + "','" + seed_ + "')";
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
        
        // printing links size  and imports - not important
        Elements links = doc.select("a[href]");
        System.out.println("imports = "+ links.size());
        print("\nLinks: (%d)", links.size());
        
        // Inserting urls into hyperlinks table in ms sql database
        int count = 0;
        
        for (Element link : links) 
        {
            String filteredlink,protocol;
            Pattern patt = Pattern.compile("http(s)?://(www\\.)?");
            Matcher match = patt.matcher(link.attr("abs:href"));
            if (match.find())
            {
                protocol = match.group(0);
                filteredlink = link.attr("abs:href").replaceFirst(match.group(0),"");

            if (!crawled_sites.contains(filteredlink) && !status404_sites.contains(filteredlink))
            {
                try
                {
                    doc = Jsoup.connect(link.attr("abs:href")).get();
                    
                    title = doc.title();
                    //System.out.println(title);
                    
                    // check if it redirects to the same page
                    patt = Pattern.compile("#");
                    match = patt.matcher(link.attr("abs:href"));

                    if (match.find())
                    {
                        count ++;
                        System.out.println("# found count = " + count);
                        continue;
                    }

                    crawled_sites.add(filteredlink);
                    print(" * a: <%s>  (%s)", link.attr("abs:href"), trim(link.text(), 35));
                    String query = "Insert into Docs_URL (Title,Protocol,URL) " + "Values ('" + trim(title,50) + "','" + protocol + "','" + filteredlink+ "')";
                    Statement st_ = con.createStatement();
                    st_.executeUpdate(query);

                    query_ = "Select count(URL) from Docs_URL;";
                    st2 = con.createStatement();
                    rt2 = st2.executeQuery(query_);
                    rt2.next();
                    
                    PrintWriter writer_ = new PrintWriter("html_docs/"+ Integer.toBinaryString(rt2.getInt(1)) +".html", "UTF-8");
                    writer_.print(doc);
                    writer_.close();
                    
                    if (rt2.getInt(1) == 15)
                    break;
                    
                }
                catch (HttpStatusException http_e)
                {
                    System.out.println("HTTP Status Exception:"+http_e.getMessage());
                    status404_sites.add(filteredlink);

                }
                catch (SocketTimeoutException se)
                {
                    System.out.println("Socket Timeout Exception:"+se.getMessage());
                }
            }
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
