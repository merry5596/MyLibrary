package ddwu.mobile.finalproject.ma02_20180962;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class ShowMyLibraryActivity extends AppCompatActivity {

    final static String TAG = "ShowMyLibraryActivity";

    ListView lvMyBook;
    RadioGroup rgListState;

    BookDBHelper helper;
    MyBookCursorAdapter adapter;

    Cursor cursor;
    int listState;
    int currentRbId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_library);

        helper = new BookDBHelper(this);

        lvMyBook = findViewById(R.id.lvMyBook);
        rgListState = findViewById(R.id.rgListState);

        // 이전 설정 가져오기
        SharedPreferences pref = getSharedPreferences("SaveState", 0);
        listState = pref.getInt("listState", 1);
        if (listState == 1) {
            currentRbId = R.id.rbWishList;
        } else if (listState == 2) {
            currentRbId = R.id.rbReadingList;
        } else if (listState == 3) {
            currentRbId = R.id.rbReadList;
        }
        rgListState.check(currentRbId);

        adapter = new MyBookCursorAdapter(this, R.layout.listview_book, null);
        lvMyBook.setAdapter(adapter);

        lvMyBook.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                BookDto book = getBookFromDB(id);

                Intent intent = new Intent(ShowMyLibraryActivity.this, ShowBookInfoActivity.class);
                intent.putExtra("book", book);
                intent.putExtra("isNew", false);
                startActivity(intent);
            }
        });

        lvMyBook.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ShowMyLibraryActivity.this);
                builder.setTitle("삭제")
                        .setMessage("삭제하시겠습니까?")
                        .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SQLiteDatabase db = helper.getWritableDatabase();
                                String whereClause = BookDBHelper.COL_ID + "=?";
                                String[] whereArgs = new String[]{String.valueOf(id)};
                                db.delete(BookDBHelper.TABLE_NAME, whereClause, whereArgs);

                                Toast.makeText(ShowMyLibraryActivity.this, "삭제되었습니다.", Toast.LENGTH_SHORT).show();

                                helper.close();
                                updateAdapter();
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(ShowMyLibraryActivity.this, "삭제가 취소되었습니다.", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setCancelable(false)
                        .show();

                return true;
            }
        });

        rgListState.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rbWishList:
                        listState = 1;
                        break;
                    case R.id.rbReadingList:
                        listState = 2;
                        break;
                    case R.id.rbReadList:
                        listState = 3;
                        break;
                }
                updateAdapter();
            }
        });
    }

    private void updateAdapter() {
        SQLiteDatabase db = helper.getReadableDatabase();
        String[] selectionArgs = new String[]{String.valueOf(listState)};
        cursor = db.rawQuery("select * from " + BookDBHelper.TABLE_NAME + " where " + BookDBHelper.COL_STATE + "=?", selectionArgs);

        adapter.changeCursor(cursor);
        helper.close();
    }

    private BookDto getBookFromDB(long id) {

        SQLiteDatabase db = helper.getReadableDatabase();
        String[] selectionArgs = new String[]{String.valueOf(id)};
        cursor = db.rawQuery("select * from " + BookDBHelper.TABLE_NAME + " where " + BookDBHelper.COL_ID + "=?", selectionArgs);

        BookDto book = null;

        if (cursor.moveToNext()) {
            String title = cursor.getString(cursor.getColumnIndex(BookDBHelper.COL_TITLE));
            String author = cursor.getString(cursor.getColumnIndex(BookDBHelper.COL_AUTHOR));
            String publisher = cursor.getString(cursor.getColumnIndex(BookDBHelper.COL_PUBLISHER));
            String pubdate = cursor.getString(cursor.getColumnIndex(BookDBHelper.COL_PUBDATE));
            int price = cursor.getInt(cursor.getColumnIndex(BookDBHelper.COL_PRICE));
            String isbn = cursor.getString(cursor.getColumnIndex(BookDBHelper.COL_ISBN));
            String description = cursor.getString(cursor.getColumnIndex(BookDBHelper.COL_DESCRIPTION));
            String imageLink = cursor.getString(cursor.getColumnIndex(BookDBHelper.COL_IMAGELINK));
            String imageFileName = cursor.getString(cursor.getColumnIndex(BookDBHelper.COL_IMAGEFILENAME));
            int state = cursor.getInt(cursor.getColumnIndex(BookDBHelper.COL_STATE));
            int page = cursor.getInt(cursor.getColumnIndex(BookDBHelper.COL_PAGE));
            String passage1 = cursor.getString(cursor.getColumnIndex(BookDBHelper.COL_PASSAGE1));
            String passage2 = cursor.getString(cursor.getColumnIndex(BookDBHelper.COL_PASSAGE2));
            String passage3 = cursor.getString(cursor.getColumnIndex(BookDBHelper.COL_PASSAGE3));
            float rate = cursor.getFloat(cursor.getColumnIndex(BookDBHelper.COL_RATE));
            String review = cursor.getString(cursor.getColumnIndex(BookDBHelper.COL_REVIEW));

            List<String> passage = new ArrayList<>();
            passage.add(passage1);
            passage.add(passage2);
            passage.add(passage3);

            book = new BookDto(id, title, author, publisher, pubdate, price, isbn, description,
                    imageLink, imageFileName, state, page, passage, rate, review);
        }

        return book;
    }


    @Override
    protected void onResume() {
        super.onResume();
        updateAdapter();
    }


    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences pref = getSharedPreferences("SaveState", 0);
        SharedPreferences.Editor edit = pref.edit();
        edit.putInt("listState", listState);
        edit.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cursor != null) cursor.close();
    }
}