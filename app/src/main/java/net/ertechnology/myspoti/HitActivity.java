package net.ertechnology.myspoti;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class HitActivity extends AppCompatActivity {

    public static final String HIT_ARTIST_ID = "HIT_ARTIST_ID";
    private static final String LOG_TAG = HitActivity.class.getSimpleName();
    String mArtistId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hit);

        Intent intent = getIntent();
        mArtistId = intent.getStringExtra(HIT_ARTIST_ID);
        Log.d(LOG_TAG, "Received id:" + mArtistId);

        getSupportFragmentManager().beginTransaction().add(R.id.hit_container, new HitActivityFragment())
                .commit();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_hit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



}
