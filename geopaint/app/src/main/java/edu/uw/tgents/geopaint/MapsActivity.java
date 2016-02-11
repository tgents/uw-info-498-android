package edu.uw.tgents.geopaint;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import petrov.kristiyan.colorpicker.ColorPicker;

public class MapsActivity extends FragmentActivity implements PopupMenu.OnMenuItemClickListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, ActivityCompat.OnRequestPermissionsResultCallback {

    GoogleApiClient mGoogleApiClient;
    Location currentLocation;
    boolean penDown = false;
    PolylineOptions currentLine;
    List<Polyline> lines = new ArrayList<Polyline>();
    int penColor;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapFragment.setRetainInstance(true);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int tempColor = sharedPref.getInt("drawColor", 0);
        if (tempColor == 0) {
            penColor = Color.BLACK;
        } else {
            penColor = tempColor;
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                penDown = !penDown;
                Toast.makeText(getApplicationContext(), "Currently drawing: " + penDown, Toast.LENGTH_SHORT).show();
                if (penDown == true) {
                    startDraw();
                } else {
                    stopDraw();
                }
            }
        });

    }

    private void startDraw() {
        mGoogleApiClient.connect();
        currentLine = new PolylineOptions();
        currentLine.color(penColor);
        mMap.addPolyline(currentLine);
    }

    private void stopDraw() {
        lines.add(mMap.addPolyline(currentLine));
        mGoogleApiClient.disconnect();
        currentLine = null;
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        setUpLocation();
        mMap.getUiSettings().setZoomControlsEnabled(true);

        getLocation(null);
        if (currentLocation != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())));
        }

    }

    public void setUpLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            mMap.setMyLocationEnabled(true);
            if (mGoogleApiClient == null) {
                mGoogleApiClient =
                        new GoogleApiClient.Builder(this)
                                .addConnectionCallbacks(this)
                                .addOnConnectionFailedListener(this)
                                .addApi(LocationServices.API) //use location
                                .build(); //build me the client already dammit!
            }
            getLocation(null);
        }
    }

    public void getLocation(View v) {
        if (mGoogleApiClient != null) {
            Location temp = null;
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                temp = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            }
            if (temp == null) {
                Log.v("Test", "Could not get location");
            } else {
                currentLocation = temp;
            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        getLocation(null);
        LocationRequest request = new LocationRequest();
        request.setInterval(10000);
        request.setFastestInterval(5000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, request, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        currentLine.add(latLng);
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.addPolyline(currentLine);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setUpLocation();
            }
        }
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.menu_main);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.color:
                ColorPicker colorPicker = new ColorPicker(this);
                colorPicker.show();
                colorPicker.setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
                    @Override
                    public void onChooseColor(int position, int color) {
                        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor edit = sharedPref.edit();
                        edit.putInt("drawColor", color);
                        penColor = color;
                        if (penDown) {
                            stopDraw();
                            startDraw();
                        }
                        Toast.makeText(getApplicationContext(), "Color has been saved", Toast.LENGTH_SHORT).show();
                    }
                });
                return true;
            case R.id.save:
                Toast.makeText(this, saveDrawings(), Toast.LENGTH_SHORT).show();
                return true;
            case R.id.share:
                if (lines.size() > 0) {
                    ShareActionProvider myShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
                    Intent myShareIntent = new Intent(Intent.ACTION_SEND);
                    myShareIntent.setType("text/plain");
                    saveDrawings();
                    myShareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(this.getExternalFilesDir(null), "drawing.geojson")));
                    myShareActionProvider.setShareIntent(myShareIntent);
                } else {
                    Toast.makeText(this, "No drawings to share...", Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return false;
        }
    }

    private String saveDrawings() {
        try {
            File file = new File(this.getExternalFilesDir(null), "drawing.geojson");
            Log.v("TEST", ""+ file.exists());
            if (lines.size() > 0) {
                FileOutputStream outputStream = new FileOutputStream(file);
                outputStream.write(GeoJsonConverter.convertToGeoJson(lines).getBytes()); //write the string to the file
                outputStream.close(); //close the stream
                return "Save successful!";
            } else {
                return "Could not save";
            }

        } catch (Exception e) {
            return "Could not save";
        }
    }
}
