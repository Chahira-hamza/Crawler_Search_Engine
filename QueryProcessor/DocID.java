import java.util.ArrayList;
import java.util.Collections;

public class DocID {

	public ArrayList<Integer> WordPos;
	public int ID;
	public double tf;
	public double wrank;
	public int WordID;
	public boolean valid;

	public double PageRank = 1;

	public DocID(int i, double t, int Wid) {
		this.ID = i;
		this.wrank = t;
		WordID = Wid;
		WordPos = new ArrayList<Integer>();
		valid = true;

	}

	void AddPos(int p) {
		WordPos.add(p);

	}

	int getId() {
		return this.ID;
	}

	void set_tf(int l) {
		this.tf = this.wrank / l;

	}

	// void SortPosList()
	// {
	// Collections.sort(WordPos);
	//
	//
	// }
	void SetRank(double r) {
		this.PageRank = r;

	}

	void Printpos() {

		for (int e : WordPos) {
			System.out.print(e + " , ");
		}

		System.out.println();
	}

}