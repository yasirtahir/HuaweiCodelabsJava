package com.yasir.huaweicodelabs.fragments.mlkit.facemask.tensorflow;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.SparseArray;

import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.face.MLFace;
import com.huawei.hms.mlsdk.face.MLFaceAnalyzer;
import com.yasir.huaweicodelabs.Utilities.AppLog;
import com.yasir.huaweicodelabs.activities.MainActivity;
import com.yasir.huaweicodelabs.fragments.mlkit.FaceMaskDetectFragment;
import com.yasir.huaweicodelabs.fragments.mlkit.facemask.MarkingBoxModel;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.image.ops.ResizeWithCropOrPadOp;
import org.tensorflow.lite.support.label.TensorLabel;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.nio.MappedByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class TensorFlowProcessor {

    public static ArrayList<MarkingBoxModel> processFaceImages(Bitmap bitmap, MLFaceAnalyzer mAnalyzer, MainActivity activity, boolean isSound) {

        ArrayList<MarkingBoxModel> markingBoxModelList = new ArrayList<>();
        MLFrame frame = MLFrame.fromBitmap(bitmap);
        SparseArray<MLFace> faces = mAnalyzer.analyseFrame(frame);

        for (int i = 0; i < faces.size(); i++) {
            MLFace thisFace = faces.get(i); // Getting the face object recognized by HMS ML Kit

            // Crop the image to face and pass it to Tensor Flow to check
            float left = thisFace.getCoordinatePoint().x;
            float top = thisFace.getCoordinatePoint().y;
            float right = left + thisFace.getWidth();
            float bottom = top + thisFace.getHeight();

            Bitmap bitmapCropped = Bitmap.createBitmap(bitmap, (int) left, (int) top,
                    ((int) right > bitmap.getWidth() ? bitmap.getWidth() - (int) left : (int) thisFace.getWidth()),
                    (((int) bottom) > bitmap.getHeight() ? bitmap.getHeight() - (int) top : (int) thisFace.getHeight()));

            // Pass the cropped image to tensorflow model to check
            HashMap<String, Float> label = tensorFlowPredictions(activity, bitmapCropped);

            String result;
            if(label.get("WithMask") != null && label.get("WithoutMask") != null){
                Float with = label.get("WithMask");
                Float without = label.get("WithoutMask");

                if (with != null && without != null) {

                    with = with * 100;
                    without = without * 100;

                    float maxValue = Math.max(with, without);

                    if (maxValue == with && with > 90) {
                        result = "Wearing Mask: " + String.format(new Locale("en"), "%.1f", with) + "%";
                    } else {
                        result = "Not wearing Mask: " + String.format(new Locale("en"), "%.1f", without) + "%";
                    }
                    if (!result.trim().isEmpty()) {
                        // Add this to our Overlay List as Box with Result and Percentage
                        markingBoxModelList.add(new MarkingBoxModel(new Rect((int) left, (int) top, (int) right, (int) bottom), result, maxValue == with && with > 90, isSound));
                    }
                }
            }
        }
        return markingBoxModelList;
    }

    private static HashMap<String, Float> tensorFlowPredictions(MainActivity activity, Bitmap faceImage) {

        HashMap<String, Float> results = new HashMap<>();

        try {
            // Loading the Model
            MappedByteBuffer mappedByteBuffer = FileUtil.loadMappedFile(activity, "model.tflite");
            Interpreter interpreter = new Interpreter(mappedByteBuffer, new Interpreter.Options());
            List<String> definedLabels = FileUtil.loadLabels(activity, "labels.txt");
            // Set Data Type
            DataType imageDataType = interpreter.getInputTensor(0).dataType();
            int[] inputShape = interpreter.getInputTensor(0).shape();
            DataType outputDataType = interpreter.getOutputTensor(0).dataType();
            int[] outputShape = interpreter.getOutputTensor(0).shape();
            // Load the Image into tensorImage
            TensorImage tensorImage = new TensorImage(imageDataType);
            TensorBuffer tensorBuffer = TensorBuffer.createFixedSize(outputShape, outputDataType);
            int cropSize = Math.min(faceImage.getWidth(), faceImage.getHeight());
            ImageProcessor imageProcessor = new ImageProcessor.Builder()
                    .add(new ResizeWithCropOrPadOp(cropSize, cropSize))
                    .add(new ResizeOp(inputShape[1], inputShape[2], ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
                    .add(new NormalizeOp(127.5f, 127.5f))
                    .build();
            tensorImage.load(faceImage);
            tensorImage = imageProcessor.process(tensorImage);
            // Run the model to find the results against defined labels
            interpreter.run(tensorImage.getBuffer(), tensorBuffer.getBuffer().rewind());
            // Get the final Output
            TensorLabel labelOutput = new TensorLabel(definedLabels, tensorBuffer);
            results.putAll(labelOutput.getMapWithFloatValue());
        } catch (Throwable throwable) {
            AppLog.Error(FaceMaskDetectFragment.class.getSimpleName(), throwable.getMessage());
            throwable.printStackTrace();
        }
        return results;
    }
}
