package com.example.talha.booksearch;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

/**
 * {@link BookAdapter} is an {@link ArrayAdapter} that can provide the layout for each list based
 * on a data source (in this instance the Google Books API), which is a list of {@link Book} objects.
 */
public class BookAdapter extends ArrayAdapter<Book>{

    /**
     * This is our own custom constructor (it doesn't mirror a superclass constructor).
     * The context is used to inflate the layout file, and the list is the data we want
     * to populate into the lists.
     *
     * @param context The current context. Used to inflate the layout file.
     * @param books A List of Book objects to display in a list.
     */
    public BookAdapter(Activity context, List<Book> books) {
        // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
        // The second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter for four TextViews and an ImageView, the adapter is not
        // going to use this second argument, so it can be any value. Here, we used 0.
        super(context, 0, books);
    }

    /**
     * Provides a view for an AdapterView (ListView, GridView, etc.)
     *
     * @param position The position in the list of data that should be displayed in the
     *                 list item view.
     * @param convertView The recycled view to populate.
     * @param parent The parent ViewGroup that is used for inflation.
     * @return The View for the position in the AdapterView.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        // Get the book object located at this position in the list.
        Book currentBook = getItem(position);

        // Find the TextView in the list_item.xml layout with the ID book_title, then set the text
        // of that TextView to be the Title of the current book object.
        TextView titleTextView = (TextView) listItemView.findViewById(R.id.book_title);
        titleTextView.setText(currentBook.getTitle());

        // Find the TextView in the list_item.xml layout with the ID book_subtitle, then set the text
        // of that TextView to be the Subtitle of the current book object. If the object doesn't
        // have a subtitle, then set the view to be gone. When the subtitle view is gone, set the
        // title text view to be match parent so that it takes the whole space.
        TextView subtitleTextView = (TextView) listItemView.findViewById(R.id.book_subtitle);
        String bookSubtitle = currentBook.getSubtitle();
        if (bookSubtitle != null && !bookSubtitle.isEmpty()) {
            subtitleTextView.setText(bookSubtitle);
            subtitleTextView.setVisibility(View.VISIBLE);
            minimiseSpace(titleTextView);

        } else {
            subtitleTextView.setVisibility(View.GONE);
            maximiseSpace(titleTextView);
        }

        // Rating TextView is initialised to maximise or minimise space if there is an author or not
        TextView ratingTextView = (TextView) listItemView.findViewById(R.id.book_rating);

        // Find the TextView in the list_item.xml layout with the ID book_author, then set the text
        // of that TextView to be the Author of the current book object. If the object doesn't have
        // an author, then set the view to be gone.
        TextView authorTextView = (TextView) listItemView.findViewById(R.id.book_author);
        String bookAuthor = currentBook.getAuthor();
        if (bookAuthor != null) {
            authorTextView.setText(bookAuthor);
            authorTextView.setVisibility(View.VISIBLE);
            ratingTextView.setGravity(Gravity.BOTTOM | Gravity.RIGHT);
        } else {
            authorTextView.setVisibility(View.GONE);
            ratingTextView.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
        }

        // Find the TextView in the list_item.xml layout with the ID book_rating, then set the text
        // of that TextView to be the Rating of the current book object. If the object doesn't have
        // a rating, then set the view to be gone. When the rating view is gone, set the author
        // text view to be match parent so that it takes up the whole space, and centre the text.
        Double rating = currentBook.getRating();
        if (rating != null) {
            ratingTextView.setText(formatRating(rating) + " ★");
            ratingTextView.setVisibility(View.VISIBLE);
            minimiseSpace(authorTextView);
            authorTextView.setGravity(Gravity.TOP | Gravity.RIGHT);
        } else {
            ratingTextView.setVisibility(View.GONE);
            maximiseSpace(authorTextView);
            authorTextView.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
        }

        // Find the ImageView in the list_item.xml layout with the ID image_view. Create a String
        // that holds the imageUrl of the book. If the object doesn't have an image, set the view
        // to be gone.
        ImageView imageView = (ImageView) listItemView.findViewById(R.id.image_view);
        String imageUrl = currentBook.getImageUrl();
        if (imageUrl != null) {
            // Using Picasso, download the Image from the internet. Picasso will automatically handle
            // view recycling and will not re-download the image if it has already been downloaded.
            Picasso.with(getContext()).load(imageUrl).into(imageView);
            imageView.setVisibility(View.VISIBLE);
        } else {
            imageView.setVisibility(View.GONE);
        }

        // Return the whole list item layout (containing 4 TextViews and a ImageView)
        // so that it can be shown in the ListView
        return listItemView;

    }

    /**
     * Sets up a view to take the full space available to it by setting it's layout_width and
     * layout_height properties to MATCH_PARENT.
     */
    private void maximiseSpace(TextView view) {
        view.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
    }

    /**
     * Sets up a view to take the least space as possible while remaining legible by setting it's
     * layout_width to MATCH_PARENT and layout_height to WRAP_CONTENT.
     */
    private void minimiseSpace(TextView view) {
        view.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
    }

    /**
     * Formats the rating to 2 decimal places.
     */
    private String formatRating(Double rating) {

        // Create an instance of the DecimalFormat class to format our doubles to 2 decimal places
        // (e.g. from 2.76 to 2.8)
        DecimalFormat formatter = new DecimalFormat("0.0");

        // Format and return the rating from the current Book according to the formatter
        return formatter.format(rating);
    }
}
