package QueryPackage;

import java.io.IOException;
import java.sql.*;

public class HPEngine {
    String URL = ""; //URL of database
    String USERNAME = "";
    String PASSWORD = "";

    Connection connection = null;
    PreparedStatement query = null;
    ResultSet resultSet = null;

    public HPEngine(String queryInput) throws SQLException{

        try{
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);

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

    public ResultSet getresult() throws SQLException{

        try{
            resultSet = query.executeQuery();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return resultSet;
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
