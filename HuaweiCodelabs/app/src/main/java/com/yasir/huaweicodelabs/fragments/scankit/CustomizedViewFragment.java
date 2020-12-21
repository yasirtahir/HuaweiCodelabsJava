package com.yasir.huaweicodelabs.fragments.scankit;

import android.Manifest;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.huawei.hms.hmsscankit.OnResultCallback;
import com.huawei.hms.hmsscankit.RemoteView;
import com.huawei.hms.ml.scan.HmsScan;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.yasir.huaweicodelabs.R;
import com.yasir.huaweicodelabs.Utilities.AppLog;
import com.yasir.huaweicodelabs.activities.MainActivity;
import com.yasir.huaweicodelabs.fragments.BaseFragment;
import com.yasir.huaweicodelabs.repos.AlertsRepo;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CustomizedViewFragment extends BaseFragment {

    @BindView(R.id.customFrameLayout)
    FrameLayout customFrameLayout;

    private View rootView;
    private RemoteView remoteView;

    public static CustomizedViewFragment newInstance() {
        return new CustomizedViewFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getMainActivity().setHeading("Customized View");

        checkPermissions();

        initView(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_customized_view, container, false);
        } else {
            container.removeView(rootView);
        }

        ButterKnife.bind(this, rootView);

        return rootView;
    }

    private void checkPermissions(){
        String[] permissions = {Manifest.permission.CAMERA};
        Permissions.check(getMainActivity(), permissions, getResources().getString(R.string.permission_request), null, new PermissionHandler() {
            @Override
            public void onGranted() {

            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {

            }
        });
    }

    private void initView(Bundle savedInstanceState){
        DisplayMetrics dm = getResources().getDisplayMetrics();
        float density = dm.density;
        int mScreenWidth = getResources().getDisplayMetrics().widthPixels;
        int mScreenHeight = getResources().getDisplayMetrics().heightPixels;
        final int SCAN_FRAME_SIZE = 300;
        int scanFrameSize = (int) (SCAN_FRAME_SIZE * density);
        Rect rect = new Rect();
        rect.left = mScreenWidth / 2 - scanFrameSize / 2;
        rect.right = mScreenWidth / 2 + scanFrameSize / 2;
        rect.top = mScreenHeight / 2 - scanFrameSize / 2;
        rect.bottom = mScreenHeight / 2 + scanFrameSize / 2;
        remoteView = new RemoteView.Builder().setContext(getMainActivity())
                .setBoundingBox(rect).setContinuouslyScan(false)
                .setFormat(HmsScan.ALL_SCAN_TYPE).build(); // Set the type of code you want to scan
        remoteView.onCreate(savedInstanceState);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        customFrameLayout.addView(remoteView, params);

        remoteView.setOnResultCallback(new OnResultCallback() {
            @Override
            public void onResult(HmsScan[] result) {
                // Obtain the scanning result object HmsScan.
                if (result != null && result.length > 0 && result[0].getOriginalValue() != null) {
                    // We are getting only the first scanned code
                    Toast.makeText(getMainActivity(), result[0].getOriginalValue(), Toast.LENGTH_LONG).show();
                    // Display the decoding result.
                    AppLog.Error(ScanKitFragment.class.getSimpleName(), result[0].getOriginalValue());
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        remoteView.onStart();
    }
    @Override
    public void onResume() {
        super.onResume();
        remoteView.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
        remoteView.onPause();
    }
    @Override
    public void onStop() {
        super.onStop();
        remoteView.onStop();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        remoteView.onDestroy();
    }
}
