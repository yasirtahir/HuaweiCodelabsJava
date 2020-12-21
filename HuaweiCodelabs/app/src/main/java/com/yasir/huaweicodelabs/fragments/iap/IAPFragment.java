package com.yasir.huaweicodelabs.fragments.iap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.iap.Iap;
import com.huawei.hms.iap.IapApiException;
import com.huawei.hms.iap.IapClient;
import com.huawei.hms.iap.entity.ConsumeOwnedPurchaseReq;
import com.huawei.hms.iap.entity.ConsumeOwnedPurchaseResult;
import com.huawei.hms.iap.entity.InAppPurchaseData;
import com.huawei.hms.iap.entity.OrderStatusCode;
import com.huawei.hms.iap.entity.ProductInfoReq;
import com.huawei.hms.iap.entity.ProductInfoResult;
import com.huawei.hms.iap.entity.PurchaseIntentReq;
import com.huawei.hms.iap.entity.PurchaseIntentResult;
import com.huawei.hms.iap.entity.PurchaseResultInfo;
import com.huawei.hms.support.api.client.Status;
import com.yasir.huaweicodelabs.R;
import com.yasir.huaweicodelabs.Utilities.AppLog;
import com.yasir.huaweicodelabs.Utilities.CipherUtil;
import com.yasir.huaweicodelabs.fragments.BaseFragment;

import org.json.JSONException;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.yasir.huaweicodelabs.Utilities.CipherUtil.getPublicKey;

public class IAPFragment extends BaseFragment implements View.OnClickListener {

    private static final int REQ_CODE_BUY = 1001;
    @BindView(R.id.btnProductOne)
    Button btnProductOne;
    @BindView(R.id.txtProductOne)
    TextView txtProductOne;
    @BindView(R.id.btnProductTwo)
    Button btnProductTwo;
    @BindView(R.id.txtProductTwo)
    TextView txtProductTwo;
    private View rootView;

    public static IAPFragment newInstance() {
        return new IAPFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getMainActivity().setHeading("In-app Purchase Kit");

        getProductInformation();

        btnProductOne.setOnClickListener(this);
        btnProductTwo.setOnClickListener(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_iap, container, false);
        } else {
            container.removeView(rootView);
        }

        ButterKnife.bind(this, rootView);

        return rootView;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnProductOne:
                buyProduct("Product1");
                break;
            case R.id.btnProductTwo:
                buyProduct("Product2");
                break;
        }
    }

    private void getProductInformation() {
        // obtain in-app product details configured in AppGallery Connect, and then show the products
        IapClient iapClient = Iap.getIapClient(getMainActivity());
        Task<ProductInfoResult> task = iapClient.obtainProductInfo(createProductInfoReq());
        task.addOnSuccessListener(result -> {
            if (result != null && !result.getProductInfoList().isEmpty() && result.getProductInfoList().size() > 1) {
                txtProductOne.setText(result.getProductInfoList().get(0).getProductName());
                txtProductTwo.setText(result.getProductInfoList().get(1).getProductName());
            }
        }).addOnFailureListener(e -> {
            AppLog.Error(IAPFragment.class.getSimpleName(), e.getMessage());
            Toast.makeText(getMainActivity(), "error", Toast.LENGTH_SHORT).show();
        });
    }

    private ProductInfoReq createProductInfoReq() {
        ProductInfoReq req = new ProductInfoReq();
        req.setPriceType(IapClient.PriceType.IN_APP_CONSUMABLE);
        ArrayList<String> productIds = new ArrayList<>();
        productIds.add("Product1");
        productIds.add("Product2");
        req.setProductIds(productIds);
        return req;
    }

    private void buyProduct(String productId) {
        gotoPay(getMainActivity(), productId);
    }

    private void gotoPay(final Activity activity, String productId) {
        AppLog.Debug(IAPFragment.class.getSimpleName(), "call createPurchaseIntent");
        IapClient mClient = Iap.getIapClient(activity);
        Task<PurchaseIntentResult> task = mClient.createPurchaseIntent(createPurchaseIntentReq(IapClient.PriceType.IN_APP_CONSUMABLE, productId));
        task.addOnSuccessListener(new OnSuccessListener<PurchaseIntentResult>() {
            @Override
            public void onSuccess(PurchaseIntentResult result) {
                AppLog.Debug(IAPFragment.class.getSimpleName(), "createPurchaseIntent, onSuccess");
                if (result == null) {
                    AppLog.Error(IAPFragment.class.getSimpleName(), "result is null");
                    return;
                }
                Status status = result.getStatus();
                if (status == null) {
                    AppLog.Error(IAPFragment.class.getSimpleName(), "status is null");
                    return;
                }
                // you should pull up the page to complete the payment process.
                if (status.hasResolution()) {
                    try {
                        status.startResolutionForResult(activity, REQ_CODE_BUY);
                    } catch (IntentSender.SendIntentException exp) {
                        AppLog.Error(IAPFragment.class.getSimpleName(), exp.getMessage());
                    }
                } else {
                    AppLog.Error(IAPFragment.class.getSimpleName(), "intent is null");
                }
            }
        }).addOnFailureListener(e -> {
            AppLog.Error(IAPFragment.class.getSimpleName(), e.getMessage());
            Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
            if (e instanceof IapApiException) {
                IapApiException apiException = (IapApiException) e;
                int returnCode = apiException.getStatusCode();
                AppLog.Error(IAPFragment.class.getSimpleName(), "createPurchaseIntent, returnCode: " + returnCode);
                // handle error scenarios
            } else {
                // Other external errors
            }
        });
    }

    private PurchaseIntentReq createPurchaseIntentReq(int type, String productId) {
        PurchaseIntentReq req = new PurchaseIntentReq();
        req.setProductId(productId);
        req.setPriceType(type);
        req.setDeveloperPayload("test");
        return req;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_CODE_BUY) {
            if (data == null) {
                Toast.makeText(getMainActivity(), "error", Toast.LENGTH_SHORT).show();
                return;
            }
            PurchaseResultInfo purchaseResultInfo = Iap.getIapClient(getMainActivity()).parsePurchaseResultInfoFromIntent(data);
            switch (purchaseResultInfo.getReturnCode()) {
                case OrderStatusCode.ORDER_STATE_SUCCESS:
                    // verify signature of payment results.
                    boolean success = CipherUtil.doCheck(purchaseResultInfo.getInAppPurchaseData(), purchaseResultInfo.getInAppDataSignature(), getPublicKey());
                    if (success) {
                        // Call the consumeOwnedPurchase interface to consume it after successfully delivering the product to your user.
                        consumeOwnedPurchase(getMainActivity(), purchaseResultInfo.getInAppPurchaseData());
                    } else {
                        Toast.makeText(getMainActivity(), "Pay successful,sign failed", Toast.LENGTH_SHORT).show();
                    }
                    return;
                case OrderStatusCode.ORDER_STATE_CANCEL:
                    // The User cancels payment.
                    Toast.makeText(getMainActivity(), "user cancel", Toast.LENGTH_SHORT).show();
                    return;
                case OrderStatusCode.ORDER_PRODUCT_OWNED:
                    // The user has already owned the product.
                    Toast.makeText(getMainActivity(), "you have owned the product", Toast.LENGTH_SHORT).show();
                    // you can check if the user has purchased the product and decide whether to provide goods
                    // if the purchase is a consumable product, consuming the purchase and deliver product
                    return;

                default:
                    Toast.makeText(getMainActivity(), "Pay failed", Toast.LENGTH_SHORT).show();
                    break;
            }
            return;
        }

    }

    private void consumeOwnedPurchase(final Context context, String inAppPurchaseData) {
        AppLog.Error(IAPFragment.class.getSimpleName(), "call consumeOwnedPurchase");
        IapClient mClient = Iap.getIapClient(context);
        Task<ConsumeOwnedPurchaseResult> task = mClient.consumeOwnedPurchase(createConsumeOwnedPurchaseReq(inAppPurchaseData));
        task.addOnSuccessListener(new OnSuccessListener<ConsumeOwnedPurchaseResult>() {
            @Override
            public void onSuccess(ConsumeOwnedPurchaseResult result) {
                // Consume success
                AppLog.Error(IAPFragment.class.getSimpleName(), "consumeOwnedPurchase success");
                Toast.makeText(context, "Pay success, and the product has been delivered", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            AppLog.Error(IAPFragment.class.getSimpleName(), e.getMessage());
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            if (e instanceof IapApiException) {
                IapApiException apiException = (IapApiException) e;
                Status status = apiException.getStatus();
                int returnCode = apiException.getStatusCode();
                AppLog.Error(IAPFragment.class.getSimpleName(), "consumeOwnedPurchase fail,returnCode: " + returnCode);
            } else {
                // Other external errors
            }

        });
    }

    /**
     * Create a ConsumeOwnedPurchaseReq instance.
     *
     * @param purchaseData JSON string that contains purchase order details.
     * @return ConsumeOwnedPurchaseReq
     */
    private ConsumeOwnedPurchaseReq createConsumeOwnedPurchaseReq(String purchaseData) {

        ConsumeOwnedPurchaseReq req = new ConsumeOwnedPurchaseReq();
        // Parse purchaseToken from InAppPurchaseData in JSON format.
        try {
            InAppPurchaseData inAppPurchaseData = new InAppPurchaseData(purchaseData);
            req.setPurchaseToken(inAppPurchaseData.getPurchaseToken());
        } catch (JSONException e) {
            AppLog.Error(IAPFragment.class.getSimpleName(), "createConsumeOwnedPurchaseReq JSONExeption");
        }

        return req;
    }
}

