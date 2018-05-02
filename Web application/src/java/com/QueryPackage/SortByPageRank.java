package com.QueryPackage;

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
        
        // to rank ascendingly
//        if (a.rank > b.rank)
//            return 1;
//        else if (a.rank < b.rank)
//            return -1;
//        else
//            return 0;
        
        // to rank descendingly, comment the above and use the following
         if (a.rank > b.rank)
            return -1;
        else if (a.rank < b.rank)
            return 1;
        else
            return 0;
      
           
    }
}