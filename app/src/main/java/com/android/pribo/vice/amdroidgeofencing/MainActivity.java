package com.android.pribo.vice.amdroidgeofencing;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnCompleteListener<Void> {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_PERMISSION_REQUEST_CODE = 34;

    private enum PendingGeofenceTask {
        ADD, REMOVE, NONE;
    }

    private GeofencingClient mGeofencingClient;
    private ArrayList<Geofence> mGeofenceArrayList;
    private PendingIntent mGeofencePendingIntent;
    private PendingGeofenceTask mPendingGeofenceTask = PendingGeofenceTask.NONE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mGeofenceArrayList = new ArrayList<>();
        mGeofencePendingIntent = null;
        mGeofencingClient = LocationServices.getGeofencingClient(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!checkPermission()) {
            requestPermissions();
        } else {
            performPendingGeoFenceTask();
        }
    }

    private void performPendingGeoFenceTask() {
        if (mPendingGeofenceTask == PendingGeofenceTask.ADD)
            addGeofences();
        else if (mPendingGeofenceTask == PendingGeofenceTask.REMOVE)
            removeGeofences();

    }

    private void removeGeofences() {
        if (!checkPermission()) {
            Toast.makeText(this, "No Permissions", Toast.LENGTH_SHORT).show();
        }
        mGeofencingClient.removeGeofences(getGeofencingPendingIntent()).addOnCompleteListener(this);
    }

    private void addGeofences() {
        if (!checkPermission()) {
            Toast.makeText(this, "No permissions", Toast.LENGTH_SHORT).show();
        }
        mGeofencingClient.addGeofences(getGeofencingReqest(), getGeofencingPendingIntent()).addOnCompleteListener(this);
    }

    private PendingIntent getGeofencingPendingIntent() {
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        } else {
            Intent intent = new Intent(this, GeofencingTransotionIntentServices.class);
            return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
    }

    private GeofencingRequest getGeofencingReqest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofence((Geofence) mGeofenceArrayList);
        return builder.build();
    }

    private boolean checkPermission() {
        int permissionState = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        boolean shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (shouldProvideRationale) {
            Toast.makeText(this, "Location permission is needed", Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_REQUEST_CODE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        /*super.onRequestPermissionsResult(requestCode, permissions, grantResults);*/
        if (requestCode == REQUEST_PERMISSION_REQUEST_CODE){
            if (grantResults.length <= 0){
                Toast.makeText(this, "No request codes...", Toast.LENGTH_SHORT).show();
            }else if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                performPendingGeoFenceTask();
            }else {
                Toast.makeText(this, "Permission was denied :(", Toast.LENGTH_LONG).show();
                mPendingGeofenceTask = PendingGeofenceTask.NONE;
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onComplete(@NonNull Task<Void> task) {
        mPendingGeofenceTask = PendingGeofenceTask.NONE;
        if (task.isSuccessful()) {
            updateGeoFenceAdded(!getGeoFencesAdded());
            int messageId;
            if (getGeoFencesAdded()) messageId = R.string.geofences_added;
            else messageId = R.string.geofences_removed;
            Toast.makeText(this, getString(messageId), Toast.LENGTH_SHORT).show();
        } else {
            String errorMessageId = GeofenceErrorMessages.getErrorString(this, task.getException());
        }


    }

    private PendingIntent getGeoFencePendingIntent() {
        if (mGeofencePendingIntent != null)
            return mGeofencePendingIntent;

        Intent intent = new Intent(this, GeofencingTransotionIntentServices.class);

        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void populateGeofenceList() {
        for (Map.Entry<String, LatLng> entry : Constants.nofim.entrySet()) {
            mGeofenceArrayList.add(new Geofence.Builder().setRequestId(entry.getKey())
                    .setCircularRegion(entry.getValue().latitude, entry.getValue().longitude, 200)
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                    .build());
        }
    }


    private boolean getGeoFencesAdded() {
        return PreferenceManager.getDefaultSharedPreferences(this).getBoolean("KEY_ADDED", false);
    }

    private void updateGeoFenceAdded(boolean added) {
        PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("KEY_ADDED", added).apply();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
