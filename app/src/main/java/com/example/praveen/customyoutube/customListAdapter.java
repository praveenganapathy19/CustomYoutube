package com.example.praveen.customyoutube;

/**
 * Created by Praveen on 02/02/2018.
 */

import android.app.Activity;
import android.support.annotation.IntegerRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.api.services.youtube.model.Thumbnail;

public class customListAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final String[] itemDescription;
    private final String[] imgThumbnail;

    public customListAdapter(Activity context, String[] itemname, String[] imgid) {
        super(context, R.layout.mylist, itemname);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.itemDescription=itemname;
        this.imgThumbnail=imgid;
    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.mylist, null,true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.itemDescription);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.imgThumbnail);
        //TextView extratxt = (TextView) rowView.findViewById(R.id.imgThumbnail);

        txtTitle.setText(itemDescription[position]);
        //imageView.setImageResource(imgThumbnail.getUrl().toString());
        new DownLoadImageTask(imageView).execute(imgThumbnail[position]);
        //extratxt.setText("Description "+itemDescription[position]);
        return rowView;

    };
}
