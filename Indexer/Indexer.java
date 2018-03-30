import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.sql.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;    // for loop on files in folder
import java.nio.file.Path;
import java.nio.file.Paths;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class Indexer {
	   // Create a variable for the connection string.  
	 	private static String connectionUrl = "jdbc:sqlserver://localhost:1433;" + "databaseName=Indexer;user=sa;password=root"; 
	   // Declare the JDBC objects.  
	    static Connection con = null;  
	    static Statement stmt = null;  
	    static ResultSet rs = null;
	    static String Doc_Text;
	    static int Doc_ID;
	    static  String Driver="com.microsoft.sqlserver.jdbc.SQLServerDriver";
	    static String Directory="E:\\Java\\APT_Project\\Docs";
	    static Path folder = Paths.get(Directory);
	    static String filename;
	    static Document doc ;
	    static StopWords stopwords = null;
	    static  ValueDict RankDict = null;
	    static  HashMap<String, Double> WDict = new HashMap<String, Double>();   //word Dictionary;
	    static Element others;
	    static  String dbName="Indexer";
	    static 	Stemmer stemmer=new Stemmer();
	    static String root;
	    static int position ;
	    static HashMap <String , Word> WordDict=new HashMap<String, Word>(); 
	    static  String Edited_Text;
	    static ArrayList<String> aList=null;
	 
	    public static  void ConnectToDB()
	    {
	    	try
	    	{
	    		// Establish the connection.  
	    		Class.forName(Driver);  
	    		con = DriverManager.getConnection(connectionUrl);  
	    		System.out.println(" Connected to Database" );
	    	}

	    	catch (Exception e)
	    	{  
	    		e.printStackTrace();  
	    	}  


	    }

	    public static void InsertWords(HashMap<String, Double> W , int DocID) throws SQLException {

		PreparedStatement WordStmt = null;

		String Insert_Words = "INSERT INTO Words (Doc_ID,word,Rankw)  VALUES  (?,?,?)";

		try {
			con.setAutoCommit(false);
		
			WordStmt = con.prepareStatement(Insert_Words);

			for (Map.Entry<String, Double> e : W.entrySet()) {
				WordStmt.setLong(1, DocID);
				WordStmt.setString(2, e.getKey());
				WordStmt.setLong(3, e.getValue().intValue());
				WordStmt.executeUpdate();
				con.commit();
			}
		} catch (SQLException e) {
			
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
			if (WordStmt != null) {
				WordStmt.close();
			}

			con.setAutoCommit(true);
		}
	}
	    
		public static void InsertText(String text, int DocID) throws SQLException {
		PreparedStatement TextStmt = null;
		String Insert_Text = "INSERT INTO DocText (Doc_ID,Dtext)  VALUES  (?,? )";

		try {
			con.setAutoCommit(false);
			TextStmt = con.prepareStatement(Insert_Text);
			TextStmt.setLong(1, DocID);
			TextStmt.setString(2, text.toLowerCase());
			TextStmt.executeUpdate();
			con.commit();

		}

		catch (SQLException e) {
			
			System.out.println("Sql Exception :" + e.getMessage());
			if (con != null) {
				try {
					System.err.print("Transaction is being rolled back");
					con.rollback();												// free the resoource
				} catch (SQLException excep) {

					System.out.println("Sql Exception :" + e.getMessage());
				}
			}
		}

		finally {
			if (TextStmt != null) {
				TextStmt.close();
			}

			con.setAutoCommit(true);
		}

	}
		
		public static void Edit_Text(String Text)
		{
				System.out.println(Text);
			String[] words = Text.split("\\P{Alpha}+");
//			for(String w:words)
//			{
//				System.out.println(w);
//			}

			aList = new ArrayList<String>(Arrays.asList(words));
			Iterator<String> it = aList.iterator();
    		while (it.hasNext()) {
    		    String st = it.next();
    		    if (stopwords.h.contains(st)) 
    		    {
    		        it.remove();
    		    }
    		}
    		System.out.println("___________________");
    		int i=0;
    		for (String element : aList) 
        		{  
        			
   					System.out.println(element+" -> index: "+i);
   					i++;
   			}
    			
		}
		public static void GetKeyWords(Document doc)  {

		String keywords = doc.select("meta[name=keywords]").attr("content");
		if (keywords != null) {
			System.out.println("keywords= " + keywords);
			String[] words = keywords.split("\\P{Alpha}+");
			String w;

			for (String word : words) 
			{
				w = word.toLowerCase(); // add words in dictionary of doc
				root=stemmer.stem(w);
				if (!(stopwords.h.contains(root)))
				{
					 System.out.println(root);
					if (!WordDict.containsKey(root))
					{
						WordDict.put(root, new Word(RankDict.VDict.get("keyword"),-1));
					}

					else
					{
						//WDict.replace(root, WDict.get(root) + RankDict.VDict.get("keyword"));
						WordDict.get(root).AddPostion(-1);
						WordDict.get(root).AddRank(RankDict.VDict.get("keyword"));

					}

				}
			}
		}

	}
		
	  
		public static void GetTitle(Document doc) {
			String s, w;
			Elements m;
			int index;

			String title = doc.title();
			System.out.println("title: " + title + "_______");
			s = title.toLowerCase();

			String[] words = s.split("\\P{Alpha}+");

			for (String word : words) {

				if (!(stopwords.h.contains(word))) {
					index = aList.indexOf(word); 			// first get the index of the word
					root = stemmer.stem(word);
					System.out.println(root);

					if (!WordDict.containsKey(root)) // check exists or not
					{
						WordDict.put(root, new Word(RankDict.VDict.get("title"), index));

					}

					else {
						WordDict.get(root).AddPostion(index);
						WordDict.get(root).AddRank(RankDict.VDict.get("title"));

					}
					// WordDict.replace(root,WordDict.get(root).AddRank(5.0), ;
					// WDict.replace(root,WDict.get(root)+RankDict.VDict.get("title"));
					aList.set(index, null);
				}

			}

			m = doc.select("title").remove();   // remove title from the doc 
		}

		public static void GetHeaders(Document doc) {
			Elements h, m;
			String n, w, s;
			int index;
			for (int i = 1; i < 7; i++) {
				n = Integer.toString(i);
				h = doc.getElementsByTag("h" + n);
				// System.out.println("h"+n+": "+h.toString());
				String header=h.text();
				System.out.println("h"+n+": "+header);
				for (Element e : h) {
					s = e.text().toLowerCase();
					String[] words1 = s.split("\\P{Alpha}+");
					for (String word : words1) {
						//w = word.toLowerCase(); // add in dictinary of doc

						if (!(stopwords.h.contains(word))) {
							index = aList.indexOf(word);
							root = stemmer.stem(word);
							System.out.println(root);
							if (!WordDict.containsKey(root)) {
								WordDict.put(root, new Word(RankDict.VDict.get("h" + n), index));
								// WDict.put(root, RankDict.VDict.get("h" + n));
							}

							else {
								// WDict.replace(root, WDict.get(root) +
								// RankDict.VDict.get("h" + n));
								WordDict.get(root).AddPostion(index);
								WordDict.get(root).AddRank(RankDict.VDict.get("h" + n));
							}
							//aList.remove(index);
							aList.set(index, null);
						}
						// System.out.println(word);
					}

				}
				m = doc.select("h" + n).remove();             // remove headers from the doc 
				String hh = m.text();
				System.out.println("removed header : " + hh );

			}

		}

		public static void GetText(Document doc) {
			int index;

			String text = doc.text().toLowerCase();
			String[] words1 = text.split("\\P{Alpha}+");
			System.out.println(text);
			System.out.println("_________________________");
			for (String w : words1) {

				if (!(stopwords.h.contains(w))) {
					root = stemmer.stem(w);
					System.out.println(root);
					index = aList.indexOf(w);
					if (!WordDict.containsKey(root)) {
						// WDict.put(w, RankDict.VDict.get("p"));
						WordDict.put(root, new Word(RankDict.VDict.get("p"), index));
					}

					else {
						// WDict.replace(w, WDict.get(w) + RankDict.VDict.get("p"));
						WordDict.get(root).AddPostion(index);
						WordDict.get(root).AddRank(RankDict.VDict.get("p"));
					}
					//boolean rem=aList.remove(word);
					aList.set(index, null);
				}
				// System.out.println(word);
			}

		}

	    public static void main(String[] args) throws SQLException {  
            
	    	try 
	    	{  
	    		ConnectToDB();

	    		DirectoryStream<Path> stream = Files.newDirectoryStream(folder);
	    		int i=1;
	    		for (Path entry : stream) 
	    		{
	    			// Process the entry
	    			filename=entry.getFileName().toString();
	    			File input = new File("E:\\Java\\APT_Project\\Docs\\"+filename);
	    			doc= Jsoup.parse(input,"utf-8");
	    			
	    			String f=filename.replaceAll("\\D+","");  								 // regex delete non digits
	    			Doc_ID=Integer.parseInt(f, 2);
	    			
	    			System.out.println("Doument ID : "+Doc_ID);
	    			System.out.println("loop #"+i+"-");
	    			
	    			String t = doc.text().toLowerCase();
	    			 Edit_Text(t);
//	    			System.out.println(t);
	    			
	    			System.out.println("_________________________________________");
	    			
	    			
	    			
	    			GetTitle(doc);				//// extract words in title ///	
	    			GetHeaders(doc);			//// extract words in headers ///
	    			GetText(doc);				// extract words in text //

	    			//GetKeyWords(doc);	
	    			System.out.println("__________________________");	
	    			
	    			
	    			

	    			//rs= stmt.executeQuery(SQL); 

	    			// String SQL = "INSERT INTO DocText (Doc_ID,Dtext)  "+ " VALUES  ('"  +1+  "','" + t +"')";
	    			//String SQL="DELETE FROM Docs_URL";
	    			//String SQL = "INSERT INTO DocText (Doc_ID,Dtext)    VALUES (?, ?")";
	    			//stmt = con.createStatement(); 
	    			//	    			PreparedStatement sql= con.prepareStatement( "INSERT INTO DocText (Doc_ID,Dtext)  VALUES  (?,?  )");
	    			//	    			sql.setLong(1, 2);
	    			//	    			sql.setString(2, t.toLowerCase());
	    			//			    	sql.executeUpdate();
	    			System.out.println("sucess text ?????????????????????????????????????");
			    	
//	    			
			    	//InsertWords(WDict,Doc_ID);
			    	//InsertText(t,Doc_ID);
			    	i++;
			    	
			    	Iterator<String> itr = WDict.keySet().iterator();
		    		Iterator<Double> itr2 = WDict.values().iterator();
		    		
		    		Iterator<String> itrw1 = WordDict.keySet().iterator();
		    		Iterator<Word> itrw2 = WordDict.values().iterator();
//		    		while (itr.hasNext())
//		    		{
//		    			System.out.print(itr.next());
//		    			System.out.print("  ");
//		    			System.out.println(itr2.next());
//
//		    		
		    		
		    		
		    		for (Map.Entry<String, Word> entry1 : WordDict.entrySet())
		    		{
		    		    	System.out.print(entry1.getKey()+"\t");
		    		    	Word w1=entry1.getValue();
		    		    	w1.Print_PosAndRank();
		    		     
		    		    
		    		}
	    		
		    		WordDict.clear();
			    	

	    		}


	    	
	    		// Create and execute an SQL statement that returns some data.  
	    		// String SQL = "SELECT Title  FROM Docs";  

	    		// Iterate through the data in the result set and display it.  
	    		//	         	while (rs.next())
	    		//	         	{  
	    		//	        	 	System.out.println(rs.getString(1));  
	    		//	         	}  
	    		stream.close();

	    		
	    	}  




	    	// Handle any errors that may have occurred.  
	    	catch (Exception e)
	    	{  
	    		
	    		 
	    		e.printStackTrace(); 
	    		

	    	}  


	    	finally 
	    	{  
	    		         if (rs != null) try { rs.close(); } catch(Exception e) {}  
	    		         if (stmt != null) try { stmt.close(); } catch(Exception e) {}  
	    			      if (con != null) try { con.close(); } catch(Exception e) {}  
	    	}  
	    }  

}
