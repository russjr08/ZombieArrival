package com.kronosad.api.internet;
import java.net.*;
import java.io.*;

public class ReadURL {
    protected String theURLString;
    public ReadURL(String url){
        theURLString = url;
    }

    public String read() throws Exception {
        StringBuilder sb = new StringBuilder();
        URL theURL = new URL((String)theURLString);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(theURL.openStream()));

        String inputLine;
        while ((inputLine = in.readLine()) != null)
            sb.append(inputLine);
        in.close();
        return sb.toString();
    }

    public void setURL(String newURL){

        theURLString = newURL;

    }

    public String getURL(){
        return theURLString;
    }


}