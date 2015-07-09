package net.ertechnology.myspoti;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.RetrofitError;


/**
 * A placeholder fragment containing a simple view.
 */
public class HitActivityFragment extends Fragment implements AsyncResponse {

    private static final String LOG_TAG = HitActivityFragment.class.getSimpleName();

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
    // http://stackoverflow.com/questions/12503836/how-to-save-custom-arraylist-on-android-screen-rotate
    @Override
    public void processFinish(List<Track> tracks) {
        if (tracks != null) {
            try {
                ListView listView = (ListView) getView().findViewById(R.id.hit_listview);
                HitAdapter mAdapter = new HitAdapter(getActivity(), tracks);
                listView.setAdapter(mAdapter);
                if (tracks.size() == 0) {
                    Toast.makeText(getActivity(), R.string.track_not_found, Toast.LENGTH_SHORT).show();
                }
            } catch (NullPointerException e) {
                Log.e(LOG_TAG, "Error", e);
            }
        } else {
            Log.d(LOG_TAG, "Null track list received");
        }
    }

    private static class GetTrackListTask extends AsyncTask<String, Void, List<Track>> {

        AsyncResponse delegate;

        @Override
        protected List<Track> doInBackground(String... params) {
            Tracks tracksObject;
            List<Track> tracks;
            try {
                SpotifyService spotifyService = MySession.getInstance().getSpotifyService();
                String artistId = params[0];
                Map<String, Object> map = new HashMap<>();
                map.put("country", "US");
                tracksObject = spotifyService.getArtistTopTrack(artistId, map);
                tracks = tracksObject.tracks;
            } catch (RetrofitError e) {
                Log.e(LOG_TAG, "Error", e);
                tracks = null;
            }
            return tracks;
        }

        @Override
        protected void onPostExecute(List<Track> tracks) {
            delegate.processFinish(tracks);
        }
    }
}
