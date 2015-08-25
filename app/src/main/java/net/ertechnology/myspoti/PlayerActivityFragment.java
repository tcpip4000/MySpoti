package net.ertechnology.myspoti;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
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

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


/**
 * A placeholder fragment containing a simple view.
 */
public class PlayerActivityFragment extends Fragment {

    private static final String LOG_TAG = PlayerActivityFragment.class.getSimpleName();
    private static final String PLAYER_PREVIOUS_PLAY = "PLAYPPLAY";
    private static final String PLAYER_FIRST_TIME = "PLAYERFTIME";

    public static final String NOTIFICATION = "net.ertechnology.myspoti.service.receiver";
    public static final String COMMAND = "COMMAND";
    public static final String RESULT = "RESULT";
    public static final String CMD_END_SONG = "CMD_END_SONG";
    public static final String CMD_LENGTH_SONG = "CMD_LENGTH_SONG";
    private PlayReceiver mPlayReceiver;

    private static final String PLAYER_ARTIST = "PLAYART";
    private static final String PLAYER_LIST = "PLAYLST";
    private static final String PLAYER_TRACK_ID = "PLAYIND";
    private static final String PLAYER_SERVICE = "PLAYSER";
    private static final String PLAYER_BOUND = "PLAYBOU";
    private static final String PLAYER_CONN = "PLAYCON";

    private ArrayList<MyTrack> mTrackList;
    private String mArtistName;
    private MyTrack mTrack;
    private int mIndex;
    private static MediaPlayer sMediaPlayer;
    private boolean sPreviousPlaying = true;
    private boolean sFirstTime = true;
    private Handler mHandler;

    PlayerService4 mService = null;
    boolean mBound = false;

    public PlayerActivityFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();

        IntentFilter playFilter = new IntentFilter(PlayerActivityFragment.NOTIFICATION);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mPlayReceiver, playFilter);

        PlayerService4.bindService(getActivity(), mConnection);

        //PlayerService.playPlayerService(getActivity(), mConnection);
        //getActivity().bindService(new Intent(getActivity(), PlayerService3.class), mConnection,
        //        Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(PLAYER_PREVIOUS_PLAY, sPreviousPlaying);
        outState.putBoolean(PLAYER_FIRST_TIME, sFirstTime);
        /*outState.putString(PLAYER_ARTIST, mArtistName);
        outState.putParcelableArrayList(PLAYER_LIST, mTrackList);
        outState.putString(PLAYER_TRACK_ID, mTrack.getId());
        outState.putParcelable(PLAYER_SERVICE, mService);
        outState.putBoolean(PLAYER_BOUND, mBound);*/
        super.onSaveInstanceState(outState);
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
        mPlayReceiver = new PlayReceiver();
        mHandler = new Handler();

        if (savedInstanceState != null) {
            sPreviousPlaying = savedInstanceState.getBoolean(PLAYER_PREVIOUS_PLAY);
            sFirstTime = savedInstanceState.getBoolean(PLAYER_FIRST_TIME);
        } else {
            sPreviousPlaying = true;
            sFirstTime = true;
        }

//        PlayerService4.bindService(getActivity(), mConnection);

/*        getActivity().bindService(new Intent(getActivity(), PlayerService4.class),
                mConnection,
                Context.BIND_AUTO_CREATE);*/
        //PlayerService.playPlayerService(getActivity(), mConnection, mTrack.getPreviewUrl());



        //sIsPrepared = false;

        /*if (savedInstanceState == null) {
            mArtistName = getActivity().getIntent().getStringExtra(PlayerActivity.PLAYER_ARTIST_NAME);
            mTrackList = getActivity().getIntent().getParcelableArrayListExtra(PlayerActivity.PLAYER_TRACKS);
            String trackId = getActivity().getIntent().getStringExtra(PlayerActivity.PLAYER_TRACK_ID);
            PlayerResponse pr = findTrack(trackId, mTrackList);
            mTrack = pr.mTrack;

            getActivity().bindService(new Intent(getActivity(), PlayerService3.class), mConnection,
                    Context.BIND_AUTO_CREATE);
            //PlayerService.playPlayerService(getActivity(), mConnection, mTrack.getPreviewUrl());

            mIndex = pr.mIndex;
            sMediaPlayer = new MediaPlayer();
            sIsPrepared = false;
            mHandler = new Handler();
            sPreviousPlaying = true;
            mIsPlaying = false;
        } else {
            mArtistName = savedInstanceState.getString(PLAYER_ARTIST);
            mTrackList = savedInstanceState.getParcelableArrayList(PLAYER_LIST);
            String trackId = savedInstanceState.getString(PLAYER_TRACK_ID);
            PlayerResponse pr = findTrack(trackId, mTrackList);
            mTrack = pr.mTrack;
            mIndex = pr.mIndex;

            getActivity().bindService(new Intent(getActivity(), PlayerService3.class), mConnection,
                    Context.BIND_AUTO_CREATE);
            //PlayerService.playPlayerService(getActivity(), mConnection, mTrack.getPreviewUrl());

            sMediaPlayer = new MediaPlayer();
            sIsPrepared = false;
            mHandler = new Handler();
            sPreviousPlaying = true;
            //mIsPlaying = false;
            mService = savedInstanceState.getParcelable(PLAYER_SERVICE);
            mBound = savedInstanceState.getBoolean(PLAYER_BOUND);
        }*/
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
        //enableButtons(false, view);
        //asyncPlay(false);

        // Listeners
        viewHolder.playerPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBound) {
                    if (mService.isPlaying()) {
                        mService.pause();
                        sPreviousPlaying = false;
                        viewHolder.playerPlayPause.setImageResource(android.R.drawable.ic_media_play);
                    } else {
                        if (mService.isPrepared()) {
                            mService.start();
                        } else {
                            //enableButtons(false, null);
                            mService.play(mTrack.getPreviewUrl());
                        }
                        sPreviousPlaying = true;
                        viewHolder.playerPlayPause.setImageResource(android.R.drawable.ic_media_pause);
                    }
                }
            }
        });

        viewHolder.playerNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*int indexOld = mIndex;
                mIndex = getNextIndex();
                if (mIndex != indexOld) {
                    if (sMediaPlayer.isPlaying()) {
                        sPreviousPlaying = true;
                        sMediaPlayer.stop();
                    } else {
                        sPreviousPlaying = false;
                    }
                    sMediaPlayer.reset();
                    sIsPrepared = false;
                    mTrack = mTrackList.get(mIndex);
                    if (getView() != null && getView().getTag() != null) {
                        updateView((ViewHolder) getView().getTag());
                    }
                    enableButtons(false, null);
                    asyncPlay(true);
                }*/
            }
        });

        viewHolder.playerBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*int indexOld = mIndex;
                mIndex = getPrevIndex();
                if (mIndex != indexOld) {
                    if (sMediaPlayer.isPlaying()) {
                        sPreviousPlaying = true;
                        sMediaPlayer.stop();
                    } else {
                        sPreviousPlaying = false;
                    }
                    sMediaPlayer.reset();
                    sIsPrepared = false;
                    mTrack = mTrackList.get(mIndex);
                    if (getView() != null && getView().getTag() != null) {
                        updateView((ViewHolder) getView().getTag());
                    }
                    enableButtons(false, null);
                    asyncPlay(true);
                }*/
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
        if (sPreviousPlaying) {
            viewHolder.playerPlayPause.setImageResource(android.R.drawable.ic_media_pause);
        } else {
            viewHolder.playerPlayPause.setImageResource(android.R.drawable.ic_media_play);
        }
    }

    private void updateViewSongPaused() {
        ViewHolder viewHolder;
        if (getView() != null && (viewHolder = (ViewHolder) getView().getTag()) != null) {
            viewHolder.playerPlayPause.setImageResource(android.R.drawable.ic_media_play);
        }
    }

    private void updateViewDuration(int totalTime) {
        final ViewHolder viewHolder;
        if (getView() != null && (viewHolder = (ViewHolder) getView().getTag()) != null) {
            viewHolder.playerProgressBar.setMax(totalTime / 1000);
            viewHolder.playerTotalTime.setText(ConvertToMinSec(totalTime));

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mService.isNotNull()) {
                        int currentPosition = mService.getCurrentPosition();
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
/*
    private void asyncPlay(boolean changedSong) {
        PlayerTask playerTask = new PlayerTask();
        playerTask.delegate = (AsyncResponseMediaPlayer) getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment);
        playerTask.execute(mTrack.getPreviewUrl(), changedSong);
    }*/

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

    /*@Override
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
    }*/

    private String ConvertToMinSec(int milliseconds) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(milliseconds),
                TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds))
                );
    }

 /*   private static class PlayerTask extends AsyncTask<Object, Void, Integer> {

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
                    if (sPreviousPlaying) {
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
    }*/

    @Override
    public void onDestroy() {
        sMediaPlayer.release();
        sMediaPlayer = null;
      /*  if (mService.isPlaying()) {
            mService.pause();
        }
        mService.stopSelf();*/
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mPlayReceiver);
        Log.d(LOG_TAG, "STOPPING");
        super.onDestroy();
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //mService= new Messenger(service);
            PlayerService4.LocalBinder binder = (PlayerService4.LocalBinder) service;
            mService = binder.getService();
            mBound = true;

            Log.d(LOG_TAG, "Firstime:" + Boolean.toString(sFirstTime));

            if (sFirstTime) {
                if (mService.isPrepared()) {
                    mService.stop();
                    mService.reset();
                }
                mService.play(mTrack.getPreviewUrl()); // Play the first time
                sFirstTime = false;
            } else {
                updateViewDuration(mService.getDuration());
            }

            //Log.d(LOG_TAG, "mBound data:" + mService.data.toString());
            Log.d(LOG_TAG, "mBound onServiceConnected:" + Boolean.toString(mBound));
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            //mService = null;
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

    public class PlayReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int result;
            String command = intent.getStringExtra(PlayerActivityFragment.COMMAND);
            Log.d(LOG_TAG, "RECEIVED COMMAND: " + command);
            switch (command) {
                case CMD_END_SONG:
                    updateViewSongPaused();
                    break;
                case CMD_LENGTH_SONG:
                    updateViewDuration(mService.getDuration());
                    break;
                default:
                    Log.d(LOG_TAG, "Local Receiver command unknown:" + command);
            }
        }
    }

}
