package com.example.talha.booksearch;

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

/**
 * Helper methods related to requesting and receiving book data from Google Books.
 */
public final class Utils {

    /** Tag for the log messages */
    private static final String LOG_TAG = Utils.class.getSimpleName();
    /**
     * Create a private constructor because no one should ever create a {@link Utils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name Utils (and an object instance of Utils is not needed).
     */
    private Utils() {
    }

    /**
     * Query the Google Books API and return a list of {@link Book} objects.
     * @param requestUrl The URL used to query the API
     */
    public static List<Book> fetchBookData(String requestUrl) {

        //Create URL object
        URL url = createUrl(requestUrl);

        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request", e);
        }

        // Extract relevant fields from JSON and return them as Book objects.
        return parseJsonResponse(jsonResponse);

    }

    /**
     * Creates a URL object from a given URL.
     * @param stringUrl is the String to be converted to a url.
     * @return the url object.
     */
    private static URL createUrl(String stringUrl) {

        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
        Log.e(LOG_TAG, "Problem building URL", e);
        }
        return url;

    }

    /**
     * Make a HTTP request to the given URL and return a String
     * @param url URL to make HTTP request to
     * @return String that contains the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try{
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(15000);
            urlConnection.setReadTimeout(10000);
            urlConnection.connect();

            // If the request was successful (Response Code 200), then read the input stream and
            // parse the response.
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error: Response code not 200, Code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving results.", e);
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
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            while (line != null) {
                output.append(line);
                line = bufferedReader.readLine();
            }
        }

        return output.toString();

    }

    private static List<Book> parseJsonResponse(String jsonResponse) {

        // We set up our for loop counter here, and set it to -1. If the app crashes at this
        // stage, we write to the logs. If we see this -1, that means the app crashed while
        // extracting the JSONArray. If it's not -1, then it crashed at that position in the
        // JSONArray while retrieving book information.
        int i = -1;

        // Create an empty ArrayList that we can start adding books to
        List<Book> books = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(jsonResponse);

            // Extract the JSONArray associated with the key called "items",
            // which represents a list of items (or books).
            JSONArray items = baseJsonResponse.getJSONArray("items");

            // For each book in the items array, create a book object.
            for (i = 0; i < items.length(); i++) {

                // Set the book's subtitle, description, author, rating, currency and price to null
                // in case the book doesn't have either.
                String bookSubtitle = null;
                String bookDescription = null;
                String bookAuthor = null;
                Double bookRating = null;
                String imageUrl = null;
                String currency = null;
                Double bookPrice = null;

                // Get a single book at position i within the array of books.
                JSONObject currentBook = items.getJSONObject(i);

                // For that book, extract the JSONObject with the key called "volumeInfo", which is
                // all the detailed information about the book.
                JSONObject bookInfo = currentBook.getJSONObject("volumeInfo");

                // Extract the value for the key called "title".
                String bookTitle = bookInfo.getString("title");

                // If the book has a subtitle, extract it.
                if (bookInfo.has("subtitle")) {
                    bookSubtitle = bookInfo.getString("subtitle");
                }

                // If the book has a description, extract it.
                if (bookInfo.has("description")) {
                    bookDescription = bookInfo.getString("description");
                }

                // If the book has an Author, extract it.
                if (bookInfo.has("authors")) {
                    JSONArray bookAuthors = bookInfo.getJSONArray("authors");
                        bookAuthor = bookAuthors.getString(0);
                }

                // If the book has a rating, extract it.
                if (bookInfo.has("averageRating")) {
                    bookRating = bookInfo.getDouble("averageRating");
                }

                // For that book, extract the JSONObject with the key called "imageLinks", which
                // contain the low and high resolution images of the book, if the book has it.
                if (bookInfo.has("imageLinks")) {
                    JSONObject imageLinks = bookInfo.getJSONObject("imageLinks");

                    // Extract the value for the key called "smallThumbnail".
                    imageUrl = imageLinks.getString("smallThumbnail");
                }

                // Extract the value for the key called "infoLink".
                String bookUrl = bookInfo.getString("infoLink");

                // Extract the value for the key called "previewLink".
                String previewUrl = bookInfo.getString("previewLink");

                // For that book, extract the JSONObject with the key called "saleInfo", which is
                // all the price and sale information about the book.
                JSONObject saleInfo = currentBook.getJSONObject("saleInfo");

                // If the book has a price, extract it and the currency it's in.
                if (saleInfo.has("listPrice")) {
                    JSONObject listPrice = saleInfo.getJSONObject("listPrice");
                    bookPrice = listPrice.getDouble("amount");
                    currency = listPrice.getString("currencyCode");
                }

                // Add the book to the ArrayList of books.
                books.add(new Book(bookTitle, bookSubtitle, bookDescription, bookAuthor,
                        bookRating, bookUrl, previewUrl, imageUrl, currency, bookPrice));
            }

        } catch (JSONException e) {
            // If an error is thrown while parsing JSON, catch the exception, and print a log message
            // along with the stack trace.
            Log.e(LOG_TAG, "Error parsing JSON at object " + i, e);
        }

        // Return the list of books.
        return books;

    }

}
