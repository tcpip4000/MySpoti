package net.ertechnology.myspoti;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by Juan on 29/06/2015.
 */
class HitAdapter extends ArrayAdapter<Track> implements Filterable {

    public HitAdapter(Context context, List<Track> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_track, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.image = (ImageView) convertView.findViewById(R.id.list_item_image);
            viewHolder.track = (TextView) convertView.findViewById(R.id.list_item_track);
            viewHolder.album = (TextView) convertView.findViewById(R.id.list_item_album);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Track item = getItem(position);
        if (item.album.images.size() > 0) {
            Picasso.with(getContext()).load(item.album.images.get(0).url).resize(250, 250).centerCrop().into(viewHolder.image);
        }
        viewHolder.track.setText(item.name);
        viewHolder.album.setText(item.album.name);

        return convertView;
    }

    private static class ViewHolder {
        public ImageView image;
        public TextView track;
        public TextView album;
    }
}
