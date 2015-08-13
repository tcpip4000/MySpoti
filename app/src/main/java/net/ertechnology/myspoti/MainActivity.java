package net.ertechnology.myspoti;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;


public class MainActivity extends AppCompatActivity implements MainActivityFragment.MainFragmentListener {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_CODE = 1337;
    private static final String REDIRECT_URI = "yourcustomprotocol://callback";
    private static final String CLIENT_ID = "22837230eb5045ba9826a6542c1c8169";
    public static final String FRAGMENT_MAIN = "FRAGMENT_MAIN";
    public static final String FRAGMENT_DETAIL = "FRAGMENT_DETAIL";
    private String mToken; // TODO: use app session
    private boolean mTwoPane;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int choiceModel;

        getTokenFromWeb(this);

        // Get spoti conn
        SpotifyApi api = new SpotifyApi();
        api.setAccessToken(mToken);
        SpotifyService mSpotify = api.getService();
        MySession.getInstance().setSpotifyService(mSpotify);

        // Dynamic fill detail fragment
        if (findViewById(R.id.detail_container) == null) {
            mTwoPane = false;
            choiceModel = ListView.CHOICE_MODE_NONE;
        } else {
            mTwoPane = true;
            choiceModel = ListView.CHOICE_MODE_SINGLE;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.detail_container, new HitActivityFragment(), FRAGMENT_DETAIL)
                        .commit();
            }
        }

        // Dynamic fill main fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.main_container, MainActivityFragment.newInstance(choiceModel), FRAGMENT_MAIN)
                    .commit();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    private static void getTokenFromWeb(Activity activity) {
        AuthenticationRequest.Builder builder =
                new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);
        builder.setScopes(new String[]{"streaming"});
        AuthenticationRequest request = builder.build();
        AuthenticationClient.openLoginActivity(activity, REQUEST_CODE, request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Log.d(LOG_TAG, "xxx callback");

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);

            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    Log.d(LOG_TAG, "token");
                    // Handle successful response
                    mToken = response.getAccessToken();
                    break;

                // Auth flow returned an error
                case ERROR:
                    Log.d(LOG_TAG, "error");
                    // Handle error response
                    break;

                // Most likely auth flow was cancelled
                default:
                    Log.d(LOG_TAG, "default");
                    // Handle other cases
            }
        }
    }

    @Override
    public void onArtistClicked(String artistId) {
        Log.d(LOG_TAG, "artist id:" + artistId);
        int targetLayout;

        HitActivityFragment hitActivityFragment = HitActivityFragment.newInstance(artistId);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (mTwoPane) {
            targetLayout = R.id.detail_container;
        } else {
            targetLayout = R.id.main_container;
        }
        ft.replace(targetLayout, hitActivityFragment, FRAGMENT_DETAIL);
        ft.addToBackStack(null);
        ft.commit();
    }


}
