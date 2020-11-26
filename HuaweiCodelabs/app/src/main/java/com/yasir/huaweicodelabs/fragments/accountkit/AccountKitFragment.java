package com.yasir.huaweicodelabs.fragments.accountkit;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.support.hwid.HuaweiIdAuthManager;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParams;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParamsHelper;
import com.huawei.hms.support.hwid.result.AuthHuaweiId;
import com.huawei.hms.support.hwid.service.HuaweiIdAuthService;
import com.yasir.huaweicodelabs.BuildConfig;
import com.yasir.huaweicodelabs.R;
import com.yasir.huaweicodelabs.Utilities.AppConstant;
import com.yasir.huaweicodelabs.Utilities.AppLog;
import com.yasir.huaweicodelabs.fragments.BaseFragment;
import com.yasir.huaweicodelabs.models.TokenModel;
import com.yasir.huaweicodelabs.repos.HttpResponseCallback;
import com.yasir.huaweicodelabs.targets.oauth.OAuthImplementation;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class AccountKitFragment extends BaseFragment implements View.OnClickListener {

    @BindView(R.id.btnTokenSignIn)
    Button btnTokenSignIn;

    @BindView(R.id.btnAuthorizationSignIn)
    Button btnAuthorizationSignIn;

    @BindView(R.id.btnSignOut)
    Button btnSignOut;

    @BindView(R.id.txtLogs)
    TextView txtLogs;

    private HuaweiIdAuthService mAuthManager;
    private HuaweiIdAuthParams mAuthParam;
    private View rootView;
    private static final int REQUEST_SIGN_IN_LOGIN = 1001;
    private static final int REQUEST_SIGN_IN_LOGIN_CODE = 1002;

    public static AccountKitFragment newInstance() {
        return new AccountKitFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getMainActivity().setHeading("Account Kit");

        txtLogs.setMovementMethod(new ScrollingMovementMethod());

        // Setting click listeners
        btnTokenSignIn.setOnClickListener(this);
        btnAuthorizationSignIn.setOnClickListener(this);
        btnSignOut.setOnClickListener(this);

        // Update Logs at the start
        updateLogs("");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_account_kit, container, false);
        } else {
            container.removeView(rootView);
        }

        ButterKnife.bind(this, rootView);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnTokenSignIn:
                signIn();
                break;
            case R.id.btnAuthorizationSignIn:
                signInCode();
                break;
            case R.id.btnSignOut:
                signOut();
                break;
        }
    }

    private void updateLogs(String logs){
        if(logs != null){
            String currentLogs = txtLogs.getText().toString().trim();
            if(currentLogs.trim().length() > 0){
                currentLogs = currentLogs + "\n\n";
            }
            currentLogs = currentLogs + logs;
            txtLogs.setText(currentLogs);
        }
    }

    private void signIn() {
        mAuthParam = new HuaweiIdAuthParamsHelper(HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
                .setIdToken()
                .setAccessToken()
                .createParams();
        mAuthManager = HuaweiIdAuthManager.getService(Objects.requireNonNull(getMainActivity()), mAuthParam);
        startActivityForResult(mAuthManager.getSignInIntent(), REQUEST_SIGN_IN_LOGIN);
    }

    private void signInCode() {
        mAuthParam = new HuaweiIdAuthParamsHelper(HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
                .setProfile()
                .setAuthorizationCode()
                .createParams();
        mAuthManager = HuaweiIdAuthManager.getService(Objects.requireNonNull(getMainActivity()), mAuthParam);
        startActivityForResult(mAuthManager.getSignInIntent(), REQUEST_SIGN_IN_LOGIN_CODE);
    }

    private void signOut() {
        if (null != mAuthManager) {
            Task<Void> signOutTask = mAuthManager.signOut();
            signOutTask.addOnSuccessListener(aVoid -> {
                AppLog.Debug(AccountKitFragment.class.getSimpleName(), "signOut Success");
                updateLogs("signOut Success");
            }).addOnFailureListener(e -> {
                AppLog.Debug(AccountKitFragment.class.getSimpleName(), "signOut fail");
                updateLogs("signOut fail");
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SIGN_IN_LOGIN) {
            //login success
            Task<AuthHuaweiId> authHuaweiIdTask = HuaweiIdAuthManager.parseAuthResultFromIntent(data);
            if (authHuaweiIdTask.isSuccessful()) {
                AuthHuaweiId huaweiAccount = authHuaweiIdTask.getResult();
                updateLogs(huaweiAccount.getDisplayName() + " signIn success ");
                updateLogs("AccessToken: " + huaweiAccount.getAccessToken());
                AppLog.Debug(AccountKitFragment.class.getSimpleName(), huaweiAccount.getDisplayName() + " signIn success ");
                AppLog.Debug(AccountKitFragment.class.getSimpleName(), "AccessToken: " + huaweiAccount.getAccessToken());
            } else {
                updateLogs("signIn failed: " + ((ApiException) authHuaweiIdTask.getException()).getStatusCode());
                AppLog.Debug(AccountKitFragment.class.getSimpleName(), "signIn failed: " + ((ApiException) authHuaweiIdTask.getException()).getStatusCode());
            }
        }
        if (requestCode == REQUEST_SIGN_IN_LOGIN_CODE) {
            //login success
            Task<AuthHuaweiId> authHuaweiIdTask = HuaweiIdAuthManager.parseAuthResultFromIntent(data);
            if (authHuaweiIdTask.isSuccessful()) {
                AuthHuaweiId huaweiAccount = authHuaweiIdTask.getResult();
                updateLogs("signIn get code success.");
                updateLogs("ServerAuthCode: " + huaweiAccount.getAuthorizationCode());
                validateToken(huaweiAccount.getAuthorizationCode());
                AppLog.Debug(AccountKitFragment.class.getSimpleName(), "signIn get code success.");
                AppLog.Debug(AccountKitFragment.class.getSimpleName(),"ServerAuthCode: " + huaweiAccount.getAuthorizationCode());
            } else {
                updateLogs("signIn get code failed: " + ((ApiException) authHuaweiIdTask.getException()).getStatusCode());
                AppLog.Debug(AccountKitFragment.class.getSimpleName(), "signIn get code failed: " + ((ApiException) authHuaweiIdTask.getException()).getStatusCode());
            }
        }
    }

    private void validateToken(String code){
        OAuthImplementation.getInstance().validateToken("authorization_code", code, BuildConfig.APP_ID, BuildConfig.APP_SECRET, "http://developer.huawei.com/", new HttpResponseCallback<TokenModel>() {
            @Override
            public void on200(TokenModel responseBody, Response response) {
                if(responseBody != null && responseBody.getAccess_token() != null){
                    updateLogs("Validated success.");
                    updateLogs("Access Token: " + responseBody.getAccess_token());
                    updateLogs("Refresh Token: " + responseBody.getRefresh_token());
                    updateLogs("Expires In: " + responseBody.getExpires_in());
                    AppLog.Debug(AccountKitFragment.class.getSimpleName(), "Validated Success");
                }
            }

            @Override
            public void on401(Response response, RetrofitError error) {
                updateLogs("401 Unable to validate Token -> " + error.getMessage());
                AppLog.Error(AccountKitFragment.class.getSimpleName(), "401 Unable to validate Token -> " + error.getMessage());
            }

            @Override
            public void onFailure(RetrofitError error) {
                updateLogs("onFailure Unable to validate Token -> " + error.getMessage());
                AppLog.Error(AccountKitFragment.class.getSimpleName(), "onFailure Unable to validate Token -> " + error.getMessage());
            }
        });
    }
}