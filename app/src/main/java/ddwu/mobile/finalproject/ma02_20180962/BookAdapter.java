package ddwu.mobile.finalproject.ma02_20180962;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class BookAdapter extends BaseAdapter {

    final static String TAG = "BookAdapter";

    private LayoutInflater inflater;
    private Context context;
    private int layout;
    private ArrayList<BookDto> list;
    private NetworkManager networkManager = null;
    private ImageFileManager imageFileManager = null;


    public BookAdapter(Context context, int resource, ArrayList<BookDto> list) {
        this.context = context;
        this.layout = resource;
        this.list = list;
        imageFileManager = new ImageFileManager(context);
        networkManager = new NetworkManager(context);
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return list.size();
    }


    @Override
    public BookDto getItem(int position) {
        return list.get(position);
    }


    @Override
    public long getItemId(int position) {
        return list.get(position).get_id();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Log.d(TAG, "getView with position : " + position);
        View view = convertView;
        ViewHolder viewHolder = null;

        if (view == null) {
            view = inflater.inflate(layout, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tvTitle = view.findViewById(R.id.tvTitle);
            viewHolder.tvAuthor = view.findViewById(R.id.tvAuthor);
            viewHolder.tvPublisher = view.findViewById(R.id.tvPublisher);
            viewHolder.tvPubdate = view.findViewById(R.id.tvPubdate);
            viewHolder.tvPrice = view.findViewById(R.id.tvPrice);
            viewHolder.ivImage = view.findViewById(R.id.ivImage);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)view.getTag();
        }

        BookDto dto = list.get(position);

        Log.d(TAG, "1: " + dto.getImageLink());

        viewHolder.tvTitle.setText(dto.getTitle());
        viewHolder.tvAuthor.setText(dto.getAuthor());
        viewHolder.tvPublisher.setText(dto.getPublisher());
        viewHolder.tvPubdate.setText(dto.getPubdate());
        viewHolder.tvPrice.setText(Integer.toString(dto.getPrice()));


//         dto??? ????????? ????????? ???????????? ????????? ????????? ?????????????????? ????????? ??????
        if (dto.getImageLink() == null) {
            Log.d(TAG, "imageLink is null");
            viewHolder.ivImage.setImageResource(R.mipmap.ic_launcher);
            return view;
        }
        // ????????? ????????? ??????
        Bitmap savedBitmap = imageFileManager.getBitmapFromTemporary(dto.getImageLink()); // ?????? ??????
        if (savedBitmap != null) {
            Log.d(TAG, "Image loading from file");
            // bitmap??? ????????? ??????????????? ??????
            viewHolder.ivImage.setImageBitmap(savedBitmap);
        } else {
            Log.d(TAG, "Image loading from network");
            // ?????? ?????? GetImageAsyncTask ??? ???????????? ????????? ?????? ???????????? ??????
            viewHolder.ivImage.setImageResource(R.mipmap.ic_launcher);
            Log.d(TAG, dto.getImageLink());
            new GetImageAsyncTask(viewHolder).execute(dto.getImageLink());
        }

        return view;
    }


    public void setList(ArrayList<BookDto> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    static class ViewHolder {
        public TextView tvTitle = null;
        public TextView tvAuthor = null;
        public TextView tvPublisher = null;
        public TextView tvPubdate = null;
        public TextView tvPrice = null;
        public ImageView ivImage = null;
    }


    /* ??? ???????????? ???????????? ??? ?????????????????? ????????? ???????????? ??????????????? ???????????? AsyncTask */
    class GetImageAsyncTask extends AsyncTask<String, Void, Bitmap> {

        ViewHolder viewHolder;
        String imageAddress;

        public GetImageAsyncTask(ViewHolder holder) {
            viewHolder = holder;
        }

        // ????????? ?????? ??????
        @Override
        protected Bitmap doInBackground(String... params) {
            imageAddress = params[0];
            Bitmap result = null;
            result = networkManager.downloadImage(imageAddress);

            return result;
        }


        @Override
        protected void onPostExecute(Bitmap bitmap) {
            /*?????????????????? ?????? ?????? ????????? ????????? ImageFileManager ??? ???????????? ?????????????????? ??????
             * ???????????? bitmap ??? ??????????????? ??????*/
            if (bitmap != null) {
                viewHolder.ivImage.setImageBitmap(bitmap);
                imageFileManager.saveBitmapToTemporary(bitmap, imageAddress);
            }
        }



    }
}
