package ddwu.mobile.finalproject.ma02_20180962;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BookDBHelper extends SQLiteOpenHelper {

    private final static String DB_NAME = "book_db";

    public final static String TABLE_NAME = "book_table";

    public final static String COL_ID = "_id";
    public final static String COL_TITLE = "title";
    public final static String COL_AUTHOR = "author";
    public final static String COL_PUBLISHER = "publisher";
    public final static String COL_PUBDATE = "pubdate";
    public final static String COL_PRICE = "price";
    public final static String COL_ISBN = "isbn";
    public final static String COL_DESCRIPTION = "description";
    public final static String COL_IMAGELINK = "imageLink";
    public final static String COL_IMAGEFILENAME = "imageFileName";
    public final static String COL_STATE = "state";
    public final static String COL_PAGE = "page";
    public final static String COL_PASSAGE1 = "passage1";
    public final static String COL_PASSAGE2 = "passage2";
    public final static String COL_PASSAGE3 = "passage3";
    public final static String COL_RATE = "rate";
    public final static String COL_REVIEW = "review";


    public BookDBHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " ( " + COL_ID + " integer primary key autoincrement, "
                + COL_TITLE + " TEXT, " + COL_AUTHOR + " TEXT, " + COL_PUBLISHER + " TEXT, "
                + COL_PUBDATE + " TEXT, " + COL_PRICE + " integer, " + COL_ISBN + " TEXT, "
                + COL_DESCRIPTION + " TEXT, " + COL_IMAGELINK + " TEXT, " + COL_IMAGEFILENAME + " TEXT, "
                + COL_STATE + " integer, " + COL_PAGE + " integer, " + COL_PASSAGE1 + " TEXT, "
                + COL_PASSAGE2 + " TEXT, " + COL_PASSAGE3 + " TEXT, " + COL_RATE + " float, " + COL_REVIEW + " TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("drop table " + TABLE_NAME);
        onCreate(db);
    }
}
