package com.example.android.newsstandv2;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class Utils {


    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = Utils.class.getSimpleName();

    private Utils() {
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                Log.i(LOG_TAG, "test : connection response is " + urlConnection.getResponseMessage());
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the news JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }


    /**
     * Query the Guardian and return a list of {@link News} objects.
     */
    public static List<News> fetchNewsData(String requestUrl) {
        Log.i(LOG_TAG, "test: fetchNewsData() is called...");
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Return the list of {@link news}s
        return extractFeatureFromJson(jsonResponse);
    }

    /**
     * Return a list of {@link News} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<News> extractFeatureFromJson(String newsJSON) {
        // If the JSON string is empty or null, then return early.

        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding newss to
        List<News> news = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(newsJSON);
            JSONObject jsonResults = baseJsonResponse.getJSONObject("response");
            // Extract the JSONArray associated with the key called "results",
            JSONArray newsArray = jsonResults.getJSONArray("results");
            // For each new in the newsArray, create an {@link News} object
            for (int i = 0; i < newsArray.length(); i++) {
                String currentNewsAuthor;
                // Get a single new at position i within the list of news
                JSONObject currentNew = newsArray.getJSONObject(i);

                // Extract the value for the key called "webTitle"
                String currentTitle = currentNew.getString("webTitle");

                // Extract the value for the key called "place"
                String currentCategory = currentNew.getString("sectionId");

                // Extract the Date & Time from the key called webPublicationDate
                String currentDateTime = "";
                if (currentNew.has("webPublicationDate")) {
                    // after extracting the value,  Remove T & Z from the date
                    currentDateTime = currentNew.getString("webPublicationDate").replace("T", " ").replace("Z", "");
                    // Remove also the last 3 characters (seconds) from the time (":09")
                    currentDateTime = currentDateTime.substring(0, currentDateTime.length() - 3);
                }

                // Extract the value for the key called "url"
                String currentUrl = currentNew.getString("webUrl");

                JSONObject currentThumbObj = currentNew.getJSONObject("fields");
                String currentThumb = currentThumbObj.getString("thumbnail");

                JSONArray tagsArray = currentNew.getJSONArray("tags");

                if (tagsArray.length() < 1) {
                    currentNewsAuthor = null;
                } else {
                    StringBuilder authorBuilder = new StringBuilder();
                    for (int y = 0; y < tagsArray.length(); y++) {
                        JSONObject name = tagsArray.getJSONObject(y);
                        authorBuilder.append(name.getString("webTitle")).append(" ");
                    }
                    currentNewsAuthor = authorBuilder.toString();
                }
                news.add(new News(currentThumb, currentTitle, currentNewsAuthor, currentCategory, currentDateTime, currentUrl));
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the news JSON results", e);
        }
        // Return the list of newss
        return news;
    }
}