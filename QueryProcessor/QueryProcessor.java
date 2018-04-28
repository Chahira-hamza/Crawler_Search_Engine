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

	public static void GetDoc_StemWord(String IN) throws SQLException {
		PreparedStatement SelectWordStmt = null;
		String Select_Docs = " SELECT  Doc_ID FROM Words where Stemmed_Word= ?";

		FilterStmt(IN);
		for (String element : SrchStmt) {
			String root = stemmer.stem(element);
			System.out.println(element);

			SelectWordStmt = con.prepareStatement(Select_Docs);
			SelectWordStmt.setString(1, root);

			ResultSet rs;
			rs = SelectWordStmt.executeQuery();

			if (!WordList.containsKey(root)) {
				boolean f = rs.next();
				if (f) {
					WordList.put(root, new SrchWord(element, rs.getInt(1)));
					while (rs.next()) {
						System.out.println(rs.getInt(1));
						WordList.get(root).AddDocsID(rs.getInt(1));

					}
				} else {

					System.out.println(element + " is not found in DB");
				}

			}
		}

		SelectWordStmt.close();
	}

	public static void GetDoc_Word(String IN) throws SQLException {
		PreparedStatement SelectWordStmt = null;
		String Select_Docs = " SELECT  Doc_ID FROM Words where Word= ?";

		FilterStmt(IN);
		for (String element : SrchStmt) {

			System.out.println(element);

			SelectWordStmt = con.prepareStatement(Select_Docs);
			SelectWordStmt.setString(1, element);

			ResultSet rs;
			rs = SelectWordStmt.executeQuery();

			if (!WordList.containsKey(element)) {
				boolean f = rs.next();
				if (f) {
					WordList.put(element, new SrchWord(element, rs.getInt(1)));
					while (rs.next()) {
						System.out.println(rs.getInt(1));
						WordList.get(element).AddDocsID(rs.getInt(1));

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
