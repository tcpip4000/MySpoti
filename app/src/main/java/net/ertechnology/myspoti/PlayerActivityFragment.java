package net.ertechnology.myspoti;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlayerActivityFragment extends Fragment {

    private static final String LOG_TAG = PlayerActivityFragment.class.getSimpleName();
    private String mArtistId;
    private ArrayList<MyTrack> mTrackList;
    private String mArtistName;
    private String mTrackId;
    private MyTrack mTrack;
    private static MediaPlayer mMediaPlayer;

    public PlayerActivityFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mArtistId = getActivity().getIntent().getStringExtra(PlayerActivity.PLAYER_ARTIST_ID);
        mArtistName = getActivity().getIntent().getStringExtra(PlayerActivity.PLAYER_ARTIST_NAME);
        mTrackList = getActivity().getIntent().getParcelableArrayListExtra(PlayerActivity.PLAYER_TRACKS);
        mTrackId = getActivity().getIntent().getStringExtra(PlayerActivity.PLAYER_TRACK_ID);
        mTrack = findTrack(mTrackId, mTrackList);
        mMediaPlayer = new MediaPlayer();
    }

    private MyTrack findTrack(String trackId, ArrayList<MyTrack> trackList) {
        for (int i = 0; i < trackList.size(); i++) {
            if (trackId.equals(trackList.get(i).getId())) {
                return trackList.get(i);
            }
        }
        return null;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Log.d(LOG_TAG, mArtistId);
        //Log.d(LOG_TAG, mTrackList.toString());
        View view = inflater.inflate(R.layout.fragment_player, container, false);
        ViewHolder viewHolder;

        if (view.getTag() == null) {
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }   else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.playerArtist.setText(mArtistName);
        viewHolder.playerSong.setText(mTrack.getName());

        Picasso.with(getActivity())
                .load(mTrack.getImages().get(0))
                .into(viewHolder.playerImage);

        PlayerTask playerTask = new PlayerTask();
        playerTask.execute(mTrack.getPreviewUrl());

        return view;
    }

    private static class PlayerTask extends AsyncTask<String, Void, Void> {

        //AsyncResponse delegate;

        @Override
        protected Void doInBackground(String... params) {
            String url = params[0];

            try {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mMediaPlayer.setDataSource(url);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            } catch (IllegalArgumentException e) {
                Log.d(LOG_TAG, "Error", e);
            } catch (IOException e) {
                Log.d(LOG_TAG, "Error", e);
            }

            return  null;
        }

/*        @Override
        protected void onPostExecute(ArrayList<MyTrack> myTracks) {
            delegate.processFinish(myTracks);
        }*/
    }

    @Override
    public void onDestroy() {
        mMediaPlayer.release();
        mMediaPlayer = null;
        super.onDestroy();
    }

    private static class ViewHolder {
        final TextView playerArtist;
        final TextView playerSong;
        final ImageView playerImage;
        final ProgressBar playerProgressBar;
        final ImageButton playerBack;
        final ImageButton playerPlayStop;
        final ImageButton playerNext;

        public ViewHolder(View view) {
            playerArtist = (TextView) view.findViewById(R.id.player_artist);
            playerSong = (TextView) view.findViewById(R.id.player_song);
            playerImage  = (ImageView) view.findViewById(R.id.player_image);
            playerProgressBar  = (ProgressBar) view.findViewById(R.id.player_progressBar);
            playerBack  = (ImageButton) view.findViewById(R.id.player_back);
            playerPlayStop  = (ImageButton) view.findViewById(R.id.player_play_stop);
            playerNext = (ImageButton) view.findViewById(R.id.player_next);
        }
    }
}
