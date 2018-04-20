
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
    
    public CustomURL(String url) throws MalformedURLException, URISyntaxException 
    {
        myURL = new URI(url).normalize().toURL();
        linkRank = 0;
        recrawling = false;
    }
    
    public CustomURL() 
    {
       linkRank = 0;
    }
    
    public void incrementLinkRank()
    {
        linkRank ++;
    }
    
    public int getLinkRank()
    {
        return linkRank;
    }
    
    public boolean getRecrawling()
    {
        return recrawling;
    }
    
    public void setRecrawling(boolean b)
    {
        recrawling = b;
    }
    
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
