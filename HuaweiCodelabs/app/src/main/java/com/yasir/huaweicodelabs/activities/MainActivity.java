package com.yasir.huaweicodelabs.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.yasir.huaweicodelabs.R;
import com.yasir.huaweicodelabs.Utilities.TvUtil;
import com.yasir.huaweicodelabs.fragments.HomeFragment;
import com.yasir.huaweicodelabs.fragments.scankit.ScanKitFragment;
import com.yasir.huaweicodelabs.repos.AlertsRepo;
import com.yasir.huaweicodelabs.repos.CustomTitleBarRepo;
import com.yasir.huaweicodelabs.repos.PermissionCallback;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.yasir.huaweicodelabs.fragments.scankit.ScanKitFragment.REQUEST_CODE_SCAN_ONE;

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

        if (!TvUtil.isDirectToTV(this)) {
            checkPermissions(null);
        }
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

        if (!title.equalsIgnoreCase("Home")) {
            mainHeader.showLeftButton(R.drawable.ic_back, v -> onBackPressed());
        }
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
            String[] permissions = {Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_WIFI_STATE,
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
            AlertsRepo.createQuitDialog(this, (dialog, which) -> finish(),
                    getResources().getString(R.string.quit_msg), getResources().getString(R.string.quit_title)).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SCAN_ONE) {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(ScanKitFragment.class.getSimpleName());
            if (fragment != null) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }
}