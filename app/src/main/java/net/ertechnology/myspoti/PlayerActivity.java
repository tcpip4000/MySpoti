package net.ertechnology.myspoti;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class PlayerActivity extends FragmentActivity {

    public static final String PLAYER_TRACK = "net.ertechnology.myspoti.track";
    public static final String PLAYER_ARTIST_ID = "net.ertechnology.myspoti.artistId";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_player);
    }

}
