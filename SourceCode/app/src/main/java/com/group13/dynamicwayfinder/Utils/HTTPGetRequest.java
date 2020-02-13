package com.group13.dynamicwayfinder.Utils;

import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HTTPGetRequest extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... strings) {

        //https://stackoverflow.com/questions/2938502/sending-post-data-in-android
        StringBuilder jsonStringBuilder = new StringBuilder();
        // Making HTTP request
        try {
            URL url = new URL(strings[0]);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            try {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuffer buffer = new StringBuffer();
                String line;

                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                    jsonStringBuilder.append(line);
                    jsonStringBuilder.append("\n");
                }
                //System.out.println(jsonStringBuilder.toString());
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //JSONFile=jsonStringBuilder.toString();
        return jsonStringBuilder.toString();
    }

    // override this method to get a result
    @Override
    protected void onPostExecute(String Result) {



    }

}
