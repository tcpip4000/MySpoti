package net.ertechnology.myspoti;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
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

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;


/**
 * Main activity fragment for myspoti.
 */
public class MainActivityFragment extends Fragment {

    private static final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    private MySpotiAdapter mCustomAdapter;
    SpotifyService mSpotify;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        // Set adapter
        SpotifyApi api = new SpotifyApi();
        api.setAccessToken(((MainActivity) getActivity()).getToken());
        mSpotify = api.getService();

        return view;
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

                    Intent intent = new Intent(getActivity(), HitActivity.class);
                    intent.putExtra(HitActivity.HIT_ARTIST_ID, item.id);
                    startActivity(intent);

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
