package app.qadheeb.fatimah.booklistapp;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by fatimah on 9/16/17.
 */

public class BookObjects implements Parcelable {
    private String bookTitle;
    private String authorName;

    public BookObjects(String bookTitle, String authorName) {
        this.bookTitle = bookTitle;
        this.authorName = authorName;
    }

    protected BookObjects(Parcel in) {
        bookTitle = in.readString();
        authorName = in.readString();
    }

    public static final Creator<BookObjects> CREATOR = new Creator<BookObjects>() {
        @Override
        public BookObjects createFromParcel(Parcel in) {
            return new BookObjects(in);
        }

        @Override
        public BookObjects[] newArray(int size) {
            return new BookObjects[size];
        }
    };

    public String getAuthorName() {
        return authorName;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(bookTitle);
        parcel.writeString(authorName);
    }
}
