import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
	static HashMap<String, SrchWord> WordList = new HashMap<String, SrchWord>();

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
		while (it.hasNext()) {
			String st = it.next();
			if (stopwords.h.contains(st)) {
				it.remove();
			}
		}
		/*
		 * for (String element : SrchStmt) {
		 * 
		 * System.out.print(element+"-");
		 * 
		 * }
		 */
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
		return posList;

	}
	public static void GetDoc_StemWord(String IN) throws SQLException {
		PreparedStatement SelectWordStmt = null;
		// " SELECT Doc_ID FROM Words where Stemmed_Word= ?";
		String Select_Docs = "SELECT   Word, Word_ID ,Rankw,Doc_ID   FROM Words where Stemmed_Word= ?";
		String SelectPos = " Select Pos from WordPostions WHERE Word_ID=? and Doc_ID= ?";

		FilterStmt(IN);
		for (String element : SrchStmt) {
			String root = stemmer.stem(element);
			System.out.println(element);

			SelectWordStmt = con.prepareStatement(Select_Docs);
			SelectWordStmt.setString(1, root);

			ResultSet rs;
			rs = SelectWordStmt.executeQuery();
			int i = 0;
			if (!WordList.containsKey(root)) {
				boolean f = rs.next();
				if (f) {
					WordList.put(element, new SrchWord(element, new DocID(rs.getInt(4), rs.getInt(3), rs.getInt(2))));

					ArrayList<Integer> pp = GetPos(rs.getInt(2), rs.getInt(4));
					for (int e : pp) {
						WordList.get(element).DocsID.get(i).AddPos(e);
					}
					while (rs.next()) {

						i++;
						WordList.get(element).AddDocsID(new DocID(rs.getInt(4), rs.getInt(3), rs.getInt(2)));

						pp = GetPos(rs.getInt(2), rs.getInt(4));

						for (int e : pp) {
							WordList.get(element).DocsID.get(i).AddPos(e);
						}

					}
				}
			} else {

				System.out.println(element + " is not found in DB");
			}

		}
		SelectWordStmt.close();
	}

	

	public static void GetDoc_Word(String IN) throws SQLException {
		PreparedStatement SelectWordStmt = null;

		String Select_Docs = " SELECT   Word, Word_ID ,Rankw,Doc_ID   FROM Words where Word= ?";
		String SelectPos = " Select Pos from WordPostions WHERE Word_ID=? and Doc_ID= ?";
		FilterStmt(IN);
		for (String element : SrchStmt) {

			// System.out.println(element);

			SelectWordStmt = con.prepareStatement(Select_Docs);
			SelectWordStmt.setString(1, element);

			ResultSet rs;
			rs = SelectWordStmt.executeQuery();
			int i = 0;
			if (!WordList.containsKey(element)) {
				boolean f = rs.next();
				if (f) {

					WordList.put(element, new SrchWord(element, new DocID(rs.getInt(4), rs.getInt(3), rs.getInt(2))));

					ArrayList<Integer> pp = GetPos(rs.getInt(2), rs.getInt(4));
					for (int e : pp) {
						WordList.get(element).DocsID.get(i).AddPos(e);
					}
					while (rs.next()) {

						i++;
						WordList.get(element).AddDocsID(new DocID(rs.getInt(4), rs.getInt(3), rs.getInt(2)));

						pp = GetPos(rs.getInt(2), rs.getInt(4));

						for (int e : pp) {
							WordList.get(element).DocsID.get(i).AddPos(e);
						}

					}
				}

				else {

					System.out.println(element + " is not found in DB");
				}

			}

		}
		SelectWordStmt.close();

	}

	public static void main(String[] args) throws SQLException {

		Scanner in = new Scanner(System.in);
		String InputStr = in.nextLine();

		try {

			ConnectToDB();
			if (InputStr.startsWith("\"") && InputStr.endsWith("\"")) {
				System.out.println("quotation");
				GetDoc_Word(InputStr);

			}

			else {
				GetDoc_StemWord(InputStr);
			}

			for (Map.Entry<String, SrchWord> entry1 : WordList.entrySet()) {
				System.out.println(entry1.getKey());
				SrchWord w1 = entry1.getValue();
				w1.PrintSrchWord();

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
