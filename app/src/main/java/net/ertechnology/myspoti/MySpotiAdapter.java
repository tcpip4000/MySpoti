package net.ertechnology.myspoti;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;

/**
 * Created by Juan on 29/06/2015.
 */
public class MySpotiAdapter extends ArrayAdapter<Artist> {

    public MySpotiAdapter(Context context, List<Artist> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.list_item_data, parent, false);
        //View view = super.getView(position, convertView, parent);
        Artist item = getItem(position);

        TextView image = (TextView) view.findViewById(R.id.list_item_image);
        TextView description = (TextView) view.findViewById(R.id.list_item_description);

        image.setText(item.id);
        description.setText(item.name);

        return view;
    }

}
