package com.yasir.huaweicodelabs.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.yasir.huaweicodelabs.R;
import com.yasir.huaweicodelabs.Utilities.AppLog;
import com.yasir.huaweicodelabs.repos.AlertsRepo;
import com.yasir.huaweicodelabs.repos.PermissionCallback;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TVActivity extends AppCompatActivity {

    @BindView(R.id.txtCurrentLocation)
    TextView txtCurrentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tv);

        // Dependency Injection
        ButterKnife.bind(this);

        checkPermissions();
    }

    public void checkPermissions() {
        String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
        Permissions.check(this, permissions, getResources().getString(R.string.permission_request), null, new PermissionHandler() {
            @Override
            public void onGranted() {
                getLocation();
            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                AlertsRepo.createMessageDialog(TVActivity.this, (dialog, which) -> dialog.dismiss(), getResources().getString(R.string.app_permissions), android.R.string.dialog_alert_title).show();
            }
        });
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Request a static location from the location manager
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if(locationManager == null){
            txtCurrentLocation.setText("Location Manager is null");
            AppLog.Error(TVActivity.class.getSimpleName(), "Location Manager is null");
            return;
        }
        Location location = locationManager.getLastKnownLocation("static");
        if(location == null){
            txtCurrentLocation.setText("Location is null");
            AppLog.Error(TVActivity.class.getSimpleName(), "Location is null");
            return;
        }

        // Attempt to get postal or zip code from the static location object
        Geocoder geocoder = new Geocoder(this);
        Address address;
        try {
            address = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1).get(0);
            if(address != null){
                String txtLocation = getResources().getString(R.string.current_location) + " " + address.getLocality()
                        + " " + address.getCountryName() + " " + address.getPostalCode();
                txtCurrentLocation.setText(txtLocation);
            }
        } catch (IOException e) {
            AppLog.Error(TVActivity.class.getSimpleName() + " ---> Geocoder error", e.getMessage());
            txtCurrentLocation.setText(e.getMessage());
        }
    }
}