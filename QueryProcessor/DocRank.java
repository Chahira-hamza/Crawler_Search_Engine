import java.util.ArrayList;

public class DocRank {
	
	// public int ID;
	 public double rank; 
	 public boolean valid;
	 
	 public DocRank(double r , boolean v)
		{
	
			this.rank=r;
			this.valid=v;
		}
	 public void  AddRank(double x)
	 {
		 this.rank+=x;
	 }

}
