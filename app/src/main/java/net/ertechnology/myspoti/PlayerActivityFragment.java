package net.ertechnology.myspoti;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
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
import java.util.concurrent.TimeUnit;

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

    PlayerService mService;
    boolean mBound = false;

    public PlayerActivityFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();

        PlayerService.startActionPlay(getActivity(), mConnection, "param1", "param2");
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
        mPreviousPlaying = true;
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
        enableButtons(false, view);
        asyncPlay(false);

/*        Log.d(LOG_TAG, "mBound onCreateView:" + Boolean.toString(mBound));
        if (mBound) {
            boolean data = mService.isPlaying();
            Log.d(LOG_TAG, "is playing onCreateView:" + Boolean.toString(data));
        }*/

        // Listeners
        viewHolder.playerPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(LOG_TAG, "mBound setOnClickListener: " + Boolean.toString(mBound));
                if (mBound) {
                    boolean data = mService.isPlaying();
                    Log.d(LOG_TAG, "is playing onCreateView: " + Boolean.toString(data));
                }

                if (sMediaPlayer.isPlaying()) {
                    sMediaPlayer.pause();

                    viewHolder.playerPlayPause.setImageResource(android.R.drawable.ic_media_play);
                } else {
                    if (!sIsPrepared) {
                        enableButtons(false, null);
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
                    if (getView() != null && getView().getTag() != null) {
                        updateView((ViewHolder) getView().getTag());
                    }
                    enableButtons(false, null);
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
                    if (getView() != null && getView().getTag() != null) {
                        updateView((ViewHolder) getView().getTag());
                    }
                    enableButtons(false, null);
                    asyncPlay(true);
                }
            }
        });

        return view;
    }

    /**
     * Called when the Fragment is no longer started.  This is generally
     * tied to {@link Activity#onStop() Activity.onStop} of the containing
     * Activity's lifecycle.
     */
    @Override
    public void onStop() {
        super.onStop();
        if (mBound) {
            getActivity().unbindService(mConnection);
            mBound = false;
            Log.d(LOG_TAG, "mBound onStop:" + Boolean.toString(mBound));
        }
    }

    private void updateView(ViewHolder viewHolder) {
        viewHolder.playerAlbum.setText(mTrack.getAlbumName());
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

    private void asyncPlay(boolean changedSong) {
        PlayerTask playerTask = new PlayerTask();
        playerTask.delegate = (AsyncResponseMediaPlayer) getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment);
        playerTask.execute(mTrack.getPreviewUrl(), changedSong);
    }

    private void enableButtons(boolean enable, View view) {
        ViewHolder viewHolder;
        if (view == null) {
            if (getView() != null && (viewHolder = (ViewHolder) getView().getTag()) != null) {
                viewHolder.playerBack.setEnabled(enable);
                viewHolder.playerPlayPause.setEnabled(enable);
                viewHolder.playerNext.setEnabled(enable);
            }
        } else {
            viewHolder = (ViewHolder) view.getTag();
            viewHolder.playerBack.setEnabled(enable);
            viewHolder.playerPlayPause.setEnabled(enable);
            viewHolder.playerNext.setEnabled(enable);
        }
    }

    @Override
    public void processFinish(Integer msg) {
        final ViewHolder viewHolder;
        if (getView() != null && (viewHolder = (ViewHolder) getView().getTag()) != null && msg == 0) {

            int totalTime = sMediaPlayer.getDuration() ;
            viewHolder.playerProgressBar.setMax(totalTime / 1000);
            viewHolder.playerTotalTime.setText(ConvertToMinSec(totalTime));

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (sMediaPlayer != null) {
                        int currentPosition = sMediaPlayer.getCurrentPosition();
                        viewHolder.playerProgressBar.setProgress(currentPosition / 1000);
                        viewHolder.playerCurrentTime.setText(ConvertToMinSec(currentPosition));
                    }
                    mHandler.postDelayed(this, 1000);
                }
            });
        } else {
            Log.e(LOG_TAG, "Error setting progress bar");
        }
        enableButtons(true, null);
    }

    private String ConvertToMinSec(int milliseconds) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(milliseconds),
                TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds))
                );
    }

    private static class PlayerTask extends AsyncTask<Object, Void, Integer> {

        AsyncResponseMediaPlayer delegate;

        @Override
        protected Integer doInBackground(Object... params) {
            String url = (String) params[0];
            boolean changedSong = (boolean) params[1];
            int status = 0;

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

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PlayerService.LocalBinder binder = (PlayerService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
            Log.d(LOG_TAG, "mBound onServiceConnected:" + Boolean.toString(mBound));
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
            Log.d(LOG_TAG, "mBound onServiceDisconnected:" + Boolean.toString(mBound));

        }
    };

    private static class ViewHolder {
        final TextView playerArtist;
        final TextView playerAlbum;
        final TextView playerSong;
        final ImageView playerImage;
        final SeekBar playerProgressBar;
        final ImageButton playerBack;
        final ImageButton playerPlayPause;
        final ImageButton playerNext;
        final TextView playerCurrentTime;
        final TextView playerTotalTime;


        public ViewHolder(View view) {
            playerArtist = (TextView) view.findViewById(R.id.player_artist);
            playerAlbum = (TextView) view.findViewById(R.id.player_album);
            playerSong = (TextView) view.findViewById(R.id.player_song);
            playerImage  = (ImageView) view.findViewById(R.id.player_image);
            playerProgressBar  = (SeekBar) view.findViewById(R.id.player_progressBar);
            playerBack  = (ImageButton) view.findViewById(R.id.player_back);
            playerPlayPause = (ImageButton) view.findViewById(R.id.player_play_stop);
            playerNext = (ImageButton) view.findViewById(R.id.player_next);
            playerCurrentTime = (TextView) view.findViewById(R.id.player_current_time);
            playerTotalTime = (TextView) view.findViewById(R.id.player_total_time);
        }
    }

}
