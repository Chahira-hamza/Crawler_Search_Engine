

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class crawler_v2 {

    private static final String DBCLASSNAME = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    private static final String CONNECTION =
			"jdbc:sqlserver://localhost:1433;databaseName=search_engine;user=sa;password=Gam3aStuff*;";
    private static Connection con;
   
    private static HashMap<String,String> crawled_sites;
    private static HashMap<String,String> extracted_sites;
    private static HashSet<String> status404_sites;
    private static ArrayList<String> seedlist;
   
   // private static int downloadedcount;
    
    public static void main(String[] args) throws SQLException, ClassNotFoundException, IOException {
 
    System.out.println("From Crawler version 2 HashSets!");  
 
    try{
        // Insert seed into database
        String seed_ = "https://www.javatpoint.com/java-tutorial";

        System.out.println(crawled_sites.size());
        System.out.println(extracted_sites.size());
        System.out.println("It works !");
        con.close();

    }
    catch (Exception e)
    {
        
    }
 }
    
//******************************************************************************
    
/* 
Constructor
Calls connecttoDatabase, throws exception if connection fails
Initializes static variables declared 
*/
crawler_v2() throws Exception
{ 
     con = connecttoDB();

     HashMap<String,String> crawled_sites = new HashMap<String,String>();
     HashMap<String,String> extracted_sites = new HashMap<String,String>();
     HashSet<String> status404_sites = new HashSet<String>();
     ArrayList<String> seedlist = new ArrayList<String>();
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
  
private static void loadSeedlist()
{
    // load from database seedlist
}

private static void loadStatefromDB() throws Exception
{
    // load any previous data first from the database 
    // Note: Load only crawled sites, not the status404
    // make a lookup table for the queries ?
   try{
    String load_query = "SELECT URL from Docs_URL ORDER BY ID;";
    Statement load_st = con.createStatement();
    ResultSet load_result = load_st.executeQuery(load_query);
    
    String sarr[] = new String[2];
    
    while(load_result.next())
    {   
        sarr[0] = load_result.getString(1);
        splitUrlfromProtocol(sarr);
        extracted_sites.put(sarr[0],sarr[1]);
        System.out.println(load_result.getString(1));
    }
   }
   catch(SQLException sqle) {
       System.out.println("Sql Exception :"+sqle.getMessage());
       throw new Exception();
    }
    
}


private static void crawlAll() throws Exception
{
    String url;
    while (crawled_sites.size() < 20 && !extracted_sites.isEmpty())
    {
       for (Map.Entry<String, String> entry : extracted_sites.entrySet())
       {
           crawlSingle(entry.getValue() + entry.getKey());
       }
    }
}


private static void crawlSingle(String url) throws Exception
{
    try
    {
         if (isUrlValid(url))
        {
            Document doc = jsoupConnect(url);
            downloadHtml(doc,url);
            putinCrawled(url);
            removefromExtracted(url);
            insertinDB(url,doc);
            Elements links = extractLinks(doc);
            
            for (Element link : links)
            {
                putinExtracted(link.attr("abs:href"));
               // print(" * a: <%s>  (%s)", link.attr("abs:href"), trim(link.text(), 35));
            }
        }
    }
    catch(Exception e)
    {
        System.out.println("Exception cought in crawlSingle(String url)");
        throw new Exception();
    }
}


private static Elements extractLinks(Document doc)
{
    Elements links = doc.select("a[href]");
    print("\nLinks: (%d)", links.size());
    return links;
}

private static Document jsoupConnect(String urlseed) throws Exception
{
    try{
        Document doc = Jsoup.connect(urlseed).get();
    }
    
     catch (HttpStatusException http_e)
    {
        System.out.println("HTTP Status Exception:"+http_e.getMessage());
        //status404_sites.put(urlseed);
        throw new Exception();
    }
    catch (SocketTimeoutException se)
    {
        System.out.println("Socket Timeout Exception:"+se.getMessage());
        throw new Exception();
    }
    catch (IOException ioe)
    {
        System.out.println("IOException:"+ ioe.getMessage());
        throw new Exception();
    }
}
    
private static void downloadHtml(Document doc, String url) throws Exception
{
    try 
    {
        int id = getUrlId(url);
        PrintWriter writer = new PrintWriter("html_docs/"+ Integer.toBinaryString(id) +".html", "UTF-8");
        writer.print(doc);
        writer.close();
    }
    catch (FileNotFoundException fe)
    {
        System.out.println("File not found Exception :"+fe.getMessage());
       throw new Exception();
    }
}

private static int getUrlId(String url) throws Exception
{
    try{
        String query = "Select ID from Docs_URL Where URL = '" + url +"';";
        Statement st = con.createStatement();
        ResultSet rt = st.executeQuery(query);
        rt.next();
        return rt.getInt(1);
    }
    catch(SQLException sqle) {
       System.out.println("Sql Exception :"+sqle.getMessage());
       throw new Exception();
    }
    
}


private static void removefromExtracted(String url)
{
    String urlarr[] = new String[2];
    splitUrlfromProtocol(urlarr);
    // synchronize the following
    extracted_sites.remove(urlarr[0]);
}

private static void putinExtracted(String url)
{
    String urlarr[] = new String[2];
    splitUrlfromProtocol(urlarr);
    // synchronize the following
    putinExtractedUrlandProtocol(urlarr[0],urlarr[1]);
}

// should be synchronized 
private static void putinExtractedUrlandProtocol(String url, String protocol)
{
    extracted_sites.put(url, protocol);
}

private static void putinCrawled(String url)
{
    String urlarr[] = new String[2];
    splitUrlfromProtocol(urlarr);
    // synchronize the following
    putinCrawledUrlandProtocol(urlarr[0],urlarr[1]);
}

private static void putinCrawledUrlandProtocol(String url, String protocol)
{
    crawled_sites.put(url, protocol);
}

private static void insertinDB(String url,Document doc) throws Exception
{
    try{
    String title = doc.title();
    String query = "Insert into Docs_URL (Title,URL) " + "Values ('" + trim(title,50) + "','" + url+ "')";
    Statement st_ = con.createStatement();
    st_.executeUpdate(query);
    }
    catch (SQLException sqle)
    {
        System.out.println("Sql Exception :"+sqle.getMessage());
        throw new Exception();
    }
}


private static void splitUrlfromProtocol(String[] url)
{
    Pattern patt = Pattern.compile("http(s)?://(www\\.)?");
    Matcher match = patt.matcher(url[0]);
    
    if (match.find())
    {
        url[1] = match.group(0);
        url[0] = url[0].replaceFirst(match.group(0),"");
    }
}

private static boolean isUrlValid(String url)
{
    return (!isSelfRedirect(url) && isNewUrl(url));
}

// should be synchronized
private static boolean isNewUrl (String url)
{
    if (crawled_sites.containsKey(url) || extracted_sites.containsKey(url))
        return false;
    else
        return true;
}


private static boolean isSelfRedirect(String url)
{
    Pattern patt = Pattern.compile("#");
    Matcher match = patt.matcher(url);
    
    return match.find();
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