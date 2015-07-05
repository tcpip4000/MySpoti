package net.ertechnology.myspoti;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.callback.Callback;

import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Album;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import kaaes.spotify.webapi.android.models.TracksPager;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * A placeholder fragment containing a simple view.
 */
public class HitActivityFragment extends Fragment {

    private static String LOG_TAG = HitActivityFragment.class.getSimpleName();
    private HitAdapter mAdapter;

    public HitActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hit, container, false);

        SpotifyService spotifyService = MySession.getInstance().getSpotifyService();
        Log.d(LOG_TAG, "ARTIST:" + ((HitActivity) getActivity()).mArtistId);
        Map<String, Object> map = new HashMap<>();
        map.put("country", "US");
        spotifyService.getArtistTopTrack(((HitActivity) getActivity()).mArtistId, map, new retrofit.Callback<Tracks>() {
            @Override
            public void success(Tracks tracks, Response response) {
                Log.d(LOG_TAG, response.getReason());
                Log.d(LOG_TAG, tracks.toString());
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(LOG_TAG, error.toString());

            }
        });

/*        ListView listView = (ListView) view.findViewById(R.id.hit_listview);
        mAdapter = new HitAdapter(getActivity(), new ArrayList<>(Artist));
        listView.setAdapter(mAdapter);*/

        return view;
    }
}
