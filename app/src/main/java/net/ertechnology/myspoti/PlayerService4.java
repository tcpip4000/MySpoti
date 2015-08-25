package net.ertechnology.myspoti;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.Date;


public class PlayerService4 extends Service {

    private static final String LOG_TAG = PlayerService4.class.getSimpleName();

    public static final Date mDate = new Date();

    private static MediaPlayer sMediaPlayer = new MediaPlayer();
    private final IBinder mBinder = new LocalBinder();
    private static boolean sIsPrepared = false;



    public void stop() {
        sMediaPlayer.stop();
    }

    public void reset() {
        sMediaPlayer.reset();
    }

    public class LocalBinder extends Binder {
        PlayerService4 getService() {
            return PlayerService4.this;
        }
    }

    public static void bindService(Context context, ServiceConnection connection) {
        context.bindService(new Intent(context, PlayerService4.class),
                connection,
                Context.BIND_AUTO_CREATE);

    }

    private void publishResults(String command, int result) {
        Intent intent = new Intent(PlayerActivityFragment.NOTIFICATION);
        intent.putExtra(PlayerActivityFragment.COMMAND, command);
        intent.putExtra(PlayerActivityFragment.RESULT, result);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        Log.d(LOG_TAG, "BROADCAST SENDED");
    }

  /*  class IncomingHandler extends Handler {

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
    }*/

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
        sMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                //viewHolder.playerPlayPause.setImageResource(android.R.drawable.ic_media_play);
                publishResults(PlayerActivityFragment.CMD_END_SONG, 1);
            }
        });
    }

    public void pause() {
        sMediaPlayer.pause();
    }

    public boolean isPrepared() {
        return sIsPrepared;
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
