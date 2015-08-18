package net.ertechnology.myspoti;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import retrofit.RetrofitError;


/**
 * Main activity fragment for myspoti.
 */
public class MainActivityFragment extends Fragment implements AsyncResponseArtists {

    private static final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    private static final String MAIN_ACTIVITY_CHOICE = "MACTCHO";
    private static final String ARTIST_LIST_POSITION = "ARTLPOS";
    private static final String ARTIST_LIST = "ARTLST";
    private static final String ARTIST_SEARCH = "ARTSE";
    private ArrayList<MyArtist> mArtistList;
    private MySpotiAdapter mCustomAdapter;

    private MainFragmentListener mCallback;
    private int mSelectedPosition;
    private ListView mListView;
    private String mArtistSearch;

    public MainActivityFragment() {
    }

    public interface MainFragmentListener {
        void onArtistClicked(String artistId, String artistName);
    }

    public static MainActivityFragment newInstance(int choiceMode) {
        MainActivityFragment mainActivityFragment = new MainActivityFragment();
        Bundle args = new Bundle();
        args.putInt(MAIN_ACTIVITY_CHOICE, choiceMode);
        mainActivityFragment.setArguments(args);
        return mainActivityFragment;
    }

    private int getChoiceMode() {
        return getArguments().getInt(MAIN_ACTIVITY_CHOICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        try {
            mListView = (ListView) view.findViewById(R.id.main_listview);
            mListView.setChoiceMode(getChoiceMode());

            if (savedInstanceState == null) {
                mArtistList = new ArrayList<>();
                mSelectedPosition = ListView.INVALID_POSITION;
                mArtistSearch = "";
            } else {
                mArtistList = savedInstanceState.getParcelableArrayList(ARTIST_LIST);
                mSelectedPosition = savedInstanceState.getInt(ARTIST_LIST_POSITION);
                mArtistSearch = savedInstanceState.getString(ARTIST_SEARCH);

            }

            mCustomAdapter = new MySpotiAdapter(getActivity(), mArtistList);
            mListView.setAdapter(mCustomAdapter);

            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    MyArtist item = mCustomAdapter.getItem(position);
                    mSelectedPosition = position;
                    mCallback.onArtistClicked(item.getId(), item.getName());
                }
            });

            if (mSelectedPosition != ListView.INVALID_POSITION) {
                mListView.setSelection(mSelectedPosition);
                mListView.setItemChecked(mSelectedPosition, true);
            }


            // Search button
            final EditText search = (EditText) view.findViewById(R.id.main_search);
            search.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
/*                    Filter filter = mCustomAdapter.getFilter();
                    filter.filter(s.toString());*/
                    mArtistSearch = s.toString();
                    GetArtistListTask getArtistListTask = new GetArtistListTask();
                    getArtistListTask.delegate = (AsyncResponseArtists) getActivity().getSupportFragmentManager().findFragmentByTag(MainActivity.FRAGMENT_MAIN);
                    getArtistListTask.execute(mArtistSearch);
                }
            });
        } catch (NullPointerException e) {
            Log.d(LOG_TAG, "Error", e);
        }
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (MainFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() +
                    "must implement MainFragmentListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.app_name);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(ARTIST_LIST_POSITION, mSelectedPosition);
        outState.putParcelableArrayList(ARTIST_LIST, mArtistList);
        outState.putString(ARTIST_SEARCH, mArtistSearch);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void processFinish(ArrayList<MyArtist> myArtists) {
        if (myArtists != null) {
            try {
                mArtistList = myArtists;
                if (getView() != null) {
                    mCustomAdapter.clear();
                    mCustomAdapter.addAll(mArtistList);
                    if (myArtists.size() == 0) {
                        Toast.makeText(getActivity(), R.string.artist_not_found, Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (NullPointerException e) {
                Log.e(LOG_TAG, "Error", e);
            }
        } else {
            Log.d(LOG_TAG, "Network connection problem");
        }
    }

    private static class GetArtistListTask extends AsyncTask<String, Void, ArrayList<MyArtist>> {

        AsyncResponseArtists delegate;

        @Override
        protected ArrayList<MyArtist> doInBackground(String... params) {
            ArrayList<MyArtist> myArtists;
            try {
                String searchString = params[0];

                ArtistsPager pager = MySession.getInstance().getSpotifyService().searchArtists(searchString);
                List<Artist> artists = pager.artists.items;
                myArtists = MyArtist.create(artists);

            } catch (RetrofitError e) {
                Log.e(LOG_TAG, "Error", e);
                myArtists = null;
            }
            return myArtists;
        }

        @Override
        protected void onPostExecute(ArrayList<MyArtist> myArtists) {
            delegate.processFinish(myArtists);
        }
    }

}
