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


public class Indexer implements Runnable {
// Create a variable for the connection string.  
     private static String connectionUrl =
             "jdbc:sqlserver://localhost:1433;databaseName=search_engine;user=sa;password=gam3astuff*;";

 static Connection con = null;  
//	    static Statement stmt = null;  
//	    static ResultSet rs = null;
 static String Doc_Text;
 static int Doc_ID;
 static  String Driver="com.microsoft.sqlserver.jdbc.SQLServerDriver";
 static String Directory="html_docs/";
 static Path folder = Paths.get(Directory);
 static String filename;
 static Document doc ;
 static StopWords stopwords = null;
 static  ValueDict RankDict = null;
 static Element others;
 static  String dbName="Indexer";
 static 	Stemmer stemmer=new Stemmer();										//For stemming the words
 static String root;
 static int position ;
 static HashMap <String , Word> WordDict=new HashMap<String, Word>();      // words and its informATION
 static  ArrayList<Integer> Doc_list=new ArrayList<Integer>();
 static  String Edited_Text;
 static ArrayList<String> aList=null;
 static Object lock;

 public Indexer(Connection connection, Object objectlock) {

     con = connection;
     lock = objectlock;
     
     System.out.println("Indexer created");
 }




public static void InsertWords(HashMap <String , Word> W , int DocID) throws SQLException {

    PreparedStatement WordStmt = null;
    PreparedStatement PostionsStmt=null;

    String Insert_Words = "INSERT INTO Words (Doc_ID,Word_ID,word,Rankw)  VALUES  (?,?,?,?)";
    String Insert_Postions="INSERT INTO WordPostions (Doc_ID, Word_ID,Pos) VALUES (?,?,?)";
    int i=1;
    try {
            con.setAutoCommit(false);

            WordStmt = con.prepareStatement(Insert_Words);
            PostionsStmt=con.prepareStatement(Insert_Postions);
            for (Map.Entry<String, Word> e : W.entrySet()) {
                    WordStmt.setLong(1, DocID);
                    WordStmt.setLong(2, i);
                    WordStmt.setString(3, e.getKey());
                    WordStmt.setLong(4, e.getValue().Rank.longValue());
                    WordStmt.executeUpdate();

                    PostionsStmt.setLong(1, Doc_ID);
                    PostionsStmt.setLong(2, i);

                    for(int in=0; in< e.getValue().Postions_list.size();in++)
                    {
                            PostionsStmt.setLong(3,(long) e.getValue().Postions_list.get(in) );
                            PostionsStmt.executeUpdate();
                    }

                    con.commit();
                    i++;
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

public static void  UpdateWords(HashMap <String , Word> W , int DocID)throws SQLException
{

        PreparedStatement Delete_WordStmt = null;

        String DeleteWords="Delete from Words where Doc_ID = (?)";
        try{
                con.setAutoCommit(false);
                Delete_WordStmt = con.prepareStatement( DeleteWords);
                Delete_WordStmt.setLong(1, DocID);
                Delete_WordStmt.executeUpdate();
                con.commit();

                InsertWords( W , DocID);

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
                if (Delete_WordStmt != null) {
                        Delete_WordStmt.close();
                }

                con.setAutoCommit(true);
        }

}

public static void UpdateText(String Text , int Doc_ID) throws SQLException
{
        PreparedStatement UpdateTextStmt = null;
        String Update_Text = "Update DocText SET Dtext = ? where Doc_ID=?;";

        try {
                con.setAutoCommit(false);
                UpdateTextStmt = con.prepareStatement(Update_Text);
                UpdateTextStmt.setString(1, Text);
                UpdateTextStmt.setLong(2, Doc_ID);
                UpdateTextStmt.executeUpdate();
                con.commit();

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
                if (UpdateTextStmt != null)
                {
                        UpdateTextStmt.close();
                }

                con.setAutoCommit(true);
        }

}

public static void Edit_Text(String Text)
{
        // System.out.println(Text);
        String[] words = Text.split("\\P{Alpha}+");

        aList = new ArrayList<String>(Arrays.asList(words));
        Iterator<String> it = aList.iterator();
        
while (it.hasNext()) {
    String st = it.next();
    if (stopwords.h.contains(st)) 
    {
        it.remove();
    }
}
//System.out.println("___________________");
int i=0;
//    		for (String element : aList) 
//        		{  
//        			
//   					System.out.println(element+" -> index: "+i);
//   					i++;
//   			}

}

public static void GetKeyWords(Document doc)  {

String keywords = doc.select("meta[name=keywords]").attr("content").toLowerCase();
if (keywords != null) {
       // System.out.println("keywords= " + keywords);
        String[] words = keywords.split("\\P{Alpha}+");
        String w;

        for (String word : words) 
        {
                root=stemmer.stem(word);
                if (!(stopwords.h.contains(root)))
                {	
                        root=stemmer.stem(word);
                        // System.out.println(root);
                        if (!WordDict.containsKey(root))
                        {
                                WordDict.put(root, new Word(RankDict.VDict.get("keyword"),-1));
                        }

                        else
                        {
                                //WDict.replace(root, WDict.get(root) + RankDict.VDict.get("keyword"));
                                //WordDict.get(root).AddPostion(-1);
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
       // System.out.println("title: " + title + "_______");
        s = title.toLowerCase();

        String[] words = s.split("\\P{Alpha}+");

        for (String word : words) {

                if (!(stopwords.h.contains(word))) {
                        index = aList.indexOf(word); 			// first get the index of the word
                        root = stemmer.stem(word);
                        //System.out.println(root);

                        if (!WordDict.containsKey(root))		 // check exists or not
                        {
                                WordDict.put(root, new Word(ValueDict.VDict.get("title"), index));

                        }

                        else {
                                WordDict.get(root).AddPostion(index);
                                WordDict.get(root).AddRank(RankDict.VDict.get("title"));

                        }

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
              //  System.out.println("h"+n+": "+header);
                for (Element e : h) {
                        s = e.text().toLowerCase();
                        String[] words1 = s.split("\\P{Alpha}+");
                        for (String word : words1) {
                                //w = word.toLowerCase(); // add in dictinary of doc

                                if (!(stopwords.h.contains(word))) {
                                        index = aList.indexOf(word);
                                        root = stemmer.stem(word);
                                       // System.out.println(root);
                                        if (!WordDict.containsKey(root)) {
                                                WordDict.put(root, new Word(RankDict.VDict.get("h" + n), index));
                                                // WDict.put(root, RankDict.VDict.get("h" + n));
                                        }

                                        else {

                                                WordDict.get(root).AddPostion(index);
                                                WordDict.get(root).AddRank(RankDict.VDict.get("h" + n));
                                        }

                                        aList.set(index, null);
                                }
                                // System.out.println(word);
                        }

                }
                m = doc.select("h" + n).remove();             // remove headers from the doc 
                String hh = m.text();
             //   System.out.println("removed header : " + hh );

        }

}

public static void GetText(Document doc) {
            int index;

            String text = doc.text().toLowerCase();
            String[] words1 = text.split("\\P{Alpha}+");
//            System.out.println(text);
//            System.out.println("_________________________");
            for (String w : words1) {

                    if (!(stopwords.h.contains(w))) {
                            root = stemmer.stem(w);
                        //    System.out.println(root);
                            index = aList.indexOf(w);
                            if (!WordDict.containsKey(root)) {

                                    WordDict.put(root, new Word(RankDict.VDict.get("p"), index));
                            }

                            else {

                                    WordDict.get(root).AddPostion(index);
                                    WordDict.get(root).AddRank(RankDict.VDict.get("p"));
                            }

                            aList.set(index, null);
                    }

            }

    }

public void run()
{

    try 
    {  
        DirectoryStream<Path> stream;
            synchronized(lock)
            {
                lock.wait();
                stream = Files.newDirectoryStream(folder);
            }
            int i=1;
             int FlagUpdate=0;
            for (Path entry : stream) 
            {
                    // Process the entry
                    filename=entry.getFileName().toString();
                    File input = new File(Directory+filename);
                    doc= Jsoup.parse(input,"utf-8");

                    String f=filename.replaceAll("\\D+","");  								 // regex delete non digits
                    Doc_ID=Integer.parseInt(f, 2);
                    if (! Doc_list.contains(Doc_ID))
                    {
                            Doc_list.add(Doc_ID);
                             FlagUpdate=0;

                    }
                    else 
                    {
                             FlagUpdate=1;
                    }

                    //FlagUpdate=1;
//                    System.out.println("Doument ID : "+Doc_ID);
//                    System.out.println("loop #"+i+"-");

                    String CompleteText = doc.text();      				/// the orginal  text
                    Edit_Text(CompleteText.toLowerCase());


                   // System.out.println("_________________________________________");



                    GetTitle(doc);				//// extract words in title ///	
                    GetHeaders(doc);			//// extract words in headers ///
                    GetText(doc);				// extract words in text //
                    GetKeyWords(doc);			// extract words in keywords //

//                    System.out.println("__________________________");	
//
//
//
//
//
//                    System.out.println("sucess text ?????????????????????????????????????");


                    if( FlagUpdate==0)
                    {
                            InsertWords(WordDict,Doc_ID);
                            InsertText(CompleteText,Doc_ID);
                    }
                    else 
                    {
                             UpdateWords(WordDict,Doc_ID);
                             UpdateText(CompleteText,Doc_ID);

                    }



                    for (Map.Entry<String, Word> entry1 : WordDict.entrySet()) 			//to show the data of Word Dictionary
                    {
                          //  System.out.print(entry1.getKey()+"\t");
                            Word w1=entry1.getValue();
                            w1.Print_PosAndRank();


                    }

                    WordDict.clear();												// clear the words Dictionary 
                    boolean sucess= input.delete();									// deleting the file 
                    if(sucess)
                    {
                            System.out.println("Deleted :"+i);

                    }
                    i++;

            }




            stream.close();
            Doc_list.clear();         				// clearing the list of documents	


    }  




    // Handle any errors that may have occurred.  
    catch (Exception e)
    {  


            e.printStackTrace(); 


    }  


    finally 
{  
//        if (rs != null) try { rs.close(); } catch(Exception e) {}  
//        if (stmt != null) try { stmt.close(); } catch(Exception e) {}  
        if (con != null) try { con.close(); } catch(Exception e) {}  
    }  
}  

}
