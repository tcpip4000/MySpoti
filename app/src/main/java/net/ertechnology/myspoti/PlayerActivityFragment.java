package net.ertechnology.myspoti;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlayerActivityFragment extends Fragment {

    private static final String LOG_TAG = PlayerActivityFragment.class.getSimpleName();
    private String mArtistId;
    private MyTrack mTrack;
    private String mArtistName;

    public PlayerActivityFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mArtistId = getActivity().getIntent().getStringExtra(PlayerActivity.PLAYER_ARTIST_ID);
        mArtistName = getActivity().getIntent().getStringExtra(PlayerActivity.PLAYER_ARTIST_NAME);
        mTrack = getActivity().getIntent().getParcelableExtra(PlayerActivity.PLAYER_TRACK);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Log.d(LOG_TAG, mArtistId);
        //Log.d(LOG_TAG, mTrack.toString());
        View view = inflater.inflate(R.layout.fragment_player, container, false);
        ViewHolder viewHolder;

        if (view.getTag() == null) {
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }   else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.playerArtist.setText(mArtistName);
        viewHolder.playerSong.setText(mTrack.getName());

        Picasso.with(getActivity())
                .load(mTrack.getImages().get(0))
                .into(viewHolder.playerImage);

        return view;
    }

    private static class ViewHolder {
        final TextView playerArtist;
        final TextView playerSong;
        final ImageView playerImage;
        final ProgressBar playerProgressBar;
        final ImageButton playerBack;
        final ImageButton playerPlayStop;
        final ImageButton playerNext;

        public ViewHolder(View view) {
            playerArtist = (TextView) view.findViewById(R.id.player_artist);
            playerSong = (TextView) view.findViewById(R.id.player_song);
            playerImage  = (ImageView) view.findViewById(R.id.player_image);
            playerProgressBar  = (ProgressBar) view.findViewById(R.id.player_progressBar);
            playerBack  = (ImageButton) view.findViewById(R.id.player_back);
            playerPlayStop  = (ImageButton) view.findViewById(R.id.player_play_stop);
            playerNext = (ImageButton) view.findViewById(R.id.player_next);
        }
    }
}
