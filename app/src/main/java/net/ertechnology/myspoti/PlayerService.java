package net.ertechnology.myspoti;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

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
    private static final String ACTION_PAUSE = "net.ertechnology.myspoti.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "net.ertechnology.myspoti.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "net.ertechnology.myspoti.extra.PARAM2";

    private static final String LOG_TAG = PlayerService.class.getSimpleName();
    private static final int SERVICE_NOTIFICATION_ID = 1;

    private boolean mIsPlaying;
    private final IBinder mBinder = new LocalBinder();

    @Override
    public void onCreate() {
        super.onCreate();

/*        Notification notification = new Notification.Builder(getBaseContext())
                .setContentTitle("playerx")
                .setContentText("sing a song")
                .build();
        startForeground(SERVICE_NOTIFICATION_ID, notification);*/

        mIsPlaying = false;

    }


    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionPlay(Context context, ServiceConnection connection, String param1, String param2) {
        Intent intent = new Intent(context, PlayerService.class);
        intent.setAction(ACTION_PLAY);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        boolean bolean = context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
        Log.d(LOG_TAG, "Binded to service startActionPlay: " + Boolean.toString(bolean));
    }

    /*public static void startActionPlay(Context context, String param1, String param2) {
        Intent intent = new Intent(context, PlayerService.class);
        intent.setAction(ACTION_PLAY);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }*/

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionPause(Context context, String param1, String param2) {
        Intent intent = new Intent(context, PlayerService.class);
        intent.setAction(ACTION_PAUSE);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    public PlayerService() {
        super("PlayerService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_PLAY.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionPlay(param1, param2);
            } else if (ACTION_PAUSE.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionPause(param1, param2);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionPlay(String param1, String param2) {
        Log.d(LOG_TAG, "Previous Playing:" + Boolean.toString(mIsPlaying));
        mIsPlaying = true;
        Log.d(LOG_TAG, "Playing:" + Boolean.toString(mIsPlaying));
        // TODO: Handle action Foo
        //throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionPause(String param1, String param2) {
        Log.d(LOG_TAG, "Previous Playing:" + Boolean.toString(mIsPlaying));
        mIsPlaying = false;
        Log.d(LOG_TAG, "Playing:" + Boolean.toString(mIsPlaying));
        // TODO: Handle action Baz
        //throw new UnsupportedOperationException("Not yet implemented");
    }

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

    public class LocalBinder extends Binder {
        PlayerService getService() {
            return PlayerService.this;
        }
    }

    public boolean isPlaying() {
        return mIsPlaying;
    }
}
