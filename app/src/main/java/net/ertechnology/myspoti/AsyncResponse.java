package net.ertechnology.myspoti;

import java.util.List;

import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by Juan on 05/07/2015.
 */
interface AsyncResponse {
    void processFinish(List<Track> tracks);
}
