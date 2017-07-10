package com.example.talha.booksearch;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * A {@link Book} object represents a single book retrieved fromm the Google Books API.
 * Each object has 5 properties: The title, subtitle, author, url and rating of the book.
 *
 * The class implements {@link Parcelable} so that when transitioning between Activities, the entire
 * object can be sent to the new activity. If Parcelable was not implemented, then we would have had
 * to put .putExtra on our intent for every method in this class.
 */
public class Book implements Parcelable {

    /**
     * Stores the title of the book
     */
    private String mTitle;

    /**
     * Stores the subtitle of the book
     */
    private String mSubtitle;

    /**
     * Stores the description of the book
     */
    private String mDescription;

    /**
     * Stores the author of the book
     */
    private String mAuthor;

    /**
     * Stores the rating of the book
     */
    private Double mRating;

    /**
     * Stores the url which takes the user to Google's overview of the book.
     */
    private String mUrl;

    /**
     * Stores the url which takes the user to a preview of the book.
     */
    private String mPreviewUrl;

    /**
     * Stores the image url of the book
     */
    private String mImageUrl;

    /**
     * Stores the currency the book's price is in.
     */
    private String mLocale;

    /**
     * Stores the price of the book
     */
    private Double mPrice;

    /**
     * Creates a new Book object.
     *
     * @param title       is the title of the book.
     * @param subtitle    is the subtitle of the book.
     * @param description is the description of the book.
     * @param author      is the author of the book.
     * @param rating      is the rating of the book.
     * @param url         is the url of the book.
     * @param previewUrl  is the url for the preview of the book.
     * @param imageUrl    is the book object's image Url.
     * @param locale      is the currency the book's price is in.
     * @param price       is the price of the book.
     */
    public Book(String title, String subtitle, String description, String author, Double rating,
                String url, String previewUrl, String imageUrl, String locale, Double price) {
        mTitle = title;
        mSubtitle = subtitle;
        mDescription = description;
        mAuthor = author;
        mRating = rating;
        mUrl = url;
        mPreviewUrl = previewUrl;
        mImageUrl = imageUrl;
        mLocale = locale;
        mPrice = price;
    }

    /**
     * @return the title of the book.
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * @return the subtitle of the book.
     */
    public String getSubtitle() {
        return mSubtitle;
    }

    /**
     * @return the description of the book.
     */
    public String getDescription() {
        return mDescription;
    }

    /**
     * @return the author of the book.
     */
    public String getAuthor() {
        return mAuthor;
    }

    /**
     * @return the rating of the book.
     */
    public Double getRating() {
        return mRating;
    }

    /**
     * @return the url of the book.
     */
    public String getUrl() {
        return mUrl;
    }

    /**
     * @return the preview url of the book.
     */
    public String getPreviewUrl() {
        return mPreviewUrl;
    }

    /**
     * @return the image url of the book.
     */
    public String getImageUrl() {
        return mImageUrl;
    }

    /**
     * @return the currency of the book's price.
     */
    public String getLocale() {
        return mLocale;
    }

    /**
     * @return the price of the book.
     */
    public Double getPrice() {
        return mPrice;
    }


    /* The following code implements the {@link Parcelable} class into this class.
     *
     * Parcelable allows an object of this class to be "parceled" up for it to be sent to another
     * activity. In the other activity the parcel is "un-parceled" so that we have access to the
     * object and it's methods. This is similar to using Intent.putExtra, and then using
     * Intent.getExtra, but instead of using a String or an Integer, we're using a {@link Book}
     * object. */


    /**
     * Override this method from {@link Parcelable} and return 0 because we will not be using this
     * method.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Packs the Parcel with the current state of the object, which will later be used to re-create
     * the object again.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {

        // All Strings are stored in this String array, and then written to the parcel.
        String[] data = {mTitle, mSubtitle, mDescription, mAuthor, mUrl, mPreviewUrl, mImageUrl, mLocale};
        dest.writeStringArray(data);

        // The doubles are written to the parcel. If they don't exist (null) then set the value to
        // be -1 (an impossible value to get from JSON). We can switch back to null after unpacking.
        if (mRating != null) {
            dest.writeDouble(mRating);
        } else {
            dest.writeDouble(-1);
        }

        if (mPrice != null) {
            dest.writeDouble(mPrice);
        } else {
            dest.writeDouble(-1);
        }
    }


    /**
     * Creates a new book object from a {@link Parcel}.
     *
     * @param in is the Parcel object that is being used to create the object.
     */
    public Book(Parcel in) {
        // Create a new String array to store the array from the parcel.
        String[] data = new String[8];
        in.readStringArray(data);

        // Retrieve all the data from the String array.
        mTitle = data[0];
        mSubtitle = data[1];
        mDescription = data[2];
        mAuthor = data[3];
        mUrl = data[4];
        mPreviewUrl = data[5];
        mImageUrl = data[6];
        mLocale = data[7];

        // Retrieve the doubles. If they are -1 (which we set if they don't exist) then we set
        // them back to null.
        Double ratingDouble = in.readDouble();
        if (ratingDouble != -1) {
            mRating = ratingDouble;
        } else {
            mRating = null;
        }

        Double priceDouble = in.readDouble();
        if (priceDouble != -1) {
            mPrice = priceDouble;
        } else {
            mPrice = null;
        }
    }

    /**
     * Class which must be implemented so that the Parcels can actually be turned back into Book objects.
     */
    public static final Creator CREATOR = new Creator() {

        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

}
