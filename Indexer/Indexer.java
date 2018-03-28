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
					if (!WDict.containsKey(root))
					{
						WDict.put(root, RankDict.VDict.get("keyword"));
					}

					else
						WDict.replace(root, WDict.get(root) + RankDict.VDict.get("keyword"));

				}
			}
		}

	}
		
	    public static void GetTitle(Document doc)
	    {	   
	    	String s,w;
	    	Elements m ;

	    	String title = doc.title();
	    	System.out.println("-"+title+"_______");
	    	s=title;

	    	String[] words=s.split("\\P{Alpha}+");

	    	for(String word : words)
	    	{
	    		w=word.toLowerCase(); 					// add  words in dictionary of doc 
	    		root=stemmer.stem(w);
	    		if(!(stopwords.h.contains(root)))
	    		{ 
	    			System.out.println(root);
	    			if (!WDict.containsKey(root))
	    			{
	    				WDict.put(root,RankDict.VDict.get("title"));
	    			}

	    			else
	    				WDict.replace(root,WDict.get(root)+RankDict.VDict.get("title"));

	    		}
	    	}	

	    	m= doc.select("title").remove();
	    }

	    public static void GetHeaders(Document doc) 
	    {
	    	Elements h, m;
	    	String n, w, s;
	    	int pos;
	    	for (int i = 1; i < 7; i++) {
	    		n = Integer.toString(i);
	    		h = doc.getElementsByTag("h" + n);
	    		//System.out.println(h.toString());

	    		for (Element e : h) {
	    			s = e.text();
	    			String[] words1 = s.split("\\P{Alpha}+");
	    			for (String word : words1) {
	    				w = word.toLowerCase();							 // add in dictinary of doc
	    				root=stemmer.stem(w);
	    				if (!(stopwords.h.contains(root))) {
	    					System.out.println(root);
	    					if (!WDict.containsKey(root)) {
	    						WDict.put(root, RankDict.VDict.get("h" + n));
	    					}

	    					else
	    						WDict.replace(root, WDict.get(root) + RankDict.VDict.get("h" + n));

	    				}
	    				// System.out.println(word);
	    			}

	    		}
	    		m = doc.select("h" + n).remove();
	    		String hh=m.text();
	    		System.out.println("_______"+hh+"_______________");

	    	}

	    }

	    public static void GetText(Document doc)
	    {
		String w;
		String text = doc.text();
		String[] words1 = text.split("\\P{Alpha}+");
		System.out.println(text);
		System.out.println("_________________________");
		for (String word : words1) {
			w = word.toLowerCase(); // add in dictinary of doc
			if (!(stopwords.h.contains(w))) {
				System.out.println(w);
				if (!WDict.containsKey(w)) {
					WDict.put(w, RankDict.VDict.get("p"));
				}

				else
					WDict.replace(w, WDict.get(w) + RankDict.VDict.get("p"));

			}
			// System.out.println(word);
		}

	}

	    public static void main(String[] args) throws SQLException {  
            
	    	try 
	    	{  
	    		//ConnectToDB();

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
	    			
//	    			String t = doc.text();
//	    			System.out.println(t);
	    			
	    			System.out.println("_________________________________________");
	    			
	    			
	    			
	    			GetTitle(doc);				//// extract title ///	
	    			GetHeaders(doc);			//// extract headers ///
	    			GetText(doc);

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
//			    	InsertWords(WDict,Doc_ID);
//			    	InsertText(t,Doc_ID);
			    	i++;
			    	
			    	Iterator<String> itr = WDict.keySet().iterator();
		    		Iterator<Double> itr2 = WDict.values().iterator();

		    		while (itr.hasNext())
		    		{
		    			System.out.print(itr.next());
		    			System.out.print("  ");
		    			System.out.println(itr2.next());

		    		}
		    		
		    		WDict.clear();
			    	

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
