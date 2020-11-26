package com.yasir.huaweicodelabs.fragments.safetydetect;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.huawei.agconnect.config.AGConnectServicesConfig;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.support.api.safetydetect.SafetyDetect;
import com.huawei.hms.support.api.safetydetect.SafetyDetectClient;
import com.huawei.hms.support.api.safetydetect.SafetyDetectStatusCodes;
import com.yasir.huaweicodelabs.R;
import com.yasir.huaweicodelabs.Utilities.AppLog;
import com.yasir.huaweicodelabs.fragments.BaseFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SafetyDetectFragment extends BaseFragment implements View.OnClickListener {

    @BindView(R.id.btnDetectUser)
    Button btnDetectUser;

    @BindView(R.id.txtLogs)
    TextView txtLogs;

    private View rootView;

    public static SafetyDetectFragment newInstance() {
        return new SafetyDetectFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getMainActivity().setHeading("Safety Detect Kit");

        btnDetectUser.setOnClickListener(this);

        initSafetyDetect(); // Init Safety Detect
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_safetydetect_kit, container, false);
        } else {
            container.removeView(rootView);
        }

        ButterKnife.bind(this, rootView);

        return rootView;
    }

    private void initSafetyDetect() {
        SafetyDetectClient client = SafetyDetect.getClient(getMainActivity());
        client.initUserDetect().addOnSuccessListener(v -> updateLogs("Safety Detect Init Success"))
                .addOnFailureListener(e -> updateLogs("Safety Detect Init Failed --> " + e.getMessage()));
    }


    @Override
    public void onClick(View v) {
        // Always make sure we are getting the correct callback of the click event
        if (v.getId() == R.id.btnDetectUser) {
            detectUser();
        }
    }

    private void detectUser() {
        SafetyDetectClient client = SafetyDetect.getClient(getMainActivity());
        String appId = AGConnectServicesConfig.fromContext(getMainActivity()).getString("client/app_id");
        client.userDetection(appId)
                .addOnSuccessListener(userDetectResponse -> {
                    // Indicates communication with the service was successful.
                    String responseToken = userDetectResponse.getResponseToken();
                    if (!responseToken.isEmpty()) {
                        updateLogs("SafetyDetect Token --> " + responseToken.trim());
                        updateLogs("Send the generated token to backend server for validation");
                    }
                })
                .addOnFailureListener(e -> {
                    // There was an error communicating with the service.
                    String errorMsg;
                    if (e instanceof ApiException) {
                        // An error with the HMS API contains some additional details.
                        // You can use the apiException.getStatusCode() method to get the status code.
                        ApiException apiException = (ApiException) e;
                        errorMsg = SafetyDetectStatusCodes.getStatusCodeString(apiException.getStatusCode()) + ": "
                                + apiException.getMessage();
                    } else {
                        // Unknown type of error has occurred.
                        errorMsg = e.getMessage();
                    }
                    AppLog.Debug(SafetyDetectFragment.class.getSimpleName(), "User detection fail. Error info: " + errorMsg);
                    updateLogs("User detection fail. Error info: " + errorMsg);
                });
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
