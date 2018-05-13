package com.bhagyashreebagwe.knowyourgovernment;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, View.OnLongClickListener{

    private static final String TAG = "MainActivity";

    private List<Official> officialList = new ArrayList <>();  // Main content is here

    private RecyclerView recyclerView; // Layout's recyclerview

    private OfficialAdapter mAdapter; // Data to recyclerview adapter

    private Locator locator;

    private TextView currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locator = new Locator(this);
        recyclerView = (RecyclerView) findViewById(R.id.official_list);
        RecyclerView.ItemDecoration itemDecor = new Decor(getDrawable(R.drawable.separator));
        mAdapter = new OfficialAdapter(officialList, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        //DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, linearLayoutManager.getOrientation());
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(itemDecor);

        currentLocation = (TextView) findViewById(R.id.loc);
        currentLocation.setText("No Data for location");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.infoMenu:
                Intent intent2 = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent2);
                break;
            case R.id.locationMenu:
                showDialogBox();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {  // click listener called by ViewHolder clicks
        int pos = recyclerView.getChildLayoutPosition(v);
        onItemClick(v, pos);
    }

    @Override
    public boolean onLongClick(View v) {
        int pos = recyclerView.getChildLayoutPosition(v);
        onItemClick(v, pos);
        return false;
    }

    public void onItemClick(View v, int idx)
    {
        Official m = officialList.get(idx);
        Intent intent = new Intent(MainActivity.this, OfficialActivity.class);
        intent.putExtra("location", currentLocation.getText());
        intent.putExtra(Official.class.getName(), m);
        startActivity(intent);
    }

    public void showDialogBox(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final EditText et = new EditText(this);
        et.setInputType(InputType.TYPE_CLASS_TEXT);
        et.setGravity(Gravity.CENTER_HORIZONTAL);

        builder.setView(et);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                AsynchTask ast1= new AsynchTask(MainActivity.this);
                ast1.execute(et.getText().toString());
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

        builder.setMessage("Enter City, State or a Zip Code:");

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 5) {
            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        locator.setUpLocationManager();
                        locator.determineLocation();
                    } else {
                        Toast.makeText(this, "Location permission was denied - cannot determine address", Toast.LENGTH_LONG).show();
                        Log.d(TAG, "onRequestPermissionsResult: NO PERM");
                    }
                }
            }
        }
        Log.d(TAG, "onRequestPermissionsResult: Exiting onRequestPermissionsResult");
    }

    @Override
    protected void onDestroy() {
        locator.shutdown();
        super.onDestroy();
    }

    public void noLocationAvailable() {
        Toast.makeText(this, "No location providers were available", Toast.LENGTH_LONG).show();
    }

    String doLocationWork(double latitude, double longitude) {
        List<Address> addresses = null;
        for (int times = 0; times < 3; times++) {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {

                addresses = geocoder.getFromLocation(latitude, longitude, 1);
                Address ad = addresses.get(0);
                String zipCode = ad.getPostalCode();
                if(zipCode!=null || zipCode!=""){
                AsynchTask ast1= new AsynchTask(this);
                ast1.execute(zipCode);
                break;
                }

            } catch (IOException e) {
                Log.d(TAG, "doLocationWork: " + e.getMessage());

            }
            Toast.makeText(this, "GeoCoder service is slow - please wait", Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(this, "GeoCoder service timed out - please try again", Toast.LENGTH_LONG).show();
        return null;
    }

    public void setOfficialList(Object[] results){
        if(results != null && results.length!= 0)
        {
            if(results[0] != null)
                currentLocation.setText(results[0].toString());
            else
                currentLocation.setText("No Data for location");

            officialList.clear();
            List<Official> tempList= (ArrayList)results[1];
            if(tempList!=null) {
                officialList.addAll(tempList);
            }
            mAdapter.notifyDataSetChanged();
        }
        else
        {
            currentLocation.setText("No Data for location");
            officialList.clear();
            mAdapter.notifyDataSetChanged();
        }
    }

}
