package com.yasir.huaweicodelabs.fragments;

import androidx.fragment.app.Fragment;

import com.yasir.huaweicodelabs.activities.MainActivity;

public abstract class BaseFragment extends Fragment {

    protected MainActivity getMainActivity(){
        MainActivity mainActivity = (MainActivity) getActivity();
        while(mainActivity == null){
            mainActivity = (MainActivity) getActivity();
            try {
                Thread.sleep(50);
            } catch (Throwable throwable){
                // No need to print this catch
            }
        }
        return mainActivity;
    }
}
