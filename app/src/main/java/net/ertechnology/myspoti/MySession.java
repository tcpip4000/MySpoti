package net.ertechnology.myspoti;

import kaaes.spotify.webapi.android.SpotifyService;

/**
 * Created by Juan on 05/07/2015.
 */
public class MySession {

    private static final MySession mInstance = new MySession();

    private SpotifyService mSpotifyService;

    public static MySession getInstance() {
        return mInstance;
    }

    private MySession() {}

    public void setSpotifyService(SpotifyService spotifyService) {
        mSpotifyService = spotifyService;
    }

    public SpotifyService getSpotifyService() {
        return mSpotifyService;
    }

}
