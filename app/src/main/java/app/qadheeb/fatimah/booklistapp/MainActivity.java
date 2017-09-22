package app.qadheeb.fatimah.booklistapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

public class MainActivity extends AppCompatActivity {
    private static final String MY_LIST_KEY = "bookList";
    private ListView bookListView;
    private BookAdapter bookAdapter;
    private Button searchBtn;
    private EditText searchEditText;
    private ArrayList<BookObjects> bookArrayList;
    private TextView textViewNote;
    private String bookTitle;
    private String authorName = "";
    private String newUrl;
    private String bookApiLink = "https://www.googleapis.com/books/v1/volumes?q=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState != null && savedInstanceState.containsKey("bookList")) {
            bookArrayList = savedInstanceState.getParcelableArrayList(MY_LIST_KEY);
            bookListView = (ListView) findViewById(R.id.list_book);
            bookAdapter = new BookAdapter(MainActivity.this, bookArrayList);
            bookListView.setAdapter(bookAdapter);
        }
        searchBtn = (Button) findViewById(R.id.search_button);
        searchEditText = (EditText) findViewById(R.id.edit_text_search);
        textViewNote = (TextView) findViewById(R.id.text_view_note);
        textViewNote.setText(null);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textViewNote.setText(null);
                String inputSearchText = searchEditText.getText().toString().replace(" ", "+");
                if (checkNetworkConnection() == true) {
                    if (inputSearchText.isEmpty()) {
                        textViewNote.setText(getString(R.string.no_search_word));
                    } else {
                        newUrl = bookApiLink.concat(inputSearchText);
                        new SetBookListTask().execute();
                    }
                } else {
                    textViewNote.setText(getString(R.string.no_connection));

                }
            }
        });
    }

    public class SetBookListTask extends AsyncTask<URL, Void, ArrayList<BookObjects>> {

        @Override
        protected ArrayList<BookObjects> doInBackground(URL... urls) {
            URL url;
            HttpURLConnection httpURLConnection = null;
            InputStream inputStream;
            ArrayList<BookObjects> jasonReadableText = null;

            try {
                url = new URL(newUrl);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();
                inputStream = httpURLConnection.getInputStream();
                String getReadableData = makeTheDataReadable(inputStream);
                if (getReadableData != null) {
                    jasonReadableText = passingJSONObject(getReadableData);

                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jasonReadableText;
        }

        @Override
        protected void onPostExecute(ArrayList<BookObjects> result) {
            if (result.isEmpty()) {
                bookListView = (ListView) findViewById(R.id.list_book);
                bookAdapter = new BookAdapter(MainActivity.this, result);
                bookListView.setAdapter(bookAdapter);
                textViewNote.setText(getString(R.string.no_books));
            } else {
                bookListView = (ListView) findViewById(R.id.list_book);
                bookAdapter = new BookAdapter(MainActivity.this, result);
                bookListView.setAdapter(bookAdapter);
            }
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    public String makeTheDataReadable(InputStream inputStream) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        StringBuilder stringBuilder = new StringBuilder();
        try {
            String lineText = bufferedReader.readLine();
            while (lineText != null) {
                stringBuilder.append(lineText);
                lineText = bufferedReader.readLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    public ArrayList<BookObjects> passingJSONObject(String jsonData) throws JSONException {

        try {
            bookArrayList = new ArrayList<>();
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray jsonArray = jsonObject.getJSONArray("items");
            if (jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonFirstFetch = jsonArray.getJSONObject(i);
                    JSONObject jsonObjectRequiredData = jsonFirstFetch.getJSONObject("volumeInfo");
                    JSONArray authorArray = jsonObjectRequiredData.optJSONArray("authors");
                    bookTitle = jsonObjectRequiredData.getString("title");
                    authorName = "";
                    if (authorArray != null) {
                        for (int j = 0; j < authorArray.length(); j++) {
                            String author = authorArray.getString(j);
                            if (authorName.isEmpty()) {
                                authorName = author;
                            } else if (j == authorArray.length() - 1) {
                                authorName = authorName + " and " + author;

                            } else {
                                authorName = authorName + ", " + author;
                            }
                        }

                    } else {
                        authorName = getString(R.string.no_author);
                    }
                    bookArrayList.add(new BookObjects(bookTitle, authorName));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return bookArrayList;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(MY_LIST_KEY, bookArrayList);
    }

    public boolean checkNetworkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkStatus = connectivityManager.getActiveNetworkInfo();
        return networkStatus != null && networkStatus.isConnected();

    }
}
