package com.bhagyashreebagwe.knowyourgovernment;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by bhagyashree on 4/2/18.
 */

public class PhotoActivity extends AppCompatActivity {

    private static final String TAG = "PhotoActivity";
    private TextView loca;
    private TextView office;
    private TextView name;
    private ImageView iv;
    private Official off;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        loca= (TextView) findViewById(R.id.location);
        office = (TextView) findViewById(R.id.office);
        name = (TextView) findViewById(R.id.name);
        iv = (ImageView) findViewById(R.id.photo);

        Intent intent = getIntent();

        if(intent.hasExtra("location"))
            loca.setText(intent.getStringExtra("location"));

        if (intent.hasExtra(Official.class.getName())) {
            off = (Official) intent.getSerializableExtra(Official.class.getName());
            office.setText(off.getOffice());
            name.setText(off.getName());
            loadRemoteImage(off.getPhotoURL());
        }

        View view = this.getWindow().getDecorView();
        if(off.getParty().equalsIgnoreCase("republican"))
            view.setBackgroundColor(Color.parseColor("#FF0000"));  //red
        else if(off.getParty().equalsIgnoreCase("democratic"))
            view.setBackgroundColor(Color.parseColor("#0000ff"));  //blue
        else
            view.setBackgroundColor(Color.parseColor("#000000"));  //black
    }

    private  void loadRemoteImage(final String imageURL){

        if (imageURL != null) {
            Picasso picasso = new Picasso.Builder(this).listener(new Picasso.Listener() {
                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                    // Here we try https if the http image attempt failed
                    final String changedUrl = imageURL.replace("http:", "https:");
                    picasso.load(changedUrl)
                            .fit()
                            .centerCrop()
                            .error(R.drawable.brokenimage)
                            .placeholder(R.drawable.placeholder)
                            .into(iv);
                }
            }).build();
            picasso.load(imageURL)
                    .fit()
                    .centerCrop()
                    .error(R.drawable.brokenimage)
                    .placeholder(R.drawable.placeholder)
                    .into(iv);
        } else {
            Picasso.with(this).load(imageURL)
                    .fit()
                    .centerCrop()
                    .error(R.drawable.brokenimage)
                    .placeholder(R.drawable.missingimage)
                    .into(iv);
        }
    }
}
