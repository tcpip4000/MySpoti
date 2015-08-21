package net.ertechnology.myspoti;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlayerActivityFragment extends Fragment implements AsyncResponseMediaPlayer {

    private static final String LOG_TAG = PlayerActivityFragment.class.getSimpleName();
    private ArrayList<MyTrack> mTrackList;
    private String mArtistName;
    private MyTrack mTrack;
    private int mIndex;
    private static MediaPlayer sMediaPlayer;
    private static boolean sIsPrepared;
    private Handler mHandler;
    private static boolean mPreviousPlaying;

    public PlayerActivityFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mArtistName = getActivity().getIntent().getStringExtra(PlayerActivity.PLAYER_ARTIST_NAME);
        mTrackList = getActivity().getIntent().getParcelableArrayListExtra(PlayerActivity.PLAYER_TRACKS);
        String trackId = getActivity().getIntent().getStringExtra(PlayerActivity.PLAYER_TRACK_ID);
        PlayerResponse pr = findTrack(trackId, mTrackList);
        mTrack = pr.mTrack;
        mIndex = pr.mIndex;
        sMediaPlayer = new MediaPlayer();
        sIsPrepared = false;
        mHandler = new Handler();
        mPreviousPlaying = false;
    }

    private PlayerResponse findTrack(String trackId, ArrayList<MyTrack> trackList) {
        for (int i = 0; i < trackList.size(); i++) {
            if (trackId.equals(trackList.get(i).getId())) {
                return new PlayerResponse(trackList.get(i), i);
            }
        }
        throw new RuntimeException("Track id not found in list");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player, container, false);
        final ViewHolder viewHolder;

        if (view.getTag() == null) {
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }   else {
            viewHolder = (ViewHolder) view.getTag();
        }

        // Update View
        viewHolder.playerArtist.setText(mArtistName);
        updateView(viewHolder);

        // Start playing for first time
        //asyncPlay(false);

        // Listeners
        viewHolder.playerPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (sMediaPlayer.isPlaying()) {
                    sMediaPlayer.pause();
                    viewHolder.playerPlayPause.setImageResource(android.R.drawable.ic_media_play);
                } else {
                    if (!sIsPrepared) {
                        asyncPlay(false);
                    } else {
                        sMediaPlayer.start();
                    }
                    viewHolder.playerPlayPause.setImageResource(android.R.drawable.ic_media_pause);
                }
            }
        });

        sMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                viewHolder.playerPlayPause.setImageResource(android.R.drawable.ic_media_play);
            }
        });

        viewHolder.playerNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int indexOld = mIndex;
                mIndex = getNextIndex();
                if (mIndex != indexOld) {
                    if (sMediaPlayer.isPlaying()) {
                        mPreviousPlaying = true;
                        sMediaPlayer.stop();
                    } else {
                        mPreviousPlaying = false;
                    }
                    sMediaPlayer.reset();
                    sIsPrepared = false;
                    mTrack = mTrackList.get(mIndex);
                    updateView((ViewHolder) getView().getTag());
                    asyncPlay(true);
                }
            }
        });

        viewHolder.playerBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int indexOld = mIndex;
                mIndex = getPrevIndex();
                if (mIndex != indexOld) {
                    if (sMediaPlayer.isPlaying()) {
                        mPreviousPlaying = true;
                        sMediaPlayer.stop();
                    } else {
                        mPreviousPlaying = false;
                    }
                    sMediaPlayer.reset();
                    sIsPrepared = false;
                    mTrack = mTrackList.get(mIndex);
                    updateView((ViewHolder) getView().getTag());
                    asyncPlay(true);
                }
            }
        });

        return view;
    }

    private void updateView(ViewHolder viewHolder) {
        viewHolder.playerSong.setText(mTrack.getName());
        Picasso.with(getActivity())
                .load(mTrack.getImages().get(0))
                .into(viewHolder.playerImage);
        if (mPreviousPlaying) {
            viewHolder.playerPlayPause.setImageResource(android.R.drawable.ic_media_pause);
        } else {
            viewHolder.playerPlayPause.setImageResource(android.R.drawable.ic_media_play);
        }
    }

    private int getNextIndex() {
        int i = mIndex + 1;
        if (i >= mTrackList.size()) {
            i = mIndex;
            Toast.makeText(getActivity(), "No more songs", Toast.LENGTH_SHORT).show();
        }
        return i;
    }

    private int getPrevIndex() {
        int i = mIndex - 1;
        if (i < 0 ) {
            i = mIndex;
            Toast.makeText(getActivity(), "No more songs", Toast.LENGTH_SHORT).show();
        }
        return i;
    }

    public void asyncPlay(boolean changedSong) {
        PlayerTask playerTask = new PlayerTask();
        playerTask.delegate = (AsyncResponseMediaPlayer) getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment);
        enableButtons(false);
        playerTask.execute(mTrack.getPreviewUrl(), changedSong);
    }

    public void enableButtons(boolean enable) {
        ViewHolder viewHolder;
        if (getView() != null && (viewHolder = (ViewHolder) getView().getTag()) != null) {
            viewHolder.playerBack.setEnabled(enable);
            viewHolder.playerPlayPause.setEnabled(enable);
            viewHolder.playerNext.setEnabled(enable);
        }
    }

    @Override
    public void processFinish(Integer msg) {
        final ViewHolder viewHolder;
        if (getView() != null && (viewHolder = (ViewHolder) getView().getTag()) != null && msg == 0) {

            viewHolder.playerProgressBar.setMax(sMediaPlayer.getDuration() / 1000);

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (sMediaPlayer != null) {
                        int currentPosition = sMediaPlayer.getCurrentPosition() / 1000;
                        viewHolder.playerProgressBar.setProgress(currentPosition);
                    }
                    mHandler.postDelayed(this, 1000);
                }
            });
        } else {
            Log.e(LOG_TAG, "Error setting progress bar");
        }
        enableButtons(true);
    }

    private static class PlayerTask extends AsyncTask<Object, Void, Integer> {

        AsyncResponseMediaPlayer delegate;

        @Override
        protected Integer doInBackground(Object... params) {
            String url = (String) params[0];
            boolean changedSong = (boolean) params[1];
            int status = 0;
            Log.d(LOG_TAG, "Started asyn...");

            try {
                sMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                sMediaPlayer.setDataSource(url);
                sMediaPlayer.prepare();
                if (!changedSong) {
                    sMediaPlayer.start();
                } else {
                    if (mPreviousPlaying) {
                        sMediaPlayer.start();
                    }
                }
                sIsPrepared = true;
            } catch (IllegalArgumentException e) {
                Log.d(LOG_TAG, "Error", e);
                status = 1;
            } catch (IOException e) {
                Log.d(LOG_TAG, "Error", e);
                status = 2;
            }

            return status;
        }

        @Override
        protected void onPostExecute(Integer msg) {
            delegate.processFinish(msg);
        }
    }

    @Override
    public void onDestroy() {
        sMediaPlayer.release();
        sMediaPlayer = null;
        super.onDestroy();
    }

    private static class ViewHolder {
        final TextView playerArtist;
        final TextView playerSong;
        final ImageView playerImage;
        final SeekBar playerProgressBar;
        final ImageButton playerBack;
        final ImageButton playerPlayPause;
        final ImageButton playerNext;

        public ViewHolder(View view) {
            playerArtist = (TextView) view.findViewById(R.id.player_artist);
            playerSong = (TextView) view.findViewById(R.id.player_song);
            playerImage  = (ImageView) view.findViewById(R.id.player_image);
            playerProgressBar  = (SeekBar) view.findViewById(R.id.player_progressBar);
            playerBack  = (ImageButton) view.findViewById(R.id.player_back);
            playerPlayPause = (ImageButton) view.findViewById(R.id.player_play_stop);
            playerNext = (ImageButton) view.findViewById(R.id.player_next);
        }
    }

}
