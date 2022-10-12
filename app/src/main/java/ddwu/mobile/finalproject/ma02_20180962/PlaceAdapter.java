package ddwu.mobile.finalproject.ma02_20180962;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.audiofx.DynamicsProcessing;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class PlaceAdapter extends BaseAdapter {

    final static String TAG = "PlaceAdapter";

    private LayoutInflater inflater;
    private Context context;
    private int layout;
    private ArrayList<PlaceDto> list;


    public PlaceAdapter(Context context, int resource, ArrayList<PlaceDto> list) {
        this.context = context;
        this.layout = resource;
        this.list = list;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return list.size();
    }


    @Override
    public PlaceDto getItem(int position) {
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
        PlaceAdapter.ViewHolder viewHolder = null;

        if (view == null) {
            view = inflater.inflate(layout, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tvName = view.findViewById(R.id.tvName);
            viewHolder.tvAddress = view.findViewById(R.id.tvAddress);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)view.getTag();
        }

        PlaceDto dto = list.get(position);

        viewHolder.tvName.setText(dto.getName());
        viewHolder.tvAddress.setText(dto.getAddress());

        return view;
    }


    public void setList(ArrayList<PlaceDto> list) {
        this.list = list;
        notifyDataSetChanged();
    }


    static class ViewHolder {
        public TextView tvName = null;
        public TextView tvAddress = null;
    }

}
