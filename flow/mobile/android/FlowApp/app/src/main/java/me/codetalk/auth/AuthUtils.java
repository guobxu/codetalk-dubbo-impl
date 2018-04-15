package me.codetalk.auth;

import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import me.codetalk.Constants;
import me.codetalk.flowapp.AppConstants;
import me.codetalk.util.StringUtils;

import static android.util.Base64.NO_WRAP;

/**
 * Created by guobxu on 14/1/2018.
 */

public final class AuthUtils {

    private static ThreadLocal<Cipher> threadCipher = new ThreadLocal<>();

    public static String cipherPwd(String pwd, String key) throws Exception {
        String salted = AuthConstants.SALT_LEFT + pwd + AuthConstants.SALT_RIGHT;

        String hexMd5 = StringUtils.hexMd5(salted);

        return cipher(hexMd5, key);
    }

    /**
     * Base64(AES("32Byte随机密钥+登录名", KEY("变换后的密码")))
     *
     * @param userLogin
     * @param pwd
     * @return
     * @throws Exception
     */
    public static String cipherLoginAuth(String userLogin, String pwd, String clientKey) throws Exception {
        String salted = AuthConstants.SALT_LEFT + pwd + AuthConstants.SALT_RIGHT, hexMd5 = StringUtils.hexMd5(salted);

        return cipher(clientKey + userLogin, hexMd5);
    }

    public static final String cipherAuthStr(String userLogin, long timeMillis, String key) throws Exception {
        String text = userLogin + timeMillis;

        return cipher(text, key);
    }


    /************************************ CIPHER ************************************/

    public static String cipher(String text, String key) throws Exception {
        Cipher cipher = getCipher();
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), AuthConstants.CIPHER_AES);
        IvParameterSpec ivSpec = new IvParameterSpec(AuthConstants.IV_SPEC_KEY.getBytes());

        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        byte[] cipherBytes = cipher.doFinal(text.getBytes(Constants.ENCODING_UTF8)),
                cipherBytesEnc = Base64.encode(cipherBytes, NO_WRAP);;
        String cipherBase64 = new String(cipherBytesEnc, AppConstants.ENCODING_UTF8);

        return cipherBase64;
    }

    public static String cipher(byte[] bytes, String key) throws Exception {
        Cipher cipher = getCipher();
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), AuthConstants.CIPHER_AES);
        IvParameterSpec ivSpec = new IvParameterSpec(AuthConstants.IV_SPEC_KEY.getBytes());

        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        byte[] cipherBytes = cipher.doFinal(bytes), cipherBytesEnc = Base64.encode(cipherBytes, NO_WRAP);
        String cipherBase64 = new String(cipherBytesEnc, AppConstants.ENCODING_UTF8);

        return cipherBase64;
    }

    public static String decipher(String ciphered, String key) throws Exception {
        Cipher cipher = getCipher();
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), AuthConstants.CIPHER_AES);
        IvParameterSpec ivSpec = new IvParameterSpec(AuthConstants.IV_SPEC_KEY.getBytes());

        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        byte[] bytes = cipher.doFinal(Base64.decode(ciphered.getBytes(AppConstants.ENCODING_UTF8), NO_WRAP));
        String text = new String(bytes, Constants.ENCODING_UTF8);

        return text;
    }

    private static Cipher getCipher() throws Exception {
        Cipher cipher = threadCipher.get();
        if(cipher == null) {
            cipher = Cipher.getInstance(AuthConstants.CIPHER_AES_CFB_NOPADDING);
            threadCipher.set(cipher);
        }

        return cipher;
    }

}
