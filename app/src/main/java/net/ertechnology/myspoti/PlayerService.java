package net.ertechnology.myspoti;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class PlayerService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_PLAY = "net.ertechnology.myspoti.action.PLAY";
    private static final String ACTION_START = "net.ertechnology.myspoti.action.START";
    private static final String ACTION_PAUSE = "net.ertechnology.myspoti.action.PAUSE";

    // TODO: Rename parameters
    private static final String EXTRA_URL = "net.ertechnology.myspoti.extra.PARAM1";
    //private static final String EXTRA_PARAM2 = "net.ertechnology.myspoti.extra.PARAM2";

    private static final String LOG_TAG = PlayerService.class.getSimpleName();
    private static final int SERVICE_NOTIFICATION_ID = 1;


    private final IBinder mBinder = new LocalBinder();
    private static boolean sIsPrepared;
    private static MediaPlayer sMediaPlayer = new MediaPlayer();
    public Date data = new Date();

    @Override
    public void onCreate() {
        super.onCreate();

        sIsPrepared = false;
        sMediaPlayer = new MediaPlayer();
    }

    public static void playPlayerService(Context context, ServiceConnection connection, String url) {
        Intent intent = new Intent(context, PlayerService.class);
        intent.setAction(ACTION_PLAY);
        intent.putExtra(EXTRA_URL, url);
        context.startService(intent);
       // boolean bolean = context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
        //Log.d(LOG_TAG, "Binded to service playPlayerService: " + Boolean.toString(bolean));
    }

    public static void startPlayerService(Context context) {
        Intent intent = new Intent(context, PlayerService.class);
        intent.setAction(ACTION_START);
        context.startService(intent);
    }

    public static void pausePlayerService(Context context) {
        Intent intent = new Intent(context, PlayerService.class);
        intent.setAction(ACTION_PAUSE);
        context.startService(intent);
    }

/*   // Destroys when rotated
    @Override
    public void onDestroy() {
        sMediaPlayer.release();
        sMediaPlayer = null;
        super.onDestroy();
    }*/

    public PlayerService() {
        super("PlayerService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_PLAY.equals(action)) {
                play(intent.getStringExtra(EXTRA_URL));
                Log.d(LOG_TAG, "PLAY");
            } else if (ACTION_START.equals(action)) {
                start();
                Log.d(LOG_TAG, "START");
            } else if (ACTION_PAUSE.equals(action)) {
                pause();
                Log.d(LOG_TAG, "PAUSE");
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
/*    private void handleActionPlay(String param1, String param2) {
        Log.d(LOG_TAG, "Previous Playing:" + Boolean.toString(mIsPlaying));
        mIsPlaying = true;
        Log.d(LOG_TAG, "Playing:" + Boolean.toString(mIsPlaying));
        // TODO: Handle action Foo
        //throw new UnsupportedOperationException("Not yet implemented");
    }*/

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
/*    private void handleActionPause(String param1, String param2) {
        Log.d(LOG_TAG, "Previous Playing:" + Boolean.toString(mIsPlaying));
        mIsPlaying = false;
        Log.d(LOG_TAG, "Playing:" + Boolean.toString(mIsPlaying));
        // TODO: Handle action Baz
        //throw new UnsupportedOperationException("Not yet implemented");
    }*/

    /**
     * Unless you provide binding for your service, you don't need to implement this
     * method, because the default implementation returns null.
     *
     * @param intent
     * @see Service#onBind
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void pause() {
        sMediaPlayer.pause();
    }

    public void start() {
        sMediaPlayer.start();
    }

    public class LocalBinder extends Binder {
        PlayerService getService() {
            return PlayerService.this;
        }
    }

    public boolean isPlaying() {
        return sMediaPlayer.isPlaying();
    }

    public void play(String url) {
        asyncPlay(url);
    }

    private void asyncPlay(String url) {
        Log.d(LOG_TAG, "ASYNC STARTED playing url:" + url);
        PlayerTask playerTask = new PlayerTask();
        //playerTask.delegate = (AsyncResponseMediaPlayer) getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment);
        playerTask.execute(url);
        //mTrack.getPreviewUrl()
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

    private static class PlayerTask extends AsyncTask<Object, Void, Integer> {

        //AsyncResponseMediaPlayer delegate;

        @Override
        protected Integer doInBackground(Object... params) {
            String url = (String) params[0];
            //boolean changedSong = (boolean) params[1];
            int status = 0;

            try {
                sMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                sMediaPlayer.setDataSource(url);
                sMediaPlayer.prepare();
                sMediaPlayer.start();

                /*if (!changedSong) {
                    sMediaPlayer.start();
                } else {
                    if (mPreviousPlaying) {
                        sMediaPlayer.start();
                    }
                }*/
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

/*        @Override
        protected void onPostExecute(Integer msg) {
            delegate.processFinish(msg);
        }*/
    }

}
