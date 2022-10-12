package ddwu.mobile.finalproject.ma02_20180962;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class SearchBookActivity extends AppCompatActivity {

    final static String TAG = "BookSearchActivity";

    EditText etKeyword;
    ListView lvBook;

    BookXmlParser parser;
    NetworkManager networkManager;
    ImageFileManager imageFileManager;
    BookAdapter adapter;

    ArrayList<BookDto> resultList;
    String apiAddress;
    String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_search);

        etKeyword = findViewById(R.id.etKeyword);
        lvBook = findViewById(R.id.lvBook);

        resultList = new ArrayList();
        adapter = new BookAdapter(this, R.layout.listview_book, resultList);
        lvBook.setAdapter(adapter);


        // Naver API
        apiAddress = getResources().getString(R.string.api_url);

        networkManager = new NetworkManager(this);
        networkManager.setClientId(getResources().getString(R.string.client_id));
        networkManager.setClientSecret(getResources().getString(R.string.client_secret));

        parser = new BookXmlParser();
        imageFileManager = new ImageFileManager(this);

        lvBook.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "position: " + i);
                BookDto book = resultList.get(i);

                Log.d(TAG, book.getTitle());
                Intent intent = new Intent(SearchBookActivity.this, ShowBookInfoActivity.class);
                intent.putExtra("book", book);
                intent.putExtra("isNew", true);
                startActivity(intent);
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 임시 파일 삭제
        imageFileManager.clearTemporaryFiles();
    }


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSearch:
                // 네이버 api 검색해서 listView에...
                query = etKeyword.getText().toString();  // UTF-8 인코딩 필요

                try {
                    new NetworkAsyncTask().execute(apiAddress + URLEncoder.encode(query, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                break;
        }

    }


    class NetworkAsyncTask extends AsyncTask<String, Integer, String> {
        ProgressDialog progressDlg;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDlg = ProgressDialog.show(SearchBookActivity.this, "Wait", "Downloading...");
        }

        @Override
        protected String doInBackground(String... strings) {
            String address = strings[0];
            String result = null;
            // networking
            result = networkManager.downloadContents(address);
            if (result == null) return "Error!";

            Log.d(TAG, result);

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            resultList = parser.parse(result);

            adapter.setList(resultList);

            progressDlg.dismiss();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_library, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent = null;

        switch (item.getItemId()) {
            case R.id.itemLibrary:
                intent = new Intent(this, ShowMyLibraryActivity.class);
                break;
        }

        startActivity(intent);

        return super.onOptionsItemSelected(item);
    }
}
