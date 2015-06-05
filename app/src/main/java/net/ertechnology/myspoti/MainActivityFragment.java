package net.ertechnology.myspoti;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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

        ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();
        HashMap<String, String> map = new HashMap<>();
        HashMap<String, String> map2 = new HashMap<>();
        map.put("image", "image1");
        map.put("description", "Soul Fly");
        map2.put("image", "image2");
        map2.put("description", "Ramones");
        data.add(map);
        data.add(map2);
        String[] from = new String[] {"image", "description"};
        int[] to = new int[]{R.id.list_item_image, R.id.list_item_description};
        SimpleAdapter arrayAdapter = new SimpleAdapter(getActivity(), data, R.layout.list_item_data, from, to);

        //String[] myData = {"data1", "data2", "data3"};
        //List<String> myList = new ArrayList<String>(Arrays.asList(myData));
        //ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_data, R.id.list_item_description, myData);

        /*ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_data, R.id.list_item_textview);
        arrayAdapter.add("mango");
        arrayAdapter.add("manzana");
        arrayAdapter.add("pera");*/

        listView.setAdapter(arrayAdapter);


        return view;
    }
}
