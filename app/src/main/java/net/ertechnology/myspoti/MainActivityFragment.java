package net.ertechnology.myspoti;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ListView listView = (ListView) view.findViewById(R.id.main_listview);

        String[] myData = {"data1", "data2", "data3"};
        List<String> myList = new ArrayList<String>(Arrays.asList(myData));
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_data, R.id.list_item_description, myData);
        /*ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_data, R.id.list_item_textview);
        arrayAdapter.add("mango");
        arrayAdapter.add("manzana");
        arrayAdapter.add("pera");*/

        listView.setAdapter(arrayAdapter);


        return view;
    }
}
