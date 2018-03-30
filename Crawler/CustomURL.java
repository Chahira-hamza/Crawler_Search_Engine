
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

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
    
    
}
