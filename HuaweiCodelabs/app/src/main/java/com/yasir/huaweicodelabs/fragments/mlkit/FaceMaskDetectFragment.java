package com.yasir.huaweicodelabs.fragments.mlkit;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.huawei.hms.ads.App;
import com.huawei.hms.mlsdk.MLAnalyzerFactory;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.face.MLFace;
import com.huawei.hms.mlsdk.face.MLFaceAnalyzer;
import com.huawei.hms.mlsdk.face.MLFaceAnalyzerSetting;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.frame.Frame;
import com.yasir.huaweicodelabs.R;
import com.yasir.huaweicodelabs.Utilities.AppLog;
import com.yasir.huaweicodelabs.Utilities.MediaPlayerUtil;
import com.yasir.huaweicodelabs.fragments.BaseFragment;
import com.yasir.huaweicodelabs.fragments.mlkit.facemask.CameraOverlayView;
import com.yasir.huaweicodelabs.fragments.mlkit.facemask.MarkingBoxModel;
import com.yasir.huaweicodelabs.fragments.mlkit.facemask.mindspore.MindSporeProcessor;
import com.yasir.huaweicodelabs.fragments.mlkit.facemask.mindspore.OnMindSporeResults;
import com.yasir.huaweicodelabs.fragments.mlkit.facemask.tensorflow.TensorFlowProcessor;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FaceMaskDetectFragment extends BaseFragment implements View.OnClickListener {

    @BindView(R.id.cameraView)
    CameraView cameraView;

    @BindView(R.id.overlayView)
    CameraOverlayView cameraOverlayView;

    @BindView(R.id.btnSwitchCamera)
    FloatingActionButton btnSwitchCamera;

    @BindView(R.id.btnToggleSound)
    FloatingActionButton btnToggleSound;

    @BindView(R.id.btnProcessor)
    ImageButton btnProcessor;

    @BindView(R.id.imgChange)
    ImageView imgChange;

    private View rootView;
    private MLFaceAnalyzer mAnalyzer;
    private MindSporeProcessor mMindSporeProcessor;
    private boolean isSound = false;
    private boolean isMindSpore = false;

    public static FaceMaskDetectFragment newInstance() {
        return new FaceMaskDetectFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getMainActivity().setHeading("Face Mask Detection");

        initObjects();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_face_detect, container, false);
        } else {
            container.removeView(rootView);
        }

        ButterKnife.bind(this, rootView);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSwitchCamera:
                cameraView.toggleFacing();
                break;
            case R.id.btnToggleSound:
                isSound = !isSound;
                toggleSound();
                break;
            case R.id.imgChange:
            case R.id.btnProcessor:
                isMindSpore = !isMindSpore;
                toggleProcessor();
                break;
        }
    }

    private void initObjects() {

        btnSwitchCamera.setOnClickListener(this);
        btnToggleSound.setOnClickListener(this);
        imgChange.setOnClickListener(this);
        btnProcessor.setOnClickListener(this);

        btnToggleSound.setBackgroundTintList(ColorStateList.valueOf(getMainActivity().getResources().getColor(R.color.colorGrey)));

        cameraView.setLifecycleOwner(this); // This refers to Camera Lifecycle based on different states

        if (mAnalyzer == null) {
            // Use custom parameter settings, and enable the speed preference mode and face tracking function to obtain a faster speed.
            MLFaceAnalyzerSetting setting = new MLFaceAnalyzerSetting.Factory()
                    .setPerformanceType(MLFaceAnalyzerSetting.TYPE_SPEED)
                    .setTracingAllowed(false)
                    .create();
            mAnalyzer = MLAnalyzerFactory.getInstance().getFaceAnalyzer(setting);
        }

        if (mMindSporeProcessor == null) {
            mMindSporeProcessor = new MindSporeProcessor(getMainActivity(), arrayList -> {
                cameraOverlayView.setBoundingMarkingBoxModels(arrayList);
                cameraOverlayView.invalidate();
            }, isSound);
        }

        cameraView.addFrameProcessor(this::processCameraFrame);
    }

    private void processCameraFrame(Frame frame) {
        Matrix matrix = new Matrix();
        matrix.setRotate(frame.getRotationToUser());
        matrix.preScale(1, -1);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        YuvImage yuvImage = new YuvImage(
                frame.getData(),
                ImageFormat.NV21,
                frame.getSize().getWidth(),
                frame.getSize().getHeight(),
                null
        );
        yuvImage.compressToJpeg(new
                        Rect(0, 0, frame.getSize().getWidth(), frame.getSize().getHeight()),
                100, out);
        byte[] imageBytes = out.toByteArray();
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        bitmap = Bitmap.createScaledBitmap(bitmap, cameraOverlayView.getWidth(), cameraOverlayView.getHeight(), true);

        AppLog.Error(FaceMaskDetectFragment.class.getSimpleName(), "Current Processor --> " + (isMindSpore ? "MindSpore" : "Tensorflow"));

        if (isMindSpore) {
            // MindSpore Processor
            findFacesMindSpore(bitmap);
        } else {
            // TensorFlow Processor
            findFacesTensorFlow(bitmap);
        }
    }

    private void findFacesMindSpore(Bitmap bitmap) {

        MLFrame frame = MLFrame.fromBitmap(bitmap);
        SparseArray<MLFace> faces = mAnalyzer.analyseFrame(frame);

        for (int i = 0; i < faces.size(); i++) {
            MLFace thisFace = faces.get(i); // Getting the face object recognized by HMS ML Kit

            // Crop the image to face and pass it to MindSpore processor
            float left = thisFace.getCoordinatePoint().x;
            float top = thisFace.getCoordinatePoint().y;
            float right = left + thisFace.getWidth();
            float bottom = top + thisFace.getHeight();

            Bitmap bitmapCropped = Bitmap.createBitmap(bitmap, (int) left, (int) top,
                    ((int) right > bitmap.getWidth() ? bitmap.getWidth() - (int) left : (int) thisFace.getWidth()),
                    (((int) bottom) > bitmap.getHeight() ? bitmap.getHeight() - (int) top : (int) thisFace.getHeight()));

            // Pass the cropped image to MindSpore processor to check
            mMindSporeProcessor.processFaceImages(bitmapCropped, thisFace.getBorder(), isSound);
        }
    }

    private void findFacesTensorFlow(Bitmap bitmap) {
        // Tensorflow Processor
        cameraOverlayView.setBoundingMarkingBoxModels(TensorFlowProcessor.processFaceImages(bitmap, mAnalyzer, getMainActivity(), isSound));
        cameraOverlayView.invalidate();
    }

    private void toggleSound() {
        if (isSound) {
            btnToggleSound.setImageResource(R.drawable.ic_img_sound);
            btnToggleSound.setBackgroundTintList(ColorStateList.valueOf(getMainActivity().getResources().getColor(R.color.colorAccent)));
        } else {
            btnToggleSound.setImageResource(R.drawable.ic_img_sound_disable);
            btnToggleSound.setBackgroundTintList(ColorStateList.valueOf(getMainActivity().getResources().getColor(R.color.colorGrey)));
        }
    }

    private void toggleProcessor() {
        if (isMindSpore) {
            btnProcessor.setImageResource(R.drawable.mindspore);
        } else {
            btnProcessor.setImageResource(R.drawable.tflite);
        }
        cameraOverlayView.setBoundingMarkingBoxModels(new ArrayList<>());
        cameraOverlayView.invalidate();
    }

    @Override
    public void onPause() {
        super.onPause();
        MediaPlayerUtil.stopSound();
    }
}
