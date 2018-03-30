import java.util.ArrayList;

public class Word {
	public double Rank=0;
	public  ArrayList Postions_list=new ArrayList();
	
	public Word( Double r,int p)
	{
		Rank=r;
		Postions_list.add(p);
	}
	
	void AddRank(double r)
	{
		
		Rank+=r;
	}
	
	void AddPostion(int p)
	{
		Postions_list.add(p);
	}
	
	void Print_PosAndRank()
	{
		System.out.print("Rank : "+this.Rank+ "   ");
		 System.out.print("[");
		for(int i=0;i<Postions_list.size();i++)
		{
			System.out.print(Postions_list.get(i)+",");
		
		}
		System.out.print("]");
		System.out.println();
	}
	
	
}

