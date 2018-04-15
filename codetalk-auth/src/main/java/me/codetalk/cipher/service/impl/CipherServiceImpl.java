package me.codetalk.cipher.service.impl;

import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Service;

import me.codetalk.Constants;
import me.codetalk.cipher.CipherConstants;
import me.codetalk.cipher.service.ICipherService;

/**
 * 
 * @author guobxu
 *
 */
@Service("cipherService")
public class CipherServiceImpl implements ICipherService {
	
	// Random byte generator - thread safe
	private SecureRandom rand = new SecureRandom();
	
	private ThreadLocal<Cipher> threadCipher = new ThreadLocal<Cipher>();

	public String cipher(String text, String key) throws Exception {
		Cipher cipher = getCipher();
		SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), CipherConstants.CIPHER_AES);
        IvParameterSpec ivSpec = new IvParameterSpec(CipherConstants.IV_SPEC_KEY.getBytes());
        
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        byte[] cipherBytes = cipher.doFinal(text.getBytes(CipherConstants.ENCODING_UTF8)),
        		cipherBytesEnc = Base64.getEncoder().encode(cipherBytes);
        String cipherBase64 = new String(cipherBytesEnc, Constants.ENCODING_UTF8);
		
		return cipherBase64;
	}
	
	@Override
	public String cipher(byte[] bytes, String key) throws Exception {
		Cipher cipher = getCipher();
		SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), CipherConstants.CIPHER_AES);
        IvParameterSpec ivSpec = new IvParameterSpec(CipherConstants.IV_SPEC_KEY.getBytes());
        
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        byte[] cipherBytes = cipher.doFinal(bytes),
        		cipherBytesEnc = Base64.getEncoder().encode(cipherBytes);
        String cipherBase64 = new String(cipherBytesEnc, Constants.ENCODING_UTF8);
		
		return cipherBase64;
	}
	
	public String decipher(String ciphered, String key) throws Exception {
		Cipher cipher = getCipher();
		SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), CipherConstants.CIPHER_AES);
        IvParameterSpec ivSpec = new IvParameterSpec(CipherConstants.IV_SPEC_KEY.getBytes());
        
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        byte[] bytes = ciphered.getBytes(Constants.ENCODING_UTF8), 
        		bytesDec = cipher.doFinal(Base64.getDecoder().decode(bytes));
        String text = new String(bytesDec, CipherConstants.ENCODING_UTF8);
        
		return text;
	}

	private Cipher getCipher() throws Exception {
		Cipher cipher = threadCipher.get();
		if(cipher == null) {
			cipher = Cipher.getInstance(CipherConstants.CIPHER_AES_CFB_NOPADDING);
			threadCipher.set(cipher);
		}
		
		return cipher;
	}
	
}
