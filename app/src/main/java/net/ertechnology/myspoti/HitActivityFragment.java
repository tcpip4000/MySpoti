package net.ertechnology.myspoti;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.RetrofitError;


/**
 * A placeholder fragment containing a simple view.
 */
public class HitActivityFragment extends Fragment implements AsyncResponse {

    private static final String HIT_ARTIST_ID = "HIT_ARTIST_ID";
    private static final String LOG_TAG = HitActivityFragment.class.getSimpleName();
    private static final String HIT_ACTIVITY_ARRAY = "HIT_ACTIVITY_ARRAY";
    private static final String HIT_ARTIST_NAME = "HIT_ARTIST_NAME";
    private HitAdapter mHitAdapter;
    private ArrayList<MyTrack> mTrackList;
    private HitActivityListener mCallback;
    private String mArtistId;
    private String mArtistName;

    public HitActivityFragment() {
    }

    public static HitActivityFragment newInstance(String artistId, String artistName) {
        HitActivityFragment hitActivityFragment = new HitActivityFragment();

        Bundle args = new Bundle();
        args.putString(HIT_ARTIST_ID, artistId);
        args.putString(HIT_ARTIST_NAME, artistName);
        hitActivityFragment.setArguments(args);

        return hitActivityFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (HitActivityListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.getLocalClassName() +
                    " activity does not implement HitActivityListener");
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mArtistId = getArguments().getString(HIT_ARTIST_ID);
        mArtistName = getArguments().getString(HIT_ARTIST_NAME);
    }

    public interface HitActivityListener {
        void hitListener(ArrayList<MyTrack> myTrack, String trackId, String artistName);
    }

   /* private String getArtistId() {
        if (getArguments() != null) {
            return getArguments().getString(HIT_ARTIST_ID);
        } else {
            return  null;
        }
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hit, container, false);
        ViewHolder viewHolder;

        if (view.getTag() == null) {
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getResources().getString(R.string.title_activity_hit));
        }

        if (savedInstanceState == null) {
            //String artistId = getArtistId();
            if (mArtistId != null) {
                Log.d(LOG_TAG, "Received id:" + mArtistId);
                GetTrackListTask getTrackListTask = new GetTrackListTask();
                getTrackListTask.delegate = this;
                getTrackListTask.execute(mArtistId);
            }
        } else {
            mTrackList = savedInstanceState.getParcelableArrayList(HIT_ACTIVITY_ARRAY);
            if (mTrackList != null) {
                mHitAdapter = new HitAdapter(getActivity(), mTrackList);
                viewHolder.listView.setAdapter(mHitAdapter);
            }
        }

        viewHolder.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyTrack myTrack = mHitAdapter.getItem(position);
                mCallback.hitListener(mTrackList, myTrack.getId(), mArtistName);
            }
        });

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
                if (getView() != null) {
                    ViewHolder viewHolder = (ViewHolder) getView().getTag();
                    mHitAdapter = new HitAdapter(getActivity(), mTrackList);
                    viewHolder.listView.setAdapter(mHitAdapter);
                    if (tracks.size() == 0) {
                        Toast.makeText(getActivity(), R.string.track_not_found, Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (NullPointerException e) {
                Log.e(LOG_TAG, "Error", e);
            }
        } else {
            Log.d(LOG_TAG, "Network connection problem");
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
                String defaultCountry = Locale.getDefault().getCountry();
                if (defaultCountry.isEmpty()) {
                    defaultCountry = "US";
                }
                map.put("country", defaultCountry);
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

    private class ViewHolder {
        final ListView listView;

        public ViewHolder(View view) {
            listView = (ListView) view.findViewById(R.id.hit_listview);
        }
    }
}
