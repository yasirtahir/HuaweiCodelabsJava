package com.yasir.huaweicodelabs.fragments.mlkit;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.huawei.agconnect.config.AGConnectServicesConfig;
import com.huawei.hms.mlplugin.asr.MLAsrCaptureActivity;
import com.huawei.hms.mlplugin.asr.MLAsrCaptureConstants;
import com.huawei.hms.mlsdk.common.MLApplication;
import com.yasir.huaweicodelabs.R;
import com.yasir.huaweicodelabs.Utilities.AppLog;
import com.yasir.huaweicodelabs.fragments.BaseFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MlKitFragment extends BaseFragment implements View.OnClickListener {

    @BindView(R.id.btnGeneralCard)
    Button btnGeneralCard;

    @BindView(R.id.btnASR)
    Button btnASR;

    private View rootView;
    private static final int ML_ASR_CAPTURE_CODE = 2002;

    public static MlKitFragment newInstance() {
        return new MlKitFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getMainActivity().setHeading("ML Kit");

        // Make sure agconnect-services.json file is added to the app folder and api_key value is readable from this file
        AGConnectServicesConfig config = AGConnectServicesConfig.fromContext(getMainActivity());
        MLApplication.getInstance().setApiKey(config.getString("client/api_key")); // Set API Key for ASR ML Kit

        btnGeneralCard.setOnClickListener(this);
        btnASR.setOnClickListener(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_ml_kit, container, false);
        } else {
            container.removeView(rootView);
        }

        ButterKnife.bind(this, rootView);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnGeneralCard:
                getMainActivity().addFragment(GeneralCardRecognitionFragment.newInstance(), GeneralCardRecognitionFragment.class.getSimpleName());
                break;
            case R.id.btnASR:
                startListeningAudio();
                break;
        }
    }

    private void startListeningAudio() {
        Intent intent = new Intent(getMainActivity(), MLAsrCaptureActivity.class)
                .putExtra(MLAsrCaptureConstants.LANGUAGE, "en-US") // Language set as English
                .putExtra(MLAsrCaptureConstants.FEATURE, MLAsrCaptureConstants.FEATURE_WORDFLUX);
        startActivityForResult(intent, ML_ASR_CAPTURE_CODE); // This Code is used in onActivityResult to get the response
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ML_ASR_CAPTURE_CODE) {
            switch (resultCode) {
                case MLAsrCaptureConstants.ASR_SUCCESS:
                    if (data != null) {
                        String text = "";
                        Bundle bundle = data.getExtras();
                        if (bundle != null && bundle.containsKey(MLAsrCaptureConstants.ASR_RESULT)) {
                            text = bundle.getString(MLAsrCaptureConstants.ASR_RESULT);
                        }
                        if (text != null && !"".equals(text)) {
                            Toast.makeText(getMainActivity(), text, Toast.LENGTH_LONG).show();
                            AppLog.Debug(MlKitFragment.class.getSimpleName(), text);
                        }
                    }
                    break;
                case MLAsrCaptureConstants.ASR_FAILURE:
                    if(data != null) {
                        Bundle bundle = data.getExtras();
                        if(bundle != null && bundle.containsKey(MLAsrCaptureConstants.ASR_ERROR_CODE)) {
                            int errorCode = bundle.getInt(MLAsrCaptureConstants.ASR_ERROR_CODE);
                            AppLog.Debug(MlKitFragment.class.getSimpleName(), "ASR Error Code --> " + errorCode);
                        }
                        if(bundle != null && bundle.containsKey(MLAsrCaptureConstants.ASR_ERROR_MESSAGE)){
                            String errorMsg = bundle.getString(MLAsrCaptureConstants.ASR_ERROR_MESSAGE);
                            AppLog.Debug(MlKitFragment.class.getSimpleName(), "ASR Error Messge --> " + errorMsg);
                        }
                        if(bundle != null && bundle.containsKey(MLAsrCaptureConstants.ASR_SUB_ERROR_CODE)) {
                            int subErrorCode = bundle.getInt(MLAsrCaptureConstants.ASR_SUB_ERROR_CODE);
                            AppLog.Debug(MlKitFragment.class.getSimpleName(), "ASR Sub Error Code --> " + subErrorCode);
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
