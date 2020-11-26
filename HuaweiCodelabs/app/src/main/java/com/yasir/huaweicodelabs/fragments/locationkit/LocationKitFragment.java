package com.yasir.huaweicodelabs.fragments.locationkit;

import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.huawei.hms.common.ApiException;
import com.huawei.hms.common.ResolvableApiException;
import com.huawei.hms.location.FusedLocationProviderClient;
import com.huawei.hms.location.LocationAvailability;
import com.huawei.hms.location.LocationCallback;
import com.huawei.hms.location.LocationRequest;
import com.huawei.hms.location.LocationResult;
import com.huawei.hms.location.LocationServices;
import com.huawei.hms.location.LocationSettingsRequest;
import com.huawei.hms.location.LocationSettingsStatusCodes;
import com.huawei.hms.location.SettingsClient;
import com.yasir.huaweicodelabs.R;
import com.yasir.huaweicodelabs.Utilities.AppLog;
import com.yasir.huaweicodelabs.fragments.BaseFragment;
import com.yasir.huaweicodelabs.repos.PermissionCallback;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LocationKitFragment extends BaseFragment implements View.OnClickListener {

    @BindView(R.id.btnRequestLocation)
    Button btnRequestLocation;

    @BindView(R.id.btnRemoveLocation)
    Button btnRemoveLocation;

    @BindView(R.id.txtLogs)
    TextView txtLogs;

    private View rootView;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private SettingsClient mSettingsClient;

    public static LocationKitFragment newInstance() {
        return new LocationKitFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getMainActivity().setHeading("Location Kit");

        txtLogs.setMovementMethod(new ScrollingMovementMethod());

        initLocationObjects();

        // Setting click listeners
        btnRemoveLocation.setOnClickListener(this);
        btnRequestLocation.setOnClickListener(this);

        // Update Logs at the start
        updateLogs("");

        getMainActivity().checkPermissions(new PermissionCallback() {
            @Override
            public void onGrantedPermissions() {
                AppLog.Debug(LocationKitFragment.class.getSimpleName(), "Good to go");
                updateLogs("All Permissions granted");
            }

            @Override
            public void onRejectedPermissions() {
                AppLog.Debug(LocationKitFragment.class.getSimpleName(), "Can't request location updates");
                updateLogs("All Permissions denied");
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_location_kit, container, false);
        } else {
            container.removeView(rootView);
        }

        ButterKnife.bind(this, rootView);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRequestLocation:
                requestLocationUpdatesWithCallback();
                break;
            case R.id.btnRemoveLocation:
                removeLocationUpdatesWithCallback();
                break;
        }
    }

    private void initLocationObjects() {
        // Init all the required objects
        if (mFusedLocationProviderClient == null) {
            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getMainActivity());
        }

        if (mLocationRequest == null) {
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(8000);
            mLocationRequest.setFastestInterval(2000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        }

        if (mSettingsClient == null) {
            mSettingsClient = LocationServices.getSettingsClient(getMainActivity());
        }

        if (mLocationCallback == null) {
            mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult != null) {
                        List<Location> locations = locationResult.getLocations();
                        if (!locations.isEmpty()) {
                            for (Location location : locations) {
                                updateLogs("onLocationResult location[Longitude,Latitude,Accuracy]:" + location.getLongitude() + "," + location.getLatitude() + "," + location.getAccuracy());
                                AppLog.Debug(LocationKitFragment.class.getSimpleName(), "onLocationResult location[Longitude,Latitude,Accuracy]:" + location.getLongitude() + "," + location.getLatitude() + "," + location.getAccuracy());
                            }
                        }
                    }
                }

                @Override
                public void onLocationAvailability(LocationAvailability locationAvailability) {
                    if (locationAvailability != null) {
                        boolean flag = locationAvailability.isLocationAvailable();
                        updateLogs("onLocationAvailability isLocationAvailable:" + flag);
                        AppLog.Debug(LocationKitFragment.class.getSimpleName(), "onLocationAvailability isLocationAvailable:" + flag);
                    }
                }
            };
        }
    }

    private void requestLocationUpdatesWithCallback() {
        try {
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
            builder.addLocationRequest(mLocationRequest);
            LocationSettingsRequest locationSettingsRequest = builder.build();
            // Check devices settings before request location updates.
            mSettingsClient.checkLocationSettings(locationSettingsRequest)
                    .addOnSuccessListener(locationSettingsResponse -> {
                        updateLogs("check location settings success");
                        AppLog.Debug(LocationKitFragment.class.getSimpleName(), "check location settings success");
                        // Request location updates
                        mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.getMainLooper())
                                .addOnSuccessListener(aVoid -> {
                                    AppLog.Debug(LocationKitFragment.class.getSimpleName(), "requestLocationUpdatesWithCallback onSuccess");
                                    updateLogs("requestLocationUpdatesWithCallback onSuccess");
                                })
                                .addOnFailureListener(e -> {
                                    updateLogs("requestLocationUpdatesWithCallback onFailure:" + e.getMessage());
                                    AppLog.Debug(LocationKitFragment.class.getSimpleName(), "requestLocationUpdatesWithCallback onFailure:" + e.getMessage());
                                });
                    })
                    .addOnFailureListener(e -> {
                        updateLogs("checkLocationSetting onFailure:" + e.getMessage());
                        AppLog.Debug(LocationKitFragment.class.getSimpleName(), "checkLocationSetting onFailure:" + e.getMessage());
                        int statusCode = ((ApiException) e).getStatusCode();
                        if (statusCode == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                            try {
                                ResolvableApiException rae = (ResolvableApiException) e;
                                rae.startResolutionForResult(getMainActivity(), 0);
                            } catch (IntentSender.SendIntentException sie) {
                                updateLogs("PendingIntent unable to execute request.");
                                AppLog.Debug(LocationKitFragment.class.getSimpleName(), "PendingIntent unable to execute request.");
                            }
                        }
                    });
        } catch (Exception e) {
            updateLogs("requestLocationUpdatesWithCallback exception:" + e.getMessage());
            AppLog.Debug(LocationKitFragment.class.getSimpleName(), "requestLocationUpdatesWithCallback exception:" + e.getMessage());
        }
    }

    private void removeLocationUpdatesWithCallback() {
        try {
            mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback)
                    .addOnSuccessListener(aVoid -> {
                        updateLogs("removeLocationUpdatesWithCallback onSuccess");
                        AppLog.Debug(LocationKitFragment.class.getSimpleName(), "removeLocationUpdatesWithCallback onSuccess");
                    })
                    .addOnFailureListener(e -> {
                        updateLogs("removeLocationUpdatesWithCallback onFailure:" + e.getMessage());
                        AppLog.Debug(LocationKitFragment.class.getSimpleName(), "removeLocationUpdatesWithCallback onFailure:" + e.getMessage());

                    });
        } catch (Exception e) {
            updateLogs("removeLocationUpdatesWithCallback exception:" + e.getMessage());
            AppLog.Debug(LocationKitFragment.class.getSimpleName(), "removeLocationUpdatesWithCallback exception:" + e.getMessage());
        }
    }

    private void updateLogs(String logs) {
        if (logs != null) {
            String currentLogs = txtLogs.getText().toString().trim();
            if (currentLogs.trim().length() > 0) {
                currentLogs = currentLogs + "\n\n";
            }
            currentLogs = currentLogs + logs;
            txtLogs.setText(currentLogs);
        }
    }
}
