package com.here.android.example.sb_locpermissions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity
        implements PermissionsChecker.PermissionListener {

    private double lat, lon;

    ViewGroup contentView;

    private TextView promptView;
    private TextView latView;
    private TextView lonView;
    private TextView infoView;

    private static int PROMPT_ID = View.generateViewId();
    private static int LAT_ID = View.generateViewId();
    private static int LON_ID = View.generateViewId();
    private static int INFO_ID = View.generateViewId();

    String[] perms  = {Manifest.permission.ACCESS_FINE_LOCATION};

    @Override
    public void permissionResponse(int code) {
        if (code == 0) {
            enableLocation();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // check permissions first
        PermissionsChecker.permissionListener = this;
        for(int i=0; i<perms.length; i++) {
            PermissionsChecker.checkPermissions(perms[i], i);
        }

    }

    private void enableLocation()  {

        // Acquire a reference to the system Location Manager
        final LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        final LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                useLocation(location);
                locationManager.removeUpdates(this);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        // Register the listener with the Location Manager to receive location updates
        // locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
                    locationListener);
        } catch (SecurityException e){
            Log.d( "MainActivity", e.toString());
        }
    }

    private void useLocation(Location location) {
        Log.d( "MainActivity", "You are here: " + location.toString());

        try {
            lat = location.getLatitude();
            lon = location.getLongitude();
        } catch (NullPointerException e) {
            lat = -1.0;
            lon = -1.0;
        }

        // setup display of latitude and longitude to user
        Context mContext= this;
        LinearLayout linearLayout = new LinearLayout (mContext);
        linearLayout.setOrientation (LinearLayout. VERTICAL);
        linearLayout.setBackgroundColor(Color.WHITE);

        int size = 20;
        TextView promptView = new TextView(mContext);
        promptView.setTextSize(TypedValue. COMPLEX_UNIT_SP, size);
        promptView.setText("You are Here: ");
        TextView latView = new TextView(mContext);
        latView.setTextSize(TypedValue. COMPLEX_UNIT_SP, size);
        latView.setText(String.valueOf(lat));
        TextView lonView = new TextView(mContext);
        lonView.setTextSize (TypedValue. COMPLEX_UNIT_SP, size);
        lonView.setText(String.valueOf(lon));
        TextView infoView = new TextView(mContext);
        infoView.setTextSize (TypedValue. COMPLEX_UNIT_SP, size);
        infoView.setText("Select Device Back Button to Return to Main App");

        // set ID's
        promptView.setId(PROMPT_ID);
        latView.setId(LAT_ID);
        lonView.setId(LON_ID);
        infoView.setId(INFO_ID);

        linearLayout.addView(promptView);
        linearLayout.addView(latView);
        linearLayout.addView(lonView);
        linearLayout.addView(infoView);

        contentView = (ViewGroup) ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        contentView.addView(linearLayout);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String [] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionsChecker.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}

