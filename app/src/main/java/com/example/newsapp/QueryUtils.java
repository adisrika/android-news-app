package com.example.newsapp;

import android.util.Log;

import com.example.newsapp.NewsItem;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Helper methods related to requesting and receiving news data from Guardian.
 */
public final class QueryUtils {

    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    public static List<NewsItem> fetchNewsItemData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, MainActivity.getAppContext().getString(R.string.error_http_failed), e);
        }

        // Extract relevant fields from the JSON response and create an {@link NewsItem} object
        return extractFeatureFromJson(jsonResponse);
    }

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Return a list of {@link NewsItem} objects that has been built up from
     * parsing a JSON response.
     */
    private static ArrayList<NewsItem> extractFeatureFromJson(String jsonData) {

        // Create an empty ArrayList that we can start adding news items to
        ArrayList<NewsItem> newsItems = new ArrayList<>();

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        if (jsonData != null && !jsonData.isEmpty()) {
            try {

                // TODO: Parse the response given by the jsonData string and
                // build up a list of News Item objects with the corresponding data.
                JSONObject baseJson = new JSONObject(jsonData);
                JSONObject baseJsonResponse = baseJson.getJSONObject("response");
                JSONArray newsItemArray = baseJsonResponse.getJSONArray("results");
                for (int i = 0; i < newsItemArray.length(); i++) {
                    JSONObject currentNewsItem = newsItemArray.getJSONObject(i);
                    newsItems.add(new NewsItem(
                            currentNewsItem.optString("webTitle"),
                            getContributor(currentNewsItem.optJSONArray("tags")),
                            currentNewsItem.optString("sectionName"),
                            getDate(currentNewsItem.optString("webPublicationDate")),
                            currentNewsItem.optString("webUrl")
                    ));
                }

            } catch (JSONException e) {
                // If an error is thrown when executing any of the above statements in the "try" block,
                // catch the exception here, so the app doesn't crash. Print a log message
                // with the message from the exception.
                Log.e("QueryUtils", MainActivity.getAppContext().getString(R.string.problem_parsing), e);
            }
        }

        // Return the list of earthquakes
        return newsItems;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, MainActivity.getAppContext().getString(R.string.error_creating_url), e);
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
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, MainActivity.getAppContext().getString(R.string.error_response_code) + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, MainActivity.getAppContext().getString(R.string.error_retrieving_news_items), e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
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
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName(MainActivity.getAppContext().getString(R.string.encoding)));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static Date getDate(String dateString) {
        Date date = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(MainActivity.getAppContext().getString(R.string.date_format));
        try {
            date = simpleDateFormat.parse(dateString);
        } catch (Exception e) {
            Log.e(LOG_TAG, MainActivity.getAppContext().getString(R.string.error_parse_date), e);
        }
        return date;
    }

    private static String getContributor(JSONArray tags) {
        if (tags.length() > 0) {
            try {
                return tags.getJSONObject(0).optString("webTitle");
            } catch (Exception e) {
                Log.e(LOG_TAG, MainActivity.getAppContext().getString(R.string.error_parse_tags), e);
            }
        }
        return "";
    }

}