
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author chahira
 */

public class CustomURL {
    
    URL myURL;
    int linkRank;
    boolean recrawling;
    float pageRank;
    float oldPageRank;
    int Visited;
    int outGoingLinks;
    
    public CustomURL(String url) throws MalformedURLException, URISyntaxException 
    {
        
        Pattern patt = Pattern.compile("(^.*)?\\?");
        Matcher match = patt.matcher(url);
        if (match.find())
        {
            url = match.group(0).replaceFirst(".$", "");
        }
        
        myURL = new URI(url).normalize().toURL();
        linkRank = 0;
        pageRank = 1;
        oldPageRank = 1;
        recrawling = false;
        Visited = 0;        // 0 : not downloaded       // 1: downloaded but still not parent       // 2: downloaded and parent
        outGoingLinks = 0;
    }
    
    public CustomURL() 
    {
       linkRank = 0;
    }
    
    protected void setOutGoingLinks(int out)
    {
        outGoingLinks = out;
    }
    
    protected int getOutGoingLinks()
    {
        return outGoingLinks;
    }
    
    protected void setVisited(int v)
    {
        Visited = v;
    }
    
    protected int getVisited()
    {
        return Visited;   
    }
    
    //*******************************************
    
    public void incrementLinkRank()
    {
        linkRank ++;
    }
    
      public int getLinkRank()
    {
        return linkRank;
    }
    
    public void setLinkRank(int rank)
    {
        linkRank = rank;
    }
    
    //*******************************************
    
    public float getOldPageRank()
    {
        return oldPageRank;
    }
    
    public void setOldPageRankNewPR()
    {
        this.oldPageRank = this.pageRank;
    }
    
  
    public void setOldPageRank(float rank)
    {
        oldPageRank = rank;   
    }
    
    
    public void updatepageRank(float rank)
    {
        pageRank += rank;
    }
    
     public float getPageRank()
    {
        return pageRank;
    }
    
    public void setPageRank(float rank)
    {
        pageRank = rank;
    }
    
    //*********************************************
    public boolean getRecrawling()
    {
        return recrawling;
    }
    
    public void setRecrawling(boolean b)
    {
        recrawling = b;
    }
    
    
    //*********************************************
    
    @Override
    public boolean equals(Object o) {
 
        // If the object is compared with itself then return true  
        if (o == this) 
        {
            return true;
        }
        
        /* Check if o is an instance of CustomURL or not
          "null instanceof [type]" also returns false */
        if (!(o instanceof CustomURL)) {
            return false;
        }
         
        // typecast o to CustomURL so that we can compare data members 
        CustomURL c = (CustomURL) o;
        CustomURL c2 = (CustomURL)this;
        
        // Compare the data members and return accordingly 
        String string1 = c2.myURL.getHost()+c2.myURL.getFile();
        String string2 = c.myURL.getHost()+c.myURL.getFile();
        
        string1 = string1.toLowerCase();
        string2 = string2.toLowerCase();
        
        return string1.equals(string2);
    }
 
    
//    private static void splitUrlfromProtocol(String[] url)
//{
//    Pattern patt = Pattern.compile("http(s)?://(www\\.)?");
//    Matcher match = patt.matcher(url[0]);
//    
//    if (match.find())
//    {
//        url[1] = match.group(0);
//        url[0] = url[0].replaceFirst(match.group(0),"");
//    }
//}

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + Objects.hashCode(this.myURL);
        hash = 59 * hash + this.linkRank;
        hash = 59 * hash + (this.recrawling ? 1 : 0);
        return hash;
    }

    
}
