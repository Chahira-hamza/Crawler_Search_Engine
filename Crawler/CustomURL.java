
import java.net.MalformedURLException;
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
    
    public CustomURL(String url) throws MalformedURLException 
    {
        myURL = new URL(url);
    }
    
    public CustomURL() 
    {
       
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
        return (c2.myURL.getHost()+c2.myURL.getFile()).equals(c.myURL.getHost()+c.myURL.getFile());
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + Objects.hashCode(this.myURL);
        return hash;
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
    
}
