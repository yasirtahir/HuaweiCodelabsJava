package com.yasir.huaweicodelabs.Utilities;

import android.text.TextUtils;
import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public class CipherUtil {
    private static final String TAG = "CipherUtil";

    private static final String SIGN_ALGORITHMS = "SHA256WithRSA";

    private static final String PUBLIC_KEY = "MIIBojANBgkqhkiG9w0BAQEFAAOCAY8AMIIBigKCAYEAwxXG5RYyVsogPdzlg8g/Xa0SFvGm7PUlobmpt1vi/3f/enYou841AQPXDfI+2/73kXcpuizsgXqOM94KX+O+4/FmHEUg/zQ9uN2f4w/sU6wREv7vt6mDnWiGk4zS5iP62mN3Vm3s8kJacvZsgQKOikiNAuLIHYhf56t+75cE/ouVk5R1Gp8M5I0h+vrJ3MeiSDPYW5r04ub1kXzDmgrAnmirRtHGfdQKeGoakOaXBvfnea4TLm7Xt8fX9Z+rs2Pieau2nwhqx5cJN67rtdiH3a+7Rhg1+XVTEslWJSLqaFqKy+nWRm2bH7EVwJEu3mL0Yu93XBQTojfCTQPMgiqA8RFRS1SZLVH5nOluB6Y3fpH63Zl+IAaWyQLpsPzV+OSJ25qbYUhAV9EB/wsEUojXYWteEXhumhJXM4J1VXQt3zJZveGmfGfjsO87y9tNNoRi3srWJi1wsVJs4r7iyPqaluzYGVk75sIywULG0ZyExm/vtluVCdytAjeKXJafwJfPAgMBAAE=";

    /**
     * the method to check the signature for the data returned from the interface
     * @param content Unsigned data
     * @param sign the signature for content
     * @param publicKey the public of the application
     * @return boolean
     */
    public static boolean doCheck(String content, String sign, String publicKey) {
        if (TextUtils.isEmpty(publicKey)) {
            AppLog.Error(TAG, "publicKey is null");
            return false;
        }

        if (TextUtils.isEmpty(content) || TextUtils.isEmpty(sign)) {
            AppLog.Error(TAG, "data is error");
            return false;
        }

        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] encodedKey = Base64.decode(publicKey, Base64.DEFAULT);
            PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));

            java.security.Signature signature = java.security.Signature.getInstance(SIGN_ALGORITHMS);

            signature.initVerify(pubKey);
            signature.update(content.getBytes("utf-8"));

            boolean bverify = signature.verify(Base64.decode(sign, Base64.DEFAULT));
            return bverify;

        } catch (NoSuchAlgorithmException e) {
            AppLog.Error(TAG, "doCheck NoSuchAlgorithmException" + e);
        } catch (InvalidKeySpecException e) {
            AppLog.Error(TAG, "doCheck InvalidKeySpecException" + e);
        } catch (InvalidKeyException e) {
            AppLog.Error(TAG, "doCheck InvalidKeyException" + e);
        } catch (SignatureException e) {
            AppLog.Error(TAG, "doCheck SignatureException" + e);
        } catch (UnsupportedEncodingException e) {
            AppLog.Error(TAG, "doCheck UnsupportedEncodingException" + e);
        }
        return false;
    }

    /**
     * get the publicKey of the application
     * During the encoding process, avoid storing the public key in clear text.
     * @return publickey
     */
    public static String getPublicKey(){
        return PUBLIC_KEY;
    }
}
