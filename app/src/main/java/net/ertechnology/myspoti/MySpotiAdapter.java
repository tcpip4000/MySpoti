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

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import kaaes.spotify.webapi.android.models.Artist;

/**
 * Created by Juan on 29/06/2015.
 */
public class MySpotiAdapter extends ArrayAdapter<Artist> implements Filterable {

    private CustomFilter mFilter;
    private List<Artist> mObjects;
    private List<Artist> mOriginalValues;

    public MySpotiAdapter(Context context, List<Artist> objects) {
        super(context, 0, objects);
        mOriginalValues = objects;
        mObjects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.list_item_data, parent, false);
        Artist item = getItem(position);

        ImageView image = (ImageView) view.findViewById(R.id.list_item_image);
        TextView description = (TextView) view.findViewById(R.id.list_item_description);

        if (item.images.size() > 0) {
            Picasso.with(getContext()).load(item.images.get(0).url).resize(250, 250).centerCrop().into(image);
        }
        description.setText(item.name);

        return view;
    }

    @Override
    public int getCount() {
        return mObjects.size();
    }

    @Override
    public Artist getItem(int position) {
        return mObjects.get(position);
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
            List<Artist> filteredList = new ArrayList<>();
            String constraintLower = constraint.toString().toLowerCase();
            if (constraint!= null && constraint.toString().length() > 0) {
                for (Artist artist : mOriginalValues) {
                    if (artist.name.toLowerCase().contains(constraintLower)) {
                        filteredList.add(artist);
                    }
                }
                results.count = filteredList.size();

            } else {
                results.count = 0;
            }

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
            mObjects = (List<Artist>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
}
