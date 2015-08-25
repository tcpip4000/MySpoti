package net.ertechnology.myspoti;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.Date;


public class PlayerService4 extends Service {

    static final int MSG_SAY_HELLO = 1;
    static final int MSG_SAY_PAUSE = 2;
    public static final Date mDate = new Date();
    private static final String LOG_TAG = PlayerService4.class.getSimpleName();
    private static MediaPlayer sMediaPlayer = new MediaPlayer();
    //private static String sUrl;
    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        PlayerService4 getService() {
            return PlayerService4.this;
        }
    }

    class IncomingHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SAY_HELLO:
                    Toast.makeText(getApplicationContext(), "hello", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_SAY_PAUSE:
                    sMediaPlayer.pause();
                default:
                    super.handleMessage(msg);
            }
        }
    }

    //final Messenger mMessenger = new Messenger(new IncomingHandler());

    public String helloWorld(String msg) {
        return  mDate.toString();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Toast.makeText(getApplicationContext(), "binding", Toast.LENGTH_SHORT).show();
        Log.d(LOG_TAG, ">>>>>>>>>>>>>>>>DATE: " + mDate.toString());
        return mBinder;
    }

    public void start() {
        sMediaPlayer.start();
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

    private static class PlayerTask extends AsyncTask<String, Void, Integer> {

        //AsyncResponseMediaPlayer delegate;

        @Override
        protected Integer doInBackground(String... params) {
            String url = params[0];
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
                //sIsPrepared = true;
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
