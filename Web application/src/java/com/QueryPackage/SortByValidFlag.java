
package com.QueryPackage;

import java.util.Comparator;

/**
 *
 * @author chahira
 */
public class SortByValidFlag implements Comparator<DocRank> {

@Override
public int compare(DocRank a, DocRank b)
{
    if (a.valid && b.valid)
    {
        if (a.rank > b.rank)
            return -1;
        else if (a.rank < b.rank)
            return 1;
        else
            return 0;
    }
    else
    {
        if (a.valid)
            return -1;
        else if (b.valid)
            return 1;
        else
        {
            if (a.rank > b.rank)
                return -1;
            else if (a.rank < b.rank)
                return 1;
            else
                return 0;
        }
    }
}

}
 