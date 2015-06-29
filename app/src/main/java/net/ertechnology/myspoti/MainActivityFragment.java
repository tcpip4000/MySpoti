package net.ertechnology.myspoti;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Pager;


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
        getdataTask.execute(mSpotify);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void processFinish(ArtistsPager artistsPager) {
        Pager<Artist> artists = artistsPager.artists;
        for (Artist artist : artists.items) {
            Log.d(LOG_TAG, "name: " + artist.name);
            Log.d(LOG_TAG, "followers: " + artist.popularity);
            if (artist.images.size() > 0) {
                Log.d(LOG_TAG, "image0: " + artist.images.get(0).url);
            }
        }
        ListView listView = (ListView) getView().findViewById(R.id.main_listview);
        mCustomAdapter = new MySpotiAdapter(getActivity(), artistsPager.artists.items);
        listView.setAdapter(mCustomAdapter);
    }


    private class GetDataTask extends AsyncTask<SpotifyService, Void, ArtistsPager> {

        public AsyncResponse delegate;

        @Override
        protected ArtistsPager doInBackground(SpotifyService... params) {
            SpotifyService spotify = params[0]; // TODO make attribute
            ArtistsPager pager = spotify.searchArtists("loco");
            return pager;
        }

        @Override
        protected void onPostExecute(ArtistsPager artistsPager) {
            delegate.processFinish(artistsPager);
        }
    }


}
