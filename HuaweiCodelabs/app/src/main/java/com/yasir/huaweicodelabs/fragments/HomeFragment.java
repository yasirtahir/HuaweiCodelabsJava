package com.yasir.huaweicodelabs.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.huawei.agconnect.config.AGConnectServicesConfig;
import com.huawei.hms.aaid.HmsInstanceId;
import com.huawei.hms.analytics.HiAnalytics;
import com.huawei.hms.analytics.HiAnalyticsInstance;
import com.huawei.hms.common.ApiException;
import com.yasir.huaweicodelabs.R;
import com.yasir.huaweicodelabs.Utilities.AppConstant;
import com.yasir.huaweicodelabs.Utilities.AppLog;
import com.yasir.huaweicodelabs.fragments.accountkit.AccountKitFragment;
import com.yasir.huaweicodelabs.fragments.ads.AdsFragment;
import com.yasir.huaweicodelabs.fragments.applinkingkit.AppLinkingKitFragment;
import com.yasir.huaweicodelabs.fragments.locationkit.LocationKitFragment;
import com.yasir.huaweicodelabs.fragments.mapkit.MapKitFragment;
import com.yasir.huaweicodelabs.fragments.mlkit.MlKitFragment;
import com.yasir.huaweicodelabs.fragments.safetydetect.SafetyDetectFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeFragment extends BaseFragment implements View.OnClickListener {

    @BindView(R.id.btnAccountKit)
    Button btnAccountKit;

    @BindView(R.id.btnGeneratePushToken)
    Button btnGeneratePushToken;

    @BindView(R.id.btnSendCustomEvent)
    Button btnSendCustomEvent;

    @BindView(R.id.btnLocationKit)
    Button btnLocationKit;

    @BindView(R.id.btnMapKit)
    Button btnMapKit;

    @BindView(R.id.btnAppLinking)
    Button btnAppLinking;

    @BindView(R.id.btnSafetyDetect)
    Button btnSafetyDetect;

    @BindView(R.id.btnAds)
    Button btnAds;

    @BindView(R.id.btnMlKit)
    Button btnMlKit;

    @BindView(R.id.txtPushToken)
    TextView txtPushToken;

    private View rootView;
    private BroadcastReceiver pushTokenReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Checking is the action is same as what we are expecting for
            // This is only called when newToken method is called from the PushService
            if (AppConstant.PUSH_TOKEN.equals(intent.getAction())) {
                String token = intent.getStringExtra("token");
                showPushToken(token);
            }
        }
    };

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getMainActivity().setHeading("Home");

        // Setting listeners
        btnAccountKit.setOnClickListener(this);
        btnGeneratePushToken.setOnClickListener(this);
        btnSendCustomEvent.setOnClickListener(this);
        btnLocationKit.setOnClickListener(this);
        btnMapKit.setOnClickListener(this);
        btnAppLinking.setOnClickListener(this);
        btnSafetyDetect.setOnClickListener(this);
        btnAds.setOnClickListener(this);
        btnMlKit.setOnClickListener(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_home, container, false);
        } else {
            container.removeView(rootView);
        }

        ButterKnife.bind(this, rootView);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAccountKit:
                sendCustomEvent("AccountKitButton");
                getMainActivity().addFragment(AccountKitFragment.newInstance(), AccountKitFragment.class.getSimpleName());
                break;
            case R.id.btnGeneratePushToken:
                generatePushToken();
                sendCustomEvent("GeneratePushTokenButton");
                break;
            case R.id.btnSendCustomEvent:
                sendCustomEvent("CustomEventButton");
                break;
            case R.id.btnLocationKit:
                sendCustomEvent("LocationKitButton");
                getMainActivity().addFragment(LocationKitFragment.newInstance(), LocationKitFragment.class.getSimpleName());
                break;
            case R.id.btnMapKit:
                sendCustomEvent("MapKitButton");
                getMainActivity().addFragment(MapKitFragment.newInstance(), MapKitFragment.class.getSimpleName());
                break;
            case R.id.btnAppLinking:
                sendCustomEvent("AppLinkingButton");
                getMainActivity().addFragment(AppLinkingKitFragment.newInstance(), AppLinkingKitFragment.class.getSimpleName());
                break;
            case R.id.btnSafetyDetect:
                sendCustomEvent("SafetyDetectButton");
                getMainActivity().addFragment(SafetyDetectFragment.newInstance(), SafetyDetectFragment.class.getSimpleName());
                break;
            case R.id.btnAds:
                sendCustomEvent("AdsButton");
                getMainActivity().addFragment(AdsFragment.newInstance(), AdsFragment.class.getSimpleName());
                break;
            case R.id.btnMlKit:
                sendCustomEvent("MLKitButton");
                getMainActivity().addFragment(MlKitFragment.newInstance(), MlKitFragment.class.getSimpleName());
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Registering to receive the updates of Push Token
        getMainActivity().registerReceiver(pushTokenReceiver, new IntentFilter(AppConstant.PUSH_TOKEN));

    }

    @Override
    public void onPause() {
        super.onPause();
        // Unregistering in order to avoid memory leak
        getMainActivity().unregisterReceiver(pushTokenReceiver);
    }

    private void generatePushToken(){
        new Thread() {
            @Override
            public void run() {
                try {
                    // Read from agconnect-services.json
                    String appId = AGConnectServicesConfig.fromContext(getMainActivity()).getString("client/app_id");
                    String token = HmsInstanceId.getInstance(getMainActivity()).getToken(appId, "HCM");
                    AppLog.Debug(HomeFragment.class.getSimpleName(), "get token:" + token);
                    showPushToken(token);
                } catch (ApiException e) {
                    AppLog.Debug(HomeFragment.class.getSimpleName(), "get token failed, " + e);
                }
            }
        }.start();
    }

    private void showPushToken(String token){
        if(!TextUtils.isEmpty(token)) {
            getMainActivity().runOnUiThread(() -> txtPushToken.setText(token));
        }
    }

    private void sendCustomEvent(String tag){
        HiAnalyticsInstance instance = HiAnalytics.getInstance(getMainActivity());

        Bundle bundle = new Bundle();
        bundle.putString("BUTTON_CLICKED", tag);

        instance.onEvent(tag, bundle);

        String showMessage = tag + " event has been sent to Analytics Console";

        Toast.makeText(getMainActivity(), showMessage, Toast.LENGTH_SHORT).show();
    }
}
