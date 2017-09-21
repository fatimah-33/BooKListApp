package app.qadheeb.fatimah.booklistapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by fatimah on 9/16/17.
 */

public class BookAdapter extends ArrayAdapter<BookObjects> {
    public BookAdapter(Context context, ArrayList<BookObjects> bookList) {
        super(context, 0, bookList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View bookListView = convertView;
        if (bookListView == null) {
            bookListView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        BookObjects currantBookList = getItem(position);

        TextView bookTitle = (TextView) bookListView.findViewById(R.id.book_title);
        TextView AuthorName = (TextView) bookListView.findViewById(R.id.author_name);

        bookTitle.setText(currantBookList.getBookTitle());
        AuthorName.setText(currantBookList.getAuthorName());

        return bookListView;
    }
}
