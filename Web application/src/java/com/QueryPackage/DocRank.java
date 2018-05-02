package com.QueryPackage;

import java.util.ArrayList;
import java.util.Comparator;

public class DocRank {
	public int ID;
	public double rank;
	public boolean valid;

	public DocRank(double r, boolean v) {

		this.rank = r;
		this.valid = v;
	}

	public void AddRank(double x) {
		this.rank += x;
	}

}