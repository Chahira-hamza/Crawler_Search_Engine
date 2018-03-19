import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
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


public class SqlConnect {
	   // Create a variable for the connection string.  
	 	private static String connectionUrl = "jdbc:sqlserver://localhost:1433;" + "databaseName=Indexer;user=sa;password=root"; 
	   // Declare the JDBC objects.  
	    static Connection con = null;  
	    static Statement stmt = null;  
	    static ResultSet rs = null;  
	    static  String Driver="com.microsoft.sqlserver.jdbc.SQLServerDriver";
	    static String Directory="E:\\Java\\APT_Project\\Docs";
	    static Path folder = Paths.get(Directory);
	    static Document doc ;
	    static  String slash="\\"+"\\";
	    static String s;
	   // static String input="D:"+slash+"CufeCourses"+slash+"APT"+slash+"project"+slash;
	    static String filename;
	    
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
	   public static void main(String[] args) {  
            String title;
           
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
				    	//// extract title //
				    	title = doc.title();
				    	System.out.println(i+"-");
				    	s=title;
				    	
				    	String[] words=s.split("\\P{Alpha}+");
							for(String word : words)
							{
								
								// add in dictinary of doc 
								System.out.println(word);
							}
						//// extract h1 //
							  System.out.println("Header 1");
							  
							Elements h1=doc.getElementsByTag("h1");
							Elements elementsObj = doc.select("h1, h2, h3, h4, h5, h6");
							List<String> elem =elementsObj.eachText();
							
							
							
							for(Element e : h1)
								{
								    s=e.text();
								    String[] words1=s.split("[\\W]");
									for(String word : words)
									{
										// add in dictinary of doc 
										System.out.println(word);
									}
								}
							
							
							
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
