package me.codetalk.util;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import me.codetalk.flowapp.AppConstants;

import static android.util.Base64.NO_WRAP;

/**
 * Created by guobxu on 17/7/2017.
 */
public final class StringUtils {

	private static SecureRandom rand = new SecureRandom();
	
	private static ThreadLocal<MessageDigest> MD5_DIGEST = new ThreadLocal<>();

	protected static final char[] HEX_ARRAY= {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};

	// Punctuation(s)
	private static final Set<String> PUNC_CHARS = new HashSet<>(Arrays.asList(new String[] {
			// 英文标点
			",", ".", "/", "`", "~", "!", "@", "#", "$", "%", "^", "&", "*", "(", ")", "-", "_", "+", "=", ":", ";", "'", "\"", "<", ">", "?",
			// 中文标点
			"，", "。", "、", "·", "！", "￥", "…", "（", "）", "——", "：", "；", "’", "”", "‘", "“", "《", "》", "？",
			// 全角
			"．", "／", "｀", "～", "＠", "＃", "＄", "％", "＾", "＆", "＊", "－", "＿", "＋", "＝", "＇", "＂", "＜", "＞",
	}));

    // 如果null 或 为空, 则返回true
    public static boolean isNull(String str) {
        return str == null || str.length() == 0;
    }

    public static boolean isNotNull(String str) {
        return str != null && str.length() > 0;
    }

    public static String toString(Object obj, boolean nullAsEmpty) {
        return obj == null ?
                (nullAsEmpty ? "" : null) : obj.toString();
    }

	public static boolean isBlank(String str) {
		int strLen;
		if (str == null || (strLen = str.length()) == 0) {
			return true;
		}
		for (int i = 0; i < strLen; i++) {
			if ((Character.isWhitespace(str.charAt(i)) == false)) {
				return false;
			}
		}
		return true;
	}

	public static boolean isNotBlank(String str) {
    	return !isBlank(str);
	}

    public static String replaceNoRegex(String str, String target, String replacement) {
        int i = str.indexOf(target);
        if(i == -1) return str;

        return str.substring(0, i) + replacement + str.substring(i + target.length());
    }
    
    private static MessageDigest getMd5Digest() throws NoSuchAlgorithmException {
    	MessageDigest digest = MD5_DIGEST.get();
		if (digest == null) {
			digest = MessageDigest.getInstance("MD5");
			MD5_DIGEST.set(digest);
		}

		return digest;
    }
    
    public static String hexMd5(String input) throws Exception {
		MessageDigest md = getMd5Digest();
		
		return bytesToHex(md.digest(input.getBytes(AppConstants.ENCODING_UTF8)));
    }
    
    public static String uuid() {
    	return UUID.randomUUID().toString();
    }
    
    public static String randomKey32() {
		byte[] bytes = new byte[16];
		rand.nextBytes(bytes);
		
		return bytesToHex(bytes);
	}
    
    private static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = HEX_ARRAY[v >>> 4];
			hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
		}
		
		return new String(hexChars);
	}
    
    public static String randomNum(int len) {
    	int min = (int)Math.pow(10, len - 1), max = min * 10 -1;
    	
    	Random r = new Random();
		int rand = r.nextInt((max - min) + 1) + min;
		
		return String.valueOf(rand);
    }

    public static String base64(String s) {
    	try {
			return Base64.encodeToString(s.getBytes(AppConstants.ENCODING_UTF8), NO_WRAP);
		} catch(UnsupportedEncodingException ex) {
    		ex.printStackTrace();

    		return null;
		}

	}

	// 是否为标点
	public static boolean isPuncChar(char c) {
		return PUNC_CHARS.contains(String.valueOf(c));
	}

    /**
     * 随机数字 长度6
     * 
     * @return
     */
    public static String randomNum6() {
		return randomNum(6);
	}
    
    /**
     * 随机数字 长度8
     * 
     * @return
     */
    public static String randomNum8() {
    	return randomNum(8);
	}

	public static String ltrim(String s) {
		for(int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if(!Character.isWhitespace(c)) return s.substring(i);
		}

		return "";
	}

	public static String rtrim(String s) {
		for(int i = s.length() - 1; i >= 0; i--) {
			char c = s.charAt(i);
			if(!Character.isWhitespace(c)) return s.substring(0, i+1);
		}

		return "";
	}

}