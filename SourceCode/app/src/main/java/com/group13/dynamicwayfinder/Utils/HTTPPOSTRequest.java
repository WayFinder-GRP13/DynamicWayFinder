package com.group13.dynamicwayfinder.Utils;

import android.os.AsyncTask;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class HTTPPOSTRequest extends AsyncTask<Void, Void, String> {
    public ServerWrapperClass serverWrapperClass;
    public ServerWrapperDistanceClass serverWrapperDistanceClass;

    public HTTPPOSTRequest(ServerWrapperClass serverWrapperClass) {
        this.serverWrapperClass = serverWrapperClass;
    }
    public HTTPPOSTRequest(ServerWrapperDistanceClass serverWrapperDistanceClass) {
        this.serverWrapperDistanceClass = serverWrapperDistanceClass;
    }

    @Override
    protected String doInBackground(Void... string) {

        //https://stackoverflow.com/questions/2938502/sending-post-data-in-android
        StringBuilder jsonStringBuilder = new StringBuilder();
        // Making HTTP request
        if (serverWrapperClass == null) {

            try {

                System.out.println("url is: " + serverWrapperDistanceClass.getHttpRequest());
                URL url = new URL(serverWrapperDistanceClass.getHttpRequest());
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json; utf-8");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setDoOutput(true);
                ObjectMapper mapper = new ObjectMapper();
                //Converting the Object to JSONString
                System.out.println(serverWrapperDistanceClass.getlocationPoint());
                String jsonInputString = mapper.writeValueAsString(serverWrapperDistanceClass.getlocationPoint());
                System.out.println("JSON INPUT: " + jsonInputString);


                try (OutputStream os = urlConnection.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes("utf-8");
                    os.write(input, 0, input.length);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(urlConnection.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine = null;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    System.out.println(response.toString());
                    return response.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //JSONFile=jsonStringBuilder.toString();
                return jsonStringBuilder.toString();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (JsonGenerationException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (JsonMappingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            try {
                System.out.println("url is: " + serverWrapperClass.getHttpRequest());
                URL url = new URL(serverWrapperClass.getHttpRequest());
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json; utf-8");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setDoOutput(true);
                ObjectMapper mapper = new ObjectMapper();
                //Converting the Object to JSONString
                System.out.println(serverWrapperClass.getRestAPIRequestInformation());
                String jsonInputString = mapper.writeValueAsString(serverWrapperClass.getRestAPIRequestInformation());
                System.out.println("JSON INPUT: " + jsonInputString);


                try (OutputStream os = urlConnection.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes("utf-8");
                    os.write(input, 0, input.length);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(urlConnection.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine = null;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    System.out.println(response.toString());
                    return response.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //JSONFile=jsonStringBuilder.toString();
                return jsonStringBuilder.toString();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (JsonGenerationException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (JsonMappingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }
        return null;
    }





    // override this method to get a result
    @Override
    protected void onPostExecute(String Result) {
    }

}
