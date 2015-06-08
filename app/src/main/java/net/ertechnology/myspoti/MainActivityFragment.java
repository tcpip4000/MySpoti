package net.ertechnology.myspoti;

import android.app.Fragment;
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
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Pager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private static final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    private SimpleAdapter mSimpleAdapter;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        // Set adapter
        ListView listView = (ListView) view.findViewById(R.id.main_listview);
        ArrayList<Map<String, String>> data = new ArrayList<>();
        ///
        HashMap<String, String> map = new HashMap<>();
        HashMap<String, String> map2 = new HashMap<>();
        map.put("image", "image1");
        map.put("description", "Soul Fly");
        map2.put("image", "image2");
        map2.put("description", "Ramones");
        data.add(map);
        data.add(map2);
        ///
        String[] from = new String[] {"image", "description"};
        int[] to = new int[]{R.id.list_item_image, R.id.list_item_description};
        mSimpleAdapter = new SimpleAdapter(getActivity(), data, R.layout.list_item_data, from, to);
        listView.setAdapter(mSimpleAdapter);

        // Search function
        EditText search = (EditText) view.findViewById(R.id.main_search);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                Filter filter = mSimpleAdapter.getFilter();
                filter.filter(s.toString());
                //Log.d("s is: ", s.toString());

            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //////
        SpotifyApi api = new SpotifyApi();
        api.setAccessToken(((MainActivity) getActivity()).getToken());

        SpotifyService spotify = api.getService();
/*                    spotify.getAlbum("2dIGnmEIy1WZIcZCFSj6i8", new Callback<Album>() {
                        @Override
                        public void success(Album album, Response response) {
                            Log.d(LOG_TAG, album.name);
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Log.d(LOG_TAG, error.toString());
                        }
                    });*/
        spotify.searchArtists("loco", new Callback<ArtistsPager>() {
            @Override
            public void success(ArtistsPager artistsPager, Response response) {
                Log.d(LOG_TAG, artistsPager.toString());
                Pager<Artist> artists = artistsPager.artists;
                for (Artist artist : artists.items) {
                    Log.d(LOG_TAG, "name: " + artist.name);
                    Log.d(LOG_TAG, "followers: " + artist.popularity);
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
        /////
    }
}
