
import java.util.ArrayList;
public class DocID {

	public  ArrayList<Integer> WordPos;
	 public int ID;
	 public double tf;
	 public double wrank;
	 public int WordID; 
	public DocID(int i ,double t, int Wid )
	{
		this.ID=i;
		this.wrank=t;
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
	
	void set_tf(int l)
	{
		this.tf=this.wrank/l;
		
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
