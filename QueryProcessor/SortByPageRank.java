
import java.util.Comparator;

/**
 *
 * @author chahira
 */

    public class SortByPageRank implements Comparator<DocRank>
{
    @Override
    public int compare(DocRank a, DocRank b)
    {
        
        //return (int) (a.rank - b.rank);
        
        if (a.rank > b.rank)
            return 1;
        else if (a.rank < b.rank)
            return -1;
        else
            return 0;
      
           
    }
}
