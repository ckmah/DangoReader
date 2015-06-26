package com.william.mangoreader.parse;

import android.os.AsyncTask;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;

public class JSONParserTask extends AsyncTask<String, Integer, Void> {

    private Gson gson;

    public JSONParserTask(){
        gson = new Gson();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        //TODO: pre-execution tasks
    }

    @Override
    protected Void doInBackground(String... params) {
        //TODO: background task
//        parseJson();
        return null;
    }

//    private void parseJson(String json) {
//        MangaCardItem m = gson.fromJson(json, MangaCardItem.class);
//    }

    @Override
    protected void onPostExecute(Void result) {
        //TODO: post execution tasks
    }

    public static String getJSONFromUrl(URL url) {
        StringWriter json = new StringWriter();
        BufferedReader in;
        try {
            in = new BufferedReader(
                    new InputStreamReader(url.openStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null)
                json.write(inputLine);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

//    public static void main(String[] args){
//        URL url = null;
//        try {
//            url = new URL("https://www.mangaeden.com/api/list/0/?p=0");
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//            System.exit(1);
//        }
//        String json = getJSONFromUrl(url);
//        System.out.print(json);
//    }
}
