
import java.util.ArrayList;
public class DocID {

	public  ArrayList<Integer> WordPos;
	 public int ID;
	 public int tf;
	 public int WordID; 
	public DocID(int i ,int t, int Wid )
	{
		this.ID=i;
		this.tf=t;
		WordID=Wid;
		WordPos=new ArrayList<Integer>();
		
	}
	
	void AddPos(int p)
	{
		WordPos.add(p);
		
	}
	
	int getId()
	{
	  return this.ID;
	}
	
	void Printpos()
	{
		
		for(int e: WordPos)
		{
			System.out.print(e+" , ");
		}
		
		System.out.println();
	}
	
}
