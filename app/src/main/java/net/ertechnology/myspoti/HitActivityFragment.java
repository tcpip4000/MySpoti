package net.ertechnology.myspoti;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;


/**
 * A placeholder fragment containing a simple view.
 */
public class HitActivityFragment extends Fragment implements AsyncResponse {

    private static String LOG_TAG = HitActivityFragment.class.getSimpleName();
    private HitAdapter mAdapter;

    public HitActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hit, container, false);

        Log.d(LOG_TAG, "ARTIST:" + ((HitActivity) getActivity()).mArtistId);
        GetTrackListTask getTrackListTask = new GetTrackListTask();
        getTrackListTask.delegate = this;
        getTrackListTask.execute(((HitActivity) getActivity()).mArtistId);

        return view;
    }

    @Override
    public void processFinish(List<Track> tracks) {
        ListView listView = (ListView) getView().findViewById(R.id.hit_listview);
        mAdapter = new HitAdapter(getActivity(), tracks);
        listView.setAdapter(mAdapter);
    }

    private static class GetTrackListTask extends AsyncTask<String, Void, List<Track>> {

        AsyncResponse delegate;

        @Override
        protected List<Track> doInBackground(String... params) {
            Tracks tracks;
            SpotifyService spotifyService = MySession.getInstance().getSpotifyService();
            String artistId = params[0];
            Map<String, Object> map = new HashMap<>();
            map.put("country", "US");
            tracks = spotifyService.getArtistTopTrack(artistId, map);
            return tracks.tracks;
        }

        @Override
        protected void onPostExecute(List<Track> tracks) {
            delegate.processFinish(tracks);
        }
    }
}
