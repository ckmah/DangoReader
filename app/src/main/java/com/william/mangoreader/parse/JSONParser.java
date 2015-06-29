package com.william.mangoreader.parse;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * Created by clarence on 6/24/15.
 */
public class JSONParser extends AsyncTask<String, Integer, Void> {

    @Override
    protected void onProgressUpdate(Integer... values) {
        //TODO: pre-execution tasks
    }

    @Override
    protected Void doInBackground(String... params) {
        //TODO: background task
        JSONObject jobj = getJSONFromUrl("");
        parseJson(jobj);
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        //TODO: post execution tasks
    }

    public JSONObject getJSONFromUrl(String url) {
        InputStream is = null;
        JSONObject jObj = null;
        String json = null;

        // Making HTTP request
        try {
            // defaultHttpClient
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);

            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();

            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            jObj = new JSONObject(json);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }

        // return JSON String
        return jObj;
    }

    public void parseJson(JSONObject json) {
//        try {
//            //TODO: generate list of "manga objects" to be created on scroll
//            // parsing json object
//        }
//        catch (JSONException e) {
//            }
    }
}
