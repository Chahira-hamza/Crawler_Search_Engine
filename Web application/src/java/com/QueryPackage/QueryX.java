package com.QueryPackage;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

//import org.json.*;


@WebServlet("/QueryX")
public class QueryX extends HttpServlet {

    private String queryInput = "hh", hiddenParam, querytest;
    private ResultSet results;
    private int page, recordsPerPage, indexsub, lastIndex;
    //private ArrayList docsID;
    private ArrayList finalList;
    private HPEngine engine;
    
    public QueryX(){
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        queryInput = request.getParameter("SQuery");
        hiddenParam = request.getParameter("formx");
        
         /*
        PrintWriter out = response.getWriter();
        response.setContentType("text/html");
        response.setHeader("Cache-control", "no-cache, no-store");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "-1");

        JSONArray arrayObj=new JSONArray();

        String query = request.getParameter("SQuery");
        System.out.println(query);
        query = query.toLowerCase();
        for(int i=0; i<COUNTRIES.length; i++) {
            String country = COUNTRIES[i].toLowerCase();
            if(country.startsWith(query)) {
                arrayObj.add(COUNTRIES[i]);
            }
        }
        */
        

        if(queryInput != null){
            querytest = queryInput;
        }
        else{
            queryInput = querytest;
        }

        if (hiddenParam.equals("form1") && queryInput.isEmpty()) {
            if (null != request.getParameter("Lumos")) {
                response.sendRedirect("index.jsp");
                return;
            } else if (null != request.getParameter("Felix Felicis")) {
                response.sendRedirect("https://www.pottermore.com/");
                return;
            }
        }
        else {
            try {
               
                finalList = new ArrayList<>();

                recordsPerPage = 10;
                if(request.getParameter("page") != null) {
                    page = Integer.parseInt(request.getParameter("page"));
                }
                else if(request.getParameter("next") != null &&request.getParameter("next").equals("Next")){
                    page = page+1;
                }
                else if(request.getParameter("previous") != null && request.getParameter("previous").equals("Previous")){
                    if(page != 1) {
                        page = page - 1;
                    }
                }
                
//                classname objectname = new classname();
//                docsID = objectname.functionname(queryinput);
//                int noOfRecords = docsID.size();

                QueryProcessor ourQProcessor = new QueryProcessor();
                
                System.out.println(queryInput);
                
                ArrayList docsID = ourQProcessor.process(queryInput);
                
                if (docsID == null)
                {
                 System.out.println("BUG NULL");
                }
                
                int noOfRecords = docsID.size();
                int noOfPages = (int) Math.ceil(noOfRecords * 1.0 / recordsPerPage);

                engine = new HPEngine(docsID);
                finalList = engine.getResultsList((page-1), recordsPerPage, noOfRecords);

                if (!finalList.isEmpty())
                {
                results LuckyURL = (results)finalList.get(0);
                
                String [] queryItems = queryInput.split(" ");
                for (int i = 0; i < finalList.size(); i++)
                {
                    results trial = (results)finalList.get(i);
                    trial.setText(trial.getText().substring(0,200));
//                    for (String a: queryItems){
//                        if (trial.getText().contains(a)){
//                            indexsub = trial.getText().indexOf(a);
//                            if(indexsub < 90){
//                                lastIndex =trial.getText().indexOf(" ", indexsub + 200);
//                                String temp = trial.getText().replaceAll(a, "<em>" + a + "</em>");
//                                trial.setText(trial.getText().substring(0, 200));
//                            }
//                            else{
//                                int firstIndex = trial.getText().lastIndexOf(" ", indexsub-90);
//                                if (trial.getText().length() < indexsub + 200) {
//                                    lastIndex = trial.getText().length();
//                                }
//                                else {
//                                    lastIndex =trial.getText().indexOf(" ", indexsub + 200);
//                                }
//                                String temp = trial.getText().replace(a, "<em>" + a + "</em>");
//                                trial.setText(temp.substring(0, 200));
//                            }
//                        }
                       // trial.setText(trial.getText() + " ...");
                    
                }
   
           
               if(null != request.getParameter("Felix Felicis")) {
                    response.sendRedirect(LuckyURL.getURL());
                    return;
                }


                request.setAttribute("linkInfo", finalList);
                request.setAttribute("noOfPages", noOfPages);
                request.setAttribute("currentPage", page);
                request.setAttribute("queryTest", querytest);
                RequestDispatcher view = request.getRequestDispatcher("main.jsp");
                
                view.forward(request, response);
                response.sendRedirect("main.jsp");
                }
                else
                {
                    response.sendRedirect("E404.jsp");
                }
                
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (NullPointerException ex) {
                Logger.getLogger(QueryX.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ArrayIndexOutOfBoundsException ex) {
                Logger.getLogger(QueryX.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(QueryX.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
 

    String excerpt(String Text, String phrase, int radius, String ending) {


        int phraseLen = Text.length();
        if (radius < phraseLen) {
            radius = phraseLen;
        }

        String [] phrases = phrase.split(" ");

        int pos=-2;
        for (String words: phrases){
            pos = Text.indexOf(words);
            if (pos > -1){
                break;
            }
        }

        int startPos = 0;
        if (pos > radius){
            startPos = pos - radius;
        }

        int textLen = Text.length();

        int endPos = pos + phraseLen + radius;
        if (endPos >= textLen){
            endPos = textLen;
        }

        String excerptx = Text.substring(startPos, endPos - startPos);
        if (startPos != 0){
            excerptx = excerptx.replaceAll(excerptx.substring(0, phraseLen), ending);
        }
        /*
        $excerpt = substr($text, $startPos, $endPos - $startPos);
        if ($startPos != 0) {
            $excerpt = substr_replace($excerpt, $ending, 0, $phraseLen);
        }*/

        if (endPos != textLen){
            excerptx = excerptx.replaceAll(excerptx.substring(textLen-phraseLen, phraseLen), ending);
        }
        
        // why does this give an error ?
//        if ($endPos != $textLen) {
//            $excerpt = substr_replace($excerpt, $ending, -$phraseLen);
//        }

        return excerptx;
    }
}
