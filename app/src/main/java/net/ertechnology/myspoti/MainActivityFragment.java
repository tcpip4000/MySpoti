package net.ertechnology.myspoti;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ListView;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.ArtistsPager;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements AsyncResponse {

    private static final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    private MySpotiAdapter mCustomAdapter;
    private SpotifyService mSpotify;

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

        GetDataTask getdataTask = new GetDataTask();
        getdataTask.delegate = this;
        getdataTask.execute("loca");

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

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

/*                GetDataTask getdataTask = new GetDataTask();
                getdataTask.delegate = (AsyncResponse) getActivity().getFragmentManager().findFragmentById(R.id.main_container);
                getdataTask.execute(s.toString());*/

                Filter filter = mCustomAdapter.getFilter();
                filter.filter(s.toString());
                Log.d("s is: ", s.toString());
            }
        });
    }

    @Override
    public void processFinish(ArtistsPager artistsPager) {
/*        Pager<Artist> artists = artistsPager.artists;
        for (Artist artist : artists.items) {
            Log.d(LOG_TAG, "name: " + artist.name);
            Log.d(LOG_TAG, "followers: " + artist.popularity);
            if (artist.images.size() > 0) {
                Log.d(LOG_TAG, "image0: " + artist.images.get(0).url);
            }
        }*/
        ListView listView = (ListView) getView().findViewById(R.id.main_listview);
        mCustomAdapter = new MySpotiAdapter(getActivity(), artistsPager.artists.items);
        listView.setAdapter(mCustomAdapter);
    }


    private class GetDataTask extends AsyncTask<String, Void, ArtistsPager> {

        public AsyncResponse delegate;

        @Override
        protected ArtistsPager doInBackground(String... params) {
            String searchString = params[0];
            ArtistsPager pager = mSpotify.searchArtists(searchString);
            return pager;
        }

        @Override
        protected void onPostExecute(ArtistsPager artistsPager) {
            delegate.processFinish(artistsPager);
        }
    }


}
