package com.yasir.huaweicodelabs.fragments.mlkit.facemask.mindspore;

import com.yasir.huaweicodelabs.fragments.mlkit.facemask.MarkingBoxModel;

import java.util.ArrayList;

public interface OnMindSporeResults {
    void onResult(ArrayList<MarkingBoxModel> arrayList);
}
