package com.QueryPackage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SrchWord {

	public String Word;

	// public ArrayList< DocID> DocsID;
	public HashMap<Integer, DocID> DocsID = new HashMap<Integer, DocID>();
	public double IDf;

	public SrchWord(String w, int DID, DocID d) {

		Word = w;

		DocsID.put(DID, d);
		// DocsID=new ArrayList< DocID>();
		// DocsID.add(d);
	}

	void set_IDF(double n) {
		double m = this.DocsID.size();
		this.IDf = Math.log10(n / m);
	}

	void AddDocsID(DocID p, int D) {
		DocsID.put(D, p);

	}

	void PrintSrchWord() {
		System.out.println("word : " + Word);
		DocID E;
		for (Map.Entry<Integer, DocID> entry1 : DocsID.entrySet()) {
			E = entry1.getValue();

			System.out.println("  DocId: " + entry1.getKey() + "  valid: " + E.valid);
			System.out.println("WordID: " + E.WordID + "  WRank : " + E.wrank + "  tf: " + E.tf);
			System.out.print("positions : ");
			E.Printpos();

		}
	}

}