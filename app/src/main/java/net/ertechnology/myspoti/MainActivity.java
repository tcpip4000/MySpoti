package net.ertechnology.myspoti;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Album;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Pager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_CODE = 1337;
    private static final String REDIRECT_URI = "yourcustomprotocol://callback";
    private static final String CLIENT_ID = "22837230eb5045ba9826a6542c1c8169";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getData(this);

        getFragmentManager().beginTransaction().add(R.id.main_container, new MainActivityFragment())
                .commit();

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

    public static void getData(Activity activity) {
        /// Auth

        AuthenticationRequest.Builder builder =
                new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);
        builder.setScopes(new String[]{"streaming"});
        AuthenticationRequest request = builder.build();
        AuthenticationClient.openLoginActivity(activity, REQUEST_CODE, request);
        ///



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
                    SpotifyApi api = new SpotifyApi();
                    api.setAccessToken(response.getAccessToken());

                    SpotifyService spotify = api.getService();
/*                    spotify.getAlbum("2dIGnmEIy1WZIcZCFSj6i8", new Callback<Album>() {
                        @Override
                        public void success(Album album, Response response) {
                            Log.d(LOG_TAG, album.name);
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Log.d(LOG_TAG, error.toString());
                        }
                    });*/
                    spotify.searchArtists("loco", new Callback<ArtistsPager>() {
                        @Override
                        public void success(ArtistsPager artistsPager, Response response) {
                            Log.d(LOG_TAG, artistsPager.toString());
                            Pager<Artist> artists = artistsPager.artists;
                            for (Artist artist : artists.items) {
                                Log.d( LOG_TAG, "name: " + artist.name);
                                Log.d( LOG_TAG, "followers: " + artist.popularity);
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {

                        }
                    });
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
}
