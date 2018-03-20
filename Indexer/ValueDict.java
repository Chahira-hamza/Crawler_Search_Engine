
import java.util.*;

public class ValueDict {
    private HashMap<String, Double> VDict;

    public ValueDict() {
        VDict = new HashMap<String, Double>();

        VDict.put("<title>",50.0);
        VDict.put("<h1>", 40.0);
        VDict.put("<h2>", 35.0);
        VDict.put("<h3>", 30.0);
        VDict.put("<h4>", 25.0);
        VDict.put("<h5>", 20.0);
        VDict.put("<h6>", 15.0);
        VDict.put("<p>", 1.0);
        VDict.getOrDefault(null, 0.0);

    }

}