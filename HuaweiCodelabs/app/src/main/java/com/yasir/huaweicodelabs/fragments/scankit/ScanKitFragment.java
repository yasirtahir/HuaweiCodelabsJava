package com.yasir.huaweicodelabs.fragments.scankit;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScan;
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions;
import com.yasir.huaweicodelabs.R;
import com.yasir.huaweicodelabs.Utilities.AppLog;
import com.yasir.huaweicodelabs.fragments.BaseFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;

public class ScanKitFragment extends BaseFragment implements View.OnClickListener {

    @BindView(R.id.btnDefaultView)
    Button btnDefaultView;

    @BindView(R.id.btnCustomView)
    Button btnCustomView;

    private View rootView;
    public static final int REQUEST_CODE_SCAN_ONE = 2202;

    public static ScanKitFragment newInstance() {
        return new ScanKitFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getMainActivity().setHeading("Scan Kit");

        btnDefaultView.setOnClickListener(this);
        btnCustomView.setOnClickListener(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_scan_kit, container, false);
        } else {
            container.removeView(rootView);
        }

        ButterKnife.bind(this, rootView);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnDefaultView:
                showDefaultView();
                break;
            case R.id.btnCustomView:
                showCustomView();
                break;
        }
    }

    private void showCustomView(){
        getMainActivity().addFragment(CustomizedViewFragment.newInstance(), CustomizedViewFragment.class.getSimpleName());
    }

    private void showDefaultView() {
        HmsScanAnalyzerOptions options = new HmsScanAnalyzerOptions.Creator()
                .setHmsScanTypes(HmsScan.ALL_SCAN_TYPE).create(); // Set the type of code you want to scan
        ScanUtil.startScan(getMainActivity(), REQUEST_CODE_SCAN_ONE, options);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK || data == null) {
            return;
        }

        if (requestCode == REQUEST_CODE_SCAN_ONE) {
            AppLog.Error(ScanKitFragment.class.getSimpleName(), data.getDataString());
            // Input an image for scanning and return the result.
            HmsScan hmsScan = data.getParcelableExtra(ScanUtil.RESULT);
            if (hmsScan != null && hmsScan.getOriginalValue() != null) {
                Toast.makeText(getMainActivity(), hmsScan.getOriginalValue(), Toast.LENGTH_LONG).show();
                // Display the decoding result.
                AppLog.Error(ScanKitFragment.class.getSimpleName(), hmsScan.getOriginalValue());
            }
        }
    }

}
