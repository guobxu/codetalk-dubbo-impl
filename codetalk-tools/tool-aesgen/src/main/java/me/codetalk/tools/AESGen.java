package me.codetalk.tools;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import me.codetalk.util.StringUtils;

public class AESGen {

private static ThreadLocal<Cipher> threadCipher = new ThreadLocal<Cipher>();
	
	private static Cipher getCipher() throws Exception {
		Cipher cipher = threadCipher.get();
		if(cipher == null) {
			cipher = Cipher.getInstance("AES/CFB/NoPadding");
			threadCipher.set(cipher);
		}
		
		return cipher;
	}
	
	public static String cipher(String text, String key) throws Exception {
		Cipher cipher = getCipher();
		SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
        IvParameterSpec ivSpec = new IvParameterSpec("O09&YA)We3qMI5EX".getBytes());
        
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        byte[] cipherBytes = cipher.doFinal(text.getBytes("UTF-8"));
        String cipherBase64 = Base64.getEncoder().encodeToString(cipherBytes);
		
		return cipherBase64;
	}
	
	public static String decipher(String ciphered, String key) throws Exception {
		Cipher cipher = getCipher();
		SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
        IvParameterSpec ivSpec = new IvParameterSpec("O09&YA)We3qMI5EX".getBytes());
        
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        byte[] bytes = cipher.doFinal(Base64.getDecoder().decode(ciphered));
        String text = new String(bytes, "UTF-8");
        
		return text;
	}
	
	protected static final char[] HEX_ARRAY= {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
	
	private static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = HEX_ARRAY[v >>> 4];
			hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
		}
		
		return new String(hexChars);
	}
	
	public static String encodeFileUri(String fdfsUrl) {
		String encodeUrl = null;

		try {
			String base64Uri = new String(Base64.getEncoder().encode(fdfsUrl.getBytes("UTF-8")),
					"UTF-8");
			encodeUrl = URLEncoder.encode(base64Uri, "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			return null;
		}

		return encodeUrl;
	}
	
	public static void main(String[] args) throws Exception {
//		String text = "44E0021BA03BFD68D83FCD2CE7219DFE";
//		System.out.println(cipher(text, "72F3897670FE064B0C9A7BAE669F0772"));
		
//		MessageDigest md = MessageDigest.getInstance("MD5");
//		String passwd = "123456";
//		byte[] md5bytes = md.digest(passwd.getBytes("UTF-8"));
//		String tranPwd = bytesToHex(md5bytes);
//		System.out.println("Tansformed password: " + tranPwd);
//		
//		String encPwd = cipher(tranPwd, "5962D3F90CE93433F7E849C20B5C0342");
//		System.out.println("Password Encrypted: " + encPwd);
//		
//		// user1 login
//		String key32 = StringUtils.randomKey32(); // D27C3DF1E2CCE74DEB3C48675EB3A350
//		System.out.println("Random key32: " + key32);
//		
//		String loginAuthStr = cipher(key32 + "user1", tranPwd);
//		System.out.println("Login AuthStr: " + loginAuthStr);
//		
//		// auth ret
//		String authRetStr = "Stfpj6mcYJliiie1Np1KWQ3JtfzRoP1vMZQ5yM9YR27tEHuf2WxStlHd54+g/fUGRLVmNvwsV1vaT6d8G5zbURHBWH5VcugqxA==";
//		String clearRet = decipher(authRetStr, "D27C3DF1E2CCE74DEB3C48675EB3A350");
//		System.out.println("Clear Return String: " + clearRet);
//		
//		int tmp = clearRet.length() - 36;
//		String transportKey = clearRet.substring(0, 32), 
//				userLogin = clearRet.substring(32, tmp),
//				token = clearRet.substring(tmp);
//		System.out.println("Transport Key: " + transportKey);
//		System.out.println("User Login: " + userLogin);
//		System.out.println("Access Token: " + token);
//		
//		// reset pwd
//		String newPwd = "123abc",
//				newTranPwd = bytesToHex(md.digest(newPwd.getBytes("UTF-8"))),
//				newEncPwd = cipher(newTranPwd + "/user1", "031B7DCF2136CA9C7CFC8797D098D621");
//		System.out.println("New Tran Password: " + newTranPwd);
//		System.out.println("New Password Encrypted: " + newEncPwd);
		
//		System.out.println(System.currentTimeMillis());
		
//		System.out.println(StringUtils.uuid());
		
		System.out.println(StringUtils.base64Enc("7:f2ee2ce7-990a-4eae-a177-ee9312a540b0:sfdafdsl"));
		
	}
	
}
