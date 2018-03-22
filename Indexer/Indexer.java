import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
	    static  String Driver="com.microsoft.sqlserver.jdbc.SQLServerDriver";
	    static String Directory="E:\\Java\\APT_Project\\Docs";
	    static Path folder = Paths.get(Directory);
	    static String filename;
	    static Document doc ;
	    static StopWords stopwords = null;
	    static  ValueDict RankDict = null;
	    static  HashMap<String, Double> WDict = new HashMap<String, Double>();   //word Dictionary;
	    static Element others;

	   
	 
	 
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
			  if(!(stopwords.h.contains(w)))
			  { 
				  System.out.println(w);
				  if (!WDict.containsKey(w))
				  {
					  WDict.put(w,RankDict.VDict.get("title"));
				  }

				  else
					  WDict.replace(w,WDict.get(w)+RankDict.VDict.get("<title>"));

			  }
		  }	

		  m= doc.select("title").remove();
	  }
	
	  
	  public static void GetHeaders(Document doc) {
		Elements h, m;
		String n, w, s;
		for (int i = 1; i < 7; i++) {
			n = Integer.toString(i);
			h = doc.getElementsByTag("h" + n);
			System.out.println(h.toString());

			for (Element e : h) {
				s = e.text();
				String[] words1 = s.split("\\P{Alpha}+");
				for (String word : words1) {
					w = word.toLowerCase(); // add in dictinary of doc
					if (!(stopwords.h.contains(w))) {
						System.out.println(w);
						if (!WDict.containsKey(w)) {
							WDict.put(w, RankDict.VDict.get("h" + n));
						}

						else
							WDict.replace(w, WDict.get(w) + RankDict.VDict.get("h" + n));

					}
					// System.out.println(word);
				}

			}
			m = doc.select("h" + n).remove();
			String hh=m.text();
			System.out.println("_______"+hh+"_______________");

		}

	}
	  
	public static void GetText(Document doc) {
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
	    public static void main(String[] args) {  
            
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


	    			System.out.print(i+"-");
	    			GetTitle(doc);				//// extract title ///	
	    			GetHeaders(doc);			//// extract headers ///
	    			GetText(doc);
	    			 			
	    			//System.out.println(hh);
	    			
//	    			String t=doc.text();
//	    			System.out.println(t);
//	    			
	    			System.out.println("__________________________");	
	    			i++;

	    		}


	    		//				    	 String SQL = "DELETE  FROM Docs"; 
	    		//				    	 stmt = con.createStatement();  
	    		//				        // rs= stmt.executeQuery(SQL); 
	    		//				    	 
	    		// for deletion 
	    		//				    	 stmt.executeUpdate(SQL);




	    		// Create and execute an SQL statement that returns some data.  
	    		// String SQL = "SELECT Title  FROM Docs";  

	    		// Iterate through the data in the result set and display it.  
	    		//	         	while (rs.next())
	    		//	         	{  
	    		//	        	 	System.out.println(rs.getString(1));  
	    		//	         	}  
	    		stream.close();

	    		Iterator<String> itr = WDict.keySet().iterator();
	    		Iterator<Double> itr2 = WDict.values().iterator();

	    		while (itr.hasNext())
	    		{
	    			System.out.print(itr.next());
	    			System.out.print("  ");
	    			System.out.println(itr2.next());

	    		}
	    	}  




	    	// Handle any errors that may have occurred.  
	    	catch (Exception e)
	    	{  
	    		e.printStackTrace(); 


	    	}  


	    	finally 
	    	{  
	    		//	         if (rs != null) try { rs.close(); } catch(Exception e) {}  
	    		//	         if (stmt != null) try { stmt.close(); } catch(Exception e) {}  
	    		//	         if (con != null) try { con.close(); } catch(Exception e) {}  
	    	}  
	    }  

}
