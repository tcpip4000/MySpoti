package net.ertechnology.myspoti;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Juan on 29/06/2015.
 */
class MySpotiAdapter extends ArrayAdapter<MyArtist> implements Filterable {

    private CustomFilter mFilter;
    private List<MyArtist> mObjects;
    private List<MyArtist> mOriginalValues;

    public MySpotiAdapter(Context context, List<MyArtist> objects) {
        super(context, 0, objects);
        mOriginalValues = objects;
        mObjects = objects;
    }

    public void setObjects(List<MyArtist> objects) {
        mOriginalValues = objects;
        mObjects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_artist, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.image = (ImageView) convertView.findViewById(R.id.list_item_image);
            viewHolder.description = (TextView) convertView.findViewById(R.id.list_item_description);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        MyArtist item = getItem(position);
        if (item.getImages().size() > 0 && !item.getImages().get(0).isEmpty()) {
            Picasso.with(getContext()).load(item.getImages().get(0)).resize(250, 250).centerCrop().into(viewHolder.image);
        } else {
            Picasso.with(getContext()).load(R.mipmap.ic_launcher).resize(250, 250).centerCrop().into(viewHolder.image);
        }
        viewHolder.description.setText(item.getName());

        return convertView;
    }

    @Override
    public int getCount() {
        int size = 0;
        if (mObjects != null) {
            size = mObjects.size();
        }
        return size;
    }

    @Override
    public MyArtist getItem(int position) {
        MyArtist artist = null;
        if (position >= 0 && position < getCount()) {
            artist = mObjects.get(position);
        }
        return artist;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new CustomFilter();
        }
        return mFilter;
    }

    private class CustomFilter extends Filter {

        /**
         * <p>Invoked in a worker thread to filter the data according to the
         * constraint. Subclasses must implement this method to perform the
         * filtering operation. Results computed by the filtering operation
         * must be returned as a {@link FilterResults} that
         * will then be published in the UI thread through
         * {@link #publishResults(CharSequence,
         * FilterResults)}.</p>
         * <p/>
         * <p><strong>Contract:</strong> When the constraint is null, the original
         * data must be restored.</p>
         *
         * @param constraint the constraint used to filter the data
         * @return the results of the filtering operation
         * @see #filter(CharSequence, FilterListener)
         * @see #publishResults(CharSequence, FilterResults)
         * @see FilterResults
         */
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<MyArtist> filteredList = new ArrayList<>();
            String constraintLower = constraint.toString().toLowerCase();
            if (constraint.toString().length() > 0) {
                for (MyArtist artist : mOriginalValues) {
                    if (artist.getName().toLowerCase().contains(constraintLower)) {
                        filteredList.add(artist);
                    }
                }
                /*if (filteredList.size() == 0) {
                    ArtistsPager pager = MySession.getInstance().getSpotifyService().searchArtists(constraint.toString());
                    filteredList = pager.artists.items;
                    mOriginalValues = filteredList;
                }*/
            }
            results.count = filteredList.size();
            results.values = filteredList;
            return results;
        }

        /**
         * <p>Invoked in the UI thread to publish the filtering results in the
         * user interface. Subclasses must implement this method to display the
         * results computed in {@link #performFiltering}.</p>
         *
         * @param constraint the constraint used to filter the data
         * @param results    the results of the filtering operation
         * @see #filter(CharSequence, FilterListener)
         * @see #performFiltering(CharSequence)
         * @see FilterResults
         */
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mObjects = (List<MyArtist>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
                if (!constraint.toString().isEmpty()) {
                    Toast.makeText(getContext(), R.string.artist_not_found, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private static class ViewHolder {
        public ImageView image;
        public TextView description;
    }
}
