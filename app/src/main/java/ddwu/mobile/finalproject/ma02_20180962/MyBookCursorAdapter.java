package ddwu.mobile.finalproject.ma02_20180962;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyBookCursorAdapter extends CursorAdapter {

    final static String TAG = "MyBookCursorAdapter";

    ImageFileManager imageFileManager;
    LayoutInflater inflater;
    int layout;

    public MyBookCursorAdapter(Context context, int layout, Cursor c) {
        super(context, c, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layout = layout;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = inflater.inflate(layout, parent, false);
        ViewHolder holder = new ViewHolder();
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        if (viewHolder.tvTitle == null) {
            viewHolder.tvTitle = view.findViewById(R.id.tvTitle);
            viewHolder.tvAuthor = view.findViewById(R.id.tvAuthor);
            viewHolder.tvPublisher = view.findViewById(R.id.tvPublisher);
            viewHolder.tvPubdate = view.findViewById(R.id.tvPubdate);
            viewHolder.tvPrice = view.findViewById(R.id.tvPrice);
            viewHolder.ivImage = view.findViewById(R.id.ivImage);
        }

        viewHolder.tvTitle.setText(cursor.getString(cursor.getColumnIndex(BookDBHelper.COL_TITLE)));
        viewHolder.tvAuthor.setText(cursor.getString(cursor.getColumnIndex(BookDBHelper.COL_AUTHOR)));
        viewHolder.tvPublisher.setText(cursor.getString(cursor.getColumnIndex(BookDBHelper.COL_PUBLISHER)));
        viewHolder.tvPubdate.setText(cursor.getString(cursor.getColumnIndex(BookDBHelper.COL_PUBDATE)));
        viewHolder.tvPrice.setText(cursor.getString(cursor.getColumnIndex(BookDBHelper.COL_PRICE)));

        // 외부 저장소에서 bitmap 가져옴
        String imageFileName = cursor.getString(cursor.getColumnIndex(BookDBHelper.COL_IMAGEFILENAME));
        imageFileManager = new ImageFileManager(context);
        Bitmap bitmap = imageFileManager.getBitmapFromExternal(imageFileName);
        if (bitmap == null) {
            Log.d(TAG, "Failed getting Bitmap: " + imageFileName);
            viewHolder.ivImage.setImageResource(R.mipmap.ic_launcher);
        } else {
            viewHolder.ivImage.setImageBitmap(bitmap);
        }
    }

    static class ViewHolder {
        public TextView tvTitle = null;
        public TextView tvAuthor = null;
        public TextView tvPublisher = null;
        public TextView tvPubdate = null;
        public TextView tvPrice = null;
        public ImageView ivImage = null;
    }

}
