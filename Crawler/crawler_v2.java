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
import java.util.ConcurrentModificationException;
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
    private static int CrawledMax;
   
    public static void main(String[] args) throws Exception {
 
    System.out.println("From Crawler version 2 HashSets!");   
    try{
        
        crawler_v2  crawler = new crawler_v2();
        
        crawler.crawlAll();
      
        System.out.println("Crawled sites(Downloaded) = " + crawled_sites.size());
        System.out.println("Extracted sites = " + extracted_sites.size());
	System.out.println("status404_sites = " + status404_sites.size());
        System.out.println("It works !");
        con.close();

    }
    catch (Exception e)
    {
        System.out.println("Exception "+e.getMessage());
    }
 }
    
//******************************************************************************
    
/* 
Constructor
Calls connecttoDatabase, throws exception if connection fails
Initializes static variables declared 
*/
public crawler_v2() throws Exception
{ 
     con = connecttoDB();

     crawled_sites = new HashMap<>();
     extracted_sites = new HashMap<>();
     status404_sites = new HashSet<>();
     seedlist = new ArrayList<>();

    CrawledMax = 60;	
     //loadSeedlist();
     
     loadStatefromDB();
}



public void crawlAll() throws Exception
{
    while( crawled_sites.size() < CrawledMax && !extracted_sites.isEmpty())
    {
        try
        {
           for (Map.Entry<String, String> entry : extracted_sites.entrySet())
           {        
               if (entry.getValue() == null)
                   continue;
               
               System.out.println("from map");
               System.out.println("entry:" + entry.getKey());
           }
        }
        catch(ConcurrentModificationException e)
        {
            System.out.println("Exception Concurrent Modification: "+ e.getMessage() );
        }
        catch (Exception e)
        {
            System.out.println("Exception ");
        }
    }

}

private  void crawlSingle(String url) throws Exception
{
	String urlarr[] = new String[2];
    try
    {
        System.out.println("crawlsingle url received: " + url);
        Document doc = jsoupConnect(url);
        
    	urlarr[0] = url;
    	splitUrlfromProtocol(urlarr);
	System.out.println("Protocol = " + urlarr[1] + "url = " + urlarr[0]);    
	
	putinCrawled(urlarr);
        removefromExtracted(urlarr); 
        
	updateDB(url,doc,1);
        downloadHtml(doc,url);
        Elements links = extractLinks(doc);

        System.out.println(extracted_sites.size());

        for (Element link : links)
        {
            if (isUrlValid(link.attr("abs:href")))
            {
                urlarr[0] = link.attr("abs:href");
                splitUrlfromProtocol(urlarr);
                
                // synchronized
                putinExtracted(urlarr);
                
                print(" * a: <%s>  (%s)", link.attr("abs:href"), trim(link.text(), 35));
                insertExtractedUrlinDB(link.attr("abs:href"));
            }

        }
    
    }
    catch(ConcurrentModificationException e)
    {
        System.out.println("Exception Concurrent modification " + e.getMessage());
        throw new Exception();
    }
    catch(Exception e)
    {
        System.out.println("Exception   " + e.getMessage());
        throw new Exception();
    }
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
  
public void loadSeedlist() throws Exception
{
    // load from database seedlist
    try{
    String load_query = "SELECT URL from Seed_List ORDER BY ID;";
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

private void loadStatefromDB() throws Exception
{
    // load any previous data first from the database 
    // Note: Load only crawled sites, not the status404
    // make a lookup table for the queries ?
   try{
    String load_query = "SELECT URL,Downloaded from Docs_URL ORDER BY ID;";
    Statement load_st = con.createStatement();
    ResultSet load_result = load_st.executeQuery(load_query);
    
    String urlarr[] = new String[2];
    
    while(load_result.next())
    {    
        urlarr[0] = load_result.getString(1);
        splitUrlfromProtocol(urlarr);
       
        
        if (load_result.getInt(2) != 0)
        {
            putinCrawled(urlarr);
            //System.out.println(load_result.getString(1));
        }
        else
        {
            // Document wasn't downloaded
           putinExtracted(urlarr);
            //System.out.println(load_result.getString(1));
        }
       
    }
   }
   catch(SQLException sqle) {
       System.out.println("Sql Exception :"+sqle.getMessage());
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
        Document doc = Jsoup.connect(urlseed).timeout(10).get();
        return doc;
    }
    
     catch (HttpStatusException http_e)
    {
        System.out.println("HTTP Status Exception:"+http_e.getMessage());
        status404_sites.add(urlseed);
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


private void removefromExtracted(String[] urlarr)
{
    // synchronize the following
    extracted_sites.remove(urlarr[0]);
}

private void putinExtracted(String[] urlarr)
{
   
    // synchronize the following
    putinExtractedUrlandProtocol(urlarr[0],urlarr[1]);
}

// should be synchronized 
private void putinExtractedUrlandProtocol(String url, String protocol)
{
    extracted_sites.put(url, protocol);
}

private void putinCrawled(String[] urlarr)
{
    // synchronize the following
    putinCrawledUrlandProtocol(urlarr[0],urlarr[1]);
}

private void putinCrawledUrlandProtocol(String url, String protocol)
{
    crawled_sites.put(url, protocol);
}

private static void insertExtractedUrlinDB (String url) throws Exception
{
    try{
    int downloadedflag = 0;    
    String query = "Insert into Docs_URL (URL, Downloaded) " + "Values ('"+ url+ "','" + downloadedflag + "')";
    Statement st_ = con.createStatement();
    st_.executeUpdate(query);
    }
    catch (SQLException sqle)
    {
        System.out.println("Sql Exception :"+sqle.getMessage());
        throw new Exception();
    }
}


private static void updateDB(String url,Document doc, int updateflag) throws Exception
{
    try{
    String title = doc.title();
    String query = "Update Docs_URL Set Title = '" + trim(title,50) + "', Downloaded = " + updateflag + "Where URL = '" + url +"';";
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

private  boolean isUrlValid(String url)
{
    if (status404_sites.contains(url))
        return false;
    else
    {
        if (url.length() >200)
        {
            return false;
        }
    boolean result = (!isSelfRedirect(url) && isNewUrl(url));
    return result;
    }
}

// should be synchronized
private  boolean isNewUrl (String url)
{
    String sarr[] = new String[2];
    sarr[0] = url;
    splitUrlfromProtocol(sarr);
    if (crawled_sites.containsKey(sarr[0]) || extracted_sites.containsKey(sarr[0]))
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