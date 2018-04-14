import java.util.ArrayList;

public class Word {
	public Double Rank=0.0;
	public  String Word;
	public  ArrayList<Integer> Postions_list=new ArrayList<Integer>();
	
	public Word( double r,String w,int p)
	{
		Rank=r;
		Word=w;
		if(p!=-1)
		{	
			Postions_list.add(p);
		}
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

