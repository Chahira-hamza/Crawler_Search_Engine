package Dictionary;
import java.util.*;

public class ValueDict {
    private HashMap<String, Double> VDict;

    public ValueDict() {
        VDict = new HashMap<String, Double>();

        VDict.put("<title>", 3.0);
        VDict.put("<h1>", 2.7);
        VDict.put("<h2>", 2.5);
        VDict.put("<h3>", 2.3);
        VDict.put("<h4>", 1.6);
        VDict.put("<h5>", 1.4);
        VDict.put("<h6>", 1.2);
        VDict.put("<p>", 1.0);
        VDict.getOrDefault(null, 0.0);

    }

}
