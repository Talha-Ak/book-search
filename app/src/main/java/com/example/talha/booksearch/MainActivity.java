package com.example.talha.booksearch;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * NOTICE: This application was built prior to the introduction of RecyclerView, ViewModel,
 * Android Jetpack etc. Some components (like {@link BookAdapter} and {@link BookLoader}) are outdated.
 */
public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<List<Book>> {

    /** Tag for log messages */
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    /**
     * Constant value for the book loader ID. We can choose any integer.
     * This really only comes into play if we're using multiple loaders.
     */
    private static final int BOOK_LOADER_ID = 1;

    private static final String PREVIOUS_SEARCH_KEY = "previousSearchQuery";

    /** Adapter for list of books */
    private BookAdapter mAdapter;

    /** URL for book data from the Google Books API */
    private static final String GOOGLE_BOOKS_URL = "https://www.googleapis.com/books/v1/volumes";

    /** Variables to check network status and to get Loader Manager */
    private ConnectivityManager connMgr;
    private NetworkInfo networkInfo;

    /** Views that will hold references to the views in our xml layout */
    private EditText mEditText;
    private TextView mEmptyTextView;
    private View mProgressBar;

    /** String that holds the query the user previously searched for */
    private String mPreviousSearchQuery = "";

    /** Boolean which is used to tell if the user just accessed the settings menu */
    private boolean justAccessedSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get references to the views in the layout
        mEditText = (EditText) findViewById(R.id.query_input);
        mProgressBar = findViewById(R.id.loading_indicator);

        // Hide the progress bar so it doesn't show until a search begins.
        mProgressBar.setVisibility(View.GONE);

        // Find the ListView in the layout.
        ListView listView = (ListView) findViewById(R.id.list);

        // Create a new adapter that takes an empty list of books as input
        mAdapter = new BookAdapter(this, new ArrayList<Book>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        listView.setAdapter(mAdapter);

        // Find the emptyView in the layout and set it on the listView.
        mEmptyTextView = (TextView) findViewById(R.id.empty_view);
        listView.setEmptyView(mEmptyTextView);

        // Set the text of the empty layout to prompt the user to search.
        mEmptyTextView.setText(R.string.Search_for_a_book);

        // Retrieve our previous state is there is any
        retrievePreviousState(savedInstanceState);

        // Set up a clickListener on the listView to open up the DetailActivity if the user clicks
        // on a list item.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // Find the current book that was clicked on
                Book currentBook = mAdapter.getItem(position);

                // Create an intent to the DetailActivity, and send the currentBook to the Activity
                // as well.
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("currentBook", currentBook);
                startActivity(intent);
            }
        });

        // Get a reference to the ConnectivityManager to check state of network connectivity
        connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // Get details on the currently active default data network
                    networkInfo = connMgr.getActiveNetworkInfo();

                    // Test if the user has a network connection, otherwise give the user an error message
                    // informing them.
                    if (networkInfo != null && networkInfo.isConnected()) {
                        // Test if the user is not searching the same thing as before. If they are,
                        // do not restart the loader as it is a waste of system resources. Also test
                        // if they just accessed the settings menu. If so, allow the search to take place,
                        // even if they did search the same thing before because we want to update the
                        // list according to the user's latest settings.
                        if (!mPreviousSearchQuery.equals(mEditText.getText().toString().trim()) || justAccessedSettings) {
                            // Set the previous search query to be the current search, so that it can be
                            // tested again for the next search.
                            mPreviousSearchQuery = mEditText.getText().toString().trim();
                            justAccessedSettings = false;
                            // Restarts/Initialises the loader. Pass in the int ID constant defined
                            // above and pass in null for the bundle. Pass in this activity for the
                            // LoaderCallbacks parameter (which is valid because this activity
                            // implements the LoaderCallbacks interface).
                            getLoaderManager().restartLoader(BOOK_LOADER_ID, null, MainActivity.this);
                        }
                    } else {
                        // Update empty state with no connection error message.
                        mEmptyTextView.setText(R.string.no_internet);
                        mPreviousSearchQuery = "";
                    }
                    return true;
                } else {
                    return false;
                }
            }
        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Save the previous search query, so when the app is restored it can be used to check against
        // the next search.
        outState.putString(PREVIOUS_SEARCH_KEY, mPreviousSearchQuery);
        super.onSaveInstanceState(outState);
    }

    private void retrievePreviousState(Bundle savedInstanceState) {
        // Check if our loader has been initialised already. If so, then than means there was an
        // orientation change. Therefore, call initLoader so the current list of books is restored.
        if (getLoaderManager().getLoader(BOOK_LOADER_ID) != null) {
            getLoaderManager().initLoader(BOOK_LOADER_ID, null, this);
        }

        // Check whether we're recreating a previously destroyed instance
        if (savedInstanceState != null) {
            // Restore value of mPreviousSearchQuery
            mPreviousSearchQuery = savedInstanceState.getString(PREVIOUS_SEARCH_KEY);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            justAccessedSettings = true;
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<List<Book>> onCreateLoader(int id, Bundle args) {

        // Set Empty TextView to an empty string, so it doesn't show while loading results.
        mEmptyTextView.setText("");

        // Clear the adapter of previous book data
        mAdapter.clear();

        // Set the progress bar to be visible, so the user knows we're handling their request.
        mProgressBar.setVisibility(View.VISIBLE);

        // Retrieve User Preferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        int maxResults = sharedPreferences.getInt(
                getString(R.string.settings_max_results_key),
                Integer.parseInt(getString(R.string.settings_max_results_default)));

        String orderBy = sharedPreferences.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));

        String printType = sharedPreferences.getString(
                getString(R.string.settings_print_type_key),
                getString(R.string.settings_print_type_default));

        // Parse the base URL and prepare it for query parameters to be added.
        Uri baseUri = Uri.parse(GOOGLE_BOOKS_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        // Add query parameters according to user actions.
        uriBuilder.appendQueryParameter("q", mEditText.getText().toString().trim());
        uriBuilder.appendQueryParameter("maxResults", String.valueOf(maxResults));
        uriBuilder.appendQueryParameter("orderBy", orderBy);
        uriBuilder.appendQueryParameter("printType", printType);
        uriBuilder.appendQueryParameter("prettyPrint", "false");

        return new BookLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> books) {

        // Hide the progress bar
        mProgressBar.setVisibility(View.GONE);

        // Set the empty text view to inform the user that no books were found. This will only be
        // displayed if there are no books to display.
        mEmptyTextView.setText(R.string.no_books_found);

        // If there is a valid list of {@link Book}s, then add them to the adapter's
        // data set. This will trigger the ListView to update. If there is not valid list, then do
        // nothing. The empty text view will display and inform the user.
        if (mAdapter.isEmpty()) {
            if (books != null && !books.isEmpty()) {
                mAdapter.addAll(books);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }

}