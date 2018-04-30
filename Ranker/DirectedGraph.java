import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;

/**
 * An example class for directed graphs.  The vertex type can be specified.
 * There are no edge costs/weights.
 * 
 * Written for CS211, Nov 2006.
 * 
 * @author Paul Chew
 */
public class DirectedGraph<V> {
    
    /**
     * The implementation here is basically an adjacency list, but instead
     * of an array of lists, a Map is used to map each vertex to its list of 
     * adjacent vertices.
     */   
    
    private Map<V,List<V>> neighbors = new HashMap<V,List<V>>();

    public DirectedGraph() 
    {
    }
    
    
   
    /**
     * Add a vertex to the graph.  Nothing happens if vertex is already in graph.
     */
    public void add (V vertex) {
        if (neighbors.containsKey(vertex)) return;
        neighbors.put(vertex, new ArrayList<V>());
    }
    
    /**
     * True iff graph contains vertex.
     */
    public boolean contains (V vertex) {
        return neighbors.containsKey(vertex);
    }
    
    /**
     * Add an edge to the graph; if either vertex does not exist, it's added.
     * This implementation allows the creation of multi-edges and self-loops.
     */
    public void add (V from, V to) {
        this.add(from); this.add(to);
        neighbors.get(from).add(to);
    }
    
    /**
     * Remove an edge from the graph.  Nothing happens if no such edge.
     * @throws IllegalArgumentException if either vertex doesn't exist.
     */
    public void remove (V from, V to) {
        if (!(this.contains(from) && this.contains(to)))
            throw new IllegalArgumentException("Nonexistent vertex");
        neighbors.get(from).remove(to);
    }
    
    /**
     * Report (as a Map) the out-degree of each vertex.
     */
    public Map<V,Integer> outDegree () {
        Map<V,Integer> result = new HashMap<V,Integer>();
        for (V v: neighbors.keySet()) result.put(v, neighbors.get(v).size());
        return result;
    }
    
    /**
     * Report (as a Map) the in-degree of each vertex.
     */
    public Map<V,Integer> inDegree () {
        Map<V,Integer> result = new HashMap<V,Integer>();
        for (V v: neighbors.keySet()) result.put(v, 0);       // All in-degrees are 0
        for (V from: neighbors.keySet()) {
            for (V to: neighbors.get(from)) {
                result.put(to, result.get(to) + 1);           // Increment in-degree
            }
        }
        return result;
    }
    
    
public void populate(LinkedList<CustomURL>[] list, Map<String,Float> URLToRank)
{
    if (list.length == 0)
    {
        System.out.print("List is empty");
        return;
    }

    for (LinkedList<CustomURL> list1 : list) 
    {
        if (!list1.isEmpty())
        {
            CustomURL parent = list1.get(0);

            if (URLToRank != null)
            {
                if (URLToRank.containsKey(parent.myURL.toString()))
                    parent.setOldPageRank(URLToRank.get(parent.myURL.toString()));
            }

            this.add((V) parent);

            for (int i=1; i<list1.size();i++)
            {
                CustomURL child = list1.get(i);
                
                if (URLToRank != null)
                {
                    if (URLToRank.containsKey(child.myURL.toString()))
                        child.setOldPageRank(URLToRank.get(child.myURL.toString()));
                 }
                this.add((V)parent,(V)child);
            }
        }
    }
}
    
    
    protected Map<V,List<V>>  getNeighbors()
    {
        return neighbors;
    }
    
    
    /**
     * Main program (for testing).
     */
//    public static void main (String[] args) {
//        // Create a Graph with Integer nodes
//        DirectedGraph<Integer> graph = new DirectedGraph<Integer>();
//        graph.add(0, 1); graph.add(0, 2); graph.add(0, 3);
//        graph.add(1, 2); graph.add(1, 3); graph.add(2, 3);
//        graph.add(2, 4); graph.add(4, 5); graph.add(5, 6);    // Tetrahedron with tail
////        System.out.println("The current graph: " + graph);
//        System.out.println("In-degrees: " + graph.inDegree().get(0));
//        System.out.println("Out-degrees: " + graph.outDegree().get(1));
//        System.out.println("child of (1): " + graph.neighbors.get(1).get(0));
//        System.out.println("child of (1): " + graph.neighbors.get(1).get(1));
//        
//        System.out.println("A topological sort of the vertices: " + graph.topSort());
//        System.out.println("The graph " + (graph.isDag()?"is":"is not") + " a dag");
//        System.out.println("BFS distances starting from " + 0 + ": " + graph.bfsDistance(0));
//        System.out.println("BFS distances starting from " + 1 + ": " + graph.bfsDistance(1));
//        System.out.println("BFS distances starting from " + 2 + ": " + graph.bfsDistance(2));
//        graph.add(4, 1);                                     // Create a cycle
//        System.out.println("Cycle created");
//        System.out.println("The current graph: " + graph);
//        System.out.println("A topological sort of the vertices: " + graph.topSort());
//        System.out.println("The graph " + (graph.isDag()?"is":"is not") + " a dag");
//        System.out.println("BFS distances starting from " + 2 + ": " + graph.bfsDistance(2));
   // }
}