package com.QueryPackage;

public class results {

    String URL = null, Title = null, Text = null;

    public results(String URL, String title, String text){
        this.URL = URL;
        Title = title;
        Text = text;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        Text = text;
    }
}
