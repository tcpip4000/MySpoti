package net.ertechnology.myspoti;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ListView;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.models.Artist;


/**
 * Main activity fragment for myspoti.
 */
public class MainActivityFragment extends Fragment {

    private static final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    private MySpotiAdapter mCustomAdapter;

    private MainFragmentListener mCallback;

    public MainActivityFragment() {
    }

    public interface MainFragmentListener {
        void onArtistClicked(String artistId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (MainFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() +
                    "must implement MainFragmentListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.app_name);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            // Set adapter
            ListView listView = (ListView) getView().findViewById(R.id.main_listview);
            mCustomAdapter = new MySpotiAdapter(getActivity(), new ArrayList<Artist>());
            listView.setAdapter(mCustomAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Artist item = mCustomAdapter.getItem(position);

                    mCallback.onArtistClicked(item.id);

                }
            });

            // Search button
            EditText search = (EditText) getView().findViewById(R.id.main_search);
            search.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    Filter filter = mCustomAdapter.getFilter();
                    filter.filter(s.toString());
                }
            });
        } catch (NullPointerException e) {
            Log.d(LOG_TAG, "Error", e);
        }
    }

}
