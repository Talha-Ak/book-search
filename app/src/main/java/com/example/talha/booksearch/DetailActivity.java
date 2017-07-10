package com.example.talha.booksearch;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;

public class DetailActivity extends AppCompatActivity {

    private Book currentBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Get the intent and retrieve the Book object from it.
        Intent intent = getIntent();
        currentBook = intent.getParcelableExtra("currentBook");

        // Get references to the views in the layout.
        ImageView bookCoverView = (ImageView) findViewById(R.id.detail_image_view);
        TextView bookTitleView = (TextView) findViewById(R.id.detail_book_title);
        TextView bookSubtitleView = (TextView) findViewById(R.id.detail_book_subtitle);
        TextView bookAuthorView = (TextView) findViewById(R.id.detail_book_author);
        TextView bookRatingView = (TextView) findViewById(R.id.detail_book_rating);
        TextView bookPriceView = (TextView) findViewById(R.id.detail_book_price);
        TextView bookDescriptionView = (TextView) findViewById(R.id.detail_book_description);
        Button viewOnGoogle = (Button) findViewById(R.id.view_on_google);
        Button previewBook = (Button) findViewById(R.id.preview_book);

        // Retrieve the book cover and display it, using Picasso. Picasso will automatically check
        // if the image has been downloaded before, and if it has, will display that instead of
        // re-downloading it.
        String imageUrl = currentBook.getImageUrl();
        if (imageUrl != null) {
            Picasso.with(this).load(imageUrl).into(bookCoverView);
        } else {
            bookCoverView.setVisibility(View.GONE);
        }

        // Set title text according to current book.
        bookTitleView.setText(currentBook.getTitle());

        // Set subtitle text according to current book if it has one. If not, set visibility to GONE.
        String bookSubtitle = currentBook.getSubtitle();
        if (bookSubtitle != null && !bookSubtitle.isEmpty()) {
            bookSubtitleView.setText(bookSubtitle);
        } else {
            bookSubtitleView.setVisibility(View.GONE);
        }

        // Set author text according to current book if it has one. If not, set visibility to GONE.
        String bookAuthor = currentBook.getAuthor();
        if (bookAuthor != null) {
            bookAuthorView.setText(getString(R.string.author) + ":\n" + bookAuthor);
        } else {
            bookAuthorView.setVisibility(View.GONE);
        }

        // Set rating according to current book if it has one. If not, set visibility to GONE.
        Double bookRating = currentBook.getRating();
        if (bookRating != null) {
            bookRatingView.setText(getString(R.string.rating) + ":\n" + formatRating(bookRating) + " ★");
        } else {
            bookRatingView.setVisibility(View.GONE);
        }

        // Set rating according to current book if it has one. If not, set visibility to GONE.
        Double bookPrice = currentBook.getPrice();
        if (bookPrice != null) {
            bookPriceView.setText(getString(R.string.price) + ":\n" + bookPrice + " " + currentBook.getLocale());
        } else {
            bookPriceView.setVisibility(View.GONE);
        }

        // Remove a divider if the book is lacking information
        if (bookAuthor == null && bookRating == null && bookPrice == null) {
            View infoSeparator = findViewById(R.id.description_info_separator);
            infoSeparator.setVisibility(View.GONE);
        }

        // Set description according to current book if it has one. If not, set visibility to GONE.
        String bookDescription = currentBook.getDescription();
        if (bookDescription != null) {
            bookDescriptionView.setText(bookDescription);
        } else {
            bookDescriptionView.setVisibility(View.GONE);
        }

        viewOnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Use Chrome Custom Tabs to open the book's url. We first create a builder.
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();

                // We tell the builder to set our toolbar (the bar across the top of the screen) to
                // be the same color as our app's primary color.
                builder.setToolbarColor(ContextCompat.getColor(DetailActivity.this, R.color.colorPrimary));

                // We then build our intent with the builder.
                CustomTabsIntent customTabsIntent = builder.build();

                // We then launch the intent with the url from the book.
                customTabsIntent.launchUrl(DetailActivity.this, Uri.parse(currentBook.getUrl()));
            }
        });


        previewBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Use Chrome Custom Tabs to open the book's url. We first create a builder.
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();

                // We tell the builder to set our toolbar (the bar across the top of the screen) to
                // be the same color as our app's primary color.
                builder.setToolbarColor(ContextCompat.getColor(DetailActivity.this, R.color.colorPrimary));

                // We then build our intent with the builder.
                CustomTabsIntent customTabsIntent = builder.build();

                // We then launch the intent with the url from the book.
                customTabsIntent.launchUrl(DetailActivity.this, Uri.parse(currentBook.getPreviewUrl()));
            }
        });
    }

    private String formatRating(Double rating) {

        // Create an instance of the DecimalFormat class to format our doubles to 2 decimal places
        // (e.g. from 2.76 to 2.8)
        DecimalFormat formatter = new DecimalFormat("0.0");

        // Format and return the rating from the current Book according to the formatter
        return formatter.format(rating);
    }

}
