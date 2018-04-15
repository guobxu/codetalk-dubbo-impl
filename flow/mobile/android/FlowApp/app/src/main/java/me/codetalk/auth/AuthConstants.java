package me.codetalk.auth;

/**
 * Created by guobxu on 14/1/2018.
 */

public final class AuthConstants {


    public static final Long AUTH_STR_EXPIRE = 10 * 60 * 1000L; // 通信认证串过期时间, 10分钟

    public static final Integer SIGNUP_VCODE_LEN = 5;

    public static final Long SSESS_TIMEOUT = 5 * 60 * 1000L; // Signup Session timeout = 5min

    // Cipher
    public static final String CIPHER_AES = "AES";
    public static final String CIPHER_AES_CFB_NOPADDING = "AES/CFB/NoPadding";

    public static final String IV_SPEC_KEY = "O09&YA)We3qMI5EX";

    // key len = 32
    public static final int CIPHER_KEY_LEN = 32;

    // cipher separator
    public static final String CIPHER_STR_SEP = "/";

    // salt
    public static final String SALT_LEFT = "0Ui&1m49p9iA5mB2";
    public static final String SALT_RIGHT = "Ix09tY!90ec9U60o";

}
