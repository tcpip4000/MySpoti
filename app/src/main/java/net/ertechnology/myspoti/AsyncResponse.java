package net.ertechnology.myspoti;

import kaaes.spotify.webapi.android.models.ArtistsPager;

/**
 * Created by Juan on 29/06/2015.
 */
public interface AsyncResponse {
    void processFinish(ArtistsPager artistsPager);
}
