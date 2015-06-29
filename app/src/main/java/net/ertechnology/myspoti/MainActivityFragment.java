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

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);
        //final ListView listView = (ListView) view.findViewById(R.id.main_listview);

        // Set adapter
        SpotifyApi api = new SpotifyApi();
        api.setAccessToken(((MainActivity) getActivity()).getToken());
        SpotifyService spotify = api.getService();

        GetDataTask getdataTask = new GetDataTask();
        getdataTask.delegate = this;
        getdataTask.execute(spotify);

/*            mCustomAdapter = new MySpotiAdapter(getActivity(), R.layout.list_item_data, null);
            listView.setAdapter(mCustomAdapter);*/

/*            spotify.searchArtists("loco", new Callback<ArtistsPager>() {
                @Override
                public void success(ArtistsPager artistsPager, Response response) {
                    Log.d(LOG_TAG, artistsPager.toString());
                    Pager<Artist> artists = artistsPager.artists;
                    for (Artist artist : artists.items) {
                        Log.d(LOG_TAG, "name: " + artist.name);
                        Log.d(LOG_TAG, "followers: " + artist.popularity);
                    }
                    mCustomAdapter = new MySpotiAdapter(getActivity(), R.layout.list_item_data, artistsPager.artists.items);
                    listView.setAdapter(mCustomAdapter);
                }

                @Override
                public void failure(RetrofitError error) {

                }
            });*/

/*        HashMap<String, String> map = new HashMap<>();
        HashMap<String, String> map2 = new HashMap<>();
        map.put("image", "image1");
        map.put("description", "Soul Fly");
        map2.put("image", "image2");
        map2.put("description", "Ramones");
        data.add(map);
        data.add(map2);
        String[] from = new String[] {"image", "description"};
        int[] to = new int[]{R.id.list_item_image, R.id.list_item_description};*/

/*            mCustomAdapter = new MySpotiAdapter(getActivity(), R.layout.list_item_data, pager.artists.items);
            listView.setAdapter(mCustomAdapter);*/

            // Search function
/*            EditText search = (EditText) view.findViewById(R.id.main_search);
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
                    //Log.d("s is: ", s.toString());
                }
            });*/


        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

/*        SpotifyApi api = new SpotifyApi();
        api.setAccessToken(((MainActivity) getActivity()).getToken());
        SpotifyService spotify = api.getService();

        spotify.searchArtists("loco", new Callback<ArtistsPager>() {
            @Override
            public void success(ArtistsPager artistsPager, Response response) {
                Log.d(LOG_TAG, artistsPager.toString());
                Pager<Artist> artists = artistsPager.artists;
                for (Artist artist : artists.items) {
                    Log.d(LOG_TAG, "name: " + artist.name);
                    Log.d(LOG_TAG, "followers: " + artist.popularity);
                }
                setListAdapter(artistsPager);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });*/
    }

    @Override
    public void processFinish(ArtistsPager artistsPager) {
        Pager<Artist> artists = artistsPager.artists;
        for (Artist artist : artists.items) {
            Log.d(LOG_TAG, "name: " + artist.name);
            Log.d(LOG_TAG, "followers: " + artist.popularity);
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
