package com.group13.dynamicwayfinder.Activities.Map;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.group13.dynamicwayfinder.R;
import java.util.ArrayList;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


public class MapActivity extends AppCompatActivity implements LocationListener, OnMapReadyCallback {
    private GoogleMap mMap;
    private android.location.LocationManager lm;
    private Marker markerLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        startGettingLocations();

    }

    private void startGettingLocations() {

        lm = (android.location.LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean isGPS = lm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
        boolean isNetwork = lm.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER);
        boolean canGetLocation = true;
        int ALL_PERMISSIONS_RESULT = 101;
        long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1;// Distance in meters
        long MIN_TIME_BW_UPDATES = 100;// Time in milliseconds

        ArrayList<String> permissions = new ArrayList<>();
        ArrayList<String> permissionsToRequest;

        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissionsToRequest = findUnAskedPermissions(permissions);


        //Check if GPS and Network are on, if not asks the user to turn on
        if (!isGPS && !isNetwork) {
            showSettingsAlert();
        } else {
            // check permissions
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (permissionsToRequest.size() > 0) {
                    requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]),
                            ALL_PERMISSIONS_RESULT);
                    canGetLocation = false;
                }
            }
        }

        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(this, "Permission not Granted", Toast.LENGTH_SHORT).show();


            return;
        }

        //Starts requesting location updates
        if (canGetLocation) {
            //This line stops gps being used as its not useful indoors comment out for gps location
            //over network location
            isGPS=false;
            if (isGPS) {
                lm.requestLocationUpdates(
                        android.location.LocationManager.GPS_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

            } else if (isNetwork) {
                // from Network Provider

                lm.requestLocationUpdates(
                        android.location.LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

            }
        } else {
            Toast.makeText(this, "Can't get location", Toast.LENGTH_SHORT).show();
        }
        lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }


    private ArrayList findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList result = new ArrayList();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canAskPermission()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canAskPermission() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("GPS is not Enabled!");
        alertDialog.setMessage("Do you want to turn on GPS?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng latlng = new LatLng(location.getLatitude(),location.getLongitude());
        addMarker(latlng);
        System.out.println("location happened");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    private void addMarker(LatLng latLng) {
        if (latLng == null) {
            return;
        }
        if (markerLocation != null) {
            markerLocation.remove();
        }

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("New Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        if (mMap != null)
            markerLocation = mMap.addMarker(markerOptions);


        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latLng.latitude, latLng.longitude))
                .zoom(16)
                .build();

        if (mMap != null)
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}