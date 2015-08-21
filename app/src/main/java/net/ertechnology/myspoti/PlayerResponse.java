package net.ertechnology.myspoti;

/**
 * Created by Juan on 20/08/2015.
 */
public class PlayerResponse {
    public MyTrack mTrack;
    public int mIndex;

    PlayerResponse(MyTrack track, int i) {
        mTrack = track;
        mIndex = i;
    }
}
