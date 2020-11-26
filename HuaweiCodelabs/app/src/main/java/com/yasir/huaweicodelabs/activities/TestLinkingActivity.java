package com.yasir.huaweicodelabs.activities;

import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.huawei.agconnect.applinking.AGConnectAppLinking;
import com.yasir.huaweicodelabs.R;
import com.yasir.huaweicodelabs.Utilities.AppLog;
import com.yasir.huaweicodelabs.repos.CustomTitleBarRepo;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TestLinkingActivity extends AppCompatActivity {

    @BindView(R.id.txtViewDisplay)
    TextView txtViewDisplay;

    @BindView(R.id.mainHeader)
    CustomTitleBarRepo mainHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_linking);

        // Dependency Injection
        ButterKnife.bind(this);

        setHeading("Redirection Activity");

        processDeepLinkText();
    }

    public void setHeading(String title) {
        mainHeader.hideButtons();
        mainHeader.setHeading(title);
    }

    private void processDeepLinkText(){
        AGConnectAppLinking.getInstance().getAppLinking(this).addOnSuccessListener(resolvedLinkData -> {
            Uri deepLink;
            if (resolvedLinkData != null) {
                deepLink = resolvedLinkData.getDeepLink();
                String text = "Welcome to " + getResources().getString(R.string.splash_name) + ".\n\n\nYou have been redirected to this app by the link: \n" + deepLink.toString();
                txtViewDisplay.setText(text);
            }
        }).addOnFailureListener(e -> AppLog.Error(TestLinkingActivity.class.getSimpleName(), "getAppLinking:onFailure " + e));
    }
}
