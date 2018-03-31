
import java.util.*;

public class ValueDict {
    static public HashMap<String, Double> VDict  = new HashMap<String, Double>();

    	static Double k=VDict.put("keyword",100.0);
    	static Double t=VDict.put("title",50.0);
    	static Double n= VDict.put("h1", 40.0);
    	static Double x= VDict.put("h2", 35.0);
    	static Double x1=VDict.put("h3", 30.0);
    	static Double x2=VDict.put("h4", 25.0);
    	static Double x3=VDict.put("h5", 20.0);
    	static Double x4=VDict.put("h6", 15.0);
    	static Double x5=VDict.put("p", 1.0);
    	static Double x6=VDict.getOrDefault(null, 0.0);

    

}