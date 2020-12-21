package com.yasir.huaweicodelabs.fragments.applinkingkit;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.huawei.agconnect.applinking.AppLinking;
import com.huawei.agconnect.applinking.ShortAppLinking;
import com.yasir.huaweicodelabs.R;
import com.yasir.huaweicodelabs.Utilities.AppLog;
import com.yasir.huaweicodelabs.fragments.BaseFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AppLinkingKitFragment extends BaseFragment implements View.OnClickListener {

    @BindView(R.id.txtLongLink)
    TextView txtLongLink;

    @BindView(R.id.txtDeepLinking)
    TextView txtDeepLinking;

    @BindView(R.id.txtShortLink)
    TextView txtShortLink;

    @BindView(R.id.btnCreateAppLink)
    Button btnCreateAppLink;

    @BindView(R.id.btnShareLongLink)
    Button btnShareLongLink;

    @BindView(R.id.btnShareShortLink)
    Button btnShareShortLink;

    private View rootView;
    private String longLink, shortLink = "";
    private static final String DOMAIN_URI_PREFIX = "https://yasirsamplejavaapp.dra.agconnect.link";
    private static final String DEEP_LINK = "https://developer.huawei.com/consumer/en/doc/development/AppGallery-connect-Guides";

    public static AppLinkingKitFragment newInstance() {
        return new AppLinkingKitFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getMainActivity().setHeading("AppLinking Kit");

        btnCreateAppLink.setOnClickListener(this);
        btnShareLongLink.setOnClickListener(this);
        btnShareShortLink.setOnClickListener(this);

        setUI(); // This method is used to set the UI for the first time
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_app_linking_kit, container, false);
        } else {
            container.removeView(rootView);
        }

        ButterKnife.bind(this, rootView);

        return rootView;
    }

    private void setUI() {
        txtDeepLinking.setText(DEEP_LINK);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCreateAppLink:
                createAppLinking();
                break;
            case R.id.btnShareLongLink:
                shareLink("LONG");
                break;
            case R.id.btnShareShortLink:
                shareLink("SHORT");
                break;
        }
    }

    private void createAppLinking() {
        AppLinking.Builder builder = new AppLinking.Builder().setUriPrefix(DOMAIN_URI_PREFIX)
                .setDeepLink(Uri.parse(DEEP_LINK));

        // Setting the generated long link on the Long Text View
        longLink = builder.buildAppLinking().getUri().toString();
        String longText = getMainActivity().getResources().getString(R.string.long_link) + " " + longLink;
        txtLongLink.setText(longText);

        builder.buildShortAppLinking(ShortAppLinking.LENGTH.SHORT).addOnSuccessListener(shortAppLinking -> {
            // Setting the generated Short link on the Short Text View
            shortLink = shortAppLinking.getShortUrl().toString();
            String shortText = getMainActivity().getResources().getString(R.string.short_link) + " " + shortLink;
            txtShortLink.setText(shortText);
        }).addOnFailureListener(e -> {
            AppLog.Error(AppLinkingKitFragment.class.getSimpleName(), "Error --> " + e.getMessage());
            Toast.makeText(getMainActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
        });
    }

    private void shareLink(String tag) {
        String agcLink = tag.equals("LONG") ? longLink : shortLink;
        if (agcLink != null) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, agcLink);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
}
