package com.yasir.huaweicodelabs.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.yasir.huaweicodelabs.R;
import com.yasir.huaweicodelabs.fragments.HomeFragment;
import com.yasir.huaweicodelabs.repos.AlertsRepo;
import com.yasir.huaweicodelabs.repos.CustomTitleBarRepo;
import com.yasir.huaweicodelabs.repos.PermissionCallback;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.mainHeader)
    CustomTitleBarRepo mainHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Dependency Injection
        ButterKnife.bind(this);

        if (savedInstanceState == null) {
            initFragment();
        }

        checkPermissions(null);
    }

    private void initFragment() {
        addFragment(HomeFragment.newInstance(), HomeFragment.class.getSimpleName());
    }

    public void addFragment(Fragment fragment, String tag) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.mainFrameLayout, fragment, tag);
        fragmentTransaction.addToBackStack(getSupportFragmentManager().getBackStackEntryCount() == 0 ? "FirstFragment" : null).commit();
    }

    public void setHeading(String title) {
        mainHeader.hideButtons();
        mainHeader.setHeading(title);
    }

    public void hideTitleButtons() {
        mainHeader.hideButtons();
        mainHeader.hideHeading();
    }

    public void checkPermissions(PermissionCallback permissionCallback) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            String[] permissions = {Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};
            Permissions.check(this, permissions, getResources().getString(R.string.permission_request), null, new PermissionHandler() {
                @Override
                public void onGranted() {
                    if (permissionCallback != null) {
                        permissionCallback.onGrantedPermissions();
                    }
                }

                @Override
                public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                    if (permissionCallback != null) {
                        permissionCallback.onRejectedPermissions();
                    }
                    AlertsRepo.createMessageDialog(MainActivity.this, (dialog, which) -> dialog.dismiss(), getResources().getString(R.string.app_permissions), android.R.string.dialog_alert_title).show();
                }
            });
        } else {
            String[] permissions = { Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};
            Permissions.check(this, permissions, getResources().getString(R.string.permission_request), null, new PermissionHandler() {
                @Override
                public void onGranted() {
                    if (permissionCallback != null) {
                        permissionCallback.onGrantedPermissions();
                    }
                }

                @Override
                public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                    if (permissionCallback != null) {
                        permissionCallback.onRejectedPermissions();
                    }
                    AlertsRepo.createMessageDialog(MainActivity.this, (dialog, which) -> dialog.dismiss(), getResources().getString(R.string.app_permissions), android.R.string.dialog_alert_title).show();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            super.onBackPressed();
        } else {
            AlertsRepo.createQuitDialog(this, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            }, "Are you sure, you want to close the application?", "Confirmation").show();
        }
    }
}