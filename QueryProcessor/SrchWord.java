import java.util.ArrayList;
public class SrchWord {
	
	public  String Word;
	
	public  ArrayList< DocID> DocsID;
	public int IDf;
	
	
	
	public SrchWord(String w,DocID d)
	{
		
		Word=w;
		
		
		 DocsID=new ArrayList< DocID>();
		DocsID.add(d);
	}
	
	
	

	void AddDocsID(DocID p)
	{
		DocsID.add(p);
		
	}
	

	void PrintSrchWord()
	{
		System.out.println("word : "+Word );
		
		for( DocID d : DocsID)
		{
			System.out.println(" Positions  in DocId: "+d.ID);
			System.out.println("WordID: "+d.WordID+"  WRank : "+d.wrank+"  tf: "+d.tf);
			
			d.Printpos();
			
		}
		
		
	}
	
}
