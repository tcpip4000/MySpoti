package net.ertechnology.myspoti;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Juan on 29/06/2015.
 */
public class MySpotiAdapter extends ArrayAdapter<MySpotiObject> {

    /**
     * Constructor
     *
     * @param context  The current context.
     * @param resource The resource ID for a layout file containing a TextView to use when
     *                 instantiating views.
     * @param objects  The objects to represent in the ListView.
     */
    public MySpotiAdapter(Context context, int resource, List<MySpotiObject> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        MySpotiObject item = getItem(position);

        TextView image = (TextView) view.findViewById(R.id.list_item_image);
        TextView description = (TextView) view.findViewById(R.id.list_item_description);

        image.setText(item.listItemImage);
        description.setText(item.listItemDescription);

        return view;
    }

}
