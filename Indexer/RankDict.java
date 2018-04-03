package Dictionary;
import java.util.*;
//import org.jsoup.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;

import javax.xml.transform.Transformer;
import java.io.*;

public class RankDict {
    private HashMap<String, Double> RDict;

    public RankDict() throws Exception{
        RDict = new HashMap<String, Double>();

        try{
            Document doc = Jsoup.connect("http://www.dustyfeet.com/").get();
            String haha3 = doc.select("p").toString();
            //forEach(System.out::println);

            String haha = doc.body().toString();
            String haha2 = doc.text();
            //System.out.println(haha);
            //System.out.println(haha2);

            String [] SMap = haha3.split(" ");
            for (String a : SMap)
            {
                a.toLowerCase();
                //RDict.putIfAbsent(a, 0.0);
                //RDict.computeIfPresent(a, (k, v) -> v + 1);
                if (!RDict.containsKey(a))
                {
                    //RDict.replace(a, RDict.get(a)+1);
                    RDict.put(a, 1.0);
                }
                else
                {
                    RDict.replace(a, RDict.get(a)+1);
                }
                //System.out.print(a);
                //System.out.print("  ");
                //System.out.println(RDict.get(a));
            }

            Iterator<String> itr = RDict.keySet().iterator();
            Iterator<Double> itr2 = RDict.values().iterator();

            while (itr.hasNext()) {
                System.out.print(itr.next());
                System.out.print("  ");
                System.out.println(itr2.next());

            }
        }
        catch (Exception e)
        {

        }

    }

}
