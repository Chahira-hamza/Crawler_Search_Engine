import java.sql.*;
import java.net.*;  
import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.HttpStatusException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.*;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class crawler_v2 {

    private static final String DBCLASSNAME = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    private static final String CONNECTION =
			"jdbc:sqlserver://localhost:1433;databaseName=search_engine;user=sa;password=;";
    private static Connection con;
   
    private static LinkedList<CustomURL> extracted;
    private static LinkedList<CustomURL> blocked;
    private static LinkedList<CustomURL> crawled;
    private static LinkedList<CustomURL> currently;
            
    private static LinkedList<CustomURL> visited;

    private static int CrawledMax;      //5'000 pages
    
    private static int depthIteration;
    private static int currentIteration;
    private static int threadNum;
   
    public static void main(String[] args) throws Exception {
 
    System.out.println("From Crawler version 2 Lists !");   
    
    try{
        
        int depth = 2;
        int thread = 1;
        
        crawler_v2  crawler = new crawler_v2(depth,thread);
        
        crawler.run();
      
        System.out.println("Crawled sites = " + crawled.size());
        System.out.println("Extracted sites = " + extracted.size());
	//System.out.println("Blocked sites = " + blocked.size());
        System.out.println("Visited sites = " + visited.size());
        
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
public crawler_v2(int depth, int threadnum) throws Exception
{ 
    con = connecttoDB();

    crawled     = new LinkedList<>();
    extracted   = new LinkedList<>();
    blocked     = new LinkedList<>();
    currently   = new LinkedList<>();
    visited     = new LinkedList<>();
   
    CrawledMax = 5000;
    depthIteration = depth;
    threadNum = threadnum;
    
    currentIteration = 1;
    
    loadStatefromDB();
}



public void run() throws Exception
{

    while (currentIterationRunning())
    {
        // get link to crawl
        CustomURL url = new CustomURL();

        // synchronized
        url = getLinktoCrawl();

        if ( (url != null && isNewUrl(url)) )
        {
           Elements links =  exctractAllLinks(url);
           addtoCurrentlyCrawling(url);
           checkAndAdd(links,url);
           addtoVisited(url);
           int visitedflag = 2;
           Document doc = null;
           insertUpdateUrlinDB(url.myURL.toString(),visitedflag,doc);
        }
        
        if (currentIteration == 1)      //desired depth
        {
            currentIteration ++;
        }
    }

    System.out.println("Thread # "+ Thread.currentThread().getName() +" finished");
    
}

private boolean currentIterationRunning()
{
    if ( ((currentIteration%2) != 0) )
    {
        return (!extracted.isEmpty());
    }
    else
        return (!crawled.isEmpty());
}

private synchronized CustomURL getLinktoCrawl()
{
    if ((currentIteration%2) != 0)
    {
        return (extracted.pollFirst());
    }
    else
    {
        return (crawled.pollFirst());
    }
       
}

private  Elements exctractAllLinks(CustomURL urlseed) throws Exception
{
    try
    {
        String urlstring = urlseed.myURL.toString();
        Document doc = jsoupConnect(urlstring);
        
        // In case the interruption happened after downloading the document
        if (isNewUrl(urlseed))
        {
            int downloadflag = 1;
            insertUpdateUrlinDB(urlstring,downloadflag,doc);

            downloadHtml(doc,urlstring);
        }
        
        Elements links = extractLinks(doc);

        System.out.println("Links = "+links.size());
        return links;
        
    }
    catch(Exception e)
    {
        System.out.println("Exception " + e.getMessage());
        throw new Exception();
    }
    
}

// Do sql querries handle concurrency access ?
private void checkAndAdd(Elements links,CustomURL url) throws Exception
{
    int downloadflag = 0;
    try { 
        
    for (Element link : links) 
    {
        if (!(link.attr("abs:href").equals("")) && !(link.attr("abs:href") == null) )
        {
            String linkstring = checkEndSlashUrl(link.attr("abs:href"));
            CustomURL urlc = new CustomURL(linkstring);
            
            if (isUrlValid(urlc))
            {
                
                if (getUrlId(linkstring) != -1)
                {
                    System.out.println("Bug");

                    System.out.println("extracted "+ extracted.contains(urlc));
                    System.out.println("crawled "+ crawled.contains(urlc));
                    System.out.println("currently "+ currently.contains(urlc));
                    System.out.println("visited "+ visited.contains(urlc));
                    continue;
                }
                
                // synchronized
               if (!addasExtracted(urlc))
                   continue;

                Document doc = null;

                
                    
                insertUpdateUrlinDB(linkstring,downloadflag,doc);
            }
        }
    }
    }
    catch(MalformedURLException e)
    {
        System.out.println("Exception   " + e.getMessage());
        throw new Exception();
    }
    catch(Exception e)
    {
        
        throw new Exception();
    }
}

private boolean addasExtracted(CustomURL url)
{
    if ((currentIteration%2) != 0)
    {
        return addtoCrawled(url);
    }
    else
    {
        return addtoExtracted(url);
    }
}

private synchronized void addtoCurrentlyCrawling(CustomURL url)
{
    currently.add(url);
}

private synchronized void addtoVisited(CustomURL url)
{
    visited.add(url);
    currently.remove(url);
}

private synchronized boolean addtoExtracted(CustomURL url)
{
    return extracted.add(url);
}

private synchronized boolean addtoCrawled(CustomURL url)
{
    return crawled.add(url);
}


private String checkEndSlashUrl(String urlstr)
{
    if (!urlstr.endsWith("/"))
    {
        return urlstr;
    }
    else
    {
        return (urlstr.replaceFirst(".$",""));
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
  
private void loadStatefromDB() throws Exception
{
 
    // initially database has seed list ?
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
            case 0:
                addtoExtracted(url);
                //addtoVisited(url);
                break;
            case 1:
                addtoCrawled(url);
                addtoVisited(url);
                break;
            default:
                addtoVisited(url);
                break;
        }
    }
   }
   catch(SQLException sqle) {
       System.out.println("Sql Exception from load state :"+sqle.getMessage());
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
        return doc;
    }
    
     catch (HttpStatusException http_e)
    {
        System.out.println("HTTP Status Exception:"+http_e.getMessage());
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
       //System.out.println("Sql Exception :"+sqle.getMessage());
       //throw new Exception();
       return -1;
    }
    
}

private static void insertUpdateUrlinDB (String url,int downloadedflag,Document doc) throws Exception
{
    String query;
    
    try{   
        con.setAutoCommit(false);
        PreparedStatement stp;
        
        switch (downloadedflag) {
            case 0:
                query = "Insert into Docs_URL (URL, Visited) Values (?,?)";
                stp =  con.prepareStatement(query);
                stp.setString(1, url);
                stp.setInt(2, downloadedflag);
                break;
            case 1:
                String title = doc.title();
                query = "Update Docs_URL Set Title = ? , Visited = ? Where URL = ?";
                stp =  con.prepareStatement(query);
                stp.setString(1, trim(title,50));
                stp.setInt(2, downloadedflag);
                stp.setString(3, url);
                break;
            default:
                query = "Update Docs_URL Set Visited = ? Where URL = ? ";
                stp =  con.prepareStatement(query);
                stp.setInt(1, downloadedflag);
                stp.setString(2, url);
                break;
        }
        
        stp.executeUpdate();
        con.commit();

    }
    catch (SQLException sqle)
    {
        System.out.println("Sql Exception from insertUpdateUrlDB :"+sqle.getMessage());
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

private  boolean isUrlValid(CustomURL url)
{
    if (blocked.contains(url))
        return false;
    else
    {
        return ( (!isSelfRedirect(url) && isNewUrl(url)) );
    }
}

// should be synchronized
private  boolean isNewUrl (CustomURL url)
{
    if (crawled.contains(url) || extracted.contains(url) || visited.contains(url) || currently.contains(url))
        return false;
    else
        return true;
}


private static boolean isSelfRedirect(CustomURL url)
{
    Pattern patt = Pattern.compile("#");
    Matcher match = patt.matcher(url.myURL.toString());
    
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
