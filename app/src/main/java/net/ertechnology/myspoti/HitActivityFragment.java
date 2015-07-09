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

import java.util.ArrayList;
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
    private static final String HIT_ACTIVITY_ARRAY = "HIT_ACTIVITY_ARRAY";
    private ArrayList<MyTrack> mTrackList;

    public HitActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hit, container, false);

        if (savedInstanceState == null) {
            Log.d(LOG_TAG, "ARTIST:" + ((HitActivity) getActivity()).mArtistId);
            GetTrackListTask getTrackListTask = new GetTrackListTask();
            getTrackListTask.delegate = this;
            getTrackListTask.execute(((HitActivity) getActivity()).mArtistId);
        } else {
            mTrackList = savedInstanceState.getParcelableArrayList(HIT_ACTIVITY_ARRAY);
            ListView listView = (ListView) view.findViewById(R.id.hit_listview);
            HitAdapter mAdapter = new HitAdapter(getActivity(), mTrackList);
            listView.setAdapter(mAdapter);
        }

        return view;
    }

    /**
     * Called to ask the fragment to save its current dynamic state, so it
     * can later be reconstructed in a new instance of its process is
     * restarted.  If a new instance of the fragment later needs to be
     * created, the data you place in the Bundle here will be available
     * in the Bundle given to {@link #onCreate(Bundle)},
     * {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}, and
     * {@link #onActivityCreated(Bundle)}.
     * <p/>
     * <p>This corresponds to {@link Activity#onSaveInstanceState(Bundle)
     * Activity.onSaveInstanceState(Bundle)} and most of the discussion there
     * applies here as well.  Note however: <em>this method may be called
     * at any time before {@link #onDestroy()}</em>.  There are many situations
     * where a fragment may be mostly torn down (such as when placed on the
     * back stack with no UI showing), but its state will not be saved until
     * its owning activity actually needs to save its state.
     *
     * @param outState Bundle in which to place your saved state.
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(HIT_ACTIVITY_ARRAY, mTrackList);
        super.onSaveInstanceState(outState);
    }

    // http://stackoverflow.com/questions/12503836/how-to-save-custom-arraylist-on-android-screen-rotate
    @Override
    public void processFinish(ArrayList<MyTrack> tracks) {
        if (tracks != null) {
            try {
                mTrackList = tracks;
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

    private static class GetTrackListTask extends AsyncTask<String, Void, ArrayList<MyTrack>> {

        AsyncResponse delegate;

        @Override
        protected ArrayList<MyTrack> doInBackground(String... params) {
            Tracks tracksObject;
            List<Track> tracks;
            ArrayList<MyTrack> myTracks;
            try {
                SpotifyService spotifyService = MySession.getInstance().getSpotifyService();
                String artistId = params[0];
                Map<String, Object> map = new HashMap<>();
                map.put("country", "US");
                tracksObject = spotifyService.getArtistTopTrack(artistId, map);
                tracks = tracksObject.tracks;
                myTracks = MyTrack.create(tracks);
            } catch (RetrofitError e) {
                Log.e(LOG_TAG, "Error", e);
                myTracks = null;
            }
            return myTracks;
        }

        @Override
        protected void onPostExecute(ArrayList<MyTrack> myTracks) {
            delegate.processFinish(myTracks);
        }
    }
}
