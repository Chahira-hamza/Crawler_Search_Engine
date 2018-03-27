
package crawler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class Robots
{
    ArrayList Disallowed_URLS;
    ArrayList Allowed_URLS;

    public static void main(String[] args) throws Exception {
        Robots RFile = new Robots();
    }

    public Robots() throws Exception{
        Disallowed_URLS = new ArrayList();
        Allowed_URLS = new ArrayList();
        Parse_Robots("https://www.google.com/"); //Problem with twitter

    }

    public void Parse_Robots(String url_tovisit) throws Exception
    {
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

                while((line = in.readLine()) != null ) {
                    if (line.startsWith("#")) {
                        continue;
                    }
                    if (!line.toLowerCase().startsWith("user-agent: *") && isUA == false) {
                        while (in.readLine().isEmpty() == false)
                        {
                            continue;
                        }
                    }
                    else {
                        isUA = true;
                        if (line.isEmpty()) {
                            break;
                        }
                        if (line.toLowerCase().startsWith("disallow:")) {
                            Disallowed_URLS.add(url_tovisit + line.substring(10));
                        } else if (line.toLowerCase().startsWith("allow:")) {
                            Allowed_URLS.add(url_tovisit + line.substring(7));
                        }
                    }
                }
            }
            else
            {
                System.out.println("Can't find robot.txt");
            }

            for (int i = 0; i < Disallowed_URLS.size(); i++) {
                System.out.println(Disallowed_URLS.get(i));
            }
        }

        catch(MalformedURLException e)
        {

        }
    }
}
	/*
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

    public static void main(String[] args) {
        Parse_Robots("https://www.youtube.com/");
    }
*/