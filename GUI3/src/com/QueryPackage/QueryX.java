package com.QueryPackage;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

@WebServlet("/QueryX")
public class QueryX extends HttpServlet {

    private String queryInput = "hh", hiddenParam, querytest;
    private ResultSet results;
    private int page, recordsPerPage;
    private ArrayList docsID;
    private ArrayList finalList;
    private HPEngine engine;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        queryInput = request.getParameter("SQuery");
        hiddenParam = request.getParameter("formx");

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
                docsID = new ArrayList<>();
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


                //classname objectname = new classname();
                //docsID = objectname.functionname(queryinput);
                //int noOfRecords = docsID.size();
                int noOfRecords = 35;
                int noOfPages = (int) Math.ceil(noOfRecords * 1.0 / recordsPerPage);

                engine = new HPEngine(docsID);
                finalList = engine.getResultsList((page-1), recordsPerPage, noOfRecords);

                /*
                String [] queryItems = queryInput.split(" ");
                for (int i = 0; i < finalList.size(); i++)
                {
                    results trial = (results)finalList.get(i);
                    for (String a: queryItems){
                        if (trial.getText().contains(a)){
                            int indexsub = trial.getText().indexOf(a);
                            int indexPeriod = 0, indexPeriod2 = 0;
                            while (indexsub-200 > indexPeriod){
                                indexPeriod = trial.getText().indexOf(" ", indexPeriod+1);
                            }
                            while (indexsub-200 >= indexPeriod2){
                                indexPeriod2 = trial.getText().indexOf(" ", indexPeriod2+100);
                            }
                            trial.setText(trial.getText().substring(indexPeriod, indexPeriod2));
                        }
                        break;
                    }
                }
                */

                for (int i = 0; i < 1; i++)
                {
                    System.out.println(finalList.size());
                }

                if(null != request.getParameter("Felix Felicis")) {
                    response.sendRedirect(finalList.get(0).toString());
                    return;
                }

                request.setAttribute("linkInfo", finalList);
                request.setAttribute("noOfPages", noOfPages);
                request.setAttribute("currentPage", page);
                request.setAttribute("queryTest", querytest);
                RequestDispatcher view = request.getRequestDispatcher("main.jsp");
                view.forward(request, response);

            } catch (SQLException e) {
                e.printStackTrace();
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
        if ($endPos != $textLen) {
            $excerpt = substr_replace($excerpt, $ending, -$phraseLen);
        }

        return excerptx;
    }
}
