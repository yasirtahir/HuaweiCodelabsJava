package com.yasir.huaweicodelabs.fragments.mlkit;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.huawei.hms.mlplugin.card.gcr.MLGcrCapture;
import com.huawei.hms.mlplugin.card.gcr.MLGcrCaptureConfig;
import com.huawei.hms.mlplugin.card.gcr.MLGcrCaptureFactory;
import com.huawei.hms.mlplugin.card.gcr.MLGcrCaptureResult;
import com.huawei.hms.mlplugin.card.gcr.MLGcrCaptureUIConfig;
import com.yasir.huaweicodelabs.R;
import com.yasir.huaweicodelabs.Utilities.AppLog;
import com.yasir.huaweicodelabs.fragments.BaseFragment;
import com.yasir.huaweicodelabs.repos.AlertsRepo;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GeneralCardRecognitionFragment extends BaseFragment implements View.OnClickListener {

    @BindView(R.id.btnCamera)
    Button btnCamera;

    @BindView(R.id.btnPhoto)
    Button btnPhoto;

    private View rootView;
    private final MLGcrCapture.Callback callback = new MLGcrCapture.Callback() {
        @Override
        public int onResult(MLGcrCaptureResult result, Object o){
            if (result != null) {
                AppLog.Error(GeneralCardRecognitionFragment.class.getSimpleName(), "Detected text --> " + result);
                if(result.text == null || result.text.getStringValue().trim().isEmpty()){
                    return MLGcrCaptureResult.CAPTURE_CONTINUE;
                } else {
                    AlertsRepo.createMessageDialog(getMainActivity(), (dialog, which) -> {
                    }, result.text.getStringValue().trim(), android.R.string.dialog_alert_title).show();
                }
            }
            return MLGcrCaptureResult.CAPTURE_STOP;
        }
        @Override
        public void onCanceled(){
            AppLog.Error(GeneralCardRecognitionFragment.class.getSimpleName(), "onCanceled");
        }
        @Override
        public void onFailure(int retCode, Bitmap bitmap){
            Toast.makeText(getMainActivity(), "Unable to read card. Please try again later", Toast.LENGTH_SHORT).show();
            AppLog.Error(GeneralCardRecognitionFragment.class.getSimpleName(), "onFailure");
            AppLog.Error(GeneralCardRecognitionFragment.class.getSimpleName(), "onFailure retCode ---> " + retCode);
            AppLog.Error(GeneralCardRecognitionFragment.class.getSimpleName(), "onFailure bitmap ---> " + bitmap);
        }
        @Override
        public void onDenied(){
            Toast.makeText(getMainActivity(), "Required permissions are missing. Please make sure all the permissions are granted", Toast.LENGTH_SHORT).show();
            AppLog.Error(GeneralCardRecognitionFragment.class.getSimpleName(), "onDenied");
        }
    };

    public static GeneralCardRecognitionFragment newInstance() {
        return new GeneralCardRecognitionFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getMainActivity().setHeading("General Card Recognition");

        btnCamera.setOnClickListener(this);
        btnPhoto.setOnClickListener(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_general_card_recog, container, false);
        } else {
            container.removeView(rootView);
        }

        ButterKnife.bind(this, rootView);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCamera:
                this.startCaptureVideo(callback);
                break;
            case R.id.btnPhoto:
                this.startCapturePhoto(callback);
                break;
        }
    }

    private void startCaptureVideo(MLGcrCapture.Callback callback) {
        MLGcrCaptureConfig cardConfig = new MLGcrCaptureConfig.Factory().create();
        MLGcrCaptureUIConfig uiConfig = new MLGcrCaptureUIConfig.Factory()
                .setScanBoxCornerColor(Color.WHITE)
                .setOrientation(MLGcrCaptureUIConfig.ORIENTATION_AUTO)
                .create();
        MLGcrCapture ocrManager = MLGcrCaptureFactory.getInstance().getGcrCapture(cardConfig, uiConfig);
        ocrManager.capturePreview(getMainActivity(), null, callback);
    }

    private void startCapturePhoto(MLGcrCapture.Callback callback) {
        MLGcrCaptureConfig cardConfig = new MLGcrCaptureConfig.Factory().create();
        MLGcrCaptureUIConfig uiConfig = new MLGcrCaptureUIConfig.Factory()
                .setScanBoxCornerColor(Color.WHITE)
                .setOrientation(MLGcrCaptureUIConfig.ORIENTATION_AUTO)
                .create();
        MLGcrCapture ocrManager = MLGcrCaptureFactory.getInstance().getGcrCapture(cardConfig, uiConfig);
        ocrManager.capturePhoto(getMainActivity(), null, callback);
    }
}
