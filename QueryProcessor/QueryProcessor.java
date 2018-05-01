import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.sql.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files; // for loop on files in folder
import java.nio.file.Path;
import java.nio.file.Paths;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class QueryProcessor {
	String inputString = "search engine";
	private static String connectionUrl = "jdbc:sqlserver://localhost:1433;"
			+ "databaseName=Indexer;user=sa;password=root"; // Declare the JDBC
	// objects.
	static Connection con = null;
	static String Driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	static Stemmer stemmer = new Stemmer();
	static StopWords stopwords = null;
	static ArrayList<String> SrchStmt = new ArrayList<String>();
	static HashMap<Integer, DocRank> pages = new HashMap<Integer, DocRank>();    // sort it 
	static HashMap<String, SrchWord> WordList = new HashMap<String, SrchWord>();
	static String Tokens[];

	public static void ConnectToDB() {
		try {
			// Establish the connection.
			Class.forName(Driver);
			con = DriverManager.getConnection(connectionUrl);
			System.out.println(" Connected to Database");
		}

		catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void FilterStmt(String InputString) {
		String s = InputString.toLowerCase();
		String[] words = s.split("\\P{Alpha}+");

		SrchStmt = new ArrayList<String>(Arrays.asList(words));
		Iterator<String> it = SrchStmt.iterator();
		int i = 0;
		while (it.hasNext()) {
			String st = it.next();
			if (stopwords.h.contains(st)) {
				it.remove();

			}

		}

		int n = SrchStmt.size();
		Tokens = new String[n];
		for (String element : SrchStmt) {

			// System.out.print(element+"-");
			Tokens[i] = element;
			i++;
		}

	}

	public static int GetLengthDoc(int DID) throws SQLException {
		int len;
		PreparedStatement GetLenStmt = null;
		String SelectLen = " select Count(Pos)from WordPostions where Doc_ID = ?";
		con.setAutoCommit(false);
		GetLenStmt = con.prepareStatement(SelectLen);
		GetLenStmt.setInt(1, DID);
		ResultSet rs;
		rs = GetLenStmt.executeQuery();
		con.commit();
		if (rs.next()) {

			len = rs.getInt(1);
			con.setAutoCommit(true);
		} else
			len = -1;
		return len;

	}

	public static int GetNumDocs() throws SQLException {

		int NumDocs;
		PreparedStatement GetNumStmt = null;
		String CountDocs = "select count(*) from DocText";
		con.setAutoCommit(false);
		GetNumStmt = con.prepareStatement(CountDocs);

		ResultSet rs;
		rs = GetNumStmt.executeQuery();
		con.commit();
		if (rs.next()) {

			NumDocs = rs.getInt(1);
			con.setAutoCommit(true);
		} else {
			NumDocs = -1;

		}

		return NumDocs;

	}

	public static ArrayList<Integer> GetPos(int WID, int DocId) throws SQLException {
		PreparedStatement GetPosStmt = null;
		String SelectPos = " Select Pos from WordPostions WHERE Word_ID=? and Doc_ID= ?";

		ArrayList<Integer> posList = new ArrayList<Integer>();
		GetPosStmt = con.prepareStatement(SelectPos);
		GetPosStmt.setInt(1, WID);
		GetPosStmt.setInt(2, DocId);

		ResultSet rs;
		rs = GetPosStmt.executeQuery();
		boolean f = rs.next();
		if (f) {
			posList.add(rs.getInt(1));
			while (rs.next()) {
				posList.add(rs.getInt(1));
			}

		}

		Collections.sort(posList);
		return posList;

	}

	public static double GetPageRank_DB(int DID) throws SQLException {
		double rank;

		PreparedStatement GetPageRStmt = null;
		String PageR = " Select linkRank *pageRank from Docs_URL where ID= ?";
		con.setAutoCommit(false);
		GetPageRStmt = con.prepareStatement(PageR);
		GetPageRStmt.setInt(1, DID);
		ResultSet rs;
		rs = GetPageRStmt.executeQuery();
		con.commit();
		if (rs.next()) {

			rank = rs.getDouble(1);
			con.setAutoCommit(true);
		} else
			rank = 1;

		return rank;
	}

	public static void GetDoc_StemWord(String IN, int numDocs) throws SQLException {
		PreparedStatement SelectWordStmt = null;
		int lenDoc;
		int DocD;
		double rank;

		String Select_Docs = "SELECT   Word, Word_ID ,Rankw,Doc_ID   FROM Words where Stemmed_Word= ?";

		FilterStmt(IN);
		try {
			for (String element : SrchStmt) {
				String root = stemmer.stem(element);
				System.out.println(element);

				con.setAutoCommit(false);
				SelectWordStmt = con.prepareStatement(Select_Docs);
				SelectWordStmt.setString(1, root);

				ResultSet rs;
				rs = SelectWordStmt.executeQuery();
				con.commit();
				int i = 0;

				if (!WordList.containsKey(root)) {
					boolean f = rs.next();
					if (f) {
						WordList.put(element, new SrchWord(rs.getString(1), rs.getInt(4),
								new DocID(rs.getInt(4), rs.getInt(3), rs.getInt(2))));

						DocD = rs.getInt(4);
						rank = GetPageRank_DB(DocD); /// to get page rank from
														/// DB
						lenDoc = GetLengthDoc(DocD);
						WordList.get(element).set_IDF(numDocs);
						ArrayList<Integer> pp = GetPos(rs.getInt(2), DocD);
						WordList.get(element).DocsID.get(rs.getInt(4)).set_tf(lenDoc);

						WordList.get(element).DocsID.get(DocD).SetRank(rank);
						for (int e : pp) {
							WordList.get(element).DocsID.get(DocD).AddPos(e);
						}
						while (rs.next()) {

							i++;
							DocD = rs.getInt(4);
							rank = GetPageRank_DB(DocD);
							WordList.get(element).AddDocsID(new DocID(DocD, rs.getInt(3), rs.getInt(2)), DocD);
							lenDoc = GetLengthDoc(DocD);
							WordList.get(element).DocsID.get(DocD).set_tf(lenDoc);
							rank = GetPageRank_DB(DocD);
							WordList.get(element).DocsID.get(DocD).SetRank(rank);
							pp = GetPos(rs.getInt(2), DocD);

							for (int e : pp) {
								WordList.get(element).DocsID.get(DocD).AddPos(e);
							}

						}
						WordList.get(element).set_IDF(numDocs);
					}
				} else {

					System.out.println(element + " is not found in DB");
				}

			}
		}

		catch (SQLException e) {

			System.out.println("Sql Exception :" + e.getMessage());
			if (con != null) {
				try {
					System.err.print("Transaction is being rolled back");
					con.rollback();
				} catch (SQLException excep) {

					System.out.println("Sql Exception :" + e.getMessage());
				}
			}
		}

		finally {
			if (SelectWordStmt != null) {
				SelectWordStmt.close();
			}

			con.setAutoCommit(true);
		}
	}

	public static void GetDoc_Word(String IN, int numDocs) throws SQLException {
		PreparedStatement SelectWordStmt = null;

		String Select_Docs = " SELECT   Word, Word_ID ,Rankw,Doc_ID   FROM Words where Word= ?";
		int lenDoc;
		int DocD;
		double rank;
		FilterStmt(IN);
		try {
			for (String element : SrchStmt) {

				// System.out.println(element);
				con.setAutoCommit(false);
				SelectWordStmt = con.prepareStatement(Select_Docs);
				SelectWordStmt.setString(1, element);

				ResultSet rs;
				rs = SelectWordStmt.executeQuery();
				con.commit();
				int i = 0;
				if (!WordList.containsKey(element)) {
					boolean f = rs.next();
					if (f) {

						WordList.put(element, new SrchWord(rs.getString(1), rs.getInt(4),
								new DocID(rs.getInt(4), rs.getInt(3), rs.getInt(2))));

						DocD = rs.getInt(4);
						lenDoc = GetLengthDoc(DocD);

						rank = GetPageRank_DB(DocD);
						WordList.get(element).DocsID.get(DocD).SetRank(rank);

						ArrayList<Integer> pp = GetPos(rs.getInt(2), DocD);
						WordList.get(element).DocsID.get(rs.getInt(4)).set_tf(lenDoc);
						for (int e : pp) {
							WordList.get(element).DocsID.get(DocD).AddPos(e);
						}
						while (rs.next()) {

							i++;
							DocD = rs.getInt(4);
							WordList.get(element).AddDocsID(new DocID(DocD, rs.getInt(3), rs.getInt(2)), DocD);
							lenDoc = GetLengthDoc(DocD);

							rank = GetPageRank_DB(DocD);
							WordList.get(element).DocsID.get(DocD).SetRank(rank);

							WordList.get(element).DocsID.get(DocD).set_tf(lenDoc);
							pp = GetPos(rs.getInt(2), DocD);

							for (int e : pp) {
								WordList.get(element).DocsID.get(DocD).AddPos(e);
							}

						}
						WordList.get(element).set_IDF(numDocs);
					}

					else {

						System.out.println(element + " is not found in DB");
					}

				}

			}
		}

		catch (SQLException e) {

			System.out.println("Sql Exception :" + e.getMessage());
			if (con != null) {
				try {
					System.err.print("Transaction is being rolled back");
					con.rollback();
				} catch (SQLException excep) {

					System.out.println("Sql Exception :" + e.getMessage());
				}
			}
		}

		finally {
			if (SelectWordStmt != null) {
				SelectWordStmt.close();
			}

			con.setAutoCommit(true);
		}

	}

	public static void Postions() {

		int mypos;
		int doc_ID;

		// loop for valid doc

		HashMap<Integer, DocID> dd = WordList.get(Tokens[0]).DocsID;

		for (Entry<Integer, DocID> entry12 : dd.entrySet())

		{
			DocID d11 = entry12.getValue();
			int doc_ID11 = entry12.getKey();

			for (int j = 1; j < Tokens.length; j++) {
				if ((WordList.get(Tokens[j]).DocsID.get(doc_ID11) == null)) {
					WordList.get(Tokens[0]).DocsID.get(doc_ID11).valid = false;
				}
			}
		}
		////////////////////////////////////////////////
		int f = 1;
		int x = 0;
		for (Entry<Integer, DocID> entry1 : dd.entrySet())

		{
			DocID d = entry1.getValue();
			doc_ID = entry1.getKey();
			if (WordList.get(Tokens[0]).DocsID.get(doc_ID).valid == true) {
				for (int i = 0; i < d.WordPos.size(); i++) {
					mypos = d.WordPos.get(i);
					f = 1;
					for (int j = 1; j < Tokens.length; j++) {

						if (!(WordList.get(Tokens[j]).DocsID.get(doc_ID).WordPos.contains(mypos + j)))

						{
							f = 0;
							break;

						}
						x = j;
					}

					if (f == 1 && x == Tokens.length - 1) {
						WordList.get(Tokens[0]).DocsID.get(doc_ID).valid = true;
						break;

					}

				}

			}

		}
	}

	public static void Ranker() {

		HashMap<Integer, DocID> documents;
		double rr = 20.0;
		double rank;
		for (int i = 0; i < Tokens.length; i++) {
			documents = WordList.get(Tokens[i]).DocsID;

			for (Entry<Integer, DocID> entry1 : documents.entrySet()) {
				DocID page = entry1.getValue();
				if (!pages.containsKey(page.ID)) {
					page.PageRank += WordList.get(Tokens[i]).IDf * page.tf * page.wrank;

					pages.put(page.ID, new DocRank(page.PageRank, page.valid));
				}

				else {
					page.PageRank += WordList.get(Tokens[i]).IDf * page.tf * page.wrank;
					pages.get(page.ID).AddRank(page.PageRank);

				}
			}

		}

	}

	public static void main(String[] args) throws SQLException {

		Scanner in = new Scanner(System.in);
		String InputStr = in.nextLine(); // to read user input;

		boolean quotated = false; // to know input with quotation or not;
		try {

			ConnectToDB();
			int TotalnumDoc = GetNumDocs();    
			if (InputStr.startsWith("\"") && InputStr.endsWith("\"")) {
				System.out.println("quotation");
				
				quotated = true;
				
				GetDoc_Word(InputStr, TotalnumDoc);

				System.out.println("total number of doc =" + TotalnumDoc);
				Postions();
				

			}

			else {
				GetDoc_StemWord(InputStr, TotalnumDoc);
				quotated = false;
			}

			for (Map.Entry<String, SrchWord> entry1 : WordList.entrySet()) /// for
			/// printing
			{
				System.out.println(entry1.getKey() + "	IDF: " + entry1.getValue().IDf);

				SrchWord w1 = entry1.getValue();
				w1.PrintSrchWord();
				System.out.println("___________________________");

			}
			System.out.println("================================================================");
			Ranker();
			for (Map.Entry<Integer, DocRank> entry1 : pages.entrySet()) // for
																		// printing
																		// pages
																		// with
																		// rank
			{
				System.out.println("DocID : " + entry1.getKey());
				System.out.println("pageRank: " + entry1.getValue().rank);
			}

		}

		catch (Exception e) {

			e.printStackTrace();

		}

		finally {

			WordList.clear(); // clearing map after searching
			if (con != null)
				try {
					con.close();
				} catch (Exception e) {
				}
		}

	}
}
