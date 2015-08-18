package net.ertechnology.myspoti;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlayerActivityFragment extends Fragment {

    private static final String LOG_TAG = PlayerActivityFragment.class.getSimpleName();
    private String mArtistId;
    private MyTrack mTrack;

    public PlayerActivityFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mArtistId = getActivity().getIntent().getStringExtra(PlayerActivity.PLAYER_ARTIST_ID);
        //mTrack = getActivity().getIntent().getParcelableExtra(PlayerActivity.PLAYER_TRACK);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(LOG_TAG, mArtistId);
        //Log.d(LOG_TAG, mTrack.toString());
        return inflater.inflate(R.layout.fragment_player, container, false);
    }
}
