import java.util.ArrayList;
public class SrchWord {
	
	public  String Word;
	
	public  ArrayList<Integer> DocsID=new ArrayList<Integer>();
	
	public SrchWord(String w,int p)
	{
		//Rank=r;
		Word=w;
		
		if(p!=-1)
		{	
			DocsID.add(p);
		}
	}
	
	
	
	void AddDocsID(int p)
	{
		DocsID.add(p);
		
	}
	

}
