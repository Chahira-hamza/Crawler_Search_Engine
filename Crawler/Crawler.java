import java.sql.*;
import java.net.*;  
import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.HttpStatusException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Crawler implements Runnable {

    private Connection con;
    private CrawlerResources ourResources;
    private Object lock;
    
public Crawler(CrawlerResources CR, Connection connection, Object lock_)
{ 
    con = connection;
    ourResources = CR;
    lock = lock_;
}

public void run() 
{
    while (ourResources.currentIterationRunning() && ourResources.docsNotReached())
    {      
    try
    {
        CustomURL url = new CustomURL();
        url = ourResources.getLinktoCrawl();
        
        if (url != null)
        {
            parseRobots(url);

            if (ourResources.isNewUrl(url))
            {
                Elements links =  exctractAllLinks(url);
                ourResources.addtoCurrentlyCrawling(url);
                checkAndAdd(links,url);

                synchronized(lock)
                {
                     ourResources.addtoVisited(url);
                     lock.notify();  // for indexer to wake up and check the downloaded document
                     System.out.println("Added to visited");
                }

                int visitedflag = 2;
                Document doc = null;
                updateUrlinDB(url.myURL.toString(),visitedflag,doc);
            }
        }
    }
    catch(Exception e)
    {
        System.out.println("Handled exception, discarding current url\n" + e.getMessage());
        //continue;
    }
    }
      
    System.out.println("Thread # "+ Thread.currentThread().getName() +" finished");
}


private  Elements exctractAllLinks(CustomURL urlseed) throws Exception
{
    try
    {
        String urlstring = urlseed.myURL.toString();
        Document doc = jsoupConnect(urlstring);
               
        int downloadflag = 1;
        updateUrlinDB(urlstring,downloadflag,doc);
        downloadHtml(doc,urlstring);

       return extractLinks(doc);
    }
    catch(Exception e)
    {
        System.out.println("Exception " + e.getMessage());
        throw new Exception();
    }
    
}


private void checkAndAdd(Elements links,CustomURL url) throws Exception {
try 
{      
    for (Element link : links) 
    {
        if (!(link.attr("abs:href").equals("")) && !(link.attr("abs:href") == null) )
        {
            String linkstring = checkEndSlashUrl(link.attr("abs:href"));
            CustomURL urlc = new CustomURL(linkstring);
            parseRobots(urlc);
            
            if (isUrlValid(urlc))
            {
                if (getUrlId(linkstring) != -1)
                {
                    // normally don't need this query!
                    // for debugging !
                    System.out.println("added already");
                    continue;
                }
                
                ourResources.addasExtracted(urlc);
                insertUrlinDB(linkstring);
            }
        }
    }
    }
    catch(MalformedURLException e)
    {
        System.out.println("Exception   " + e.getMessage());
        throw new Exception();
    }
    catch(SQLException e)
    {
        System.out.println("Exception " + e.getMessage());
    }
     catch(Exception e)
    {
        System.out.println("Exception   " + e.getMessage());
        throw new Exception();
    }
}

private void checkNeedRecrawl()
{
    // we have a query that selects from the database the urls
    // that follow a certain criteria
    // column linkRank was added in the Docs_URL schema for this purpose
    // if linkRank is higher than a certain threshold
    // we update its value in the database to zero, and update the visited to zero as well
    // and we call the function addasExtracted(url) for it
    // to be added in the proper place
    // we will need to remove it from the visited, so it is a valid URL to be crawled
    // it will then take its path normally in the extracted list
    // the only check that will be made is to see if it has an ID or not in the database
    // or add the set of links to recrawl in a hashset to check by contains faster
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

private Elements extractLinks(Document doc)
{
    Elements links = doc.select("a[href]");
    print("\nLinks: (%d)", links.size());
    return links;
}

private Document jsoupConnect(String urlseed) throws Exception
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
    
private void downloadHtml(Document doc, String url) throws Exception
{
    try 
    {
        int id = getUrlId(url);
        String path = "html_docs/"+ Integer.toBinaryString(id) +".html";
        
        File f = new File(path);
        
        // if the file exits: either we downloaded it before
        // or it is being used now by the indexer, so better leave it alone
        // but in case of re-crawling we should return a boolean then !
        
        if( !f.exists()) 
        {  
            PrintWriter writer = new PrintWriter(path, "UTF-8");
            writer.print(doc);
            writer.close();
        }
    }
    catch (FileNotFoundException fe)
    {
        System.out.println("File not found Exception :"+fe.getMessage());
       throw new Exception();
    }
}

private int getUrlId(String url)
{
    try{
        String query = "Select ID from Docs_URL Where URL = '" + url +"';";
        Statement st = con.createStatement();
        ResultSet rt = st.executeQuery(query);
        rt.next();
        return rt.getInt(1);
    }
    catch(SQLException sqle) {
       return -1;
    }
    
}

private void insertUrlinDB (String url) throws Exception
{
    String query;
    int downloadedflag = 0;
    try{   
        con.setAutoCommit(false);
        PreparedStatement stp;

        query = "Insert into Docs_URL (URL, Visited) Values (?,?)";
        stp =  con.prepareStatement(query);
        stp.setString(1, url);
        stp.setInt(2, downloadedflag);
             
        stp.executeUpdate();
        con.commit();

    }
    catch (SQLException sqle)
    {
        System.out.println("Sql Exception from insert:"+sqle.getMessage());
        throw new Exception();
    }
}


private void updateUrlinDB (String url,int downloadedflag,Document doc) throws Exception
{
    String query;
    
    try{   
        con.setAutoCommit(false);
        PreparedStatement stp;
        
        switch (downloadedflag) {
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
        System.out.println("Sql Exception from UpdateUrlDB :"+sqle.getMessage());
        throw new Exception();
    }
}

private boolean isUrlValid(CustomURL url)
{
    if (ourResources.isDisllowed(url))
        return false;
    else
    {
        return ( (!isSelfRedirect(url) && ourResources.isNewUrl(url)));
    }
}

private  boolean isSelfRedirect(CustomURL url)
{
    Pattern patt = Pattern.compile("#");
    Matcher match = patt.matcher(url.myURL.toString());
    
    return match.find();
}

private void print(String msg, Object... args) {
        System.out.println(String.format(msg, args));
}

private String trim(String s, int width) {
        if (s.length() > width)
                return s.substring(0, width-1) + ".";
        else
                return s;
}

public void parseRobots(CustomURL urlc)
{

    if (ourResources.isRobotsParsed(urlc.myURL.getHost()))
        return;
    else
    {
        String url_tovisit = urlc.myURL.getProtocol() + "://" + urlc.myURL.getHost();
        String url_robots = url_tovisit + "/robots.txt";

        try
        {
            URL url = new URL(url_robots);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            int Rcode = connection.getResponseCode();
            boolean isUA = false;

            if (Rcode == 200)
            {
                BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                String line = null;

                while((line = in.readLine()) != null ) 
                {
                    if (line.startsWith("#")) {
                        continue;
                    }
                    if (!line.toLowerCase().startsWith("user-agent: *") && isUA == false) {
//                        while (in.readLine().isEmpty() == false)
//                        {
//                            continue;
//                        }
                    }
                    else {
                        isUA = true;
                        if (line.isEmpty()) {
                            break;
                        }
                        if (line.toLowerCase().startsWith("disallow:")) 
                        {
                            CustomURL disURL = new CustomURL(url_tovisit + line.substring(10));
                            ourResources.addDisallowed(disURL);
                        } 
//                        else if (line.toLowerCase().startsWith("allow:")) 
//                        {
//                            CustomURL allURL = new CustomURL(url_tovisit + line.substring(7));
//                            addAllowed(allURL);   
//                        }
                    }
                    
                }
            }
            else
            {
                //System.out.println("Can't find robot.txt");
            }

//            for (int i = 0; i < Disallowed_URLS.size(); i++) {
//                System.out.println(Disallowed_URLS.get(i));
//            }

                 ourResources.addHostParsed(urlc.myURL.getHost());
        }

        catch(Exception e)
        {
            // System.out.println(e.getMessage());
        }
    }
    }
}
