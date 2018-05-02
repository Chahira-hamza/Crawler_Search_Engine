package com.QueryPackage;

import static com.QueryPackage.QueryProcessor.Driver;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import org.jsoup.nodes.Document;

public class HPEngine {
    private String URL = ""; //URL of database
    private String USERNAME = "sa";
    private String PASSWORD = "gam3astuff*";

    static String Driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    private Connection connection = null;
    private PreparedStatement query = null;
    private ResultSet resultSet = null;
    private ArrayList resultsList;
    private ArrayList pagelist;
    Document doc;
    private static String connectionUrl = "jdbc:sqlserver://localhost:1433;"
            + "databaseName=search_engine;user=sa;password=gam3astuff*"; // Declare the JDBC

    public HPEngine(ArrayList docsID) throws SQLException, NullPointerException, ArrayIndexOutOfBoundsException, IOException, ClassNotFoundException{
   
    try
    {
            if (docsID != null) {
            
            resultsList = new ArrayList<>();
            pagelist = new ArrayList<>();
            
            Class.forName(Driver);
            connection = DriverManager.getConnection(connectionUrl);
            connection.setAutoCommit(false);
            for (int i = 0; i < docsID.size(); i++) {
               
//                String SearchQ = "SELECT Docs_URL.Title, Docs_URL.URL, DocText.Dtext "
//                        + "FROM Docs_URL "
//                        + "INNER JOIN DocText "
//                        + "ON Docs_URL.ID = DocText.Doc_ID "
//                        + "WHERE Docs_URL.ID = " + docsID.get(i);
                
                PreparedStatement stp;     
                PreparedStatement stp2;
                
                String SearchQ1 = "SELECT Title, URL FROM Docs_URL WHERE ID = ?";
                String SearchQ2 = "Select Dtext from DocText where Doc_ID = ?";
                
                stp =  connection.prepareStatement(SearchQ1);
                stp2 = connection.prepareStatement(SearchQ2);
                
                stp.setInt(1,(int) docsID.get(i));
                stp2.setInt(1, (int) docsID.get(i));
                
                //System.out.println(docsID.get(i));
                
                resultSet = stp.executeQuery();
                ResultSet result2 = stp2.executeQuery();
                
                connection.commit();
               
                if (resultSet.next() && result2.next())
                {
                   // System.out.println(resultSet.getString("URL"));
                    
                resultsList.add(new results(resultSet.getString("URL"), resultSet.getString("Title")
                , result2.getString("Dtext")));
                
            }
            
        }
            
        connection.setAutoCommit(true);
        }
       
    }
    catch(Exception e)
    {
        e.printStackTrace();
    }
    }

    /*
    public ResultSet getresult() throws SQLException{

        try{
            resultSet = query.executeQuery();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return resultSet;
    }
    */

    public ArrayList getResultsList(int page, int records, int total){

        pagelist.clear();
        for (int i = 0; i < records; i++){
            if(i + page*records >= total){
                return pagelist;
            }
            pagelist.add(resultsList.get(i + page*records));
        }
        return pagelist;
    }

    /*public ResultSet executeQ(String queryInput) throws SQLException{

        try{
            if (queryInput.startsWith("\"") && queryInput.endsWith("\""))
            {
                String searchQ = "SELECT " + queryInput + "FROM "; //Search query
                query = connection.prepareStatement(searchQ); // actual select search statement needed
            }
            else {
                String[] words=queryInput.split("\\s");

                for(String w:words) {

                    String searchQ = "SELECT " + w + "FROM "; //Search query
                    query = connection.prepareStatement(searchQ); // actual select search statement needed
                }
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }

    }
    */
}
