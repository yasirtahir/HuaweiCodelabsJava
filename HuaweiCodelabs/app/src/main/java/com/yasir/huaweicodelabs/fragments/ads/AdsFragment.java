package com.yasir.huaweicodelabs.fragments.ads;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.huawei.hms.ads.AdListener;
import com.huawei.hms.ads.AdParam;
import com.huawei.hms.ads.InterstitialAd;
import com.yasir.huaweicodelabs.R;
import com.yasir.huaweicodelabs.Utilities.AppLog;
import com.yasir.huaweicodelabs.fragments.BaseFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AdsFragment extends BaseFragment {

    @BindView(R.id.btnInitAd)
    Button btnInitAd;

    private View rootView;
    private InterstitialAd mInterstitialAd;

    private AdListener adListener = new AdListener() {
        @Override
        public void onAdLoaded() {
            super.onAdLoaded();
            // Display an interstitial ad.
            showInterstitial();
        }

        @Override
        public void onAdFailed(int errorCode) {
            Toast.makeText(getMainActivity(), "Ad load failed with error code: " + errorCode, Toast.LENGTH_SHORT).show();
            AppLog.Debug(AdsFragment.class.getSimpleName(), "Ad load failed with error code: " + errorCode);
        }

        @Override
        public void onAdClosed() {
            super.onAdClosed();
            AppLog.Debug(AdsFragment.class.getSimpleName(), "onAdClosed");
        }

        @Override
        public void onAdClicked() {
            AppLog.Debug(AdsFragment.class.getSimpleName(), "onAdClicked");
            super.onAdClicked();
        }

        @Override
        public void onAdOpened() {
            AppLog.Debug(AdsFragment.class.getSimpleName(), "onAdOpened");
            super.onAdOpened();
        }
    };

    public static AdsFragment newInstance() {
        return new AdsFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getMainActivity().setHeading("Ad Kit");

        btnInitAd.setOnClickListener(v -> {
            // Display an interstitial ad.
            loadInterstitialAd();
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_ads, container, false);
        } else {
            container.removeView(rootView);
        }

        ButterKnife.bind(this, rootView);

        return rootView;
    }

    private void loadInterstitialAd() {
        mInterstitialAd = new InterstitialAd(getMainActivity());
        mInterstitialAd.setAdId(getAdId()); // Set an ad slot ID.

        mInterstitialAd.setAdListener(adListener);

        // Load an interstitial ad.
        AdParam adParam = new AdParam.Builder().build();
        mInterstitialAd.loadAd(adParam);
    }

    private String getAdId() {
//        if (displayRadioGroup.getCheckedRadioButtonId() == R.id.display_image) {
            return getString(R.string.image_ad_id); // The value of image_ad_id is teste9ih9j0rc3.
//        } else {
//            return getString(R.string.video_ad_id); // The value of video_ad_id is testb4znbuh3n2.
//        }
    }

    private void showInterstitial() {
        // Display an interstitial ad.
        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Toast.makeText(getMainActivity(), "Interstitial Ad did not load", Toast.LENGTH_SHORT).show();
        }
    }
}
