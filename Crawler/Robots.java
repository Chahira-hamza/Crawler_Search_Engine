
package crawler;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;


public class Robots 
{   
    ArrayList Disallowed_URLS;
    
    public void Parse_Robots(String url_tovisit) throws Exception
    {
        String url_robots = url_tovisit + "/robots.txt";
        
        try
        {
            URL url_ = new URL(url_robots);
            Scanner scan = new Scanner(url_.openStream());
            
            while (scan.findInLine("User-agent: *") != null)
            {
                if (scan.findInLine("Disallow: /") != null)
                {
                   String str_exclude = scan.nextLine();
                   System.out.print(url_tovisit +"/" + str_exclude);
                   Disallowed_URLS.add(url_tovisit +"/" + str_exclude);
                }
            }
            
        }
        catch(MalformedURLException e)
        {
            
        }
    }
    
}
